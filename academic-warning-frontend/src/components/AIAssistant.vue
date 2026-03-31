<template>
  <div class="ai-assistant">
    <!-- 聊天按钮 -->
    <div class="chat-button" @click="toggleChat">
      <div class="robot-icon">
        <div class="robot-head">
          <div class="robot-eyes"></div>
          <div class="robot-mouth"></div>
        </div>
        <div class="robot-antenna"></div>
      </div>
      <span class="badge" v-if="unreadMessages > 0">{{ unreadMessages }}</span>
    </div>

    <!-- 聊天窗口 -->
    <div class="chat-window" v-if="isChatOpen">
      <div class="chat-header">
        <div class="chat-title">
          <div class="avatar">
            <i class="el-icon-robot"></i>
          </div>
          <span>学业预警系统AI助手</span>
        </div>
        <div class="chat-actions">
          <button class="action-btn minimize-btn" @click="toggleChat">
            <span>—</span>
          </button>
          <button class="action-btn close-btn" @click="toggleChat">
            <span>×</span>
          </button>
        </div>
      </div>

      <div class="chat-body" ref="chatBody">
        <!-- 主聊天区 -->
        <div class="chat-main" ref="chatMain">
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="message-content">
              <p>你好，我是学业预警系统的AI助手</p>
              <p>我可以帮你查询成绩、了解预警信息、计算GPA等学业相关问题</p>
              <div class="suggestions">
                <button class="suggestion-btn" @click="sendSuggestion('我的成绩如何')">我的成绩如何</button>
                <button class="suggestion-btn" @click="sendSuggestion('我有哪些预警')">我有哪些预警</button>
                <button class="suggestion-btn" @click="sendSuggestion('如何计算GPA')">如何计算GPA</button>
              </div>
            </div>
          </div>

          <!-- 消息列表 -->
          <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
            <div class="message-avatar">
              {{ message.type === 'user' ? username.charAt(0) : 'AI' }}
            </div>
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>

          <!-- 加载中 -->
          <div v-if="isLoading" class="loading-message">
            <div class="loading-spinner"></div>
            <span>AI正在思考...</span>
          </div>
        </div>
      </div>

      <div class="chat-footer">
        <input 
          type="text" 
          v-model="inputMessage" 
          placeholder="输入学业相关问题" 
          @keyup.enter="sendMessage"
          class="message-input"
        >
        <button @click="sendMessage" class="send-button">
          <i class="el-icon-arrow-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { aiAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const isChatOpen = ref(false)
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const unreadMessages = ref(0)
const chatBody = ref(null)
const chatMain = ref(null)
const username = ref(localStorage.getItem('userName') || '学生')

// 切换聊天窗口
const toggleChat = () => {
  isChatOpen.value = !isChatOpen.value
  if (isChatOpen.value) {
    unreadMessages.value = 0
    nextTick(() => {
      scrollToBottom()
    })
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (chatMain.value) {
    chatMain.value.scrollTop = chatMain.value.scrollHeight
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  const message = inputMessage.value.trim()
  inputMessage.value = ''

  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: message,
    time: new Date().toLocaleTimeString()
  })
  scrollToBottom()

  isLoading.value = true
  try {
    const userId = getUserId()
    if (!userId) {
      throw new Error('用户未登录')
    }
    const response = await aiAPI.getResponse(userId, message)
    
    // 添加AI回复
    messages.value.push({
      type: 'ai',
      content: response.content,
      time: new Date().toLocaleTimeString()
    })
  } catch (error) {
    console.error('AI回复失败:', error)
    messages.value.push({
      type: 'ai',
      content: '抱歉，我暂时无法回答你的问题，请稍后再试。',
      time: new Date().toLocaleTimeString()
    })
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

// 发送建议问题
const sendSuggestion = (suggestion) => {
  inputMessage.value = suggestion
  sendMessage()
}

// 清空聊天
const clearChat = () => {
  messages.value = []
}

// 监听消息变化，自动滚动
watch(messages, () => {
  nextTick(() => {
    scrollToBottom()
  })
}, { deep: true })

// 组件挂载时初始化
onMounted(() => {
  // 可以在这里加载历史消息或初始化其他数据
})
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 1000;
}

.chat-button {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #6c63ff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(108, 99, 255, 0.4);
  transition: all 0.3s ease;
  position: relative;
}

.chat-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(108, 99, 255, 0.6);
}

