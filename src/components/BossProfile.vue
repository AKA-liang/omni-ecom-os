<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <button class="close-btn" @click="$emit('close')">
        <X :size="18" />
      </button>

      <div class="profile-header">
        <div class="avatar-section">
          <div class="avatar-wrapper" @click="showUploadModal = true">
            <img :src="displayAvatar" :alt="localBoss.name" class="avatar" />
            <div class="avatar-overlay">
              <span>更换头像</span>
            </div>
          </div>
        </div>
        
        <div class="header-info">
          <h2 class="boss-name">{{ localBoss.name }}</h2>
          <p class="boss-position">{{ localBoss.position }}</p>
          <p class="boss-department">{{ localBoss.department }}</p>
        </div>

        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-value">{{ localBoss.teamSize }}</span>
            <span class="stat-label">团队成员</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ localBoss.projectsCompleted }}</span>
            <span class="stat-label">完成项目</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ localBoss.efficiency }}</span>
            <span class="stat-label">效率</span>
          </div>
        </div>
      </div>

      <div class="profile-body">
        <div class="info-section">
          <h3 class="section-title">基本信息</h3>
          
          <div class="info-grid">
            <div class="info-item">
              <label class="info-label">姓名</label>
              <input 
                v-if="isEditing"
                v-model="localBoss.name"
                type="text"
                class="info-input"
              />
              <span v-else class="info-value">{{ localBoss.name }}</span>
            </div>

            <div class="info-item">
              <label class="info-label">职位</label>
              <input 
                v-if="isEditing"
                v-model="localBoss.position"
                type="text"
                class="info-input"
              />
              <span v-else class="info-value">{{ localBoss.position }}</span>
            </div>

            <div class="info-item">
              <label class="info-label">部门</label>
              <input 
                v-if="isEditing"
                v-model="localBoss.department"
                type="text"
                class="info-input"
              />
              <span v-else class="info-value">{{ localBoss.department }}</span>
            </div>

            <div class="info-item">
              <label class="info-label">邮箱</label>
              <input 
                v-if="isEditing"
                v-model="localBoss.email"
                type="email"
                class="info-input"
              />
              <span v-else class="info-value">{{ localBoss.email }}</span>
            </div>

            <div class="info-item">
              <label class="info-label">电话</label>
              <input 
                v-if="isEditing"
                v-model="localBoss.phone"
                type="tel"
                class="info-input"
              />
              <span v-else class="info-value">{{ localBoss.phone }}</span>
            </div>

            <div class="info-item">
              <label class="info-label">入职日期</label>
              <span class="info-value">{{ localBoss.joinDate }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h3 class="section-title">个人简介</h3>
          <textarea 
            v-if="isEditing"
            v-model="localBoss.bio"
            class="bio-textarea"
            rows="4"
          ></textarea>
          <p v-else class="bio-text">{{ localBoss.bio }}</p>
        </div>
      </div>

      <div class="profile-footer">
        <button v-if="!isEditing" class="action-btn primary" @click="startEditing">
          <span>编辑信息</span>
        </button>
        <template v-else>
          <button class="action-btn primary" @click="saveChanges">
            <span>保存修改</span>
          </button>
          <button class="action-btn secondary" @click="cancelEditing">
            <span>取消</span>
          </button>
        </template>
      </div>

      <div v-if="showUploadModal" class="upload-modal-overlay" @click.self="showUploadModal = false">
        <div class="upload-modal">
          <div class="modal-header">
            <h3>更换头像</h3>
            <button class="close-modal-btn" @click="showUploadModal = false">
              <X :size="18" />
            </button>
          </div>
          <div class="modal-body">
            <ImageUpload 
              :currentAvatar="localBoss.avatar"
              @upload-success="handleUploadSuccess"
              @upload-error="handleUploadError"
            />
          </div>
          <div class="modal-footer">
            <button class="action-btn secondary" @click="showUploadModal = false">
              <span>关闭</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '../services/api.js'
import ImageUpload from './ImageUpload.vue'
import { X } from 'lucide-vue-next'

export default {
  name: 'BossProfile',
  components: {
    ImageUpload
  },
  emits: ['close', 'update'],
  setup(props, { emit }) {
    const localBoss = ref({
      name: '张总',
      position: '数字员工管理系统负责人',
      email: 'boss@company.com',
      phone: '138-0013-8000',
      department: 'AI运营中心',
      avatar: '',
      bio: '致力于打造最优秀的AI数字员工团队，推动企业智能化转型',
      joinDate: '2024-01-15',
      teamSize: 13,
      projectsCompleted: 48,
      efficiency: '98.5%'
    })
    const isEditing = ref(false)
    const showUploadModal = ref(false)
    const originalBoss = ref({})
    const uploadError = ref('')

    const displayAvatar = computed(() => {
      if (localBoss.value.avatar && localBoss.value.avatar.startsWith('/api/files/')) {
        return api.getFileUrl(localBoss.value.avatar.replace('/api/files/', ''))
      }
      return localBoss.value.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=boss&backgroundColor=c0aede&radius=50'
    })

    onMounted(async () => {
      try {
        const bossData = await api.getBoss()
        localBoss.value = bossData
        originalBoss.value = { ...bossData }
      } catch (err) {
        console.error('获取Boss信息失败:', err)
      }
    })

    const startEditing = () => {
      originalBoss.value = { ...localBoss.value }
      isEditing.value = true
    }

    const cancelEditing = () => {
      localBoss.value = { ...originalBoss.value }
      isEditing.value = false
    }

    const saveChanges = async () => {
      try {
        const updated = await api.updateBoss(localBoss.value)
        localBoss.value = updated
        originalBoss.value = { ...updated }
        isEditing.value = false
        emit('update', updated)
      } catch (err) {
        console.error('保存失败:', err)
        alert('保存失败: ' + err.message)
      }
    }

    const handleUploadSuccess = (avatarUrl) => {
      localBoss.value.avatar = avatarUrl
      showUploadModal.value = false
      emit('update', localBoss.value)
    }

    const handleUploadError = (error) => {
      uploadError.value = error
    }

    return {
      localBoss,
      isEditing,
      showUploadModal,
      displayAvatar,
      uploadError,
      startEditing,
      cancelEditing,
      saveChanges,
      handleUploadSuccess,
      handleUploadError
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
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-container {
  background: #ffffff;
  border-radius: 24px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  animation: slideUp 0.4s cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.96);
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
  color: #1d1d1f;
  transition: all 0.25s ease;
  z-index: 10;
}

.close-btn :deep(svg) {
  color: #1d1d1f;
}

.close-btn:hover {
  background: #e5e5ea;
  transform: scale(1.1);
}

.profile-header {
  display: flex;
  gap: 32px;
  padding: 48px;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 24px 24px 0 0;
  align-items: flex-start;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
}

.avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.avatar-wrapper:hover .avatar {
  transform: scale(1.05);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

.avatar-overlay {
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
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay span {
  color: white;
  font-size: 14px;
  font-weight: 600;
}

.header-info {
  flex: 1;
  padding-top: 8px;
}

.boss-name {
  font-size: 36px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.boss-position {
  font-size: 18px;
  color: #1d1d1f;
  font-weight: 500;
  margin-bottom: 4px;
}

.boss-department {
  font-size: 15px;
  color: #86868b;
  font-weight: 500;
}

.header-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  background: #ffffff;
  padding: 14px 20px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.04);
  min-width: 120px;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #007aff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #86868b;
  font-weight: 600;
}

.profile-body {
  padding: 40px 48px;
}

.info-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f5f5f7;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 13px;
  font-weight: 600;
  color: #86868b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 15px;
  color: #1d1d1f;
  font-weight: 500;
  padding: 12px;
  background: #f5f5f7;
  border-radius: 8px;
}

.info-input {
  padding: 12px;
  font-size: 15px;
  color: #1d1d1f;
  background: #ffffff;
  border: 2px solid #007aff;
  border-radius: 8px;
  outline: none;
  transition: all 0.25s ease;
}

.info-input:focus {
  border-color: #0051d5;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.bio-textarea {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  color: #1d1d1f;
  background: #ffffff;
  border: 2px solid #007aff;
  border-radius: 8px;
  outline: none;
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
  transition: all 0.25s ease;
}

.bio-textarea:focus {
  border-color: #0051d5;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.bio-text {
  font-size: 15px;
  color: #1d1d1f;
  line-height: 1.7;
  padding: 16px;
  background: #f5f5f7;
  border-radius: 8px;
}

.profile-footer {
  display: flex;
  gap: 12px;
  padding: 32px 48px;
  background: #f8f9fa;
  border-radius: 0 0 24px 24px;
}

.action-btn {
  flex: 1;
  padding: 14px 24px;
  border-radius: 12px;
  font-size: 15px;
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

.upload-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 20px;
}

.upload-modal {
  background: #ffffff;
  border-radius: 20px;
  max-width: 500px;
  width: 100%;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: pickerSlideUp 0.3s ease;
}

@keyframes pickerSlideUp {
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
  border-bottom: 1px solid #f5f5f7;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
}

.close-modal-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f5f5f7;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1d1d1f;
  transition: all 0.25s ease;
}

.close-modal-btn :deep(svg) {
  color: #1d1d1f;
}

.close-modal-btn:hover {
  background: #e5e5ea;
  transform: scale(1.1);
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #f5f5f7;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .modal-container {
    max-height: 95vh;
    border-radius: 20px;
  }

  .profile-header {
    flex-direction: column;
    padding: 32px 24px;
    gap: 24px;
  }

  .avatar {
    width: 120px;
    height: 120px;
  }

  .header-stats {
    flex-direction: row;
    width: 100%;
  }

  .stat-item {
    flex: 1;
    min-width: 0;
  }

  .boss-name {
    font-size: 28px;
  }

  .profile-body {
    padding: 24px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .profile-footer {
    padding: 24px;
    flex-direction: column;
  }
}
</style>
