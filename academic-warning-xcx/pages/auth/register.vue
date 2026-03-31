<template>
  <view class="register-page">
    <view class="register-wrapper">
      <view class="register-card">
        <view class="register-header">
          <image src="../../assets/vue.svg" alt="哈尔滨信息工程学院" class="logo-image"></image>
          <text class="register-title">智学预警云服务平台</text>
          <text class="register-subtitle">用户注册</text>
        </view>

        <view class="role-tabs">
          <view 
            v-for="role in roles" 
            :key="role.value"
            :class="['role-tab', { active: selectedRole === role.value }]"
            @click="selectRole(role.value)"
          >
            {{ role.label }}
          </view>
        </view>

        <view class="form-group">
          <input 
            v-model="registerForm.username" 
            :placeholder="selectedRole === 'student' ? '学号' : '工号'"
            class="register-input"
            placeholder-class="placeholder"
          />
        </view>
        
        <view class="form-group">
          <input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="密码"
            class="register-input"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-group">
          <input 
            v-model="registerForm.name" 
            placeholder="姓名"
            class="register-input"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-group">
          <input 
            v-model="registerForm.phone" 
            placeholder="手机号"
            class="register-input"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-group">
          <input 
            v-model="registerForm.email" 
            placeholder="邮箱"
            class="register-input"
            placeholder-class="placeholder"
          />
        </view>

        <!-- 教师和辅导员特有字段 -->
        <view v-if="selectedRole === 'teacher'" class="form-group">
          <view class="register-select" @click="showCollegePicker = true">
            <text>{{ getSelectedCollegeName() }}</text>
          </view>
          <picker 
            v-if="showCollegePicker" 
            :range="colleges" 
            range-key="name"
            :value="getCollegeIndex()"
            @change="handleCollegeChange"
            @cancel="showCollegePicker = false"
            @confirm="showCollegePicker = false"
          >
          </picker>
        </view>

        <view v-if="selectedRole === 'counselor'" class="form-group">
          <input 
            v-model="registerForm.majorCode" 
            placeholder="专业编码"
            class="register-input"
            placeholder-class="placeholder"
          />
        </view>

        <button @click="handleRegister" type="primary" class="register-btn">注册</button>

        <view class="login-link">
          <text>已有账号？</text>
          <navigator url="/pages/auth/login" class="link">立即登录</navigator>
        </view>

        <view v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </view>

        <view v-if="successMessage" class="success-message">
          {{ successMessage }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiClient } from '../../services/api'

const selectedRole = ref('student')
const registerForm = ref({
  username: '',
  password: '',
  name: '',
  phone: '',
  email: '',
  collegeId: '',
  majorCode: ''
})
const errorMessage = ref('')
const successMessage = ref('')
const colleges = ref([])
const showCollegePicker = ref(false)

const roles = [
  { label: '学生', value: 'student' },
  { label: '教师', value: 'teacher' },
  { label: '辅导员', value: 'counselor' }
]

const selectRole = (role) => {
  selectedRole.value = role
  // 重置表单
  registerForm.value = {
    username: '',
    password: '',
    name: '',
    phone: '',
    email: '',
    collegeId: '',
    majorCode: ''
  }
  errorMessage.value = ''
  successMessage.value = ''
}

const loadColleges = async () => {
  try {
    const data = await apiClient.getColleges()
    colleges.value = data
  } catch (error) {
    console.error('获取学院列表失败:', error)
  }
}

const getSelectedCollegeName = () => {
  if (!registerForm.value.collegeId) {
    return '请选择学院'
  }
  const college = colleges.value.find(c => c.id == registerForm.value.collegeId)
  return college ? college.name : '请选择学院'
}

const getCollegeIndex = () => {
  if (!registerForm.value.collegeId) {
    return 0
  }
  const index = colleges.value.findIndex(c => c.id == registerForm.value.collegeId)
  return index >= 0 ? index : 0
}

const handleCollegeChange = (e) => {
  const index = e.detail.value
  if (colleges.value[index]) {
    registerForm.value.collegeId = colleges.value[index].id
  }
}

