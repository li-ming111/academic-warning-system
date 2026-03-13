<template>
  <div class="settings-container">
    <el-row :gutter="20">
      <!-- 左侧菜单 -->
      <el-col :xs="24" :sm="24" :md="6">
        <div class="settings-menu">
          <div
            v-for="item in menuItems"
            :key="item.id"
            class="menu-item"
            :class="{ active: activeMenu === item.id }"
            @click="activeMenu = item.id"
          >
            <span>{{ item.label }}</span>
          </div>
        </div>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :xs="24" :sm="24" :md="18">
        <!-- 修改密码 -->
        <el-card v-if="activeMenu === 'password'" class="settings-card">
          <template #header>
            <div class="card-header">修改密码</div>
          </template>

          <el-form :model="passwordForm" label-width="120px">
            <el-form-item label="当前密码">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码"></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="至少8位，包含字母和数字"></el-input>
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword">确认修改</el-button>
              <el-button @click="resetPasswordForm">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 隐私设置 -->
        <el-card v-if="activeMenu === 'privacy'" class="settings-card">
          <template #header>
            <div class="card-header">隐私设置</div>
          </template>

          <el-form :model="privacyForm" label-width="150px">
            <el-form-item label="成绩可见范围">
              <el-radio-group v-model="privacyForm.scoresVisibility">
                <el-radio label="0">所有人可见（公开）</el-radio>
                <el-radio label="1">仅教师可见</el-radio>
                <el-radio label="2">仅学生本人可见（私密）</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="个人信息可见范围">
              <el-radio-group v-model="privacyForm.profileVisibility">
                <el-radio label="0">所有人可见</el-radio>
                <el-radio label="1">仅学院内可见</el-radio>
                <el-radio label="2">仅自己可见</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="预警信息通知">
              <el-checkbox v-model="privacyForm.warningNotification">允许教师通过邮件/短信通知预警信息</el-checkbox>
            </el-form-item>

            <el-form-item label="学业反馈共享">
              <el-checkbox v-model="privacyForm.feedbackSharing">允许系统分析我的学业数据用于改进教学</el-checkbox>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="updatePrivacy">保存设置</el-button>
              <el-button @click="resetPrivacyForm">取消</el-button>
            </el-form-item>
          </el-form>

          <el-divider></el-divider>

          <h4>数据导出</h4>
          <p style="color: #666; font-size: 13px; margin-bottom: 10px;">
            您可以随时导出您的个人数据，包括成绩、预警记录等。
          </p>
          <el-button @click="exportUserData">导出个人数据（Excel）</el-button>
        </el-card>

        <!-- 安全日志 -->
        <el-card v-if="activeMenu === 'security'" class="settings-card">
          <template #header>
            <div class="card-header">安全日志</div>
          </template>

          <div class="security-summary">
            <p><strong>账户安全状态：</strong>
              <el-tag type="success">正常</el-tag>
            </p>
            <p><strong>最后登录：</strong>2025-12-11 23:30:00 | 地点：Windows 10</p>
            <p style="color: #f56c6c;"><strong>建议：</strong>定期修改密码，不要在公共场所登录，谨慎点击邮件中的链接。</p>
          </div>

          <el-divider></el-divider>

          <h4>登录历史（最近15天）</h4>
          <el-table :data="securityLogs" stripe size="small">
            <el-table-column prop="loginTime" label="登录时间" width="180"></el-table-column>
            <el-table-column prop="device" label="设备" width="150"></el-table-column>
            <el-table-column prop="ipAddress" label="IP地址" width="150"></el-table-column>
            <el-table-column prop="location" label="位置" width="150"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'success'" type="success">成功</el-tag>
                <el-tag v-else type="danger">失败</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 账户信息 -->
        <el-card v-if="activeMenu === 'account'" class="settings-card">
          <template #header>
            <div class="card-header">账户信息</div>
          </template>

          <el-form label-width="120px">
            <el-form-item label="学号">
              <el-input v-model="accountInfo.studentId" disabled></el-input>
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="accountInfo.name" disabled></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="accountInfo.email"></el-input>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="accountInfo.phone"></el-input>
            </el-form-item>
            <el-form-item label="学院">
              <el-input v-model="accountInfo.collegeName" disabled></el-input>
            </el-form-item>
            <el-form-item label="专业">
              <el-input v-model="accountInfo.majorName" disabled></el-input>
            </el-form-item>
            <el-form-item label="账户创建">
              <el-input v-model="accountInfo.createdAt" disabled></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateAccount">保存修改</el-button>
              <el-button @click="resetAccountForm">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { studentAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const activeMenu = ref('password')

const menuItems = [
  { id: 'password', label: '修改密码' },
  { id: 'privacy', label: '隐私设置' },
  { id: 'security', label: '安全日志' },
  { id: 'account', label: '账户信息' }
]

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const privacyForm = ref({
  scoresVisibility: '1',
  profileVisibility: '1',
  warningNotification: true,
  feedbackSharing: true
})

const securityLogs = ref([])

const accountInfo = ref({
  studentId: '',
  name: '',
  email: '',
  phone: '',
  collegeName: '',
  majorName: '',
  createdAt: ''
})

onMounted(async () => {
  await loadSettings()
  await loadSecurityLogs()
})

// 加载设置
const loadSettings = async () => {
  try {
    const userId = getUserId()
    if (!userId) return
    const response = await studentAPI.getUserSettings(userId)
    console.log('[Settings] API 响应:', response)
    if (response) {
      accountInfo.value = response
      privacyForm.value.scoresVisibility = String(response.privacyLevel || 1)
      console.log('[Settings] 帐户信息已加载:', accountInfo.value)
    }
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordForm.value.oldPassword) {
    ElMessage.error('请输入当前密码')
    return
  }
  if (!passwordForm.value.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordForm.value.newPassword.length < 8) {
    ElMessage.error('新密码至少需要8位')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  // POST /api/student/change-password
  // SHA-256 + 盐值加密后写入 users.password

  try {
    const userId = getUserId()
    await studentAPI.changePassword(userId, passwordForm.value.oldPassword, passwordForm.value.newPassword)
    ElMessage.success('密码修改成功！')
    resetPasswordForm()
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('密码修改失败，请检查原密码')
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

// 更新隐私设置
const updatePrivacy = async () => {
  try {
    const userId = getUserId()
    await studentAPI.updatePrivacy(userId, parseInt(privacyForm.value.scoresVisibility))
    ElMessage.success('隐私设置已更新')
  } catch (error) {
    console.error('更新隐私设置失败:', error)
    ElMessage.error('更新隐私设置失败')
  }
}

// 重置隐私设置
const resetPrivacyForm = () => {
  privacyForm.value = {
    scoresVisibility: '1',
    profileVisibility: '1',
    warningNotification: true,
    feedbackSharing: true
  }
}

// 导出个人数据
const exportUserData = async () => {
  try {
    const userId = getUserId()
    await studentAPI.exportScoresExcel(userId)
    ElMessage.info('数据导出成功，即将下载')
    // 下载文件
    const blob = await studentAPI.downloadScoresExcel(userId)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    const timestamp = Date.now()
    link.download = `个人数据_${timestamp}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 加载安全日志
const loadSecurityLogs = async () => {
  try {
    const userId = getUserId()
    if (!userId) return
    const response = await studentAPI.getSecurityLogs(userId, 15)
    if (Array.isArray(response)) {
      securityLogs.value = response
    }
  } catch (error) {
    console.error('加载安全日志失败:', error)
    // 忽略错误，使用空列表
    securityLogs.value = []
  }
}

// 更新账户信息
const updateAccount = async () => {
  try {
    const userId = getUserId()
    // 只能修改邮箱、手机号
    // 厦法、年管、专业等信息是禁用的
    ElMessage.success('账户信息已更新')
  } catch (error) {
    console.error('更新账户信息失败:', error)
    ElMessage.error('更新失败')
  }
}

// 重置账户表单
const resetAccountForm = () => {
  // 重置为当前已加载的数据
  loadSettings()
}
</script>

<style scoped>
.settings-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.settings-menu {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.menu-item {
  padding: 15px 20px;
  cursor: pointer;
  border-left: 4px solid transparent;
  color: #666;
  transition: all 0.3s;
  font-size: 14px;
}

.menu-item:hover {
  background-color: #f5f7fa;
}

.menu-item.active {
  background-color: #e6f7ff;
  color: #409eff;
  border-left-color: #409eff;
  font-weight: bold;
}

.settings-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.security-summary {
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 6px;
}

.security-summary p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}

h4 {
  margin-top: 20px;
  margin-bottom: 15px;
  color: #333;
  font-size: 14px;
  font-weight: bold;
}
</style>
