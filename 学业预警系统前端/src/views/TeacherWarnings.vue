<template>
  <div class="teacher-warnings">
    <div class="page-header">
      <h1>学业预警管理</h1>
      <p>处理学生预警、帮扶和沟通记录</p>
    </div>

    <!-- 预警统计 -->
    <div class="stats-grid">
      <div class="stat-card red">
        <div class="stat-number">{{ warningStats.highWarnings || 0 }}</div>
        <div class="stat-label">红色预警</div>
      </div>
      <div class="stat-card yellow">
        <div class="stat-number">{{ warningStats.mediumWarnings || 0 }}</div>
        <div class="stat-label">黄色预警</div>
      </div>
      <div class="stat-card blue">
        <div class="stat-number">{{ warningStats.lowWarnings || 0 }}</div>
        <div class="stat-label">蓝色预警</div>
      </div>
      <div class="stat-card gray">
        <div class="stat-number">{{ warningStats.processedWarnings || 0 }}</div>
        <div class="stat-label">已处理</div>
      </div>
    </div>

    <!-- 预警列表 -->
    <el-card>
      <template #header>
        <div class="card-header">预警学生列表</div>
      </template>

      <el-tabs>
        <el-tab-pane :label="`红色预警 (${warningStats.highWarnings || 0})`">
          <div class="warning-list">
            <div v-if="warnings.high.length === 0" class="empty-state">暂无红色预警</div>
            <div v-for="warning in warnings.high" :key="warning.id" class="warning-item">
              <div class="warning-header">
                <span class="level red">红色</span>
                <span class="student-info">{{ warning.studentName }} - {{ warning.courseName }}</span>
              </div>
              <div class="warning-body">
                <p><strong>成绩：</strong>{{ warning.score }}分</p>
                <p><strong>原因：</strong>{{ warning.description }}</p>
              </div>
              <div class="warning-actions">
                <el-button type="primary" size="small" @click="handleWarning(warning)">处理</el-button>
                <el-button size="small" @click="viewDetails(warning)">详情</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane :label="`黄色预警 (${warningStats.mediumWarnings || 0})`">
          <div class="warning-list">
            <div v-if="warnings.medium.length === 0" class="empty-state">暂无黄色预警</div>
            <div v-for="warning in warnings.medium" :key="warning.id" class="warning-item">
              <div class="warning-header">
                <span class="level yellow">黄色</span>
                <span class="student-info">{{ warning.studentName }} - {{ warning.courseName }}</span>
              </div>
              <div class="warning-body">
                <p><strong>成绩：</strong>{{ warning.score }}分</p>
                <p><strong>提醒：</strong>需要关注该生成绩发展趋势</p>
              </div>
              <div class="warning-actions">
                <el-button type="warning" size="small" link @click="handleWarning(warning)">关注</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane :label="`蓝色预警 (${warningStats.lowWarnings || 0})`">
          <div v-if="warnings.low.length === 0" class="empty-state">暂无蓝色预警</div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 处理对话框 -->
    <el-dialog v-model="handleDialogVisible" title="处理预警" width="600px">
      <el-form :model="handleForm" label-width="120px">
        <el-form-item label="学生姓名">
          <el-input :value="handleForm.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="课程">
          <el-input :value="handleForm.course" disabled></el-input>
        </el-form-item>
        <el-form-item label="帮扶措施">
          <el-select v-model="handleForm.measure" placeholder="选择帮扶措施">
            <el-option label="加课辅导" value="tutoring"></el-option>
            <el-option label="定期答疑" value="qa"></el-option>
            <el-option label="学习小组" value="group"></el-option>
            <el-option label="心理疏导" value="psychology"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="沟通内容">
          <el-input v-model="handleForm.communication" type="textarea" rows="4" placeholder="记录与学生的沟通内容"></el-input>
        </el-form-item>
        <el-form-item label="跟踪进度">
          <el-input-number v-model="handleForm.progress" :min="0" :max="100" :step="10"></el-input-number>
          <span style="margin-left: 10px;">%</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认处理</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="预警详情" width="600px">
      <div class="detail-content">
        <p><strong>学生：</strong>{{ detailForm.studentName }}</p>
        <p><strong>课程：</strong>{{ detailForm.course }}</p>
        <p><strong>当前成绩：</strong>{{ detailForm.score }}</p>
        <p><strong>预警级别：</strong>{{ detailForm.level }}</p>
        <p><strong>历史沟通：</strong></p>
        <el-timeline>
          <el-timeline-item v-for="(record, index) in communicationRecords" :key="index" :timestamp="record.timestamp">
            <p>{{ record.content }}</p>
          </el-timeline-item>
          <el-timeline-item v-if="communicationRecords.length === 0">
            <p>暂无沟通记录</p>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { teacherAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const handleDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const warningStats = ref({
  highWarnings: 0,
  mediumWarnings: 0,
  lowWarnings: 0,
  processedWarnings: 0
})
const warnings = ref({
  high: [],
  medium: [],
  low: []
})

const handleForm = ref({
  warningId: '',
  studentName: '',
  course: '',
  measure: '',
  communication: '',
  progress: 0
})

const detailForm = ref({
  studentName: '',
  course: '',
  score: '',
  level: ''
})

const communicationRecords = ref([])

onMounted(async () => {
  await loadWarnings()
})

// 加载预警数据
const loadWarnings = async () => {
  try {
    const teacherId = localStorage.getItem('teacherId') || getUserId()
    if (!teacherId) return
    const response = await teacherAPI.getWarnings(teacherId)
    if (response && Array.isArray(response)) {
      // 分类预警
      const high = response.filter(w => w.warningLevel === 'high')
      const medium = response.filter(w => w.warningLevel === 'medium')
      const low = response.filter(w => w.warningLevel === 'low')
      
      warnings.value = { high, medium, low }
      warningStats.value = {
        highWarnings: high.length,
        mediumWarnings: medium.length,
        lowWarnings: low.length,
        processedWarnings: response.filter(w => w.status === 'processed').length
      }
    }
  } catch (error) {
    console.error('加载预警失败:', error)
  }
}

// 处理预警
const handleWarning = (warning) => {
  handleForm.value = {
    warningId: warning.id,
    studentName: warning.studentName,
    course: warning.courseName,
    measure: '',
    communication: '',
    progress: 0
  }
  handleDialogVisible.value = true
}

// 查看详情
const viewDetails = async (warning) => {
  detailForm.value = {
    studentName: warning.studentName,
    course: warning.courseName,
    score: warning.score,
    level: warning.level
  }
  // 加载沟通记录
  communicationRecords.value = [] // 暂时设置为空数组
  detailDialogVisible.value = true
}

// 提交预警处理
const submitHandle = async () => {
  try {
    if (!handleForm.value.measure) {
      ElMessage.error('请选择帮扶措施')
      return
    }
    
    const userId = getUserId()
    
    // 保存沟通记录
    if (handleForm.value.communication) {
      await teacherAPI.saveCommunication({
        teacherId: userId,
        studentId: handleForm.value.warningId,
        content: handleForm.value.communication
      })
    }
    
    // 处理预警
    await teacherAPI.processWarning(handleForm.value.warningId, {
      measures: handleForm.value.measure
    })
    
    ElMessage.success('预警已处理，沟通记录已保存')
    handleDialogVisible.value = false
    await loadWarnings()
  } catch (error) {
    console.error('处理预警失败:', error)
    ElMessage.error('处理预警失败')
  }
}
</script>

<style scoped>
.teacher-warnings {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 28px;
  padding: 32px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 16px;
  color: white;
  box-shadow: 0 8px 24px rgba(79, 172, 254, 0.4);
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
  border-top: 4px solid;
}

.stat-card.red { border-top-color: #f56c6c; }
.stat-card.yellow { border-top-color: #e6a23c; }
.stat-card.blue { border-top-color: #409eff; }
.stat-card.gray { border-top-color: #909399; }

.stat-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  transform: translateY(-8px);
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.warning-item {
  background: white;
  padding: 20px;
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

.warning-item:hover {
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
  border-left-color: #764ba2;
}

.warning-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.level {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  display: inline-block;
}

.level.red {
  background: linear-gradient(135deg, #f56c6c 0%, #ef4040 100%);
}

.level.yellow {
  background: linear-gradient(135deg, #e6a23c 0%, #d89216 100%);
}

.student-info {
  font-weight: 600;
  color: #333;
  font-size: 15px;
}

.warning-body {
  margin-bottom: 16px;
}

.warning-body p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.warning-body strong {
  color: #333;
  font-weight: 600;
}

.warning-actions {
  display: flex;
  gap: 8px;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: #999;
}

.detail-content {
  line-height: 1.8;
}

.detail-content p {
  margin: 12px 0;
  color: #333;
  font-size: 15px;
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

:deep(.el-tabs__header) {
  background: linear-gradient(135deg, #f8f9fa 0%, #e8ecf1 100%);
  border-radius: 12px;
  padding: 8px;
}

:deep(.el-tabs__nav-wrap::after) {
  background: none;
}

:deep(.el-button) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
}
</style>
