<template>
  <div class="teacher-audit-log">
    <div class="page-header">
      <h1>📋 成绩修改审计日志</h1>
      <p>查看成绩修改历史和变更记录</p>
    </div>

    <!-- 统计信息 -->
    <div class="stats-grid">
      <div class="stat-box">
        <div class="stat-title">总修改数</div>
        <div class="stat-value">{{ totalLogs }}</div>
        <div class="stat-hint">条</div>
      </div>
      <div class="stat-box">
        <div class="stat-title">平均变化</div>
        <div class="stat-value">{{ avgChange }}</div>
        <div class="stat-hint">分</div>
      </div>
      <div class="stat-box">
        <div class="stat-title">最大提升</div>
        <div class="stat-value" style="color: #67c23a;">+{{ maxIncrease }}</div>
        <div class="stat-hint">分</div>
      </div>
      <div class="stat-box">
        <div class="stat-title">最大下降</div>
        <div class="stat-value" style="color: #f56c6c;">{{ minDecrease }}</div>
        <div class="stat-hint">分</div>
      </div>
    </div>

    <!-- 修改日志表格 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">📝 成绩修改记录</div>
      </template>

      <el-table :data="scoreLogs" stripe v-loading="loading">
        <el-table-column prop="studentName" label="学生名称" width="120"></el-table-column>
        <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
        <el-table-column prop="oldScore" label="原成绩" width="100">
          <template #default="{ row }">
            <span style="color: #999;">{{ row.oldScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="newScore" label="新成绩" width="100">
          <template #default="{ row }">
            <span style="color: #333; font-weight: bold;">{{ row.newScore }}</span>
          </template>
        </el-table-column>
        <el-table-column label="变化" width="100">
          <template #default="{ row }">
            <el-tag :type="row.difference > 0 ? 'success' : (row.difference < 0 ? 'danger' : 'info')">
              {{ row.difference > 0 ? '+' : '' }}{{ row.difference }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="modifiedAt" label="修改时间" width="180"></el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="totalLogs"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px;"
        @size-change="handlePageSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="修改详情" width="600px">
      <div v-if="selectedLog" class="log-detail">
        <p><strong>学生名称：</strong>{{ selectedLog.studentName }}</p>
        <p><strong>学号：</strong>{{ selectedLog.studentId }}</p>
        <p><strong>课程ID：</strong>{{ selectedLog.courseId }}</p>
        <p><strong>原成绩：</strong>{{ selectedLog.oldScore }} 分</p>
        <p><strong>新成绩：</strong>{{ selectedLog.newScore }} 分</p>
        <p><strong>变化：</strong>
          <el-tag :type="selectedLog.difference > 0 ? 'success' : (selectedLog.difference < 0 ? 'danger' : 'info')">
            {{ selectedLog.difference > 0 ? '+' : '' }}{{ selectedLog.difference }} 分
          </el-tag>
        </p>
        <p><strong>修改时间：</strong>{{ selectedLog.modifiedAt }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { teacherAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'
import { ElMessage } from 'element-plus'

const scoreLogs = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const totalLogs = ref(0)
const detailDialogVisible = ref(false)
const selectedLog = ref(null)

// 计算统计信息
const avgChange = computed(() => {
  if (scoreLogs.value.length === 0) return 0
  const sum = scoreLogs.value.reduce((acc, log) => acc + log.difference, 0)
  return (sum / scoreLogs.value.length).toFixed(2)
})

const maxIncrease = computed(() => {
  if (scoreLogs.value.length === 0) return 0
  return Math.max(...scoreLogs.value.map(log => Math.max(0, log.difference)))
})

const minDecrease = computed(() => {
  if (scoreLogs.value.length === 0) return 0
  const decreases = scoreLogs.value.filter(log => log.difference < 0).map(log => log.difference)
  return decreases.length > 0 ? Math.min(...decreases) : 0
})

onMounted(async () => {
  await loadScoreLogs()
})

// 加载成绩日志
const loadScoreLogs = async () => {
  loading.value = true
  try {
    const userId = getUserId()
    const teacherId = localStorage.getItem('teacherId') || userId
    if (!teacherId) return
    
    const response = await teacherAPI.getScoreLogs(teacherId, currentPage.value, pageSize.value)
    if (Array.isArray(response)) {
      scoreLogs.value = response
      totalLogs.value = response.length
    }
  } catch (error) {
    console.error('加载成绩日志失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handlePageChange = () => {
  loadScoreLogs()
}

const handlePageSizeChange = () => {
  currentPage.value = 1
  loadScoreLogs()
}

// 查看详情
const viewDetail = (row) => {
  selectedLog.value = row
  detailDialogVisible.value = true
}
</script>

<style scoped>
.teacher-audit-log {
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

.log-detail {
  line-height: 1.8;
}

.log-detail p {
  margin: 12px 0;
  color: #333;
  font-size: 15px;
}

:deep(.el-card) {
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
  transition: all 0.3s ease;
  margin-top: 24px !important;
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
