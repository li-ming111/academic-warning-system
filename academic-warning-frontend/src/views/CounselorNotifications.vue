<template>
  <div class="counselor-notifications">
    <div class="page-header">
      <h1>批量通知</h1>
      <p>向学生发送批量通知消息</p>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">创建新通知</div>
      </template>
      
      <el-form :model="notificationForm" label-width="120px">
        <el-form-item label="通知类型">
          <el-select v-model="notificationForm.type" placeholder="选择通知类型">
            <el-option label="预警通知" value="warning"></el-option>
            <el-option label="重要通知" value="important"></el-option>
            <el-option label="普通通知" value="normal"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="收件人">
          <el-select v-model="notificationForm.recipients" multiple placeholder="选择接收学生">
            <el-option label="全部学生" value="all"></el-option>
            <el-option label="预警学生" value="warning-students"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="通知标题">
          <el-input v-model="notificationForm.title" placeholder="输入通知标题"></el-input>
        </el-form-item>

        <el-form-item label="通知内容">
          <el-input v-model="notificationForm.content" type="textarea" :rows="4" placeholder="输入通知内容"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="sendNotification">发送通知</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">通知历史</div>
      </template>
      
      <el-table :data="notificationHistory" stripe>
        <el-table-column prop="title" label="标题" width="200"></el-table-column>
        <el-table-column prop="type" label="类型" width="120"></el-table-column>
        <el-table-column prop="recipientCount" label="接收人数" width="100"></el-table-column>
        <el-table-column prop="createdAt" label="发送时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" link>查看详情</el-button>
            <el-button type="danger" size="small" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const notificationForm = ref({
  type: 'normal',
  recipients: [],
  title: '',
  content: ''
})

const notificationHistory = ref([
  {
    id: 1,
    title: '学分预警通知',
    type: 'warning',
    recipientCount: 12,
    createdAt: '2025-12-13 10:00:00'
  }
])

const sendNotification = () => {
  if (!notificationForm.value.title || !notificationForm.value.content) {
    ElMessage.error('请填写通知标题和内容')
    return
  }
  ElMessage.success('通知已发送')
  notificationForm.value = {
    type: 'normal',
    recipients: [],
    title: '',
    content: ''
  }
}
</script>

<style scoped>
.counselor-notifications {
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
