<template>
  <div class="admin-courses">
    <el-card class="stats-card">
      <div>
        <span>—2023级毕业要求完成率: </span>
        <el-progress :percentage="stats.completionRate2023" />
      </div>
      <div style="margin-top: 20px;">
        <span>—2024级毕业要求完成率: </span>
        <el-progress :percentage="stats.completionRate2024" />
      </div>
    </el-card>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>毕业要求管理</span>
          <el-button type="primary" @click="exportInsufficient">导出未达标学生</el-button>
        </div>
      </template>
      <el-table :data="requirementsList" stripe>
        <el-table-column prop="grade" label="等级" />
        <el-table-column prop="requiredCredits" label="要求学分" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="editRequirement(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 20px; padding: 20px; background: #f5f7fa; border-radius: 4px;">
        <p>未达标学生数: <strong>{{ stats.insufficientStudents }}</strong></p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminAPI } from '@/api/index'

const requirementsList = ref([])
const stats = ref({
  completionRate2023: 85,
  completionRate2024: 72,
  insufficientStudents: 143
})

onMounted(async () => {
  await loadRequirements()
})

const loadRequirements = async () => {
  try {
    const response = await adminAPI.getCourseRequirements()
    if (Array.isArray(response)) {
      requirementsList.value = response
    }
  } catch (error) {
    console.error('加载毕业要求失败:', error)
  }
}

const editRequirement = async (row) => {
  ElMessage.info(`编辑${row.grade}级毕业要求`)
}

const exportInsufficient = async () => {
  try {
    await adminAPI.exportInsufficientStudents()
    ElMessage.success('未达标学生名单已导出')
  } catch (error) {
    console.error('导出失败:', error)
  }
}
</script>

<style scoped>
.admin-courses {
  padding: 20px;
}
.stats-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
