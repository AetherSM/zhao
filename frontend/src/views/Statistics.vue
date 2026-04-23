<template>
  <el-card>
    <div class="toolbar">
      <el-select v-model="studentType" style="width: 200px">
        <el-option value="grade" label="学员按年级" />
        <el-option value="gender" label="学员按性别" />
        <el-option value="enrollTime" label="学员按入学月份" />
      </el-select>
      <el-button type="primary" @click="loadStudent">刷新学员统计</el-button>

      <el-divider direction="vertical" />

      <el-date-picker v-model="rev.start" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" />
      <el-date-picker v-model="rev.end" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" />
      <el-select v-model="rev.type" style="width: 140px">
        <el-option value="day" label="按日" />
        <el-option value="month" label="按月" />
        <el-option value="year" label="按年" />
      </el-select>
      <el-button type="primary" @click="loadRevenue">刷新营收</el-button>
    </div>

    <div class="grid">
      <el-card class="box">
        <template #header>学员统计</template>
        <div ref="studentEl" style="height: 320px; width: 100%"></div>
      </el-card>
      <el-card class="box">
        <template #header>营收统计</template>
        <div ref="revEl" style="height: 320px; width: 100%"></div>
      </el-card>
    </div>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { statStudent, statRevenue } from '../api/statistics'

const studentType = ref('grade')
const studentEl = ref()
const revEl = ref()
let studentChart, revChart

const today = new Date()
const start = new Date(today.getFullYear(), today.getMonth() - 2, 1)
const fmt = (d) => d.toISOString().slice(0, 10)
const rev = ref({ start: fmt(start), end: fmt(today), type: 'month' })

async function loadStudent() {
  try {
    const data = await statStudent({ type: studentType.value })
    const names = (data || []).map((i) => i.name)
    const values = (data || []).map((i) => Number(i.value))
    studentChart.setOption({
      tooltip: {},
      xAxis: { type: 'category', data: names },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: values }]
    })
  } catch (e) {
    console.error('Failed to load student statistics', e)
  }
}

async function loadRevenue() {
  try {
    const data = await statRevenue({ startTime: rev.value.start, endTime: rev.value.end, type: rev.value.type })
    const names = (data || []).map((i) => i.name)
    const values = (data || []).map((i) => Number(i.value))
    revChart.setOption({
      tooltip: {},
      xAxis: { type: 'category', data: names },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data: values }]
    })
  } catch (e) {
    console.error('Failed to load revenue statistics', e)
  }
}

onMounted(async () => {
  studentChart = echarts.init(studentEl.value)
  revChart = echarts.init(revEl.value)
  loadStudent()
  loadRevenue()
  window.addEventListener('resize', () => {
    studentChart?.resize()
    revChart?.resize()
  })
})
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; margin-bottom: 12px; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.box { min-height: 380px; }
@media (max-width: 1100px) {
  .grid { grid-template-columns: 1fr; }
}
</style>

