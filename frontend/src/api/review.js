import http from '../utils/http'

export function listReviews(params) {
  return http.get('/api/v1/courseReview/list', { params })
}

export function createReview(data) {
  return http.post('/api/v1/courseReview', data)
}

export function deleteReview(id) {
  return http.delete(`/api/v1/courseReview/${id}`)
}
