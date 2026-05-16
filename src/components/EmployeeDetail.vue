<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <button class="close-btn" @click="$emit('close')">
        <span>✕</span>
      </button>

      <div class="detail-header">
        <div class="avatar-section">
          <img :src="employee.avatar" :alt="employee.name" class="large-avatar" />
          <div class="status-badge" :style="{ backgroundColor: statusInfo.color }">
            {{ statusInfo.label }}
          </div>
        </div>

        <div class="header-content">
          <div class="name-section">
            <h2 class="employee-name">{{ employee.name }}</h2>
            <p class="employee-position">{{ employee.position }}</p>
          </div>
          
          <span class="role-badge">{{ employee.role }}</span>
          
          <div class="quick-stats">
            <div class="stat-card">
              <span class="stat-value">{{ employee.tasksCompleted || 0 }}</span>
              <span class="stat-label">完成任务</span>
            </div>
            <div class="stat-card">
              <span class="stat-value">{{ employee.accuracy || '0%' }}</span>
              <span class="stat-label">准确率</span>
            </div>
            <div class="stat-card">
              <span class="stat-value">{{ employee.avgResponseTime || '0s' }}</span>
              <span class="stat-label">响应时间</span>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-body">
        <div class="info-section">
          <h3 class="section-title">当前任务</h3>
          <p class="task-description">{{ employee.currentTask }}</p>
        </div>

        <div class="info-section">
          <h3 class="section-title">最近产出</h3>
          <p class="output-description">{{ employee.recentOutput }}</p>
        </div>

        <div class="info-section">
          <h3 class="section-title">核心技能</h3>
          <div class="skills-list">
            <span 
              v-for="(skill, index) in skillsList" 
              :key="index"
              class="skill-tag"
            >
              {{ skill }}
            </span>
          </div>
        </div>

        <div class="info-grid">
          <div class="grid-item">
            <span class="grid-label">排班状态</span>
            <span class="grid-value" :class="{ 'on-duty': employee.isOnDuty }">
              {{ employee.isOnDuty ? '在岗' : '休息' }}
            </span>
          </div>
          <div class="grid-item">
            <span class="grid-label">工作时间</span>
            <span class="grid-value">{{ employee.schedule }}</span>
          </div>
          <div class="grid-item">
            <span class="grid-label">员工编号</span>
            <span class="grid-value">#{{ employee.id.toString().padStart(4, '0') }}</span>
          </div>
          <div class="grid-item">
            <span class="grid-label">所属分类</span>
            <span class="grid-value">{{ employee.category.toUpperCase() }}</span>
          </div>
        </div>

        <div v-if="employee.prompt" class="prompt-section">
          <h3 class="section-title">角色提示词</h3>
          <div class="prompt-content">
            {{ employee.prompt }}
          </div>
        </div>

        <div class="performance-section">
          <h3 class="section-title">绩效指标</h3>
          <div class="performance-bar-container">
            <div class="bar-label">
              <span>准确率</span>
              <span class="bar-value">{{ employee.accuracy || '0%' }}</span>
            </div>
            <div class="performance-bar">
              <div class="bar-fill" :style="{ width: accuracyValue + '%' }"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-footer">
        <button class="action-btn disabled" disabled title="即将上线">
          <span>发送消息</span>
          <span class="coming-soon">即将上线</span>
        </button>
        <button class="action-btn secondary" @click="$emit('show-tasks', employee)">
          <span>查看任务日志</span>
        </button>
        <button class="action-btn primary" @click="$emit('edit', employee)">
          <span>管理设置</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { statusMap } from '../data/employees.js'

