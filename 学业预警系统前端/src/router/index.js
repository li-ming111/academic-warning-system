import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Dashboard from '../views/Dashboard.vue'
import Scores from '../views/Scores.vue'
import Warnings from '../views/Warnings.vue'
import Assistance from '../views/Assistance.vue'
import Appeals from '../views/Appeals.vue'
import Messages from '../views/Messages.vue'
import Settings from '../views/Settings.vue'
import Statistics from '../views/Statistics.vue'
import NotificationCenter from '../views/student/NotificationCenter.vue'
import BenchmarkAnalysis from '../views/student/BenchmarkAnalysis.vue'
import AppealManagement from '../views/student/AppealManagement.vue'
import AssistanceFeedback from '../views/student/AssistanceFeedback.vue'
import TeacherDashboard from '../views/TeacherDashboard.vue'
import TeacherRegister from '../views/TeacherRegister.vue'
import TeacherScores from '../views/TeacherScores.vue'
import TeacherWarnings from '../views/TeacherWarnings.vue'
import TeacherAnalysis from '../views/TeacherAnalysis.vue'
import TeacherCreditPrediction from '../views/TeacherCreditPrediction.vue'
import TeacherAuditLog from '../views/TeacherAuditLog.vue'
import TeacherFeedbackManagement from '../views/TeacherFeedbackManagement.vue'
import TeacherFeedback from '../views/TeacherFeedback.vue'
import TeacherCourses from '../views/TeacherCourses.vue'
import CounselorDashboard from '../views/CounselorDashboard.vue'
import CounselorRegister from '../views/CounselorRegister.vue'
import CounselorStudents from '../views/CounselorStudents.vue'
import CounselorWarnings from '../views/CounselorWarnings.vue'
import CounselorCourses from '../views/CounselorCourses.vue'
import CounselorCreditMonitor from '../views/CounselorCreditMonitor.vue'
import CounselorNotifications from '../views/CounselorNotifications.vue'
import CounselorClassManagement from '../views/CounselorClassManagement.vue'
import CounselorSettings from '../views/CounselorSettings.vue'
import AdminRegister from '../views/AdminRegister.vue'
import AdminColleges from '../views/AdminColleges.vue'
import AdminMajors from '../views/AdminMajors.vue'
import AdminUsers from '../views/AdminUsers.vue'
import AdminRules from '../views/AdminRules.vue'
import AdminStatistics from '../views/AdminStatistics.vue'
import AdminCourses from '../views/AdminCourses.vue'
import AdminPermissions from '../views/AdminPermissions.vue'
import AdminMessages from '../views/AdminMessages.vue'
import AdminDataExport from '../views/AdminDataExport.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import TeacherLayout from '../views/TeacherLayout.vue'
import AdminLayout from '../views/AdminLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/scores',
    name: 'Scores',
    component: Scores,
    meta: { requiresAuth: true }
  },
  {
    path: '/warnings',
    name: 'Warnings',
    component: Warnings,
    meta: { requiresAuth: true }
  },
  {
    path: '/assistance',
    name: 'Assistance',
    component: Assistance,
    meta: { requiresAuth: true }
  },
  {
    path: '/appeals',
    name: 'Appeals',
    component: Appeals,
    meta: { requiresAuth: true }
  },
  {
    path: '/messages',
    name: 'Messages',
    component: Messages,
    meta: { requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: { requiresAuth: true }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: Statistics,
    meta: { requiresAuth: true }
  },
  {
    path: '/notification-center',
    name: 'NotificationCenter',
    component: NotificationCenter,
    meta: { requiresAuth: true }
  },
  {
    path: '/benchmark-analysis',
    name: 'BenchmarkAnalysis',
    component: BenchmarkAnalysis,
    meta: { requiresAuth: true }
  },
  {
    path: '/appeal-management',
    name: 'AppealManagement',
    component: AppealManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/assistance-feedback',
    name: 'AssistanceFeedback',
    component: AssistanceFeedback,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor-register',
    name: 'CounselorRegister',
    component: CounselorRegister,
    meta: { requiresAuth: false }
  },
  {
    path: '/admin-register',
    name: 'AdminRegister',
    component: AdminRegister,
    meta: { requiresAuth: false }
  },
  {
    path: '/teacher-register',
    name: 'TeacherRegister',
    component: TeacherRegister,
    meta: { requiresAuth: false }
  },
  {
    path: '/teacher-dashboard',
    name: 'TeacherDashboard',
    component: TeacherDashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-scores',
    name: 'TeacherScores',
    component: TeacherScores,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-warnings',
    name: 'TeacherWarnings',
    component: TeacherWarnings,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-analysis',
    name: 'TeacherAnalysis',
    component: TeacherAnalysis,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-credit-prediction',
    name: 'TeacherCreditPrediction',
    component: TeacherCreditPrediction,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-audit-log',
    name: 'TeacherAuditLog',
    component: TeacherAuditLog,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-feedback-management',
    name: 'TeacherFeedbackManagement',
    component: TeacherFeedbackManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-feedback',
    name: 'TeacherFeedback',
    component: TeacherFeedback,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-courses',
    name: 'TeacherCourses',
    component: TeacherCourses,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor-dashboard',
    name: 'CounselorDashboard',
    component: CounselorDashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor/students',
    name: 'CounselorStudents',
    component: CounselorStudents,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor/warnings',
    name: 'CounselorWarnings',
    component: CounselorWarnings,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor/courses',
    name: 'CounselorCourses',
    component: CounselorCourses,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor/credit-monitor',
    name: 'CounselorCreditMonitor',
    component: CounselorCreditMonitor,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor/notifications',
    name: 'CounselorNotifications',
    component: CounselorNotifications,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor/class-management',
    name: 'CounselorClassManagement',
    component: CounselorClassManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor/settings',
    name: 'CounselorSettings',
    component: CounselorSettings,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard
      },
      {
        path: 'colleges',
        name: 'AdminColleges',
        component: AdminColleges
      },
      {
        path: 'majors',
        name: 'AdminMajors',
        component: AdminMajors
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: AdminUsers
      },
      {
        path: 'rules',
        name: 'AdminRules',
        component: AdminRules
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: AdminStatistics
      },
      {
        path: 'courses',
        name: 'AdminCourses',
        component: AdminCourses
      },
      {
        path: 'permissions',
        name: 'AdminPermissions',
        component: AdminPermissions
      },
      {
        path: 'messages',
        name: 'AdminMessages',
        component: AdminMessages
      },
      {
        path: 'data-export',
        name: 'AdminDataExport',
        component: AdminDataExport
      }
    ]
  },
  {
    path: '/notifications',
    redirect: '/dashboard'
  },
  {
    path: '/hybridaction/:pathMatch(.*)*',
    redirect: '/dashboard'
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const hasToken = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  
  // 如果没有token且需要认证，跳转到登录
  if (to.meta.requiresAuth && !hasToken) {
    next('/login')
    return
  }
  
  // 如果已登录且访问登录/注册页，根据角色跳转到对应首页
  if ((to.path === '/login' || to.path === '/register' || to.path === '/teacher-register' || to.path === '/counselor-register' || to.path === '/admin-register') && hasToken) {
    if (role === '2' || role === 'teacher') {
      next('/teacher/dashboard')
    } else if (role === '4' || role === 'counselor') {
      next('/counselor-dashboard')
    } else if (role === '3' || role === 'admin') {
      next('/admin/dashboard')
    } else {
      // 默认学生
      next('/dashboard')
    }
    return
  }
  
  next()
})

export default router
