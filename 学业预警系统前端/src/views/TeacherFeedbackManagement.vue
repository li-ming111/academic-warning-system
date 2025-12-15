<template>
  <div class="teacher-feedback-management">
    <div class="page-header">
      <h1>💬 学生反馈管理</h1>
      <p>查看和回复学生反馈，管理反馈分类</p>
    </div>

    <!-- 筛选 -->
    <div class="action-bar">
      <el-select v-model="selectedCategory" placeholder="选择分类" @change="loadFeedbacks" style="width: 200px;">
        <el-option label="全部" value=""></el-option>
        <el-option label="教学质量" value="教学质量"></el-option>
        <el-option label="成绩问题" value="成绩问题"></el-option>
        <el-option label="作业评分" value="作业评分"></el-option>
        <el-option label="考试安排" value="考试安排"></el-option>
        <el-option label="其他" value="其他"></el-option>
      </el-select>
    </div>

    <!-- 统计信息 -->
    <div class="stats-grid">
      <div class="stat-box">
        <div class="stat-title">总反馈数</div>
        <div class="stat-value">{{ feedbacks.length }}</div>
        <div class="stat-hint">条</div>
      </div>
      <div class="stat-box">
        <div class="stat-title">待回复</div>
        <div class="stat-value" style="color: #e6a23c;">{{ pendingCount }}</div>
        <div class="stat-hint">条</div>
      </div>
      <div class="stat-box">
        <div class="stat-title">已回复</div>
        <div class="stat-value" style="color: #67c23a;">{{ repliedCount }}</div>
        <div class="stat-hint">条</div>
      </div>
      <div class="stat-box">
        <div class="stat-title">回复率</div>
        <div class="stat-value">{{ replyRate }}%</div>
        <div class="stat-hint">百分比</div>
      </div>
    </div>

    <!-- 反馈列表 -->
    <el-card>
      <template #header>
        <div class="card-header">📋 反馈列表</div>
      </template>

      <el-table :data="feedbacks" stripe v-loading="loading">
        <el-table-column prop="studentName" label="学生" width="120"></el-table-column>
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="反馈内容" min-width="250">
          <template #default="{ row }">
            <div class="feedback-content">{{ row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'replied' ? 'success' : 'warning'">
              {{ row.status === 'replied' ? '已回复' : '待回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              link 
              @click="openReplyDialog(row)"
              v-if="row.status === 'pending'"
            >回复</el-button>
            <el-button 
              type="info" 
              size="small" 
              link 
              @click="viewReply(row)"
              v-else
            >查看回复</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="totalFeedbacks"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px;"
        @size-change="handlePageSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="回复反馈" width="700px">
      <div v-if="selectedFeedback" class="reply-dialog">
        <div class="feedback-info">
          <p><strong>学生：</strong>{{ selectedFeedback.studentName }}</p>
          <p><strong>分类：</strong><el-tag>{{ selectedFeedback.category }}</el-tag></p>
          <p><strong>反馈内容：</strong></p>
          <div class="feedback-content-box">{{ selectedFeedback.content }}</div>
        </div>

        <el-divider></el-divider>

        <div class="reply-form">
          <p><strong>回复内容：</strong></p>
          <el-input 
            v-model="replyContent" 
            type="textarea" 
            rows="5" 
            placeholder="请输入回复内容..."
          ></el-input>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply">提交回复</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看回复对话框 -->
    <el-dialog v-model="viewReplyDialogVisible" title="反馈回复" width="700px">
      <div v-if="selectedFeedback" class="reply-view">
        <p><strong>学生：</strong>{{ selectedFeedback.studentName }}</p>
        <p><strong>分类：</strong><el-tag>{{ selectedFeedback.category }}</el-tag></p>
        <p><strong>反馈内容：</strong></p>
        <div class="feedback-content-box">{{ selectedFeedback.content }}</div>

        <el-divider></el-divider>

        <p><strong>教师回复：</strong></p>
        <div class="reply-content-box">{{ selectedFeedback.reply_content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { teacherAPI } from '@/api/index'
import { ElMessage } from 'element-plus'

const feedbacks = ref([])
const loading = ref(false)
const selectedCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const totalFeedbacks = ref(0)
const replyDialogVisible = ref(false)
const viewReplyDialogVisible = ref(false)
const selectedFeedback = ref(null)
const replyContent = ref('')

// 计算统计信息
const pendingCount = computed(() => 
  feedbacks.value.filter(f => f.status === 'pending').length
)

const repliedCount = computed(() => 
  feedbacks.value.filter(f => f.status === 'replied').length
)

const replyRate = computed(() => {
  if (feedbacks.value.length === 0) return 0
  return Math.round((repliedCount.value / feedbacks.value.length) * 100)
})

onMounted(async () => {
  await loadFeedbacks()
})

// 加载反馈列表
const loadFeedbacks = async () => {
  loading.value = true
  try {
    const userId = localStorage.getItem('userId')
    const teacherId = localStorage.getItem('teacherId') || userId
    if (!teacherId) return
    
    const response = await teacherAPI.getFeedbackList(
      teacherId, 
      selectedCategory.value || undefined, 
      currentPage.value, 
      pageSize.value
    )
    if (Array.isArray(response)) {
      feedbacks.value = response
      totalFeedbacks.value = response.length
    }
  } catch (error) {
    console.error('加载反馈列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handlePageChange = () => {
  loadFeedbacks()
}

const handlePageSizeChange = () => {
  currentPage.value = 1
  loadFeedbacks()
}

// 打开回复对话框
const openReplyDialog = (row) => {
  selectedFeedback.value = row
  replyContent.value = ''
  replyDialogVisible.value = true
}

// 提交回复
const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    await teacherAPI.replyToFeedback(selectedFeedback.value.id, {
      reply_content: replyContent.value
    })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    await loadFeedbacks()
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  }
}

// 查看回复
const viewReply = (row) => {
  selectedFeedback.value = row
  viewReplyDialogVisible.value = true
}
</script>

<style scoped>
.teacher-feedback-management {
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

.action-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  align-items: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-box {
  background: white;
  padding: 24px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-top: 4px solid #667eea;
}

.stat-box:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  transform: translateY(-8px);
}

.stat-title {
  color: #999;
  font-size: 13px;
  margin-bottom: 10px;
  font-weight: 500;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 6px;
}

.stat-hint {
  color: #999;
  font-size: 12px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.feedback-content {
  color: #666;
  word-wrap: break-word;
  white-space: pre-wrap;
  max-width: 100%;
}

.reply-dialog, .reply-view {
  padding: 10px 0;
}

.feedback-info, .reply-view {
  margin-bottom: 20px;
}

.feedback-info p, .reply-view p {
  margin: 12px 0;
  color: #333;
  font-size: 15px;
}

.feedback-content-box, .reply-content-box {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 12px;
  border-left: 4px solid #667eea;
  margin: 12px 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.reply-form {
  margin-bottom: 20px;
}

.reply-form p {
  margin: 12px 0;
  font-weight: 600;
  color: #333;
}

.dialog-footer {
  text-align: right;
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

:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: linear-gradient(135deg, #f8f9fa 0%, #e8ecf1 100%);
  color: #333;
  font-weight: 600;
}

:deep(.el-button) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
