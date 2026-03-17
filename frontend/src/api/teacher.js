import http from '../utils/http'

export function listTeachers(params) {
  return http.get('/api/v1/teacher/list', { params })
}
export function listAllTeachers() {
  return http.get('/api/v1/teacher/listAll')
}
export function createTeacher(data) {
  return http.post('/api/v1/teacher', data)
}
export function updateTeacher(id, data) {
  return http.put(`/api/v1/teacher/${id}`, data)
}
export function deleteTeacher(id) {
  return http.delete(`/api/v1/teacher/${id}`)
}

