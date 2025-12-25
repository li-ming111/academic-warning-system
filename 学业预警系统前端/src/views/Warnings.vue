 <template>
  <div class="warnings-container">
    <!-- 预警统计 -->
    <div class="warning-stats">
      <div class="stat-box">
        <div class="stat-label">红色预警</div>
        <div class="stat-number red">{{ redWarnings.length }}</div>
      </div>
      <div class="stat-box">
        <div class="stat-label">黄色预警</div>
        <div class="stat-number yellow">{{ yellowWarnings.length }}</div>
      </div>
      <div class="stat-box">
        <div class="stat-label">蓝色预警</div>
        <div class="stat-number blue">{{ blueWarnings.length }}</div>
      </div>
      <div class="stat-box">
        <div class="stat-label">待处理申诉</div>
        <div class="stat-number">{{ pendingAppeals.length }}</div>
      </div>
    </div>

    <!-- 预警列表 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span>预警管理</span>
          <el-select v-model="filterLevel" placeholder="筛选预警等级" style="width: 150px; margin-left: 20px;">
            <el-option label="全部预警" value=""></el-option>
            <el-option label="红色预警" value="red"></el-option>
            <el-option label="黄色预警" value="yellow"></el-option>
            <el-option label="蓝色预警" value="blue"></el-option>
          </el-select>
        </div>
      </template>

      <el-table :data="filteredWarnings" stripe>
        <el-table-column type="expand">
          <template #default="{ row }">
            <div style="padding: 20px;">
              <p><strong>详细说明：</strong>{{ row.description }}</p>
              <p><strong>处理建议：</strong>{{ row.suggestion }}</p>
              <p><strong>相关课程：</strong>{{ row.courseName }}</p>
              <p><strong>创建时间：</strong>{{ row.createdAt }}</p>
              <p><strong>更新时间：</strong>{{ row.updatedAt }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="warningLevel" label="预警等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.warningLevel)">{{ row.warningLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程" width="120"></el-table-column>
        <el-table-column prop="title" label="预警标题" width="200"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'confirmed'" type="danger">已确认</el-tag>
            <el-tag v-else type="info">待确认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetails(row)">详情</el-button>
            <el-button type="warning" size="small" @click="openAppealDialog(row)">申诉</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 待处理申诉 -->
    <el-card v-if="pendingAppeals.length > 0">
      <template #header>
        <div class="card-header">我的申诉记录</div>
      </template>

      <el-table :data="pendingAppeals" stripe>
        <el-table-column prop="courseName" label="课程" width="150"></el-table-column>
        <el-table-column prop="currentScore" label="原成绩" width="100"></el-table-column>
        <el-table-column prop="reason" label="申诉原因" width="150"></el-table-column>
        <el-table-column prop="status" label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待处理</el-tag>
            <el-tag v-else-if="row.status === 'approved'" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submittedAt" label="提交时间" width="150"></el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewAppealDetails(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 申诉对话框 -->
    <el-dialog v-model="appealDialogVisible" title="预警申诉" width="600px">
      <el-form :model="appealForm" label-width="100px">
        <el-form-item label="预警课程">
          <el-input v-model="appealForm.courseName" disabled></el-input>
        </el-form-item>
        <el-form-item label="预警内容">
          <el-input v-model="appealForm.warningTitle" disabled></el-input>
        </el-form-item>
        <el-form-item label="申诉原因">
          <el-input v-model="appealForm.appealReason" type="textarea" rows="3" placeholder="请说明您的申诉理由"></el-input>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            v-model:file-list="appealForm.attachments"
            action="#"
            :auto-upload="false"
            :limit="3"
          >
            <template #trigger>
              <el-button type="primary">选择文件</el-button>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="appealDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitWarningAppeal">提交申诉</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailsDialogVisible" title="预警详情" width="600px">
      <div v-if="selectedWarning" class="warning-details">
        <p><strong>预警标题：</strong>{{ selectedWarning.title }}</p>
        <p><strong>预警等级：</strong>
          <el-tag :type="getLevelType(selectedWarning.warningLevel)">{{ selectedWarning.warningLevel }}</el-tag>
        </p>
        <p><strong>关联课程：</strong>{{ selectedWarning.courseName }}</p>
        <p><strong>详细说明：</strong>{{ selectedWarning.description }}</p>
        <p><strong>处理建议：</strong>{{ selectedWarning.suggestion }}</p>
        <p><strong>创建时间：</strong>{{ selectedWarning.createdAt }}</p>

        <el-divider></el-divider>

        <p style="color: #f56c6c;"><strong>⚠️ 建议行动：</strong></p>
        <ul>
          <li>立即联系课程教师了解详细情况</li>
          <li>制定针对性的学习计划</li>
          <li>参加教师组织的补课或答疑</li>
          <li>如有异议，可提交成绩申诉</li>
        </ul>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { studentAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const filterLevel = ref('')
const allWarnings = ref([])
const detailsDialogVisible = ref(false)
const appealDialogVisible = ref(false)
const selectedWarning = ref(null)
const pendingAppeals = ref([])

const appealForm = ref({
  warningId: null,
  courseName: '',
  warningTitle: '',
  appealReason: '',
  attachments: []
})

onMounted(async () => {
  const userId = getUserId()
  if (!userId) {
    ElMessage.error('请先登录')
    return
  }
  await loadWarnings(userId)
  await loadAppeals(userId)
})

// 加载预警数据
const loadWarnings = async (userId) => {
  try {
    console.log('预警userId:', userId, 'type:', typeof userId)
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    const response = await studentAPI.getWarnings(userId)
    if (Array.isArray(response) && response.length > 0) {
      allWarnings.value = response.map(w => ({
        ...w,
        warningLevel: translateLevel(w.warningLevel),
        status: w.status || 'confirmed'
      }))
    } else {
      allWarnings.value = []
    }
  } catch (error) {
    console.error('加载预警失败:', error)
    allWarnings.value = []
  }
}

// 加载申诉数据
const loadAppeals = async (userId) => {
  try {
    if (!userId) return
    const response = await studentAPI.getAppeals(userId)
    if (Array.isArray(response) && response.length > 0) {
      pendingAppeals.value = response
    }
  } catch (error) {
    console.error('加载申诉失败:', error)
  }
}

// 计算不同级别的预警
const redWarnings = computed(() => allWarnings.value.filter(w => w.warningLevel === '红色'))
const yellowWarnings = computed(() => allWarnings.value.filter(w => w.warningLevel === '黄色'))
const blueWarnings = computed(() => allWarnings.value.filter(w => w.warningLevel === '蓝色'))

// 根据等级筛选预警
const filteredWarnings = computed(() => {
  if (!filterLevel.value) return allWarnings.value
  const levelMap = {
    'red': '红色',
    'yellow': '黄色',
    'blue': '蓝色'
  }
  const translatedLevel = levelMap[filterLevel.value]
  return allWarnings.value.filter(w => w.warningLevel === translatedLevel)
})

// 翻译不同系统的预警等级
const translateLevel = (level) => {
  if (level === 'high' || level === 'red') return '红色'
  if (level === 'medium' || level === 'yellow') return '黄色'
  if (level === 'low' || level === 'blue') return '蓝色'
  return '低'
}

// 获取等级标签类型
const getLevelType = (level) => {
  switch (level) {
    case '红色':
      return 'danger'
    case '黄色':
      return 'warning'
    case '蓝色':
      return 'info'
    case 'high':
      return 'danger'
    case 'medium':
      return 'warning'
    case 'low':
      return 'info'
    default:
      return 'info'
  }
}

// 查看详情
const viewDetails = (row) => {
  selectedWarning.value = row
  detailsDialogVisible.value = true
}

// 打开申诉对话框
const openAppealDialog = (row) => {
  appealForm.value = {
    warningId: row.id,
    courseName: row.courseName,
    warningTitle: row.title,
    appealReason: '',
    attachments: []
  }
  appealDialogVisible.value = true
}

// 提交申诉
const submitWarningAppeal = async () => {
  if (!appealForm.value.appealReason) {
    ElMessage.error('请填写申诉原因')
    return
  }

  try {
    const userId = getUserId()
    const data = {
      studentId: userId,
      warningId: appealForm.value.warningId,
      reason: appealForm.value.appealReason,
      description: appealForm.value.appealReason
    }
    await studentAPI.submitAppeal(data)
    ElMessage.success('预警申诉已提交')
    appealDialogVisible.value = false
    await loadAppeals(userId)
  } catch (error) {
    console.error('提交申诉失败:', error)
    ElMessage.error('提交申诉失败')
  }
}

// 查看申诉详情
const viewAppealDetails = async (row) => {
  try {
    const detail = await studentAPI.getAppealDetail(row.id)
    if (detail) {
      ElMessage.info(detail.reason)
    }
  } catch (error) {
    console.error('加载申诉详情失败:', error)
  }
}
</script>

<style scoped>
.warnings-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.warning-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.stat-box {
  background: white;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stat-label {
  color: #666;
  font-size: 13px;
  margin-bottom: 8px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

.stat-number.red {
  color: #f56c6c;
}

.stat-number.yellow {
  color: #e6a23c;
}

.stat-number.blue {
  color: #409eff;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  display: flex;
  align-items: center;
}

.warning-details {
  line-height: 1.8;
}

.warning-details p {
  margin: 10px 0;
}

.warning-details ul {
  margin-left: 20px;
}

.warning-details li {
  margin: 8px 0;
  color: #666;
}
</style>
