import http from '../utils/http'

export function listPayments(params) {
  return http.get('/api/v1/payment/list', { params })
}
export function createPayment(data) {
  return http.post('/api/v1/payment', data)
}
export function listArrears(params) {
  return http.get('/api/v1/payment/arrears', { params })
}

