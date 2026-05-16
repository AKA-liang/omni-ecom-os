<template>
  <div class="app">
    <header class="header">
      <div class="top-bar">
        <div class="top-bar-left">
          <router-link to="/" class="brand-link">
            <div class="brand">
              <div class="brand-logo">
                <Target class="logo-icon" />
              </div>
              <div class="brand-text">
                <h1 class="brand-title">OPC</h1>
                <span class="brand-subtitle">智能体控制中心</span>
              </div>
            </div>
          </router-link>
        </div>

        <nav class="nav-menu">
          <router-link to="/" class="nav-item" exact-active-class="active">
            <LayoutDashboard class="nav-icon" :size="18" />
            <span class="nav-text">控制台</span>
          </router-link>
          <router-link to="/agents" class="nav-item" active-class="active">
            <Users class="nav-icon" :size="18" />
            <span class="nav-text">智能体管理</span>
          </router-link>
          <router-link to="/analytics" class="nav-item" active-class="active">
            <BarChart3 class="nav-icon" :size="18" />
            <span class="nav-text">数据分析</span>
          </router-link>
          <router-link to="/settings" class="nav-item" active-class="active">
            <Settings class="nav-icon" :size="18" />
            <span class="nav-text">系统设置</span>
          </router-link>
        </nav>

        <div class="top-bar-right">
          <button class="action-btn primary" @click="openAddEmployee" title="添加智能体">
            <Plus :size="18" />
            <span>添加智能体</span>
          </button>
          <div class="user-profile" @click="showBossProfile = true">
            <img :src="displayBossAvatar" alt="管理员" class="user-avatar" />
            <div class="user-info">
              <span class="user-name">{{ bossData.name || '管理员' }}</span>
              <span class="user-role">超级管理员</span>
            </div>
            <ChevronDown class="dropdown-icon" :size="14" />
          </div>
        </div>
      </div>

      <div class="status-bar">
        <div class="status-bar-left">
          <span class="breadcrumb">
            <template v-for="(crumb, index) in breadcrumbs" :key="index">
              <span v-if="index > 0" class="breadcrumb-separator">/</span>
              <span class="breadcrumb-item" :class="{ active: index === breadcrumbs.length - 1 }">{{ crumb }}</span>
            </template>
          </span>
        </div>
      </div>
    </header>

    <main class="main">
      <router-view 
        @show-detail="handleShowDetail"
        @show-form="handleShowForm"
        @delete-employee="handleDeleteEmployee"
      />
    </main>

    <EmployeeDetail
      v-if="selectedEmployee"
      :employee="selectedEmployee"
      @close="selectedEmployee = null"
      @edit="handleEditFromDetail"
      @show-tasks="handleShowTasks"
    />

    <BossProfile
      v-if="showBossProfile"
      @close="showBossProfile = false"
      @update="handleBossUpdate"
    />

    <EmployeeForm
      v-if="showEmployeeForm"
      :employee="editingEmployee"
      @close="closeEmployeeForm"
      @save="handleSaveEmployee"
      @delete="handleDeleteEmployee"
    />

    <div v-if="showTaskLog" class="modal-overlay" @click.self="showTaskLog = false">
      <div class="task-log-modal">
        <div class="modal-header-row">
          <h2>{{ taskLogEmployee?.name }} - 任务日志</h2>
          <button class="close-btn" @click="showTaskLog = false">✕</button>
        </div>
        <div class="task-log-body">
          <div v-for="log in taskLogs" :key="log.id" class="task-log-item">
            <div class="log-time">{{ log.time }}</div>
            <div class="log-status-dot" :style="{ backgroundColor: log.color }"></div>
            <div class="log-content">
              <span class="log-task">{{ log.task }}</span>
              <span class="log-result">{{ log.result }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import api from './services/api.js'
import EmployeeDetail from './components/EmployeeDetail.vue'
import BossProfile from './components/BossProfile.vue'
import EmployeeForm from './components/EmployeeForm.vue'
import {
  Target,
  LayoutDashboard,
  Users,
  BarChart3,
  Settings,
  Plus,
  ChevronDown
} from 'lucide-vue-next'

export default {
  name: 'App',
  components: {
    EmployeeDetail,
    BossProfile,
    EmployeeForm,
    Target,
    LayoutDashboard,
    Users,
    BarChart3,
    Settings,
    Plus,
    ChevronDown
  },
  setup() {
    const route = useRoute()
    const selectedEmployee = ref(null)
    const showBossProfile = ref(false)
    const showEmployeeForm = ref(false)
    const editingEmployee = ref(null)
    const showTaskLog = ref(false)
    const taskLogEmployee = ref(null)
    const bossData = ref({ name: '张总', avatar: '' })

    const taskLogs = [
      { id: 1, time: '2026-05-15 10:30', task: '执行日常分析任务', result: '已完成，产出报告 1 份', color: '#34c759' },
      { id: 2, time: '2026-05-15 09:15', task: '处理用户请求 #2847', result: '已响应，耗时 0.3s', color: '#34c759' },
      { id: 3, time: '2026-05-14 18:00', task: '执行数据同步', result: '已完成，同步 150 条记录', color: '#34c759' },
      { id: 4, time: '2026-05-14 14:22', task: '生成周报', result: '已完成，发送至管理邮箱', color: '#007aff' },
      { id: 5, time: '2026-05-14 11:00', task: '系统健康检查', result: '正常，所有指标达标', color: '#34c759' },
    ]

    const breadcrumbs = computed(() => {
      return route.meta?.breadcrumb || ['首页']
    })

    const displayBossAvatar = computed(() => {
      if (bossData.value.avatar && bossData.value.avatar.startsWith('/api/files/')) {
        return api.getFileUrl(bossData.value.avatar.replace('/api/files/', ''))
      }
      return bossData.value.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=boss&backgroundColor=c0aede&radius=50'
    })

    onMounted(async () => {
      try {
        const boss = await api.getBoss()
        bossData.value = boss
      } catch (err) {
        console.error('获取初始化数据失败:', err)
      }
    })

    const openAddEmployee = () => {
      editingEmployee.value = null
      showEmployeeForm.value = true
    }

    const handleShowDetail = (employee) => {
      selectedEmployee.value = employee
    }

    const handleShowForm = (employee) => {
      editingEmployee.value = employee
      showEmployeeForm.value = true
    }

    const handleEditFromDetail = (employee) => {
      selectedEmployee.value = null
      editingEmployee.value = employee
      showEmployeeForm.value = true
    }

    const handleShowTasks = (employee) => {
      taskLogEmployee.value = employee
      showTaskLog.value = true
    }

    const closeEmployeeForm = () => {
      showEmployeeForm.value = false
      editingEmployee.value = null
    }

    const handleSaveEmployee = async (employeeData) => {
      try {
        if (editingEmployee.value) {
          await api.updateEmployee(editingEmployee.value.id, employeeData)
        } else {
          await api.createEmployee(employeeData)
        }
        closeEmployeeForm()
        alert('保存成功')
      } catch (err) {
        console.error('保存员工失败:', err)
        alert('保存失败: ' + err.message)
      }
    }

    const handleDeleteEmployee = async (id) => {
      try {
        await api.deleteEmployee(id)
        selectedEmployee.value = null
        closeEmployeeForm()
        alert('删除成功')
      } catch (err) {
        console.error('删除员工失败:', err)
        alert('删除失败: ' + err.message)
      }
    }

    const handleBossUpdate = (updatedBoss) => {
      bossData.value = updatedBoss
    }

    return {
      bossData,
      displayBossAvatar,
      selectedEmployee,
      showBossProfile,
      showEmployeeForm,
      editingEmployee,
      showTaskLog,
      taskLogEmployee,
      taskLogs,
      breadcrumbs,
      openAddEmployee,
      handleShowDetail,
      handleShowForm,
      handleEditFromDetail,
      handleShowTasks,
      closeEmployeeForm,
      handleSaveEmployee,
      handleDeleteEmployee,
      handleBossUpdate
    }
  }
}
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f2f5 0%, #ffffff 100%);
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.top-bar {
  max-width: 100%;
  margin: 0 auto;
  padding: 0 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.top-bar-left {
  display: flex;
  align-items: center;
}

.brand-link {
  text-decoration: none;
}

.brand {
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand-logo {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #ffffff 0%, rgba(255, 255, 255, 0.9) 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.logo-icon {
  width: 28px;
  height: 28px;
  color: #667eea;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-title {
  font-size: 24px;
  font-weight: 800;
  color: #ffffff;
  margin: 0;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.brand-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  letter-spacing: 1px;
}

.nav-menu {
  display: flex;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: 500;
  font-size: 14px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff;
  transform: translateY(-2px);
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-icon {
  flex-shrink: 0;
}

.top-bar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.action-btn.primary {
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
  color: #667eea;
  border: none;
  font-weight: 600;
}

.action-btn.primary:hover {
  background: linear-gradient(135deg, #ffffff 0%, #ffffff 100%);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.user-profile:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
}

.user-role {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.8);
}

.dropdown-icon {
  color: rgba(255, 255, 255, 0.6);
  flex-shrink: 0;
}

.status-bar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  padding: 12px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.breadcrumb-item {
  color: #86868b;
  font-weight: 500;
}

.breadcrumb-item.active {
  color: #667eea;
  font-weight: 600;
}

.breadcrumb-separator {
  color: #c7c7cc;
}

.main {
  max-width: 1600px;
  margin: 0 auto;
  padding: 40px;
}

.task-log-modal {
  background: #ffffff;
  border-radius: 20px;
  max-width: 700px;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
  animation: slideUp 0.3s ease;
}

.modal-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e5e5ea;
}

.modal-header-row h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.close-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f5f5f7;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #1d1d1f;
  transition: all 0.25s ease;
}

.close-btn:hover {
  background: #e5e5ea;
  transform: scale(1.1);
}

.task-log-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.task-log-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.log-time {
  font-size: 12px;
  color: #86868b;
  white-space: nowrap;
  min-width: 130px;
  font-weight: 500;
}

.log-status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 4px;
}

.log-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.log-task {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
}

.log-result {
  font-size: 13px;
  color: #86868b;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 2000;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1200px) {
  .nav-menu {
    display: none;
  }
}

@media (max-width: 768px) {
  .top-bar {
    padding: 0 20px;
    height: 60px;
  }

  .brand-logo {
    width: 40px;
    height: 40px;
  }

  .logo-icon {
    font-size: 24px;
  }

  .brand-title {
    font-size: 20px;
  }

  .action-btn span:last-child {
    display: none;
  }

  .user-info {
    display: none;
  }

  .status-bar {
    padding: 12px 20px;
  }

  .main {
    padding: 20px;
  }
}
</style>
