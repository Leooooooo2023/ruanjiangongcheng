<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px">
      <h3 style="margin: 0">我的报修</h3>
      <el-button type="primary" @click="$router.push('/repairs/create')">提交报修</el-button>
    </div>

    <el-empty v-if="tableData.length === 0" description="暂无报修记录" />

    <el-card v-for="item in tableData" :key="item.id" style="margin-bottom: 12px" shadow="hover">
      <div style="display: flex; justify-content: space-between; align-items: center">
        <div>
          <h4 style="margin: 0 0 8px">{{ item.title }}</h4>
          <p style="color: #666; margin: 0; font-size: 14px">{{ item.content }}</p>
          <div style="margin-top: 8px; font-size: 12px; color: #999">{{ item.createTime }}</div>
        </div>
        <div style="text-align: right">
          <el-tag :type="statusTagType(item.status)" size="small">{{ statusText(item.status) }}</el-tag>
          <div v-if="item.status === 2 && !item.rating" style="margin-top: 8px">
            <el-button size="small" type="warning" @click="handleRate(item)">评价</el-button>
          </div>
          <div v-if="item.rating" style="margin-top: 8px; color: #E6A23C; font-size: 14px">
            {{ item.rating }}星
          </div>
        </div>
      </div>
    </el-card>

    <el-pagination
      v-if="total > query.size"
      style="margin-top: 15px; justify-content: center"
      v-model:current-page="query.page"
      v-model:page-size="query.size"
      :total="total"
      @current-change="loadData"
      layout="prev, pager, next"
      small
    />

    <!-- 评价弹窗 -->
    <el-dialog v-model="rateVisible" title="完工评价" width="350px">
      <div style="text-align: center">
        <el-rate v-model="rating" :colors="['#F56C6C', '#E6A23C', '#409EFF']" show-text :texts="['很差', '较差', '一般', '较好', '很好']" />
      </div>
      <template #footer>
        <el-button @click="rateVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyRepairs, rateRepair } from '../api/repair'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const total = ref(0)
const rateVisible = ref(false)
const rateId = ref(null)
const rating = ref(5)

const query = reactive({ page: 1, size: 10 })

const statusText = (s) => ['待处理', '维修中', '已完成'][s] || '未知'
const statusTagType = (s) => ['warning', 'primary', 'success'][s] || 'info'

const loadData = async () => {
  const res = await getMyRepairs(query)
  tableData.value = res.data.list
  total.value = res.data.total
}

const handleRate = (item) => {
  rateId.value = item.id
  rating.value = 5
  rateVisible.value = true
}

const submitRate = async () => {
  await rateRepair(rateId.value, { rating: rating.value })
  ElMessage.success('评价成功')
  rateVisible.value = false
  loadData()
}

onMounted(loadData)
</script>
