<template>
  <div class="notification-center-container">
    <el-container>
      <!-- 侧边栏 - 订阅管理 -->
      <el-aside width="250px" class="subscription-sidebar">
        <div class="sidebar-title">订阅偏好</div>
        
        <div class="subscription-section">
          <div class="section-title">预警通知</div>
          <el-checkbox v-model="preferences.subscribeWarnings">
            订阅预警通知
          </el-checkbox>
          
          <div class="level-settings">
            <el-checkbox v-model="preferences.subscribeLow">
              <span class="low-badge">低</span> 低级预警
            </el-checkbox>
            <el-checkbox v-model="preferences.subscribeMedium">
              <span class="medium-badge">中</span> 中级预警
            </el-checkbox>
            <el-checkbox v-model="preferences.subscribeHigh">
              <span class="high-badge">高</span> 高级预警
            </el-checkbox>
          </div>
        </div>

        <div class="subscription-section">
          <div class="section-title">其他通知</div>
          <el-checkbox v-model="preferences.subscribeAssistance">
            帮扶计划更新
          </el-checkbox>
          <el-checkbox v-model="preferences.subscribeSystem">
            系统消息
          </el-checkbox>
        </div>

        <div class="subscription-section">
          <div class="section-title">推送渠道</div>
          <el-checkbox v-model="preferences.pushApp"> 
            <i class="el-icon-bell"></i> 应用内通知
          </el-checkbox>
          <el-checkbox v-model="preferences.pushEmail">
            <i class="el-icon-message"></i> 邮件通知
          </el-checkbox>
          <el-checkbox v-model="preferences.pushSms">
            <i class="el-icon-phone"></i> 短信通知
          </el-checkbox>
        </div>

        <el-button type="primary" @click="savePreferences" style="width: 100%; margin-top: 20px">
          保存设置
        </el-button>
      </el-aside>

      <!-- 主内容区 - 通知列表 -->
      <el-main class="notification-main">
        <!-- 顶部工具栏 -->
        <div class="notification-toolbar">
          <div class="toolbar-left">
            <el-tag type="info">未读: {{ unreadCount }}</el-tag>
            <el-button-group style="margin-left: 20px">
              <el-button size="small" :type="activeTab === 'all' ? 'primary' : 'default'" @click="activeTab = 'all'">
                全部
              </el-button>
              <el-button size="small" :type="activeTab === 'unread' ? 'primary' : 'default'" @click="activeTab = 'unread'">
                未读
              </el-button>
            </el-button-group>
          </div>
          
          <div class="toolbar-right">
            <el-button size="small" @click="markAllAsRead">标记全部已读</el-button>
            <el-button size="small" @click="clearReadNotifications" type="danger" plain>清空已读</el-button>
          </div>
        </div>

        <!-- 通知列表 -->
        <el-table 
          :data="activeTab === 'unread' ? unreadNotifications : allNotifications"
          style="width: 100%; margin-top: 20px"
          v-loading="loading"
        >
          <el-table-column prop="title" label="标题" min-width="200"></el-table-column>
          
          <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip></el-table-column>
          
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)">{{ getTypeLabel(row.type) }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createdAt" label="时间" width="180" :formatter="formatTime"></el-table-column>
          
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <span v-if="row.isRead === 1" style="color: #909399">已读</span>
              <el-tag v-else type="warning">未读</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="150" align="center">
            <template #default="{ row }">
              <el-button v-if="row.isRead === 0" size="small" link @click="markAsRead(row.id)">
                标记已读
              </el-button>
              <el-button size="small" link type="danger" @click="deleteNotification(row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
          v-if="activeTab === 'all'"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
          style="margin-top: 20px; text-align: right"
        ></el-pagination>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { studentAPI } from '@/api'

