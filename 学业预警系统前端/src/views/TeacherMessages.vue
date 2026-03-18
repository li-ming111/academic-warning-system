<template>
  <div class="teacher-messages">
    <!-- 页面头部 -->
    <div class="messages-header">
      <div class="header-left">
        <h2>消息中心</h2>
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" type="danger" class="unread-badge">
          未读
        </el-badge>
      </div>
      <div class="header-right">
        <el-button type="primary" plain @click="refreshMessages" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button type="success" plain @click="markAllAsRead" :disabled="unreadCount === 0">
          <el-icon><Check /></el-icon>
          全部已读
        </el-button>
      </div>
    </div>
    
    <!-- 筛选和搜索 -->
    <div class="messages-filter">
      <el-select v-model="filterStatus" placeholder="消息状态" class="filter-select">
        <el-option label="全部" value="all" />
        <el-option label="未读" value="UNREAD" />
        <el-option label="已读" value="READ" />
      </el-select>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索消息内容"
        prefix-icon="Search"
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>
    
    <!-- 消息列表 -->
    <div class="messages-list" v-if="filteredMessages.length > 0">
      <el-card 
        v-for="message in filteredMessages" 
        :key="message.id" 
        :class="{ 'unread': message.status === 0 }"
        shadow="hover"
        transition
      >
        <div class="message-header">
          <div class="message-sender">
            <el-avatar :size="48" :class="{ 'unread-avatar': message.status === 0 }">
              {{ message.senderName ? message.senderName.charAt(0) : '学' }}
            </el-avatar>
            <div class="sender-info">
              <h3>{{ message.senderName || '学生' }}</h3>
              <p class="message-time">{{ formatTime(message.createdAt) }}</p>
            </div>
          </div>
          <div class="message-status">
            <el-tag v-if="message.status === 0" type="danger" size="small">未读</el-tag>
            <el-tag v-else type="success" size="small">已读</el-tag>
          </div>
        </div>
        
        <div class="message-content">
          <p>{{ message.content }}</p>
          <div v-if="message.reply" class="message-reply">
            <div class="reply-header">
              <el-icon><ChatDotRound /></el-icon>
              <span>回复</span>
            </div>
            <p>{{ message.reply }}</p>
          </div>
        </div>
        
        <div class="message-actions">
          <el-button 
            v-if="message.status === 0" 
            type="primary" 
            size="small" 
            @click="markAsRead(message.id)"
          >
            <el-icon><Check /></el-icon>
            标记为已读
          </el-button>
          <el-button type="info" size="small" @click="showReplyDialog(message)">
            <el-icon><ChatLineRound /></el-icon>
            回复
          </el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 空状态 -->
    <div class="empty-messages" v-else>
      <el-empty 
        description="暂无消息" 
        :image-size="120"
      />
    </div>
    
    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      :title="`回复 ${currentMessage?.senderName || '学生'}`"
      width="500px"
    >
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="回复内容">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            rows="4"
            placeholder="请输入回复内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply" :loading="submittingReply">
            发送回复
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { teacherAPI } from '@/api/index'
import { Refresh, Check, Search, ChatDotRound, ChatLineRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 状态管理
const messages = ref([])
const unreadCount = ref(0)
const loading = ref(false)
const filterStatus = ref('all')
const searchKeyword = ref('')
const replyDialogVisible = ref(false)
const currentMessage = ref(null)
const replyForm = ref({ content: '' })
const submittingReply = ref(false)

// 计算属性：过滤后的消息
const filteredMessages = computed(() => {
  let result = [...messages.value]
  
  // 按状态过滤
  if (filterStatus.value !== 'all') {
    if (filterStatus.value === 'UNREAD') {
      result = result.filter(msg => msg.status === 0)
    } else if (filterStatus.value === 'READ') {
      result = result.filter(msg => msg.status === 1)
    }
  }
  
  // 按关键词搜索
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    result = result.filter(msg => 
      msg.content.toLowerCase().includes(keyword) ||
      (msg.senderName && msg.senderName.toLowerCase().includes(keyword)) ||
      (msg.reply && msg.reply.toLowerCase().includes(keyword))
    )
  }
  
  // 按时间倒序排序
  return result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

// 加载消息列表
const loadMessages = async () => {
  try {
    loading.value = true
    const userId = localStorage.getItem('userId')
    if (!userId) return
    
    console.log('开始加载教师消息，userId:', userId)
    const messagesData = await teacherAPI.getMessages(userId)
    console.log('消息API响应:', messagesData)
    
    // 直接使用返回的数据，因为响应拦截器已经处理了data字段
    if (Array.isArray(messagesData)) {
      messages.value = messagesData
      console.log('消息数据:', messagesData)
    } else {
      messages.value = []
      console.log('消息数据格式不正确:', messagesData)
    }
  } catch (error) {
    console.error('加载消息失败:', error)
    ElMessage.error('加载消息失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 加载未读消息数
const loadUnreadCount = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) return
    
    console.log('开始加载未读消息数，userId:', userId)
    const unreadData = await teacherAPI.getUnreadCount(userId)
    console.log('未读消息数API响应:', unreadData)
    
    // 直接使用返回的数据
    if (typeof unreadData === 'number') {
      unreadCount.value = unreadData
      console.log('未读消息数:', unreadData)
    } else {
      unreadCount.value = 0
      console.log('未读消息数格式不正确:', unreadData)
    }
  } catch (error) {
    console.error('加载未读消息数失败:', error)
  }
}

// 标记消息为已读
const markAsRead = async (messageId) => {
  try {
    await teacherAPI.markMessageAsRead(messageId)
    const message = messages.value.find(m => m.id === messageId)
    if (message) {
      message.status = 1 // 1表示已读
    }
    await loadUnreadCount()
    ElMessage.success('标记成功')
  } catch (error) {
    console.error('标记消息为已读失败:', error)
    ElMessage.error('标记失败，请稍后重试')
  }
}

// 标记所有消息为已读
const markAllAsRead = async () => {
  try {
    loading.value = true
    const userId = localStorage.getItem('userId')
    if (!userId) return
    
    // 这里需要后端提供批量标记已读的API
    // 暂时遍历所有未读消息并标记
    const unreadMessages = messages.value.filter(m => m.status === 0)
    for (const message of unreadMessages) {
      await teacherAPI.markMessageAsRead(message.id)
      message.status = 1 // 1表示已读
    }
    
    await loadUnreadCount()
    ElMessage.success('全部标记为已读')
  } catch (error) {
    console.error('标记所有消息为已读失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 刷新消息
const refreshMessages = async () => {
  await loadMessages()
  await loadUnreadCount()
  ElMessage.success('消息已刷新')
}

// 搜索消息
const handleSearch = () => {
  // 搜索逻辑已在computed属性中实现
}

// 显示回复对话框
const showReplyDialog = (message) => {
  currentMessage.value = message
  replyForm.value = { content: '' }
  replyDialogVisible.value = true
}

// 提交回复
const submitReply = async () => {
  if (!replyForm.value.content.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  try {
    submittingReply.value = true
    // 这里需要后端提供回复消息的API
    // 暂时模拟回复功能
    const message = messages.value.find(m => m.id === currentMessage.value.id)
    if (message) {
      message.reply = replyForm.value.content
      message.status = 1 // 1表示已读
    }
    
    replyDialogVisible.value = false
    ElMessage.success('回复成功')
  } catch (error) {
    console.error('回复消息失败:', error)
    ElMessage.error('回复失败，请稍后重试')
  } finally {
    submittingReply.value = false
  }
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadMessages()
  loadUnreadCount()
})
</script>

<style scoped>
.teacher-messages {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.messages-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.unread-badge {
  font-size: 12px;
  padding: 0 8px;
  height: 20px;
  line-height: 20px;
}

.header-right {
  display: flex;
  gap: 12px;
}

.messages-filter {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.filter-select {
  width: 120px;
}

.search-input {
  flex: 1;
  max-width: 400px;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.el-card {
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
}

.el-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
  transform: translateY(-2px);
}

.unread {
  border-left: 4px solid #f56c6c;
}

.unread-avatar {
  background-color: #f56c6c !important;
  color: white !important;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.message-sender {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sender-info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.message-time {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.message-status {
  margin-top: 4px;
}

.message-content {
  margin-bottom: 16px;
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  word-break: break-word;
}

.message-reply {
  margin-top: 12px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 6px;
  border-left: 3px solid #409eff;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  font-size: 12px;
  color: #409eff;
  font-weight: 500;
}

.message-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.empty-messages {
  margin-top: 80px;
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .teacher-messages {
    padding: 16px;
  }
  
  .messages-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .header-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .messages-filter {
    flex-direction: column;
    gap: 12px;
  }
  
  .filter-select,
  .search-input {
    width: 100% !important;
    max-width: none !important;
  }
  
  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .message-actions {
    align-self: flex-end;
  }
}
</style>