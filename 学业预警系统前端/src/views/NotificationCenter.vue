<template>
  <div class="notification-center-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">🔔 通知中心</span>
          <div class="header-buttons">
            <el-button v-if="unreadCount > 0" type="primary" size="small" @click="markAllAsRead">
              全部已读
            </el-button>
            <el-button type="default" size="small" @click="refreshNotifications">刷新</el-button>
          </div>
        </div>
      </template>

      <!-- 未读通知数 -->
      <div v-if="unreadCount > 0" class="unread-banner">
        <el-alert
          :title="`您有 ${unreadCount} 条未读通知`"
          type="info"
          :closable="false"
          show-icon
        ></el-alert>
      </div>

      <!-- 通知列表 -->
      <div v-if="notifications.length === 0" class="empty-state">
        <p>暂无通知</p>
        <small>所有的通知都会出现在这里</small>
      </div>

      <el-timeline v-else>
        <el-timeline-item
          v-for="notification in notifications"
          :key="notification.id"
          :timestamp="formatDate(notification.createdAt)"
          :placement="'top'"
        >
          <div class="notification-item" :class="{ unread: notification.isRead === 0 }">
            <div class="notification-header">
              <el-tag :type="getNotificationTagType(notification.notificationType)" class="notification-type">
                {{ getNotificationLabel(notification.notificationType) }}
              </el-tag>
              <el-tag v-if="notification.priority" :type="getPriorityType(notification.priority)" class="notification-priority">
                {{ getPriorityLabel(notification.priority) }}
              </el-tag>
              <span v-if="notification.isRead === 0" class="unread-badge">未读</span>
            </div>

            <div class="notification-content">
              <h4 class="notification-title">{{ notification.title }}</h4>
              <p class="notification-body">{{ notification.content }}</p>
            </div>

            <div class="notification-footer">
              <el-button
                v-if="notification.isRead === 0"
                type="text"
                size="small"
                @click="markAsRead(notification.id)"
              >
                标记为已读
              </el-button>
              <el-button type="text" size="small" @click="deleteNotification(notification.id)" style="color: #f56c6c;">
                删除
              </el-button>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>

      <!-- 分页 -->
      <el-pagination
        v-if="notifications.length > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 15, 20]"
        :total="totalNotifications"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; text-align: right"
      ></el-pagination>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { studentAPI } from '@/api'

const notifications = ref([])
const unreadCount = ref(0)
const totalNotifications = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 获取通知列表
const fetchNotifications = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }

    // 获取通知列表
    const response = await studentAPI.getNotifications(userId)
    if (Array.isArray(response)) {
      totalNotifications.value = response.length
      // 分页处理
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      notifications.value = response.slice(start, end)
    }

    // 获取未读通知数
    const unreadResp = await studentAPI.getUnreadNotificationCount(userId)
    if (typeof unreadResp === 'number') {
      unreadCount.value = unreadResp
    }
  } catch (error) {
    console.error('加载通知失败:', error)
    ElMessage.error('加载通知失败')
  }
}

// 标记单个通知为已读
const markAsRead = async (notificationId) => {
  try {
    await studentAPI.markNotificationAsRead(notificationId)
    ElMessage.success('已标记为已读')
    fetchNotifications()
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

// 全部标记为已读
const markAllAsRead = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) return

    await studentAPI.markAllNotificationsAsRead(userId)
    ElMessage.success('所有通知已标记为已读')
    fetchNotifications()
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

// 删除通知
const deleteNotification = async (notificationId) => {
  try {
    await studentAPI.deleteNotification(notificationId)
    ElMessage.success('通知已删除')
    fetchNotifications()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 刷新通知
const refreshNotifications = () => {
  fetchNotifications()
  ElMessage.success('已刷新')
}

// 获取通知类型的标签类型
const getNotificationTagType = (type) => {
  const typeMap = {
    warning: 'danger',
    appeal: 'primary',
    grade: 'success',
    message: 'info',
    assistance: 'warning'
  }
  return typeMap[type] || 'info'
}

// 获取通知类型的标签显示文本
const getNotificationLabel = (type) => {
  const labelMap = {
    warning: '预警通知',
    appeal: '申诉结果',
    grade: '成绩更新',
    message: '消息',
    assistance: '帮扶计划'
  }
  return labelMap[type] || '通知'
}

// 获取优先级的标签类型
const getPriorityType = (priority) => {
  const typeMap = {
    high: 'danger',
    medium: 'warning',
    low: 'success'
  }
  return typeMap[priority] || 'info'
}

// 获取优先级的标签显示文本
const getPriorityLabel = (priority) => {
  const labelMap = {
    high: '高优先级',
    medium: '中优先级',
    low: '低优先级'
  }
  return labelMap[priority] || '优先级'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchNotifications()
})
</script>

<style scoped>
.notification-center-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 60px);
}

.box-card {
  margin: 20px auto;
  max-width: 1000px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.unread-banner {
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.empty-state small {
  margin-top: 10px;
  font-size: 12px;
}

.notification-item {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border-left: 4px solid #ddd;
  transition: background-color 0.3s ease;
}

.notification-item.unread {
  background-color: #fff9f0;
  border-left-color: #f56c6c;
}

.notification-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.notification-type {
  font-size: 12px;
}

.notification-priority {
  font-size: 12px;
}

.unread-badge {
  font-size: 12px;
  color: #f56c6c;
  font-weight: bold;
  margin-left: 8px;
}

.notification-content {
  margin-bottom: 10px;
}

.notification-title {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.notification-body {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.notification-footer {
  display: flex;
  gap: 10px;
}

/* 分页 */
::v-deep .el-pagination {
  margin-top: 20px;
  text-align: right;
}

/* 响应式 */
@media (max-width: 768px) {
  .notification-center-container {
    padding: 10px;
  }

  .card-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .header-buttons {
    width: 100%;
  }

  .header-buttons button {
    flex: 1;
  }

  .notification-item {
    padding: 10px;
  }

  .notification-header {
    flex-wrap: wrap;
  }
}
</style>
