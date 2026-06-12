<template>
  <el-dialog
    v-model="visible"
    title="创建考试"
    width="520px"
    :close-on-click-modal="false"
    @closed="resetForm"
  >
    <el-form
      ref="formRef"
      :model="examForm"
      :rules="rules"
      label-width="90px"
    >
      <el-form-item label="考试名称" prop="title">
        <el-input v-model="examForm.title" placeholder="请输入考试名称" />
      </el-form-item>

      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="examForm.startTime"
          type="datetime"
          placeholder="选择开始时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="examForm.endTime"
          type="datetime"
          placeholder="选择结束时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select v-model="examForm.status" placeholder="请选择状态" style="width: 100%">
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="published" />
          <el-option label="已关闭" value="closed" />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitExam" :loading="submitting">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const visible = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const examForm = reactive({
  title: '',
  startTime: '',
  endTime: '',
  status: 'draft'
})

const rules = {
  title: [
    { required: true, message: '请输入考试名称', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const resetForm = () => {
  examForm.title = ''
  examForm.startTime = ''
  examForm.endTime = ''
  examForm.status = 'draft'
  formRef.value?.resetFields()
}

const submitExam = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    const teacherId = currentUser.id || null

    if (!teacherId) {
      ElMessage.error('无法获取教师信息，请重新登录')
      return
    }

    const params = new URLSearchParams()
    params.append('title', examForm.title)
    params.append('teacherId', teacherId)
    params.append('startTime', examForm.startTime)
    params.append('endTime', examForm.endTime)
    params.append('status', examForm.status)

    const response = await request.post('/api/exam/add', params)

    if (response.data === true) {
      ElMessage.success('考试创建成功')
      visible.value = false
      // 通知父组件刷新
      window.dispatchEvent(new Event('exam-created'))
    }
  } catch (error) {
    ElMessage.error('创建失败: ' + (error.response?.data?.message || error.message))
  } finally {
    submitting.value = false
  }
}

const show = () => {
  visible.value = true
}

defineExpose({ show })
</script>
