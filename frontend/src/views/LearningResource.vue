<template>
  <div class="resource-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学习资源与作业中心</span>
          <el-button v-if="isTeacher || isAdmin" type="primary" @click="openResource">发布资源</el-button>
        </div>
      </template>

      <el-tabs v-model="activeType" @tab-change="load">
        <el-tab-pane label="全部资源" name="" />
        <el-tab-pane label="作业布置" name="HOMEWORK" />
        <el-tab-pane label="学习资料" name="MATERIAL" />
      </el-tabs>

      <el-table :data="resources" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'HOMEWORK' ? 'warning' : 'success'">
              {{ row.type === 'HOMEWORK' ? '作业' : '资料' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="对应课程" width="200">
          <template #default="{ row }">
            {{ getCourseName(row.courseId) }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="资源描述或链接" />
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column v-if="isAdmin || isTeacher" label="操作" width="120">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="onDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          layout="prev, pager, next, total"
          :total="total"
          @current-change="load"
        />
      </div>
    </el-card>

    <el-dialog v-model="dlg" title="发布学习资源" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入资源标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio label="HOMEWORK">作业布置</el-radio>
            <el-radio label="MATERIAL">学习资料</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="对应课程">
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容链接">
          <el-input v-model="form.content" type="textarea" rows="4" placeholder="请填写资源内容描述或云盘链接..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">确认发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { listResources, createResource, deleteResource } from '../api/resource'
import { listAllCourses } from '../api/course'
import { useAuthStore } from '../store/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const auth = useAuthStore()
const isAdmin = computed(() => auth.user?.roles.some(r => ['SUPER_ADMIN', 'ORG_ADMIN'].includes(r)))
const isTeacher = computed(() => auth.user?.roles.includes('TEACHER'))

const resources = ref([])
const courses = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const activeType = ref('')

const dlg = ref(false)
const form = reactive({
  title: '',
  type: 'HOMEWORK',
  courseId: '',
  content: ''
})

async function load() {
  const res = await listResources({
    page: page.value,
    size: size.value,
    type: activeType.value || undefined
  })
  resources.value = res.records
  total.value = res.total
}

async function fetchCourses() {
  const res = await listAllCourses()
  courses.value = res
}

function getCourseName(id) {
  const c = courses.value.find(x => x.id === id)
  return c ? c.courseName : id
}

function openResource() {
  form.title = ''
  form.type = 'HOMEWORK'
  form.courseId = ''
  form.content = ''
  dlg.value = true
}

async function onSubmit() {
  if (!form.title || !form.courseId) {
    return ElMessage.warning('请填写完整的资源标题和课程信息')
  }
  try {
    await createResource({
      ...form,
      courseId: Number(form.courseId)
    })
    ElMessage.success('发布成功')
    dlg.value = false
    load()
  } catch (e) {
    ElMessage.error(e.message || '发布失败')
  }
}

async function onDelete(id) {
  await ElMessageBox.confirm('确定删除该资源吗？', '警告', { type: 'warning' })
  await deleteResource(id)
  ElMessage.success('删除成功')
  load()
}

function formatTime(time) {
  if (!time) return ''
  return time.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  load()
  fetchCourses()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
