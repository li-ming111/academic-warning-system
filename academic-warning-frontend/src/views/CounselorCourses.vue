<template>
  <div class="counselor-courses">
    <div class="page-header">
      <h1>学生选修课信息</h1>
      <p>班级修读概览和学分核查</p>
    </div>

    <!-- 班级选择 -->
    <div class="filter-bar">
      <el-select v-model="selectedClass" placeholder="选择班级" @change="loadCourseData">
        <el-option v-for="cls in classList" :key="cls.id" :label="cls.name" :value="cls.id"></el-option>
      </el-select>
    </div>

    <!-- 修读概览 -->
    <el-card v-if="selectedClass" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">班级修读概览</div>
      </template>

      <div class="overview-stats">
        <div class="stat">
          <span class="label">选修课完成率</span>
          <span class="value">{{ overviewStats.completionRate }}%</span>
        </div>
        <div class="stat">
          <span class="label">平均学分</span>
          <span class="value">{{ overviewStats.averageCredits }}/8.0</span>
        </div>
        <div class="stat">
          <span class="label">学分不足人数</span>
          <span class="value">{{ overviewStats.insufficientStudents }}</span>
        </div>
      </div>
    </el-card>

    <!-- 学生选修详情 -->
    <el-card v-if="selectedClass">
      <template #header>
        <div class="card-header">学生选修课详情</div>
      </template>

      <el-table :data="courseList" stripe>
        <el-table-column prop="studentName" label="学生" width="100"></el-table-column>
        <el-table-column prop="courseName" label="选修课" width="150"></el-table-column>
        <el-table-column prop="score" label="成绩" width="80"></el-table-column>
        <el-table-column prop="credits" label="学分" width="80"></el-table-column>
        <el-table-column prop="totalCredits" label="累计学分" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.totalCredits < 8 ? '#f56c6c' : '#67c23a' }">
              {{ row.totalCredits }}/8.0
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 未达标预警 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">学分不足预警 ({{ insufficientStudents.length }})</div>
      </template>

      <div class="warning-list">
        <div v-if="insufficientStudents.length === 0" class="empty-state">
          <el-empty description="暂无学分不足学生"></el-empty>
        </div>
        <div class="warning-item" v-for="student in insufficientStudents" :key="student.id">
          <span class="student-name">{{ student.studentName }}</span>
          <span class="credits-status">{{ student.totalCredits }}/8.0学分</span>
          <el-button type="warning" size="small" link>提醒</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { counselorAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'
import { ElMessage } from 'element-plus'

const selectedClass = ref('')
const courseList = ref([])
const classList = ref([])
const insufficientStudents = ref([])
const overviewStats = ref({
  completionRate: 0,
  averageCredits: 0,
  insufficientStudents: 0
})

const loadCourses = async () => {
  try {
    const counselorId = localStorage.getItem('counselorId') || getUserId()
    const response = await counselorAPI.getCourses(counselorId)
    if (response && response.code === 0) {
      courseList.value = response.data || []
    } else if (Array.isArray(response)) {
      courseList.value = response
    }
  } catch (error) {
    console.error('加载课程数据失败:', error)
  }
}

onMounted(async () => {
  // 加载班级列表
  await loadClassList()
})

const loadClassList = async () => {
  try {
    const counselorId = localStorage.getItem('counselorId') || getUserId()
    // 这里可以添加获取班级列表的API调用
    // 暂时设置为空数组
    classList.value = []
  } catch (error) {
    console.error('加载班级列表失败:', error)
  }
}

const loadCourseData = async () => {
  if (!selectedClass.value) {
    ElMessage.error('请先选择班级')
    return
  }
  try {
    const counselorId = localStorage.getItem('counselorId') || getUserId()
    const response = await counselorAPI.getEnrollments(counselorId)
    if (response.data?.code === 200) {
      const enrollments = response.data.data || []
      courseList.value = enrollments
      
      // 计算修读概览数据
      if (enrollments.length > 0) {
        const totalCredits = enrollments.reduce((sum, item) => sum + (item.credits || 0), 0)
        const averageCredits = (totalCredits / enrollments.length).toFixed(1)
        const insufficient = enrollments.filter(item => (item.totalCredits || 0) < 8)
        
        overviewStats.value = {
          completionRate: Math.round((enrollments.length / 50) * 100), // 假设每班50人
          averageCredits: averageCredits,
          insufficientStudents: insufficient.length
        }
        
        // 学分不足学生
        insufficientStudents.value = insufficient
      }
    }
  } catch (error) {
    console.error('加载课程数据失败:', error)
    ElMessage.error('加载失败')
  }
}

// 监听班级选择变化
import { watch } from 'vue'
watch(selectedClass, () => {
  if (selectedClass.value) {
    loadCourseData()
  }
})

</script>

<style scoped>
.counselor-courses {
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

.filter-bar {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.overview-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat .label {
  color: #999;
  font-size: 12px;
  margin-bottom: 8px;
}

.stat .value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.warning-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background: #fdf6ec;
  border-radius: 6px;
  border-left: 3px solid #e6a23c;
}

.student-name {
  font-weight: bold;
  color: #333;
}

.credits-status {
  color: #f56c6c;
  font-weight: bold;
}
</style>
