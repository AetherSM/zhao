<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="q.courseName" placeholder="课程名称" clearable style="width: 220px" />
      <el-input v-model="q.applyGrade" placeholder="适用年级" clearable style="width: 180px" />
      <el-input v-model="q.categoryId" placeholder="分类ID" clearable style="width: 140px" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="openCreate">新增</el-button>
    </div>
    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="120" />
      <el-table-column prop="courseName" label="课程名称" />
      <el-table-column prop="categoryId" label="分类ID" width="120" />
      <el-table-column prop="price" label="单价" width="100" />
      <el-table-column prop="totalHours" label="课时数" width="100" />
      <el-table-column prop="applyGrade" label="适用年级" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDel(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total" v-model:current-page="page" :page-size="size" @current-change="load" />
    </div>
  </el-card>

  <el-dialog v-model="dlg" :title="form.id ? '编辑课程' : '新增课程'" width="560px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="名称"><el-input v-model="form.courseName" /></el-form-item>
      <el-form-item label="分类ID"><el-input v-model="form.categoryId" /></el-form-item>
      <el-form-item label="单价"><el-input v-model="form.price" /></el-form-item>
      <el-form-item label="课时"><el-input-number v-model="form.totalHours" :min="1" :max="999" /></el-form-item>
      <el-form-item label="年级"><el-input v-model="form.applyGrade" /></el-form-item>
      <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
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
import { listCourses, createCourse, updateCourse, deleteCourse } from '../api/course'

const q = reactive({ courseName: '', categoryId: '', applyGrade: '' })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const dlg = ref(false)
const form = reactive({ id: null, courseName: '', categoryId: '', price: '', totalHours: 40, applyGrade: '', description: '' })

async function load() {
  const params = { page: page.value, size: size.value, courseName: q.courseName, applyGrade: q.applyGrade }
  if (q.categoryId) params.categoryId = Number(q.categoryId)
  const data = await listCourses(params)
  rows.value = data.records || []
  total.value = data.total || 0
}
function openCreate() {
  Object.assign(form, { id: null, courseName: '', categoryId: '', price: '', totalHours: 40, applyGrade: '', description: '' })
  dlg.value = true
}
function openEdit(row) {
  Object.assign(form, row)
  dlg.value = true
}
async function onSave() {
  try {
    const payload = { ...form, categoryId: Number(form.categoryId) }
    if (form.id) await updateCourse(form.id, payload)
    else await createCourse(payload)
    ElMessage.success('保存成功')
    dlg.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  }
}
async function onDel(row) {
  await ElMessageBox.confirm('确认删除该课程？', '提示', { type: 'warning' })
  await deleteCourse(row.id)
  ElMessage.success('删除成功')
  await load()
}

load()
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
.pager { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>

