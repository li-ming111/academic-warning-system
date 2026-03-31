<template>
  <div class="messages-container">
    <el-row :gutter="20">
      <el-col :xs="24" :md="6">
        <el-card class="unread-card">
          <template #header>
            <span>未读消息</span>
          </template>
          <div class="unread-count">{{ unreadCount }}</div>
          <el-button type="primary" block @click="loadMessages" style="margin-top: 20px;">刷新</el-button>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>沟通记录</span>
              <el-button type="primary" @click="openSendDialog">发送消息</el-button>
            </div>
          </template>

          <el-table :data="messages" stripe max-height="600">
            <el-table-column prop="content" label="内容" min-width="300"></el-table-column>
            <el-table-column label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getTypeColor(row.type)">{{ getTypeName(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusColor(row.status)">{{ getStatusName(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button 
                  v-if="row.status === 'unread'"
                  link 
                  type="primary" 
                  size="small" 
                  @click="markAsRead(row)">
                  标记已读
                </el-button>
                <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 发送消息对话框 -->
    <el-dialog v-model="sendDialogVisible" title="发送消息" width="600px">
      <el-form :model="messageForm" label-width="100px">
        <el-form-item label="教师">
          <el-select v-model="messageForm.teacherId" placeholder="选择教师" @change="handleTeacherChange">
            <el-option 
              v-for="teacher in teachers" 
              :key="teacher.id" 
              :label="teacher.name" 
              :value="teacher.id">
              {{ teacher.name }} (ID: {{ teacher.id }})
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="消息类型">
          <el-select v-model="messageForm.type" placeholder="选择消息类型">
            <el-option label="提问" value="question"></el-option>
            <el-option label="反馈" value="feedback"></el-option>
            <el-option label="请求" value="request"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="消息内容">
          <el-input 
            v-model="messageForm.content" 
            type="textarea" 
            :rows="4"
            placeholder="请输入消息内容">
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sendMessage">发送</el-button>
      </template>
    </el-dialog>

    <!-- 消息详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="消息详情" width="600px">
      <div v-if="selectedMessage" class="message-detail">
        <div class="detail-item">
          <span class="label">消息内容：</span>
          <div class="value-text">{{ selectedMessage.content }}</div>
        </div>
        <div class="detail-item">
          <span class="label">消息类型：</span>
          <el-tag :type="getTypeColor(selectedMessage.type)">{{ getTypeName(selectedMessage.type) }}</el-tag>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <el-tag :type="getStatusColor(selectedMessage.status)">{{ getStatusName(selectedMessage.status) }}</el-tag>
        </div>
        <div class="detail-item">
          <span class="label">发送时间：</span>
          <span class="value">{{ formatDate(selectedMessage.createdAt) }}</span>
        </div>
        <div v-if="selectedMessage.reply" class="detail-item">
          <span class="label">教师回复：</span>
          <div class="value-text">{{ selectedMessage.reply }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { studentAPI, teacherAPI } from '../api/index'
import { getUserId } from '@/utils/userUtils'

const messages = ref([])
const unreadCount = ref(0)
const sendDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedMessage = ref(null)
const teachers = ref([])

const messageForm = ref({
  teacherId: null,
  type: '',
  content: ''
})

const getTypeColor = (type) => {
  const map = {
    'question': 'info',
    'feedback': 'warning',
    'request': 'primary',
    'other': 'info'
  }
  return map[type] || 'info'
}

const getTypeName = (type) => {
  const map = {
    'question': '提问',
    'feedback': '反馈',
    'request': '请求',
    'other': '其他'
  }
  return map[type] || type
}

const getStatusColor = (status) => {
  const map = {
    0: 'danger',
    1: 'warning',
    2: 'success'
  }
  return map[status] || 'info'
}

const getStatusName = (status) => {
  const map = {
    0: '未读',
    1: '已读',
    2: '已回复'
  }
  return map[status] || status
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const loadMessages = async () => {
  try {
    const userId = getUserId()
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }

    // 加载消息列表
    console.log('开始加载消息，userId:', userId)
    const messagesResponse = await studentAPI.getMessages(userId)
    console.log('消息API响应:', messagesResponse)
    if (Array.isArray(messagesResponse)) {
      console.log('消息数据:', messagesResponse)
      messages.value = messagesResponse
    } else {
      console.log('消息数据格式不正确:', messagesResponse)
    }

    // 加载未读数
    const unreadResponse = await studentAPI.getUnreadCount(userId)
    console.log('未读消息数API响应:', unreadResponse)
    if (typeof unreadResponse === 'number') {
      console.log('未读消息数:', unreadResponse)
      unreadCount.value = unreadResponse
    } else {
      console.log('未读消息数格式不正确:', unreadResponse)
    }
  } catch (error) {
    console.error('加载消息失败:', error)
    ElMessage.error('加载消息失败')
  }
}

const loadTeachers = async () => {
  try {
    // 调用真实的API获取教师列表
    const response = await teacherAPI.getTeachers()
    if (Array.isArray(response)) {
      teachers.value = response.map(teacher => ({
        id: teacher.id,
        name: teacher.name || teacher.username
      }))
    }
  } catch (error) {
    console.error('加载教师列表失败:', error)
    // 加载失败时使用模拟数据作为 fallback
    teachers.value = [
      { id: 2, name: '张老师' },
      { id: 3, name: '李老师' },
      { id: 4, name: '王老师' }
    ]
  }
}

const openSendDialog = () => {
  messageForm.value = {
    teacherId: null,
    type: '',
    content: ''
  }
  loadTeachers()
  sendDialogVisible.value = true
}

const handleTeacherChange = (value) => {
  console.log('选择的教师ID:', value)
}

const sendMessage = async () => {
  console.log('发送按钮被点击')
  console.log('messageForm:', messageForm.value)
  
  if (!messageForm.value.teacherId) {
    console.log('教师未选择')
    ElMessage.warning('请选择教师')
    return
  }
  if (!messageForm.value.type) {
    console.log('消息类型未选择')
    ElMessage.warning('请选择消息类型')
    return
  }
  if (!messageForm.value.content) {
    console.log('消息内容为空')
    ElMessage.warning('请输入消息内容')
    return
  }

  try {
    const userId = localStorage.getItem('userId')
    console.log('获取到的userId:', userId)
    
    // 获取学生档案信息，使用正确的studentId
    const studentInfo = await studentAPI.getStudentInfoByUserId(userId)
    console.log('获取到的学生信息:', studentInfo)
    
    const studentId = studentInfo.id
    console.log('使用正确的studentId:', studentId)
    
    const data = {
      teacherId: Number(messageForm.value.teacherId),
      content: messageForm.value.content,
      studentId: Number(studentId),
      status: 0 // 设置消息状态为未读（0=未读）
    }
    console.log('发送的数据:', data)
    
    console.log('调用studentAPI.sendMessage')
    const result = await studentAPI.sendMessage(data)
    console.log('API响应结果:', result)
    
    // 处理不同类型的响应
    if (typeof result === 'string' && result.includes('消息已发送')) {
      console.log('消息发送成功')
      ElMessage.success('消息已发送')
      sendDialogVisible.value = false
      loadMessages()
    } else if (result && result.code === 200) {
      console.log('消息发送成功')
      ElMessage.success('消息已发送')
      sendDialogVisible.value = false
      loadMessages()
    } else if (result && result.data && result.data.includes('消息已发送')) {
      console.log('消息发送成功')
      ElMessage.success('消息已发送')
      sendDialogVisible.value = false
      loadMessages()
    } else {
      console.log('API响应格式不正确')
      ElMessage.error('发送失败：响应格式不正确')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  }
}

const markAsRead = async (message) => {
  try {
    const result = await studentAPI.markMessageAsRead(message.id)
    if (result && result.code === 200) {
      ElMessage.success('已标记为已读')
      message.status = 1
      loadMessages()
    }
  } catch (error) {
    console.error('标记为已读失败:', error)
    ElMessage.error('标记为已读失败')
  }
}

const viewDetail = (message) => {
  selectedMessage.value = message
  detailDialogVisible.value = true
}

onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
.messages-container {
  padding: 20px;
}

.unread-card {
  text-align: center;
}

.unread-count {
  font-size: 36px;
  font-weight: bold;
  color: #f56c6c;
  margin: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-detail {
  padding: 20px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.detail-item .label {
  font-weight: bold;
  width: 100px;
  flex-shrink: 0;
}

.detail-item .value {
  flex: 1;
  word-break: break-word;
}

.detail-item .value-text {
  flex: 1;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
}
</style>
