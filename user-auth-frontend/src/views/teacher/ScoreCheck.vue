<template>
  <div class="score-check">
    <h1>题目管理</h1>
    
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题目列表</span>
          <div>
            <el-button type="primary" @click="showAddQuestionDialog">添加题目</el-button>
            <el-button @click="fetchQuestions">刷新</el-button>
          </div>
        </div>
      </template>
      
      <!-- 题目表格 -->
      <el-table 
        :data="questionsPage" 
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="questionId" label="题目ID" width="80"></el-table-column>
        <el-table-column prop="questionContent" label="题目内容" min-width="200"></el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'CQ' ? 'primary' : 'success'">
              {{ scope.row.type === 'CQ' ? '选择题' : '填空题' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="80"></el-table-column>
        <el-table-column label="选项/答案" min-width="200">
          <template #default="scope">
            <div v-if="scope.row.type === 'CQ'">
              <div>A: {{ scope.row.optionA }}</div>
              <div>B: {{ scope.row.optionB }}</div>
              <div>C: {{ scope.row.optionC }}</div>
              <div>D: {{ scope.row.optionD }}</div>
              <div><strong>答案: {{ scope.row.answer }}</strong></div>
            </div>
            <div v-else>
              <strong>答案: {{ scope.row.answer }}</strong>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" @click="editQuestion(scope.row)">编辑</el-button>
            <el-button size="small" @click="showAddToExamDialog(scope.row)">添加到考试</el-button>
            <el-button size="small" type="danger" @click="deleteQuestion(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="questions.length"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>
    
    <!-- 添加/编辑题目对话框 -->
    <el-dialog 
      v-model="questionDialogVisible" 
      :title="editingQuestion ? '编辑题目' : '添加题目'" 
      width="600px"
    >
      <el-form 
        :model="questionForm" 
        :rules="questionRules" 
        ref="questionFormRef" 
        label-width="100px"
      >
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="questionForm.type" placeholder="请选择题目类型" @change="onTypeChange">
            <el-option label="选择题" value="CQ"></el-option>
            <el-option label="填空题" value="FB"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="题目内容" prop="questionContent">
          <el-input 
            v-model="questionForm.questionContent" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入题目内容"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="分数" prop="score">
          <el-input-number v-model="questionForm.score" :min="1" :max="100"></el-input-number>
        </el-form-item>
        
        <template v-if="questionForm.type === 'CQ'">
          <el-form-item label="选项A" prop="optionA">
            <el-input v-model="questionForm.optionA" placeholder="请输入选项A内容"></el-input>
          </el-form-item>
          
          <el-form-item label="选项B" prop="optionB">
            <el-input v-model="questionForm.optionB" placeholder="请输入选项B内容"></el-input>
          </el-form-item>
          
          <el-form-item label="选项C" prop="optionC">
            <el-input v-model="questionForm.optionC" placeholder="请输入选项C内容"></el-input>
          </el-form-item>
          
          <el-form-item label="选项D" prop="optionD">
            <el-input v-model="questionForm.optionD" placeholder="请输入选项D内容"></el-input>
          </el-form-item>
          
          <el-form-item label="答案" prop="answer">
            <el-select v-model="questionForm.answer" placeholder="请选择正确答案">
              <el-option label="A" value="A"></el-option>
              <el-option label="B" value="B"></el-option>
              <el-option label="C" value="C"></el-option>
              <el-option label="D" value="D"></el-option>
            </el-select>
          </el-form-item>
        </template>
        
        <template v-else-if="questionForm.type === 'FB'">
          <el-form-item label="答案" prop="answer">
            <el-input v-model="questionForm.answer" placeholder="请输入正确答案"></el-input>
          </el-form-item>
        </template>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="questionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveQuestion">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 添加到考试对话框 -->
    <el-dialog 
      v-model="addToExamDialogVisible" 
      title="添加到考试" 
      width="500px"
    >
      <el-form 
        :model="addToExamForm" 
        :rules="addToExamRules" 
        ref="addToExamFormRef" 
        label-width="100px"
      >
        <el-form-item label="选择考试" prop="examId">
          <el-select 
            v-model="addToExamForm.examId" 
            placeholder="请选择考试" 
            style="width: 100%"
          >
            <el-option
              v-for="exam in teacherExams"
              :key="exam.examId"
              :label="exam.examName"
              :value="exam.examId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="题目顺序" prop="orderNum">
          <el-input-number 
            v-model="addToExamForm.orderNum" 
            :min="1" 
            :max="100" 
            placeholder="请输入题目顺序"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addToExamDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addToExam">添加</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 数据
const questions = ref([])
const loading = ref(false)
const teacherExams = ref([]) // 教师的考试列表

// 分页
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框
const questionDialogVisible = ref(false)
const addToExamDialogVisible = ref(false)
const editingQuestion = ref(null)

// 表单引用
const questionFormRef = ref(null)
const addToExamFormRef = ref(null)

// 题目表单
const questionForm = ref({
  questionId: null,
  questionContent: '',
  type: 'CQ',
  score: 1,
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  answer: ''
})

// 添加到考试表单
const addToExamForm = ref({
  questionId: null,
  examId: null,
  orderNum: 1
})

// 表单验证规则
const questionRules = {
  questionContent: [
    { required: true, message: '请输入题目内容', trigger: 'blur' }
  ],
  score: [
    { required: true, message: '请输入分数', trigger: 'blur' }
  ],
  optionA: [
    { required: true, message: '请输入选项A', trigger: 'blur' }
  ],
  optionB: [
    { required: true, message: '请输入选项B', trigger: 'blur' }
  ],
  optionC: [
    { required: true, message: '请输入选项C', trigger: 'blur' }
  ],
  optionD: [
    { required: true, message: '请输入选项D', trigger: 'blur' }
  ],
  answer: [
    { required: true, message: '请选择/输入答案', trigger: 'blur' }
  ]
}

const addToExamRules = {
  examId: [
    { required: true, message: '请选择考试', trigger: 'change' }
  ],
  orderNum: [
    { required: true, message: '请输入题目顺序', trigger: 'blur' }
  ]
}

// 计算属性 - 分页数据
const questionsPage = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return questions.value.slice(start, end)
})

