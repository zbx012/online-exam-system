<template>
  <div class="exam-list">
    <h2>考试管理</h2>
    
    <!-- 选项卡 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="已选课程" name="selected">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>已选课程列表</span>
            </div>
          </template>
          
          <el-table 
            :data="selectedExamsPage" 
            v-loading="selectedLoading"
            style="width: 100%"
          >
            <el-table-column prop="examName" label="考试名称" width="150"></el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
            <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
            <el-table-column prop="score" label="成绩" width="80">
              <template #default="scope">
                <span v-if="scope.row.score !== null">{{ scope.row.score }}</span>
                <span v-else>未考试</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getExamStatusType(scope.row.status)">
                  {{ getExamStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button 
                  size="small" 
                  type="primary" 
                  @click="enterExam(scope.row)"
                  :disabled="!isExamAvailable(scope.row)"
                >
                  进入考试
                </el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  @click="dropCourse(scope.row)"
                >
                  退课
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 已选课程分页 -->
          <el-pagination
            v-model:current-page="selectedCurrentPage"
            v-model:page-size="selectedPageSize"
            :page-sizes="[5, 10, 20]"
            :total="selectedExams.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSelectedSizeChange"
            @current-change="handleSelectedCurrentChange"
            style="margin-top: 20px; justify-content: center;"
          />
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="所有考试" name="all">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>所有考试列表</span>
            </div>
          </template>
          
          <el-table 
            :data="allExamsPage" 
            v-loading="allLoading"
            style="width: 100%"
          >
            <el-table-column prop="examName" label="考试名称" width="150"></el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
            <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getExamStatusType(scope.row.status)">
                  {{ getExamStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button 
                  size="small" 
                  type="success" 
                  @click="showSelectExamDialog(scope.row)"
                  :disabled="isExamSelected(scope.row.examId)"
                >
                  {{ isExamSelected(scope.row.examId) ? '已选' : '选课' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 所有考试分页 -->
          <el-pagination
            v-model:current-page="allCurrentPage"
            v-model:page-size="allPageSize"
            :page-sizes="[5, 10, 20]"
            :total="allExams.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleAllSizeChange"
            @current-change="handleAllCurrentChange"
            style="margin-top: 20px; justify-content: center;"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 考试界面 -->
    <el-dialog 
      v-model="examDialogVisible" 
      :title="currentExam?.examName" 
      width="80%" 
      :fullscreen="true"
      append-to-body
    >
      <div v-if="!examSubmitted" class="exam-container">
        <div class="exam-header">
          <el-tag :type="getExamStatusType(currentExam?.status)">
            {{ getExamStatusText(currentExam?.status) }}
          </el-tag>
          <div class="timer">剩余时间: {{ formatTime(remainingTime) }}</div>
          <el-progress 
            :percentage="(currentQuestionIndex / (questions.length || 1)) * 100" 
            :format="() => `${currentQuestionIndex + 1}/${questions.length || 1}`"
            style="flex: 1; margin: 0 20px;"
          />
        </div>
        
        <el-card v-if="questions.length > 0 && currentQuestionIndex < questions.length" class="question-card">
          <div class="question-info">
            <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
            <span class="question-score">
              {{ questions[currentQuestionIndex]?.type === 'FB' ? '填空题' : '选择题' }} 
              ({{ questions[currentQuestionIndex]?.score }}分)
            </span>
          </div>
          
          <div class="question-content">
            {{ questions[currentQuestionIndex]?.questionContent }}
          </div>
          
          <div v-if="questions[currentQuestionIndex]?.type === 'CQ'" class="options">
            <el-radio-group v-model="answers[questions[currentQuestionIndex].questionId]">
              <el-radio :label="'A'" class="option-item">
                A. {{ questions[currentQuestionIndex]?.optionA }}
              </el-radio>
              <el-radio :label="'B'" class="option-item">
                B. {{ questions[currentQuestionIndex]?.optionB }}
              </el-radio>
              <el-radio :label="'C'" class="option-item">
                C. {{ questions[currentQuestionIndex]?.optionC }}
              </el-radio>
              <el-radio :label="'D'" class="option-item">
                D. {{ questions[currentQuestionIndex]?.optionD }}
              </el-radio>
            </el-radio-group>
          </div>
          
          <div v-else-if="questions[currentQuestionIndex]?.type === 'FB'" class="fill-blank">
            <el-input 
              v-model="answers[questions[currentQuestionIndex].questionId]" 
              placeholder="请输入答案"
              style="width: 300px;"
            />
          </div>
        </el-card>
        
        <div class="navigation-buttons">
          <el-button 
            @click="prevQuestion" 
            :disabled="currentQuestionIndex === 0"
          >
            上一题
          </el-button>
          
          <el-button 
            v-if="questions.length > 0 && currentQuestionIndex < questions.length - 1" 
            type="primary" 
            @click="nextQuestion"
          >
            下一题
          </el-button>
          
          <el-button 
            v-if="questions.length > 0 && currentQuestionIndex === questions.length - 1" 
            type="success" 
            @click="submitExam"
          >
            提交试卷
          </el-button>
        </div>
      </div>
      
      <div v-else class="result-section">
        <el-result 
          icon="success" 
          title="考试已完成" 
          :sub-title="`您的得分: ${finalScore}`"
        >
          <template #extra>
            <el-button type="primary" @click="closeExamDialog">关闭</el-button>
          </template>
        </el-result>
      </div>
    </el-dialog>
    
    <!-- 选课确认对话框 -->
    <el-dialog v-model="selectDialogVisible" :title="`选择考试 - ${selectedExamForDialog.examName}`" width="500px">
      <p>您确定要选择考试 "{{ selectedExamForDialog.examName }}" 吗？</p>
      <p>开始时间: {{ selectedExamForDialog.startTime }}</p>
      <p>结束时间: {{ selectedExamForDialog.endTime }}</p>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="selectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSelectExam">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onBeforeUnmount } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

// 选项卡
const activeTab = ref('selected')

// 数据
const selectedExams = ref([])
const allExams = ref([])
const selectedLoading = ref(false)
const allLoading = ref(false)

// 分页 - 已选课程
const selectedCurrentPage = ref(1)
const selectedPageSize = ref(10)

// 分页 - 所有考试
const allCurrentPage = ref(1)
const allPageSize = ref(10)

// 选课对话框
const selectDialogVisible = ref(false)
const selectedExamForDialog = ref({})

// 考试对话框
const examDialogVisible = ref(false)
const currentExam = ref(null)
const questions = ref([])
const answers = ref({})
const currentQuestionIndex = ref(0)
const examSubmitted = ref(false)
const finalScore = ref(0)

// 考试倒计时
const remainingTime = ref(0)
let timerInterval = null

// 计算属性 - 已选课程分页数据
const selectedExamsPage = computed(() => {
  const start = (selectedCurrentPage.value - 1) * selectedPageSize.value
  const end = start + selectedPageSize.value
  return selectedExams.value.slice(start, end)
})

// 计算属性 - 所有考试分页数据
const allExamsPage = computed(() => {
  const start = (allCurrentPage.value - 1) * allPageSize.value
  const end = start + allPageSize.value
  return allExams.value.slice(start, end)
})

// 获取当前学生ID
const getCurrentStudentId = () => {
  try {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    return currentUser.id || null
  } catch (error) {
    console.error('获取学生ID失败:', error)
    return null
  }
}

// 判断考试是否已选
const isExamSelected = (examId) => {
  return selectedExams.value.some(exam => exam.examId === examId)
}

// 获取已选课程
const fetchSelectedExams = async () => {
  const studentId = getCurrentStudentId()
  if (!studentId) {
    ElMessage.error('无法获取学生信息，请重新登录')
    return
  }
  
  selectedLoading.value = true
  try {
    // 获取学生选课信息
    const response = await request.get('/api/examStudent/selectByStudent', {
      params: {
        studentId: studentId
      }
    })
    
    if (response.data && Array.isArray(response.data)) {
      const examStudentMap = {}
      response.data.forEach(item => {
        examStudentMap[item.examId] = item
      })
      
      // 获取考试详细信息
      const examResponse = await request.get('/api/exam/selectAll')
      if (examResponse.data && Array.isArray(examResponse.data)) {
        // 过滤出已选的考试
        const selectedExamList = examResponse.data.filter(exam => 
          examStudentMap.hasOwnProperty(exam.examId)
        ).map(exam => ({
          ...exam,
          recordId: examStudentMap[exam.examId].recordId,
          score: examStudentMap[exam.examId].score
        }))
        
        selectedExams.value = selectedExamList
        // 重置分页
        selectedCurrentPage.value = 1
      }
    }
  } catch (error) {
    ElMessage.error('获取已选课程失败: ' + (error.response?.data?.message || error.message))
    console.error('获取已选课程错误:', error)
  } finally {
    selectedLoading.value = false
  }
}

// 获取所有考试
const fetchAllExams = async () => {
  allLoading.value = true
  try {
    const response = await request.get('/api/exam/selectAll')
    
    if (response.data && Array.isArray(response.data)) {
      allExams.value = response.data
      // 重置分页
      allCurrentPage.value = 1
    }
  } catch (error) {
    ElMessage.error('获取考试列表失败: ' + (error.response?.data?.message || error.message))
    console.error('获取考试列表错误:', error)
  } finally {
    allLoading.value = false
  }
}

// 显示选课对话框
const showSelectExamDialog = (exam) => {
  // 防止重复选择
  if (isExamSelected(exam.examId)) {
    ElMessage.info('该课程已选')
    return
  }
  
  selectedExamForDialog.value = { ...exam }
  selectDialogVisible.value = true
}

// 确认选课
const confirmSelectExam = async () => {
  const studentId = getCurrentStudentId()
  if (!studentId) {
    ElMessage.error('无法获取学生信息，请重新登录')
    return
  }
  
  try {
    const response = await request.post('/api/examStudent/insert', null, {
      params: {
        examId: selectedExamForDialog.value.examId,
        studentId: studentId
      }
    })
    
    if (response.data) {
      ElMessage.success('选课成功')
      selectDialogVisible.value = false
      // 刷新数据
      fetchSelectedExams()
      fetchAllExams()
    }
  } catch (error) {
    ElMessage.error('选课失败: ' + (error.response?.data?.message || error.message))
    console.error('选课错误:', error)
  }
}

// 退课
const dropCourse = (exam) => {
  ElMessageBox.confirm(
    `确定要退选课程 "${exam.examName}" 吗？`,
    '确认退课',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    const studentId = getCurrentStudentId()
    if (!studentId) {
      ElMessage.error('无法获取学生信息，请重新登录')
      return
    }
    
    try {
      const response = await request.delete('/api/examStudent/delete', {
        params: {
          examId: exam.examId,
          studentId: studentId
        }
      })
      
      if (response.data) {
        ElMessage.success('退课成功')
        // 刷新数据
        fetchSelectedExams()
        fetchAllExams()
      }
    } catch (error) {
      ElMessage.error('退课失败: ' + (error.response?.data?.message || error.message))
      console.error('退课错误:', error)
    }
  }).catch(() => {
    // 用户取消退课
  })
}

// 进入考试
const enterExam = async (exam) => {
  // 检查考试状态是否允许进入
  if (!isExamAvailable(exam)) {
    ElMessage.warning('当前考试不在可参与时间内')
    return
  }
  
  try {
    // 获取考试信息
    const examResponse = await request.get(`/api/exam/info?examId=${exam.examId}`)
    if (examResponse.data) {
      currentExam.value = examResponse.data
    }
    
    // 获取考试题目
    const questionResponse = await request.get(
      `/api/examQuestion/selectQuestionsByExamId?examId=${exam.examId}`
    )
    
    if (questionResponse.data && Array.isArray(questionResponse.data)) {
      questions.value = questionResponse.data
      
      // 初始化答案数组
      const initialAnswers = {}
      questions.value.forEach(question => {
        initialAnswers[question.questionId] = ''
      })
      answers.value = initialAnswers
      
      // 重置考试状态
      currentQuestionIndex.value = 0
      examSubmitted.value = false
      finalScore.value = 0
      
      // 根据考试实际结束时间计算剩余秒数
      const endTime = new Date(exam.endTime || currentExam.value.endTime)
      const now = new Date()
      const computedRemaining = Math.max(0, Math.floor((endTime.getTime() - now.getTime()) / 1000))
      remainingTime.value = computedRemaining
      
      // 启动倒计时
      startTimer()
      
      // 显示考试对话框
      examDialogVisible.value = true
    } else {
      throw new Error('未获取到考试题目')
    }
  } catch (error) {
    ElMessage.error('进入考试失败: ' + (error.response?.data?.message || error.message))
    console.error('进入考试错误:', error)
  }
}

// 启动倒计时
const startTimer = () => {
  if (timerInterval) clearInterval(timerInterval)
  
  timerInterval = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      // 时间到自动提交试卷
      clearInterval(timerInterval)
      submitExam()
    }
  }, 1000)
}

// 下一题
const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
  }
}

