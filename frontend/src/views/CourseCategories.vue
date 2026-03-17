<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="q.categoryName" placeholder="分类名称" clearable style="width: 220px" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="openCreate">新增</el-button>
    </div>
    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="120" />
      <el-table-column prop="categoryName" label="分类名称" />
      <el-table-column prop="sort" label="排序" width="120" />
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

  <el-dialog v-model="dlg" :title="form.id ? '编辑分类' : '新增分类'" width="520px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="名称"><el-input v-model="form.categoryName" /></el-form-item>
      <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" :max="999" /></el-form-item>
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
import { listCourseCategories, createCourseCategory, updateCourseCategory, deleteCourseCategory } from '../api/course'

const q = reactive({ categoryName: '' })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const dlg = ref(false)
const form = reactive({ id: null, categoryName: '', sort: 0 })

async function load() {
  const data = await listCourseCategories({ page: page.value, size: size.value, ...q })
  rows.value = data.records || []
  total.value = data.total || 0
}
function openCreate() {
  Object.assign(form, { id: null, categoryName: '', sort: 0 })
  dlg.value = true
}
function openEdit(row) {
  Object.assign(form, row)
  dlg.value = true
}
async function onSave() {
  try {
    if (form.id) await updateCourseCategory(form.id, form)
    else await createCourseCategory(form)
    ElMessage.success('保存成功')
    dlg.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  }
}
async function onDel(row) {
  await ElMessageBox.confirm('确认删除该分类？', '提示', { type: 'warning' })
  await deleteCourseCategory(row.id)
  ElMessage.success('删除成功')
  await load()
}

load()
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
.pager { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>

