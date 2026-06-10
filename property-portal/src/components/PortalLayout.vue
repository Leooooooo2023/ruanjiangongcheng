<template>
  <div class="portal-shell">
    <!-- 顶部导航 -->
    <el-header class="portal-header">
      <span class="portal-brand">小区物业 - 业主端</span>
      <el-dropdown @command="handleCommand">
        <span class="portal-user">
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
    <el-main class="portal-main">
      <router-view />
    </el-main>

    <!-- 底部导航（移动端） -->
    <div class="portal-tabs">
      <div v-for="item in navItems" :key="item.path"
        class="portal-tab"
        :class="{ active: $route.path === item.path }"
        @click="$router.push(item.path)"
      >
        <el-icon :size="22"><component :is="item.icon" /></el-icon>
        <div class="portal-tab-label">{{ item.label }}</div>
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

<style scoped>
.portal-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--page);
}

.portal-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 22px;
  background: var(--brand);
  box-shadow: 0 10px 26px rgba(64, 158, 255, 0.22);
}

.portal-brand {
  color: #fff;
  font-size: 20px;
  font-weight: 800;
}

.portal-user {
  cursor: pointer;
  color: #fff;
  display: flex;
  align-items: center;
  height: 36px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  transition: background-color 0.18s ease, transform 0.18s ease;
}

.portal-user:hover {
  background: rgba(255, 255, 255, 0.22);
  transform: translateY(-1px);
}

.portal-main {
  flex: 1;
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
  padding: 24px;
}

.portal-tabs {
  display: flex;
  background: rgba(255, 255, 255, 0.96);
  border-top: 1px solid var(--line);
  padding: 8px 10px;
  box-shadow: 0 -10px 24px rgba(31, 45, 61, 0.06);
}

.portal-tab {
  flex: 1;
  min-height: 50px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  border-radius: 8px;
  transition: color 0.18s ease, background-color 0.18s ease, transform 0.18s ease;
}

.portal-tab:hover {
  color: var(--brand);
  background: var(--brand-soft);
  transform: translateY(-1px);
}

.portal-tab.active {
  color: var(--brand);
  background: var(--brand-soft);
  font-weight: 700;
}

.portal-tab-label {
  font-size: 12px;
  margin-top: 2px;
}
</style>
