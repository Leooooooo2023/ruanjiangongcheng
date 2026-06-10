import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('portal_token') || '')
  const user = ref(JSON.parse(localStorage.getItem('portal_user') || 'null'))

  function setLogin(data) {
    token.value = data.token
    user.value = data
    localStorage.setItem('portal_token', data.token)
    localStorage.setItem('portal_user', JSON.stringify(data))
  }

  function setLogout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('portal_token')
    localStorage.removeItem('portal_user')
  }

  return { token, user, setLogin, setLogout }
})