// 上一题
const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// 提交试卷
const submitExam = () => {
  ElMessageBox.confirm(
    '确定要提交试卷吗？提交后将无法修改答案。',
    '确认提交',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 计算得分
      let totalScore = 0
      
      questions.value.forEach(question => {
        const userAnswer = answers.value[question.questionId]
        if (userAnswer && userAnswer.toString().toUpperCase() === question.answer.toString().toUpperCase()) {
          totalScore += question.score
        }
      })
      
      finalScore.value = totalScore
      
      // 更新学生成绩
      const studentId = getCurrentStudentId()
      if (!studentId) {
        ElMessage.error('无法获取学生信息，请重新登录')
        return
      }
      
      const updateResponse = await request.patch(
        '/api/examStudent/updateScore',
        {
          studentId: studentId,
          examId: currentExam.value.examId,
          score: totalScore
        }
      )
      
      if (updateResponse.data) {
        examSubmitted.value = true
        ElMessage.success(`试卷提交成功！您的得分: ${totalScore}`)
        
        // 刷新已选课程列表以更新成绩
        fetchSelectedExams()
      } else {
        throw new Error('更新成绩失败')
      }
    } catch (error) {
      ElMessage.error('提交试卷失败: ' + (error.response?.data?.message || error.message))
      console.error('提交试卷错误:', error)
    }
  }).catch(() => {
    // 用户取消提交
  })
}

