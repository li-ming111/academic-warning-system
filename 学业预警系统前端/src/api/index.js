import apiClient from './client'

export const authAPI = {
  login: async (username, password) => {
    return apiClient.post('/auth/login', { username, password })
  },
  getColleges: () => {
    return apiClient.get('/auth/colleges')
  },
  registerStudent: (data) => {
    return apiClient.post('/auth/register/student', data)
  },
  registerTeacher: (data) => {
    return apiClient.post('/auth/register/teacher', data)
  },
  registerCounselor: (data) => {
    return apiClient.post('/auth/register/counselor', data)
  },
  registerAdmin: (data) => {
    return apiClient.post('/auth/register/admin', data)
  }
}

export const studentAPI = {
  register: (data) => {
    return apiClient.post('/students/register', data)
  },
  getStudentInfo: (studentId) => {
    return apiClient.get(`/students/${studentId}`)
  },
  getStudentInfoByUserId: (userId) => {
    return apiClient.get(`/students/student-by-user/${userId}`)
  },
  getStudentGPA: (studentId) => {
    return apiClient.get(`/students/${studentId}/gpa`)
  },
  getDashboard: (userId) => {
    return apiClient.get(`/students/dashboard/${userId}`)
  },
  getScores: (userId, semester) => {
    return apiClient.get(`/students/scores/${userId}`, { params: { semester } })
  },
  getWarnings: (userId) => {
    return apiClient.get(`/students/warnings/${userId}`)
  },
  getAssistancePlans: (userId) => {
    return apiClient.get(`/students/assistance/${userId}`)
  },
  updatePlanProgress: (planId, progress) => {
    return apiClient.post(`/students/assistance/${planId}/progress`, null, { params: { progress } })
  },
  // ========== 班级信息 ==========
  getClassInfo: (studentId) => {
    return apiClient.get(`/students/${studentId}/class-info`)
  },
  // ========== 通知管理 ==========
  
  getUnreadNotifications: (userId) => {
    return apiClient.get(`/students/notification-center/${userId}/unread`)
  },
  getNotificationsPageable: (userId, page = 1, pageSize = 10) => {
    return apiClient.get(`/students/notification-center/${userId}/list`, {
      params: { page, pageSize }
    })
  },
  markNotificationRead: (notificationId) => {
    return apiClient.post(`/students/notification-center/${notificationId}/mark-read`)
  },
  markMultipleNotificationsAsRead: (userId, notificationIds) => {
    return apiClient.post(`/students/notification-center/${userId}/mark-batch-read`, notificationIds)
  },
  deleteNotificationMsg: (notificationId) => {
    return apiClient.post(`/students/notification-center/${notificationId}/delete`)
  },
  getUnreadNotificationCount: (userId) => {
    return apiClient.get(`/students/notification-center/${userId}/unread-count`)
  },
  clearReadNotifications: (userId) => {
    return apiClient.post(`/students/notification-center/${userId}/clear-read`)
  },
  
  // 订阅偏好管理API
  getSubscriptionPreferences: (studentId) => {
    return apiClient.get(`/students/subscription/${studentId}/preferences`)
  },
  updateSubscriptionPreferences: (studentId, preference) => {
    return apiClient.post(`/students/subscription/${studentId}/update`, preference)
  },
  subscribeWarningLevel: (studentId, level) => {
    return apiClient.post(`/students/subscription/${studentId}/subscribe-level`, null, {
      params: { level }
    })
  },
  unsubscribeWarningLevel: (studentId, level) => {
    return apiClient.post(`/students/subscription/${studentId}/unsubscribe-level`, null, {
      params: { level }
    })
  },
  setPushChannel: (studentId, channel, enabled) => {
    return apiClient.post(`/students/subscription/${studentId}/set-channel`, null, {
      params: { channel, enabled }
    })
  },
  
  // ========== 对标分析 ==========
  
  getLatestBenchmark: (studentId) => {
    return apiClient.get(`/students/benchmark/${studentId}/latest`)
  },
  getBenchmarkBySemester: (studentId, semester) => {
    return apiClient.get(`/students/benchmark/${studentId}/semester`, { params: { semester } })
  },
  getHistoryBenchmark: (studentId) => {
    return apiClient.get(`/students/benchmark/${studentId}/history`)
  },
  getClassRanking: (studentId, classId, semester) => {
    return apiClient.get(`/students/benchmark/${studentId}/class-ranking`, {
      params: { classId, semester }
    })
  },
  getMajorRanking: (studentId, majorId, semester) => {
    return apiClient.get(`/students/benchmark/${studentId}/major-ranking`, {
      params: { majorId, semester }
    })
  },
  getClassBenchmarkComparison: (classId, semester) => {
    return apiClient.get(`/students/benchmark/class/${classId}/comparison`, { params: { semester } })
  },
  getMajorBenchmarkComparison: (majorId, semester) => {
    return apiClient.get(`/students/benchmark/major/${majorId}/comparison`, { params: { semester } })
  },
  getProgressReport: (studentId) => {
    return apiClient.get(`/students/benchmark/${studentId}/progress-report`)
  },
  
  // ========== 申诉管理 ==========
  
  
  submitAppeal: (appeal) => {
    return apiClient.post(`/students/appeals/submit`, appeal)
  },
  getStudentAppeals: (studentId) => {
    return apiClient.get(`/students/appeals/${studentId}/list`)
  },
  getPendingAppeals: (studentId) => {
    return apiClient.get(`/students/appeals/${studentId}/pending`)
  },
  getAppealDetail: (appealId) => {
    return apiClient.get(`/students/appeals/${appealId}/detail`)
  },
  withdrawAppeal: (appealId) => {
    return apiClient.post(`/students/appeals/${appealId}/withdraw`)
  },
  getAppealStatistics: (studentId) => {
    return apiClient.get(`/students/appeals/${studentId}/statistics`)
  },
  getAppealSuccessRate: (studentId) => {
    return apiClient.get(`/students/appeals/${studentId}/success-rate`)
  },
  getAppealsByStatus: (studentId, status) => {
    return apiClient.get(`/students/appeals/${studentId}/by-status`, { params: { status } })
  },
  getAppealHistory: (studentId, page = 1, pageSize = 10) => {
    return apiClient.get(`/students/appeals/${studentId}/history`, { params: { page, pageSize } })
  },
  getAppealReasonStatistics: (studentId) => {
    return apiClient.get(`/students/appeals/${studentId}/reason-statistics`)
  },
  
  // ========== 帮扶反馈 ==========
  
  
  submitEvaluation: (evaluation) => {
    return apiClient.post(`/students/evaluations/submit`, evaluation)
  },
  getStudentEvaluations: (studentId) => {
    return apiClient.get(`/students/evaluations/${studentId}/list`)
  },
  getPlanEvaluation: (planId) => {
    return apiClient.get(`/students/evaluations/${planId}/detail`)
  },
  getEvaluationStatistics: (studentId) => {
    return apiClient.get(`/students/evaluations/${studentId}/statistics`)
  },
  getUserSettings: (userId) => {
    return apiClient.get(`/students/settings/${userId}`)
  },
  updateUserSettings: (userId, settings) => {
    return apiClient.put(`/students/settings/${userId}`, settings)
  },
  getSecurityLogs: (userId, page = 1, pageSize = 10) => {
    return apiClient.get(`/students/settings/${userId}/security-logs`, { params: { page, pageSize } })
  },
  getAppeals: (studentId) => {
    return apiClient.get(`/students/appeals/${studentId}/list`)
  },
  getUnreadNotificationCountAdvanced: (userId) => {
    return apiClient.get(`/students/notification-center/${userId}/unread-count`)
  },
  getUnreadNotificationsAdvanced: (userId) => {
    return apiClient.get(`/students/notification-center/${userId}/unread`)
  },
  // ========== 消息管理API ==========
  getMessages: (userId) => {
    return apiClient.get(`/students/messages/${userId}`)
  },
  getUnreadCount: (userId) => {
    return apiClient.get(`/students/messages/${userId}/unread-count`)
  },
  sendMessage: (data) => {
    return apiClient.post(`/students/messages/send`, data)
  },
  markMessageAsRead: (messageId) => {
    return apiClient.post(`/students/messages/${messageId}/mark-read`)
  }
}

