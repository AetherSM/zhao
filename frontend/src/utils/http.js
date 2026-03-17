import axios from 'axios'
import { getToken, clearToken } from './token'

const http = axios.create({
  baseURL: '',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers = config.headers || {}
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (resp) => {
    const res = resp.data
    if (res && typeof res.code !== 'undefined') {
      if (res.code === 200) return res.data
      if (res.code === 401) {
        clearToken()
        location.href = '/#/login'
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  (err) => Promise.reject(err)
)

export default http

