<template>
  <div class="benchmark-analysis-container">
    <!-- 学期选择器 -->
    <el-card class="semester-selector" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">学期选择</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-select v-model="selectedSemester" @change="loadBenchmarkData" placeholder="请选择学期" style="width: 100%">
            <el-option v-for="sem in semesters" :key="sem" :label="sem" :value="sem"></el-option>
          </el-select>
        </el-col>
      </el-row>
    </el-card>

    <!-- 关键指标卡片 -->
    <el-row :gutter="20" class="metrics-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-value">{{ latestBenchmark?.studentGpa || '--' }}</div>
          <div class="metric-label">我的GPA</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-value">{{ latestBenchmark?.classRank || '--' }}/{{ latestBenchmark?.classTotal || 0 }}</div>
          <div class="metric-label">班级排名</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-value">{{ latestBenchmark?.majorRank || '--' }}/{{ latestBenchmark?.majorTotal || 0 }}</div>
          <div class="metric-label">专业排名</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-value">{{ coursesPassed || 0 }}</div>
          <div class="metric-label">通过课程数</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 对标对比分析 -->
    <el-row :gutter="20" class="analysis-row">
      <!-- 班级对标 -->
      <el-col :xs="24" :md="12">
        <el-card class="analysis-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="title">班级成绩对标</span>
            </div>
          </template>
          <div v-if="latestBenchmark" class="comparison-content">
            <div class="comparison-item">
              <span class="label">我的GPA</span>
              <el-progress :percentage="gpaPercentage" :color="gpaStatusColor"></el-progress>
              <span class="value">{{ (latestBenchmark.studentGpa || 0).toFixed(2) }}</span>
            </div>
            <div class="comparison-item">
              <span class="label">班级平均GPA</span>
              <el-progress :percentage="100" color="#E6A23C"></el-progress>
              <span class="value">{{ (latestBenchmark.classAvgGpa || 0).toFixed(2) }}</span>
            </div>
            <div class="comparison-status">
              <el-tag v-if="isAboveClassAvg" type="success" effect="light">高于班级平均</el-tag>
              <el-tag v-else type="warning" effect="light">低于班级平均</el-tag>
            </div>
          </div>
          <div v-else class="no-data">暂无数据</div>
        </el-card>
      </el-col>

      <!-- 专业对标 -->
      <el-col :xs="24" :md="12">
        <el-card class="analysis-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="title">专业成绩对标</span>
            </div>
          </template>
          <div v-if="latestBenchmark" class="comparison-content">
            <div class="comparison-item">
              <span class="label">我的GPA</span>
              <el-progress :percentage="majorGpaPercentage" :color="majorGpaStatusColor"></el-progress>
              <span class="value">{{ (latestBenchmark.studentGpa || 0).toFixed(2) }}</span>
            </div>
            <div class="comparison-item">
              <span class="label">专业平均GPA</span>
              <el-progress :percentage="100" color="#409EFF"></el-progress>
              <span class="value">{{ (latestBenchmark.majorAvgGpa || 0).toFixed(2) }}</span>
            </div>
            <div class="comparison-status">
              <el-tag v-if="isAboveMajorAvg" type="success" effect="light">高于专业平均</el-tag>
              <el-tag v-else type="warning" effect="light">低于专业平均</el-tag>
            </div>
          </div>
          <div v-else class="no-data">暂无数据</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 排名分析 -->
    <el-row :gutter="20" class="analysis-row">
      <el-col :xs="24">
        <el-card class="analysis-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="title">排名分析</span>
            </div>
          </template>
          <el-row v-if="latestBenchmark" :gutter="20">
            <el-col :xs="24" :sm="12">
              <div class="ranking-box">
                <h4>班级排名</h4>
                <div class="ranking-content">
                  <div class="ranking-stat">
                    <div class="ranking-number">{{ latestBenchmark.classRank }}</div>
                    <div class="ranking-label">当前排名</div>
                  </div>
                  <div class="ranking-total">
                    <span>班级共{{ latestBenchmark.classTotal }}人</span>
                  </div>
                  <el-progress type="circle" :percentage="classRankPercentage" :width="100"></el-progress>
                </div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12">
              <div class="ranking-box">
                <h4>专业排名</h4>
                <div class="ranking-content">
                  <div class="ranking-stat">
                    <div class="ranking-number">{{ latestBenchmark.majorRank }}</div>
                    <div class="ranking-label">当前排名</div>
                  </div>
                  <div class="ranking-total">
                    <span>专业共{{ latestBenchmark.majorTotal }}人</span>
                  </div>
                  <el-progress type="circle" :percentage="majorRankPercentage" :width="100"></el-progress>
                </div>
              </div>
            </el-col>
          </el-row>
          <div v-else class="no-data">暂无数据</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学分达标度 -->
    <el-row :gutter="20" class="analysis-row">
      <el-col :xs="24">
        <el-card class="analysis-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="title">学分达标度分析</span>
            </div>
          </template>
          <div v-if="latestBenchmark" class="credits-content">
            <div class="credits-stat">
              <span class="label">已修习学分：</span>
              <span class="value">{{ latestBenchmark.requiredCredits || 0 }}</span>
            </div>
            <div class="credits-stat">
              <span class="label">通过课程：</span>
              <span class="value">{{ latestBenchmark.coursesPassed || 0 }}门</span>
            </div>
            <div class="credits-stat">
              <span class="label">不及格课程：</span>
              <span class="value">{{ latestBenchmark.coursesFailed || 0 }}门</span>
            </div>
            <div class="credits-status">
              <el-tag v-if="latestBenchmark.creditsOnTrack === 1" type="success" effect="light" size="large">学分达标</el-tag>
              <el-tag v-else type="danger" effect="light" size="large">学分未达标</el-tag>
            </div>
          </div>
          <div v-else class="no-data">暂无数据</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 历史趋势分析 -->
    <el-row :gutter="20" class="analysis-row">
      <el-col :xs="24">
        <el-card class="analysis-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="title">GPA历史趋势</span>
            </div>
          </template>
          <div class="trend-chart">
            <table v-if="historyBenchmarks && historyBenchmarks.length > 0" class="trend-table">
              <thead>
                <tr>
                  <th>学期</th>
                  <th>我的GPA</th>
                  <th>班级平均</th>
                  <th>专业平均</th>
                  <th>班级排名</th>
                  <th>专业排名</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in historyBenchmarks" :key="item.id">
                  <td>{{ item.semester }}</td>
                  <td><strong>{{ (item.studentGpa || 0).toFixed(2) }}</strong></td>
                  <td>{{ (item.classAvgGpa || 0).toFixed(2) }}</td>
                  <td>{{ (item.majorAvgGpa || 0).toFixed(2) }}</td>
                  <td>{{ item.classRank }}/{{ item.classTotal }}</td>
                  <td>{{ item.majorRank }}/{{ item.majorTotal }}</td>
                </tr>
              </tbody>
            </table>
            <div v-else class="no-data">暂无历史数据</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 进度报告 -->
    <el-row :gutter="20" class="analysis-row">
      <el-col :xs="24">
        <el-card class="analysis-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="title">综合进度报告</span>
            </div>
          </template>
          <div v-if="progressReport && Object.keys(progressReport).length > 0" class="report-content">
            <el-alert
              v-if="progressReport.gpaTrend"
              :title="gpaTrendTitle"
              :type="gpaTrendType"
              :closable="false"
              style="margin-bottom: 15px"
            ></el-alert>
            <div class="report-section">
              <h4>关键指标</h4>
              <el-row :gutter="20">
                <el-col :xs="24" :sm="12" :md="6">
                  <div class="indicator">
                    <div class="indicator-label">当前学期GPA</div>
                    <div class="indicator-value">{{ (progressReport.currentGpa || 0).toFixed(2) }}</div>
                  </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="6">
                  <div class="indicator">
                    <div class="indicator-label">班级排名百分比</div>
                    <div class="indicator-value">{{ (progressReport.benchmarkSummary?.classRankPercentage || 0).toFixed(1) }}%</div>
                  </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="6">
                  <div class="indicator">
                    <div class="indicator-label">专业排名百分比</div>
                    <div class="indicator-value">{{ (progressReport.benchmarkSummary?.majorRankPercentage || 0).toFixed(1) }}%</div>
                  </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="6">
                  <div class="indicator">
                    <div class="indicator-label">学分达标状态</div>
                    <div class="indicator-value">
                      <el-tag :type="progressReport.creditsOnTrack ? 'success' : 'danger'" effect="light">
                        {{ progressReport.creditsOnTrack ? '达标' : '未达标' }}
                      </el-tag>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
          <div v-else class="no-data">暂无进度报告数据</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { studentAPI } from '@/api'
