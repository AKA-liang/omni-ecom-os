<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <div class="modal-header">
        <h2>{{ isEdit ? '编辑员工' : '添加新员工' }}</h2>
        <button class="close-btn" @click="$emit('close')">✕</button>
      </div>

      <div class="modal-body">
        <div class="avatar-section">
          <EmployeeAvatarUpload 
            :currentAvatar="formData.avatar"
            @upload-success="handleAvatarSuccess"
            @upload-error="handleAvatarError"
          />
        </div>

        <div class="form-grid">
          <div class="form-item">
            <label>姓名 *</label>
            <input v-model="formData.name" type="text" placeholder="输入员工姓名" required />
          </div>

          <div class="form-item">
            <label>角色</label>
            <select v-model="formData.role">
              <option value="">选择角色</option>
              <option value="AI电商系列">AI电商系列</option>
              <option value="AI编程系列">AI编程系列</option>
              <option value="AI培训板块">AI培训板块</option>
            </select>
          </div>

          <div class="form-item">
            <label>分类</label>
            <select v-model="formData.category">
              <option value="">选择分类</option>
              <option value="ecommerce">电商系列</option>
              <option value="programming">编程系列</option>
              <option value="training">培训板块</option>
            </select>
          </div>

          <div class="form-item">
            <label>状态</label>
            <select v-model="formData.status">
              <option value="working">工作中</option>
              <option value="online">在线</option>
              <option value="offline">离线</option>
              <option value="busy">忙碌</option>
            </select>
          </div>

          <div class="form-item full-width">
            <label>岗位职责</label>
            <input v-model="formData.position" type="text" placeholder="输入岗位职责描述" />
          </div>

          <div class="form-item full-width">
            <label>当前任务</label>
            <input v-model="formData.currentTask" type="text" placeholder="正在处理的任务" />
          </div>

          <div class="form-item full-width">
            <label>最近产出</label>
            <input v-model="formData.recentOutput" type="text" placeholder="最近的产出成果" />
          </div>

          <div class="form-item">
            <label>工作时段</label>
            <input v-model="formData.schedule" type="text" placeholder="如：09:00-18:00" />
          </div>

          <div class="form-item">
            <label>完成任务数</label>
            <input v-model.number="formData.tasksCompleted" type="number" placeholder="0" />
          </div>

          <div class="form-item">
            <label>准确率</label>
            <input v-model="formData.accuracy" type="text" placeholder="如：96.8%" />
          </div>

          <div class="form-item">
            <label>响应时间</label>
            <input v-model="formData.avgResponseTime" type="text" placeholder="如：0.3s" />
          </div>

          <div class="form-item">
            <label>是否在岗</label>
            <select v-model="formData.isOnDuty">
              <option :value="true">在岗</option>
              <option :value="false">休息</option>
            </select>
          </div>

          <div class="form-item full-width">
            <label>核心技能（用逗号分隔）</label>
            <input v-model="skillsText" type="text" placeholder="技能1, 技能2, 技能3" />
          </div>

          <div class="form-item full-width">
            <label>角色提示词</label>
            <textarea
              v-model="formData.prompt"
              rows="6"
              placeholder="输入角色提示词，用于定义智能体的行为和职责..."
            ></textarea>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button v-if="isEdit" class="delete-btn" @click="handleDelete">删除员工</button>
        <div class="action-group">
          <button class="cancel-btn" @click="$emit('close')">取消</button>
          <button class="save-btn" @click="handleSave">
            {{ isEdit ? '保存修改' : '添加员工' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'
import EmployeeAvatarUpload from './EmployeeAvatarUpload.vue'

export default {
  name: 'EmployeeForm',
  components: {
    EmployeeAvatarUpload
  },
  emits: ['close', 'save', 'delete'],
  props: {
    employee: {
      type: Object,
      default: null
    }
  },
  setup(props, { emit }) {
    const isEdit = computed(() => !!props.employee)
    
    const formData = ref({
      name: '',
      position: '',
      role: '',
      avatar: '',
      status: 'online',
      currentTask: '',
      recentOutput: '',
      isOnDuty: true,
      schedule: '',
      category: '',
      tasksCompleted: 0,
      accuracy: '',
      avgResponseTime: '',
      prompt: ''
    })

    const skillsText = ref('')

    watch(() => props.employee, (newEmployee) => {
      if (newEmployee) {
        Object.keys(formData.value).forEach(key => {
          formData.value[key] = newEmployee[key] ?? ''
        })
        skillsText.value = Array.isArray(newEmployee.skills) 
          ? newEmployee.skills.join(', ') 
          : (newEmployee.skills || '')
      } else {
        Object.keys(formData.value).forEach(key => {
          formData.value[key] = key === 'isOnDuty' ? true : ''
        })
        skillsText.value = ''
      }
    }, { immediate: true })

    const handleAvatarSuccess = (avatarUrl) => {
      formData.value.avatar = avatarUrl
    }

    const handleAvatarError = (error) => {
      alert(error)
    }

    const handleSave = () => {
      if (!formData.value.name) {
        alert('请输入员工姓名')
        return
      }

      const skills = skillsText.value
        .split(',')
        .map(s => s.trim())
        .filter(s => s)

      emit('save', {
        ...formData.value,
        skills: skills.join(',')
      })
    }

    const handleDelete = () => {
      if (confirm('确定要删除这个员工吗？')) {
        emit('delete', formData.value.id)
      }
    }

    return {
      formData,
      skillsText,
      isEdit,
      handleAvatarSuccess,
      handleAvatarError,
      handleSave,
      handleDelete
    }
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
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 2000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-container {
  background: #ffffff;
  border-radius: 20px;
  max-width: 700px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease;
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

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e5e5ea;
}

.modal-header h2 {
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

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item.full-width {
  grid-column: 1 / -1;
}

.form-item label {
  font-size: 13px;
  font-weight: 600;
  color: #86868b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.form-item input,
.form-item select {
  padding: 12px;
  font-size: 15px;
  color: #1d1d1f;
  background: #ffffff;
  border: 2px solid #e5e5ea;
  border-radius: 8px;
  outline: none;
  transition: all 0.25s ease;
}

.form-item input:focus,
.form-item select:focus,
.form-item textarea:focus {
  border-color: #007aff;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.form-item input::placeholder,
.form-item textarea::placeholder {
  color: #c7c7cc;
}

.form-item textarea {
  width: 100%;
  padding: 12px;
  font-size: 14px;
  color: #1d1d1f;
  background: #ffffff;
  border: 2px solid #e5e5ea;
  border-radius: 8px;
  outline: none;
  transition: all 0.25s ease;
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  line-height: 1.6;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: #f8f9fa;
  border-top: 1px solid #e5e5ea;
}

.action-group {
  display: flex;
  gap: 12px;
}

.cancel-btn,
.save-btn,
.delete-btn {
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  border: none;
}

.cancel-btn {
  background: #ffffff;
  color: #1d1d1f;
  border: 2px solid #e5e5ea;
}

.cancel-btn:hover {
  background: #f5f5f7;
}

.save-btn {
  background: #007aff;
  color: white;
}

.save-btn:hover {
  background: #0051d5;
  transform: translateY(-2px);
}

.delete-btn {
  background: #ff3b30;
  color: white;
}

.delete-btn:hover {
  background: #d32f2f;
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .modal-footer {
    flex-direction: column;
    gap: 12px;
  }

  .action-group {
    width: 100%;
    flex-direction: column;
  }

  .cancel-btn,
  .save-btn,
  .delete-btn {
    width: 100%;
  }
}
</style>
