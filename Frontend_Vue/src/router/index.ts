import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import MyURLs from "../views/MyURLs.vue"
import Statistics from '../views/Statistics.vue'
import ForgotPassword from '../views/ForgotPassword.vue'
import Plans from '../views/Plans.vue'  
import SignUp from '../views/SignUp.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/myurls',
      name: 'myurls',
      component: MyURLs
    },
    {
      path: '/statistics/:id',
      name: 'statistics',
      component: Statistics
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: ForgotPassword
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignUp
    },
    {
      path: '/plans',   
      name: 'plans',
      component: Plans
    }
  ]
})

export default router
