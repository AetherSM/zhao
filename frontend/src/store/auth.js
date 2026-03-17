import { defineStore } from 'pinia'
import { getToken, setToken, clearToken } from '../utils/token'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    user: null
  }),
  actions: {
    setLogin(token, user) {
      this.token = token
      this.user = user
      setToken(token)
    },
    logout() {
      this.token = ''
      this.user = null
      clearToken()
    }
  }
})

