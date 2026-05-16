<template>
  <div class="employee-card">
    <div class="card-header">
      <div class="avatar-wrapper" @click.stop="handleAvatarClick">
        <div v-if="hasCustomAvatar" class="avatar-container">
          <img :src="displayAvatar" :alt="employee.name" class="avatar" />
        </div>
        <div v-else class="default-avatar" :style="avatarStyle">
          <span class="avatar-initials">{{ avatarInitials }}</span>
        </div>
        <div class="status-dot" :style="{ backgroundColor: statusInfo.color }"></div>
        <div class="avatar-edit-overlay">
          <Camera :size="20" class="camera-icon" />
        </div>
        <input
          ref="avatarInput"
          type="file"
          accept="image/*"
          @change="handleAvatarChange"
          style="display: none"
        />
      </div>
      <div class="employee-info">
        <h3 class="employee-name">{{ employee.name }}</h3>
        <p class="employee-role">{{ employee.role }}</p>
      </div>
      <div class="action-buttons">
        <button class="edit-btn" @click.stop="$emit('edit', employee)" title="编辑员工">
          <Pencil :size="14" />
        </button>
        <div class="duty-badge" :class="{ 'on-duty': employee.isOnDuty }">
          {{ employee.isOnDuty ? '在岗' : '休息' }}
        </div>
      </div>
    </div>

    <div class="card-body">
      <div class="info-section">
        <div class="section-header">
          <span class="section-label">当前状态</span>
          <span class="status-tag" :style="{ backgroundColor: statusInfo.bgColor, color: statusInfo.color }">
            {{ statusInfo.label }}
          </span>
        </div>
      </div>

      <div class="info-section position-section">
        <div class="section-header">
          <span class="section-label">岗位职责</span>
        </div>
        <p class="position-text">{{ employee.position || '暂无岗位职责' }}</p>
      </div>

      <div class="info-section">
        <div class="section-header">
          <span class="section-label">正在处理</span>
        </div>
        <p class="task-text">{{ employee.currentTask || '暂无任务' }}</p>
      </div>

      <div class="info-section">
        <div class="section-header">
          <span class="section-label">最近产出</span>
        </div>
        <p class="output-text">{{ employee.recentOutput || '暂无产出' }}</p>
      </div>

      <div class="info-section">
        <div class="section-header">
          <span class="section-label">工作时段</span>
        </div>
        <p class="schedule-text">{{ employee.schedule || '未设置' }}</p>
      </div>

      <div v-if="employee.prompt" class="info-section prompt-section">
        <div class="section-header">
          <span class="section-label">角色提示词</span>
        </div>
        <p class="prompt-text">{{ promptSummary }}</p>
      </div>
    </div>

    <div class="card-footer">
      <div class="stat-item">
        <span class="stat-value">{{ employee.tasksCompleted || 0 }}</span>
        <span class="stat-label">完成任务</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ employee.accuracy || '0%' }}</span>
        <span class="stat-label">准确率</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ employee.avgResponseTime || '0s' }}</span>
        <span class="stat-label">响应时间</span>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref } from 'vue'
import { statusMap } from '../data/employees.js'
import api from '../services/api.js'
import { getAvatarGradient, getInitials } from '../utils/avatar.js'
import { Camera, Pencil } from 'lucide-vue-next'

export default {
  name: 'EmployeeCard',
  components: {
    Camera,
    Pencil
  },
  props: {
    employee: {
      type: Object,
      required: true
    }
  },
  emits: ['edit', 'avatar-upload'],
  setup(props, { emit }) {
    const statusInfo = computed(() => {
      return statusMap[props.employee.status] || statusMap.offline
    })

    const hasCustomAvatar = computed(() => {
      return props.employee.avatar && !props.employee.avatar.includes('dicebear')
    })

    const displayAvatar = computed(() => {
      if (props.employee.avatar && props.employee.avatar.startsWith('/api/files/')) {
        return api.getFileUrl(props.employee.avatar.replace('/api/files/', ''))
      }
      return props.employee.avatar
    })

    const avatarStyle = computed(() => {
      return getAvatarGradient(props.employee.name)
    })

    const avatarInitials = computed(() => {
      return getInitials(props.employee.name)
    })

    const promptSummary = computed(() => {
      if (!props.employee.prompt) return ''
      const text = props.employee.prompt
      return text.length > 80 ? text.substring(0, 80) + '...' : text
    })

    const avatarInput = ref(null)

    const handleAvatarClick = () => {
      avatarInput.value?.click()
    }

    const handleAvatarChange = (event) => {
      const file = event.target.files[0]
      if (file) {
        const reader = new FileReader()
        reader.onload = (e) => {
          const tempUrl = e.target.result
          emit('avatar-upload', { employee: props.employee, file: file, tempUrl: tempUrl })
        }
        reader.readAsDataURL(file)
      }
      event.target.value = ''
    }

    return {
      statusInfo,
      displayAvatar,
      avatarInput,
      handleAvatarClick,
      handleAvatarChange,
      hasCustomAvatar,
      avatarStyle,
      avatarInitials,
      promptSummary
    }
  }
}
</script>

