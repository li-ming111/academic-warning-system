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
import ClassMembers from '../views/student/ClassMembers.vue'
import ClassRanking from '../views/student/ClassRanking.vue'
import StudentLayout from '../views/StudentLayout.vue'
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
import TeacherClassManagement from '../views/TeacherClassManagement.vue'
import TeacherMessages from '../views/TeacherMessages.vue'
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
import AdminClassManagement from '../views/AdminClassManagement.vue'
import AdminSettings from '../views/AdminSettings.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import TeacherLayout from '../views/TeacherLayout.vue'
import AdminLayout from '../views/AdminLayout.vue'
import CounselorLayout from '../views/CounselorLayout.vue'

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
    path: '/student',
    component: StudentLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        component: Dashboard,
        meta: { requiresAuth: true }
      },
      {
        path: 'scores',
        component: Scores,
        meta: { requiresAuth: true }
      },
      {
        path: 'warnings',
        component: Warnings,
        meta: { requiresAuth: true }
      },
      {
        path: 'assistance',
        component: Assistance,
        meta: { requiresAuth: true }
      },
      {
        path: 'appeals',
        component: Appeals,
        meta: { requiresAuth: true }
      },
      {
        path: 'messages',
        component: Messages,
        meta: { requiresAuth: true }
      },
      {
        path: 'notification-center',
        component: NotificationCenter,
        meta: { requiresAuth: true }
      },
      {
        path: 'benchmark-analysis',
        component: BenchmarkAnalysis,
        meta: { requiresAuth: true }
      },
      {
        path: 'assistance-feedback',
        component: AssistanceFeedback,
        meta: { requiresAuth: true }
      },
      {
        path: 'class-members',
        component: ClassMembers,
        meta: { requiresAuth: true }
      },
      {
        path: 'class-ranking',
        component: ClassRanking,
        meta: { requiresAuth: true }
      },
      {
        path: 'settings',
        component: Settings,
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/dashboard',
    redirect: '/student/dashboard'
  },
  {
    path: '/counselor-register',
    name: 'CounselorRegister',
    component: CounselorRegister,
    meta: { requiresAuth: false }
  },

  {
    path: '/teacher-register',
    name: 'TeacherRegister',
    component: TeacherRegister,
    meta: { requiresAuth: false }
  },
  {
    path: '/teacher',
    component: TeacherLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: TeacherDashboard
      },
      {
        path: 'scores',
        name: 'TeacherScores',
        component: TeacherScores
      },
      {
        path: 'warnings',
        name: 'TeacherWarnings',
        component: TeacherWarnings
      },
      {
        path: 'analysis',
        name: 'TeacherAnalysis',
        component: TeacherAnalysis
      },
      {
        path: 'credit-prediction',
        name: 'TeacherCreditPrediction',
        component: TeacherCreditPrediction
      },
      {
        path: 'audit-log',
        name: 'TeacherAuditLog',
        component: TeacherAuditLog
      },
      {
        path: 'feedback-management',
        name: 'TeacherFeedbackManagement',
        component: TeacherFeedbackManagement
      },
      {
        path: 'feedback',
        name: 'TeacherFeedback',
        component: TeacherFeedback
      },
      {
        path: 'courses',
        name: 'TeacherCourses',
        component: TeacherCourses
      },
      {
        path: 'class-management',
        name: 'TeacherClassManagement',
        component: TeacherClassManagement
      },
      {
        path: 'messages',
        name: 'TeacherMessages',
        component: TeacherMessages
      }
    ]
  },
  {
    path: '/teacher-dashboard',
    name: 'TeacherDashboardLegacy',
    component: TeacherDashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-scores',
    name: 'TeacherScoresLegacy',
    component: TeacherScores,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-warnings',
    name: 'TeacherWarningsLegacy',
    component: TeacherWarnings,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-analysis',
    name: 'TeacherAnalysisLegacy',
    component: TeacherAnalysis,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-credit-prediction',
    name: 'TeacherCreditPredictionLegacy',
    component: TeacherCreditPrediction,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-audit-log',
    name: 'TeacherAuditLogLegacy',
    component: TeacherAuditLog,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-feedback-management',
    name: 'TeacherFeedbackManagementLegacy',
    component: TeacherFeedbackManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-feedback',
    name: 'TeacherFeedbackLegacy',
    component: TeacherFeedback,
    meta: { requiresAuth: true }
  },
  {
    path: '/teacher-courses',
    name: 'TeacherCoursesLegacy',
    component: TeacherCourses,
    meta: { requiresAuth: true }
  },
  {
    path: '/counselor',
    component: CounselorLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'CounselorDashboard',
        component: CounselorDashboard
      },
      {
        path: 'students',
        name: 'CounselorStudents',
        component: CounselorStudents
      },
      {
        path: 'warnings',
        name: 'CounselorWarnings',
        component: CounselorWarnings
      },
      {
        path: 'courses',
        name: 'CounselorCourses',
        component: CounselorCourses
      },
      {
        path: 'credit-monitor',
        name: 'CounselorCreditMonitor',
        component: CounselorCreditMonitor
      },
      {
        path: 'notifications',
        name: 'CounselorNotifications',
        component: CounselorNotifications
      },
      {
        path: 'class-management',
        name: 'CounselorClassManagement',
        component: CounselorClassManagement
      },
      {
        path: 'settings',
        name: 'CounselorSettings',
        component: CounselorSettings
      }
    ]
  },
  {
    path: '/counselor-dashboard',
    redirect: '/counselor/dashboard'
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
      },
      {
        path: 'class-management',
        name: 'AdminClassManagement',
        component: AdminClassManagement
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: AdminSettings
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
    redirect: (to) => {
      const hasToken = localStorage.getItem('token')
      if (!hasToken) {
        return '/login'
      }
      // 如果已登录，根据角色跳转
      const role = localStorage.getItem('role')
      if (role === '2' || role === 'teacher') {
        return '/teacher/dashboard'
      } else if (role === '4' || role === 'counselor') {
        return '/counselor-dashboard'
      } else if (role === '3' || role === 'admin') {
        return '/admin/dashboard'
      } else {
        return '/student/dashboard'
      }
    }
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
      next('/student/dashboard')
    }
    return
  }
  
  next()
})

export default router
