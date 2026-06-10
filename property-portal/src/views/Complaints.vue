<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px">
      <h3 style="margin: 0">我的投诉</h3>
      <el-button type="primary" @click="$router.push('/complaints/create')">提交投诉</el-button>
    </div>

    <el-empty v-if="tableData.length === 0" description="暂无投诉记录" />

    <el-card v-for="item in tableData" :key="item.id" style="margin-bottom: 12px" shadow="hover">
      <h4 style="margin: 0 0 8px">{{ item.title }}</h4>
      <p style="color: #666; margin: 0; font-size: 14px">{{ item.content }}</p>
      <div v-if="item.reply" style="margin-top: 10px; padding: 10px; background: #f0f9eb; border-radius: 6px">
        <div style="font-size: 12px; color: #67C23A; margin-bottom: 4px">物业回复：</div>
        <div style="font-size: 14px; color: #333">{{ item.reply }}</div>
      </div>
      <div style="margin-top: 8px; display: flex; justify-content: space-between; align-items: center">
        <span style="font-size: 12px; color: #999">{{ item.createTime }}</span>
        <el-tag :type="item.status === 0 ? 'warning' : 'success'" size="small">
          {{ item.status === 0 ? '待回复' : '已回复' }}
        </el-tag>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyComplaints } from '../api/complaint'

const tableData = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10 })

const loadData = async () => {
  const res = await getMyComplaints(query)
  tableData.value = res.data.list
  total.value = res.data.total
}

onMounted(loadData)
</script>
