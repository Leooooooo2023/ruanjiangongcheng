import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/',
    component: () => import('../components/PortalLayout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('../views/Home.vue') },
      { path: 'repairs', name: 'Repairs', component: () => import('../views/Repairs.vue') },
      { path: 'repairs/create', name: 'CreateRepair', component: () => import('../views/CreateRepair.vue') },
      { path: 'complaints', name: 'Complaints', component: () => import('../views/Complaints.vue') },
      { path: 'complaints/create', name: 'CreateComplaint', component: () => import('../views/CreateComplaint.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue') },
      { path: 'parking', name: 'Parking', component: () => import('../views/Parking.vue') },
      { path: 'building', name: 'Building', component: () => import('../views/Building.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('portal_token')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && token) {
    next('/home')
  } else {
    next()
  }
})

export default router
