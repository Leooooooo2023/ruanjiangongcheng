<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>楼栋管理</span>
          <el-button type="primary" @click="handleAdd">新增楼栋</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="楼栋名称" />
        <el-table-column prop="units" label="单元数" />
        <el-table-column prop="floors" label="楼层数" />
        <el-table-column prop="description" label="描述" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑楼栋' : '新增楼栋'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入楼栋名称" />
        </el-form-item>
        <el-form-item label="单元数" prop="units">
          <el-input-number v-model="form.units" :min="1" />
        </el-form-item>
        <el-form-item label="楼层数" prop="floors">
          <el-input-number v-model="form.floors" :min="1" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
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
import { getBuildings, addBuilding, updateBuilding, deleteBuilding } from '../api/building'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const query = reactive({ page: 1, size: 10, keyword: '' })
const form = reactive({ id: null, name: '', units: 1, floors: 1, description: '' })
const rules = {
  name: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  units: [{ required: true, message: '请输入单元数', trigger: 'blur' }],
  floors: [{ required: true, message: '请输入楼层数', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getBuildings(query)
  tableData.value = res.data.list
  total.value = res.data.total
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', units: 1, floors: 1, description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该楼栋？', '提示', { type: 'warning' })
  await deleteBuilding(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateBuilding(form.id, form)
    ElMessage.success('修改成功')
  } else {
    await addBuilding(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>