import { ElMessage } from 'element-plus'

const userId = ref(null)
const studentId = ref(null)
const classId = ref(null)
const majorId = ref(null)

const selectedSemester = ref('')
const semesters = ref([])
const latestBenchmark = ref(null)
const historyBenchmarks = ref([])
const progressReport = ref({})

onMounted(async () => {
  // 从localStorage获取用户信息
  const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
  userId.value = localStorage.getItem('userId') || userInfo.userId || userInfo.id
  studentId.value = localStorage.getItem('studentId') || userInfo.studentId
  classId.value = localStorage.getItem('classId') || userInfo.classId
  majorId.value = localStorage.getItem('majorId') || userInfo.majorId

  console.log('=== BenchmarkAnalysis 初始化 ===')
  console.log('userId:', userId.value)
  console.log('studentId:', studentId.value)
  console.log('=============================')  
  
  if (userId.value) {
    await loadHistoryBenchmark()
    await loadProgressReport()
  } else {
    console.error('userId 为空！无法加载数据')
    ElMessage.warning('无法获取用户信息，请重新登录')
  }
})

async function loadHistoryBenchmark() {
  try {
    console.log('loadHistoryBenchmark - userId:', userId.value, 'studentId:', studentId.value)
    // 优先使用userId，因为studentId可能有问题
    const targetId = userId.value || studentId.value
    if (!targetId) {
      console.warn('userId和studentId都未定义')
      ElMessage.warning('学生信息未找到')
      return
    }
    const response = await studentAPI.getHistoryBenchmark(targetId)
    console.log('getHistoryBenchmark response:', response)
    
    // 处理两种可能的响应格式
    let data = []
    if (response && Array.isArray(response)) {
      data = response
    } else if (response?.data?.code === 200) {
      data = response.data.data || []
    } else if (response?.data && Array.isArray(response.data)) {
      data = response.data
    }
    
    console.log('处理后的数据:', data)
    historyBenchmarks.value = data
    
    // 提取所有学期
    semesters.value = data.map(item => item.semester)
    console.log('提取的学期:', semesters.value)
    
    // 设置当前学期为最新的学期
    if (semesters.value.length > 0) {
      selectedSemester.value = semesters.value[0]
      latestBenchmark.value = data[0]
      console.log('设置的selectedSemester:', selectedSemester.value)
      console.log('设置的latestBenchmark:', latestBenchmark.value)
      // 强制触发一次loadBenchmarkData
      await loadBenchmarkData()
    }
  } catch (error) {
    console.error('加载历史对标分析数据失败:', error)
    ElMessage.error('加载历史对标分析数据失败')
  }
}

