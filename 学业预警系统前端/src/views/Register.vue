<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-title">学业预警系统 - 学生注册</div>
      </template>

      <el-form :model="form" @submit.prevent="handleRegister" label-width="100px">
        <el-form-item label="学号">
          <el-input v-model="form.studentId" placeholder="请输入10位学号" maxlength="10" @input="onStudentIdChange"></el-input>
          <span v-if="form.studentId.length === 10" class="student-id-hint">✓ 学号有效</span>
        </el-form-item>

        <el-form-item label="专业">
          <el-input v-model="form.majorName" disabled placeholder="自动填充" />
          <p v-if="form.majorName" class="major-hint">{{ form.collegeName }} | {{ form.majorName }}</p>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入真实姓名"></el-input>
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :value="defaultPassword"></el-input>
          <span class="default-password-hint">默认密码: 123456</span>
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11"></el-input>
          <span v-if="isValidPhone" class="valid-hint">✓ 手机号有效</span>
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
          <span v-if="isValidEmail" class="valid-hint">✓ 邮箱有效</span>
        </el-form-item>

        <el-form-item>
          <div class="register-actions">
            <el-button type="primary" @click="handleRegister" :loading="loading" class="register-btn">注册账号</el-button>
            <router-link to="/login" class="login-link">已有账号？返回登录</router-link>
          </div>
        </el-form-item>
      </el-form>

      <el-alert v-if="errorMessage" :title="errorMessage" type="error" closable @close="errorMessage = ''" style="margin-top: 20px;"></el-alert>
      <el-alert v-if="successMessage" :title="successMessage" type="success" closable @close="successMessage = ''" style="margin-top: 20px;"></el-alert>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import apiClient from '@/api/client'

const router = useRouter()
const API_BASE_URL = 'http://localhost:8081/api'
const form = ref({
  studentId: '',
  majorName: '',
  collegeName: '',
  majorId: null,
  collegeId: null,
  name: '',
  username: '',
  password: '123456',
  phone: '',
  email: ''
})

const defaultPassword = ref('123456')

const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

// 计算属性：验证手机号和邮箱
const isValidPhone = computed(() => /^\d{11}$/.test(form.value.phone))
const isValidEmail = computed(() => /@/.test(form.value.email) && /\./.test(form.value.email))

// 学号变化时自动匹配专业
const onStudentIdChange = async () => {
  if (form.value.studentId.length !== 10) {
    form.value.majorName = ''
    form.value.collegeName = ''
    return
  }

  // 智能解析学号第5-6位
  const majorCode = form.value.studentId.substring(4, 6)
  
  // 从后端获取专业信息
  try {
    const majors = await apiClient.get('/admin/majors')
    if (majors) {
      const major = majors.find(m => m.code === majorCode)
      if (major) {
        form.value.majorName = major.name
        form.value.majorId = major.id
        // 获取学院信息
        const college = await apiClient.get(`/admin/colleges/${major.collegeId}`)
        if (college) {
          form.value.collegeName = college.name
          form.value.collegeId = major.collegeId
        }
      } else {
        form.value.majorName = '（未找到匹配专业）'
        form.value.collegeName = ''
      }
    }
  } catch (error) {
    console.error('获取专业数据失败:', error)
    form.value.majorName = '（加载失败）'
  }
}

// 注册提交
const handleRegister = async () => {
  // 验证表单
  if (!form.value.studentId || form.value.studentId.length !== 10) {
    errorMessage.value = '请输入有效的10位学号'
    return
  }
  if (!form.value.name) {
    errorMessage.value = '请输入姓名'
    return
  }
  if (!form.value.password || form.value.password.length < 6) {
    errorMessage.value = '密码至少需要6位'
    return
  }
  if (!isValidPhone.value) {
    errorMessage.value = '请输入有效的11位手机号'
    return
  }
  if (!isValidEmail.value) {
    errorMessage.value = '请输入有效的邮箱地址'
    return
  }

  loading.value = true
  try {
    // 调用后端注册API
    const response = await apiClient.post('/auth/register/student', {
      studentId: form.value.studentId,
      name: form.value.name,
      password: form.value.password,
      phone: form.value.phone,
      email: form.value.email
    })

    successMessage.value = '注册成功！请返回登录'
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error) {
    console.error('注册错误:', error)
    errorMessage.value = error.message || '注册失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: url('../assets/bj.jpg') center/cover no-repeat;
  padding: 20px;
  position: relative;
}

.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
  z-index: 0;
}

.register-card {
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 1;
}

.register-title {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.student-id-hint {
  color: #67c23a;
  font-size: 12px;
  margin-left: 10px;
}

.major-hint {
  margin: 5px 0 0 0;
  font-size: 13px;
  color: #666;
}

.valid-hint {
  color: #67c23a;
  font-size: 12px;
  margin-left: 10px;
}

.default-password-hint {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}

.login-link {
  margin-left: 20px;
  color: #409eff;
  text-decoration: none;
}

.login-link:hover {
  text-decoration: underline;
}

.register-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.register-btn {
  background-color: #409eff;
  border-color: #409eff;
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 14px;
}

.register-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.login-link {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
  margin-left: 0;
}
</style>
