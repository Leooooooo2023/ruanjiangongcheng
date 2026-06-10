<template>
  <div>
    <!-- 欢迎横幅 -->
    <el-card style="margin-bottom: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none">
      <div style="color: #fff; padding: 10px 0">
        <h2 style="margin: 0 0 8px; font-size: 24px">👋 欢迎回来，{{ userStore.user?.name || '管理员' }}</h2>
        <p style="margin: 0; opacity: 0.9; font-size: 14px">小区物业管理系统 · 今天是 {{ today }}</p>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #409EFF, #36a3f7)">
          <div class="stat-icon"><el-icon :size="28"><OfficeBuilding /></el-icon></div>
          <div class="stat-number">{{ stats.buildings }}</div>
          <div class="stat-label">楼栋总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #67C23A, #85ce61)">
          <div class="stat-icon"><el-icon :size="28"><User /></el-icon></div>
          <div class="stat-number">{{ stats.owners }}</div>
          <div class="stat-label">业主总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #E6A23C, #f0b860)">
          <div class="stat-icon"><el-icon :size="28"><UserFilled /></el-icon></div>
          <div class="stat-number">{{ stats.employees }}</div>
          <div class="stat-label">员工总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #909399, #b0b4b8)">
          <div class="stat-icon"><el-icon :size="28"><Van /></el-icon></div>
          <div class="stat-number">{{ stats.parkings }}</div>
          <div class="stat-label">停车位总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #F56C6C, #f89898)">
          <div class="stat-icon"><el-icon :size="28"><SetUp /></el-icon></div>
          <div class="stat-number">{{ stats.pendingRepairs }}</div>
          <div class="stat-label">待处理报修</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #9b59b6, #be78d0)">
          <div class="stat-icon"><el-icon :size="28"><ChatDotSquare /></el-icon></div>
          <div class="stat-number">{{ stats.pendingComplaints }}</div>
          <div class="stat-label">待回复投诉</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: bold">📋 快捷操作</span>
          </template>
          <el-row :gutter="15">
            <el-col :span="8">
              <div class="quick-action" @click="$router.push('/repairs')">
                <el-icon :size="30" color="#409EFF"><Document /></el-icon>
                <span>报修管理</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="quick-action" @click="$router.push('/complaints')">
                <el-icon :size="30" color="#E6A23C"><ChatDotSquare /></el-icon>
                <span>投诉管理</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="quick-action" @click="$router.push('/parkings')">
                <el-icon :size="30" color="#67C23A"><Van /></el-icon>
                <span>车位管理</span>
              </div>
            </el-col>
            <el-col :span="8" style="margin-top: 15px">
              <div class="quick-action" @click="$router.push('/owners')">
                <el-icon :size="30" color="#9b59b6"><Avatar /></el-icon>
                <span>业主管理</span>
              </div>
            </el-col>
            <el-col :span="8" style="margin-top: 15px">
              <div class="quick-action" @click="$router.push('/employees')">
                <el-icon :size="30" color="#F56C6C"><User /></el-icon>
                <span>员工管理</span>
              </div>
            </el-col>
            <el-col :span="8" style="margin-top: 15px">
              <div class="quick-action" @click="$router.push('/buildings')">
                <el-icon :size="size" color="#909399"><OfficeBuilding /></el-icon>
                <span>楼栋管理</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: bold">📊 车位状态</span>
          </template>
          <el-row :gutter="20" style="text-align: center">
            <el-col :span="12">
              <div style="padding: 20px">
                <div style="font-size: 48px; color: #67C23A">{{ stats.availableParkings }}</div>
                <div style="color: #999; margin-top: 8px">空闲车位</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div style="padding: 20px">
                <div style="font-size: 48px; color: #F56C6C">{{ stats.occupiedParkings }}</div>
                <div style="color: #999; margin-top: 8px">已占用车位</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { getBuildings } from '../api/building'
import { getOwners } from '../api/owner'
import { getEmployees } from '../api/employee'
import { getParkings } from '../api/parking'
import { getRepairs } from '../api/repair'
import { getComplaints } from '../api/complaint'

const router = useRouter()
const userStore = useUserStore()

const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
})

const stats = ref({
  buildings: 0,
  owners: 0,
  employees: 0,
  parkings: 0,
  availableParkings: 0,
  occupiedParkings: 0,
  pendingRepairs: 0,
  pendingComplaints: 0
})

onMounted(async () => {
  try {
    const [b, o, e, p, r, c] = await Promise.all([
      getBuildings({ page: 1, size: 1 }),
      getOwners({ page: 1, size: 1 }),
      getEmployees({ page: 1, size: 1 }),
      getParkings({ page: 1, size: 100 }),
      getRepairs({ page: 1, size: 100 }),
      getComplaints({ page: 1, size: 100 })
    ])
    stats.value.buildings = b.data.total
    stats.value.owners = o.data.total
    stats.value.employees = e.data.total
    stats.value.parkings = p.data.total
    // 从列表中统计空闲/占用
    const parkingList = p.data.list || []
    stats.value.availableParkings = parkingList.filter(p => p.status === 0).length
    stats.value.occupiedParkings = parkingList.filter(p => p.status === 1).length
    // 只统计待处理（status=0）和待回复（status=0）的数量
    const repairList = r.data.list || []
    stats.value.pendingRepairs = repairList.filter(r => r.status === 0).length
    const complaintList = c.data.list || []
    stats.value.pendingComplaints = complaintList.filter(c => c.status === 0).length
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.stat-card {
  border-radius: 10px;
  overflow: hidden;
  color: #fff;
  text-align: center;
  padding: 10px 0;
}
.stat-icon {
  margin-bottom: 8px;
  opacity: 0.9;
}
.stat-number {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 4px;
}
.stat-label {
  font-size: 13px;
  opacity: 0.9;
}
.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f7fa;
}
.quick-action:hover {
  background: #e8f4fd;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.quick-action span {
  margin-top: 8px;
  font-size: 13px;
  color: #666;
}
</style>
