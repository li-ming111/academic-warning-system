误] app.json: 在项目根目录未找到 app.json (env: Windows,mp,2.01.2510280; lib: 3.15.1)
         <template>
  <view class="login-page">
    <view class="login-wrapper">
      <view class="login-card">
        <view class="login-header">
          <image src="../../assets/vue.svg" alt="哈尔滨信息工程学院" class="logo-image"></image>
          <text class="login-title">智学预警云服务平台</text>
          <text class="login-subtitle">智能学业预警与帮扶云服务平台</text>
        </view>

        <view class="form-group">
          <input 
            v-model="loginForm.username" 
            placeholder="学号或工号"
            class="login-input"
            placeholder-class="placeholder"
          />
        </view>
        
        <view class="form-group">
          <input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码"
            class="login-input"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-group">
          <input 
            v-model="loginForm.captcha" 
            placeholder="验证码"
            class="login-input captcha-input"
            placeholder-class="placeholder"
          />
          <view class="captcha-btn" @click="generateCaptcha">
            <text>{{ captchaCode }}</text>
          </view>
        </view>

        <button @click="handleLogin" type="primary" class="login-btn">登录</button>

        <view class="login-links">
          <text>快速注册：</text>
          <navigator url="/pages/auth/register" class="link">学生</navigator>
          <navigator url="/pages/auth/register" class="link">教师</navigator>
          <navigator url="/pages/auth/register" class="link">辅导员</navigator>
        </view>

        <view v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiClient } from '../../services/api'

const loginForm = ref({
  username: '',
  password: '',
  captcha: ''
})

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
    errorMessage.value = '请输入学号或工号'
    return
  }
  if (!loginForm.value.password) {
    errorMessage.value = '请输入密码'
    return
  }
  if (!loginForm.value.captcha) {
    errorMessage.value = '请输入验证码'
    return
  }

  try {
    const response = await apiClient.login(loginForm.value.username, loginForm.value.password)

    if (response && response.token) {
      // 保存token和用户信息
      uni.setStorageSync('token', response.token)
      uni.setStorageSync('userId', response.userId)
      uni.setStorageSync('username', response.username)
      uni.setStorageSync('role', response.role)
      
      // 保存用户姓名
      if (response.name) {
        uni.setStorageSync('userName', response.name)
      } else {
        // 根据角色设置默认名称
        const role = String(response.role)
        if (role === '2' || role === 'teacher') {
          uni.setStorageSync('userName', '教师')
        } else if (role === '1' || role === 'student') {
          uni.setStorageSync('userName', '学生')
        } else if (role === '3' || role === 'admin') {
          uni.setStorageSync('userName', '管理员')
        } else if (role === '4' || role === 'counselor') {
          uni.setStorageSync('userName', '辅导员')
        } else {
          uni.setStorageSync('userName', response.username || '用户')
        }
      }

      // 保存学号/工号
      const role = String(response.role)
      if (role === '1' || role === 'student') {
        uni.setStorageSync('studentId', response.username)
      } else if (response.studentId) {
        uni.setStorageSync('studentId', response.studentId)
      }

      if (response.teacherId) uni.setStorageSync('teacherId', response.teacherId)
      if (response.counselorId) uni.setStorageSync('counselorId', response.counselorId)
      if (response.adminId) uni.setStorageSync('adminId', response.adminId)

      uni.showToast({
        title: '登录成功',
        icon: 'success'
      })
      
      // 根据用户角色跳转
      if (role === '1' || role === 'student') {
        uni.navigateTo({ url: '/pages/student/dashboard' })
      } else if (role === '2' || role === 'teacher') {
        uni.navigateTo({ url: '/pages/teacher/dashboard' })
      } else if (role === '4' || role === 'counselor') {
        uni.navigateTo({ url: '/pages/counselor/dashboard' })
      } else if (role === '3' || role === 'admin') {
        uni.navigateTo({ url: '/pages/admin/dashboard' })
      } else {
        uni.showToast({
          title: '未能识别用户角色，请联系管理员',
          icon: 'none'
        })
      }
    } else {
      errorMessage.value = response?.message || '登录失败'
    }
  } catch (error) {
    console.error('登录错误:', error)
    const errorMsg = error.response?.data?.message || error.message || '登录失败，请重试'
    errorMessage.value = errorMsg
    generateCaptcha()
    loginForm.value.captcha = ''
  }
}

onMounted(() => {
  // 清除可能残留的旧token
  uni.removeStorageSync('token')
  uni.removeStorageSync('userId')
  uni.removeStorageSync('username')
  uni.removeStorageSync('role')
  uni.removeStorageSync('studentId')
  uni.removeStorageSync('teacherId')
  uni.removeStorageSync('counselorId')
  uni.removeStorageSync('adminId')
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
  background-color: #f5f5f5;
}

.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.login-card {
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  padding: 60rpx 50rpx;
  width: 100%;
  max-width: 480rpx;
}

.login-header {
  text-align: center;
  margin-bottom: 40rpx;
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

.login-title {
  font-size: 24rpx;
  color: #1A3D5C;
  font-weight: 700;
  display: block;
  margin-bottom: 8rpx;
}

.login-subtitle {
  font-size: 14rpx;
  color: #666;
  font-weight: 400;
}

.form-group {
  margin-bottom: 12rpx;
  display: flex;
  align-items: center;
}

.login-input {
  height: 44rpx;
  padding: 0 20rpx;
  border: 1.5rpx solid #E0E0E0;
  border-radius: 12rpx;
  font-size: 16rpx;
  flex: 1;
  background-color: #F9FAFB;
}

.captcha-input {
  flex: 2;
  margin-right: 10rpx;
}

.captcha-btn {
  width: 110rpx;
  height: 44rpx;
  border: 1.5rpx solid #E0E0E0;
  border-radius: 12rpx;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  font-weight: 700;
  font-size: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.login-btn {
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

.login-links {
  text-align: center;
  margin-top: 30rpx;
  font-size: 14rpx;
  color: #4facfe;
}

.link {
  color: #4facfe;
  text-decoration: none;
  margin: 0 6rpx;
  position: relative;
  padding-bottom: 2rpx;
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

.placeholder {
  color: #999;
}
</style>