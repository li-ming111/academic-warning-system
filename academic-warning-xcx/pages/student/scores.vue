<template>
  <view class="scores-page">
    <view class="header">
      <text class="page-title">成绩查询</text>
      <text class="page-subtitle">查看您的课程成绩和学分情况</text>
    </view>

    <view class="filter-section" hover-class="filter-section-hover">
      <picker :range="semesters" :value="getSemesterIndex()" @change="handleSemesterChange" class="semester-picker">
        <view class="picker-label">
          <text class="picker-text">{{ selectedSemester }}</text>
          <text class="picker-arrow">▼</text>
        </view>
      </picker>
    </view>

    <view class="scores-container">
      <view v-if="scores.length > 0" class="scores-list">
        <view v-for="course in scores" :key="course.id" class="score-item" hover-class="score-item-hover">
          <view class="course-info">
            <text class="course-name">{{ course.courseName }}</text>
            <text class="course-semester">{{ course.semester }}</text>
          </view>
          <view class="score-details">
            <text :class="['score', getScoreClass(course.scoreTotal)]">{{ course.scoreTotal }}</text>
            <text class="credit">{{ course.credits }} 学分</text>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <view class="empty-icon">📊</view>
        <text>暂无成绩记录</text>
      </view>
    </view>

    <view class="statistics">
      <view class="stat-item" hover-class="stat-item-hover">
        <view class="stat-icon average">📈</view>
        <text class="stat-label">平均成绩</text>
        <text class="stat-value">{{ averageScore }}</text>
      </view>
      <view class="stat-item" hover-class="stat-item-hover">
        <view class="stat-icon credits">📚</view>
        <text class="stat-label">已修学分</text>
        <text class="stat-value">{{ completedCredits }}</text>
      </view>
      <view class="stat-item" hover-class="stat-item-hover">
        <view class="stat-icon failed">⚠️</view>
        <text class="stat-label">挂科数量</text>
        <text class="stat-value warning">{{ failedCount }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { apiClient } from '../../services/api'

const selectedSemester = ref('全部学期')
const semesters = ref([
  '全部学期',
  '2025-2026春',
  '2025-2026秋',
  '2024-2025春',
  '2024-2025秋',
  '2023-2024春',
  '2023-2024秋'
])

const scores = ref([])

const averageScore = computed(() => {
  if (scores.value.length === 0) return '0.0'
  const sum = scores.value.reduce((total, course) => total + (course.scoreTotal || 0), 0)
  return (sum / scores.value.length).toFixed(1)
})

const completedCredits = computed(() => {
  return scores.value.reduce((total, course) => {
    return (course.scoreTotal || 0) >= 60 ? total + (course.credits ? parseFloat(course.credits) : 0) : total
  }, 0).toFixed(1)
})

const failedCount = computed(() => {
  return scores.value.filter(course => (course.scoreTotal || 0) < 60).length
})

const getScoreClass = (score) => {
  const scoreNum = score || 0
  if (scoreNum < 60) return 'failed'
  if (scoreNum < 70) return 'pass'
  if (scoreNum < 80) return 'good'
  return 'excellent'
}

const getSemesterIndex = () => {
  return semesters.value.indexOf(selectedSemester.value)
}

const handleSemesterChange = (e) => {
  const index = e.detail.value
  selectedSemester.value = semesters.value[index]
  loadScores()
}

const loadScores = async () => {
  try {
    const semester = selectedSemester.value === '全部学期' ? null : selectedSemester.value
    const response = await apiClient.getStudentScores(semester)
    if (response) {
      scores.value = response
    }
  } catch (error) {
    console.error('获取成绩失败:', error)
    // 使用模拟数据
    scores.value = [
      {
        id: 1,
        courseName: '高等数学',
        scoreTotal: 58,
        credits: 4,
        semester: '2025-2026春'
      },
      {
        id: 2,
        courseName: '大学英语',
        scoreTotal: 75,
        credits: 3,
        semester: '2025-2026春'
      },
      {
        id: 3,
        courseName: '计算机基础',
        scoreTotal: 82,
        credits: 3,
        semester: '2025-2026春'
      },
      {
        id: 4,
        courseName: '大学物理',
        scoreTotal: 65,
        credits: 4,
        semester: '2025-2026秋'
      },
      {
        id: 5,
        courseName: '线性代数',
        scoreTotal: 78,
        credits: 3,
        semester: '2025-2026秋'
      }
    ]
  }
}

// 监听学期变化
watch(selectedSemester, () => {
  loadScores()
})

onMounted(() => {
  loadScores()
})
</script>

<style scoped>
.scores-page {
  padding: 20rpx;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  margin-bottom: 24rpx;
}

.page-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.page-subtitle {
  font-size: 18rpx;
  color: #666;
  display: block;
}

.filter-section {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
  transition: all 0.3s ease;
}

.filter-section-hover {
  transform: translateY(-2rpx);
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.12);
}

