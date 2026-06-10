<template>
  <div style="min-height: 100vh; display: flex; flex-direction: column">
    <!-- 顶部导航 -->
    <el-header style="background: #409EFF; display: flex; align-items: center; justify-content: space-between; padding: 0 20px">
      <span style="color: #fff; font-size: 20px; font-weight: bold">小区物业 - 业主端</span>
      <el-dropdown @command="handleCommand">
        <span style="cursor: pointer; color: #fff; display: flex; align-items: center">
          <el-icon style="margin-right: 5px"><UserFilled /></el-icon>
          {{ userStore.user?.name || '业主' }}
          <el-icon style="margin-left: 5px"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-header>

    <!-- 内容区 -->
    <el-main style="flex: 1; max-width: 800px; margin: 0 auto; width: 100%; padding: 20px">
      <router-view />
    </el-main>

    <!-- 底部导航（移动端） -->
    <div style="display: flex; background: #fff; border-top: 1px solid #e6e6e6; padding: 8px 0">
      <div v-for="item in navItems" :key="item.path"
        @click="$router.push(item.path)"
        :style="{ flex: 1, textAlign: 'center', cursor: 'pointer', color: $route.path === item.path ? '#409EFF' : '#666' }">
        <el-icon :size="22"><component :is="item.icon" /></el-icon>
        <div style="font-size: 12px; margin-top: 2px">{{ item.label }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { logout } from '../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const navItems = [
  { path: '/home', label: '首页', icon: 'HomeFilled' },
  { path: '/repairs', label: '报修', icon: 'SetUp' },
  { path: '/complaints', label: '投诉', icon: 'ChatDotSquare' },
  { path: '/parking', label: '车位', icon: 'Van' },
  { path: '/profile', label: '我的', icon: 'User' }
]

const handleCommand = async (command) => {
  if (command === 'logout') {
    try { await logout() } catch (e) { /* ignore */ }
    userStore.setLogout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>
