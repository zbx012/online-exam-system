<template>
  <div class="system-setting">
    <h1>系统设置</h1>

    <!-- 系统信息卡片 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>数据库状态</span>
              <el-tag :type="dbConnected ? 'success' : 'danger'">
                {{ dbConnected ? '已连接' : '未连接' }}
              </el-tag>
            </div>
          </template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="数据库类型">MySQL</el-descriptions-item>
            <el-descriptions-item label="数据库名称">online_exam</el-descriptions-item>
            <el-descriptions-item label="连接状态">
              <el-icon v-if="dbConnected" color="#67c23a"><CircleCheck /></el-icon>
              <el-icon v-else color="#f56c6c"><CircleClose /></el-icon>
            </el-descriptions-item>
          </el-descriptions>
          <div style="margin-top: 12px">
            <el-button type="primary" size="small" @click="checkDbStatus" :loading="dbLoading">
              检测连接
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>用户统计</span>
            </div>
          </template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="用户总数">{{ userStats.total }}</el-descriptions-item>
            <el-descriptions-item label="管理员">{{ userStats.admin }}</el-descriptions-item>
            <el-descriptions-item label="教师">{{ userStats.teacher }}</el-descriptions-item>
            <el-descriptions-item label="学生">{{ userStats.student }}</el-descriptions-item>
          </el-descriptions>
          <div style="margin-top: 12px">
            <el-button type="primary" size="small" @click="fetchUserStats" :loading="statsLoading">
              刷新统计
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="后端框架">Spring Boot 2.5</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="Java 版本">JDK 1.8</el-descriptions-item>
            <el-descriptions-item label="服务端口">8080</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统公告管理 -->
    <el-card style="margin-top: 20px" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>系统公告</span>
          <el-button type="primary" size="small" @click="addNotice">发布公告</el-button>
        </div>
      </template>

      <el-table :data="notices" style="width: 100%" stripe empty-text="暂无公告">
        <el-table-column prop="title" label="标题" min-width="200"></el-table-column>
        <el-table-column prop="content" label="内容" min-width="300"></el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" type="danger" @click="deleteNotice(scope.$index)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发布公告对话框 -->
    <el-dialog v-model="noticeDialogVisible" title="发布系统公告" width="500px">
      <el-form :model="noticeForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="noticeForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入公告内容"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="noticeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddNotice">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 数据库状态
const dbConnected = ref(false)
const dbLoading = ref(false)

// 用户统计
const statsLoading = ref(false)
const userStats = reactive({
  total: 0,
  admin: 0,
  teacher: 0,
  student: 0
})

// 公告管理
const notices = ref([])
const noticeDialogVisible = ref(false)
const noticeForm = reactive({
  title: '',
  content: ''
})

// 检测数据库连接
const checkDbStatus = async () => {
  dbLoading.value = true
  try {
    // 通过尝试获取用户列表来验证数据库连接
    const response = await request.get('/api/auth/getAllUser')
    dbConnected.value = response.data !== null
    ElMessage.success('数据库连接正常')
  } catch {
    dbConnected.value = false
    ElMessage.error('数据库连接失败')
  } finally {
    dbLoading.value = false
  }
}

// 获取用户统计
const fetchUserStats = async () => {
  statsLoading.value = true
  try {
    const response = await request.get('/api/auth/getAllUser')
    if (response.data) {
      const users = response.data
      userStats.total = users.length
      userStats.admin = users.filter(u => u.type === 'admin').length
      userStats.teacher = users.filter(u => u.type === 'teacher').length
      userStats.student = users.filter(u => u.type === 'student').length
    }
  } catch {
    ElMessage.error('获取用户统计失败')
  } finally {
    statsLoading.value = false
  }
}

// 发布公告
const addNotice = () => {
  noticeForm.title = ''
  noticeForm.content = ''
  noticeDialogVisible.value = true
}

const confirmAddNotice = () => {
  if (!noticeForm.title.trim()) {
    ElMessage.warning('请输入公告标题')
    return
  }
  notices.value.unshift({
    title: noticeForm.title,
    content: noticeForm.content,
    createTime: new Date().toLocaleString('zh-CN')
  })
  noticeDialogVisible.value = false
  ElMessage.success('公告发布成功')
}

const deleteNotice = (index) => {
  notices.value.splice(index, 1)
  ElMessage.success('公告已删除')
}

onMounted(() => {
  checkDbStatus()
  fetchUserStats()
})
</script>

<style scoped>
.system-setting {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
