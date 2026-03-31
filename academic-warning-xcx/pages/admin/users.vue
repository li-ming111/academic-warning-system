<template>
  <view class="users-container">
    <view class="header">
      <text class="title">用户管理</text>
      <text class="subtitle">管理系统所有用户信息</text>
    </view>

    <view class="search-section">
      <view class="search-box">
        <input 
          class="search-input" 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索用户名、姓名或学号"
          @confirm="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">搜索</button>
      </view>
      <view class="filter-section">
        <picker :range="roleOptions" :value="selectedRoleIndex" @change="handleRoleChange" class="filter-picker">
          <view class="picker-label">
            <text class="picker-text">{{ selectedRole }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>
        <picker :range="statusOptions" :value="selectedStatusIndex" @change="handleStatusChange" class="filter-picker">
          <view class="picker-label">
            <text class="picker-text">{{ selectedStatus }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>
      </view>
    </view>

    <view class="users-list">
      <view v-if="filteredUsers.length > 0" class="users-content">
        <view v-for="user in filteredUsers" :key="user.id" class="user-item" hover-class="user-item-hover">
          <view class="user-avatar">
            <text class="avatar-text">{{ user.name.charAt(0) }}</text>
          </view>
          <view class="user-info">
            <view class="user-header">
              <text class="user-name">{{ user.name }}</text>
              <text :class="['user-status', user.status === 'active' ? 'status-active' : 'status-inactive']">
                {{ user.status === 'active' ? '正常' : '禁用' }}
              </text>
            </view>
            <view class="user-details">
              <text class="user-role">{{ getRoleText(user.role) }}</text>
              <text class="user-id">{{ user.userId }}</text>
            </view>
            <view class="user-meta">
              <text class="user-department">{{ user.department }}</text>
              <text class="user-phone">{{ user.phone }}</text>
            </view>
          </view>
          <view class="user-actions">
            <button class="action-btn edit-btn" @click="editUser(user)">编辑</button>
            <button class="action-btn toggle-btn" @click="toggleUserStatus(user)">
              {{ user.status === 'active' ? '禁用' : '启用' }}
            </button>
            <button class="action-btn delete-btn" @click="deleteUser(user)">删除</button>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <view class="empty-icon">👥</view>
        <text>暂无用户数据</text>
      </view>
    </view>

    <view class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="prevPage">上一页</button>
      <text class="page-info">{{ currentPage }} / {{ totalPages }}</text>
      <button class="page-btn" :disabled="currentPage === totalPages" @click="nextPage">下一页</button>
    </view>

    <!-- 编辑用户弹窗 -->
    <view v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">编辑用户</text>
          <text class="close-btn" @click="closeEditModal">×</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">用户名：</text>
            <input class="form-input" type="text" v-model="editForm.name" placeholder="请输入用户名" />
          </view>
          <view class="form-item">
            <text class="form-label">角色：</text>
            <picker :range="roleOptions" :value="editForm.roleIndex" @change="handleEditRoleChange" class="form-picker">
              <view class="picker-label">
                <text class="picker-text">{{ editForm.role }}</text>
                <text class="picker-arrow">▼</text>
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">部门：</text>
            <input class="form-input" type="text" v-model="editForm.department" placeholder="请输入部门" />
          </view>
          <view class="form-item">
            <text class="form-label">手机号：</text>
            <input class="form-input" type="text" v-model="editForm.phone" placeholder="请输入手机号" />
          </view>
        </view>
        <view class="modal-footer">
          <button class="modal-btn cancel-btn" @click="closeEditModal">取消</button>
          <button class="modal-btn confirm-btn" @click="confirmEdit">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const searchKeyword = ref('');
const selectedRole = ref('全部角色');
const selectedRoleIndex = ref(0);
const selectedStatus = ref('全部状态');
const selectedStatusIndex = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = ref(1);
const showEditModal = ref(false);

const roleOptions = ['全部角色', '学生', '教师', '辅导员', '管理员'];
const statusOptions = ['全部状态', '正常', '禁用'];

const editForm = ref({
  id: null,
  name: '',
  role: '学生',
  roleIndex: 1,
  department: '',
  phone: ''
});

// 模拟用户数据
const users = ref([
  {
    id: 1,
    userId: '2021001',
    name: '张三',
    role: 'student',
    department: '计算机学院',
    phone: '13800138001',
    status: 'active'
  },
  {
    id: 2,
    userId: '2021002',
    name: '李四',
    role: 'student',
    department: '计算机学院',
    phone: '13800138002',
    status: 'active'
  },
  {
    id: 3,
    userId: 'T001',
    name: '王老师',
    role: 'teacher',
    department: '计算机学院',
    phone: '13800138003',
    status: 'active'
  },
  {
    id: 4,
    userId: 'C001',
    name: '辅导员赵',
    role: 'counselor',
    department: '学生处',
    phone: '13800138004',
    status: 'active'
  },
  {
    id: 5,
    userId: 'A001',
    name: '管理员',
    role: 'admin',
    department: '信息中心',
    phone: '13800138005',
    status: 'active'
  }
]);

const filteredUsers = computed(() => {
  let result = users.value;
  
  // 角色筛选
  if (selectedRole.value !== '全部角色') {
    const roleMap = {
      '学生': 'student',
      '教师': 'teacher',
      '辅导员': 'counselor',
      '管理员': 'admin'
    };
    result = result.filter(user => user.role === roleMap[selectedRole.value]);
  }
  
  // 状态筛选
  if (selectedStatus.value !== '全部状态') {
    const statusMap = {
      '正常': 'active',
      '禁用': 'inactive'
    };
    result = result.filter(user => user.status === statusMap[selectedStatus.value]);
  }
  
  // 搜索关键词筛选
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(user => 
      user.name.toLowerCase().includes(keyword) ||
      user.userId.toLowerCase().includes(keyword)
    );
  }
  
  return result;
});

const getRoleText = (role) => {
  const roleMap = {
    'student': '学生',
    'teacher': '教师',
    'counselor': '辅导员',
    'admin': '管理员'
  };
  return roleMap[role] || role;
};

const handleSearch = () => {
  currentPage.value = 1;
  // 实际项目中调用API进行搜索
  console.log('搜索关键词:', searchKeyword.value);
};

const handleRoleChange = (e) => {
  selectedRoleIndex.value = e.detail.value;
  selectedRole.value = roleOptions[e.detail.value];
  currentPage.value = 1;
};

const handleStatusChange = (e) => {
  selectedStatusIndex.value = e.detail.value;
  selectedStatus.value = statusOptions[e.detail.value];
  currentPage.value = 1;
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const editUser = (user) => {
  editForm.value = {
    id: user.id,
    name: user.name,
    role: getRoleText(user.role),
    roleIndex: roleOptions.indexOf(getRoleText(user.role)),
    department: user.department,
    phone: user.phone
  };
  showEditModal.value = true;
};

const handleEditRoleChange = (e) => {
  editForm.value.roleIndex = e.detail.value;
  editForm.value.role = roleOptions[e.detail.value];
};

const closeEditModal = () => {
  showEditModal.value = false;
};

const confirmEdit = () => {
  const userIndex = users.value.findIndex(u => u.id === editForm.value.id);
  if (userIndex !== -1) {
    const roleMap = {
      '学生': 'student',
      '教师': 'teacher',
      '辅导员': 'counselor',
      '管理员': 'admin'
    };
    users.value[userIndex] = {
      ...users.value[userIndex],
      name: editForm.value.name,
      role: roleMap[editForm.value.role],
      department: editForm.value.department,
      phone: editForm.value.phone
    };
  }
  showEditModal.value = false;
  uni.showToast({
    title: '保存成功',
    icon: 'success'
  });
};

const toggleUserStatus = (user) => {
  uni.showModal({
    title: '确认操作',
    content: `确定要${user.status === 'active' ? '禁用' : '启用'}用户 ${user.name} 吗？`,
    success: (res) => {
      if (res.confirm) {
        user.status = user.status === 'active' ? 'inactive' : 'active';
        uni.showToast({
          title: '操作成功',
          icon: 'success'
        });
      }
    }
  });
};

const deleteUser = (user) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除用户 ${user.name} 吗？此操作不可恢复！`,
    success: (res) => {
      if (res.confirm) {
        const index = users.value.findIndex(u => u.id === user.id);
        if (index !== -1) {
          users.value.splice(index, 1);
        }
        uni.showToast({
          title: '删除成功',
          icon: 'success'
        });
      }
    }
  });
};

onMounted(() => {
  // 实际项目中调用API获取用户列表
  console.log('用户管理页面加载');
});
</script>

<style scoped>
.users-container {
  padding: 20rpx;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  margin-bottom: 24rpx;
}

.title {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
  margin-bottom: 8rpx;
  display: block;
}

.subtitle {
  font-size: 18rpx;
  color: #666;
  display: block;
}

.search-section {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
}

.search-box {
  display: flex;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.search-input {
  flex: 1;
  height: 80rpx;
  padding: 0 24rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 12rpx;
  font-size: 16rpx;
  background-color: #f9f9f9;
}

.search-btn {
  width: 120rpx;
  height: 80rpx;
  background-color: #4facfe;
  color: white;
  border: none;
  border-radius: 12rpx;
  font-size: 16rpx;
  font-weight: 500;
}

.filter-section {
  display: flex;
  gap: 16rpx;
}

.filter-picker {
  flex: 1;
}

.picker-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background-color: #f9f9f9;
  border: 1rpx solid #e0e0e0;
  border-radius: 12rpx;
}

.picker-text {
  font-size: 16rpx;
  color: #333;
}

.picker-arrow {
  font-size: 14rpx;
  color: #999;
}

.users-list {
  margin-bottom: 24rpx;
}

.users-content {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.user-item {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
  display: flex;
  align-items: flex-start;
  transition: all 0.3s ease;
}

.user-item-hover {
  transform: translateY(-4rpx);
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.12);
  border-color: #4facfe;
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.avatar-text {
  font-size: 32rpx;
  font-weight: 700;
  color: white;
}

.user-info {
  flex: 1;
  margin-right: 16rpx;
}

.user-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.user-name {
  font-size: 20rpx;
  font-weight: 600;
  color: #333;
}

.user-status {
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  font-size: 14rpx;
  font-weight: 500;
}

.status-active {
  background-color: #d4edda;
  color: #155724;
}

.status-inactive {
  background-color: #f8d7da;
  color: #721c24;
}

.user-details {
  display: flex;
  gap: 16rpx;
  margin-bottom: 8rpx;
}

.user-role {
  font-size: 16rpx;
  color: #4facfe;
  font-weight: 500;
}

.user-id {
  font-size: 16rpx;
  color: #666;
}

.user-meta {
  display: flex;
  gap: 16rpx;
}

.user-department,
.user-phone {
  font-size: 14rpx;
  color: #999;
}

.user-actions {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex-shrink: 0;
}

.action-btn {
  padding: 8rpx 16rpx;
  border: none;
  border-radius: 8rpx;
  font-size: 14rpx;
  font-weight: 500;
  transition: all 0.3s ease;
}

.edit-btn {
  background-color: #e3f2fd;
  color: #1976d2;
}

.toggle-btn {
  background-color: #fff3cd;
  color: #856404;
}

.delete-btn {
  background-color: #ffebee;
  color: #d32f2f;
}

.empty-state {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 16rpx;
  background-color: #fff;
  border-radius: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
}

.empty-icon {
  font-size: 60rpx;
  margin-bottom: 16rpx;
  display: block;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 0;
}

.page-btn {
  padding: 12rpx 24rpx;
  background-color: #4facfe;
  color: white;
  border: none;
  border-radius: 12rpx;
  font-size: 16rpx;
  font-weight: 500;
}

.page-btn[disabled] {
  background-color: #e0e0e0;
  color: #999;
}

.page-info {
  font-size: 16rpx;
  color: #666;
  font-weight: 500;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease-out;
}

.modal-content {
  width: 90%;
  max-width: 600rpx;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.2);
  animation: slideIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(-20rpx); }
  to { opacity: 1; transform: translateY(0); }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
  font-size: 24rpx;
  font-weight: 700;
  color: #333;
}

.close-btn {
  font-size: 32rpx;
  color: #999;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  color: #333;
  transform: rotate(90deg);
}

.modal-body {
  margin-bottom: 32rpx;
}

.form-item {
  margin-bottom: 20rpx;
}

.form-label {
  font-size: 16rpx;
  color: #666;
  display: block;
  margin-bottom: 8rpx;
}

.form-input {
  width: 100%;
  height: 80rpx;
  padding: 0 20rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 12rpx;
  font-size: 16rpx;
  background-color: #f9f9f9;
  box-sizing: border-box;
}

.form-picker {
  width: 100%;
}

.modal-footer {
  display: flex;
  gap: 20rpx;
  justify-content: center;
}

.modal-btn {
  padding: 16rpx 48rpx;
  border: none;
  border-radius: 12rpx;
  font-size: 18rpx;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #333;
}

.confirm-btn {
  background-color: #4facfe;
  color: #fff;
}

/* 动画效果 */
.users-container > * {
  animation: fadeIn 0.5s ease-out forwards;
}

.users-container > *:nth-child(1) { animation-delay: 0.1s; }
.users-container > *:nth-child(2) { animation-delay: 0.2s; }
.users-container > *:nth-child(3) { animation-delay: 0.3s; }
</style>