async function loadBenchmarkData() {
  if (!selectedSemester.value) return

  try {
    // 优先使用userId，因为studentId可能有问题
    const targetId = userId.value || studentId.value
    console.log('loadBenchmarkData - userId:', userId.value, 'semester:', selectedSemester.value)
    const response = await studentAPI.getBenchmarkBySemester(targetId, selectedSemester.value)
    console.log('getBenchmarkBySemester response:', response)
    
    // 处理响应格式 - response 可能是直接对象
    let data = null
    if (response?.code === 200) {
      // 格式1: {code: 200, data: {...}}
      data = response.data
    } else if (response?.data?.code === 200) {
      // 格式2: response.data = {code: 200, data: {...}}
      data = response.data.data
    } else if (response?.data && !response.data.code && response.data.id) {
      // 格式3: response.data 就是对象本身
      data = response.data
    } else if (response?.id) {
      // 格式4: response 就是对象本身
      data = response
    }
    
    if (data && data.id) {
      latestBenchmark.value = data
      console.log('更新的benchmark数据:', data)
      // 强制触发DOM更新
      await nextTick()
    } else {
      console.warn('数据格式异常，无法解析:', response)
    }
  } catch (error) {
    console.error('加载对标分析数据失败:', error)
  }
}

async function loadProgressReport() {
  try {
    console.log('loadProgressReport - userId:', userId.value)
    const response = await studentAPI.getProgressReport(userId.value)
    console.log('getProgressReport response:', response)
    if (response.data?.code === 200) {
      progressReport.value = response.data.data || {}
    }
  } catch (error) {
    console.error('加载进度报告失败:', error)
    ElMessage.error('加载进度报告失败')
  }
}

