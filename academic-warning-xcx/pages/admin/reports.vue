<template>
  <view class="reports-container">
    <view class="header">
      <text class="title">报表统计</text>
      <text class="subtitle">查看系统数据统计报表</text>
    </view>

    <view class="report-cards">
      <view class="report-card" hover-class="report-card-hover">
        <view class="card-icon">📊</view>
        <view class="card-info">
          <text class="card-title">学生统计</text>
          <text class="card-value">2,000</text>
          <text class="card-desc">总学生人数</text>
        </view>
      </view>
      <view class="report-card" hover-class="report-card-hover">
        <view class="card-icon">⚠️</view>
        <view class="card-info">
          <text class="card-title">预警统计</text>
          <text class="card-value">500</text>
          <text class="card-desc">本月预警数</text>
        </view>
      </view>
      <view class="report-card" hover-class="report-card-hover">
        <view class="card-icon">🤝</view>
        <view class="card-info">
          <text class="card-title">帮扶统计</text>
          <text class="card-value">200</text>
          <text class="card-desc">进行中帮扶</text>
        </view>
      </view>
      <view class="report-card" hover-class="report-card-hover">
        <view class="card-icon">📈</view>
        <view class="card-info">
          <text class="card-title">成绩统计</text>
          <text class="card-value">85.6</text>
          <text class="card-desc">平均成绩</text>
        </view>
      </view>
    </view>

    <view class="chart-section">
      <text class="section-title">预警趋势图</text>
      <view class="chart-placeholder">
        <text class="chart-text">📊 预警趋势图表区域</text>
      </view>
    </view>

    <view class="export-section">
      <button class="export-btn" @click="exportReport">导出报表</button>
    </view>
  </view>
</template>

<script setup>
const exportReport = () => {
  uni.showToast({ title: '报表导出成功', icon: 'success' });
};
</script>

<style scoped>
.reports-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.report-cards { display: grid; grid-template-columns: 1fr 1fr; gap: 20rpx; margin-bottom: 24rpx; }
.report-card { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: center; transition: all 0.3s ease; }
.report-card-hover { transform: translateY(-4rpx); box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.12); border-color: #4facfe; }
.card-icon { font-size: 48rpx; margin-right: 16rpx; }
.card-info { flex: 1; }
.card-title { font-size: 16rpx; color: #666; display: block; margin-bottom: 4rpx; }
.card-value { font-size: 28rpx; font-weight: 700; color: #4facfe; display: block; margin-bottom: 4rpx; }
.card-desc { font-size: 14rpx; color: #999; }
.chart-section { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; margin-bottom: 24rpx; }
.section-title { font-size: 20rpx; font-weight: 600; color: #333; display: block; margin-bottom: 16rpx; }
.chart-placeholder { height: 300rpx; background-color: #f9f9f9; border-radius: 12rpx; display: flex; align-items: center; justify-content: center; }
.chart-text { font-size: 16rpx; color: #999; }
.export-section { margin-top: 40rpx; }
.export-btn { width: 100%; height: 90rpx; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; border: none; border-radius: 16rpx; font-size: 18rpx; font-weight: 600; }
</style>