<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-box">
        <div class="register-header">
          <h1>辅导员注册</h1>
          <p>学院管理权限 · 学生情况概览</p>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="工号" prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="请输入10位工号"
              maxlength="10"
              clearable
            >
              <template #prefix>
                <el-icon><Postcard /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="姓名" prop="name">
            <el-input 
              v-model="form.name" 
              placeholder="请输入真实姓名"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="所属学院" prop="collegeId">
            <el-select 
              v-model="form.collegeId" 
              placeholder="请选择学院"
              style="width: 100%"
              clearable
              @change="handleCollegeChange"
            >
              <el-option
                v-for="college in colleges"
                :key="college.id"
                :label="college.name"
                :value="college.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="所属专业" prop="majorCode">
            <el-select 
              v-model="form.majorCode" 
              placeholder="请选择专业"
              style="width: 100%"
              clearable
              :disabled="!form.collegeId"
            >
              <el-option
                v-for="major in majors"
                :key="major.code"
                :label="major.name"
                :value="major.code"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input 
              v-model="form.phone" 
              placeholder="请输入11位手机号"
              maxlength="11"
              clearable
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input 
              v-model="form.email" 
              placeholder="请输入邮箱地址"
              clearable
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码（至少6位）"
              show-password
              clearable
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
            <span class="default-password-hint">默认密码: 123456</span>
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="form.confirmPassword" 
              type="password" 
              placeholder="请再次输入密码"
              show-password
              clearable
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <div class="register-actions">
              <el-button 
                type="primary" 
                @click="handleRegister" 
                :loading="loading"
                class="register-btn"
              >
                注册账号
              </el-button>
              <router-link to="/login" class="login-link">已有账号？返回登录</router-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, Message, Postcard } from '@element-plus/icons-vue'
import { authAPI } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const colleges = ref([])
const majors = ref([])

const form = ref({
  username: '',
  name: '',
  collegeId: '',
  majorCode: '',
  phone: '',
  email: '',
  password: '123456',
  confirmPassword: '123456'
})

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱地址'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码至少6位'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.value.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入工号', trigger: 'blur' },
    { pattern: /^\d{10}$/, message: '工号必须为10位数字', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  majorCode: [{ required: true, message: '请选择专业', trigger: 'change' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  email: [{ validator: validateEmail, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
}

// 获取专业列表
const getMajorsByCollege = async (collegeId) => {
  try {
    const response = await authAPI.getMajorsByCollege(collegeId)
    majors.value = response || []
  } catch (error) {
    ElMessage.error('加载专业列表失败')
    majors.value = []
  }
}

// 学院变化处理
const handleCollegeChange = (collegeId) => {
  form.value.majorCode = '' // 清空专业选择
  if (collegeId) {
    getMajorsByCollege(collegeId)
  } else {
    majors.value = []
  }
}

onMounted(async () => {
  try {
    const response = await authAPI.getColleges()
    colleges.value = response || []
  } catch (error) {
    ElMessage.error('加载学院列表失败')
  }
})

const handleRegister = async () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await authAPI.registerCounselor({
        username: form.value.username,
        name: form.value.name,
        majorCode: form.value.majorCode, // 改为提交专业编码
        phone: form.value.phone,
        email: form.value.email,
        password: form.value.password,
        role: 4
      })
      
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (error) {
      ElMessage.error(error.message || '注册失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: url('../assets/bj.jpg') center/cover no-repeat;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
}

.register-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
  z-index: 0;
}

.register-container {
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 1;
}

.register-box {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  margin: 0 0 8px 0;
  font-size: 26px;
  color: #333;
  font-weight: bold;
}

.register-header p {
  margin: 0;
  font-size: 13px;
  color: #999;
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
  width: auto;
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

.default-password-hint {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}
</style>