// 计算属性
const coursesPassed = computed(() => {
  return latestBenchmark.value?.coursesPassed || 0
})

const gpaPercentage = computed(() => {
  if (!latestBenchmark.value?.studentGpa || !latestBenchmark.value?.classAvgGpa) return 0
  const percentage = (latestBenchmark.value.studentGpa / latestBenchmark.value.classAvgGpa) * 100
  return Math.min(percentage, 100)
})

const gpaStatusColor = computed(() => {
  if (!latestBenchmark.value) return '#606266'
  return latestBenchmark.value.studentGpa >= latestBenchmark.value.classAvgGpa ? '#67C23A' : '#F56C6C'
})

const majorGpaPercentage = computed(() => {
  if (!latestBenchmark.value?.studentGpa || !latestBenchmark.value?.majorAvgGpa) return 0
  const percentage = (latestBenchmark.value.studentGpa / latestBenchmark.value.majorAvgGpa) * 100
  return Math.min(percentage, 100)
})

const majorGpaStatusColor = computed(() => {
  if (!latestBenchmark.value) return '#606266'
  return latestBenchmark.value.studentGpa >= latestBenchmark.value.majorAvgGpa ? '#67C23A' : '#F56C6C'
})

const isAboveClassAvg = computed(() => {
  if (!latestBenchmark.value) return false
  return latestBenchmark.value.studentGpa >= latestBenchmark.value.classAvgGpa
})

const isAboveMajorAvg = computed(() => {
  if (!latestBenchmark.value) return false
  return latestBenchmark.value.studentGpa >= latestBenchmark.value.majorAvgGpa
})

const classRankPercentage = computed(() => {
  if (!latestBenchmark.value?.classRank || !latestBenchmark.value?.classTotal) return 0
  return Math.round((latestBenchmark.value.classRank / latestBenchmark.value.classTotal) * 100)
})

const majorRankPercentage = computed(() => {
  if (!latestBenchmark.value?.majorRank || !latestBenchmark.value?.majorTotal) return 0
  return Math.round((latestBenchmark.value.majorRank / latestBenchmark.value.majorTotal) * 100)
})

const gpaTrendTitle = computed(() => {
  if (!progressReport.value?.gpaTrend) return ''
  const trend = progressReport.value.gpaTrend
  if (trend > 0) {
    return `学习进步！上学期与本学期GPA提升${trend.toFixed(2)}分`
  } else if (trend < 0) {
    return `需要关注！上学期与本学期GPA下降${Math.abs(trend).toFixed(2)}分`
  } else {
    return '本学期GPA与上学期持平'
  }
})

