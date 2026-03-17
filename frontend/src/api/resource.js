import http from '../utils/http'

export function listResources(params) {
  return http.get('/api/v1/learningResource/list', { params })
}

export function createResource(data) {
  return http.post('/api/v1/learningResource', data)
}

export function deleteResource(id) {
  return http.delete(`/api/v1/learningResource/${id}`)
}
