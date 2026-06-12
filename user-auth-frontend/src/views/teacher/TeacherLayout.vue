<template>
  <div class="teacher-layout">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h2>考试系统</h2>
        <p>教师端</p>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/teacher/home">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </el-menu-item>
        
        <el-menu-item index="/teacher/exams">
          <i class="el-icon-document"></i>
          <span>考试管理</span>
        </el-menu-item>
        
        <el-menu-item index="/teacher/scores">
          <i class="el-icon-tickets"></i>
          <span>题目管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumb" :key="item.path">
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar">
                {{ userName?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userName }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item divided command="logout">
                  <i class="el-icon-switch-button"></i>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const userName = ref('')
const userAvatar = ref('')

const activeMenu = computed(() => {
  return route.path
})

const breadcrumb = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched.map(item => ({
    path: item.path,
    meta: item.meta
  }))
})

onMounted(() => {
  try {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    userName.value = currentUser.username || '教师用户'
  } catch {
    userName.value = '教师用户'
  }
  userAvatar.value = ''
})

const handleCommand = async (command) => {
  if (command === 'logout') {
    localStorage.removeItem('currentUser')
    router.push('/login')
  }
}
</script>

<style scoped>
.teacher-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background: linear-gradient(180deg, #304156 0%, #2b3a4e 100%);
  color: white;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
  color: white;
}

.logo p {
  margin: 0;
  font-size: 12px;
  color: rgba(255,255,255,0.7);
}

.sidebar-menu {
  border-right: none;
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item) {
  color: rgba(255,255,255,0.7);
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-menu-item.is-active) {
  color: white;
  background: rgba(255,255,255,0.1);
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background: white;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  flex: 1;
}

.header-right {
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-name {
  margin: 0 10px;
  color: #303133;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f0f2f5;
}
</style>