<style scoped>
.employee-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.1, 0.25, 1);
  border: 1px solid rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  animation: cardGlow 2s ease-in-out infinite;
}

@keyframes cardGlow {
  0%, 100% {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04), 0 0 0 0 rgba(102, 126, 234, 0);
  }
  50% {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04), 0 0 20px 5px rgba(102, 126, 234, 0.4);
  }
}

.employee-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  opacity: 0.8;
  animation: topBarPulse 2s ease-in-out infinite;
}

@keyframes topBarPulse {
  0%, 100% {
    opacity: 0.4;
  }
  50% {
    opacity: 1;
  }
}

.employee-card:hover::before {
  opacity: 1;
  animation: none;
}

.employee-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.15), 0 8px 24px rgba(0, 0, 0, 0.08), 0 0 30px 10px rgba(102, 126, 234, 0.5);
  border-color: rgba(102, 126, 234, 0.3);
  animation: none;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar-container,
.default-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  transition: transform 0.3s ease;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border: 2px solid #f5f5f7;
}

.default-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-initials {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.employee-card:hover .avatar-container,
.employee-card:hover .default-avatar {
  transform: scale(1.08);
}

.status-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 2px solid #ffffff;
}

.avatar-edit-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
}

.avatar-wrapper:hover .avatar-edit-overlay {
  opacity: 1;
}

.avatar-edit-overlay .camera-icon {
  color: #ffffff;
}

.employee-info {
  flex: 1;
  min-width: 0;
}

.employee-name {
  font-size: 18px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 4px;
  letter-spacing: -0.3px;
  background: linear-gradient(135deg, #1d1d1f 0%, #495057 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.employee-role {
  font-size: 13px;
  color: #86868b;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.edit-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.25s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.edit-btn :deep(svg) {
  color: #1d1d1f;
}

.edit-btn:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.duty-badge {
  padding: 6px 12px;
  background: #f5f5f7;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #86868b;
  white-space: nowrap;
}

.duty-badge.on-duty {
  background: #d4edda;
  color: #155724;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 20px;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-label {
  font-size: 12px;
  font-weight: 600;
  color: #86868b;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.status-tag {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.position-text,
.task-text,
.output-text {
  font-size: 14px;
  color: #1d1d1f;
  line-height: 1.5;
  font-weight: 400;
}

.position-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 12px;
  border-radius: 10px;
  border-left: 4px solid #667eea;
  transition: all 0.3s ease;
}

.employee-card:hover .position-section {
  background: linear-gradient(135deg, #f0f1ff 0%, #e8ebff 100%);
  transform: translateX(4px);
}

.position-section .section-label {
  color: #667eea;
  font-weight: 600;
}

.position-text {
  font-weight: 500;
  color: #495057;
  line-height: 1.6;
}

.schedule-text {
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
}

.prompt-section {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  padding: 10px;
  border-radius: 8px;
  border-left: 3px solid #f59e0b;
}

.prompt-section .section-label {
  color: #d97706;
  font-weight: 600;
  font-size: 11px;
}

.prompt-text {
  font-size: 12px;
  color: #92400e;
  line-height: 1.5;
  font-weight: 400;
}

.card-footer {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.stat-item {
  text-align: center;
  padding: 12px 8px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 10px;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  background: linear-gradient(135deg, #ffffff 0%, #f0f1ff 100%);
}

.stat-value {
  display: block;
  font-size: 16px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 10px;
  color: #86868b;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.2px;
}

@media (max-width: 768px) {
  .card-header {
    flex-wrap: wrap;
  }

  .action-buttons {
    order: 3;
    flex-direction: row;
    width: 100%;
    justify-content: space-between;
  }

  .card-footer {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .stat-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 16px;
  }

  .stat-value {
    margin-bottom: 0;
  }
}
</style>
