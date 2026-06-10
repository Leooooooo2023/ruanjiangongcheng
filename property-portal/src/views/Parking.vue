<template>
  <div>
    <h3 style="margin-bottom: 15px">我的车位</h3>
    <el-card v-if="parking">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="车位号">{{ parking.number }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ parking.location }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag type="success">已分配</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="租期开始">{{ parking.startDate }}</el-descriptions-item>
        <el-descriptions-item label="租期结束">{{ parking.endDate }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-empty v-else description="暂无分配车位" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const parking = ref(null)

onMounted(async () => {
  try {
    const res = await request.get('/api/owner/parking')
    parking.value = res.data
  } catch (e) { /* ignore */ }
})
</script>