// 关闭考试对话框
const closeExamDialog = () => {
  examDialogVisible.value = false
  if (timerInterval) {
    clearInterval(timerInterval)
  }
}

// 判断考试是否可参与
const isExamAvailable = (exam) => {
  const now = new Date()
  const startTime = new Date(exam.startTime)
  const endTime = new Date(exam.endTime)
  return now >= startTime && now <= endTime && exam.status === 'published'
}

// 获取考试状态类型
const getExamStatusType = (status) => {
  const statusMap = {
    'draft': 'info',
    'published': 'success',
    'closed': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取考试状态文本
const getExamStatusText = (status) => {
  const statusMap = {
    'draft': '草稿',
    'published': '已发布',
    'closed': '已关闭'
  }
  return statusMap[status] || status
}

// 格式化时间显示
const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  if (hours > 0) {
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 选项卡切换
const handleTabChange = (tabName) => {
  // 可以在这里添加切换时的逻辑
}

// 分页相关方法 - 已选课程
const handleSelectedSizeChange = (val) => {
  selectedPageSize.value = val
  selectedCurrentPage.value = 1
}

const handleSelectedCurrentChange = (val) => {
  selectedCurrentPage.value = val
}

// 分页相关方法 - 所有考试
const handleAllSizeChange = (val) => {
  allPageSize.value = val
  allCurrentPage.value = 1
}

const handleAllCurrentChange = (val) => {
  allCurrentPage.value = val
}

// 初始化数据
onMounted(() => {
  fetchSelectedExams()
  fetchAllExams()
})

// 组件销毁前清除定时器
onBeforeUnmount(() => {
  if (timerInterval) {
    clearInterval(timerInterval)
  }
})
</script>

<style scoped>
.exam-list {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}

.exam-container {
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
}

.exam-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.timer {
  font-weight: bold;
  color: #f56c6c;
  min-width: 120px;
  text-align: center;
}

.question-card {
  flex: 1;
  margin-bottom: 10px; /* 从20px减少到10px */
  overflow-y: auto;
}

.question-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.question-number {
  font-weight: bold;
  font-size: 16px;
}

.question-score {
  color: #67c23a;
}

.question-content {
  font-size: 18px;
  margin-bottom: 20px;
  line-height: 1.6;
}

.options {
  margin-top: 15px;
}

.option-item {
  display: block;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.option-item:hover {
  background-color: #f5f7fa;
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 10px; /* 从20px减少到10px */
}

.result-section {
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
</style>