import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'

import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: '/dashboard', component: () => import('../views/Dashboard.vue') },
      { path: '/students', component: () => import('../views/Students.vue') },
      { path: '/teachers', component: () => import('../views/Teachers.vue') },
      { path: '/course-categories', component: () => import('../views/CourseCategories.vue') },
      { path: '/courses', component: () => import('../views/Courses.vue') },
      { path: '/enroll', component: () => import('../views/Enroll.vue') },
      { path: '/schedule', component: () => import('../views/Schedule.vue') },
      { path: '/payments', component: () => import('../views/Payments.vue') },
      { path: '/attendance', component: () => import('../views/Attendance.vue') },
      { path: '/reviews', component: () => import('../views/CourseReview.vue') },
      { path: '/resources', component: () => import('../views/LearningResource.vue') },
      { path: '/statistics', component: () => import('../views/Statistics.vue') },
      { path: '/chat', component: () => import('../views/Chat.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.path === '/login' || to.path === '/register') return true
  if (!auth.token) return '/login'
  return true
})

export default router

