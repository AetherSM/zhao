<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="q.studentId" placeholder="学员ID" clearable style="width: 160px" />
      <el-input v-model="q.courseId" placeholder="课程ID" clearable style="width: 160px" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="openCreate">新增报名</el-button>
    </div>
    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="120" />
      <el-table-column prop="studentId" label="学员ID" width="120" />
      <el-table-column prop="courseId" label="课程ID" width="120" />
      <el-table-column prop="signTime" label="报名时间" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="onDel(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total" v-model:current-page="page" :page-size="size" @current-change="load" />
    </div>
  </el-card>

  <el-dialog v-model="dlg" title="新增报名" width="480px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="学员ID"><el-input v-model="form.studentId" /></el-form-item>
      <el-form-item label="课程ID"><el-input v-model="form.courseId" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dlg=false">取消</el-button>
      <el-button type="primary" @click="onSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listEnroll, createEnroll, deleteEnroll } from '../api/enroll'

const q = reactive({ studentId: '', courseId: '' })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const dlg = ref(false)
const form = reactive({ studentId: '', courseId: '' })

async function load() {
  const params = { page: page.value, size: size.value }
  if (q.studentId) params.studentId = Number(q.studentId)
  if (q.courseId) params.courseId = Number(q.courseId)
  const data = await listEnroll(params)
  rows.value = data.records || []
  total.value = data.total || 0
}
function openCreate() {
  Object.assign(form, { studentId: '', courseId: '' })
  dlg.value = true
}
async function onSave() {
  try {
    await createEnroll({ studentId: Number(form.studentId), courseId: Number(form.courseId) })
    ElMessage.success('报名成功')
    dlg.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.message || '报名失败')
  }
}
async function onDel(row) {
  await ElMessageBox.confirm('确认删除该报名记录？', '提示', { type: 'warning' })
  await deleteEnroll(row.id)
  ElMessage.success('删除成功')
  await load()
}

load()
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
.pager { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>

