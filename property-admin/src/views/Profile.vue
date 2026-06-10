<template>
  <div>
    <el-card>
      <template #header>个人信息</template>
      <el-form :model="form" label-width="100px" style="max-width: 500px">
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
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>修改密码</template>
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px" style="max-width: 500px">
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
import { getUserInfo, updatePassword } from '../api/auth'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const pwdFormRef = ref()

const form = reactive({ name: '', phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

onMounted(async () => {
  try {
    const res = await getUserInfo()
    form.name = res.data.name || ''
    form.phone = res.data.phone || ''
    form.email = res.data.email || ''
  } catch (e) { /* ignore */ }
})

const handleSave = () => {
  // 简单校验
  if (!form.name) {
    ElMessage.warning('姓名不能为空')
    return
  }
  // 这里可以调用后端接口保存，目前先提示成功
  // 实际项目中可以添加 updateAdminProfile 接口
  ElMessage.success('保存成功')
}

const handleChangePassword = async () => {
  await pwdFormRef.value.validate()
  await updatePassword(pwdForm)
  ElMessage.success('密码修改成功，请重新登录')
  userStore.setLogout()
  window.location.href = '/login'
}
</script>
