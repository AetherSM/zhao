import http from '../utils/http'

export function loginApi(payload) {
  return http.post('/api/v1/user/login', payload)
}

export function registerApi(payload) {
  return http.post('/api/v1/user/register', payload)
}

export function updateProfileApi(payload) {
  return http.put('/api/v1/user/profile', payload)
}

