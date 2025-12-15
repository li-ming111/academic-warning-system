<template>
  <div class="counselor-warnings">
    <div class="page-header">
      <h1>⚠️ 学生预警管理</h1>
      <p>班级学生预警处理和追踪</p>
    </div>

    <!-- 预警统计 -->
    <div class="stats-grid">
      <div class="stat-card red">
        <div class="stat-number">{{ warningStats.redWarnings || 0 }}</div>
        <div class="stat-label">红色预警</div>
      </div>
      <div class="stat-card yellow">
        <div class="stat-number">{{ warningStats.yellowWarnings || 0 }}</div>
        <div class="stat-label">黄色预警</div>
      </div>
      <div class="stat-card blue">
        <div class="stat-number">{{ warningStats.blueWarnings || 0 }}</div>
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

      <el-table :data="warningsList" stripe>
        <el-table-column prop="studentName" label="学生" width="100"></el-table-column>
        <el-table-column prop="className" label="班级" width="80"></el-table-column>
        <el-table-column label="预警级别" width="100">
          <template #default="{ row }">
            <el-tag :type="row.level === 'red' ? 'danger' : row.level === 'yellow' ? 'warning' : 'info'">
              {{ row.level === 'red' ? '🔴 红色' : row.level === 'yellow' ? '🟡 黄色' : '🔵 蓝色' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="预警原因" width="180"></el-table-column>
        <el-table-column label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.processed ? 'success' : 'info'">{{ row.processed ? '已处理' : '待处理' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleWarning(row)" v-if="!row.processed">处理</el-button>
            <el-button type="info" size="small" link @click="viewDetails(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 处理对话框 -->
    <el-dialog v-model="handleDialogVisible" title="处理预警" width="600px">
      <el-form :model="handleForm" label-width="120px">
        <el-form-item label="学生">
          <el-input :value="handleForm.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="处理意见">
          <el-input v-model="handleForm.opinion" type="textarea" rows="3" placeholder="记录处理意见..."></el-input>
        </el-form-item>
        <el-form-item label="分配协作">
          <el-select v-model="handleForm.assignTo" placeholder="选择协作人（如教师）">
            <el-option label="教师A" value="teacher1"></el-option>
            <el-option label="教师B" value="teacher2"></el-option>
            <el-option label="心理咨询师" value="counselor"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { counselorAPI } from '@/api/index'

const handleDialogVisible = ref(false)
const warningsList = ref([])
const warningStats = ref({
  redWarnings: 0,
  yellowWarnings: 0,
  blueWarnings: 0,
  processedWarnings: 0
})

const handleForm = ref({
  warningId: '',
  studentName: '',
  opinion: '',
  assignTo: ''
})

onMounted(async () => {
  await loadWarnings()
})

const loadWarnings = async () => {
  try {
    const userId = localStorage.getItem('userId')
    const counselorId = localStorage.getItem('counselorId') || userId
    if (!counselorId) return
    const response = await counselorAPI.getWarnings(counselorId)
    if (response && response.code === 0) {
      warningsList.value = response.data || []
    } else if (Array.isArray(response)) {
      warningsList.value = response
    }
    // 计算统计数据
    warningStats.value = {
      redWarnings: warningsList.value.filter(w => w.warning_level === 'red').length,
      yellowWarnings: warningsList.value.filter(w => w.warning_level === 'yellow').length,
      blueWarnings: warningsList.value.filter(w => w.warning_level === 'blue').length,
      processedWarnings: warningsList.value.filter(w => w.status === 'processed').length
    }
  } catch (error) {
    console.error('加载预警失败:', error)
  }
}

const handleWarning = (row) => {
  handleForm.value = {
    warningId: row.id,
    studentName: row.studentName,
    opinion: '',
    assignTo: ''
  }
  handleDialogVisible.value = true
}

const submitHandle = async () => {
  if (!handleForm.value.opinion) {
    ElMessage.error('请输入处理意见')
    return
  }
  try {
    const data = {
      measures: handleForm.value.opinion
    }
    await counselorAPI.processWarning(handleForm.value.warningId, data)
    ElMessage.success('预警已处理')
    handleDialogVisible.value = false
    await loadWarnings()
  } catch (error) {
    console.error('处理预警失败:', error)
    ElMessage.error('处理失败')
  }
}

const viewDetails = (row) => {
  ElMessage.info(`${row.studentName}的预警详情`)
}
</script>

<style scoped>
.counselor-warnings {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.stat-card {
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  color: white;
}

.stat-card.red {
  background: linear-gradient(135deg, #f56c6c, #ef4040);
}

.stat-card.yellow {
  background: linear-gradient(135deg, #e6a23c, #d89216);
}

.stat-card.blue {
  background: linear-gradient(135deg, #409eff, #1e90ff);
}

.stat-card.gray {
  background: linear-gradient(135deg, #909399, #6c7280);
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
</style>
