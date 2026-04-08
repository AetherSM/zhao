<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="logo">TMS</div>
        <h1 class="title">辅导机构管理系统</h1>
        <p class="subtitle">让教育管理更高效、更透明</p>
      </div>

      <el-tabs v-model="activeRole" class="role-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="教职工登录" name="STAFF">
          <template #label>
            <span class="tab-label"><el-icon><Avatar /></el-icon> 教职工</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="学生/家长" name="STUDENT">
          <template #label>
            <span class="tab-label"><el-icon><User /></el-icon> 学生/家长</span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <el-form :model="form" class="login-form" @keyup.enter="onLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password size="large" />
        </el-form-item>
        
        <div class="form-options">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <el-button link type="primary">忘记密码？</el-button>
        </div>

        <el-button type="primary" class="login-btn" :loading="loading" size="large" @click="onLogin">
          立即登录
        </el-button>

        <div class="register-link">
          还没有账号？<el-button link type="primary" @click="goRegister">立即注册</el-button>
        </div>
      </el-form>

      <div class="login-footer">
        <div class="demo-account" v-if="activeRole === 'STAFF'">
          <el-tag size="small" type="info">演示账号: admin / 123456</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginApi } from '../api/user'
import { useAuthStore } from '../store/auth'
import { User, Lock, Avatar } from '@element-plus/icons-vue'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const activeRole = ref('STAFF')
const rememberMe = ref(false)
const form = reactive({ username: 'admin', password: '123456' })

function handleTabChange(name) {
  if (name === 'STUDENT') {
    form.username = ''
    form.password = ''
  } else {
    form.username = 'admin'
    form.password = '123456'
  }
}

async function onLogin() {
  if (!form.username || !form.password) {
    return ElMessage.warning('请输入用户名和密码')
  }
  
  loading.value = true
  try {
    const res = await loginApi(form)
    auth.setLogin(res.token, { 
      userId: res.userId, 
      username: res.username, 
      realName: res.realName, 
      roles: res.roles 
    })
    ElMessage.success('欢迎回来, ' + (res.realName || res.username))
    router.replace('/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}

function goRegister() {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}
.login-box {
  width: 100%;
  max-width: 440px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}
.login-header {
  text-align: center;
  margin-bottom: 30px;
}
.logo {
  font-size: 32px;
  font-weight: 900;
  color: #409eff;
  margin-bottom: 8px;
  letter-spacing: 2px;
}
.title {
  font-size: 24px;
  color: #303133;
  margin: 0 0 8px 0;
}
.subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}
.role-tabs {
  margin-bottom: 24px;
}
.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}
.login-form {
  margin-top: 20px;
}
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  margin-bottom: 16px;
}
.register-link {
  text-align: center;
  font-size: 14px;
  color: #606266;
}
.login-footer {
  margin-top: 24px;
  text-align: center;
}
.demo-account {
  opacity: 0.8;
}
</style>

