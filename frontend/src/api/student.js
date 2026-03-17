import http from '../utils/http'

export function listStudents(params) {
  return http.get('/api/v1/student/list', { params })
}
export function createStudent(data) {
  return http.post('/api/v1/student', data)
}
export function updateStudent(id, data) {
  return http.put(`/api/v1/student/${id}`, data)
}
export function deleteStudent(id) {
  return http.delete(`/api/v1/student/${id}`)
}