export default {
  name: 'EmployeeDetail',
  props: {
    employee: {
      type: Object,
      required: true
    }
  },
  emits: ['close', 'edit', 'show-tasks'],
  setup(props) {
    const statusInfo = computed(() => {
      return statusMap[props.employee.status] || statusMap.offline
    })

    const skillsList = computed(() => {
      if (Array.isArray(props.employee.skills)) return props.employee.skills
      if (typeof props.employee.skills === 'string' && props.employee.skills) {
        return props.employee.skills.split(',').map(s => s.trim()).filter(s => s)
      }
      return []
    })

    const accuracyValue = computed(() => {
      return parseFloat(props.employee.accuracy) || 0
    })

    return { statusInfo, skillsList, accuracyValue }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 1000;
  animation: fadeIn 0.25s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-container {
  background: #ffffff;
  border-radius: 24px;
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  animation: slideUp 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.25);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f5f5f7;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #1d1d1f;
  transition: all 0.25s ease;
  z-index: 10;
}

.close-btn:hover {
  background: #e5e5ea;
  transform: scale(1.1);
}

.detail-header {
  display: flex;
  gap: 32px;
  padding: 40px;
  background: #f8f9fa;
  border-radius: 24px 24px 0 0;
  align-items: flex-start;
}

.avatar-section {
  position: relative;
  flex-shrink: 0;
}

.large-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.status-badge {
  position: absolute;
  bottom: 8px;
  right: 8px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  border: 3px solid #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  white-space: nowrap;
}

.header-content {
  flex: 1;
  padding-top: 4px;
}

.name-section {
  margin-bottom: 16px;
}

.employee-name {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 6px;
  letter-spacing: -0.5px;
}

.employee-position {
  font-size: 16px;
  color: #86868b;
  font-weight: 500;
}

.role-badge {
  display: inline-block;
  padding: 8px 18px;
  background: #007aff;
  color: white;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 24px;
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.stat-card {
  background: #ffffff;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #86868b;
  font-weight: 600;
}

.detail-body {
  padding: 32px 40px;
}

.info-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 12px;
}

.task-description,
.output-description {
  font-size: 15px;
  color: #1d1d1f;
  line-height: 1.6;
  font-weight: 400;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 3px solid #007aff;
}

.skills-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-tag {
  padding: 8px 16px;
  background: #007aff;
  color: white;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.grid-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: #f8f9fa;
  border-radius: 10px;
}

.grid-label {
  font-size: 13px;
  font-weight: 600;
  color: #86868b;
}

.grid-value {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
}

.grid-value.on-duty {
  color: #34c759;
}

.prompt-section {
  margin-top: 24px;
  margin-bottom: 24px;
}

.prompt-content {
  font-size: 14px;
  color: #1d1d1f;
  line-height: 1.8;
  font-weight: 400;
  padding: 20px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 12px;
  border-left: 4px solid #f59e0b;
  max-height: 300px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.prompt-content::-webkit-scrollbar {
  width: 6px;
}

.prompt-content::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.prompt-content::-webkit-scrollbar-thumb {
  background: #d97706;
  border-radius: 3px;
}

.prompt-content::-webkit-scrollbar-thumb:hover {
  background: #b45309;
}

.performance-section {
  margin-top: 24px;
}

.performance-bar-container {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.bar-label {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  font-weight: 600;
  color: #86868b;
  margin-bottom: 10px;
}

.bar-value {
  color: #007aff;
  font-weight: 700;
}

.performance-bar {
  height: 8px;
  background: #e5e5ea;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: #007aff;
  border-radius: 4px;
  transition: width 1s ease;
}

.detail-footer {
  display: flex;
  gap: 12px;
  padding: 28px 40px;
  background: #f8f9fa;
  border-radius: 0 0 24px 24px;
}

.action-btn {
  flex: 1;
  padding: 14px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.25s ease;
  border: none;
}

.action-btn.primary {
  background: #007aff;
  color: white;
}

.action-btn.primary:hover {
  background: #0051d5;
  transform: translateY(-2px);
}

.action-btn.secondary {
  background: #ffffff;
  color: #1d1d1f;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.action-btn.secondary:hover {
  background: #f5f5f7;
  transform: translateY(-2px);
}

.action-btn.disabled {
  background: #f5f5f7;
  color: #c7c7cc;
  cursor: not-allowed;
  position: relative;
}

.action-btn.disabled:hover {
  transform: none;
}

.coming-soon {
  font-size: 10px;
  background: #e5e5ea;
  color: #86868b;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 4px;
}

@media (max-width: 768px) {
  .modal-container {
    max-height: 95vh;
    border-radius: 20px;
  }

  .detail-header {
    flex-direction: column;
    padding: 28px 24px;
    gap: 24px;
  }

  .large-avatar {
    width: 100px;
    height: 100px;
  }

  .employee-name {
    font-size: 26px;
  }

  .quick-stats {
    grid-template-columns: 1fr;
  }

  .detail-body {
    padding: 24px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .detail-footer {
    flex-direction: column;
    padding: 24px;
  }
}
</style>