export const teacherAPI = {
  register: (data) => {
    return apiClient.post(`/teachers/register`, data)
  },
  getDashboard: (userId) => {
    return apiClient.get(`/teachers/dashboard/${userId}`)
  },
  getStudents: (teacherId) => {
    return apiClient.get(`/teachers/students/${teacherId}`)
  },
  getCourses: (teacherId) => {
    return apiClient.get(`/teachers/courses`, { params: { teacher_id: teacherId } })
  },
  getScores: (courseId) => {
    return apiClient.get(`/teachers/scores`, { params: { course_id: courseId } })
  },
  saveScores: (scores) => {
    return apiClient.post(`/teachers/scores`, scores)
  },
  updateScore: (scoreId, data) => {
    return apiClient.put(`/teachers/scores/${scoreId}`, data)
  },
  exportScores: (courseId) => {
    return apiClient.get(`/teachers/export/scores`, { params: { course_id: courseId }, responseType: 'blob' })
  },
  downloadScores: (courseId) => {
    return apiClient.get(`/teachers/download/scores`, { params: { course_id: courseId }, responseType: 'blob' })
  },
  getFeedbacks: (teacherId, category) => {
    return apiClient.get(`/teachers/feedbacks`, { params: { teacher_id: teacherId, category } })
  },
  replyFeedback: (feedbackId, data) => {
    return apiClient.put(`/teachers/feedbacks/${feedbackId}`, data)
  },
  getEnrollments: (teacherId, courseId) => {
    const params = {}
    if (teacherId) params.teacherId = teacherId
    if (courseId) params.course_id = courseId
    return apiClient.get(`/teachers/enrollments`, { params })
  },
  saveCommunication: (data) => {
    return apiClient.post(`/teachers/communications`, data)
  },
  getAnalysis: (courseId) => {
    return apiClient.get(`/teachers/analysis`, { params: { course_id: courseId } })
  },
  getCourseDetail: (courseId) => {
    return apiClient.get(`/teachers/courses/${courseId}`)
  },
  recommendCourse: (data) => {
    return apiClient.post(`/teachers/courses/recommend`, data)
  },
  getWarnings: (teacherId, status) => {
    return apiClient.get(`/teachers/warnings/${teacherId}`, { params: { status } })
  },
  processWarning: (warningId, data) => {
    return apiClient.post(`/teachers/warnings/${warningId}/process`, data)
  },
  // ============= 班级管理申请API =============
  submitClassManagementRequest: (data) => {
    return apiClient.post(`/teachers/class-management/request`, data)
  },
  getMyClassManagementRequests: (teacherId) => {
    return apiClient.get(`/teachers/class-management/requests`, { params: { teacherId } })
  },
  getTodos: (teacherId) => {
    return apiClient.get(`/teachers/todos/${teacherId}`)
  },
  // ============= 成绩分析API =============
  getCourseScoreDistribution: (courseId) => {
    return apiClient.get(`/teachers/courses/${courseId}/distribution`)
  },
  getCourseAnomalies: (courseId, threshold = 60) => {
    return apiClient.get(`/teachers/courses/${courseId}/anomaly`, { params: { threshold } })
  },
  getCourseStudents: (courseId, page = 1, size = 20) => {
    return apiClient.get(`/teachers/courses/${courseId}/students`, { params: { page, size } })
  },
  // ============= 学分预测API =============
  getCreditPrediction: (studentId) => {
    return apiClient.get(`/teachers/students/${studentId}/credit-prediction`)
  },
  getCourseRecommendations: (courseId, limit = 5) => {
    return apiClient.get(`/teachers/courses/${courseId}/recommendations`, { params: { limit } })
  },
  // ============= 审计日志API =============
  getScoreLogs: (teacherId, page = 1, size = 20) => {
    return apiClient.get(`/teachers/scores/logs/${teacherId}`, { params: { page, size } })
  },
  // ============= 反馈管理API =============
  getFeedbackList: (teacherId, category, page = 1, size = 20) => {
    return apiClient.get(`/teachers/feedbacks/${teacherId}/list`, { params: { category, page, size } })
  },
  replyToFeedback: (feedbackId, data) => {
    return apiClient.post(`/teachers/feedbacks/${feedbackId}/reply`, data)
  },
  // 兼容性别名
  getFeedback: (teacherId, category) => {
    return apiClient.get(`/teachers/feedbacks/${teacherId}/list`, { params: { category } })
  }
}

