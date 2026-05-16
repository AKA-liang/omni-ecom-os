<template>
  <div class="agent-management">
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="search-box">
          <Search :size="16" class="search-icon" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索智能体名称..."
            class="search-input"
          />
        </div>
        <div class="filter-group">
          <select v-model="filterCategory" class="filter-select">
            <option value="">全部分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
          </select>
          <select v-model="filterStatus" class="filter-select">
            <option value="">全部状态</option>
            <option v-for="(info, key) in statusMap" :key="key" :value="key">{{ info.label }}</option>
          </select>
        </div>
      </div>
      <div class="toolbar-right">
        <button class="add-btn" @click="$emit('showForm', null)">
          <Plus :size="16" />
          <span>添加智能体</span>
        </button>
      </div>
    </div>

    <div class="table-container">
      <table v-if="pagedEmployees.length > 0" class="agent-table">
        <thead>
          <tr>
            <th class="col-avatar">头像</th>
            <th class="col-name">姓名</th>
            <th class="col-role">角色</th>
            <th class="col-category">分类</th>
            <th class="col-status">状态</th>
            <th class="col-tasks">完成任务</th>
            <th class="col-accuracy">准确率</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="emp in pagedEmployees"
            :key="emp.id"
            class="table-row"
            @click="$emit('showDetail', emp)"
          >
            <td class="col-avatar">
              <div v-if="emp.avatar" class="avatar-cell">
                <img :src="resolveAvatar(emp.avatar)" :alt="emp.name" class="avatar-img" />
              </div>
              <div v-else class="avatar-cell default-avatar" :style="getAvatarGradient(emp.name)">
                <span class="avatar-initials">{{ getInitials(emp.name) }}</span>
              </div>
            </td>
            <td class="col-name">{{ emp.name }}</td>
            <td class="col-role">{{ emp.role }}</td>
            <td class="col-category">{{ getCategoryName(emp.category) }}</td>
            <td class="col-status">
              <span
                class="status-badge"
                :style="{ color: getStatusInfo(emp.status).color, backgroundColor: getStatusInfo(emp.status).bgColor }"
              >
                {{ getStatusInfo(emp.status).label }}
              </span>
            </td>
            <td class="col-tasks">{{ emp.tasksCompleted || 0 }}</td>
            <td class="col-accuracy">{{ emp.accuracy || '0%' }}</td>
            <td class="col-actions" @click.stop>
              <button class="action-btn edit" @click="$emit('showForm', emp)">编辑</button>
              <button class="action-btn delete" @click="handleDelete(emp)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="empty-state">
        <Inbox :size="48" class="empty-icon" />
        <p class="empty-text">暂无匹配的智能体</p>
        <p class="empty-hint">尝试调整搜索条件或筛选器</p>
      </div>
    </div>

    <div v-if="totalPages > 1" class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">
        <ChevronLeft :size="16" />
        上一页
      </button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">
        下一页
        <ChevronRight :size="16" />
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import api from '../services/api.js'
import { statusMap } from '../data/employees.js'
import { getAvatarGradient, getInitials } from '../utils/avatar.js'
import { Search, Plus, ChevronLeft, ChevronRight, Inbox } from 'lucide-vue-next'

