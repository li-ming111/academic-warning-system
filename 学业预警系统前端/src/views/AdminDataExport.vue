<template>
  <div class="admin-data-export">
    <div class="page-header">
      <h1>📊 数据导出与备份</h1>
      <p>Excel导入导出、报表生成、数据备份恢复</p>
    </div>

    <!-- Tab页签 -->
    <el-tabs type="card">
      <!-- 数据导出 -->
      <el-tab-pane label="📥 数据导出">
        <div class="tab-content">
          <el-row :gutter="20" style="margin-bottom: 20px;">
            <el-col :xs="24" :sm="12" :md="6">
              <el-card class="export-card" shadow="hover">
                <template #header>
                  <div class="card-header">学生数据</div>
                </template>
                <p style="text-align: center; color: #666; margin-bottom: 15px;">
                  导出所有学生信息
                </p>
                <el-button type="primary" @click="exportStudents" style="width: 100%;">
                  导出Excel
                </el-button>
              </el-card>
            </el-col>

            <el-col :xs="24" :sm="12" :md="6">
              <el-card class="export-card" shadow="hover">
                <template #header>
                  <div class="card-header">成绩数据</div>
                </template>
                <p style="text-align: center; color: #666; margin-bottom: 15px;">
                  导出全部课程成绩
                </p>
                <el-button type="primary" @click="exportScores" style="width: 100%;">
                  导出Excel
                </el-button>
              </el-card>
            </el-col>

            <el-col :xs="24" :sm="12" :md="6">
              <el-card class="export-card" shadow="hover">
                <template #header>
                  <div class="card-header">预警数据</div>
                </template>
                <p style="text-align: center; color: #666; margin-bottom: 15px;">
                  导出全部预警记录
                </p>
                <el-button type="primary" @click="exportWarnings" style="width: 100%;">
                  导出Excel
                </el-button>
              </el-card>
            </el-col>

            <el-col :xs="24" :sm="12" :md="6">
              <el-card class="export-card" shadow="hover">
                <template #header>
                  <div class="card-header">用户数据</div>
                </template>
                <p style="text-align: center; color: #666; margin-bottom: 15px;">
                  导出管理人员数据
                </p>
                <el-button type="primary" @click="exportUsers" style="width: 100%;">
                  导出Excel
                </el-button>
              </el-card>
            </el-col>
          </el-row>

          <!-- 导出历史 -->
          <el-card>
            <template #header>
              <div class="card-header">导出历史</div>
            </template>
            <el-table :data="exportHistory" stripe>
              <el-table-column prop="file_name" label="文件名" min-width="180"></el-table-column>
              <el-table-column prop="data_type" label="数据类型" width="120"></el-table-column>
              <el-table-column prop="record_count" label="记录数" width="100"></el-table-column>
              <el-table-column prop="file_size" label="文件大小" width="100"></el-table-column>
              <el-table-column prop="created_at" label="导出时间" width="180"></el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" link @click="downloadFile(row)">下载</el-button>
                  <el-button type="danger" size="small" link @click="deleteExport(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 数据导入 -->
      <el-tab-pane label="📤 数据导入">
        <div class="tab-content">
          <el-row :gutter="20">
            <el-col :xs="24" :md="12">
              <el-card>
                <template #header>
                  <div class="card-header">导入学生数据</div>
                </template>

                <el-upload
                  ref="uploadStudents"
                  action="/api/admin/import/students"
                  :headers="{ Authorization: 'Bearer ' + token }"
                  :auto-upload="false"
                  accept=".xlsx,.xls"
                  @change="handleStudentFileChange"
                  style="margin-bottom: 20px;"
                >
                  <template #default>
                    <el-button>选择Excel文件</el-button>
                  </template>
                </el-upload>

                <div style="margin-bottom: 15px; padding: 10px; background: #f5f7fa; border-radius: 4px;">
                  <p style="color: #666; margin: 0;">
                    <strong>导入说明：</strong>
                  </p>
                  <ul style="margin: 5px 0; padding-left: 20px; color: #666;">
                    <li>支持Excel格式（.xlsx、.xls）</li>
                    <li>需包含：学号、姓名、班级等字段</li>
                    <li>系统将自动验证数据完整性</li>
                  </ul>
                </div>

                <el-button type="success" @click="importStudents">上传导入</el-button>
                <el-button @click="downloadTemplate('students')">下载模板</el-button>
              </el-card>
            </el-col>

            <el-col :xs="24" :md="12">
              <el-card>
                <template #header>
                  <div class="card-header">导入成绩数据</div>
                </template>

                <el-upload
                  ref="uploadScores"
                  action="/api/admin/import/scores"
                  :headers="{ Authorization: 'Bearer ' + token }"
                  :auto-upload="false"
                  accept=".xlsx,.xls"
                  @change="handleScoreFileChange"
                  style="margin-bottom: 20px;"
                >
                  <template #default>
                    <el-button>选择Excel文件</el-button>
                  </template>
                </el-upload>

                <div style="margin-bottom: 15px; padding: 10px; background: #f5f7fa; border-radius: 4px;">
                  <p style="color: #666; margin: 0;">
                    <strong>导入说明：</strong>
                  </p>
                  <ul style="margin: 5px 0; padding-left: 20px; color: #666;">
                    <li>支持Excel格式（.xlsx、.xls）</li>
                    <li>需包含：学号、课程、成绩等字段</li>
                    <li>支持批量导入（最多5000行）</li>
                  </ul>
                </div>

                <el-button type="success" @click="importScores">上传导入</el-button>
                <el-button @click="downloadTemplate('scores')">下载模板</el-button>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <!-- 报表生成 -->
      <el-tab-pane label="📈 报表生成">
        <div class="tab-content">
          <el-card style="margin-bottom: 20px;">
            <template #header>
              <div class="card-header">自定义报表</div>
            </template>

            <el-form :model="reportForm" label-width="120px" style="max-width: 600px;">
              <el-form-item label="报表模板">
                <el-select v-model="reportForm.template_id" placeholder="选择报表模板">
                  <el-option
                    v-for="template in reportTemplates"
                    :key="template.id"
                    :label="template.name"
                    :value="template.id"
                  ></el-option>
                </el-select>
              </el-form-item>

              <el-form-item label="开始日期">
                <el-date-picker v-model="reportForm.start_date" type="date"></el-date-picker>
              </el-form-item>

              <el-form-item label="结束日期">
                <el-date-picker v-model="reportForm.end_date" type="date"></el-date-picker>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="generateReport">生成报表</el-button>
                <el-button @click="resetReportForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 预定义模板 -->
          <el-card>
            <template #header>
              <div class="card-header">可用模板</div>
            </template>

            <el-row :gutter="20">
              <el-col v-for="template in reportTemplates" :key="template.id" :xs="24" :md="8">
                <div style="padding: 15px; border: 1px solid #ddd; border-radius: 4px; margin-bottom: 10px;">
                  <h4 style="margin: 0 0 8px 0;">{{ template.name }}</h4>
                  <p style="margin: 0 0 10px 0; color: #666; font-size: 12px;">
                    {{ template.description }}
                  </p>
                  <div>
                    <el-tag v-for="field in template.fields" :key="field" size="small" style="margin: 2px;">
                      {{ field }}
                    </el-tag>
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 数据备份 -->
      <el-tab-pane label="💾 数据备份">
        <div class="tab-content">
          <el-card style="margin-bottom: 20px;">
            <template #header>
              <div class="card-header">备份操作</div>
            </template>

            <div style="margin-bottom: 15px; padding: 15px; background: #f0f9ff; border-radius: 4px; border-left: 4px solid #409eff;">
              <p style="margin: 0; color: #333;">
                <strong>系统自动每日备份</strong>，备份时间为凌晨02:00。您也可手动备份所有数据。
              </p>
            </div>

            <el-button type="success" size="large" @click="backupNow" style="margin-bottom: 20px;">
              🔒 立即备份数据
            </el-button>

            <!-- 备份列表 -->
            <el-table :data="backupList" stripe v-loading="backupLoading">
              <el-table-column prop="name" label="备份名称" min-width="180"></el-table-column>
              <el-table-column prop="created_at" label="备份时间" width="180"></el-table-column>
              <el-table-column prop="size" label="文件大小" width="120"></el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag type="success">{{ row.status === 'success' ? '成功' : '进行中' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" link @click="restoreBackup(row.id)"
                    >恢复</el-button
                  >
                  <el-button type="danger" size="small" link @click="deleteBackup(row.id)"
                    >删除</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminAPI } from '@/api/index'
import { ElMessage } from 'element-plus'

// 数据
const token = localStorage.getItem('token')
const exportHistory = ref([])
const reportTemplates = ref([])
const backupList = ref([])
const backupLoading = ref(false)

const studentFile = ref(null)
const scoreFile = ref(null)

const reportForm = ref({
  template_id: '',
  start_date: null,
  end_date: null
})

onMounted(async () => {
  await loadReportTemplates()
  await loadBackupList()
  initExportHistory()
})

// 初始化导出历史
const initExportHistory = () => {
  exportHistory.value = [
    {
      id: 1,
      file_name: 'students_20251213_100000.xlsx',
      data_type: '学生数据',
      record_count: 1250,
      file_size: '2.5 MB',
      created_at: '2025-12-13 10:00:00'
    },
    {
      id: 2,
      file_name: 'scores_20251213_090000.xlsx',
      data_type: '成绩数据',
      record_count: 5000,
      file_size: '8.2 MB',
      created_at: '2025-12-13 09:00:00'
    }
  ]
}

// 导出学生数据
const exportStudents = async () => {
  try {
    const response = await adminAPI.exportStudents()
    if (response) {
      ElMessage.success('学生数据已导出')
      initExportHistory()
    }
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 导出成绩数据
const exportScores = async () => {
  try {
    const response = await adminAPI.exportScores()
    if (response) {
      ElMessage.success('成绩数据已导出')
      initExportHistory()
    }
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 导出预警数据
const exportWarnings = async () => {
  try {
    const response = await adminAPI.exportWarnings()
    if (response) {
      ElMessage.success('预警数据已导出')
      initExportHistory()
    }
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 导出用户数据
const exportUsers = async () => {
  try {
    const response = await adminAPI.exportUsers()
    if (response) {
      ElMessage.success('用户数据已导出')
      initExportHistory()
    }
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 文件选择处理
const handleStudentFileChange = (file) => {
  studentFile.value = file
}

const handleScoreFileChange = (file) => {
  scoreFile.value = file
}

// 导入学生数据
const importStudents = async () => {
  if (!studentFile.value) {
    ElMessage.error('请选择文件')
    return
  }

  try {
    await adminAPI.importStudents(studentFile.value)
    ElMessage.success('学生数据导入成功')
    studentFile.value = null
  } catch (error) {
    ElMessage.error('导入失败')
  }
}

// 导入成绩数据
const importScores = async () => {
  if (!scoreFile.value) {
    ElMessage.error('请选择文件')
    return
  }

  try {
    await adminAPI.importScores(scoreFile.value)
    ElMessage.success('成绩数据导入成功')
    scoreFile.value = null
  } catch (error) {
    ElMessage.error('导入失败')
  }
}

// 下载模板
const downloadTemplate = (type) => {
  const templateName = type === 'students' ? '学生导入模板.xlsx' : '成绩导入模板.xlsx'
  ElMessage.info('模板下载功能开发中...')
}

// 加载报表模板
const loadReportTemplates = async () => {
  try {
    const response = await adminAPI.getReportTemplates()
    if (Array.isArray(response)) {
      reportTemplates.value = response
    }
  } catch (error) {
    console.error('加载报表模板失败:', error)
  }
}

// 生成报表
const generateReport = async () => {
  if (!reportForm.value.template_id) {
    ElMessage.error('请选择报表模板')
    return
  }
  if (!reportForm.value.start_date || !reportForm.value.end_date) {
    ElMessage.error('请选择时间范围')
    return
  }

  try {
    await adminAPI.generateReport(reportForm.value)
    ElMessage.success('报表已生成')
    resetReportForm()
  } catch (error) {
    ElMessage.error('生成报表失败')
  }
}

// 重置报表表单
const resetReportForm = () => {
  reportForm.value = {
    template_id: '',
    start_date: null,
    end_date: null
  }
}

// 备份数据
const backupNow = async () => {
  try {
    await adminAPI.backupData()
    ElMessage.success('备份已完成')
    await loadBackupList()
  } catch (error) {
    ElMessage.error('备份失败')
  }
}

// 加载备份列表
const loadBackupList = async () => {
  backupLoading.value = true
  try {
    const response = await adminAPI.getBackupList()
    if (Array.isArray(response)) {
      backupList.value = response
    }
  } catch (error) {
    console.error('加载备份列表失败:', error)
  } finally {
    backupLoading.value = false
  }
}

// 恢复备份
const restoreBackup = async (backupId) => {
  ElMessage.warning('数据恢复需5-10分钟，恢复期间系统将暂时不可用')
  try {
    await adminAPI.restoreBackup(backupId)
    ElMessage.success('数据恢复中，请稍候...')
  } catch (error) {
    ElMessage.error('恢复失败')
  }
}

// 删除备份
const deleteBackup = (backupId) => {
  ElMessage.info('删除功能开发中...')
}

// 删除导出
const deleteExport = (exportId) => {
  ElMessage.info('删除功能开发中...')
}

// 下载文件
const downloadFile = (file) => {
  ElMessage.info('下载功能开发中...')
}
</script>

<style scoped>
.admin-data-export {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.tab-content {
  padding: 20px 0;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.export-card {
  height: 100%;
}
</style>
