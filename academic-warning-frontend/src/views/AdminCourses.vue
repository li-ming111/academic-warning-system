<template>
  <div class="admin-courses" style="background-color: #f8f9fa !important; min-height: 100vh;">
    <div class="page-header">
      <h1>课程管理</h1>
      <p>课程信息查看和管理</p>
    </div>
    
    <div class="action-bar">
      <el-input v-model="searchText" placeholder="搜索课程名称或ID" style="width: 250px;" @input="handleSearch" />
      <el-select v-model="filterType" placeholder="选择课程类型" style="width: 150px;" @change="handleSearch">
        <el-option label="全部" value=""></el-option>
        <el-option label="必修课" value="必修课"></el-option>
        <el-option label="选修课" value="选修课"></el-option>
      </el-select>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">课程列表</div>
      </template>
      <el-table :data="filteredCourseList" stripe>
        <el-table-column prop="id" label="课程ID" width="100"></el-table-column>
        <el-table-column prop="name" label="课程名称" min-width="300"></el-table-column>
        <el-table-column prop="credits" label="学分" width="100"></el-table-column>
        <el-table-column prop="courseType" label="课程分类" width="120"></el-table-column>
      </el-table>
      
      <div style="margin-top: 20px; text-align: right;">
        <el-pagination 
          v-model:current-page="currentPage" 
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]" 
          :total="totalCourses"
          layout="total, sizes, prev, pager, next, jumper">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { adminAPI } from '@/api/index'

const courseList = ref([])
const searchText = ref('')
const filterType = ref('')
const currentPage = ref(1)
const pageSize = ref(20)

onMounted(async () => {
  await loadCourses()
})

const loadCourses = async () => {
  try {
    const response = await adminAPI.getCourses()
    if (response && response.code === 0) {
      courseList.value = response.data || []
    } else if (Array.isArray(response)) {
      courseList.value = response
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const filteredCourseList = computed(() => {
  let filtered = courseList.value
  
  if (searchText.value) {
    const searchLower = searchText.value.toLowerCase()
    filtered = filtered.filter(course => 
      course.name.toLowerCase().includes(searchLower) || 
      String(course.id).includes(searchLower)
    )
  }
  
  if (filterType.value) {
    filtered = filtered.filter(course => course.courseType === filterType.value)
  }
  
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

const totalCourses = computed(() => {
  let filtered = courseList.value
  
  if (searchText.value) {
    const searchLower = searchText.value.toLowerCase()
    filtered = filtered.filter(course => 
      course.name.toLowerCase().includes(searchLower) || 
      String(course.id).includes(searchLower)
    )
  }
  
  if (filterType.value) {
    filtered = filtered.filter(course => course.courseType === filterType.value)
  }
  
  return filtered.length
})
</script>

<style scoped>
.admin-courses {
  padding: 20px;
  background-color: #f8f9fa !important;
  min-height: 100vh;
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

.action-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.admin-courses :deep(.el-card) {
  border: 1px solid #e9ecef !important;
}

.admin-courses :deep(.el-button--primary) {
  background-color: #667eea !important;
  border-color: #667eea !important;
}

.admin-courses :deep(.el-button--primary:hover) {
  background-color: #5568d3 !important;
  border-color: #5568d3 !important;
}
</style>