.semester-picker {
  width: 100%;
}

.picker-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18rpx;
  color: #333;
}

.picker-text {
  font-weight: 500;
}

.picker-arrow {
  font-size: 14rpx;
  color: #999;
  transition: transform 0.3s ease;
}

.filter-section-hover .picker-arrow {
  transform: rotate(180deg);
}

.scores-container {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
}

.scores-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.score-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border: 1rpx solid #f0f0f0;
  border-radius: 16rpx;
  background-color: #f9f9f9;
  transition: all 0.3s ease;
}

.score-item-hover {
  background-color: #f0f7ff;
  border-color: #4facfe;
  transform: translateX(8rpx);
}

.course-info {
  flex: 1;
  margin-right: 20rpx;
}

.course-name {
  font-size: 18rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 6rpx;
}

.course-semester {
  font-size: 16rpx;
  color: #666;
}

.score-details {
  text-align: right;
}

.score {
  font-size: 24rpx;
  font-weight: 700;
  display: block;
  margin-bottom: 6rpx;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  display: inline-block;
}

.score.failed {
  color: #ff5252;
  background-color: #ffebee;
}

.score.pass {
  color: #4caf50;
  background-color: #e8f5e9;
}

.score.good {
  color: #2196f3;
  background-color: #e3f2fd;
}

.score.excellent {
  color: #ff9800;
  background-color: #fff3e0;
}

.credit {
  font-size: 16rpx;
  color: #666;
  font-weight: 500;
}

.empty-state {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 16rpx;
}

.empty-icon {
  font-size: 60rpx;
  margin-bottom: 16rpx;
  display: block;
}

.statistics {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20rpx;
}

.stat-item {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  text-align: center;
  border: 1rpx solid #f0f0f0;
  transition: all 0.3s ease;
}

.stat-item-hover {
  transform: translateY(-4rpx);
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.12);
}

.stat-icon {
  font-size: 36rpx;
  margin-bottom: 12rpx;
  display: block;
}

.stat-icon.average {
  color: #4facfe;
}

.stat-icon.credits {
  color: #4caf50;
}

.stat-icon.failed {
  color: #ff5252;
}

.stat-label {
  font-size: 16rpx;
  color: #666;
  display: block;
  margin-bottom: 8rpx;
}

.stat-value {
  font-size: 24rpx;
  font-weight: 700;
  color: #333;
}

.stat-value.warning {
  color: #ff5252;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.scores-page > * {
  animation: fadeIn 0.5s ease-out forwards;
}

.scores-page > *:nth-child(1) {
  animation-delay: 0.1s;
}

.scores-page > *:nth-child(2) {
  animation-delay: 0.2s;
}

.scores-page > *:nth-child(3) {
  animation-delay: 0.3s;
}

.scores-page > *:nth-child(4) {
  animation-delay: 0.4s;
}
</style>