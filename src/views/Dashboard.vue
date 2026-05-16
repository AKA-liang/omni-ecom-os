<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card total">
        <div class="stat-icon-wrapper">
          <Users class="stat-icon" :size="22" />
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ employees.length }}</span>
          <span class="stat-label">智能体总数</span>
        </div>
        <div class="stat-decoration"></div>
      </div>
      <div class="stat-card online">
        <div class="stat-icon-wrapper">
          <Wifi class="stat-icon" :size="22" />
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ onlineCount }}</span>
          <span class="stat-label">在线运行</span>
        </div>
        <div class="stat-decoration"></div>
      </div>
      <div class="stat-card offline">
        <div class="stat-icon-wrapper">
          <WifiOff class="stat-icon" :size="22" />
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ employees.length - onlineCount }}</span>
          <span class="stat-label">离线待命</span>
        </div>
        <div class="stat-decoration"></div>
      </div>
      <div class="stat-card accuracy">
        <div class="stat-icon-wrapper">
          <Target class="stat-icon" :size="22" />
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ averageAccuracy }}</span>
          <span class="stat-label">平均准确率</span>
        </div>
        <div class="stat-decoration"></div>
      </div>
    </div>

    <div class="filter-bar">
      <CategoryFilter
        :categories="categories"
        :selectedCategory="selectedCategory"
        @select="handleCategorySelect"
      />
      <button class="add-agent-btn" @click="$emit('showForm', null)">
        <Plus :size="18" />
        <span>添加智能体</span>
      </button>
    </div>

    <div class="employees-grid">
      <EmployeeCard
        v-for="employee in filteredEmployees"
        :key="employee.id"
        :employee="employee"
        @click="$emit('showDetail', employee)"
        @edit="$emit('showForm', employee)"
        @avatar-upload="handleAvatarUpload"
      />
    </div>

    <div v-if="filteredEmployees.length === 0" class="empty-state">
      <Search class="empty-icon" :size="48" />
      <p class="empty-text">暂无匹配的智能体</p>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { employees as defaultEmployees, categories } from '../data/employees.js'
import api from '../services/api.js'
import CategoryFilter from '../components/CategoryFilter.vue'
import EmployeeCard from '../components/EmployeeCard.vue'
import { Users, Wifi, WifiOff, Target, Plus, Search } from 'lucide-vue-next'

export default {
  name: 'Dashboard',
  components: {
    CategoryFilter,
    EmployeeCard,
    Users,
    Wifi,
    WifiOff,
    Target,
    Plus,
    Search
  },
  emits: ['showDetail', 'showForm', 'deleteEmployee'],
  setup(_, { emit }) {
    const selectedCategory = ref('all')
    const employees = ref([...defaultEmployees])

    const filteredEmployees = computed(() => {
      if (selectedCategory.value === 'all') {
        return employees.value
      }
      return employees.value.filter(emp => emp.category === selectedCategory.value)
    })

    const onlineCount = computed(() => {
      return employees.value.filter(emp => emp.status === 'working' || emp.status === 'online').length
    })

    const averageAccuracy = computed(() => {
      if (employees.value.length === 0) return '0%'
      const total = employees.value.reduce((sum, emp) => {
        const val = parseFloat(emp.accuracy) || 0
        return sum + val
      }, 0)
      return (total / employees.value.length).toFixed(1) + '%'
    })

    const handleCategorySelect = (category) => {
      selectedCategory.value = category
    }

    const handleAvatarUpload = async ({ employee, file, tempUrl }) => {
      try {
        const index = employees.value.findIndex(e => e.id === employee.id)
        if (index !== -1) {
          employees.value[index] = { ...employees.value[index], avatar: tempUrl }
        }

        const avatarUrl = await api.uploadEmployeeAvatar(file)

        if (index !== -1) {
          employees.value[index] = { ...employees.value[index], avatar: avatarUrl }
        }
      } catch (err) {
        console.error('头像上传失败:', err)
        const index = employees.value.findIndex(e => e.id === employee.id)
        if (index !== -1) {
          const original = defaultEmployees.find(e => e.id === employee.id)
          if (original) {
            employees.value[index] = { ...employees.value[index], avatar: original.avatar }
          }
        }
      }
    }

    onMounted(async () => {
      try {
        const data = await api.getEmployees()
        if (Array.isArray(data) && data.length > 0) {
          employees.value = data
        }
      } catch (err) {
        console.error('从API加载智能体失败，使用本地数据:', err)
      }
    })

    return {
      employees,
      categories,
      selectedCategory,
      filteredEmployees,
      onlineCount,
      averageAccuracy,
      handleCategorySelect,
      handleAvatarUpload
    }
  }
}
</script>

<style scoped>
.dashboard {
  max-width: 1600px;
  margin: 0 auto;
  padding: 32px 40px 40px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 8px;
}

.stat-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  border-radius: 16px;
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
}

.stat-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-card.total .stat-icon-wrapper {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.online .stat-icon-wrapper {
  background: linear-gradient(135deg, #34c759 0%, #30d158 100%);
}

.stat-card.offline .stat-icon-wrapper {
  background: linear-gradient(135deg, #8e8e93 0%, #aeaeb2 100%);
}

.stat-card.accuracy .stat-icon-wrapper {
  background: linear-gradient(135deg, #ff9500 0%, #ffac33 100%);
}

.stat-icon {
  color: #ffffff;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  color: #1d1d1f;
  line-height: 1.2;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 12px;
  font-weight: 500;
  color: #86868b;
  margin-top: 2px;
}

.stat-decoration {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  opacity: 0.06;
}

.stat-card.total .stat-decoration {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.online .stat-decoration {
  background: #34c759;
}

.stat-card.offline .stat-decoration {
  background: #8e8e93;
}

.stat-card.accuracy .stat-decoration {
  background: #ff9500;
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  padding: 0 0 0 0;
}

.filter-bar :deep(.category-filter) {
  border-bottom: none;
  padding: 24px 0;
}

.add-agent-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.add-agent-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.employees-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 24px;
  margin-top: 32px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #86868b;
}

.empty-icon {
  color: #c7c7cc;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 20px 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }

  .stat-card {
    padding: 16px;
    gap: 12px;
  }

  .stat-icon-wrapper {
    width: 40px;
    height: 40px;
    border-radius: 10px;
  }

  .stat-value {
    font-size: 20px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .add-agent-btn {
    justify-content: center;
  }

  .employees-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    margin-top: 20px;
  }
}
</style>