const handleRegister = async () => {
  // 验证输入
  if (!registerForm.value.username) {
    errorMessage.value = selectedRole.value === 'student' ? '请输入学号' : '请输入工号'
    return
  }
  if (!registerForm.value.password) {
    errorMessage.value = '请输入密码'
    return
  }
  if (!registerForm.value.name) {
    errorMessage.value = '请输入姓名'
    return
  }
  if (!registerForm.value.phone) {
    errorMessage.value = '请输入手机号'
    return
  }
  if (!registerForm.value.email) {
    errorMessage.value = '请输入邮箱'
    return
  }

  if (selectedRole.value === 'teacher' && !registerForm.value.collegeId) {
    errorMessage.value = '请选择学院'
    return
  }

  if (selectedRole.value === 'counselor' && !registerForm.value.majorCode) {
    errorMessage.value = '请输入专业编码'
    return
  }

  try {
    let response
    if (selectedRole.value === 'student') {
      response = await apiClient.registerStudent({
        studentId: registerForm.value.username,
        password: registerForm.value.password,
        name: registerForm.value.name,
        phone: registerForm.value.phone,
        email: registerForm.value.email
      })
    } else if (selectedRole.value === 'teacher') {
      response = await apiClient.registerTeacher({
        username: registerForm.value.username,
        password: registerForm.value.password,
        name: registerForm.value.name,
        collegeId: registerForm.value.collegeId,
        phone: registerForm.value.phone,
        email: registerForm.value.email
      })
    } else if (selectedRole.value === 'counselor') {
      response = await apiClient.registerCounselor({
        username: registerForm.value.username,
        password: registerForm.value.password,
        name: registerForm.value.name,
        majorCode: registerForm.value.majorCode,
        phone: registerForm.value.phone,
        email: registerForm.value.email
      })
    }

    successMessage.value = '注册成功！'
    errorMessage.value = ''

    // 3秒后跳转到登录页
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/auth/login' })
    }, 3000)
  } catch (error) {
    console.error('注册失败:', error)
    errorMessage.value = error.message || '注册失败，请重试'
    successMessage.value = ''
  }
}

onMounted(() => {
  loadColleges()
})
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  background-color: #f5f5f5;
}

.register-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.register-card {
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  padding: 40rpx 50rpx;
  width: 100%;
  max-width: 480rpx;
}

.register-header {
  text-align: center;
  margin-bottom: 30rpx;
}

.logo-image {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 16rpx;
  border-radius: 12rpx;
  background: white;
  padding: 8rpx;
  border: 1rpx solid #e0e0e0;
}

.register-title {
  font-size: 24rpx;
  color: #1A3D5C;
  font-weight: 700;
  display: block;
  margin-bottom: 8rpx;
}

.register-subtitle {
  font-size: 18rpx;
  color: #666;
  font-weight: 400;
}

.role-tabs {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30rpx;
  border-bottom: 1rpx solid #e0e0e0;
}

.role-tab {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  font-size: 16rpx;
  color: #666;
  border-bottom: 2rpx solid transparent;
  cursor: pointer;
}

.role-tab.active {
  color: #4facfe;
  border-bottom-color: #4facfe;
  font-weight: 600;
}

.form-group {
  margin-bottom: 16rpx;
}

.register-input {
  height: 44rpx;
  padding: 0 20rpx;
  border: 1.5rpx solid #E0E0E0;
  border-radius: 12rpx;
  font-size: 16rpx;
  width: 100%;
  background-color: #F9FAFB;
}

.register-select {
  height: 44rpx;
  padding: 0 20rpx;
  border: 1.5rpx solid #E0E0E0;
  border-radius: 12rpx;
  font-size: 16rpx;
  width: 100%;
  background-color: #F9FAFB;
}

.register-btn {
  width: 100%;
  height: 44rpx;
  font-size: 16rpx;
  font-weight: 700;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  margin-top: 28rpx;
  border-radius: 12rpx;
  color: white;
  box-shadow: 0 6rpx 20rpx rgba(79, 172, 254, 0.3);
}

.login-link {
  text-align: center;
  margin-top: 20rpx;
  font-size: 14rpx;
  color: #666;
}

.link {
  color: #4facfe;
  text-decoration: none;
  margin-left: 4rpx;
}

.error-message {
  background-color: #FFEBEE;
  border-left: 4rpx solid #FF5252;
  color: #D32F2F;
  padding: 12rpx;
  border-radius: 8rpx;
  margin-top: 20rpx;
  font-size: 14rpx;
}

.success-message {
  background-color: #E8F5E8;
  border-left: 4rpx solid #4CAF50;
  color: #2E7D32;
  padding: 12rpx;
  border-radius: 8rpx;
  margin-top: 20rpx;
  font-size: 14rpx;
}

.placeholder {
  color: #999;
}
</style>