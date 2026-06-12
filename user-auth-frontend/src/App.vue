<template>
  <div id="app">

    <!--header标签在上面-->
    <header>
      <h1>在线考试系统</h1>
      <p>Spring Boot + Vue 全栈项目</p>
    </header>
    
    <!--main标签在中间，占比最多-->
    <main>
      <div class="container">


      <div v-if="isLoggedIn">
          <router-view />
          <!-- 分割线 -->
       
          <div style="padding:20px">
          <h3>测试图标组件</h3>
           <div style="margin: 10px 0">
              社区图标:<IconCommunity/>
           </div>
           <div style="margin: 10px 0">
              工具图标:<IconTooling/>
           </div>
           <div style="margin: 10px 0">
              支持图标:<IconSupport/>
           </div>
           <div style="margin: 10px 0">
              生态图标:<IconEcosystem/>
           </div>
           <div style="margin: 10px 0">
              文档图标:<IconDocumentation/>
           </div>
        </div>
      </div>

    <div v-else>
        <div class="form-section">
          <!--登录表单组件，显示条件，自定义事件(子文件中emit这个)-->
          <LoginForm v-if="activeForm === 'login'"
            @switch-to-register="switchToRegister"
            @login-success="handleLoginSuccess"
          />
          <!--注册表单组件，显示条件，自定义事件-->
          <RegisterForm v-if="activeForm === 'register'"
           @switch-to-login="switchToLogin"
          />
        </div>
      
        

        </div>
      </div>
    </main>
    
    <!--footer标签在最下面-->
    <footer>
      <p>
        <span v-if="activeForm === 'login'">还没有账号？</span>
        <span v-else>已有账号？</span>
        <el-button 
          type="text" 
          @click="toggleForm"
          style="margin-left: 10px; padding: 0;"
        >
          {{ activeForm === 'login' ? '立即注册' : '立即登录' }}
        </el-button>
      </p>
      <p class="footer-info">当前状态：{{ activeForm === 'login' ? '登录模式' : '注册模式' }}</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router' 
import LoginForm from './components/LoginForm.vue'
import RegisterForm from './components/RegisterForm.vue'
import IconCommunity from './components/icons/IconCommunity.vue'
import IconDocumentation from './components/icons/IconDocumentation.vue'
import IconEcosystem from './components/icons/IconEcosystem.vue'
import IconTooling from './components/icons/IconTooling.vue'
import IconSupport from './components/icons/IconSupport.vue'



//ref是Vue的响应api，声明一个响应式变量(其值改变会被vue监听，自动重新渲染依赖这个变量的模板），其值只能是其中一个，默认是login
const activeForm = ref<'login' | 'register'>('login')
const router= useRouter() 
const isLoggedIn = ref(false)
const userEmail = ref('')
// 在 handleLogin 成功后设置这些变量
const handleLoginSuccess = () => {
  isLoggedIn.value = true
  userEmail.value = localStorage.getItem('email') || ''
   const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
   if (currentUser.type === 'teacher') {
    router.push('/teacher/home')
  } else if(currentUser.type === 'student'){
    router.push('/student/home')
  }else if(currentUser.type === 'admin'){
    router.push('/admin/home')
}else{
  router.push('/')
}
}
// 在 handleLogout 时重置
const handleLogout = () => {
  isLoggedIn.value = false
  userEmail.value = ''
  localStorage.removeItem('userEmail')
  localStorage.removeItem('currentUser')
}
// 是否显示分割线（两个表单都显示时才显示），computed是计算属性。根据窗口宽度判断。
const showDivider = computed(() => {
  return window.innerWidth > 768 // 在桌面端显示分割线
})

//切换到注册表单的函数。
const switchToRegister = () => {
  activeForm.value = 'register'
}

const switchToLogin = () => {
  activeForm.value = 'login'
}

//表单切换的切换器函数，用于来回切换
const toggleForm = () => {
  activeForm.value = activeForm.value === 'login' ? 'register' : 'login'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  text-shadow: 1px 1px 3px rgba(0,0,0,0.2);
}

header p {
  font-size: 1rem;
  opacity: 0.9;
  font-weight: 300;
}

main {
  flex: 1;
  padding: 40px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.container {
  display: flex;
  gap: 60px;
  max-width: 1100px;
  width: 100%;
  justify-content: center;
  align-items: flex-start;
}

.form-section {
  flex: 1;
  max-width: 500px;
  transition: all 0.3s ease;
}

.form-section.active {
  transform: translateY(0);
  opacity: 1;
}

.divider {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  min-width: 100px;
}

.line {
  flex: 1;
  width: 1px;
  background-color: #dcdfe6;
  min-height: 100px;
}

.or {
  padding: 10px;
  color: #909399;
  font-size: 14px;
  background-color: #f5f7fa;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 10px 0;
  border: 1px solid #dcdfe6;
}

footer {
  background-color: #2c3e50;
  color: #bdc3c7;
  padding: 20px;
  text-align: center;
  font-size: 0.9rem;
  border-top: 1px solid rgba(255,255,255,0.1);
}

footer p {
  margin-bottom: 5px;
}

.footer-info {
  font-size: 0.8rem;
  color: #7f8c8d;
  margin-top: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    flex-direction: column;
    gap: 30px;
  }
  
  .form-section {
    max-width: 100%;
  }
  
  .divider {
    display: none;
  }
  
  header h1 {
    font-size: 2rem;
  }
}
</style>