export const counselorAPI = {
  register: (data) => {
    return apiClient.post(`/counselors/register`, data)
  },
  getDashboard: (userId) => {
    return apiClient.get(`/counselors/dashboard/${userId}`)
  },
  getStudents: (counselorId) => {
    return apiClient.get(`/counselors/students`, { params: { counselor_id: counselorId } })
  },
  searchStudents: (searchName, classId) => {
    // 前端过滤，后端不提供此接口
    return Promise.resolve([])
  },
  getStudentDetail: (studentId) => {
    return apiClient.get(`/counselors/students/${studentId}`)
  },
  notifyStudents: (data) => {
    return apiClient.post(`/counselors/students/notify`, data)
  },
  getWarnings: (counselorId, status) => {
    return apiClient.get(`/counselors/warnings`, { params: { counselor_id: counselorId, status } })
  },
  processWarning: (warningId, data) => {
    return apiClient.post(`/counselors/warnings/${warningId}/process`, data)
  },
  batchProcessWarnings: (warningIds) => {
    return apiClient.post(`/counselors/warnings/batch-process`, warningIds)
  },
  getEnrollments: (counselorId) => {
    return apiClient.get(`/counselors/enrollments`, { params: { counselor_id: counselorId } })
  },
  // ============= 帮扶计划API =============
  getPlansByStudent: (studentId) => {
    return apiClient.get(`/counselors/assistance/student/${studentId}`)
  },
  createAssistancePlan: (data) => {
    return apiClient.post(`/counselors/assistance/create`, data)
  },
  updatePlanProgress: (planId, progressPercentage) => {
    return apiClient.post(`/counselors/assistance/${planId}/progress`, { progress_percentage: progressPercentage })
  },
  // ============= 成绩跟踪API =============
  getClassScores: (counselorId, classId) => {
    return apiClient.get(`/counselors/scores/class/${classId}`, { params: { counselor_id: counselorId } })
  },
  getCourseScoreDistribution: (courseId) => {
    return apiClient.get(`/counselors/scores/distribution/${courseId}`)
  },
  getLowScoreStudents: (counselorId) => {
    return apiClient.get(`/counselors/scores/low-score`, { params: { counselor_id: counselorId } })
  },
  // ============= 通知中心API =============
  getNotificationHistory: (counselorId, page, size) => {
    return apiClient.get(`/counselors/notifications/history`, { params: { counselor_id: counselorId, page, size } })
  },
  getNotificationTemplates: () => {
    return apiClient.get(`/counselors/notifications/templates`)
  },
  getWeeklyNotifications: (counselorId) => {
    return apiClient.get(`/counselors/notifications/weekly-count`, { params: { counselor_id: counselorId } })
  },
  // ============= 班级管理API =============
  getClasses: (counselorId) => {
    return apiClient.get(`/counselors/classes`, { params: { counselor_id: counselorId } })
  },
  getClassActivities: (counselorId) => {
    return apiClient.get(`/counselors/classes/activities`, { params: { counselor_id: counselorId } })
  },
  getClassDetail: (classId) => {
    return apiClient.get(`/counselors/classes/${classId}/detail`)
  },
  getClassStudents: (classId) => {
    return apiClient.get(`/counselors/classes/${classId}/students`)
  },
  getClassWarningOverview: (classId) => {
    return apiClient.get(`/counselors/classes/${classId}/warnings`)
  },
  compareClassWarnings: (counselorId) => {
    return apiClient.get(`/counselors/classes/warnings/compare`, { params: { counselor_id: counselorId } })
  },
  // ============= 数据分析API =============
  getCreditInsufficientRate: (counselorId) => {
    return apiClient.get(`/counselors/analytics/credit-insufficient`, { params: { counselor_id: counselorId } })
  },
  getWarningLevelDistribution: (counselorId) => {
    return apiClient.get(`/counselors/analytics/warning-distribution`, { params: { counselor_id: counselorId } })
  },
  getWarningHandlingEfficiency: (counselorId) => {
    return apiClient.get(`/counselors/analytics/handling-efficiency`, { params: { counselor_id: counselorId } })
  },
  getClassCreditAchievementRanking: (counselorId) => {
    return apiClient.get(`/counselors/analytics/credit-achievement-ranking`, { params: { counselor_id: counselorId } })
  },
  getWarningTrend: (counselorId) => {
    return apiClient.get(`/counselors/analytics/warning-trend`, { params: { counselor_id: counselorId } })
  },
  getAssistancePlanCompletionRate: (counselorId) => {
    return apiClient.get(`/counselors/analytics/assistance-completion`, { params: { counselor_id: counselorId } })
  },
  // ============= 学分监控API =============
  getCreditMonitor: (counselorId) => {
    return apiClient.get(`/counselors/credit-monitor`, { params: { counselor_id: counselorId } })
  },
  getCreditInsufficientStudents: (counselorId, page = 1, size = 20) => {
    return apiClient.get(`/counselors/credit-insufficient`, { params: { counselor_id: counselorId, page, size } })
  }
}

