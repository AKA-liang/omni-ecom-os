<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <div class="modal-header">
        <h2>站点配置</h2>
        <button class="close-btn" @click="$emit('close')">
          <X :size="18" />
        </button>
      </div>

      <div class="modal-body">
        <div class="form-item">
          <label>站点名称</label>
          <input v-model="formData.siteName" type="text" placeholder="智能数字员工" />
        </div>

        <div class="form-item">
          <label>副标题</label>
          <input v-model="formData.siteSubtitle" type="text" placeholder="Digital Employee Management" />
        </div>
      </div>

      <div class="modal-footer">
        <button class="cancel-btn" @click="$emit('close')">取消</button>
        <button class="save-btn" @click="handleSave">保存配置</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import api from '../services/api.js'
import { X } from 'lucide-vue-next'

export default {
  name: 'SiteConfigForm',
  emits: ['close', 'update'],
  setup(props, { emit }) {
    const formData = ref({
      siteName: '',
      siteSubtitle: ''
    })

    onMounted(async () => {
      try {
        const config = await api.getSiteConfig()
        formData.value = {
          siteName: config.siteName || '',
          siteSubtitle: config.siteSubtitle || ''
        }
      } catch (err) {
        console.error('获取站点配置失败:', err)
      }
    })

    const handleSave = async () => {
      try {
        await api.updateSiteConfig(formData.value)
        emit('update', formData.value)
        emit('close')
        alert('配置已保存')
      } catch (err) {
        console.error('保存配置失败:', err)
        alert('保存失败: ' + err.message)
      }
    }

    return {
      formData,
      handleSave
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
}

.modal-container {
  background: #ffffff;
  border-radius: 20px;
  max-width: 500px;
  width: 100%;
  overflow: hidden;
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
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1d1d1f;
}

.close-btn :deep(svg) {
  color: #1d1d1f;
}

.close-btn:hover {
  background: #e5e5ea;
}

.modal-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 13px;
  font-weight: 600;
  color: #86868b;
  text-transform: uppercase;
}

.form-item input {
  padding: 12px;
  font-size: 15px;
  color: #1d1d1f;
  background: #ffffff;
  border: 2px solid #e5e5ea;
  border-radius: 8px;
  outline: none;
}

.form-item input:focus {
  border-color: #007aff;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e5ea;
  background: #f8f9fa;
}

.cancel-btn,
.save-btn {
  flex: 1;
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
}
</style>