// 获取当前教师ID
const getCurrentTeacherId = () => {
  try {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    return currentUser.id || null
  } catch (error) {
    console.error('获取教师ID失败:', error)
    return null
  }
}

// 获取所有题目
const fetchQuestions = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/question/selectAll')
    
    if (response.data && Array.isArray(response.data)) {
      questions.value = response.data
      // 重置分页
      currentPage.value = 1
    }
  } catch (error) {
    ElMessage.error('获取题目列表失败: ' + (error.response?.data?.message || error.message))
    console.error('获取题目列表错误:', error)
  } finally {
    loading.value = false
  }
}

// 获取教师的考试列表
const fetchTeacherExams = async () => {
  const teacherId = getCurrentTeacherId()
  if (!teacherId) {
    return
  }
  
  try {
    const response = await axios.get('http://localhost:8080/api/exam/selectByTeacherId', {
      params: {
        teacherId: teacherId
      }
    })
    
    if (response.data && Array.isArray(response.data)) {
      teacherExams.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取考试列表失败: ' + (error.response?.data?.message || error.message))
    console.error('获取考试列表错误:', error)
  }
}

// 获取下一个题目ID
const getNextQuestionId = () => {
  if (questions.value.length === 0) {
    return 1
  }
  const maxId = Math.max(...questions.value.map(q => q.questionId))
  return maxId + 1
}

// 显示添加题目对话框
const showAddQuestionDialog = () => {
  editingQuestion.value = null
  questionForm.value = {
    questionId: getNextQuestionId(),
    questionContent: '',
    type: 'CQ',
    score: 1,
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    answer: ''
  }
  questionDialogVisible.value = true
}

// 显示添加到考试对话框
const showAddToExamDialog = (question) => {
  addToExamForm.value = {
    questionId: question.questionId,
    examId: null,
    orderNum: 1
  }
  // 获取教师考试列表
  fetchTeacherExams()
  addToExamDialogVisible.value = true
}

// 添加题目到考试
const addToExam = () => {
  addToExamFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await axios.post('http://localhost:8080/api/examQuestion/insert', null, {
          params: {
            examId: addToExamForm.value.examId,
            questionId: addToExamForm.value.questionId,
            orderNum: addToExamForm.value.orderNum
          }
        })
        
        if (response.data) {
          ElMessage.success('题目添加到考试成功')
          addToExamDialogVisible.value = false
        }
      } catch (error) {
        ElMessage.error('添加题目到考试失败: ' + (error.response?.data?.message || error.message))
        console.error('添加题目到考试错误:', error)
      }
    }
  })
}

