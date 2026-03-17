import http from '../utils/http'

export function listAttendance(params) {
  return http.get('/api/v1/attendance/list', { params })
}
export function createAttendance(data) {
  return http.post('/api/v1/attendance', data)
}
export function deleteAttendance(id) {
  return http.delete(`/api/v1/attendance/${id}`)
}

