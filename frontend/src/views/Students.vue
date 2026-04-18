<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="q.studentName" placeholder="学员姓名" clearable style="width: 180px" />
      <el-input v-model="q.grade" placeholder="年级" clearable style="width: 180px" />
      <el-input v-model="q.phone" placeholder="手机号" clearable style="width: 180px" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button v-if="isAdmin" type="success" @click="openCreate">新增学员</el-button>
    </div>
    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="studentName" label="姓名" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '-' }}</template>
      </el-table-column>
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="grade" label="年级" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="enrollTime" label="入学时间" width="120" />
      <el-table-column v-if="isAdmin" label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" plain @click="onDel(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pager">
      <el-pagination
        background
        layout="prev, pager, next, total"
        :total="total"
        v-model:current-page="page"
        :page-size="size"
        @current-change="load"
      />
    </div>
  </el-card>

  <el-dialog v-model="dlg" :title="form.id ? '编辑学员' : '新增学员'" width="520px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="姓名"><el-input v-model="form.studentName" /></el-form-item>
      <el-form-item label="性别">
        <el-select v-model="form.gender" style="width: 100%">
          <el-option :value="1" label="男" />
          <el-option :value="2" label="女" />
        </el-select>
      </el-form-item>
      <el-form-item label="年龄"><el-input-number v-model="form.age" :min="1" :max="100" /></el-form-item>
      <el-form-item label="年级"><el-input v-model="form.grade" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
      <el-form-item label="入学时间"><el-date-picker v-model="form.enrollTime" type="date" value-format="YYYY-MM-DD" /></el-form-item>
      <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
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
import { listStudents, createStudent, updateStudent, deleteStudent } from '../api/student'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const isAdmin = computed(() => auth.user?.roles.some(r => ['SUPER_ADMIN', 'ORG_ADMIN'].includes(r)))

const q = reactive({ studentName: '', grade: '', phone: '' })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const dlg = ref(false)
const form = reactive({ id: null, studentName: '', gender: 1, age: null, grade: '', phone: '', enrollTime: '', address: '' })

async function load() {
  const data = await listStudents({ page: page.value, size: size.value, ...q })
  rows.value = data.records || []
  total.value = data.total || 0
}
function openCreate() {
  Object.assign(form, { id: null, studentName: '', gender: 1, age: null, grade: '', phone: '', enrollTime: '', address: '' })
  dlg.value = true
}
  function openEdit(row) {
    console.log('ID from row (before assign):', row.id, 'Type:', typeof row.id);
    // 使用解构赋值创建一个新对象，避免直接修改列表行数据
    Object.assign(form, { ...row })
    dlg.value = true
  }
  async function onSave() {
    try {
      console.log('ID being sent (before API call):', form.id, 'Type:', typeof form.id);
      if (form.id) await updateStudent(form.id, form)
      else await createStudent(form)
      ElMessage.success('保存成功')
      dlg.value = false
      await load()
    } catch (e) {
      ElMessage.error(e.message || '保存失败')
    }
  }
async function onDel(row) {
  await ElMessageBox.confirm('确认删除该学员？', '提示', { type: 'warning' })
  await deleteStudent(row.id)
  ElMessage.success('删除成功')
  await load()
}

load()
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
.pager { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>

