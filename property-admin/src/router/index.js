import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../components/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'buildings', name: 'Buildings', component: () => import('../views/Buildings.vue') },
      { path: 'employees', name: 'Employees', component: () => import('../views/Employees.vue') },
      { path: 'owners', name: 'Owners', component: () => import('../views/Owners.vue') },
      { path: 'parkings', name: 'Parkings', component: () => import('../views/Parkings.vue') },
      { path: 'repairs', name: 'Repairs', component: () => import('../views/Repairs.vue') },
      { path: 'complaints', name: 'Complaints', component: () => import('../views/Complaints.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
