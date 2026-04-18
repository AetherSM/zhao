<template>
  <el-card>
    <div class="toolbar">
      <el-select v-model="q.studentId" placeholder="选择学员" clearable style="width: 180px">
        <el-option v-for="item in students" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-select v-model="q.courseId" placeholder="选择课程" clearable style="width: 180px">
        <el-option v-for="item in courses" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="openCreate">新增报名</el-button>
    </div>
    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column label="学员姓名" width="150">
        <template #default="{ row }">
          {{ getStudentName(row.studentId) }}
        </template>
      </el-table-column>
      <el-table-column label="课程名称" width="200">
        <template #default="{ row }">
          {{ getCourseName(row.courseId) }}
        </template>
      </el-table-column>
      <el-table-column prop="signTime" label="报名时间" />
      <el-table-column label="操作" width="100">
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
      <el-form-item label="学员姓名">
        <el-select v-model="form.studentId" placeholder="请选择学员" filterable style="width: 100%">
          <el-option v-for="item in students" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="报名课程">
        <el-select v-model="form.courseId" placeholder="请选择课程" filterable style="width: 100%">
          <el-option v-for="item in courses" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dlg=false">取消</el-button>
      <el-button type="primary" @click="onSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listEnroll, createEnroll, deleteEnroll } from '../api/enroll'
import { getAllStudentNames } from '../api/student'
import { getAllCourseNames } from '../api/course'

const q = reactive({ studentId: '', courseId: '' })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const students = ref([])
const courses = ref([])

const dlg = ref(false)
const form = reactive({ studentId: '', courseId: '' })

async function initOptions() {
  try {
    const [stuRes, courRes] = await Promise.all([
      getAllStudentNames(),
      getAllCourseNames()
    ])
    students.value = stuRes || []
    courses.value = courRes || []
  } catch (e) {
    console.error('加载选项失败', e)
  }
}

function getStudentName(id) {
  const s = students.value.find(x => x.id === String(id))
  return s ? s.name : id
}

function getCourseName(id) {
  const c = courses.value.find(x => x.id === String(id))
  return c ? c.name : id
}

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
  if (!form.studentId || !form.courseId) {
    return ElMessage.warning('请选择学员和课程')
  }
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

onMounted(async () => {
  await initOptions()
  load()
})
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
.pager { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>

