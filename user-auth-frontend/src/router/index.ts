import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
//import InfoPage from '@/views/InfoPage.vue'
import StudentHome from '@/views/student/StudentHome.vue'
import StudentLayout from '@/views/student/StudentLayout.vue'
import ExamList from '@/views/student/ExamList.vue'

import TeacherLayout from '@/views/teacher/TeacherLayout.vue'
import TeacherHome from '@/views/teacher/TeacherHome.vue'
import ExamManage from '@/views/teacher/ExamManage.vue'
import ScoreCheck from '@/views/teacher/ScoreCheck.vue'
import AdminHome from '@/views/admin/AdminHome.vue'
import SystemSetting from '@/views/admin/SystemSetting.vue'
import UserManage from '@/views/admin/UserManage.vue'
import AdminLayout from '@/views/admin/AdminLayout.vue'

const routes: Array<RouteRecordRaw> = [
  //{
    //path: '/user/info',
    //name: 'StudentHome',
    //component: StudentHome
  //},
{
    path: '/student',
    component: StudentLayout,
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: StudentHome
      },
       {
        path: 'exams',
        component: () => import('@/views/student/ExamList.vue'),
        meta: { title: '所有考试' }
      },
      /*
      {
        path: 'exam/:examId/take',
        component: () => import('@/views/student/ExamTaking.vue'),
        meta: { title: '参加考试' }
      },
      {
        path: 'record/:recordId/result',
        component: () => import('@/views/student/ExamResult.vue'),
        meta: { title: '考试成绩' }
      },
      {
        path: 'profile',
        component: () => import('@/views/student/Profile.vue'),
        meta: { title: '个人资料' }
      },
     */
    ]
  },
{
    path: '/teacher',
    component: TeacherLayout,
    children: [
      {
        path: 'home',
        name: 'TeacherHome',
        component: TeacherHome,
        meta: { title: '教师首页' }
      },
      {
        path: 'exams',
        name: 'ExamManage',
        component: ExamManage,
        meta: { title: '考试管理' }
      },
      {
        path: 'scores',
        name: 'ScoreCheck',
        component: ScoreCheck,
        meta: { title: '成绩查看' }
      }
    ]
  },

  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: 'home',
        name: 'AdminHome',
        component: AdminHome,
        meta: { title: '管理员首页' }
      },
      {
        path: 'setting',
        name: 'SystemSetting',
        component: SystemSetting,
        meta: { title: '系统设置' }
      },
      {
        path: 'teachers',
        name: 'UserManage',
        component: UserManage,
        meta: { title: '用户管理' }
      }
    ]
  },
  // 添加默认路由
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router