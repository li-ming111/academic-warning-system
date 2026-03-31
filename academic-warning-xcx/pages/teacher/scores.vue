<template>
  <view class="scores-container">
    <view class="header">
      <text class="title">成绩管理</text>
      <text class="subtitle">管理学生课程成绩</text>
    </view>

    <view class="filter-section">
      <view class="search-box">
        <input class="search-input" type="text" v-model="searchKeyword" placeholder="搜索学生姓名或学号" @confirm="handleSearch" />
        <button class="search-btn" @click="handleSearch">搜索</button>
      </view>
      <picker :range="courseOptions" :value="selectedCourseIndex" @change="handleCourseChange" class="course-picker">
        <view class="picker-label">
          <text class="picker-text">{{ selectedCourse }}</text>
          <text class="picker-arrow">▼</text>
        </view>
      </picker>
    </view>

    <view class="scores-list">
      <view v-if="filteredStudents.length > 0" class="students-content">
        <view v-for="student in filteredStudents" :key="student.id" class="student-item" hover-class="student-item-hover">
          <view class="student-info">
            <text class="student-name">{{ student.name }}</text>
            <text class="student-id">{{ student.id }}</text>
          </view>
          <view class="score-info">
            <input class="score-input" type="number" v-model="student.score" placeholder="输入成绩" @blur="updateScore(student)" />
            <text :class="['score-grade', getGradeClass(student.score)]">{{ getGrade(student.score) }}</text>
          </view>
          <view class="student-actions">
            <button class="action-btn save-btn" @click="saveScore(student)">保存</button>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <view class="empty-icon">📊</view>
        <text>暂无学生数据</text>
      </view>
    </view>

    <view class="batch-actions">
      <button class="batch-btn" @click="batchImport">批量导入</button>
      <button class="batch-btn export-btn" @click="exportScores">导出成绩</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';

const searchKeyword = ref('');
const selectedCourse = ref('全部课程');
const selectedCourseIndex = ref(0);
const courseOptions = ['全部课程', '高等数学', '大学英语', '计算机基础', '大学物理'];

const students = ref([
  { id: '2021001', name: '张三', course: '高等数学', score: 85 },
  { id: '2021002', name: '李四', course: '高等数学', score: 78 },
  { id: '2021003', name: '王五', course: '高等数学', score: 92 },
  { id: '2021004', name: '赵六', course: '大学英语', score: 88 },
  { id: '2021005', name: '钱七', course: '大学英语', score: 75 }
]);

const filteredStudents = computed(() => {
  let result = students.value;
  if (selectedCourse.value !== '全部课程') {
    result = result.filter(s => s.course === selectedCourse.value);
  }
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(s => s.name.toLowerCase().includes(keyword) || s.id.toLowerCase().includes(keyword));
  }
  return result;
});

const getGrade = (score) => {
  if (score >= 90) return '优秀';
  if (score >= 80) return '良好';
  if (score >= 70) return '中等';
  if (score >= 60) return '及格';
  return '不及格';
};

const getGradeClass = (score) => {
  if (score >= 90) return 'excellent';
  if (score >= 80) return 'good';
  if (score >= 70) return 'medium';
  if (score >= 60) return 'pass';
  return 'fail';
};

const handleSearch = () => {
  console.log('搜索:', searchKeyword.value);
};

const handleCourseChange = (e) => {
  selectedCourseIndex.value = e.detail.value;
  selectedCourse.value = courseOptions[e.detail.value];
};

const updateScore = (student) => {
  console.log('更新成绩:', student.name, student.score);
};

const saveScore = (student) => {
  uni.showToast({ title: '成绩保存成功', icon: 'success' });
};

const batchImport = () => {
  uni.showToast({ title: '批量导入功能', icon: 'none' });
};

const exportScores = () => {
  uni.showToast({ title: '成绩导出成功', icon: 'success' });
};
</script>

<style scoped>
.scores-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.filter-section { background: white; border-radius: 20rpx; padding: 24rpx; margin-bottom: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; }
.search-box { display: flex; gap: 16rpx; margin-bottom: 20rpx; }
.search-input { flex: 1; height: 80rpx; padding: 0 24rpx; border: 1rpx solid #e0e0e0; border-radius: 12rpx; font-size: 16rpx; background-color: #f9f9f9; }
.search-btn { width: 120rpx; height: 80rpx; background-color: #4facfe; color: white; border: none; border-radius: 12rpx; font-size: 16rpx; font-weight: 500; }
.course-picker { width: 100%; }
.picker-label { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 24rpx; background-color: #f9f9f9; border: 1rpx solid #e0e0e0; border-radius: 12rpx; }
.picker-text { font-size: 16rpx; color: #333; }
.picker-arrow { font-size: 14rpx; color: #999; }
.scores-list { margin-bottom: 24rpx; }
.students-content { display: flex; flex-direction: column; gap: 16rpx; }
.student-item { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: center; transition: all 0.3s ease; }
.student-item-hover { transform: translateY(-4rpx); box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.12); border-color: #4facfe; }
.student-info { flex: 1; }
.student-name { font-size: 18rpx; font-weight: 600; color: #333; display: block; margin-bottom: 4rpx; }
.student-id { font-size: 14rpx; color: #666; }
.score-info { display: flex; align-items: center; gap: 16rpx; margin-right: 16rpx; }
.score-input { width: 120rpx; height: 60rpx; padding: 0 16rpx; border: 1rpx solid #e0e0e0; border-radius: 8rpx; font-size: 16rpx; text-align: center; }
.score-grade { font-size: 14rpx; font-weight: 500; padding: 4rpx 12rpx; border-radius: 8rpx; }
.score-grade.excellent { background-color: #e8f5e9; color: #4caf50; }
.score-grade.good { background-color: #e3f2fd; color: #1976d2; }
.score-grade.medium { background-color: #fff8e1; color: #f57c00; }
.score-grade.pass { background-color: #f5f5f5; color: #666; }
.score-grade.fail { background-color: #ffebee; color: #d32f2f; }
.student-actions { flex-shrink: 0; }
.action-btn { padding: 10rpx 20rpx; border: none; border-radius: 8rpx; font-size: 14rpx; font-weight: 500; }
.save-btn { background-color: #4facfe; color: white; }
.empty-state { text-align: center; padding: 60rpx 0; color: #999; font-size: 16rpx; background-color: #fff; border-radius: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; }
.empty-icon { font-size: 60rpx; margin-bottom: 16rpx; display: block; }
.batch-actions { display: flex; gap: 20rpx; }
.batch-btn { flex: 1; height: 90rpx; background-color: #4facfe; color: white; border: none; border-radius: 16rpx; font-size: 18rpx; font-weight: 600; }
.export-btn { background: linear-gradient(135deg, #4caf50 0%, #8bc34a 100%); }
</style>