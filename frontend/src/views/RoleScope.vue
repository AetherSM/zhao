<template>
  <div class="role-scope">
    <el-card shadow="never">
      <template #header>
        <span class="title">管理员职责说明</span>
      </template>
      <p class="lead">
        系统设置统一<strong>管理员</strong>角色（<code>ADMIN</code>），负责辅导机构日常运营与教务管理，包括学员与教师档案、课程与班级、报名与排课、考勤、学习资源协同、缴费与统计分析等。教师、财务、学员/家长等角色权限相互隔离，由接口与菜单控制。
      </p>

      <el-descriptions :column="1" border title="主要能力范围">
        <el-descriptions-item label="教务与经营">学员、教师、课程分类、班级、报名、排课（含冲突检测）、考勤、缴费、统计看板等。</el-descriptions-item>
        <el-descriptions-item label="教学资源">学习资源的查阅与管理协同（与教师发布权限配合）。</el-descriptions-item>
        <el-descriptions-item label="评价治理">查看课程评价；可对不当评价执行删除等处理。</el-descriptions-item>
        <el-descriptions-item label="扩展能力">全局公告、操作审计、备份恢复等可作为后续迭代模块单独建设。</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.role-scope {
  max-width: 960px;
}
.title {
  font-size: 16px;
  font-weight: 600;
}
.lead {
  line-height: 1.7;
  color: #606266;
  margin: 0 0 16px;
}
code {
  font-size: 0.9em;
}
</style>
