<template>
  <el-card>
    <div class="toolbar">
      <el-select v-model="q.studentId" placeholder="选择学员" clearable style="width: 180px">
        <el-option v-for="s in students" :key="s.id" :label="s.name" :value="s.id" />
      </el-select>
      <el-select v-model="q.courseId" placeholder="选择课程" clearable style="width: 180px">
        <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button v-if="isFinanceOrAdmin" type="success" @click="openCreate">新增缴费</el-button>
      <el-button v-if="isFinanceOrAdmin" type="warning" @click="loadArrears">欠费统计</el-button>
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
      <el-table-column prop="amount" label="金额" width="120" />
      <el-table-column prop="payType" label="方式" width="120" />
      <el-table-column prop="payTime" label="缴费时间" />
      <el-table-column prop="remark" label="备注" />
    </el-table>
    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total" v-model:current-page="page" :page-size="size" @current-change="load" />
    </div>
  </el-card>

  <el-dialog v-model="dlg" title="新增缴费" width="560px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="学员姓名">
        <el-select v-model="form.studentId" placeholder="请选择学员" filterable style="width: 100%">
          <el-option v-for="s in students" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="收费课程">
        <el-select v-model="form.courseId" placeholder="请选择课程" filterable style="width: 100%">
          <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="金额"><el-input v-model="form.amount" /></el-form-item>
      <el-form-item label="方式"><el-input v-model="form.payType" placeholder="现金/微信/支付宝" /></el-form-item>
      <el-form-item label="时间"><el-date-picker v-model="form.payTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dlg=false">取消</el-button>
      <el-button type="primary" @click="onSave">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="dlg2" title="欠费统计" width="860px">
    <div class="toolbar">
      <el-input v-model="arrearsQ.studentName" placeholder="学员姓名（模糊）" clearable style="width: 220px" />
      <el-button type="primary" @click="loadArrears">查询</el-button>
    </div>
    <el-table :data="arrearsRows" border>
      <el-table-column prop="studentName" label="学员" />
      <el-table-column prop="courseName" label="课程" />
      <el-table-column prop="totalFee" label="应缴" />
      <el-table-column prop="paidFee" label="已缴" />
      <el-table-column prop="arrearsFee" label="欠费" />
    </el-table>
  </el-dialog>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listPayments, createPayment, listArrears } from '../api/payment'
import { getAllStudentNames } from '../api/student'
import { getAllCourseNames } from '../api/course'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const isFinanceOrAdmin = computed(() => auth.user?.roles.some(r => ['SUPER_ADMIN', 'ORG_ADMIN', 'FINANCE'].includes(r)))

const q = reactive({ studentId: '', courseId: '' })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const students = ref([])
const courses = ref([])

const dlg = ref(false)
const form = reactive({ studentId: '', courseId: '', amount: '', payType: '微信', payTime: '', remark: '' })

const dlg2 = ref(false)
const arrearsQ = reactive({ studentName: '' })
const arrearsRows = ref([])

async function fetchOptions() {
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
  const data = await listPayments(params)
  rows.value = data.records || []
  total.value = data.total || 0
}

function openCreate() {
  const now = new Date()
  const iso = now.toISOString().slice(0, 19)
  Object.assign(form, { studentId: '', courseId: '', amount: '', payType: '微信', payTime: iso, remark: '' })
  dlg.value = true
}

async function onSave() {
  try {
    await createPayment({
      studentId: Number(form.studentId),
      courseId: Number(form.courseId),
      amount: Number(form.amount),
      payType: form.payType,
      payTime: form.payTime,
      remark: form.remark
    })
    ElMessage.success('保存成功')
    dlg.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  }
}

async function loadArrears() {
  dlg2.value = true
  const data = await listArrears({ page: 1, size: 50, studentName: arrearsQ.studentName || undefined })
  arrearsRows.value = data.records || []
}

onMounted(async () => {
  await fetchOptions()
  load()
})
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
.pager { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>

