import http from '../utils/http'

export function listEnroll(params) {
  return http.get('/api/v1/enroll/list', { params })
}
export function createEnroll(data) {
  return http.post('/api/v1/enroll', data)
}
export function deleteEnroll(id) {
  return http.delete(`/api/v1/enroll/${id}`)
}

