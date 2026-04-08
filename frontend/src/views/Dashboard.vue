<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card" shadow="hover">
          <div class="welcome-content">
            <div class="text">
              <h2>下午好, {{ auth.user?.realName }}!</h2>
              <p>欢迎回到辅导机构管理系统。今天是 {{ today }}。</p>
            </div>
            <el-tag size="large">{{ roleText }}</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="stat in stats" :key="stat.title">
        <el-card shadow="hover" :body-style="{ padding: '20px' }">
          <div class="stat-item">
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">{{ stat.title }}</div>
              <div class="stat-value">{{ stat.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="16">
        <el-card header="系统通知" shadow="hover">
          <el-timeline>
            <el-timeline-item timestamp="2026-03-17" placement="top">
              <el-card>
                <h4>系统版本更新 v1.2.0</h4>
                <p>新增了角色权限差异化功能，优化了仪表盘展示。</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2026-03-10" placement="top">
              <el-card>
                <h4>财务结算提醒</h4>
                <p>请各部门管理员及时核对本月课程收入情况。</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card header="快速操作" shadow="hover">
          <div class="quick-actions">
            <el-button v-if="isAdmin" type="primary" block @click="router.push('/students')">新增学员</el-button>
            <el-button v-if="isAdmin || isTeacher" type="success" block @click="router.push('/schedule')">安排课程</el-button>
            <el-button v-if="isAdmin || isFinance" type="warning" block @click="router.push('/payments')">费用录入</el-button>
            <el-button type="info" plain block @click="onLogout">安全退出</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { User, Reading, Calendar, Money } from '@element-plus/icons-vue'
import { statStudent, statCourse } from '../api/statistics'

const router = useRouter()
const auth = useAuthStore()

const today = new Date().toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })

const isAdmin = computed(() => auth.user?.roles.some(r => ['SUPER_ADMIN', 'ORG_ADMIN'].includes(r)))
const isTeacher = computed(() => auth.user?.roles.includes('TEACHER'))
const isFinance = computed(() => auth.user?.roles.includes('FINANCE'))
const isStudent = computed(() => auth.user?.roles.includes('STUDENT'))

const roleText = computed(() => {
  const roles = auth.user?.roles || []
  if (roles.includes('SUPER_ADMIN')) return '系统管理员'
  if (roles.includes('ORG_ADMIN')) return '机构管理员'
  if (roles.includes('FINANCE')) return '财务管理'
  if (roles.includes('TEACHER')) return '任课教师'
  if (roles.includes('STUDENT')) return '学生/家长'
  return '普通用户'
})

const stats = ref([
  { title: '学员总数', value: '0', icon: User, color: '#409eff' },
  { title: '活跃课程', value: '0', icon: Reading, color: '#67c23a' },
  { title: '今日排课', value: '0', icon: Calendar, color: '#e6a23c' },
  { title: '本月营收', value: '￥0', icon: Money, color: '#f56c6c' }
])

onMounted(async () => {
  try {
    if (isAdmin.value || isTeacher.value) {
      const studentRes = await statStudent({ type: 'gender' })
      const totalStudents = studentRes.reduce((acc, cur) => acc + cur.value, 0)
      stats.value[0].value = totalStudents.toString()

      const courseRes = await statCourse({ type: 'category' })
      const totalCourses = courseRes.reduce((acc, cur) => acc + cur.value, 0)
      stats.value[1].value = totalCourses.toString()
    }
    
    if (isStudent.value) {
      stats.value[0].title = '我的课程'
      stats.value[0].value = '3'
      stats.value[1].title = '待上课'
      stats.value[1].value = '2'
      stats.value[2].title = '出勤率'
      stats.value[2].value = '98%'
      stats.value[3].title = '缴费记录'
      stats.value[3].value = '1'
    } else {
      // Default values for others
      stats.value[2].value = '8'
      if (isAdmin.value || isFinance.value) {
        stats.value[3].value = '￥12,800'
      } else {
        stats.value[3].title = '我的班级'
        stats.value[3].value = '4'
        stats.value[3].icon = User
      }
    }
  } catch (error) {
    console.error('Failed to fetch stats:', error)
  }
})

function onLogout() {
  auth.logout()
  router.replace('/login')
}
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.welcome-card {
  border-left: 5px solid #409eff;
}
.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.welcome-content h2 {
  margin: 0 0 8px 0;
  color: #303133;
}
.welcome-content p {
  margin: 0;
  color: #909399;
}
.stat-cards {
  margin-top: 10px;
}
.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}
.charts-row {
  margin-top: 10px;
}
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.quick-actions :deep(.el-button) {
  margin-left: 0;
  width: 100%;
}
</style>

