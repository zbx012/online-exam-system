<!-- src/components/ExamRegistration.vue -->
<template>
  <div>
    <!-- 考试注册模态框 -->
    <div v-if="showExamModal" class="modal">
      <div class="modal-content">
        <h3>注册考试</h3>
        <form @submit.prevent="registerExam">
          <div>
            <label>考试标题*:</label>
            <input v-model="examForm.title" required />
          </div>
          <div>
            <label>开始时间:</label>
            <input v-model="examForm.startTime" type="datetime-local" />
          </div>
          <div>
            <label>结束时间:</label>
            <input v-model="examForm.endTime" type="datetime-local" />
          </div>
          <div>
            <label>状态:</label>
            <select v-model="examForm.status">
              <option value="">请选择</option>
              <option value="draft">草稿</option>
              <option value="published">已发布</option>
              <option value="closed">已关闭</option>
            </select>
          </div>
          <div>
            <button type="submit">提交</button>
            <button type="button" @click="showExamModal = false">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import request from '@/utils/request'

interface ExamForm {
  title: string
  startTime?: string | null
  endTime?: string | null
  status?: string | null
}

const showExamModal = ref(false)
const examForm = reactive<ExamForm>({
  title: '',
  startTime: null,
  endTime: null,
  status: null
})

const registerExam = async () => {
  try {
    // 获取当前教师ID
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    const teacherId = currentUser.id || null
    
    if (!teacherId) {
      alert('无法获取教师信息，请重新登录')
      return
    }
    
    // 处理空值，确保空字符串转换为null
    const payload = {
      title: examForm.title,
      teacherId: teacherId, // 自动使用当前教师ID
      startTime: examForm.startTime || null,
      endTime: examForm.endTime || null,
      status: examForm.status || null
    }

    const response = await request.post('/api/exam/add', payload)
    
    if (response.data === true) {
      alert('考试注册成功')
      showExamModal.value = false
      // 重置表单
      Object.assign(examForm, {
        title: '',
        startTime: null,
        endTime: null,
        status: null
      })
    }
  } catch (error) {
    console.error('考试注册失败:', error)
    alert('考试注册失败')
  }
}

// 暴露方法给父组件使用
const show = () => {
  showExamModal.value = true
}

defineExpose({
  show
})
</script>

<style scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 4px;
  min-width: 300px;
  max-width: 500px;
}

.modal-content h3 {
  margin-top: 0;
}

.modal-content div {
  margin-bottom: 15px;
}

.modal-content label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.modal-content input,
.modal-content select {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.modal-content button {
  margin-right: 10px;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal-content button[type="submit"] {
  background-color: #007bff;
  color: white;
}

.modal-content button[type="button"] {
  background-color: #6c757d;
  color: white;
}
</style>