export const adminAPI = {
  register: (data) => {
    return apiClient.post(`/admin/register`, data)
  },
  getDashboard: () => {
    return apiClient.get(`/admin/dashboard`)
  },
  getColleges: () => {
    return apiClient.get(`/admin/colleges`)
  },
  createCollege: (data) => {
    return apiClient.post(`/admin/colleges`, data)
  },
  updateCollege: (collegeId, data) => {
    return apiClient.put(`/admin/colleges/${collegeId}`, data)
  },
  deleteCollege: (collegeId) => {
    return apiClient.delete(`/admin/colleges/${collegeId}`)
  },
  getMajorsByCollege: (collegeId) => {
    return apiClient.get(`/admin/colleges/${collegeId}/majors`)
  },
  getMajors: () => {
    return apiClient.get(`/admin/majors`)
  },
  createMajor: (data) => {
    return apiClient.post(`/admin/majors`, data)
  },
  updateMajor: (majorId, data) => {
    return apiClient.put(`/admin/majors/${majorId}`, data)
  },
  deleteMajor: (majorId) => {
    return apiClient.delete(`/admin/majors/${majorId}`)
  },
  getUsers: (page = 1, size = 10) => {
    return apiClient.get(`/admin/users`, { params: { page, size } })
  },
  disableUser: (userId) => {
    return apiClient.post(`/admin/users/${userId}/disable`)
  },
  enableUser: (userId) => {
    return apiClient.post(`/admin/users/${userId}/enable`)
  },
  getRules: () => {
    return apiClient.get(`/admin/rules`)
  },
  createRule: (data) => {
    return apiClient.post(`/admin/rules`, data)
  },
  updateRule: (ruleId, data) => {
    return apiClient.put(`/admin/rules/${ruleId}`, data)
  },
  deleteRule: (ruleId) => {
    return apiClient.delete(`/admin/rules/${ruleId}`)
  },
  getStatistics: () => {
    return apiClient.get(`/admin/statistics`)
  },
  getCourses: () => {
    return apiClient.get(`/admin/courses`)
  },
  getCourseRequirements: () => {
    // 临时返回空数组，后端需实现该端点
    return Promise.resolve([])
  },
  exportInsufficientStudents: () => {
    return apiClient.get(`/admin/export/insufficient-students`, { responseType: 'blob' })
  },
  exportColleges: () => {
    return apiClient.get(`/admin/export/colleges`, { responseType: 'blob' })
  },
  exportUsers: () => {
    return apiClient.get(`/admin/export/users`, { responseType: 'blob' })
  },
  updateUser: (userId, data) => {
    return apiClient.put(`/admin/users/${userId}`, data)
  },
  deleteUser: (userId) => {
    return apiClient.delete(`/admin/users/${userId}`)
  },
  // ============= 权限管理API =============
  getRoles: () => {
    return apiClient.get(`/admin/permissions/roles`)
  },
  getPermissions: () => {
    return apiClient.get(`/admin/permissions/list`)
  },
  getUserPermissions: (userId) => {
    return apiClient.get(`/admin/permissions/user/${userId}`)
  },
  assignRole: (data) => {
    return apiClient.post(`/admin/permissions/assign-role`, data)
  },
  toggleUserStatus: (userId) => {
    return apiClient.post(`/admin/users/${userId}/toggle-status`)
  },
  // ============= 消息管理API =============
  broadcastMessage: (data) => {
    return apiClient.post(`/admin/messages/broadcast`, data)
  },
  sendTargetedMessage: (data) => {
    return apiClient.post(`/admin/messages/targeted`, data)
  },
  getMessages: (page = 1, size = 20) => {
    return apiClient.get(`/admin/messages/list`, { params: { page, size } })
  },
  deleteMessage: (messageId) => {
    return apiClient.delete(`/admin/messages/${messageId}`)
  },
  // ============= 任务管理API =============
  getTasks: (page = 1, size = 20, status) => {
    return apiClient.get(`/admin/tasks/list`, { params: { page, size, status } })
  },
  createTask: (data) => {
    return apiClient.post(`/admin/tasks/create`, data)
  },
  updateTaskStatus: (taskId, status) => {
    return apiClient.post(`/admin/tasks/${taskId}/status`, { status })
  },
  deleteTask: (taskId) => {
    return apiClient.delete(`/admin/tasks/${taskId}`)
  },
  // ============= 数据导出与API =============
  exportStudents: () => {
    return apiClient.get(`/admin/export/students`)
  },
  exportScores: (courseId) => {
    return apiClient.get(`/admin/export/scores`, { params: { course_id: courseId } })
  },
  exportWarnings: () => {
    return apiClient.get(`/admin/export/warnings`)
  },
  exportUsers: (role) => {
    return apiClient.get(`/admin/export/users`, { params: { role } })
  },
  importStudents: (data) => {
    return apiClient.post(`/admin/import/students`, data)
  },
  importScores: (data) => {
    return apiClient.post(`/admin/import/scores`, data)
  },
  // ============= 报表API =============
  getReportTemplates: () => {
    return apiClient.get(`/admin/reports/templates`)
  },
  generateReport: (data) => {
    return apiClient.post(`/admin/reports/generate`, data)
  },
  // ============= 数据备份API =============
  backupData: () => {
    return apiClient.post(`/admin/backup`)
  },
  getBackupList: () => {
    return apiClient.get(`/admin/backups/list`)
  },
  restoreBackup: (backupId) => {
    return apiClient.post(`/admin/backup/${backupId}/restore`)
  },
  getExportHistory: () => {
    return apiClient.get(`/admin/export/history`)
  },
  deleteExport: (exportId) => {
    return apiClient.delete(`/admin/export/${exportId}`)
  },
  // ============= 班级管理申请API =============
  getPendingClassManagementRequests: () => {
    return apiClient.get(`/admin/class-management/pending-requests`)
  },
  approveClassManagementRequest: (requestId) => {
    return apiClient.post(`/admin/class-management/approve/${requestId}`)
  },
  rejectClassManagementRequest: (requestId, reason) => {
    return apiClient.post(`/admin/class-management/reject/${requestId}`, { reason })
  }
}