export default {
  name: 'NotificationCenter',
  data() {
    return {
      activeTab: 'all',
      unreadCount: 0,
      unreadNotifications: [],
      allNotifications: [],
      preferences: {
        subscribeWarnings: 1,
        subscribeLow: 1,
        subscribeMedium: 1,
        subscribeHigh: 1,
        subscribeAssistance: 1,
        subscribeSystem: 1,
        pushEmail: 1,
        pushSms: 0,
        pushApp: 1
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false
    }
  },
  computed: {
    userId() {
      return localStorage.getItem('userId') || this.$store.state.user?.id
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    async init() {
      this.loading = true
      try {
        // 加载未读通知
        await this.loadUnreadNotifications()
        // 加载所有通知
        await this.loadAllNotifications()
        // 加载用户订阅偏好
        await this.loadPreferences()
        // 加载未读数量
        await this.loadUnreadCount()
      } finally {
        this.loading = false
      }
    },

    async loadUnreadNotifications() {
      try {
        const response = await studentAPI.getUnreadNotificationsAdvanced(this.userId)
        if (response.data?.code === 200) {
          this.unreadNotifications = response.data.data || []
        }
      } catch (error) {
        this.$message.error('加载未读通知失败')
      }
    },

    async loadAllNotifications() {
      try {
        const response = await studentAPI.getNotificationsPageable(this.userId, this.currentPage, this.pageSize)
        if (response.data?.code === 200) {
          const result = response.data.data
          this.allNotifications = result.data || []
          this.total = result.total || 0
        }
      } catch (error) {
        this.$message.error('加载通知列表失败')
      }
    },

    async loadPreferences() {
      try {
        const studentId = localStorage.getItem('studentId') || this.userId
        const response = await studentAPI.getSubscriptionPreferences(studentId)
        if (response.data?.code === 200) {
          Object.assign(this.preferences, response.data.data)
        }
      } catch (error) {
        console.warn('加载订阅偏好失败', error)
      }
    },

    async loadUnreadCount() {
      try {
        const response = await studentAPI.getUnreadNotificationCountAdvanced(this.userId)
        if (response.data?.code === 200) {
          this.unreadCount = response.data.data || 0
        }
      } catch (error) {
        console.error('加载未读数量失败', error)
      }
    },

    async savePreferences() {
      try {
        const response = await studentAPI.updateSubscriptionPreferences(this.userId, this.preferences)
        if (response.data?.code === 200) {
          this.$message.success('订阅设置已保存')
        }
      } catch (error) {
        this.$message.error('保存设置失败')
      }
    },

    async markAsRead(notificationId) {
      try {
        const response = await studentAPI.markNotificationRead(notificationId)
        if (response.data?.code === 200) {
          this.$message.success('已标记为已读')
          this.init()
        }
      } catch (error) {
        this.$message.error('操作失败')
      }
    },

    async markAllAsRead() {
      try {
        const notificationIds = this.unreadNotifications.map(n => n.id)
        if (notificationIds.length === 0) {
          this.$message.info('没有未读通知')
          return
        }
        const response = await studentAPI.markMultipleNotificationsAsRead(this.userId, notificationIds)
        if (response.data?.code === 200) {
          this.$message.success('已标记全部为已读')
          this.init()
        }
      } catch (error) {
        this.$message.error('操作失败')
      }
    },

    async deleteNotification(notificationId) {
      this.$confirm('确定删除该通知？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await studentAPI.deleteNotificationMsg(notificationId)
          if (response.data?.code === 200) {
            this.$message.success('删除成功')
            this.init()
          }
        } catch (error) {
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },

    async clearReadNotifications() {
      this.$confirm('确定清空所有已读通知？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await studentAPI.clearReadNotifications(this.userId)
          if (response.data?.code === 200) {
            this.$message.success('已清空')
            this.init()
          }
        } catch (error) {
          this.$message.error('操作失败')
        }
      }).catch(() => {})
    },

    handlePageChange(page) {
      this.currentPage = page
      this.loadAllNotifications()
    },

    handlePageSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.loadAllNotifications()
    },

    formatTime(row) {
      const date = new Date(row.createdAt)
      return date.toLocaleString('zh-CN')
    },

    getTypeLabel(type) {
      const labels = {
        warning: '预警通知',
        plan_update: '帮扶更新',
        system_message: '系统消息'
      }
      return labels[type] || type
    },

    getTypeTagType(type) {
      const types = {
        warning: 'danger',
        plan_update: 'success',
        system_message: 'info'
      }
      return types[type] || 'info'
    }
  }
}
</script>

<style scoped lang="scss">
.notification-center-container {
  .el-container {
    height: 100%;
  }

  .subscription-sidebar {
    background: #f5f7fa;
    border-right: 1px solid #ebeef5;
    padding: 20px;
    overflow-y: auto;

    .sidebar-title {
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 20px;
      color: #303133;
    }

    .subscription-section {
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #ebeef5;

      .section-title {
        font-size: 14px;
        font-weight: bold;
        color: #606266;
        margin-bottom: 10px;
      }

      .el-checkbox {
        display: block;
        margin-bottom: 8px;
        color: #606266;
      }

      .level-settings {
        padding-left: 20px;
        margin-top: 10px;

        .el-checkbox {
          margin-bottom: 6px;
        }
      }

      .low-badge, .medium-badge, .high-badge {
        display: inline-block;
        width: 20px;
        height: 20px;
        border-radius: 3px;
        color: white;
        text-align: center;
        line-height: 20px;
        font-weight: bold;
        margin-right: 6px;
        font-size: 12px;
      }

      .low-badge {
        background: #67c23a;
      }

      .medium-badge {
        background: #e6a23c;
      }

      .high-badge {
        background: #f56c6c;
      }
    }
  }

  .notification-main {
    padding: 20px;

    .notification-toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px;
      background: #fff;
      border: 1px solid #ebeef5;
      border-radius: 4px;

      .toolbar-left, .toolbar-right {
        display: flex;
        align-items: center;
        gap: 10px;
      }
    }
  }
}
</style>
