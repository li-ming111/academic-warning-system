<template>
  <div class="class-ranking-container">
    <div class="header">
      <h1>班级排名排行榜</h1>
      <p>显示班级全体学生的学业排名</p>
    </div>

    <!-- 排序选项 -->
    <div class="sort-section">
      <el-select v-model="sortBy" placeholder="选择排序方式" class="sort-select">
        <el-option 
          v-for="option in sortOptions" 
          :key="option.value" 
          :label="option.label" 
          :value="option.value"
        />
      </el-select>
    </div>

    <!-- 排名列表 -->
    <div class="ranking-list">
      <div v-if="sortedMembers.length === 0" class="empty-state">
        <el-empty description="暂无班级排名数据"></el-empty>
      </div>
      <div v-else class="rankings">
        <div 
          v-for="(member, index) in sortedMembers" 
          :key="member.id" 
          class="ranking-item"
          :class="[getRankClass(index), { 'is-current': member.isCurrent }]"
        >
          <div class="rank-badge">
            <span class="rank-number">{{ member.rank }}</span>
          </div>
          
          <div class="member-details">
            <div class="member-name-section">
              <h3 class="member-name">{{ member.name }}</h3>
              <span v-if="member.isCurrent" class="badge-current">你</span>
            </div>
            <p class="student-id">学号: {{ member.studentId }}</p>
          </div>

          <div class="member-scores">
            <div class="score-item">
              <span class="score-label">GPA</span>
              <span class="score-value">{{ member.gpa.toFixed(2) }}</span>
            </div>
            <div class="score-item">
              <span class="score-label">学分</span>
              <span class="score-value">{{ member.credits }}</span>
            </div>
          </div>

          <div class="status-badge">
            <el-tag :type="getStatusType(member.status)">{{ member.status }}</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { studentAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const sortBy = ref('rank')
const members = ref([])
const loading = ref(false)

const sortOptions = [
  { label: 'GPA排序', value: 'gpa' },
  { label: '学分排序', value: 'credits' },
  { label: '默认排序', value: 'rank' }
]

// 排序成员
const sortedMembers = computed(() => {
  let sorted = [...members.value]
  
  if (sortBy.value === 'gpa') {
    sorted.sort((a, b) => b.gpa - a.gpa)
  } else if (sortBy.value === 'credits') {
    sorted.sort((a, b) => b.credits - a.credits)
  } else {
    sorted.sort((a, b) => a.rank - b.rank)
  }
  
  return sorted
})

// 获取排名样式
const getRankClass = (index) => {
  if (index === 0) return 'rank-1'
  if (index === 1) return 'rank-2'
  if (index === 2) return 'rank-3'
  return 'rank-normal'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '优秀': 'success',
    '良好': 'info',
    '及格': 'warning',
    '警告': 'danger'
  }
  return typeMap[status] || 'info'
}

// 加载班级排名
const loadClassRanking = async () => {
  loading.value = true
  try {
    const userId = getUserId()
    console.log('[ClassRanking] 获取 userId...', userId)
    console.log('[ClassRanking] localStorage.userId:', localStorage.getItem('userId'))
    console.log('[ClassRanking] 所有 localStorage:', JSON.parse(JSON.stringify(localStorage)))
    
    if (!userId) {
      console.error('[ClassRanking] 无法获取用户ID，localStorage中的userId为:', localStorage.getItem('userId'))
      return
    }

    console.log('[ClassRanking] 即将调用API: /students/class-ranking/' + userId)
    const response = await studentAPI.getClassRankingList(userId)
    console.log('[ClassRanking] API完整响应:', JSON.stringify(response))
    
    if (response && Array.isArray(response) && response.length > 0) {
      members.value = response
      console.log('[ClassRanking] 班级排名已加载:', members.value.length, '条数据')
      console.log('[ClassRanking] 数据内容:', members.value)
    } else {
      console.warn('[ClassRanking] 返回数据为空或格式不正确:', response)
    }
  } catch (error) {
    console.error('[ClassRanking] 加载班级排名异常:', error.message, error)
    console.error('错误详情:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadClassRanking()
})
</script>

<style scoped>
.class-ranking-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: 100vh;
}

.header {
  margin-bottom: 30px;
}

.header h1 {
  font-size: 28px;
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 排序部分 */
.sort-section {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.sort-select {
  width: 150px;
}

/* 排名列表 */
.ranking-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.rankings {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 10px;
  border-left: 4px solid #e8ecf1;
  transition: all 0.3s ease;
}

.ranking-item:hover {
  background: #f0f2f7;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.ranking-item.is-current {
  background: linear-gradient(135deg, #f5f3ff 0%, #efe6ff 100%);
  border-left-color: #667eea;
}

.ranking-item.rank-1 {
  border-left-color: #ffd700;
  background: linear-gradient(135deg, #fffef0 0%, #fffde8 100%);
}

.ranking-item.rank-2 {
  border-left-color: #c0c0c0;
  background: linear-gradient(135deg, #fefefe 0%, #f9f9f9 100%);
}

.ranking-item.rank-3 {
  border-left-color: #cd7f32;
  background: linear-gradient(135deg, #fffbf5 0%, #ffece0 100%);
}

/* 排名徽章 */
.rank-badge {
  min-width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: white;
  font-size: 24px;
}

.rank-1 .rank-badge {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #333;
}

.rank-2 .rank-badge {
  background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%);
  color: #666;
}

.rank-3 .rank-badge {
  background: linear-gradient(135deg, #cd7f32 0%, #dd8f3d 100%);
}

.rank-normal .rank-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 成员详情 */
.member-details {
  flex: 1;
  min-width: 0;
}

.member-name-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.member-name {
  margin: 0;
  font-size: 16px;
  color: #2c3e50;
  font-weight: 600;
}

.badge-current {
  background: #667eea;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.student-id {
  margin: 0;
  font-size: 12px;
  color: #999;
}

/* 成绩部分 */
.member-scores {
  display: flex;
  gap: 20px;
  min-width: 120px;
}

.score-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-label {
  font-size: 11px;
  color: #999;
  margin-bottom: 4px;
}

.score-value {
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
}

/* 状态徽章 */
.status-badge {
  min-width: 80px;
  text-align: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .ranking-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .member-scores {
    width: 100%;
    justify-content: space-between;
  }

  .rank-badge {
    min-width: 50px;
    height: 50px;
    font-size: 20px;
  }
}
</style>
