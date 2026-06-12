<template>
   <!-- html写在template标签里 -->
  <div class="login-container">
    <h2>用户登录</h2>
  <!--el-form指element ui的表单组件-->
    <el-form :model="form" label-width="80px">
      
      <el-form-item label="用户名">
        <el-input 
          v-model="form.username" 
          placeholder="请输入用户名"
          :disabled="loading"
        />
      </el-form-item>
      
      <el-form-item label="密码">
        <el-input 
          v-model="form.password" 
          type="password" 
          placeholder="请输入密码"
          show-password
          :disabled="loading"
        />
      </el-form-item>
      
      <el-form-item>
        <el-button 
          type="primary" 
          @click="handleLogin" 
          :loading="loading"
          :disabled="!form.username || !form.password"
        >
          {{ loading ? '登录中...' : '登录' }}
        </el-button>
        <el-button @click="switchToRegister" :disabled="loading">
          去注册
        </el-button>
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
import { defineComponent, ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'  // 导入axios实例

export default defineComponent({
  name: 'LoginForm',
  
  emits: ['switch-to-register', 'login-success'],
  
  setup(props, { emit }) {
    // 表单数据
    const form = reactive({
      username: '',
      password: ''
    })

    // 状态
    const loading = ref(false)
    const message = ref('')
    const messageType = ref<'success' | 'error' | 'info' | 'warning'>('info')

    // 登录处理
    const handleLogin = async () => {
      if (!form.username || !form.password) {
        ElMessage.error('请输入用户名和密码')
        return
      }
      
      // 设置加载状态
      loading.value = true
      message.value = '正在登录，请稍候...'
      messageType.value = 'info'
      
      try {
        // 1. 构建表单数据（重要：与后端@RequestParam匹配）
        const formData = new URLSearchParams()
        formData.append('username', form.username)
        formData.append('password', form.password)
        
        // 2. 发送登录请求
        console.log('📤 发送登录请求:', {
          username: form.username,
          password: '[HIDDEN]'  // 安全考虑，不打印真实密码
        })
        
        const response = await request.post('/api/auth/login', formData)
        
        // 3. 处理响应
        console.log('📥 收到登录响应:', response.data)
        
        if (response.data.success) {
          message.value = response.data.message
          messageType.value = 'success'
          ElMessage.success(response.data.message)
          
          // 保存 token 和用户信息到本地存储
          localStorage.setItem('token', response.data.token)
          localStorage.setItem('currentUser', JSON.stringify(response.data.user))
          localStorage.setItem('email', response.data.user.email)
          // 清空表单
          form.username = ''
          form.password = ''
          emit('login-success')
      } else {
      message.value = response.data
      messageType.value = 'error'
      ElMessage.error(response.data)
}
        
      } catch (error: any) {
        console.error('❌ 登录请求失败:', error)
        message.value = '登录失败，请检查网络连接'
        messageType.value = 'error'
        
        // 显示具体错误信息
        if (error.response?.data) {
          ElMessage.error(`登录失败: ${error.response.data}`)
        } else if (error.message.includes('Network Error')) {
          ElMessage.error('网络错误，请检查后端服务是否运行')
        } else if (error.message.includes('timeout')) {
          ElMessage.error('请求超时，请检查网络')
        } else {
          ElMessage.error('登录失败，请稍后重试')
        }
      } finally {
        loading.value = false
      }
    }

    // 切换到注册页面
    const switchToRegister = () => {
      emit('switch-to-register')
    }

    return {
      form,
      loading,
      message,
      messageType,
      handleLogin,
      switchToRegister
    }
  }
})
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 30px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  background-color: white;
}

.login-container h2 {
  text-align: center;
  color: #409eff;
  margin-bottom: 30px;
  font-size: 24px;
}
</style>