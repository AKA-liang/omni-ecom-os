import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { title: '控制台', breadcrumb: ['首页', '控制台'] }
  },
  {
    path: '/agents',
    name: 'AgentManagement',
    component: () => import('../views/AgentManagement.vue'),
    meta: { title: '智能体管理', breadcrumb: ['首页', '智能体管理'] }
  },
  {
    path: '/analytics',
    name: 'DataAnalytics',
    component: () => import('../views/DataAnalytics.vue'),
    meta: { title: '数据分析', breadcrumb: ['首页', '数据分析'] }
  },
  {
    path: '/settings',
    name: 'SystemSettings',
    component: () => import('../views/SystemSettings.vue'),
    meta: { title: '系统设置', breadcrumb: ['首页', '系统设置'] }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'OPC'} - 智能体控制中心`
  next()
})

export default router
