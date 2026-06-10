<template>
  <div>
    <h3 style="margin-bottom: 15px">提交报修</h3>
    <el-card>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入报修标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请描述报修问题" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            accept="image/*"
            list-type="picture"
          >
            <el-button size="small" type="primary">选择图片</el-button>
            <template #tip><div style="font-size: 12px; color: #999">支持手机拍照上传</div></template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { submitRepair } from '../api/repair'
import { uploadFile } from '../api/file'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const selectedFile = ref(null)

const form = reactive({ title: '', content: '', image: '' })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const handleSubmit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    // 先上传图片
    if (selectedFile.value) {
      const uploadRes = await uploadFile(selectedFile.value)
      form.image = uploadRes.data
    }
    await submitRepair(form)
    ElMessage.success('提交成功')
    router.push('/repairs')
  } finally {
    loading.value = false
  }
}
</script>
