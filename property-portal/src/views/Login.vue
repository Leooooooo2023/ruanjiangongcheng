<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <template #header>
        <h2 class="auth-title">小区物业 - 业主登录</h2>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" prefix-icon="User" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" prefix-icon="Lock" placeholder="请输入密码" type="password" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item prop="captchaCode">
          <div class="captcha-row">
            <el-input v-model="form.captchaCode" placeholder="请输入验证码" style="flex: 1" @keyup.enter="handleLogin" />
            <img :src="captchaImage" alt="验证码" title="点击刷新" @click="refreshCaptcha" class="captcha-image" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
        <el-form-item>
          <div style="text-align: center; width: 100%">
            还没有账号？<el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login, getCaptcha } from '../api/auth'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')

const form = reactive({ username: '', password: '', captchaCode: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaImage.value = res.data.imageBase64
    captchaKey.value = res.data.key
    form.captchaCode = ''
  } catch {
    ElMessage.error('获取验证码失败')
  }
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login({ username: form.username, password: form.password, role: 1, captchaKey: captchaKey.value, captchaCode: form.captchaCode })
    userStore.setLogin(res.data)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/home'
    router.push(redirect)
  } finally {
    loading.value = false
    refreshCaptcha()
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(64, 158, 255, 0.12), transparent 32%),
    linear-gradient(180deg, #f8fbff 0%, #eef4fb 100%);
}

.auth-card {
  width: min(400px, 100%);
  border-radius: 8px;
  box-shadow: 0 20px 50px rgba(31, 45, 61, 0.14);
}

.auth-title {
  text-align: center;
  margin: 0;
  color: var(--ink);
  font-size: 21px;
  font-weight: 800;
}

.captcha-row {
  display: flex;
  width: 100%;
  gap: 10px;
}

.captcha-image {
  height: 40px;
  cursor: pointer;
  border-radius: 6px;
  border: 1px solid var(--line);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.captcha-image:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(31, 45, 61, 0.12);
}
</style>
