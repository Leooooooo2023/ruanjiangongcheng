<template>
  <div>
    <h3 style="margin-bottom: 15px">个人信息</h3>

    <el-card style="margin-bottom: 20px">
      <template #header>基本信息</template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input :value="userStore.user?.username" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header>住房信息</template>
      <el-form label-width="80px">
        <el-form-item label="楼栋">
          <el-input :value="form.buildingName" disabled />
        </el-form-item>
        <el-form-item label="单元">
          <el-input :value="form.unit" disabled />
        </el-form-item>
        <el-form-item label="房号">
          <el-input :value="form.room" disabled />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header>账户信息</template>
      <el-form label-width="80px">
        <el-form-item label="注册时间">
          <el-input :value="form.createTime" disabled />
        </el-form-item>
      </el-form>
    </el-card>

    <div style="display: flex; gap: 10px; margin-bottom: 20px">
      <el-button type="primary" :loading="saving" @click="handleSave">保存个人信息</el-button>
    </div>

    <el-card>
      <template #header>修改密码</template>
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../store/user'
import { getUserInfo, updatePassword, updateProfile } from '../api/auth'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const pwdFormRef = ref()
const saving = ref(false)

const form = reactive({
  name: '',
  phone: '',
  email: '',
  buildingName: '',
  unit: '',
  room: '',
  createTime: ''
})

const pwdForm = reactive({ oldPassword: '', newPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    const data = res.data
    form.name = data.name || ''
    form.phone = data.phone || ''
    form.email = data.email || ''
    form.buildingName = data.buildingName || '未分配'
    form.unit = data.unit || '未知'
    form.room = data.room || '未知'
    form.createTime = data.createTime || ''
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  loadUserInfo()
})

const handleSave = async () => {
  saving.value = true
  try {
    await updateProfile({
      name: form.name,
      phone: form.phone,
      email: form.email
    })
    ElMessage.success('保存成功')
  } catch (e) { /* error handled by interceptor */ }
  finally { saving.value = false }
}

const handleChangePassword = async () => {
  try {
    await pwdFormRef.value.validate()
  } catch {
    return
  }
  try {
    await updatePassword(pwdForm)
    ElMessage.success('密码修改成功，请重新登录')
    userStore.setLogout()
    window.location.href = '/login'
  } catch (e) { /* error handled by interceptor */ }
}
</script>
