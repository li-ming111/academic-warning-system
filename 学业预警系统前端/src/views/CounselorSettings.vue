<template>
  <div class="counselor-settings">
    <div class="page-header">
      <h1>个人设置</h1>
      <p>管理账户信息和系统偏好</p>
    </div>

    <el-tabs>
      <el-tab-pane label="账户管理">
        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">🔐 修改密码</div>
          </template>
          
          <el-form :model="passwordForm" label-width="120px">
            <el-form-item label="当前密码">
              <el-input v-model="passwordForm.currentPassword" type="password" placeholder="输入当前密码"></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="输入新密码"></el-input>
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="再次输入新密码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updatePassword">确认修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">👤 个人信息</div>
          </template>
          
          <el-form :model="userInfo" label-width="120px">
            <el-form-item label="姓名">
              <el-input v-model="userInfo.name" placeholder="输入姓名"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" type="email" placeholder="输入邮箱"></el-input>
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="userInfo.phone" placeholder="输入电话"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateUserInfo">保存信息</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="通知偏好">
        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">🔔 通知设置</div>
          </template>
          
          <el-form :model="notificationPreferences" label-width="200px">
            <el-form-item label="接收预警通知">
              <el-switch v-model="notificationPreferences.warningNotification"></el-switch>
            </el-form-item>
            <el-form-item label="接收系统消息">
              <el-switch v-model="notificationPreferences.systemMessage"></el-switch>
            </el-form-item>
            <el-form-item label="接收班级通知">
              <el-switch v-model="notificationPreferences.classNotification"></el-switch>
            </el-form-item>
            <el-form-item label="接收学生反馈">
              <el-switch v-model="notificationPreferences.studentFeedback"></el-switch>
            </el-form-item>
            <el-form-item label="邮件提醒">
              <el-switch v-model="notificationPreferences.emailReminder"></el-switch>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateNotificationPreferences">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="工作设置">
        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">💼 工作信息</div>
          </template>
          
          <el-form :model="workSettings" label-width="120px">
            <el-form-item label="学院">
              <el-select v-model="workSettings.college" placeholder="选择学院">
                <el-option label="计算机学院" value="1"></el-option>
                <el-option label="工程学院" value="2"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="办公地点">
              <el-input v-model="workSettings.office" placeholder="输入办公地点"></el-input>
            </el-form-item>
            <el-form-item label="办公电话">
              <el-input v-model="workSettings.officePhone" placeholder="输入办公电话"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateWorkSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const userInfo = ref({
  name: '李四',
  email: 'lisi@example.com',
  phone: '13800138000'
})

const notificationPreferences = ref({
  warningNotification: true,
  systemMessage: true,
  classNotification: true,
  studentFeedback: true,
  emailReminder: false
})

const workSettings = ref({
  college: '1',
  office: '行政楼302室',
  officePhone: '010-12345678'
})

const updatePassword = () => {
  if (!passwordForm.value.currentPassword || !passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    ElMessage.error('请填写所有字段')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  ElMessage.success('密码修改成功')
  passwordForm.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

const updateUserInfo = () => {
  ElMessage.success('个人信息已保存')
}

const updateNotificationPreferences = () => {
  ElMessage.success('通知偏好已保存')
}

const updateWorkSettings = () => {
  ElMessage.success('工作设置已保存')
}
</script>

<style scoped>
.counselor-settings {
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
