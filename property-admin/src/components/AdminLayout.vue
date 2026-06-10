<template>
  <el-container class="admin-shell">
    <el-aside width="232px" class="admin-aside">
      <div class="admin-brand">
        物业管理系统
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="#cbd5e1"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/buildings">
          <el-icon><OfficeBuilding /></el-icon>
          <span>楼栋管理</span>
        </el-menu-item>
        <el-menu-item index="/employees">
          <el-icon><User /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/owners">
          <el-icon><Avatar /></el-icon>
          <span>业主管理</span>
        </el-menu-item>
        <el-menu-item index="/parkings">
          <el-icon><Van /></el-icon>
          <span>停车位管理</span>
        </el-menu-item>
        <el-menu-item index="/repairs">
          <el-icon><SetUp /></el-icon>
          <span>报修管理</span>
        </el-menu-item>
        <el-menu-item index="/complaints">
          <el-icon><ChatDotSquare /></el-icon>
          <span>留言投诉</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><Setting /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <span class="admin-title">管理员后台</span>
        <el-dropdown @command="handleCommand">
          <span class="admin-user">
            <el-icon style="margin-right: 5px"><UserFilled /></el-icon>
            {{ userStore.user?.name || '管理员' }}
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
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { logout } from '../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await logout()
    } catch (e) { /* ignore */ }
    userStore.setLogout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.admin-shell {
  min-height: 100vh;
  background: var(--page);
}

.admin-aside {
  background: #26364b;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 8px 0 28px rgba(31, 45, 61, 0.12);
}

.admin-brand {
  height: 68px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.admin-aside :deep(.el-menu) {
  border-right: none;
  padding: 12px;
}

.admin-aside :deep(.el-menu-item) {
  height: 44px;
  margin-bottom: 6px;
  border-radius: 8px;
  transition: background-color 0.18s ease, color 0.18s ease, transform 0.18s ease;
}

.admin-aside :deep(.el-menu-item:hover) {
  background: rgba(64, 158, 255, 0.16);
  transform: translateX(2px);
}

.admin-aside :deep(.el-menu-item.is-active) {
  background: var(--brand);
  box-shadow: 0 10px 24px rgba(64, 158, 255, 0.26);
}

.admin-header {
  height: 68px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid var(--line);
  backdrop-filter: blur(10px);
}

.admin-title {
  font-size: 17px;
  font-weight: 800;
  color: var(--ink);
}

.admin-user {
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 38px;
  padding: 0 12px;
  border-radius: 999px;
  color: #344054;
  background: #f8fbff;
  border: 1px solid var(--line);
  transition: background-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;
}

.admin-user:hover {
  background: var(--brand-soft);
  box-shadow: 0 8px 18px rgba(64, 158, 255, 0.14);
  transform: translateY(-1px);
}

.admin-main {
  padding: 24px;
  overflow: auto;
}
</style>
