<template>
  <el-card>
    <div class="toolbar">
      <el-select v-model="q.studentId" placeholder="学员" clearable filterable style="width: 220px">
        <el-option v-for="s in students" :key="s.id" :label="s.name" :value="s.id" />
      </el-select>
      <el-select v-model="q.scheduleId" placeholder="排课" clearable filterable style="width: 360px">
        <el-option v-for="sch in scheduleRows" :key="sch.id" :label="scheduleLabel(sch)" :value="sch.id" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button v-if="isAdmin || isTeacher" type="success" @click="openCreate">新增考勤</el-button>
    </div>
    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="120" />
      <el-table-column label="学员" width="140">
        <template #default="{ row }">{{ getStudentName(row.studentId) }}</template>
      </el-table-column>
      <el-table-column label="排课" min-width="280">
        <template #default="{ row }">{{ getScheduleLabel(row.scheduleId) }}</template>
      </el-table-column>
      <el-table-column prop="attendanceStatus" label="状态" width="120">
        <template #default="{ row }">{{ statusText(row.attendanceStatus) }}</template>
      </el-table-column>
      <el-table-column prop="attendanceTime" label="时间" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column v-if="isAdmin || isTeacher" label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" type="danger" plain @click="onDel(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total" v-model:current-page="page" :page-size="size" @current-change="load" />
    </div>
  </el-card>

  <el-dialog v-model="dlg" title="新增考勤" width="560px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="学员" required>
        <el-select v-model="form.studentId" placeholder="请选择学员" filterable style="width: 100%">
          <el-option v-for="s in students" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="排课" required>
        <el-select v-model="form.scheduleId" placeholder="请选择排课" filterable style="width: 100%">
          <el-option v-for="sch in scheduleRows" :key="sch.id" :label="scheduleLabel(sch)" :value="sch.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.attendanceStatus" style="width: 100%">
          <el-option :value="1" label="正常" />
          <el-option :value="2" label="迟到" />
          <el-option :value="3" label="早退" />
          <el-option :value="4" label="缺勤" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间"><el-date-picker v-model="form.attendanceTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dlg=false">取消</el-button>
      <el-button type="primary" @click="onSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listAttendance, createAttendance, deleteAttendance } from '../api/attendance'
import { getAllStudentNames } from '../api/student'
import { listSchedules } from '../api/schedule'
import { listAllCourses } from '../api/course'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const isAdmin = computed(() => auth.user?.roles.includes('ADMIN'))
const isTeacher = computed(() => auth.user?.roles.includes('TEACHER'))

const q = reactive({ studentId: '', scheduleId: '' })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const students = ref([])
const courses = ref([])
const scheduleRows = ref([])

const dlg = ref(false)
const form = reactive({ studentId: '', scheduleId: '', attendanceStatus: 1, attendanceTime: '', remark: '' })

function statusText(v) {
  return v === 1 ? '正常' : v === 2 ? '迟到' : v === 3 ? '早退' : v === 4 ? '缺勤' : '-'
}

function scheduleLabel(row) {
  if (!row) return ''
  const cid = row.courseId
  const c = courses.value.find((x) => x.id === cid || Number(x.id) === Number(cid))
  const name = c?.courseName || '课程'
  const date = row.scheduleDate ?? ''
  const period = row.classPeriod ? String(row.classPeriod) : ''
  const room = row.classroom ? String(row.classroom) : ''
  return [name, date, period, room].filter(Boolean).join(' · ')
}

function getStudentName(id) {
  if (id == null || id === '') return '—'
  const s = students.value.find((x) => String(x.id) === String(id))
  return s?.name ?? id
}

function getScheduleLabel(id) {
  if (id == null || id === '') return '—'
  const row = scheduleRows.value.find((s) => Number(s.id) === Number(id))
  return row ? scheduleLabel(row) : id
}

async function loadOptions() {
  const [stuRes, courRes, schData] = await Promise.all([
    getAllStudentNames(),
    listAllCourses(),
    listSchedules({ page: 1, size: 1000 }),
  ])
  students.value = stuRes || []
  courses.value = courRes || []
  scheduleRows.value = schData.records || []
}

async function load() {
  const params = { page: page.value, size: size.value }
  if (q.studentId !== '' && q.studentId != null) params.studentId = Number(q.studentId)
  if (q.scheduleId !== '' && q.scheduleId != null) params.scheduleId = Number(q.scheduleId)
  const data = await listAttendance(params)
  rows.value = data.records || []
  total.value = data.total || 0
}

async function openCreate() {
  await loadOptions()
  const iso = new Date().toISOString().slice(0, 19)
  Object.assign(form, { studentId: '', scheduleId: '', attendanceStatus: 1, attendanceTime: iso, remark: '' })
  dlg.value = true
}

async function onSave() {
  if (form.studentId === '' || form.studentId == null) {
    ElMessage.warning('请选择学员')
    return
  }
  if (form.scheduleId === '' || form.scheduleId == null) {
    ElMessage.warning('请选择排课')
    return
  }
  try {
    await createAttendance({
      studentId: Number(form.studentId),
      scheduleId: Number(form.scheduleId),
      attendanceStatus: form.attendanceStatus,
      attendanceTime: form.attendanceTime,
      remark: form.remark
    })
    ElMessage.success('保存成功')
    dlg.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  }
}

async function onDel(row) {
  await ElMessageBox.confirm('确认删除该考勤？', '提示', { type: 'warning' })
  await deleteAttendance(row.id)
  ElMessage.success('删除成功')
  await load()
}

loadOptions().then(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
.pager { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>

