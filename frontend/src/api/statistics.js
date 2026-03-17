import http from '../utils/http'

export function statStudent(params) {
  return http.get('/api/v1/statistics/student', { params })
}
export function statCourse(params) {
  return http.get('/api/v1/statistics/course', { params })
}
export function statRevenue(params) {
  return http.get('/api/v1/statistics/revenue', { params })
}

