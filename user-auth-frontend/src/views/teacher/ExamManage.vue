<template>
  <div class="exam-manage">
    <h1>考试管理</h1>
    
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考试列表</span>
          <el-button type="primary" @click="showExamRegistration">添加考试</el-button>
        </div>
      </template>
      
      <!-- 考试表格 -->
      <el-table 
        :data="examList" 
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="examId" label="考试ID" width="80"></el-table-column>
        <el-table-column prop="examName" label="考试名称" width="150"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusDisplayName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button size="small" @click="viewExamDetails(scope.row)">查看详情</el-button>
            <el-button 
              size="small" 
              type="primary" 
              @click="editExam(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="warning"
              @click="viewStudents(scope.row)"
            >
              学生
            </el-button>
            <el-button 
              size="small" 
              type="info"
              @click="viewQuestions(scope.row)"
            >
              题目
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteExam(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>
    
    <!-- 考试详情对话框 -->
    <el-dialog v-model="detailDialogVisible" :title="currentExam.examName" width="600px">
      <el-form label-width="100px">
        <el-form-item label="考试ID">
          <span>{{ currentExam.examId }}</span>
        </el-form-item>
        <el-form-item label="考试名称">
          <span>{{ currentExam.examName }}</span>
        </el-form-item>
        <el-form-item label="开始时间">
          <span>{{ currentExam.startTime }}</span>
        </el-form-item>
        <el-form-item label="结束时间">
          <span>{{ currentExam.endTime }}</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="getStatusTagType(currentExam.status)">
            {{ getStatusDisplayName(currentExam.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="教师ID">
          <span>{{ currentExam.teacherId }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 编辑考试对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑考试" width="500px">
      <el-form 
        :model="editForm" 
        :rules="editRules" 
        ref="editFormRef" 
        label-width="100px"
      >
        <el-form-item label="考试ID">
          <el-input v-model="editForm.examId" disabled></el-input>
        </el-form-item>
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="editForm.examName"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="editForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="editForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveExamEdit">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 学生列表对话框 -->
    <el-dialog v-model="studentsDialogVisible" :title="`考试 ${currentExam.examName} 的学生`" width="700px">
      <el-table :data="studentList" v-loading="studentsLoading">
        <el-table-column prop="studentId" label="学生ID" width="100"></el-table-column>
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="scope">
            <el-input 
              v-model="scope.row.score" 
              size="small" 
              @change="updateStudentScore(scope.row)"
              style="width: 80px"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button 
              size="small" 
              type="primary" 
              @click="updateStudentScore(scope.row)"
            >
              保存
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="studentsDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 题目列表对话框 -->
    <el-dialog v-model="questionsDialogVisible" :title="`考试 ${currentExam.examName} 的题目`" width="800px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>{{ `考试 ${currentExam.examName} 的题目` }}</span>
          <el-button type="primary" size="small" @click="showAddQuestionDialog">添加题目</el-button>
        </div>
      </template>
      
      <el-table :data="questionList" v-loading="questionsLoading">
        <el-table-column prop="questionId" label="题目ID" width="80"></el-table-column>
        <el-table-column prop="questionContent" label="题目内容" min-width="200"></el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'CQ' ? 'primary' : 'success'">
              {{ scope.row.type === 'CQ' ? '选择题' : '填空题' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="80"></el-table-column>
        <el-table-column label="选项/答案" min-width="150">
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
      </el-table>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="questionsDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加题目对话框 -->
    <el-dialog 
      v-model="addQuestionDialogVisible" 
      title="添加题目到考试" 
      width="600px"
    >
      <el-form 
        :model="addQuestionForm" 
        label-width="100px"
      >
        <el-form-item label="题目ID">
          <el-input 
            v-model="addQuestionForm.questionId" 
            placeholder="请输入题目ID"
          ></el-input>
        </el-form-item>
        <el-form-item label="题目顺序">
          <el-input-number 
            v-model="addQuestionForm.orderNum" 
            :min="1" 
            :max="100"
          ></el-input-number>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addQuestionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAddQuestion">添加</el-button>
        </span>
      </template>
    </el-dialog>
      
    
    <!-- 考试注册组件 -->
    <ExamRegistration ref="examRegistrationRef" />
  </div>
</template>


<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import ExamRegistration from '@/components/ExamRegistration.vue'

// 响应式数据
const examList = ref([])
const loading = ref(false)
const detailDialogVisible = ref(false)
const editDialogVisible = ref(false)
const studentsDialogVisible = ref(false)
const questionsDialogVisible = ref(false)
const studentsLoading = ref(false)
const questionsLoading = ref(false)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表单引用
const editFormRef = ref(null)
const examRegistrationRef = ref(null)

// 当前选中的考试
const currentExam = ref({
  examId: null,
  examName: '',
  teacherId: null,
  startTime: '',
  endTime: '',
  status: ''
})

// 编辑表单
const editForm = ref({
  examId: null,
  examName: '',
  startTime: '',
  endTime: ''
})

// 编辑表单验证规则
const editRules = {
  examName: [
    { required: true, message: '请输入考试名称', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

// 学生和题目列表
const studentList = ref([])
const questionList = ref([])

// 添加题目相关数据
const addQuestionDialogVisible = ref(false)
const addQuestionForm = ref({
  questionId: '',
  orderNum: 1
})

// 获取当前教师ID（从本地存储中获取）
const getCurrentTeacherId = () => {
  try {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    return currentUser.id || null
  } catch (error) {
    console.error('获取教师ID失败:', error)
    return null
  }
}

// 获取考试列表
const fetchExams = async () => {
  const teacherId = getCurrentTeacherId()
  if (!teacherId) {
    ElMessage.error('无法获取教师信息，请重新登录')
    return
  }
  
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/exam/selectByTeacherId', {
      params: {
        teacherId: teacherId
      }
    })
    
    if (response.data && Array.isArray(response.data)) {
      // 由于后端直接返回所有数据，我们需要在前端实现分页
      const allExams = response.data
      total.value = allExams.length
      
      // 计算当前页应该显示的数据
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      examList.value = allExams.slice(start, end)
    }
  } catch (error) {
    ElMessage.error('获取考试列表失败: ' + (error.response?.data?.message || error.message))
    console.error('获取考试列表错误:', error)
  } finally {
    loading.value = false
  }
}

// 查看考试详情
const viewExamDetails = (exam) => {
  currentExam.value = { ...exam }
  detailDialogVisible.value = true
}

// 显示考试注册
const showExamRegistration = () => {
  examRegistrationRef.value.show()
}

// 编辑考试
const editExam = (exam) => {
  editForm.value = {
    examId: exam.examId,
    examName: exam.examName,
    startTime: exam.startTime,
    endTime: exam.endTime
  }
  editDialogVisible.value = true
}

// 保存考试编辑
const saveExamEdit = () => {
  editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 获取当前教师ID
        const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
        const teacherId = currentUser.id || null
        
        if (!teacherId) {
          ElMessage.error('无法获取教师信息，请重新登录')
          return
        }
        
        const response = await axios.patch('http://localhost:8080/api/exam/update', {
          examId: editForm.value.examId,
          examName: editForm.value.examName,
          startTime: editForm.value.startTime,
          endTime: editForm.value.endTime,
          teacherId: teacherId // 确保传递教师ID
        })
        
        if (response.data) {
          ElMessage.success('考试信息更新成功')
          editDialogVisible.value = false
          fetchExams() // 刷新考试列表
        }
      } catch (error) {
        ElMessage.error('更新失败: ' + (error.response?.data?.message || error.message))
        console.error('更新考试信息错误:', error)
      }
    }
  })
}

// 删除考试
const deleteExam = (exam) => {
  ElMessageBox.confirm(
    `确定要删除考试 "${exam.examName}" 吗？此操作不可恢复！`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await axios.delete('http://localhost:8080/api/exam/delete', {
        params: {
          examId: exam.examId
        }
      })
      
      if (response.data) {
        ElMessage.success('考试删除成功')
        fetchExams() // 刷新考试列表
      }
    } catch (error) {
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message))
      console.error('删除考试错误:', error)
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 查看学生列表
const viewStudents = async (exam) => {
  currentExam.value = { ...exam }
  studentsLoading.value = true
  studentsDialogVisible.value = true
  
  try {
    const response = await axios.get('http://localhost:8080/api/examStudent/selectByExam', {
      params: {
        examId: exam.examId
      }
    })
    
    if (response.data && Array.isArray(response.data)) {
      studentList.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取学生列表失败: ' + (error.response?.data?.message || error.message))
    console.error('获取学生列表错误:', error)
  } finally {
    studentsLoading.value = false
  }
}

// 更新学生成绩
const updateStudentScore = async (student) => {
  try {
    const response = await axios.patch('http://localhost:8080/api/examStudent/updateScore', {
      studentId: student.studentId,
      examId: currentExam.value.examId,
      score: student.score
    })
    
    if (response.data) {
      ElMessage.success('成绩更新成功')
    }
  } catch (error) {
    ElMessage.error('更新成绩失败: ' + (error.response?.data?.message || error.message))
    console.error('更新成绩错误:', error)
  }
}

// 查看题目列表
const viewQuestions = async (exam) => {
  currentExam.value = { ...exam }
  questionsLoading.value = true
  questionsDialogVisible.value = true
  
  try {
    // 获取考试题目
    const response = await axios.get('http://localhost:8080/api/examQuestion/selectQuestionsByExamId', {
      params: {
        examId: exam.examId
      }
    })
    
    if (response.data && Array.isArray(response.data)) {
      questionList.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取题目列表失败: ' + (error.response?.data?.message || error.message))
    console.error('获取题目列表错误:', error)
  } finally {
    questionsLoading.value = false
  }
}

// 显示添加题目对话框
const showAddQuestionDialog = () => {
  // 重置表单
  addQuestionForm.value = {
    questionId: '',
    orderNum: 1
  }
  addQuestionDialogVisible.value = true
}

// 确认添加题目
const confirmAddQuestion = async () => {
  try {
    // 参数校验
    if (!currentExam.value.examId) {
      ElMessage.error('考试ID无效')
      return
    }
    
    if (!addQuestionForm.value.questionId) {
      ElMessage.error('请输入题目ID')
      return
    }

    const response = await axios.post('http://localhost:8080/api/examQuestion/insert', {
      examId: currentExam.value.examId,
      questionId: addQuestionForm.value.questionId,
      orderNum: addQuestionForm.value.orderNum
    })
    
    if (response.data) {
      ElMessage.success('题目添加成功')
      // 关闭对话框
      addQuestionDialogVisible.value = false
      // 重新加载考试题目列表
      await viewQuestions(currentExam.value)
    }
  } catch (error) {
    ElMessage.error('添加题目失败: ' + (error.response?.data?.message || error.message))
    console.error('添加题目错误:', error)
  }
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const tagMap = {
    'published': 'success',
    'draft': 'warning',
    'closed': 'danger'
  }
  return tagMap[status] || 'info'
}

// 获取状态显示名称
const getStatusDisplayName = (status) => {
  const nameMap = {
    'published': '已发布',
    'draft': '草稿',
    'closed': '已关闭'
  }
  return nameMap[status] || status
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchExams()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchExams()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchExams()
})
</script>    

<style scoped>
.exam-manage {
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