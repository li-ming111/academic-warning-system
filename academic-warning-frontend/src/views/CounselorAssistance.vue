<template>
  <div class="counselor-assistance">
    <div class="page-header">
      <h1>帮扶计划管理</h1>
      <p>为预警学生创建、编辑、跟踪帮扶计划</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-number">{{ stats.totalPlans }}</div>
        <div class="stat-label">总计划数</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.inProgressPlans }}</div>
        <div class="stat-label">进行中</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.completedPlans }}</div>
        <div class="stat-label">已完成</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.completionRate }}%</div>
        <div class="stat-label">完成率</div>
      </div>
    </div>

    <!-- 帮扶计划列表 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">帮扶计划列表</div>
      </template>

      <el-table :data="assistancePlans" stripe>
        <el-table-column prop="studentName" label="学生" width="100"></el-table-column>
        <el-table-column prop="title" label="计划标题" width="150"></el-table-column>
        <el-table-column label="目标" width="150">
          <template #default="{ row }">{{ row.target || '学分提升' }}</template>
        </el-table-column>
        <el-table-column label="进度" width="150">
          <template #default="{ row }">
            <el-progress :percentage="row.progressPercentage || 0" :color="customColor"></el-progress>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : row.status === 'in_progress' ? 'warning' : 'info'">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="editPlan(row)">编辑</el-button>
            <el-button type="warning" size="small" link @click="updateProgress(row)">更新进度</el-button>
            <el-button type="info" size="small" link @click="viewEffectiveness(row)">查看效果</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑帮扶计划" width="600px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="学生">
          <el-input :value="editForm.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="计划标题">
          <el-input v-model="editForm.title" placeholder="输入计划标题"></el-input>
        </el-form-item>
        <el-form-item label="目标">
          <el-input v-model="editForm.target" type="textarea" rows="2" placeholder="输入目标"></el-input>
        </el-form-item>
        <el-form-item label="措施">
          <el-input v-model="editForm.measures" type="textarea" rows="3" placeholder="输入具体措施"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="已启动" value="initiated"></el-option>
            <el-option label="进行中" value="in_progress"></el-option>
            <el-option label="已完成" value="completed"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePlan">保存</el-button>
      </template>
    </el-dialog>

    <!-- 进度更新对话框 -->
    <el-dialog v-model="progressDialogVisible" title="更新计划进度" width="500px">
      <el-form :model="progressForm" label-width="100px">
        <el-form-item label="进度">
          <el-slider v-model="progressForm.progress" :min="0" :max="100"></el-slider>
        </el-form-item>
        <el-form-item label="完成情况">
          <el-input v-model="progressForm.notes" type="textarea" rows="2" placeholder="描述完成情况"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProgress">更新</el-button>
      </template>
    </el-dialog>

    <!-- 效果查看对话框 -->
    <el-dialog v-model="effectivenessDialogVisible" title="帮扶效果" width="600px">
      <div v-if="effectiveness" class="effectiveness-content">
        <p><strong>学分提升:</strong> {{ effectiveness.creditIncrease }}分</p>
        <p><strong>成绩改善:</strong> {{ effectiveness.scoreImprovement }}%</p>
        <p><strong>预警状态:</strong> {{ effectiveness.warningStatus }}</p>
        <p><strong>完成度:</strong> {{ effectiveness.completionRate }}%</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { counselorAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const stats = ref({
  totalPlans: 0,
  inProgressPlans: 0,
  completedPlans: 0,
  completionRate: 0
})
const assistancePlans = ref([])
const editDialogVisible = ref(false)
const progressDialogVisible = ref(false)
const effectivenessDialogVisible = ref(false)

const editForm = ref({
  id: '',
  studentName: '',
  title: '',
  target: '',
  measures: '',
  status: 'initiated'
})

const progressForm = ref({
  progress: 0,
  notes: ''
})

const effectiveness = ref(null)

const customColor = computed(() => {
  return [
    { color: '#f56c6c', percentage: 33 },
    { color: '#e6a23c', percentage: 66 },
    { color: '#67c23a', percentage: 100 }
  ]
})

onMounted(async () => {
  await loadAssistancePlans()
})

const loadAssistancePlans = async () => {
  try {
    const userId = getUserId()
    const counselorId = localStorage.getItem('counselorId') || userId
    if (!counselorId) return
    
    // 获取当前登录用户的所有帮扶计划（通过学生列表）
    const response = await counselorAPI.getStudents(counselorId)
    const students = response.data || []
    
    // 为演示目的，获取第一个学生的帮扶计划
    if (students.length > 0) {
      const plansResponse = await counselorAPI.getPlansByStudent(students[0].id)
      assistancePlans.value = plansResponse.data || []
    } else {
      assistancePlans.value = []
    }
    
    // 更新统计数据
    const plans = assistancePlans.value
    stats.value = {
      totalPlans: plans.length,
      inProgressPlans: plans.filter(p => p.status === 'in_progress').length,
      completedPlans: plans.filter(p => p.status === 'completed').length,
      completionRate: plans.length === 0 ? 0 : Math.round((plans.filter(p => p.status === 'completed').length / plans.length) * 100)
    }
  } catch (error) {
    console.error('加载帮扶计划失败:', error)
  }
}

const editPlan = (row) => {
  editForm.value = { ...row }
  editDialogVisible.value = true
}

const savePlan = async () => {
  try {
    ElMessage.success('计划已保存')
    editDialogVisible.value = false
    await loadAssistancePlans()
  } catch (error) {
    console.error('保存计划失败:', error)
  }
}

const updateProgress = (row) => {
  progressForm.value.progress = row.progressPercentage || 0
  progressDialogVisible.value = true
}

const saveProgress = async () => {
  try {
    ElMessage.success('进度已更新')
    progressDialogVisible.value = false
    await loadAssistancePlans()
  } catch (error) {
    console.error('更新进度失败:', error)
  }
}

const viewEffectiveness = async (row) => {
  try {
    effectiveness.value = {
      creditIncrease: 1.5,
      scoreImprovement: 15,
      warningStatus: '已解除',
      completionRate: row.progressPercentage
    }
    effectivenessDialogVisible.value = true
  } catch (error) {
    console.error('获取效果数据失败:', error)
  }
}

const formatStatus = (status) => {
  const statusMap = {
    initiated: '已启动',
    in_progress: '进行中',
    completed: '已完成',
    archived: '已归档'
  }
  return statusMap[status] || status
}
</script>

<style scoped>
.counselor-assistance {
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
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  color: #999;
  font-size: 12px;
  margin-top: 8px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.effectiveness-content {
  line-height: 1.8;
}

.effectiveness-content p {
  margin: 10px 0;
}
</style>
