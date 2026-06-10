<template>
  <div>
    <el-card>
      <template #header>
        <span>留言投诉管理</span>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ownerId" label="业主ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'warning' : 'success'">
              {{ scope.row.status === 0 ? '待回复' : '已回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reply" label="回复" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" v-if="scope.row.status === 0" @click="handleReply(scope.row)">回复</el-button>
            <el-button size="small" v-else @click="handleView(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        style="margin-top: 15px; justify-content: flex-end"
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        @current-change="loadData"
        layout="total, prev, pager, next"
      />
    </el-card>

    <!-- 回复 -->
    <el-dialog v-model="replyVisible" title="回复投诉" width="500px">
      <el-form label-width="80px">
        <el-form-item label="标题">
          <span>{{ replyData.title }}</span>
        </el-form-item>
        <el-form-item label="内容">
          <span>{{ replyData.content }}</span>
        </el-form-item>
        <el-form-item label="回复">
          <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看 -->
    <el-dialog v-model="viewVisible" title="投诉详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ viewData.content }}</el-descriptions-item>
        <el-descriptions-item label="回复">{{ viewData.reply || '暂无回复' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getComplaints, replyComplaint } from '../api/complaint'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const total = ref(0)
const replyVisible = ref(false)
const viewVisible = ref(false)
const replyData = ref({})
const viewData = ref({})
const replyContent = ref('')

const query = reactive({ page: 1, size: 10, keyword: '' })

const loadData = async () => {
  const res = await getComplaints(query)
  tableData.value = res.data.list
  total.value = res.data.total
}

const handleReply = (row) => {
  replyData.value = row
  replyContent.value = ''
  replyVisible.value = true
}

const handleView = (row) => {
  viewData.value = row
  viewVisible.value = true
}

const submitReply = async () => {
  if (!replyContent.value) {
    ElMessage.warning('请输入回复内容')
    return
  }
  await replyComplaint(replyData.value.id, { reply: replyContent.value })
  ElMessage.success('回复成功')
  replyVisible.value = false
  loadData()
}

onMounted(loadData)
</script>