const gpaTrendType = computed(() => {
  if (!progressReport.value?.gpaTrend) return 'info'
  return progressReport.value.gpaTrend > 0 ? 'success' : (progressReport.value.gpaTrend < 0 ? 'warning' : 'info')
})
</script>

<style scoped lang="scss">
.benchmark-analysis-container {
  padding: 20px;
  background: #f5f7fa;

  .semester-selector {
    margin-bottom: 20px;
  }

  .metrics-row {
    margin-bottom: 20px;

    .metric-card {
      text-align: center;
      padding: 20px;

      .metric-value {
        font-size: 28px;
        font-weight: bold;
        color: #409eff;
        margin-bottom: 8px;
      }

      .metric-label {
        font-size: 14px;
        color: #606266;
      }
    }
  }

  .analysis-row {
    margin-bottom: 20px;

    .analysis-card {
      .comparison-content {
        .comparison-item {
          margin-bottom: 16px;
          display: flex;
          align-items: center;
          gap: 12px;

          .label {
            min-width: 100px;
            font-size: 14px;
            color: #606266;
          }

          .value {
            font-weight: bold;
            color: #409eff;
            min-width: 60px;
            text-align: right;
          }

          :deep(.el-progress) {
            flex: 1;
          }
        }

        .comparison-status {
          margin-top: 16px;
          text-align: center;
        }
      }

      .ranking-box {
        padding: 20px;
        background: #f5f7fa;
        border-radius: 4px;

        h4 {
          margin: 0 0 16px 0;
          color: #303133;
        }

        .ranking-content {
          text-align: center;

          .ranking-stat {
            margin-bottom: 12px;

            .ranking-number {
              font-size: 32px;
              font-weight: bold;
              color: #409eff;
            }

            .ranking-label {
              font-size: 12px;
              color: #909399;
            }
          }

          .ranking-total {
            font-size: 14px;
            color: #606266;
            margin-bottom: 12px;
          }

          :deep(.el-progress) {
            display: flex;
            justify-content: center;
          }
        }
      }

      .credits-content {
        .credits-stat {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 10px 0;
          border-bottom: 1px solid #ebeef5;

          .label {
            color: #606266;
          }

          .value {
            font-weight: bold;
            color: #409eff;
            font-size: 18px;
          }

          &:last-of-type {
            border-bottom: none;
          }
        }

        .credits-status {
          margin-top: 16px;
          text-align: center;
        }
      }

      .trend-chart {
        .trend-table {
          width: 100%;
          border-collapse: collapse;

          thead {
            background: #f5f7fa;

            th {
              padding: 12px;
              text-align: left;
              font-weight: 600;
              color: #303133;
              border-bottom: 1px solid #ebeef5;
            }
          }

          tbody {
            tr {
              border-bottom: 1px solid #ebeef5;

              td {
                padding: 12px;
                color: #606266;

                strong {
                  color: #409eff;
                }
              }

              &:hover {
                background: #f5f7fa;
              }
            }
          }
        }
      }

      .report-content {
        .report-section {
          margin-bottom: 20px;

          h4 {
            margin: 0 0 16px 0;
            color: #303133;
          }

          .indicator {
            padding: 16px;
            background: #f5f7fa;
            border-radius: 4px;
            text-align: center;

            .indicator-label {
              font-size: 12px;
              color: #909399;
              margin-bottom: 8px;
            }

            .indicator-value {
              font-size: 20px;
              font-weight: bold;
              color: #409eff;
            }
          }
        }
      }

      .no-data {
        text-align: center;
        padding: 40px 20px;
        color: #909399;
      }
    }
  }

  .title {
    font-weight: 600;
  }
}
</style>
