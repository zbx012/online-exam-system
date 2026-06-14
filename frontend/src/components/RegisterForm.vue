<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <el-form 
      ref="registerFormRef" 
      :model="registerForm" 
      :rules="registerRules" 
      label-width="80px"
    >
      <!-- 用户名 -->
      <el-form-item label="用户名" prop="username">
        <el-input 
          v-model="registerForm.username" 
          placeholder="请输入用户名"
          clearable
        />
      </el-form-item>

      <!-- 密码 -->
      <el-form-item label="密码" prop="password">
        <el-input 
          v-model="registerForm.password" 
          type="password" 
          placeholder="请输入密码"
          show-password
          clearable
        />
      </el-form-item>

      <!-- 确认密码 -->
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input 
          v-model="registerForm.confirmPassword" 
          type="password" 
          placeholder="请再次输入密码"
          show-password
          clearable
        />
      </el-form-item>

      <!-- 邮箱 -->
      <el-form-item label="邮箱" prop="email">
        <el-input 
          v-model="registerForm.email" 
          placeholder="请输入邮箱（可选）"
          clearable
        />
      </el-form-item>

      <!-- 用户类型 -->
      <el-form-item label="用户类型" prop="type">
        <el-radio-group v-model="registerForm.type">
          <el-radio label="student">学生</el-radio>
          <el-radio label="teacher">教师</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 按钮 -->
      <el-form-item>
        <el-button 
          type="primary" 
          @click="handleRegister" 
          :loading="loading"
        >
          {{ loading ? '注册中...' : '立即注册' }}
        </el-button>
        <el-button @click="switchToLogin">返回登录</el-button>
      </el-form-item>
    </el-form>

    <!-- 响应消息 -->
    <el-alert
      v-if="message"
      :title="message"
      :type="messageType"
      :closable="false"
      show-icon
      style="margin-top: 20px;"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, computed } from 'vue'
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

export default defineComponent({
  name: 'RegisterForm',
  
  emits: ['switch-to-login'],
  
  setup(props, { emit }) {
    // 表单引用
    const registerFormRef = ref<FormInstance>()

    // 表单数据
    const registerForm = reactive({
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      type:'student'
    })

  
    
    // 加载状态
    const loading = ref(false)
    
    // 消息提示
    const message = ref('')
    const messageType = ref<'success' | 'error' | 'info' | 'warning'>('info')

    // 表单验证规则
    const registerRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请再次输入密码', trigger: 'blur' },
        {
          validator: (rule: any, value: string, callback: any) => {
            if (value !== registerForm.password) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ],
      email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择用户类型', trigger: 'change' }
      ]
    }

    
    

    // 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  // 表单验证
  registerFormRef.value.validate(async (valid: boolean) => {
    if (!valid) {
      ElMessage.error('请填写完整的表单信息')
      return
    }

    // 设置加载状态
    loading.value = true
    message.value = '正在注册，请稍候...'
    messageType.value = 'info'

    try {
      // 1. 构建表单数据
      const formData = new URLSearchParams()
      formData.append('username', registerForm.username)
      formData.append('password', registerForm.password)
      formData.append('type', registerForm.type)
      if (registerForm.email) {
        formData.append('email', registerForm.email)
      }
      
      // 2. 发送注册请求
      console.log('📤 发送注册请求:', {
        username: registerForm.username,
        password: '[HIDDEN]',
        type: registerForm.type,
        email: registerForm.email || '未填写'
      })
      
      const response = await request.post('/api/auth/register', formData)
      
      // 3. 处理响应
      console.log('📥 收到注册响应:', response.data)
      
      if (response.data.includes('成功')) {
        message.value = '注册成功！'
        messageType.value = 'success'
        ElMessage.success('注册成功！')
        
        // 保存注册信息（可选）
        localStorage.setItem('lastRegisteredUser', registerForm.username)
        
        // 清空表单
        registerFormRef.value.resetFields()
        
        // 3秒后自动跳转到登录
        setTimeout(() => {
          ElMessage.info('即将跳转到登录页面')
          emit('switch-to-login')
        }, 3000)
      } else {
        message.value = response.data
        messageType.value = 'error'
        ElMessage.error(response.data)
        
        
      }
      
    } catch (error: any) {
      console.error('❌ 注册请求失败:', error)
      message.value = '注册失败，请检查网络连接'
      messageType.value = 'error'
      
      if (error.response?.data) {
        ElMessage.error(`注册失败: ${error.response.data}`)
      } else if (error.message.includes('Network Error')) {
        ElMessage.error('网络错误，请检查后端服务是否运行')
      } else {
        ElMessage.error('注册失败，请稍后重试')
      }
    } finally {
      loading.value = false
    }
  })
}
      

    // 切换到登录
    const switchToLogin = () => {
      emit('switch-to-login')
    }

    return {
      registerFormRef,
      registerForm,
      registerRules,
      loading,
      message,
      messageType,
      handleRegister,
      switchToLogin
    }
  }
})
</script>

<style scoped>
.register-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 30px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  background-color: white;
}

.register-container h2 {
  text-align: center;
  color: #409eff;
  margin-bottom: 30px;
  font-size: 24px;
}

.check-status {
  font-size: 12px;
  margin-top: 5px;
  padding: 3px 8px;
  border-radius: 4px;
  display: inline-block;
}

.check-status.checking {
  color: #e6a23c;
  background-color: #fdf6ec;
  border: 1px solid #faecd8;
}

.check-status.exists {
  color: #f56c6c;
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
}

.check-status.available {
  color: #67c23a;
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
}

/* 调整Element Plus默认样式 */
:deep(.el-form-item__label) {
  font-weight: 600;
}

:deep(.el-button) {
  width: 120px;
}
</style>