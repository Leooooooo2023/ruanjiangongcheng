<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>业主管理</span>
          <el-button type="primary" @click="handleAdd">新增业主</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="登录账号" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="buildingName" label="楼栋" width="120" />
        <el-table-column prop="unit" label="单元" width="80" />
        <el-table-column prop="room" label="房号" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑业主' : '新增业主'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input v-model="form.password" placeholder="默认密码：owner123" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" clearable style="width: 100%">
            <el-option
              v-for="b in buildingList"
              :key="b.id"
              :label="b.name"
              :value="b.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="单元号" prop="unit">
          <el-input v-model="form.unit" />
        </el-form-item>
        <el-form-item label="房号" prop="room">
          <el-input v-model="form.room" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOwners, addOwner, updateOwner, deleteOwner } from '../api/owner'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const buildingList = ref([])

const query = reactive({ page: 1, size: 10, keyword: '' })
const form = reactive({ id: null, username: '', name: '', password: '', phone: '', email: '', buildingId: null, unit: '', room: '' })
const rules = {
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  unit: [{ required: true, message: '请输入单元号', trigger: 'blur' }],
  room: [{ required: true, message: '请输入房号', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getOwners(query)
  tableData.value = res.data.list
  total.value = res.data.total
}

const loadBuildings = async () => {
  try {
    const res = await request.get('/api/buildings/all')
    buildingList.value = res.data || []
  } catch (e) { /* ignore */ }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, username: '', name: '', password: '', phone: '', email: '', buildingId: null, unit: '', room: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该业主？', '提示', { type: 'warning' })
  await deleteOwner(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateOwner(form.id, form)
    ElMessage.success('修改成功')
  } else {
    await addOwner(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
  loadBuildings()
})
</script>