export default {
  name: 'AgentManagement',
  components: { Search, Plus, ChevronLeft, ChevronRight, Inbox },
  emits: ['showDetail', 'showForm'],
  setup(props, { emit }) {
    const employees = ref([])
    const categories = ref([])
    const searchQuery = ref('')
    const filterCategory = ref('')
    const filterStatus = ref('')
    const currentPage = ref(1)
    const pageSize = 10

    onMounted(async () => {
      try {
        const [emps, cats] = await Promise.all([
          api.getEmployees(),
          api.getCategories()
        ])
        employees.value = emps
        categories.value = cats
      } catch (err) {
        console.error('加载数据失败:', err)
      }
    })

    watch([searchQuery, filterCategory, filterStatus], () => {
      currentPage.value = 1
    })

    const filteredEmployees = computed(() => {
      let result = employees.value
      if (searchQuery.value) {
        const q = searchQuery.value.toLowerCase()
        result = result.filter(e => e.name && e.name.toLowerCase().includes(q))
      }
      if (filterCategory.value) {
        result = result.filter(e => e.category === filterCategory.value)
      }
      if (filterStatus.value) {
        result = result.filter(e => e.status === filterStatus.value)
      }
      return result
    })

    const totalPages = computed(() => Math.max(1, Math.ceil(filteredEmployees.value.length / pageSize)))

    const pagedEmployees = computed(() => {
      const start = (currentPage.value - 1) * pageSize
      return filteredEmployees.value.slice(start, start + pageSize)
    })

    const getCategoryName = (categoryId) => {
      const cat = categories.value.find(c => c.id === categoryId)
      return cat ? cat.name : categoryId || '-'
    }

    const getStatusInfo = (status) => {
      return statusMap[status] || statusMap.offline
    }

    const resolveAvatar = (avatar) => {
      if (avatar && avatar.startsWith('/api/files/')) {
        return api.getFileUrl(avatar.replace('/api/files/', ''))
      }
      return avatar
    }

    const handleDelete = async (emp) => {
      if (!confirm(`确定删除「${emp.name}」？`)) return
      try {
        await api.deleteEmployee(emp.id)
        employees.value = employees.value.filter(e => e.id !== emp.id)
      } catch (err) {
        console.error('删除失败:', err)
        alert('删除失败: ' + err.message)
      }
    }

    return {
      employees,
      categories,
      searchQuery,
      filterCategory,
      filterStatus,
      currentPage,
      totalPages,
      pagedEmployees,
      statusMap,
      getCategoryName,
      getStatusInfo,
      resolveAvatar,
      getAvatarGradient,
      getInitials,
      handleDelete
    }
  }
}
</script>

<style scoped>
.agent-management {
  padding: 24px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 12px;
  color: #86868b;
  pointer-events: none;
}

.search-input {
  padding: 10px 12px 10px 36px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  font-size: 14px;
  width: 240px;
  background: #ffffff;
  transition: all 0.25s ease;
  outline: none;
}

.search-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.filter-group {
  display: flex;
  gap: 8px;
}

.filter-select {
  padding: 10px 32px 10px 12px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  font-size: 14px;
  background: #ffffff;
  cursor: pointer;
  outline: none;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%2386868b' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  transition: all 0.25s ease;
}

.filter-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.table-container {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.agent-table {
  width: 100%;
  border-collapse: collapse;
}

.agent-table thead {
  background: linear-gradient(135deg, #f8f9fa 0%, #f0f1ff 100%);
}

.agent-table th {
  padding: 14px 16px;
  text-align: left;
  font-size: 12px;
  font-weight: 600;
  color: #86868b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  white-space: nowrap;
}

.agent-table td {
  padding: 14px 16px;
  font-size: 14px;
  color: #1d1d1f;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  vertical-align: middle;
}

.table-row {
  cursor: pointer;
  transition: all 0.2s ease;
}

.table-row:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04) 0%, rgba(118, 75, 162, 0.04) 100%);
}

.table-row:hover td {
  color: #667eea;
}

.table-row:hover .col-actions .action-btn {
  opacity: 1;
}

.col-avatar {
  width: 56px;
}

.avatar-cell {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.avatar-initials {
  font-size: 16px;
  font-weight: 700;
  color: #ffffff;
}

.col-name {
  font-weight: 600;
  white-space: nowrap;
}

.col-role {
  color: #86868b;
  white-space: nowrap;
}

.col-category {
  white-space: nowrap;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.col-actions {
  white-space: nowrap;
}

.col-actions .action-btn {
  padding: 5px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-right: 6px;
  opacity: 0.7;
}

.col-actions .action-btn.edit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
}

.col-actions .action-btn.edit:hover {
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  transform: translateY(-1px);
  opacity: 1;
}

.col-actions .action-btn.delete {
  background: #f5f5f7;
  color: #ff3b30;
}

.col-actions .action-btn.delete:hover {
  background: #ff3b30;
  color: #ffffff;
  transform: translateY(-1px);
  opacity: 1;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 24px;
}

.empty-icon {
  color: #c7c7cc;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  font-weight: 600;
  color: #86868b;
  margin: 0 0 4px;
}

.empty-hint {
  font-size: 13px;
  color: #c7c7cc;
  margin: 0;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 20px;
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  background: #ffffff;
  font-size: 13px;
  font-weight: 500;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-info {
  font-size: 13px;
  font-weight: 600;
  color: #86868b;
}

@media (max-width: 768px) {
  .agent-management {
    padding: 16px;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-left {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .filter-group {
    flex-direction: column;
  }

  .add-btn {
    justify-content: center;
  }

  .table-container {
    overflow-x: auto;
  }

  .agent-table {
    min-width: 640px;
  }
}
</style>
