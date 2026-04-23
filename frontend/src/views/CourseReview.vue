<template>
  <div class="review-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程评价反馈</span>
          <el-button v-if="isStudent" type="primary" @click="openReview">发布新评价</el-button>
        </div>
      </template>

      <el-table :data="reviews" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseId" label="课程ID" width="100" />
        <el-table-column prop="teacherId" label="教师ID" width="100" />
        <el-table-column label="评分" width="180">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评价内容" />
        <el-table-column prop="createTime" label="评价时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column v-if="isAdmin" label="操作" width="120">
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

    <el-dialog v-model="dlg" title="发布课程评价" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="对应课程">
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="授课教师">
          <el-select v-model="form.teacherId" placeholder="请选择教师" style="width: 100%">
            <el-option v-for="t in teachers" :key="t.id" :label="t.teacherName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="form.rating" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="form.comment" type="textarea" rows="4" placeholder="请分享您的上课感受..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { listReviews, createReview, deleteReview } from '../api/review'
import { listAllCourses } from '../api/course'
import { listAllTeachers } from '../api/teacher'
import { useAuthStore } from '../store/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const auth = useAuthStore()
const isAdmin = computed(() => auth.user?.roles.includes('ADMIN'))
const isStudent = computed(() => auth.user?.roles.includes('STUDENT'))

const reviews = ref([])
const courses = ref([])
const teachers = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const dlg = ref(false)
const form = reactive({
  courseId: '',
  teacherId: '',
  rating: 5,
  comment: ''
})

async function load() {
  const res = await listReviews({ page: page.value, size: size.value })
  reviews.value = res.records
  total.value = res.total
}

async function fetchOptions() {
  const [cRes, tRes] = await Promise.all([listAllCourses(), listAllTeachers()])
  courses.value = cRes
  teachers.value = tRes
}

function openReview() {
  form.courseId = ''
  form.teacherId = ''
  form.rating = 5
  form.comment = ''
  dlg.value = true
}

async function onSubmit() {
  if (!form.courseId || !form.teacherId) {
    return ElMessage.warning('请选择课程和教师')
  }
  try {
    await createReview({
      ...form,
      courseId: Number(form.courseId),
      teacherId: Number(form.teacherId)
    })
    ElMessage.success('评价成功')
    dlg.value = false
    load()
  } catch (e) {
    ElMessage.error(e.message || '发布失败')
  }
}

async function onDelete(id) {
  await ElMessageBox.confirm('确定删除该评价吗？', '警告', { type: 'warning' })
  await deleteReview(id)
  ElMessage.success('删除成功')
  load()
}

function formatTime(time) {
  if (!time) return ''
  return time.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  load()
  fetchOptions()
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
