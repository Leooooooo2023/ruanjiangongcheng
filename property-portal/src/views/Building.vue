<template>
  <div>
    <h3 style="margin-bottom: 15px">我的楼栋</h3>
    <el-card v-if="building">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="楼栋名称">{{ building.name }}</el-descriptions-item>
        <el-descriptions-item label="单元数">{{ building.units }}</el-descriptions-item>
        <el-descriptions-item label="楼层数">{{ building.floors }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ building.description || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top: 15px">
        <el-descriptions :column="1" border title="我的住房信息">
          <el-descriptions-item label="单元号">{{ ownerInfo.unit || '-' }}</el-descriptions-item>
          <el-descriptions-item label="房号">{{ ownerInfo.room || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
    <el-empty v-else description="暂无楼栋信息" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../utils/request'

const building = ref(null)
const ownerInfo = reactive({ unit: '', room: '' })

onMounted(async () => {
  try {
    const res = await request.get('/api/owner/building')
    const data = res.data
    ownerInfo.unit = data.unit || ''
    ownerInfo.room = data.room || ''
    building.value = data.building || null
  } catch (e) { /* ignore */ }
})
</script>
