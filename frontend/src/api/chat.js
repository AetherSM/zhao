import http from '../utils/http'

export const sendMessage = (data) => {
  return http.post('/api/v1/chat/send', data)
}

export const getChatHistory = (withUserId) => {
  return http.get('/api/v1/chat/history', { params: { withUserId } })
}

export const getContacts = (keyword) => {
  return http.get('/api/v1/chat/contacts', { params: { keyword } })
}
