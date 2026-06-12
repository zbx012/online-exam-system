<!-- src/components/Home.vue -->
<template>
  <div class="user-home" :class="userType + '-home'">
    <h1>{{ getTitleByType(userType) }}</h1>
    
    <el-card class="user-info-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-form 
        :model="userInfo" 
        :rules="rules" 
        ref="formRef" 
        label-width="120px"
        v-loading="loading"
        class="user-info-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户ID" prop="id">
              <el-input v-model="userInfo.id" disabled></el-input>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userInfo.username" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userInfo.email"></el-input>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="用户类型" prop="type">
              <el-tag :type="getTagTypeByUserType(userInfo.type)">{{ userInfo.type }}</el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="userInfo.password" disabled type="password"></el-input>
        </el-form-item>
        
        <!-- 添加修改密码的表单项 -->
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码"></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="updateUserInfo" :disabled="!isModified">保存修改</el-button>
          <el-button type="warning" @click="updatePassword" :disabled="!isPasswordValid" style="margin-left: 10px;">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 定义 props
const props = defineProps({
  userType: {
    type: String,
    default: 'user'
  }
})

// 定义响应式数据
const userInfo = ref({
  id: null,
  username: '',
  password: '',
  email: '',
  type: props.userType
})

// 添加密码表单数据
const passwordForm = ref({
  newPassword: '',
  confirmPassword: ''
})

const originalUserInfo = ref({})
const loading = ref(false)
const formRef = ref(null)

// 检查信息是否有修改
const isModified = ref(false)

// 计算属性：判断密码是否有效
const isPasswordValid = computed(() => {
  return passwordForm.value.newPassword && 
         passwordForm.value.confirmPassword && 
         passwordForm.value.newPassword === passwordForm.value.confirmPassword &&
         passwordForm.value.newPassword.length >= 6
})

watch(userInfo, (newVal) => {
  isModified.value = JSON.stringify(newVal) !== JSON.stringify(originalUserInfo.value)
}, { deep: true })

// 监听新密码变化，触发确认密码验证
watch(() => passwordForm.value.newPassword, () => {
  if (passwordForm.value.confirmPassword) {
    formRef.value?.validateField('confirmPassword')
  }
})

// 监听确认密码变化，触发自身验证
watch(() => passwordForm.value.confirmPassword, () => {
  formRef.value?.validateField('confirmPassword')
})

// 根据用户类型获取标题
const getTitleByType = (type) => {
  const titles = {
    'admin': '管理员主页',
    'teacher': '教师主页',
    'student': '学生主页'
  }
  return titles[type] || '用户主页'
}

// 根据用户类型获取标签类型
const getTagTypeByUserType = (type) => {
  const tagTypes = {
    'admin': 'danger',
    'teacher': 'warning',
    'student': 'success'
  }
  return tagTypes[type] || 'info'
}

// 表单验证规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  newPassword: [
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 验证确认密码
function validateConfirmPassword(rule, value, callback) {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 获取当前登录用户信息
const fetchUserInfo = async () => {
  loading.value = true
  try {
    // 从本地存储获取当前登录用户信息
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    const currentUsername = currentUser.username || localStorage.getItem('username')
    
    if (!currentUsername) {
      ElMessage.error('无法获取当前用户信息，请重新登录')
      return
    }
    
    const response = await axios.get(`http://localhost:8080/api/auth/info?userName=${currentUsername}`)
    
    if (response.data) {
      userInfo.value = {
        ...userInfo.value,
        ...response.data
      }
      // 保存原始信息用于比较
      originalUserInfo.value = { ...userInfo.value }
      isModified.value = false
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败: ' + (error.response?.data?.message || error.message))
    console.error('获取用户信息错误:', error)
  } finally {
    loading.value = false
  }
}

// 更新用户信息
const updateUserInfo = () => {
  if (!formRef.value) return
  
  formRef.value.validateField(['email'], async (valid) => {
    if (valid) {
      try {
        loading.value = true
        
        const response = await axios.patch('http://localhost:8080/api/auth/update', {
          username: userInfo.value.username,
          email: userInfo.value.email
        })
        
        if (response.data) {
          ElMessage.success('信息更新成功')
          // 更新原始信息
          originalUserInfo.value = { ...userInfo.value }
          isModified.value = false
          
          // 更新本地存储中的用户信息
          const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
          if (currentUser.username) {
            currentUser.email = userInfo.value.email
            localStorage.setItem('currentUser', JSON.stringify(currentUser))
          }
        }
      } catch (error) {
        const errorMessage = error.response?.data?.message || error.message || '更新失败'
        ElMessage.error('更新失败: ' + errorMessage)
        console.error('更新用户信息错误:', error)
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请检查表单填写是否正确')
    }
  })
}

// 修改密码功能
const updatePassword = async () => {
  // 直接使用计算属性检查密码有效性
  if (!isPasswordValid.value) {
    ElMessage.warning('请确保密码填写正确且一致')
    return
  }

  try {
    loading.value = true
    
    const response = await axios.patch('http://localhost:8080/api/auth/update', {
      username: userInfo.value.username,
      password: passwordForm.value.newPassword
    })
    
    if (response.data) {
      ElMessage.success('密码修改成功')
      // 清空密码输入框
      passwordForm.value.newPassword = ''
      passwordForm.value.confirmPassword = ''
      // 清除验证状态
      formRef.value?.clearValidate(['newPassword', 'confirmPassword'])
    }
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || '修改失败'
    ElMessage.error('密码修改失败: ' + errorMessage)
    console.error('修改密码错误:', error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取用户信息
onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.user-home {
  padding: 20px;
}

.user-info-card {
  max-width: 800px;
  margin-top: 20px;
}

.card-header {
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info-form {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>