.robot-icon {
  position: relative;
  width: 30px;
  height: 30px;
}

.robot-head {
  width: 30px;
  height: 30px;
  background: white;
  border-radius: 50%;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.robot-eyes {
  display: flex;
  gap: 4px;
  margin-bottom: 2px;
}

.robot-eyes::before, .robot-eyes::after {
  content: '';
  width: 4px;
  height: 4px;
  background: #333;
  border-radius: 50%;
}

.robot-mouth {
  width: 8px;
  height: 2px;
  background: #333;
  border-radius: 1px;
}

.robot-antenna {
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 2px;
  height: 8px;
  background: white;
}

.robot-antenna::after {
  content: '';
  position: absolute;
  top: -4px;
  left: 50%;
  transform: translateX(-50%);
  width: 4px;
  height: 4px;
  background: #ff6b6b;
  border-radius: 50%;
}

.badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #ff6b6b;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.chat-window {
  width: 380px;
  height: 500px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.3s ease-out;
  border: 1px solid #e0e0e0;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.chat-header {
  background: #6c63ff;
  color: white;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  font-size: 16px;
  font-family: 'Microsoft YaHei', sans-serif;
}

.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c63ff;
  font-weight: bold;
}

.avatar i {
  font-size: 18px;
}

.chat-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 4px;
  color: white;
  padding: 4px 8px;
  cursor: pointer;
  transition: background 0.3s ease;
  font-size: 16px;
  line-height: 1;
}

.minimize-btn {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px 0 0 4px;
}

.close-btn {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 0 4px 4px 0;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.4);
}

.chat-body {
  flex: 1;
  background: #f8f9fa;
}

.chat-main {
  height: 100%;
  padding: 16px;
  overflow-y: auto;
}

.welcome-message {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e0e0e0;
}

.welcome-message p {
  margin: 0 0 12px 0;
  color: #333;
  line-height: 1.5;
  font-family: 'Microsoft YaHei', sans-serif;
}

.welcome-message p:last-child {
  margin-bottom: 20px;
}

.suggestions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.suggestion-btn {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  padding: 12px 16px;
  text-align: center;
  color: #6c63ff;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-family: 'Microsoft YaHei', sans-serif;
}

.suggestion-btn:hover {
  background: #f0f0ff;
  border-color: #6c63ff;
  box-shadow: 0 2px 8px rgba(108, 99, 255, 0.2);
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #6c63ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
  font-family: 'Microsoft YaHei', sans-serif;
}

.message.user .message-avatar {
  background: #4facfe;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  background: white;
  padding: 12px 16px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 4px;
  line-height: 1.4;
  font-family: 'Microsoft YaHei', sans-serif;
  border: 1px solid #e0e0e0;
}

.message.user .message-text {
  background: #6c63ff;
  color: white;
  border: none;
}

.message-time {
  font-size: 10px;
  color: #999;
  margin-top: 4px;
  font-family: 'Microsoft YaHei', sans-serif;
}

.loading-message {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
  padding: 12px;
  color: #666;
  font-family: 'Microsoft YaHei', sans-serif;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #6c63ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.chat-footer {
  padding: 16px;
  background: white;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 12px;
  align-items: center;
}

.message-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 24px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
  font-family: 'Microsoft YaHei', sans-serif;
}

.message-input:focus {
  border-color: #6c63ff;
  box-shadow: 0 0 0 2px rgba(108, 99, 255, 0.1);
}

.send-button {
  background: #6c63ff;
  border: none;
  border-radius: 50%;
  width: 44px;
  height: 44px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 18px;
  font-weight: bold;
}

.send-button:hover {
  background: #5a52d5;
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(108, 99, 255, 0.4);
}

.send-button i {
  font-size: 18px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-assistant {
    bottom: 20px;
    right: 20px;
  }
  
  .chat-window {
    width: 320px;
    height: 450px;
  }
}
</style>