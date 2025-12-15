<template>
  <div class="counselor-class-management">
    <div class="page-header">
      <h1>班级管理</h1>
      <p>查看和管理班级信息、学生和活动</p>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">🏫 我的班级</div>
      </template>

      <el-table :data="classList" stripe>
        <el-table-column prop="className" label="班级名称" width="150"></el-table-column>
        <el-table-column prop="studentCount" label="学生数" width="100"></el-table-column>
        <el-table-column prop="warningCount" label="预警人数" width="100"></el-table-column>
        <el-table-column prop="warningRate" label="预警率" width="100"></el-table-column>
        <el-table-column prop="year" label="年级" width="100"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" link @click="viewClassDetail(scope.row)">查看详情</el-button>
            <el-button type="info" size="small" link @click="viewStudents(scope.row)">学生列表</el-button>
            <el-button type="success" size="small" link @click="viewStatistics(scope.row)">统计分析</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">📋 班级活动记录</div>
      </template>

      <el-table :data="classActivities" stripe>
        <el-table-column prop="type" label="活动类型" width="150"></el-table-column>
        <el-table-column prop="title" label="活动标题" width="250"></el-table-column>
        <el-table-column prop="date" label="时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" link>编辑</el-button>
            <el-button type="danger" size="small" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-button type="primary" style="margin-top: 15px;">新增活动</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const classList = ref([
  {
    id: 1,
    className: '2023级计算机科学与技术01班',
    studentCount: 45,
    warningCount: 5,
    warningRate: '11%',
    year: '2023级'
  },
  {
    id: 2,
    className: '2023级计算机科学与技术02班',
    studentCount: 42,
    warningCount: 3,
    warningRate: '7%',
    year: '2023级'
  }
])

const classActivities = ref([
  {
    id: 1,
    type: '考试安排',
    title: '2025年春学期期末考试',
    date: '2025-12-15'
  },
  {
    id: 2,
    type: '班级活动',
    title: '班级座谈会',
    date: '2025-12-10'
  }
])

const viewClassDetail = (classInfo) => {
  ElMessage.info('查看班级：' + classInfo.className)
}

const viewStudents = (classInfo) => {
  ElMessage.info('学生列表：' + classInfo.className)
}

const viewStatistics = (classInfo) => {
  ElMessage.info('统计分析：' + classInfo.className)
}
</script>

<style scoped>
.counselor-class-management {
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

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
</style>
