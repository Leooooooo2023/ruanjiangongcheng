<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background-color: #304156">
      <div style="height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold">
        物业管理系统
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
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
      <el-header style="display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #e6e6e6">
        <span style="font-size: 16px; font-weight: bold">管理员后台</span>
        <el-dropdown @command="handleCommand">
          <span style="cursor: pointer; display: flex; align-items: center">
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
      <el-main>
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
