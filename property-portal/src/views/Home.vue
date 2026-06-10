<template>
  <div class="home-page">
    <el-card class="welcome-card">
      <h3>欢迎，{{ userStore.user?.name || '业主' }}</h3>
      <p>小区物业管理系统 - 业主端</p>
    </el-card>

    <el-row :gutter="15">
      <el-col :span="12" :xs="24">
        <el-card shadow="hover" class="service-card" @click="$router.push('/repairs/create')">
          <div class="service-content">
            <el-icon :size="40" color="#E6A23C"><SetUp /></el-icon>
            <div>提交报修</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12" :xs="24">
        <el-card shadow="hover" class="service-card" @click="$router.push('/complaints/create')">
          <div class="service-content">
            <el-icon :size="40" color="#F56C6C"><ChatDotSquare /></el-icon>
            <div>留言投诉</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12" :xs="24">
        <el-card shadow="hover" class="service-card" @click="$router.push('/repairs')">
          <div class="service-content">
            <el-icon :size="40" color="#409EFF"><List /></el-icon>
            <div>我的报修</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12" :xs="24">
        <el-card shadow="hover" class="service-card" @click="$router.push('/parking')">
          <div class="service-content">
            <el-icon :size="40" color="#67C23A"><Van /></el-icon>
            <div>我的车位</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useUserStore } from '../store/user'
const userStore = useUserStore()
</script>

<style scoped>
.home-page {
  padding-bottom: 8px;
}

.welcome-card {
  margin-bottom: 20px;
  background: #f8fbff;
  border-color: rgba(64, 158, 255, 0.18);
}

.welcome-card h3 {
  margin: 0 0 10px;
  font-size: 22px;
  font-weight: 800;
  color: var(--ink);
}

.welcome-card p {
  color: var(--muted);
  margin: 0;
}

.service-card {
  cursor: pointer;
  margin-bottom: 15px;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.service-card:hover {
  transform: translateY(-3px);
  border-color: rgba(64, 158, 255, 0.28);
  box-shadow: 0 16px 34px rgba(31, 45, 61, 0.12);
}

.service-card:active {
  transform: translateY(-1px) scale(0.99);
}

.service-content {
  text-align: center;
  padding: 24px 0;
  color: #344054;
  font-size: 16px;
  font-weight: 700;
}

.service-content div {
  margin-top: 10px;
}
</style>
