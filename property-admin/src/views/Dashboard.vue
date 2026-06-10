<template>
  <div>
    <!-- 欢迎横幅 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎回来，{{ userStore.user?.name || '管理员' }}</h2>
        <p>小区物业管理系统 · 今天是 {{ today }}</p>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card stat-blue">
          <div class="stat-icon"><el-icon :size="28"><OfficeBuilding /></el-icon></div>
          <div class="stat-number">{{ stats.buildings }}</div>
          <div class="stat-label">楼栋总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card stat-green">
          <div class="stat-icon"><el-icon :size="28"><User /></el-icon></div>
          <div class="stat-number">{{ stats.owners }}</div>
          <div class="stat-label">业主总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card stat-amber">
          <div class="stat-icon"><el-icon :size="28"><UserFilled /></el-icon></div>
          <div class="stat-number">{{ stats.employees }}</div>
          <div class="stat-label">员工总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card stat-slate">
          <div class="stat-icon"><el-icon :size="28"><Van /></el-icon></div>
          <div class="stat-number">{{ stats.parkings }}</div>
          <div class="stat-label">停车位总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card stat-red">
          <div class="stat-icon"><el-icon :size="28"><SetUp /></el-icon></div>
          <div class="stat-number">{{ stats.pendingRepairs }}</div>
          <div class="stat-label">待处理报修</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card stat-indigo">
          <div class="stat-icon"><el-icon :size="28"><ChatDotSquare /></el-icon></div>
          <div class="stat-number">{{ stats.pendingComplaints }}</div>
          <div class="stat-label">待回复投诉</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <span class="panel-title">快捷操作</span>
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
                <el-icon :size="30" color="#909399"><OfficeBuilding /></el-icon>
                <span>楼栋管理</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <span class="panel-title">车位状态</span>
          </template>
          <el-row :gutter="20" style="text-align: center">
            <el-col :span="12">
              <div style="padding: 20px">
                <div class="parking-number success">{{ stats.availableParkings }}</div>
                <div class="parking-label">空闲车位</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div style="padding: 20px">
                <div class="parking-number danger">{{ stats.occupiedParkings }}</div>
                <div class="parking-label">已占用车位</div>
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
  border-radius: 8px;
  overflow: hidden;
  color: var(--ink);
  text-align: center;
  padding: 10px 0;
  border: 1px solid var(--line);
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}
.welcome-card {
  margin-bottom: 20px;
  border: 1px solid rgba(64, 158, 255, 0.18);
  background: #f8fbff;
}
.welcome-content {
  padding: 12px 2px;
}
.welcome-content h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: var(--ink);
}
.welcome-content p {
  margin: 0;
  color: var(--muted);
  font-size: 14px;
}
.stat-card:hover {
  transform: translateY(-3px);
  border-color: rgba(64, 158, 255, 0.28);
  box-shadow: 0 16px 34px rgba(31, 45, 61, 0.12);
}
.stat-blue {
  background: #f2f8ff;
}
.stat-green {
  background: #f3faf0;
}
.stat-amber {
  background: #fff8ed;
}
.stat-slate {
  background: #f7f8fa;
}
.stat-red {
  background: #fff5f5;
}
.stat-indigo {
  background: #f6f4ff;
}
.stat-icon {
  margin-bottom: 8px;
  color: var(--brand);
}
.stat-number {
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 4px;
}
.stat-label {
  font-size: 13px;
  color: var(--muted);
}
.panel-card {
  min-height: 100%;
}
.panel-title {
  font-weight: 800;
  color: var(--ink);
}
.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.18s ease, transform 0.18s ease, box-shadow 0.18s ease;
  background: #f8fbff;
  border: 1px solid var(--line);
}
.quick-action:hover {
  background: var(--brand-soft);
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(64, 158, 255, 0.16);
}
.quick-action span {
  margin-top: 8px;
  font-size: 13px;
  color: #344054;
  font-weight: 600;
}
.parking-number {
  font-size: 48px;
  font-weight: 800;
}
.parking-number.success {
  color: #67C23A;
}
.parking-number.danger {
  color: #F56C6C;
}
.parking-label {
  color: var(--muted);
  margin-top: 8px;
}
</style>
