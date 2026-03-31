<template>
  <div class="class-members-container">
    <div class="header">
      <h1>班级成员列表</h1>
      <p>当前班级：{{ classIdentifier }}</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-input 
        v-model="searchQuery" 
        placeholder="搜索学生姓名或学号..." 
        class="search-input"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 成员列表 -->
    <div class="members-list">
      <div v-if="filteredMembers.length === 0" class="empty-state">
        <el-empty description="暂无班级成员数据"></el-empty>
      </div>
      <div v-else class="members-grid">
        <div v-for="member in filteredMembers" :key="member.id" class="member-card">
          <div class="member-header">
            <div class="member-avatar">{{ member.name.charAt(0) }}</div>
            <div class="member-rank">{{ member.rank }}</div>
          </div>
          <div class="member-info">
            <h3>{{ member.name }}</h3>
            <p class="student-id">{{ member.studentId }}</p>
            <p class="major">{{ member.major }}</p>
          </div>
          <div class="member-stats">
            <div class="stat-item">
              <span class="label">GPA</span>
              <span class="value">{{ member.gpa.toFixed(2) }}</span>
            </div>
            <div class="stat-item">
              <span class="label">学分</span>
              <span class="value">{{ member.credits }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { studentAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const searchQuery = ref('')
const classIdentifier = ref('班级成员')
const members = ref([])
const loading = ref(false)

// 筛选成员
const filteredMembers = computed(() => {
  if (!searchQuery.value) {
    return members.value
  }
  const query = searchQuery.value.toLowerCase()
  return members.value.filter(member => 
    member.name.toLowerCase().includes(query) || 
    member.studentId.includes(query)
  )
})

// 加载班级成员
const loadClassMembers = async () => {
  loading.value = true
  try {
    const userId = getUserId()
    console.log('[ClassMembers] 获取 userId...', userId)
    console.log('[ClassMembers] localStorage.userId:', localStorage.getItem('userId'))
    console.log('[ClassMembers] 所有 localStorage:', JSON.parse(JSON.stringify(localStorage)))
    
    if (!userId) {
      console.error('[ClassMembers] 无法获取用户ID，localStorage中的userId为:', localStorage.getItem('userId'))
      return
    }
    
    console.log('[ClassMembers] 即将调用API: /students/class-members/' + userId)
    const response = await studentAPI.getClassMembersList(userId)
    console.log('[ClassMembers] API完整响应:', JSON.stringify(response))
    
    if (response && Array.isArray(response) && response.length > 0) {
      members.value = response
      console.log('[ClassMembers] 班级成员已加载:', members.value.length, '条数据')
      console.log('[ClassMembers] 数据内容:', members.value)
    } else {
      console.warn('[ClassMembers] 返回数据为空或格式不正确:', response)
    }
  } catch (error) {
    console.error('[ClassMembers] 加载班级成员异常:', error.message, error)
    console.error('错误详情:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadClassMembers()
})
</script>

<style scoped>
.class-members-container {
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

/* 搜索和筛选 */
.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.search-input {
  max-width: 300px;
}

/* 成员列表 */
.members-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.members-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.member-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e8ecf1;
  border-radius: 10px;
  padding: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.member-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.member-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.member-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
}

.member-rank {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.member-info {
  margin-bottom: 12px;
}

.member-info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #2c3e50;
  font-weight: 600;
}

.student-id {
  margin: 0 0 4px 0;
  font-size: 12px;
  color: #999;
}

.major {
  margin: 0;
  font-size: 12px;
  color: #666;
}

.member-stats {
  display: flex;
  justify-content: space-around;
  padding-top: 12px;
  border-top: 1px solid #e8ecf1;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-item .label {
  display: block;
  font-size: 11px;
  color: #999;
  margin-bottom: 4px;
}

.stat-item .value {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
}

/* 响应式 */
@media (max-width: 768px) {
  .members-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
}
</style>
