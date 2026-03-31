// API服务模块

const API_BASE_URL = 'http://172.16.57.180:8081/api'; // 后端服务地址

class ApiClient {
  constructor() {
    this.baseUrl = API_BASE_URL;
  }

  // 通用请求方法
  async request(endpoint, options = {}) {
    const url = `${this.baseUrl}${endpoint}`;
    const token = uni.getStorageSync('token');

    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    try {
      const response = await uni.request({
        url,
        method: options.method || 'GET',
        data: options.data,
        header: headers,
      });

      if (response.statusCode === 200) {
        return response.data.data;
      } else {
        throw new Error(response.data.message || '请求失败');
      }
    } catch (error) {
      console.error('API请求错误:', error);
      throw error;
    }
  }

  // 登录
  async login(username, password) {
    return this.request('/auth/login', {
      method: 'POST',
      data: {
        username,
        password,
      },
    });
  }

  // 登出
  async logout(userId, role) {
    return this.request('/auth/logout', {
      method: 'POST',
    });
  }

  // 获取学生仪表盘数据
  async getStudentDashboard() {
    const userId = uni.getStorageSync('userId');
    if (!userId) {
      throw new Error('用户未登录');
    }
    return this.request(`/students/dashboard/${userId}`);
  }

  // 获取学生成绩列表
  async getStudentScores(semester = null) {
    const userId = uni.getStorageSync('userId');
    if (!userId) {
      throw new Error('用户未登录');
    }
    const params = semester ? `?semester=${semester}` : '';
    return this.request(`/students/scores/${userId}${params}`);
  }

  // 获取学生预警列表
  async getStudentWarnings() {
    const userId = uni.getStorageSync('userId');
    if (!userId) {
      throw new Error('用户未登录');
    }
    return this.request(`/students/warnings/${userId}`);
  }

  // 获取学生帮扶计划
  async getStudentAssistancePlans() {
    const userId = uni.getStorageSync('userId');
    if (!userId) {
      throw new Error('用户未登录');
    }
    return this.request(`/students/assistance/${userId}`);
  }

  // 更新帮扶计划进度
  async updatePlanProgress(planId, progress) {
    return this.request(`/students/assistance/${planId}/progress?progress=${progress}`, {
      method: 'POST',
    });
  }

  // 学生注册
  async registerStudent(data) {
    return this.request('/auth/register/student', {
      method: 'POST',
      data,
    });
  }

  // 教师注册
  async registerTeacher(data) {
    return this.request('/auth/register/teacher', {
      method: 'POST',
      data,
    });
  }

  // 辅导员注册
  async registerCounselor(data) {
    return this.request('/auth/register/counselor', {
      method: 'POST',
      data,
    });
  }

  // 获取学院列表
  async getColleges() {
    return this.request('/auth/colleges');
  }

  // 根据学院ID获取专业列表
  async getMajorsByCollege(collegeId) {
    return this.request(`/auth/majors?collegeId=${collegeId}`);
  }

  // 获取所有课程列表
  async getAllCourses() {
    return this.request('/auth/courses');
  }
}

export const apiClient = new ApiClient();
