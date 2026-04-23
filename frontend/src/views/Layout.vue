<template>
  <div class="layout">
    <aside class="sider">
      <div class="logo">TMS</div>
      <el-menu :default-active="route.path" router class="menu">
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        
        <el-sub-menu v-if="canAny(['ADMIN'])" index="student-mgmt">
          <template #title>
            <el-icon><User /></el-icon>
            <span>学员与报名</span>
          </template>
          <el-menu-item index="/students">学员管理</el-menu-item>
          <el-menu-item index="/enroll">课程报名</el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="canAny(['ADMIN'])" index="course-mgmt">
          <template #title>
            <el-icon><Reading /></el-icon>
            <span>课程管理</span>
          </template>
          <el-menu-item index="/course-categories">课程分类</el-menu-item>
          <el-menu-item index="/courses">所有班级</el-menu-item>
        </el-sub-menu>

        <el-menu-item v-if="canAny(['ADMIN'])" index="/teachers">
          <el-icon><Avatar /></el-icon>
          <span>教师管理</span>
        </el-menu-item>

        <el-menu-item v-if="canAny(['ADMIN','TEACHER','STUDENT'])" index="/schedule">
          <el-icon><Calendar /></el-icon>
          <span>排课查询</span>
        </el-menu-item>

        <el-menu-item v-if="canAny(['ADMIN','TEACHER','STUDENT'])" index="/attendance">
          <el-icon><Checked /></el-icon>
          <span>我的考勤</span>
        </el-menu-item>

        <el-menu-item v-if="canAny(['ADMIN','TEACHER','STUDENT'])" index="/resources">
          <el-icon><Files /></el-icon>
          <span>学习资源</span>
        </el-menu-item>

        <el-menu-item v-if="canAny(['ADMIN','STUDENT'])" index="/reviews">
          <el-icon><ChatDotRound /></el-icon>
          <span>评价反馈</span>
        </el-menu-item>

        <el-menu-item v-if="canAny(['ADMIN','TEACHER','STUDENT'])" index="/chat">
          <el-icon><ChatLineRound /></el-icon>
          <span>家校沟通</span>
        </el-menu-item>

        <el-menu-item v-if="canAny(['ADMIN','FINANCE'])" index="/payments">
          <el-icon><CreditCard /></el-icon>
          <span>缴费管理</span>
        </el-menu-item>

        <el-menu-item v-if="canAny(['ADMIN','FINANCE'])" index="/statistics">
          <el-icon><PieChart /></el-icon>
          <span>统计分析</span>
        </el-menu-item>

        <el-menu-item v-if="canAny(['ADMIN'])" index="/role-scope">
          <el-icon><Document /></el-icon>
          <span>职责与权限说明</span>
        </el-menu-item>
      </el-menu>
    </aside>
    <main class="main">
      <header class="header">
        <div class="header-left">
          <h2 class="title">辅导机构管理系统</h2>
        </div>
        <div class="user">
          <el-tag effect="plain" class="role-tag">{{ roleText }}</el-tag>
          <span class="name" @click="showProfile = true" style="cursor: pointer; color: #409eff;">{{ auth.user?.realName }}</span>
          <el-button type="danger" plain size="small" @click="onLogout">退出</el-button>
        </div>
      </header>
      <section class="content">
        <router-view />
      </section>
    </main>

    <!-- 个人信息修改对话框 -->
    <el-dialog v-model="showProfile" title="修改个人信息" width="400px">
      <el-form :model="profileForm" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="profileForm.username" placeholder="请输入新账号" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="profileForm.realName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="profileForm.password" type="password" placeholder="不修改请留空" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showProfile = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateProfile" :loading="updating">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { updateProfileApi } from '../api/user'
import { ElMessage } from 'element-plus'
import { Monitor, User, Reading, Avatar, Calendar, Checked, CreditCard, PieChart, Files, ChatDotRound, ChatLineRound, Document } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const showProfile = ref(false)
const updating = ref(false)
const profileForm = reactive({
  username: '',
  realName: '',
  password: ''
})

// 监听对话框打开，重新填充数据
watch(showProfile, (val) => {
  if (val) {
    profileForm.username = auth.user?.username || ''
    profileForm.realName = auth.user?.realName || ''
    profileForm.password = ''
  }
})

async function handleUpdateProfile() {
  if (!profileForm.username) return ElMessage.warning('账号不能为空')
  if (!profileForm.realName) return ElMessage.warning('用户名不能为空')
  updating.value = true
  try {
    const res = await updateProfileApi(profileForm)
    // 根据拦截器逻辑，res 如果是成功请求则直接返回 data，如果失败则可能抛出错误或返回错误对象
    // 如果没有抛出错误，说明请求是成功的
    ElMessage.success('修改成功，请重新登录')
    onLogout()
  } catch (e) {
    console.error('Update profile error:', e)
    ElMessage.error(e.msg || e.message || '修改失败')
  } finally {
    updating.value = false
  }
}

const roleText = computed(() => {
  const roles = auth.user?.roles || []
  if (roles.includes('ADMIN')) return '管理员'
  if (roles.includes('FINANCE')) return '财务管理'
  if (roles.includes('TEACHER')) return '任课教师'
  if (roles.includes('STUDENT')) return '学生/家长'
  return '普通用户'
})

function canAny(roleCodes) {
  const roles = auth.user?.roles || []
  return roleCodes.some((r) => roles.includes(r))
}

function onLogout() {
  auth.logout()
  router.replace('/login')
}
</script>

<style scoped>
.layout {
  height: 100vh;
  display: flex;
}
.sider {
  width: 240px;
  background: #001529;
  color: #fff;
  transition: width 0.3s;
}
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 20px;
  letter-spacing: 2px;
  color: #409eff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.menu {
  border-right: none;
  background: transparent;
}
.menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}
.menu :deep(.el-menu-item:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}
.menu :deep(.el-menu-item.is-active) {
  color: #fff !important;
  background: #409eff !important;
}
.menu :deep(.el-sub-menu.is-active .el-sub-menu__title) {
  color: #fff !important;
}
.menu :deep(.el-menu--inline .el-menu-item) {
  background: #000c17;
}
.menu :deep(.el-menu--inline .el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
}
.menu :deep(.el-menu--inline .el-menu-item.is-active) {
  background: #409eff !important;
}
.menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}
.menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.1);
}
.main {
  flex: 1;
  background: #f0f2f5;
  display: flex;
  flex-direction: column;
}
.header {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
}
.title {
  margin: 0;
  font-size: 18px;
  color: #333;
}
.content {
  padding: 24px;
  overflow: auto;
  flex: 1;
}
.user {
  display: flex;
  align-items: center;
  gap: 12px;
}
.name {
  font-weight: 600;
  color: #333;
}
.role-tag {
  margin-right: 4px;
}
</style>

