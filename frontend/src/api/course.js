import http from '../utils/http'

export function listCourseCategories(params) {
  return http.get('/api/v1/courseCategory/list', { params })
}
export function createCourseCategory(data) {
  return http.post('/api/v1/courseCategory', data)
}
export function updateCourseCategory(id, data) {
  return http.put(`/api/v1/courseCategory/${id}`, data)
}
export function deleteCourseCategory(id) {
  return http.delete(`/api/v1/courseCategory/${id}`)
}

export function listCourses(params) {
  return http.get('/api/v1/course/list', { params })
}
export function listAllCourses() {
  return http.get('/api/v1/course/listAll')
}
export function createCourse(data) {
  return http.post('/api/v1/course', data)
}
export function updateCourse(id, data) {
  return http.put(`/api/v1/course/${id}`, data)
}
export function deleteCourse(id) {
  return http.delete(`/api/v1/course/${id}`)
}

