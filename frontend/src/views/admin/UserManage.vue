<!-- d:\code\J\user-auth-frontend\src\views\admin\UserManage.vue -->
<template>
  <div class="teacher-manage">
    <h1>人员管理</h1>
    
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="fetchAllUsers" :loading="loading">刷新</el-button>
        </div>
      </template>
      
      <!-- 用户表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="用户ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="password" label="密码" width="180"></el-table-column>
        <el-table-column prop="type" label="用户类型" width="100">
          <template #default="scope">
            <el-tag :type="getUserTypeTag(scope.row.type)">
              {{ getUserTypeName(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="resetPassword(scope.row)">重置密码</el-button>
            <el-button size="small" @click="showChangePasswordDialog(scope.row)">修改密码</el-button>
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
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="changePasswordDialogVisible" title="修改密码" width="400px">
      <el-form 
        :model="passwordForm" 
        :rules="passwordRules" 
        ref="passwordFormRef"
        label-width="80px"
      >
        <el-form-item label="用户名">
          <el-input v-model="passwordForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password
            placeholder="请输入新密码"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="changePasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="changePassword">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'TeacherManage',
  
  setup() {
    // 表格数据
    const tableData = ref([])
    const allUsers = ref([]) // 存储所有用户数据
    const loading = ref(false)
    
    // 分页相关
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    
    // 修改密码对话框
    const changePasswordDialogVisible = ref(false)
    const passwordForm = ref({
      id: null,
      username: '',
      newPassword: ''
    })
    const passwordFormRef = ref(null)
    
    // 密码表单验证规则
    const passwordRules = {
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ]
    }
    
    // 获取用户类型标签样式
    const getUserTypeTag = (type) => {
      const tagMap = {
        'admin': 'danger',
        'teacher': 'warning',
        'student': 'success'
      }
      return tagMap[type] || 'info'
    }
    
    // 获取用户类型名称
    const getUserTypeName = (type) => {
      const nameMap = {
        'admin': '管理员',
        'teacher': '教师',
        'student': '学生'
      }
      return nameMap[type] || type
    }
    
    // 获取所有用户
    const fetchAllUsers = async () => {
      loading.value = true
      try {
        const response = await request.get('/api/auth/getAllUser')
        
        if (response.data && Array.isArray(response.data)) {
          allUsers.value = response.data
          total.value = response.data.length
          updateTableData() // 更新当前页显示数据
        }
      } catch (error) {
        ElMessage.error('获取用户列表失败: ' + (error.response?.data?.message || error.message))
        console.error('获取用户列表错误:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 根据分页参数更新表格数据
    const updateTableData = () => {
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      tableData.value = allUsers.value.slice(start, end)
    }
    
    // 重置密码为123456
    const resetPassword = (row) => {
      ElMessageBox.confirm(
        `确定要将用户 "${row.username}" 的密码重置为 "123456" 吗？`,
        '确认重置',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(async () => {
        try {
          const response = await request.patch('/api/auth/update', {
            username: row.username,
            password: '123456' //返回123456，让后端加密
          })
          
          if (response.data) {
            ElMessage.success('密码重置成功')
          }
        } catch (error) {
          ElMessage.error('密码重置失败: ' + (error.response?.data?.message || error.message))
          console.error('密码重置错误:', error)
        }
      }).catch(() => {
        // 用户取消操作
      })
    }
    
    // 显示修改密码对话框
    const showChangePasswordDialog = (row) => {
      passwordForm.value = {
        id: row.id,
        username: row.username,
        newPassword: ''
      }
      changePasswordDialogVisible.value = true
    }
    
    // 修改密码
    const changePassword = () => {
      passwordFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const response = await request.patch('/api/auth/update', {
              username: passwordForm.value.username,
              password: passwordForm.value.newPassword
            })
            
            if (response.data) {
              ElMessage.success('密码修改成功')
              changePasswordDialogVisible.value = false
            }
          } catch (error) {
            ElMessage.error('密码修改失败: ' + (error.response?.data?.message || error.message))
            console.error('密码修改错误:', error)
          }
        }
      })
    }
    
    // 分页相关方法
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      updateTableData()
    }
    
    const handleCurrentChange = (val) => {
      currentPage.value = val
      updateTableData()
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      fetchAllUsers()
    })
    
    return {
      tableData,
      loading,
      currentPage,
      pageSize,
      total,
      changePasswordDialogVisible,
      passwordForm,
      passwordFormRef,
      passwordRules,
      getUserTypeTag,
      getUserTypeName,
      fetchAllUsers,
      resetPassword,
      showChangePasswordDialog,
      changePassword,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.teacher-manage {
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