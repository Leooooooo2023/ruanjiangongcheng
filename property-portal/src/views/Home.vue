<template>
  <div>
    <el-card style="margin-bottom: 20px">
      <h3 style="margin: 0 0 10px">欢迎，{{ userStore.user?.name || '业主' }}</h3>
      <p style="color: #999; margin: 0">小区物业管理系统 - 业主端</p>
    </el-card>

    <el-row :gutter="15">
      <el-col :span="12" :xs="24">
        <el-card shadow="hover" style="cursor: pointer; margin-bottom: 15px" @click="$router.push('/repairs/create')">
          <div style="text-align: center; padding: 20px 0">
            <el-icon :size="40" color="#E6A23C"><SetUp /></el-icon>
            <div style="margin-top: 10px; font-size: 16px">提交报修</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12" :xs="24">
        <el-card shadow="hover" style="cursor: pointer; margin-bottom: 15px" @click="$router.push('/complaints/create')">
          <div style="text-align: center; padding: 20px 0">
            <el-icon :size="40" color="#F56C6C"><ChatDotSquare /></el-icon>
            <div style="margin-top: 10px; font-size: 16px">留言投诉</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12" :xs="24">
        <el-card shadow="hover" style="cursor: pointer; margin-bottom: 15px" @click="$router.push('/repairs')">
          <div style="text-align: center; padding: 20px 0">
            <el-icon :size="40" color="#409EFF"><List /></el-icon>
            <div style="margin-top: 10px; font-size: 16px">我的报修</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12" :xs="24">
        <el-card shadow="hover" style="cursor: pointer; margin-bottom: 15px" @click="$router.push('/parking')">
          <div style="text-align: center; padding: 20px 0">
            <el-icon :size="40" color="#67C23A"><Van /></el-icon>
            <div style="margin-top: 10px; font-size: 16px">我的车位</div>
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
