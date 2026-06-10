<template>
  <div>
    <el-card>
      <template #header>
        <span>报修管理</span>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ownerName" label="业主" width="120">
          <template #default="scope">
            <span :style="{ color: scope.row.ownerId ? '' : '#ccc' }">{{ scope.row.ownerName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="employeeName" label="维修员工" width="120">
          <template #default="scope">
            <span :style="{ color: scope.row.employeeId ? '' : '#ccc' }">{{ scope.row.employeeName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评价" width="80">
          <template #default="scope">
            <span v-if="scope.row.rating">{{ scope.row.rating }}星</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button size="small" type="primary" v-if="scope.row.status === 0" @click="handleAssign(scope.row)">分配</el-button>
            <el-button size="small" type="success" v-if="scope.row.status === 1" @click="handleComplete(scope.row)">完工</el-button>
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

    <!-- 查看 -->
    <el-dialog v-model="viewVisible" title="报修详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ viewData.content }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(viewData.status) }}</el-descriptions-item>
        <el-descriptions-item label="图片" v-if="viewData.image">
          <el-image :src="viewData.image" style="max-width: 300px" fit="contain" />
        </el-descriptions-item>
        <el-descriptions-item label="评价" v-if="viewData.rating">{{ viewData.rating }}星</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 分配 -->
    <el-dialog v-model="assignVisible" title="分配维修员工" width="500px">
      <el-form label-width="80px">
        <el-form-item label="选择员工" prop="employeeId">
          <el-select
            v-model="assignEmployeeId"
            filterable
            remote
            :remote-method="searchEmployees"
            :loading="employeeLoading"
            placeholder="请输入员工姓名或职位搜索"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="e in employeeOptions"
              :key="e.id"
              :label="`${e.name} (ID:${e.id})`"
              :value="e.id"
            >
              <span style="font-weight: 500">{{ e.name }}</span>
              <span style="color: #409EFF; margin-left: 8px; font-size: 12px">ID:{{ e.id }}</span>
              <span style="color: #999; margin-left: 8px; font-size: 12px">{{ e.position || '' }} / {{ e.department || '' }}</span>
            </el-option>
          </el-select>
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
import { getRepairs, assignRepair, updateRepairStatus } from '../api/repair'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const total = ref(0)
const viewVisible = ref(false)
const assignVisible = ref(false)
const viewData = ref({})
const assignRepairId = ref(null)
const assignEmployeeId = ref(null)
const employeeLoading = ref(false)
const employeeOptions = ref([])

const query = reactive({ page: 1, size: 10, keyword: '' })

const statusText = (s) => ['待处理', '维修中', '已完成'][s] || '未知'
const statusTagType = (s) => ['warning', 'primary', 'success'][s] || 'info'

const loadData = async () => {
  const res = await getRepairs(query)
  tableData.value = res.data.list
  total.value = res.data.total
}

const searchEmployees = async (keyword) => {
  if (!keyword) {
    employeeOptions.value = []
    return
  }
  employeeLoading.value = true
  try {
    const res = await request.get('/api/employees/all')
    const list = res.data || []
    employeeOptions.value = list.filter(e =>
      e.name.includes(keyword) ||
      (e.position && e.position.includes(keyword)) ||
      (e.department && e.department.includes(keyword))
    )
  } catch (e) { /* ignore */ }
  employeeLoading.value = false
}

const handleView = (row) => {
  viewData.value = row
  viewVisible.value = true
}

const handleAssign = (row) => {
  assignRepairId.value = row.id
  assignEmployeeId.value = null
  employeeOptions.value = []
  assignVisible.value = true
}

const handleComplete = async (row) => {
  await ElMessageBox.confirm('确定标记为完工？', '提示', { type: 'info' })
  await updateRepairStatus(row.id, { status: 2 })
  ElMessage.success('已标记完工')
  loadData()
}

const submitAssign = async () => {
  if (!assignEmployeeId.value) {
    ElMessage.warning('请选择员工')
    return
  }
  await assignRepair(assignRepairId.value, { employeeId: assignEmployeeId.value })
  ElMessage.success('分配成功')
  assignVisible.value = false
  loadData()
}

onMounted(loadData)
</script>
