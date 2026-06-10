<template>
  <div>
    <!-- 状态摘要 -->
    <el-card style="margin-bottom: 15px">
      <div style="display: flex; align-items: center; gap: 24px; flex-wrap: wrap">
        <div>
          <span style="color: #666">车位状态：</span>
          <el-tag v-if="myParking" type="success" size="large">
            ⭐ 已分配：{{ myParking.number }}（{{ myParking.location }}）
          </el-tag>
          <el-tag v-else-if="pendingApp" type="warning" size="large">
            ⏳ 审核中：{{ pendingApp.parkingNumber }}
          </el-tag>
          <el-tag v-else type="info" size="large">暂无车位</el-tag>
        </div>
        <div v-if="myParking" style="font-size: 13px; color: #999">
          租期：{{ myParking.startDate }} ~ {{ myParking.endDate }}
        </div>
      </div>
    </el-card>

    <!-- 停车场地图 -->
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>🅿️ 停车场平面图</span>
          <span style="font-size: 12px; color: #999">拖拽移动 · 滚轮缩放 · 点击车位查看详情</span>
        </div>
      </template>
      <ParkingMap
        :spots="allSpots"
        :applications="myApplications"
        :my-parking-id="myParking?.id"
        @spot-click="handleSpotClick"
      />
    </el-card>

    <!-- 车位详情弹窗 -->
    <el-dialog v-model="dialogVisible" title="车位详情" width="480px">
      <template v-if="checkResult">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="车位号">{{ checkResult.parking?.number }}</el-descriptions-item>
          <el-descriptions-item label="位置">{{ checkResult.parking?.location }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="checkResult.parking?.status === 0" type="success">空闲</el-tag>
            <el-tag v-else type="danger">已占用</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="能否申请">
            <el-tag v-if="checkResult.canApply" type="success">✅ 可申请</el-tag>
            <el-tag v-else type="danger">❌ 不可申请</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 不能申请的原因 -->
        <template v-if="!checkResult.canApply && checkResult.reason">
          <el-alert type="warning" :closable="false" show-icon style="margin-top: 12px">
            <template #title>{{ checkResult.reason }}</template>
          </el-alert>
        </template>

        <!-- 审核中 -->
        <template v-if="checkResult.applyStatus === 'pending'">
          <el-alert type="warning" :closable="false" show-icon style="margin-top: 12px">
            <template #title>您已申请该车位，正在等待管理员审核</template>
          </el-alert>
        </template>

        <!-- 已占用且是自己的 -->
        <template v-if="checkResult.parking?.status === 1 && myParking && checkResult.parking?.id === myParking.id">
          <el-alert type="success" :closable="false" show-icon style="margin-top: 12px">
            <template #title>这是您的车位</template>
            <p style="margin: 4px 0 0; font-size: 12px">
              租期：{{ myParking.startDate }} ~ {{ myParking.endDate }}
            </p>
          </el-alert>
        </template>
      </template>

      <template v-if="checkLoading">
        <el-skeleton :rows="5" animated />
      </template>

      <template #footer>
        <!-- 可申请时显示申请按钮 -->
        <el-button
          v-if="checkResult?.canApply"
          type="primary"
          :loading="applying"
          @click="submitApply"
        >
          申请该车位
        </el-button>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 我的申请记录 -->
    <el-card style="margin-top: 15px" v-if="myApplications.length > 0">
      <template #header><span>我的申请记录</span></template>
      <el-table :data="myApplications" border stripe size="small">
        <el-table-column prop="parkingNumber" label="车位号" width="100" />
        <el-table-column prop="parkingLocation" label="位置" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="170" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllParkings, checkParking, applyParking, getMyApplications, getMyParking } from '../api/parking'
import ParkingMap from '../components/ParkingMap.vue'

const allSpots = ref([])
const myParking = ref(null)
const myApplications = ref([])
const dialogVisible = ref(false)
const checkLoading = ref(false)
const checkResult = ref(null)
const applying = ref(false)

const pendingApp = computed(() => {
  return myApplications.value.find(a => a.status === 0)
})

const loadData = async () => {
  // 独立加载，一个失败不影响另一个
  try {
    const spotsRes = await getAllParkings()
    allSpots.value = spotsRes.data?.list || []
  } catch (e) { /* ignore */ }
  try {
    const parkingRes = await getMyParking()
    myParking.value = parkingRes.data
  } catch (e) { /* ignore */ }
  try {
    const appsRes = await getMyApplications()
    myApplications.value = appsRes.data || []
  } catch (e) { /* ignore */ }
}

const handleSpotClick = async ({ spot }) => {
  if (!spot) return
  checkLoading.value = true
  checkResult.value = null
  dialogVisible.value = true

  try {
    const res = await checkParking(spot.id)
    checkResult.value = res.data
  } catch (e) {
    checkResult.value = null
  }
  checkLoading.value = false
}

const submitApply = async () => {
  if (!checkResult.value?.parking) return
  applying.value = true
  try {
    await applyParking(checkResult.value.parking.id)
    ElMessage.success('申请已提交，请等待管理员审核')
    dialogVisible.value = false
    // 刷新数据（独立加载）
    try {
      const spotsRes = await getAllParkings()
      allSpots.value = spotsRes.data?.list || []
    } catch (e) { /* ignore */ }
    try {
      const parkingRes = await getMyParking()
      myParking.value = parkingRes.data
    } catch (e) { /* ignore */ }
    try {
      const appsRes = await getMyApplications()
      myApplications.value = appsRes.data || []
    } catch (e) { /* ignore */ }
  } catch (e) {
    // 申请失败，错误已在拦截器中处理
  }
  applying.value = false
}

onMounted(loadData)
</script>
