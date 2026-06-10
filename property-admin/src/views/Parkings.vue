<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>停车位管理</span>
          <el-button type="primary" @click="handleAdd">新增车位</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe>
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
import { ref, reactive, onMounted } from 'vue'
import { getParkings, addParking, updateParking, deleteParking, assignParking, revokeParking } from '../api/parking'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const assignVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const ownerLoading = ref(false)
const ownerOptions = ref([])

const query = reactive({ page: 1, size: 10, keyword: '' })
const form = reactive({ id: null, number: '', location: '' })
const assignForm = reactive({ id: null, ownerId: null, startDate: '', endDate: '' })
const rules = {
  number: [{ required: true, message: '请输入车位号', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getParkings(query)
  tableData.value = res.data.list
  total.value = res.data.total
}

const searchOwners = async (keyword) => {
  if (!keyword) {
    ownerOptions.value = []
    return
  }
  ownerLoading.value = true
  try {
    const res = await request.get('/api/owners/all')
    const list = res.data || []
    // 前端过滤：姓名或账号匹配
    ownerOptions.value = list.filter(o =>
      o.name.includes(keyword) || o.username.includes(keyword)
    )
  } catch (e) { /* ignore */ }
  ownerLoading.value = false
}

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
}

onMounted(loadData)
</script>
