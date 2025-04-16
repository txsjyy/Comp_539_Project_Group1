import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null as string | null,
    isLoggedIn: false
  }),
  
  actions: {
    setUser(userId: string) {
      this.userId = userId
      this.isLoggedIn = true
    },
    
    clearUser() {
      this.userId = null
      this.isLoggedIn = false
    }
  },
  
  persist: true
}) 