<template>
  <div class="login-page">
    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <div class="logo-icon"></div>
          <h1>学业预警系统</h1>
          <p>智能学业预警与帮扶平台</p>
        </div>

        <el-form :model="loginForm" @submit.prevent="handleLogin" label-width="0px">
          <el-form-item>
            <el-input 
              v-model="loginForm.username" 
              placeholder="学号或工号"
              clearable
              class="login-input"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item>
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="密码"
              clearable
              class="login-input"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <div class="captcha-row">
              <el-input 
                v-model="loginForm.captcha" 
                placeholder="验证码"
                clearable
                class="login-input captcha-input"
              >
                <template #prefix>
                  <el-icon><CircleCheck /></el-icon>
                </template>
              </el-input>
              <button type="button" @click="generateCaptcha" class="captcha-btn">
                {{ captchaCode }}
              </button>
            </div>
          </el-form-item>

          <el-button @click="handleLogin" type="primary" class="login-btn">登录</el-button>
        </el-form>

        <div class="login-links">
          <span>快速注册：</span>
          <router-link to="/register" class="link">👨‍🎓 学生</router-link>
          <router-link to="/teacher-register" class="link">👨‍🏫 教师</router-link>
          <router-link to="/counselor-register" class="link">👔 辅导员</router-link>
          <router-link to="/admin-register" class="link">⚙️ 管理员</router-link>
        </div>

        <el-alert 
          v-if="errorMessage"
          :title="errorMessage" 
          type="error"
          closable
          @close="errorMessage = ''"
          style="margin-top: 20px;">
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api'
import { ElMessage } from 'element-plus'
import { User, Lock, CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()
const loginForm = ref({
  username: '',
  password: '',
  captcha: ''
})
const loading = ref(false)
const errorMessage = ref('')
const captchaCode = ref('')

// 生成验证码
const generateCaptcha = () => {
  const code = Math.random().toString(36).substring(2, 8).toUpperCase()
  captchaCode.value = code
}

// 初始化验证码
const initCaptcha = () => {
  generateCaptcha()
}

const handleLogin = async () => {
  // 验证输入
  if (!loginForm.value.username) {
    ElMessage.error('请输入学号或工号')
    return
  }
  if (!loginForm.value.password) {
    ElMessage.error('请输入密码')
    return
  }
  if (!loginForm.value.captcha) {
    ElMessage.error('请输入验证码')
    return
  }

  // 验证验证码
  // if (loginForm.value.captcha !== captchaCode.value) {
  //   ElMessage.error('验证码错误')
  //   generateCaptcha()
  //   loginForm.value.captcha = ''
  //   return
  // }

  loading.value = true
  try {
    const response = await authAPI.login(loginForm.value.username, loginForm.value.password)

    if (response && response.token) {
      // 保存token和用户信息
      localStorage.setItem('token', response.token)
      localStorage.setItem('userId', response.userId)
      localStorage.setItem('username', response.username)
      localStorage.setItem('role', response.role)
      // 保存用户姓名（name）
      if (response.name) {
        localStorage.setItem('userName', response.name)
      } else {
        // 如果后端没有返回 name，使用 username 作为默认值
        localStorage.setItem('userName', response.username || '')
      }
      // 保存不同角色的profile ID
      if (response.studentId) localStorage.setItem('studentId', response.studentId)
      if (response.teacherId) localStorage.setItem('teacherId', response.teacherId)
      if (response.counselorId) localStorage.setItem('counselorId', response.counselorId)
      if (response.adminId) localStorage.setItem('adminId', response.adminId)
      ElMessage.success('登录成功')
      
      // 根据用户角色跳转（角色值：1=学生, 2=教师, 3=管理员, 4=辅导员）
      const role = String(response.role)  // 转换为字符串确保比较正确
      console.log('登录成功，role:', role, 'type:', typeof role)
      if (role === '1' || role === 'student') {
        router.push('/dashboard')
      } else if (role === '2' || role === 'teacher') {
        router.push('/teacher/dashboard')
      } else if (role === '4' || role === 'counselor') {
        router.push('/counselor-dashboard')
      } else if (role === '3' || role === 'admin') {
        router.push('/admin/dashboard')
      } else {
        // 不明角色，水平不跳转，提示错誤
        ElMessage.warning('未能识别用户角色，请联系管理员')
        return
      }
    } else {
      errorMessage.value = response?.message || '登录失败'
    }
  } catch (error) {
    console.error('登录错误:', error)
    // 获取真实的错误信息
    const errorMsg = error.response?.data?.message || error.message || '登录失败，请重试'
    errorMessage.value = errorMsg
    generateCaptcha()
    loginForm.value.captcha = ''
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 清除可能残留的过旧 token
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('studentId')
  localStorage.removeItem('teacherId')
  localStorage.removeItem('counselorId')
  localStorage.removeItem('adminId')
  initCaptcha()
})
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  background: url('../assets/bj.jpg') center/cover no-repeat;
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
  z-index: 0;
}

.login-wrapper {
  position: relative;
  z-index: 10;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 60px 50px;
  width: 100%;
  max-width: 480px;
  animation: slideUp 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 12px;
  display: inline-block;
  animation: bounce 2s ease-in-out infinite;
}

.login-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  color: #1A3D5C;
  font-weight: 700;
  letter-spacing: 1px;
}

.login-header p {
  margin: 0;
  font-size: 14px;
  color: #666;
  font-weight: 400;
}

.login-input {
  height: 44px;
  margin-bottom: 12px;
}

.login-input :deep(.el-input__wrapper) {
  background-color: #F9FAFB;
  border: 1.5px solid #E0E0E0;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.login-input :deep(.el-input__wrapper:hover) {
  border-color: #551EFF;
  background-color: #F0F4FF;
  box-shadow: 0 4px 12px rgba(85, 31, 255, 0.1);
}

.login-input :deep(.el-input__wrapper:focus-within) {
  border-color: #551EFF;
  background-color: #FFFFFF;
  box-shadow: 0 8px 20px rgba(85, 31, 255, 0.2);
  transform: translateY(-2px);
}

.captcha-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-input {
  flex: 1;
}

.captcha-btn {
  width: 110px;
  height: 44px;
  border: 1.5px solid #E0E0E0;
  border-radius: 12px;
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  color: white;
  font-weight: 700;
  font-size: 12px;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.captcha-btn:hover {
  background: linear-gradient(135deg, #764BA2 0%, #667EEA 100%);
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  border: none;
  margin-top: 28px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
}

.login-btn:hover {
  background: linear-gradient(135deg, #764BA2 0%, #667EEA 100%);
  box-shadow: 0 10px 28px rgba(102, 126, 234, 0.4);
  transform: translateY(-3px);
}

.login-links {
  text-align: center;
  margin-top: 30px;
  font-size: 14px;
  color: #551EFF;
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.link {
  color: #551EFF;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  margin: 0 6px;
  position: relative;
  padding-bottom: 2px;
}

.link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: #551EFF;
  transition: width 0.3s ease;
}

.link:hover::after {
  width: 100%;
}

.link:hover {
  color: #764BA2;
  transform: scale(1.05);
}

/* 错误提示优化 */
.el-alert {
  background-color: #FFEBEE;
  border-color: #FFCDD2;
  color: #D32F2F;
}

.el-alert__title {
  font-weight: 600;
}
</style>