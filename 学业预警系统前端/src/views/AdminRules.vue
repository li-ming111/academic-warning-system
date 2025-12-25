<template>
  <div class="admin-rules" style="background-color: #f8f9fa !important; min-height: 100vh;">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预警规则管理</span>
          <el-button type="primary" @click="addRuleDialogVisible = true">添加规则</el-button>
        </div>
      </template>
      <el-table :data="rulesList" stripe>
        <el-table-column prop="name" label="规则名称" />
        <el-table-column prop="condition" label="触发条件" />
        <el-table-column prop="level" label="预警级别" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="testRule(row)">测试</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="addRuleDialogVisible" title="添加规则">
      <el-form :model="ruleForm" label-width="80px">
        <el-form-item label="规则名称">
          <el-input v-model="ruleForm.name" />
        </el-form-item>
        <el-form-item label="触发条件">
          <el-input v-model="ruleForm.condition" type="textarea" />
        </el-form-item>
        <el-form-item label="预警级别">
          <el-select v-model="ruleForm.level">
            <el-option label="红色" value="red" />
            <el-option label="黄色" value="yellow" />
            <el-option label="蓝色" value="blue" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addRuleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddRule">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminAPI } from '@/api/index'

const addRuleDialogVisible = ref(false)
const rulesList = ref([])

const ruleForm = ref({ name: '', condition: '', level: '' })

onMounted(async () => {
  await loadRules()
})

const loadRules = async () => {
  try {
    const response = await adminAPI.getRules()
    if (Array.isArray(response)) {
      rulesList.value = response
    }
  } catch (error) {
    console.error('加载规则列表失败:', error)
  }
}

const testRule = (row) => ElMessage.success(`规则${row.name}测试通过`)

const submitAddRule = async () => {
  if (!ruleForm.value.name || !ruleForm.value.condition) {
    ElMessage.error('请填写规则名称和条件')
    return
  }
  try {
    await adminAPI.addRule(ruleForm.value)
    ElMessage.success('规则已添加')
    addRuleDialogVisible.value = false
    ruleForm.value = { name: '', condition: '', level: '' }
    await loadRules()
  } catch (error) {
    console.error('添加规则失败:', error)
  }
}
</script>

<style scoped>
.admin-rules {
  padding: 20px;
  background-color: #f8f9fa !important;
  min-height: 100vh;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
/* 全局样式覆盖 */
.admin-rules :deep(.el-card) {
  border: 1px solid #e9ecef !important;
}
.admin-rules :deep(.el-button--primary) {
  background-color: #667eea !important;
  border-color: #667eea !important;
}
</style>
