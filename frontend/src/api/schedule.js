import http from '../utils/http'

export function listSchedules(params) {
  return http.get('/api/v1/schedule/list', { params })
}
export function createSchedule(data) {
  return http.post('/api/v1/schedule', data)
}
export function checkConflict(params) {
  return http.get('/api/v1/schedule/checkConflict', { params })
}
export function deleteSchedule(id) {
  return http.delete(`/api/v1/schedule/${id}`)
}

