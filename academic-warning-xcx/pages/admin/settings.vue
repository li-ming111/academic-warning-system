<template>
  <view class="settings-container">
    <view class="header">
      <text class="title">系统设置</text>
      <text class="subtitle">配置系统参数和选项</text>
    </view>

    <view class="settings-list">
      <view class="setting-group">
        <text class="group-title">预警设置</text>
        <view class="setting-item">
          <text class="setting-label">预警阈值</text>
          <input class="setting-input" type="number" v-model="settings.warningThreshold" placeholder="60" />
        </view>
        <view class="setting-item">
          <text class="setting-label">严重预警阈值</text>
          <input class="setting-input" type="number" v-model="settings.criticalThreshold" placeholder="40" />
        </view>
      </view>

      <view class="setting-group">
        <text class="group-title">通知设置</text>
        <view class="setting-item">
          <text class="setting-label">启用邮件通知</text>
          <switch :checked="settings.enableEmail" @change="toggleSetting('enableEmail')" />
        </view>
        <view class="setting-item">
          <text class="setting-label">启用短信通知</text>
          <switch :checked="settings.enableSMS" @change="toggleSetting('enableSMS')" />
        </view>
      </view>

      <view class="setting-group">
        <text class="group-title">系统维护</text>
        <view class="setting-item">
          <text class="setting-label">系统版本</text>
          <text class="setting-value">v1.0.0</text>
        </view>
        <view class="setting-item">
          <text class="setting-label">数据备份</text>
          <button class="action-btn" @click="backupData">立即备份</button>
        </view>
      </view>
    </view>

    <view class="save-section">
      <button class="save-btn" @click="saveSettings">保存设置</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const settings = ref({
  warningThreshold: 60,
  criticalThreshold: 40,
  enableEmail: true,
  enableSMS: false
});

const toggleSetting = (key) => {
  settings.value[key] = !settings.value[key];
};

const backupData = () => {
  uni.showToast({ title: '数据备份成功', icon: 'success' });
};

const saveSettings = () => {
  uni.showToast({ title: '设置保存成功', icon: 'success' });
};
</script>

<style scoped>
.settings-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.settings-list { display: flex; flex-direction: column; gap: 20rpx; }
.setting-group { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; }
.group-title { font-size: 20rpx; font-weight: 600; color: #333; display: block; margin-bottom: 16rpx; padding-bottom: 12rpx; border-bottom: 1rpx solid #f0f0f0; }
.setting-item { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.setting-item:last-child { border-bottom: none; }
.setting-label { font-size: 16rpx; color: #333; }
.setting-input { width: 120rpx; height: 60rpx; padding: 0 16rpx; border: 1rpx solid #e0e0e0; border-radius: 8rpx; font-size: 16rpx; text-align: center; }
.setting-value { font-size: 16rpx; color: #666; }
.action-btn { padding: 8rpx 20rpx; background-color: #4facfe; color: white; border: none; border-radius: 8rpx; font-size: 14rpx; }
.save-section { margin-top: 40rpx; }
.save-btn { width: 100%; height: 90rpx; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; border: none; border-radius: 16rpx; font-size: 18rpx; font-weight: 600; }
</style>