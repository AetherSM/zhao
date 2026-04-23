<template>
  <el-card>
    <div class="toolbar">
      <el-select v-if="isAdmin" v-model="q.teacherId" placeholder="选择教师" clearable style="width: 180px">
        <el-option v-for="t in teachers" :key="t.id" :label="t.teacherName" :value="t.id" />
      </el-select>
      <el-select v-model="q.courseId" placeholder="选择课程" clearable style="width: 180px">
        <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
      </el-select>
      <el-date-picker v-model="q.scheduleDate" type="date" value-format="YYYY-MM-DD" placeholder="日期" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button v-if="isAdmin || isTeacher" type="success" @click="openCreate">新增排课</el-button>
    </div>
    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column label="课程名称" width="200">
        <template #default="{ row }">
          {{ getCourseName(row.courseId) }}
        </template>
      </el-table-column>
      <el-table-column v-if="isAdmin" label="任课教师" width="150">
        <template #default="{ row }">
          {{ getTeacherName(row.teacherId) }}
        </template>
      </el-table-column>
      <el-table-column prop="classroom" label="教室" width="140" />
      <el-table-column prop="scheduleDate" label="日期" width="120" />
      <el-table-column prop="classPeriod" label="节次" />
      <el-table-column v-if="isAdmin || isTeacher" label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="danger" plain @click="onDel(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total" v-model:current-page="page" :page-size="size" @current-change="load" />
    </div>
  </el-card>

  <el-dialog v-model="dlg" title="新增排课" width="520px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="课程">
        <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%">
          <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="isAdmin" label="教师">
        <el-select v-model="form.teacherId" placeholder="请选择教师" style="width: 100%">
          <el-option v-for="t in teachers" :key="t.id" :label="t.teacherName" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="教室"><el-input v-model="form.classroom" /></el-form-item>
      <el-form-item label="日期"><el-date-picker v-model="form.scheduleDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
      <el-form-item label="节次"><el-input v-model="form.classPeriod" placeholder="如：上午 1-2 节" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dlg=false">取消</el-button>
      <el-button @click="onCheck">检测冲突</el-button>
      <el-button type="primary" @click="onSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listSchedules, createSchedule, checkConflict, deleteSchedule } from '../api/schedule'
import { listAllCourses } from '../api/course'
import { listAllTeachers } from '../api/teacher'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const isAdmin = computed(() => auth.user?.roles.includes('ADMIN'))
const isTeacher = computed(() => auth.user?.roles.includes('TEACHER'))

const q = reactive({ teacherId: '', courseId: '', scheduleDate: '' })
const rows = ref([])
const courses = ref([])
const teachers = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

function getCourseName(id) {
  const c = courses.value.find(x => x.id === id)
  return c ? c.courseName : id
}

function getTeacherName(id) {
  const t = teachers.value.find(x => x.id === id)
  return t ? t.teacherName : id
}

const dlg = ref(false)
const form = reactive({ courseId: '', teacherId: '', classroom: '', scheduleDate: '', classPeriod: '' })

async function load() {
  const params = { page: page.value, size: size.value }
  if (q.teacherId) params.teacherId = Number(q.teacherId)
  if (q.courseId) params.courseId = Number(q.courseId)
  if (q.scheduleDate) params.scheduleDate = q.scheduleDate
  const data = await listSchedules(params)
  rows.value = data.records || []
  total.value = data.total || 0
}

async function fetchOptions() {
  const [cRes, tRes] = await Promise.all([listAllCourses(), listAllTeachers()])
  courses.value = cRes
  teachers.value = tRes
}

function openCreate() {
  Object.assign(form, { courseId: '', teacherId: '', classroom: '101 教室', scheduleDate: '', classPeriod: '' })
  dlg.value = true
}
async function onCheck() {
  const teacherId = isAdmin.value ? Number(form.teacherId) : 0 // Backend will use current user's teacherId
  const ok = await checkConflict({
    teacherId: teacherId,
    classroom: form.classroom,
    scheduleDate: form.scheduleDate,
    classPeriod: form.classPeriod
  })
  ElMessage.info(ok ? '有冲突：该教师或教室该时间已排课' : '无冲突：可排课')
}
async function onSave() {
  try {
    const teacherId = isAdmin.value ? Number(form.teacherId) : 0
    await createSchedule({
      courseId: Number(form.courseId),
      teacherId: teacherId,
      classroom: form.classroom,
      scheduleDate: form.scheduleDate,
      classPeriod: form.classPeriod
    })
    ElMessage.success('排课成功')
    dlg.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.message || '排课失败')
  }
}
async function onDel(row) {
  await ElMessageBox.confirm('确认删除该排课？', '提示', { type: 'warning' })
  await deleteSchedule(row.id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(() => {
  load()
  fetchOptions()
})
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
.pager { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>

