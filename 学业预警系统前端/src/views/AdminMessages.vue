<template>
  <div class="admin-messages">
    <div class="page-header">
      <h1>💬 消息与任务管理</h1>
      <p>系统广播消息、定向通知、任务分配与追踪</p>
    </div>

    <!-- Tab页签 -->
    <el-tabs type="card">
      <!-- 消息管理 -->
      <el-tab-pane label="📨 消息管理">
        <div class="tab-content">
          <!-- 发送消息卡片 -->
          <el-card style="margin-bottom: 20px;">
            <template #header>
              <div class="card-header">发送新消息</div>
            </template>

            <el-form :model="newMessage" label-width="80px">
              <el-form-item label="消息标题">
                <el-input v-model="newMessage.title" placeholder="输入消息标题"></el-input>
              </el-form-item>
              <el-form-item label="消息内容">
                <el-input
                  v-model="newMessage.content"
                  type="textarea"
                  rows="4"
                  placeholder="输入消息内容"
                ></el-input>
              </el-form-item>
              <el-form-item label="消息等级">
                <el-radio-group v-model="newMessage.level">
                  <el-radio value="info">信息</el-radio>
                  <el-radio value="warning">警告</el-radio>
                  <el-radio value="danger">危急</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="发送范围">
                <el-radio-group v-model="newMessage.target">
                  <el-radio value="all">全部用户</el-radio>
                  <el-radio value="targeted">指定用户</el-radio>
                </el-radio-group>
              </el-form-item>

              <div v-if="newMessage.target === 'targeted'" style="margin-bottom: 20px;">
                <el-form-item label="选择用户">
                  <el-select
                    v-model="newMessage.user_ids"
                    multiple
                    placeholder="选择接收消息的用户"
                    style="width: 100%;"
                  >
                    <el-option
                      v-for="(user, idx) in mockUsers"
                      :key="idx"
                      :label="user.username"
                      :value="user.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <el-form-item>
                <el-button type="primary" @click="sendMessage">发送消息</el-button>
                <el-button @click="resetMessageForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 消息列表 -->
          <el-card>
            <template #header>
              <div class="card-header">消息历史</div>
            </template>

            <el-table :data="messages" stripe v-loading="loading">
              <el-table-column prop="title" label="标题" min-width="150"></el-table-column>
              <el-table-column prop="content" label="内容" min-width="250"></el-table-column>
              <el-table-column prop="level" label="等级" width="80">
                <template #default="{ row }">
                  <el-tag
                    :type="row.level === 'info' ? '' : (row.level === 'warning' ? 'warning' : 'danger')"
                  >
                    {{ row.level }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="target" label="目标" width="100">
                <template #default="{ row }">
                  <el-tag type="success">{{ row.target === 'all' ? '全部用户' : '指定用户' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="created_at" label="发送时间" width="180"></el-table-column>
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button type="danger" size="small" link @click="deleteMessage(row.id)"
                    >删除</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 任务管理 -->
      <el-tab-pane label="✅ 任务管理">
        <div class="tab-content">
          <!-- 创建任务卡片 -->
          <el-card style="margin-bottom: 20px;">
            <template #header>
              <div class="card-header">创建新任务</div>
            </template>

            <el-form :model="newTask" label-width="80px">
              <el-form-item label="任务标题">
                <el-input v-model="newTask.title" placeholder="输入任务标题"></el-input>
              </el-form-item>
              <el-form-item label="任务描述">
                <el-input
                  v-model="newTask.description"
                  type="textarea"
                  rows="4"
                  placeholder="输入任务描述"
                ></el-input>
              </el-form-item>
              <el-form-item label="优先级">
                <el-select v-model="newTask.priority" placeholder="选择优先级">
                  <el-option label="低" value="low"></el-option>
                  <el-option label="中" value="medium"></el-option>
                  <el-option label="高" value="high"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="createTask">创建任务</el-button>
                <el-button @click="resetTaskForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 任务统计 -->
          <div class="stats-grid" style="margin-bottom: 20px;">
            <div class="stat-card">
              <div class="stat-title">总任务数</div>
              <div class="stat-value">{{ tasks.length }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-title">待处理</div>
              <div class="stat-value" style="color: #f56c6c;">
                {{ tasks.filter(t => t.status === 'pending').length }}
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-title">进行中</div>
              <div class="stat-value" style="color: #e6a23c;">
                {{ tasks.filter(t => t.status === 'in_progress').length }}
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-title">已完成</div>
              <div class="stat-value" style="color: #67c23a;">
                {{ tasks.filter(t => t.status === 'completed').length }}
              </div>
            </div>
          </div>

          <!-- 任务列表 -->
          <el-card>
            <template #header>
              <div class="card-header">任务列表</div>
            </template>

            <div style="margin-bottom: 15px;">
              <el-button
                v-for="s in ['all', 'pending', 'in_progress', 'completed']"
                :key="s"
                :type="taskFilter === s ? 'primary' : 'info'"
                size="small"
                @click="taskFilter = s"
              >
                {{ s === 'all' ? '全部' : (s === 'pending' ? '待处理' : (s === 'in_progress' ? '进行中' : '已完成')) }}
              </el-button>
            </div>

            <el-table :data="filteredTasks" stripe>
              <el-table-column prop="title" label="任务标题" min-width="150"></el-table-column>
              <el-table-column prop="description" label="描述" min-width="250"></el-table-column>
              <el-table-column label="优先级" width="80">
                <template #default="{ row }">
                  <el-tag
                    :type="row.priority === 'high' ? 'danger' : (row.priority === 'medium' ? 'warning' : 'info')"
                  >
                    {{ row.priority === 'high' ? '高' : (row.priority === 'medium' ? '中' : '低') }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <el-select v-model="row.status" size="small" @change="updateTaskStatus(row)">
                    <el-option label="待处理" value="pending"></el-option>
                    <el-option label="进行中" value="in_progress"></el-option>
                    <el-option label="已完成" value="completed"></el-option>
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="created_at" label="创建时间" width="180"></el-table-column>
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button type="danger" size="small" link @click="deleteTask(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminAPI } from '@/api/index'
import { ElMessage } from 'element-plus'

// 数据
const messages = ref([])
const tasks = ref([])
const loading = ref(false)
const taskFilter = ref('all')

const newMessage = ref({
  title: '',
  content: '',
  level: 'info',
  target: 'all',
  user_ids: []
})

const newTask = ref({
  title: '',
  description: '',
  priority: 'medium'
})

const mockUsers = ref([
  { id: 1, username: '学生用户1' },
  { id: 2, username: '学生用户2' },
  { id: 3, username: '教师用户1' },
  { id: 4, username: '辅导员用户1' }
])

// 计算属性
const filteredTasks = computed(() => {
  if (taskFilter.value === 'all') return tasks.value
  return tasks.value.filter(t => t.status === taskFilter.value)
})

onMounted(async () => {
  await loadMessages()
  await loadTasks()
})

// 加载消息
const loadMessages = async () => {
  loading.value = true
  try {
    const response = await adminAPI.getMessages()
    if (Array.isArray(response)) {
      messages.value = response
    }
  } catch (error) {
    console.error('加载消息失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载任务
const loadTasks = async () => {
  loading.value = true
  try {
    const response = await adminAPI.getTasks()
    if (Array.isArray(response)) {
      tasks.value = response
    }
  } catch (error) {
    console.error('加载任务失败:', error)
  } finally {
    loading.value = false
  }
}

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.title || !newMessage.value.content) {
    ElMessage.error('请输入消息标题和内容')
    return
  }

  try {
    if (newMessage.value.target === 'all') {
      await adminAPI.broadcastMessage(newMessage.value)
    } else {
      await adminAPI.sendTargetedMessage(newMessage.value)
    }
    ElMessage.success('消息已发送')
    resetMessageForm()
    await loadMessages()
  } catch (error) {
    ElMessage.error('发送消息失败')
  }
}

// 删除消息
const deleteMessage = async (messageId) => {
  try {
    await adminAPI.deleteMessage(messageId)
    ElMessage.success('消息已删除')
    await loadMessages()
  } catch (error) {
    ElMessage.error('删除消息失败')
  }
}

// 创建任务
const createTask = async () => {
  if (!newTask.value.title) {
    ElMessage.error('请输入任务标题')
    return
  }

  try {
    await adminAPI.createTask(newTask.value)
    ElMessage.success('任务已创建')
    resetTaskForm()
    await loadTasks()
  } catch (error) {
    ElMessage.error('创建任务失败')
  }
}

// 更新任务状态
const updateTaskStatus = async (task) => {
  try {
    await adminAPI.updateTaskStatus(task.id, task.status)
    ElMessage.success('任务状态已更新')
  } catch (error) {
    ElMessage.error('更新任务状态失败')
  }
}

// 删除任务
const deleteTask = async (taskId) => {
  try {
    await adminAPI.deleteTask(taskId)
    ElMessage.success('任务已删除')
    await loadTasks()
  } catch (error) {
    ElMessage.error('删除任务失败')
  }
}

// 重置表单
const resetMessageForm = () => {
  newMessage.value = {
    title: '',
    content: '',
    level: 'info',
    target: 'all',
    user_ids: []
  }
}

const resetTaskForm = () => {
  newTask.value = {
    title: '',
    description: '',
    priority: 'medium'
  }
}
</script>

<style scoped>
.admin-messages {
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

.tab-content {
  padding: 20px 0;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stat-title {
  color: #999;
  font-size: 12px;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
