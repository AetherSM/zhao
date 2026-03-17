<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <div class="logo">TMS</div>
        <h1 class="title">新用户注册</h1>
        <p class="subtitle">加入辅导机构管理系统</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="register-form" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="建议使用手机号或邮箱" prefix-icon="User" size="large" />
        </el-form-item>
        
        <el-form-item label="设置密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password size="large" />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" prefix-icon="CircleCheck" show-password size="large" />
        </el-form-item>

        <el-form-item label="注册身份" prop="roleCode">
          <el-radio-group v-model="form.roleCode" class="role-selector">
            <el-radio-button label="STUDENT">我是学生/家长</el-radio-button>
            <el-radio-button label="TEACHER">我是教师</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-button type="primary" class="register-btn" :loading="loading" size="large" @click="onRegister">
          提交注册
        </el-button>

        <div class="login-link">
          已有账号？<el-button link type="primary" @click="router.push('/login')">立即登录</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerApi } from '../api/user'
import { User, Lock, CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  roleCode: 'STUDENT'
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass, trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请选择注册身份', trigger: 'change' }
  ]
}

async function onRegister() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await registerApi({
          username: form.username,
          password: form.password,
          roleCode: form.roleCode
        })
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (e) {
        ElMessage.error(e.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  padding: 20px;
}
.register-box {
  width: 100%;
  max-width: 480px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}
.register-header {
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
.role-selector {
  width: 100%;
  display: flex;
}
.role-selector :deep(.el-radio-button) {
  flex: 1;
}
.role-selector :deep(.el-radio-button__inner) {
  width: 100%;
}
.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  margin-top: 10px;
  margin-bottom: 16px;
}
.login-link {
  text-align: center;
  font-size: 14px;
  color: #606266;
}
</style>