// 编辑题目
const editQuestion = (question) => {
  editingQuestion.value = question
  questionForm.value = { ...question }
  questionDialogVisible.value = true
}

// 保存题目（添加或编辑）
const saveQuestion = () => {
  questionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        const formData = { ...questionForm.value }
        
        if (editingQuestion.value) {
          // 编辑题目
          if (formData.type === 'CQ') {
            // 编辑选择题 - 只发送需要更新的字段
            const updateData = {
              questionId: formData.questionId
            }
            if (formData.optionA || formData.optionB || formData.optionC || formData.optionD) {
              Object.assign(updateData, {
                optionA: formData.optionA,
                optionB: formData.optionB,
                optionC: formData.optionC,
                optionD: formData.optionD
              })
            }
            if (formData.score) updateData.score = formData.score
            if (formData.answer) updateData.answer = formData.answer
            
            response = await axios.patch('http://localhost:8080/api/question/updateChoice', updateData)
          } else {
            // 编辑填空题
            const updateData = {
              questionId: formData.questionId,
              score: formData.score,
              answer: formData.answer
            }
            response = await axios.patch('http://localhost:8080/api/question/updateFill', updateData)
          }
        } else {
          // 添加题目
          if (formData.type === 'CQ') {
            // 添加选择题
            response = await axios.post('http://localhost:8080/api/question/addChoice', formData)
          } else {
            // 添加填空题
            response = await axios.post('http://localhost:8080/api/question/addFill', formData)
          }
        }
        
        if (response.data) {
          ElMessage.success(editingQuestion.value ? '题目更新成功' : '题目添加成功')
          questionDialogVisible.value = false
          fetchQuestions() // 刷新列表
        }
      } catch (error) {
        ElMessage.error((editingQuestion.value ? '更新' : '添加') + '题目失败: ' + (error.response?.data?.message || error.message))
        console.error((editingQuestion.value ? '更新' : '添加') + '题目错误:', error)
      }
    }
  })
}

// 删除题目
const deleteQuestion = (question) => {
  ElMessageBox.confirm(
    `确定要删除题目 "${question.questionContent}" 吗？此操作不可恢复！`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      let response
      if (question.type === 'CQ') {
        // 删除选择题
        response = await axios.delete('http://localhost:8080/api/question/deleteChoice', {
          params: { questionId: question.questionId }
        })
      } else {
        // 删除填空题
        response = await axios.delete('http://localhost:8080/api/question/deleteFill', {
          params: { questionId: question.questionId }
        })
      }
      
      if (response.data) {
        ElMessage.success('题目删除成功')
        fetchQuestions() // 刷新列表
      }
    } catch (error) {
      ElMessage.error('删除题目失败: ' + (error.response?.data?.message || error.message))
      console.error('删除题目错误:', error)
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 题目类型改变时重置答案
const onTypeChange = (value) => {
  questionForm.value.answer = ''
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 初始化数据
onMounted(() => {
  fetchQuestions()
})
</script>

<style scoped>
.score-check {
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
</style>