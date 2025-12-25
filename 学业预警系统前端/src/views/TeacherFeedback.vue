<template>
  <div class="teacher-feedback">
    <div class="page-header">
      <h1>💬 学生反馈管理</h1>
      <p>查看、分类和回复学生课程反馈</p>
    </div>

    <!-- 反馈统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-number">24</div>
        <div class="stat-label">总反馈数</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">8</div>
        <div class="stat-label">待回复</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">4.5</div>
        <div class="stat-label">平均评分</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">88%</div>
        <div class="stat-label">回复率</div>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar">
      <el-select v-model="filterCategory" placeholder="反馈分类">
        <el-option label="全部分类" value="all"></el-option>
        <el-option label="教学方法" value="teaching"></el-option>
        <el-option label="课程内容" value="content"></el-option>
        <el-option label="作业布置" value="homework"></el-option>
        <el-option label="其他" value="other"></el-option>
      </el-select>
      <el-select v-model="filterStatus" placeholder="状态">
        <el-option label="全部状态" value="all"></el-option>
        <el-option label="待回复" value="pending"></el-option>
        <el-option label="已回复" value="replied"></el-option>
      </el-select>
    </div>

    <!-- 反馈列表 -->
    <el-card>
      <template #header>
        <div class="card-header">📝 反馈列表</div>
      </template>

      <div class="feedback-list">
        <div class="feedback-item" v-for="i in 5" :key="i">
          <div class="feedback-header">
            <div class="feedback-info">
              <h4 class="student-name">{{ i === 1 ? '李明' : i === 2 ? '王红' : i === 3 ? '张凯' : i === 4 ? '陈杰' : '刘芳' }}</h4>
              <div class="feedback-meta">
                <el-tag :type="i % 2 === 1 ? 'warning' : 'success'" size="small">{{ i % 2 === 1 ? '教学方法' : '课程内容' }}</el-tag>
                <span class="feedback-time">{{ i }}小时前</span>
                <el-rate v-model="feedbackRating[i-1]" :max="5" size="small" disabled></el-rate>
              </div>
            </div>
            <el-button v-if="i % 2 === 1" type="danger" size="small" link>待回复</el-button>
            <el-button v-else type="success" size="small" link>已回复</el-button>
          </div>

          <div class="feedback-content">
            <p v-if="i === 1">教学进度太快，很多内容没有时间消化，希望能放慢速度或增加复习时间。</p>
            <p v-else-if="i === 2">讲授内容很有深度，感谢老师的耐心讲解和案例分析。</p>
            <p v-else-if="i === 3">作业量有些大，平时要花很多时间完成，建议适度减少。</p>
            <p v-else-if="i === 4">实践课程很有价值，希望未来能增加更多实战项目。</p>
            <p v-else>希望能提供更多学习资料和参考文献供我们深入学习。</p>
          </div>

          <div class="feedback-actions">
            <el-button v-if="i % 2 === 1" type="primary" @click="replyFeedback(i)">回复</el-button>
            <el-button type="info" size="small" link @click="viewFeedback(i)">详情</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="回复反馈" width="600px">
      <el-form :model="replyForm" label-width="100px">
        <el-form-item label="学生">
          <el-input :value="replyForm.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input :value="replyForm.content" type="textarea" disabled rows="3"></el-input>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="replyForm.reply" type="textarea" rows="4" placeholder="请输入您的回复..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">发送回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { teacherAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const filterCategory = ref('all')
const filterStatus = ref('all')
const replyDialogVisible = ref(false)
const feedbackList = ref([])
const feedbackRating = ref([5, 4, 5, 4, 5])
const feedbackStats = ref({
  totalFeedback: 0,
  pendingReply: 0,
  averageRating: 0,
  replyRate: 0
})

const replyForm = ref({
  feedbackId: '',
  studentName: '',
  content: '',
  reply: ''
})

onMounted(async () => {
  await loadFeedback()
})

// 加载反馈数据
const loadFeedback = async () => {
  try {
    const userId = getUserId()
    if (!userId) return
    const response = await teacherAPI.getFeedback(userId)
    if (response) {
      feedbackList.value = response.feedbacks || []
      feedbackStats.value = response.stats
    }
  } catch (error) {
    console.error('加载反馈失败:', error)
  }
}

const replyFeedback = (feedback) => {
  replyForm.value = {
    feedbackId: feedback.id,
    studentName: feedback.studentName,
    content: feedback.content,
    reply: ''
  }
  replyDialogVisible.value = true
}

const submitReply = async () => {
  if (!replyForm.value.reply) {
    ElMessage.error('请输入回复内容')
    return
  }
  try {
    const data = {
      feedbackId: replyForm.value.feedbackId,
      reply: replyForm.value.reply
    }
    await teacherAPI.replyFeedback(data)
    ElMessage.success('回复已发送')
    replyDialogVisible.value = false
    await loadFeedback()
  } catch (error) {
    console.error('发送回复失败:', error)
    ElMessage.error('发送回复失败')
  }
}

const viewFeedback = (id) => {
  ElMessage.info('查看反馈详情')
}
</script>

<style scoped>
.teacher-feedback {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 28px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
  animation: slideDown 0.6s ease-out;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-header h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: bold;
  color: white;
}

.page-header p {
  margin: 0;
  font-size: 14px;
  opacity: 0.95;
  color: white;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-top: 4px solid #667eea;
}

.stat-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  transform: translateY(-8px);
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
}

.stat-label {
  color: #999;
  font-size: 13px;
  font-weight: 500;
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  align-items: center;
  flex-wrap: wrap;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-item {
  padding: 20px;
  background: white;
  border-radius: 16px;
  border-left: 4px solid #667eea;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}

.feedback-item:hover {
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
  border-left-color: #764ba2;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.feedback-info {
  flex: 1;
}

.student-name {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.feedback-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.feedback-time {
  color: #999;
}

.feedback-content {
  margin-bottom: 16px;
  color: #666;
  line-height: 1.6;
  font-size: 15px;
}

.feedback-content p {
  margin: 0;
}

.feedback-actions {
  display: flex;
  gap: 8px;
}

:deep(.el-card) {
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

:deep(.el-card:hover) {
  border-color: #e8ecf1;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:deep(.el-button) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
}
</style>
