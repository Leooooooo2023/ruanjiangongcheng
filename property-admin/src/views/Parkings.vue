<template>
  <div>
    <!-- 模式切换 -->
    <el-card style="margin-bottom: 15px">
      <div style="display: flex; justify-content: space-between; align-items: center">
        <el-radio-group v-model="viewMode" size="default">
          <el-radio-button value="map">🅿️ 地图模式</el-radio-button>
          <el-radio-button value="table">📋 表格模式</el-radio-button>
          <el-radio-button value="applications">📝 申请审核
            <el-badge :value="pendingCount" :hidden="pendingCount === 0" style="margin-left: 4px" />
          </el-radio-button>
        </el-radio-group>
        <el-button v-if="viewMode !== 'applications'" type="primary" @click="handleAdd">新增车位</el-button>
      </div>
    </el-card>

    <!-- 地图视图 -->
    <div v-if="viewMode === 'map'" v-loading="loading">
      <ParkingMap
        :spots="allSpots"
        :applications="applicationList"
        @spot-click="handleSpotClick"
      />
    </div>

    <!-- 表格视图 -->
    <el-card v-if="viewMode === 'table'">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="number" label="车位号" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
              {{ scope.row.status === 0 ? '空闲' : '占用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerUsername" label="业主账号" width="140">
          <template #default="scope">
            <span :style="{ color: scope.row.ownerId ? '' : '#ccc' }">{{ scope.row.ownerUsername || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="业主姓名" width="120">
          <template #default="scope">
            <span :style="{ color: scope.row.ownerId ? '' : '#ccc' }">{{ scope.row.ownerName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="租期开始" width="120" />
        <el-table-column prop="endDate" label="租期结束" width="120" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="success" v-if="scope.row.status === 0" @click="handleAssign(scope.row)">分配</el-button>
            <el-button size="small" type="warning" v-if="scope.row.status === 1" @click="handleRevoke(scope.row)">收回</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        style="margin-top: 15px; justify-content: flex-end"
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        @current-change="loadData"
        layout="total, prev, pager, next"
      />
    </el-card>

    <!-- 申请审核视图 -->
    <el-card v-if="viewMode === 'applications'" v-loading="appLoading">
      <template #header>
        <span>车位申请审核</span>
      </template>
      <el-table :data="applicationList" border stripe>
        <el-table-column prop="id" label="申请ID" width="80" />
        <el-table-column prop="parkingNumber" label="车位号" width="100" />
        <el-table-column prop="parkingLocation" label="车位位置" width="120" />
        <el-table-column prop="ownerName" label="申请人" width="100" />
        <el-table-column prop="ownerUsername" label="申请人账号" width="120" />
        <el-table-column prop="createTime" label="申请时间" width="170" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <template v-if="scope.row.status === 0">
              <el-button size="small" type="success" @click="handleApprove(scope.row)">通过</el-button>
              <el-button size="small" type="danger" @click="handleReject(scope.row)">拒绝</el-button>
            </template>
            <span v-else style="color: #999">—</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 车位详情弹窗（地图模式点击） -->
    <el-dialog v-model="spotDialogVisible" title="车位详情" width="480px">
      <template v-if="selectedSpot">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="车位号">{{ selectedSpot.number }}</el-descriptions-item>
          <el-descriptions-item label="位置">{{ selectedSpot.location }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="selectedSpot.status === 0 && !selectedApplication" type="success">空闲</el-tag>
            <el-tag v-else-if="selectedSpot.status === 1" type="danger">已占用</el-tag>
            <el-tag v-else-if="selectedApplication && selectedApplication.status === 0" type="warning">申请中</el-tag>
            <el-tag v-else type="success">空闲</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="业主账号">
            {{ selectedSpot.ownerUsername || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="业主姓名">
            {{ selectedSpot.ownerName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="租期">
            <template v-if="selectedSpot.startDate">
              {{ selectedSpot.startDate }} ~ {{ selectedSpot.endDate }}
            </template>
            <template v-else>-</template>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 申请中：显示申请信息 -->
        <template v-if="selectedApplication && selectedApplication.status === 0">
          <el-divider />
          <el-alert type="warning" :closable="false" show-icon>
            <template #title>
              {{ selectedApplication.ownerName }}（{{ selectedApplication.ownerUsername }}）申请该车位
            </template>
            <p style="margin: 8px 0 0; font-size: 12px; color: #999">
              申请时间：{{ selectedApplication.createTime }}
            </p>
          </el-alert>
        </template>

        <!-- 已占用：显示当前业主信息 -->
        <template v-if="selectedSpot.status === 1 && selectedSpot.ownerId">
          <el-divider />
          <el-alert type="info" :closable="false" show-icon>
            <template #title>
              当前归属：{{ selectedSpot.ownerName }}（{{ selectedSpot.ownerUsername }}）
            </template>
          </el-alert>
        </template>
      </template>

      <template #footer>
        <!-- 空闲：可分配 -->
        <template v-if="selectedSpot && selectedSpot.status === 0 && !selectedApplication">
          <el-button type="primary" @click="spotDialogVisible = false; handleAssign(selectedSpot)">分配车位</el-button>
        </template>
        <!-- 申请中：审核按钮 -->
        <template v-if="selectedApplication && selectedApplication.status === 0">
          <el-button type="success" @click="handleApprove(selectedApplication); spotDialogVisible = false">通过申请</el-button>
          <el-button type="danger" @click="handleReject(selectedApplication); spotDialogVisible = false">拒绝申请</el-button>
        </template>
        <!-- 已占用：收回 -->
        <template v-if="selectedSpot && selectedSpot.status === 1">
          <el-button type="warning" @click="spotDialogVisible = false; handleRevoke(selectedSpot)">收回车位</el-button>
        </template>
        <el-button @click="spotDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑车位' : '新增车位'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="车位号" prop="number">
          <el-input v-model="form.number" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配车位 -->
    <el-dialog v-model="assignVisible" title="分配车位" width="500px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="选择业主" prop="ownerId">
          <el-select
            v-model="assignForm.ownerId"
            filterable
            remote
            :remote-method="searchOwners"
            :loading="ownerLoading"
            placeholder="请输入业主姓名或账号搜索"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="o in ownerOptions"
              :key="o.id"
              :label="`${o.name} (${o.username})`"
              :value="o.id"
            >
              <span>{{ o.name }}</span>
              <span style="color: #999; margin-left: 10px; font-size: 12px">{{ o.username }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="assignForm.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="assignForm.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import {
  getParkings, addParking, updateParking, deleteParking,
  assignParking, revokeParking,
  getParkingApplications, approveApplication, rejectApplication
} from '../api/parking'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import ParkingMap from '../components/ParkingMap.vue'

const viewMode = ref('map')
const loading = ref(false)
const appLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const allSpots = ref([])
const applicationList = ref([])
const dialogVisible = ref(false)
const spotDialogVisible = ref(false)
const assignVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const ownerLoading = ref(false)
const ownerOptions = ref([])
const selectedSpot = ref(null)
const selectedApplication = ref(null)

const query = reactive({ page: 1, size: 10, keyword: '' })
const form = reactive({ id: null, number: '', location: '' })
const assignForm = reactive({ id: null, ownerId: null, startDate: '', endDate: '' })
const rules = {
  number: [{ required: true, message: '请输入车位号', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }]
}

const pendingCount = computed(() => {
  return applicationList.value.filter(a => a.status === 0).length
})

// ====== 数据加载 ======

const loadData = async () => {
  loading.value = true
  try {
    const res = await getParkings(query)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (e) { /* ignore */ }
  loading.value = false
}

const loadAllSpots = async () => {
  try {
    const res = await getParkings({ page: 1, size: 100 })
    allSpots.value = res.data.list || []
  } catch (e) { /* ignore */ }
}

const loadApplications = async () => {
  appLoading.value = true
  try {
    const res = await getParkingApplications()
    applicationList.value = res.data || []
  } catch (e) { /* ignore */ }
  appLoading.value = false
}

// ====== 地图交互 ======

const handleSpotClick = ({ spot, application }) => {
  selectedSpot.value = spot
  selectedApplication.value = application
  spotDialogVisible.value = true
}

// ====== 申请审核 ======

const handleApprove = async (row) => {
  await ElMessageBox.confirm(`确定通过 ${row.ownerName} 对车位 ${row.parkingNumber} 的申请？`, '审核通过', { type: 'warning' })
  await approveApplication(row.id)
  ElMessage.success('审核通过，车位已分配')
  loadAllSpots()
  loadApplications()
  loadData()
}

const handleReject = async (row) => {
  await ElMessageBox.confirm(`确定拒绝 ${row.ownerName} 对车位 ${row.parkingNumber} 的申请？`, '审核拒绝', { type: 'warning' })
  await rejectApplication(row.id)
  ElMessage.success('已拒绝')
  loadAllSpots()
  loadApplications()
}

// ====== 搜索业主 ======

const searchOwners = async (keyword) => {
  if (!keyword) {
    ownerOptions.value = []
    return
  }
  ownerLoading.value = true
  try {
    const res = await request.get('/api/owners/all')
    const list = res.data || []
    ownerOptions.value = list.filter(o =>
      o.name.includes(keyword) || o.username.includes(keyword)
    )
  } catch (e) { /* ignore */ }
  ownerLoading.value = false
}

// ====== 表格操作 ======

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, number: '', location: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该车位？', '提示', { type: 'warning' })
  await deleteParking(row.id)
  ElMessage.success('删除成功')
  loadData()
  loadAllSpots()
}

const handleAssign = (row) => {
  assignForm.id = row.id
  assignForm.ownerId = null
  assignForm.startDate = ''
  assignForm.endDate = ''
  ownerOptions.value = []
  assignVisible.value = true
}

const handleRevoke = async (row) => {
  await ElMessageBox.confirm('确定收回该车位？', '提示', { type: 'warning' })
  await revokeParking(row.id)
  ElMessage.success('收回成功')
  loadData()
  loadAllSpots()
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateParking(form.id, form)
    ElMessage.success('修改成功')
  } else {
    await addParking(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
  loadAllSpots()
}

const submitAssign = async () => {
  if (!assignForm.ownerId) {
    ElMessage.warning('请选择业主')
    return
  }
  await assignParking(assignForm.id, assignForm)
  ElMessage.success('分配成功')
  assignVisible.value = false
  loadData()
  loadAllSpots()
}

// ====== 初始化 ======

onMounted(() => {
  loadData()
  loadAllSpots()
  loadApplications()
})
</script>
