<template>
  <div class="avatar-upload">
    <div class="avatar-wrapper" @click="triggerUpload" @dragover.prevent="onDragOver" @dragleave="onDragLeave" @drop.prevent="onDrop">
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        @change="handleFileSelect"
        style="display: none"
      />

      <div v-if="previewUrl" class="preview-container">
        <img :src="previewUrl" alt="预览" class="preview-image" />
        <div class="preview-overlay">
          <span>更换头像</span>
        </div>
      </div>

      <div v-else class="upload-placeholder">
        <Camera :size="32" class="upload-icon" />
        <span class="upload-text">点击上传头像</span>
      </div>
    </div>

    <div v-if="uploading" class="upload-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <span class="progress-text">上传中... {{ uploadProgress }}%</span>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import api from '../services/api.js'
import { Camera } from 'lucide-vue-next'

export default {
  name: 'EmployeeAvatarUpload',
  components: {
    Camera
  },
  props: {
    currentAvatar: {
      type: String,
      default: ''
    }
  },
  emits: ['upload-success', 'upload-error'],
  setup(props, { emit }) {
    const fileInput = ref(null)
    const previewUrl = ref('')
    const uploading = ref(false)
    const uploadProgress = ref(0)

    const triggerUpload = () => {
      fileInput.value?.click()
    }

    const handleFileSelect = (event) => {
      const file = event.target.files[0]
      if (file) {
        processFile(file)
      }
    }

    const onDragOver = () => {
    }

    const onDragLeave = () => {
    }

    const onDrop = (event) => {
      const file = event.dataTransfer.files[0]
      if (file && file.type.startsWith('image/')) {
        processFile(file)
      } else {
        emit('upload-error', '请上传图片文件')
      }
    }

    const processFile = (file) => {
      if (file.size > 10 * 1024 * 1024) {
        emit('upload-error', '文件大小不能超过10MB')
        return
      }

      const reader = new FileReader()
      reader.onload = (e) => {
        previewUrl.value = e.target.result
      }
      reader.readAsDataURL(file)

      uploadFile(file)
    }

    const uploadFile = async (file) => {
      uploading.value = true
      uploadProgress.value = 0

      const interval = setInterval(() => {
        if (uploadProgress.value < 90) {
          uploadProgress.value += 10
        }
      }, 100)

      try {
        const fileUrl = await api.uploadEmployeeAvatar(file)
        clearInterval(interval)
        uploadProgress.value = 100

        setTimeout(() => {
          uploading.value = false
          emit('upload-success', fileUrl)
        }, 500)
      } catch (err) {
        clearInterval(interval)
        uploading.value = false
        emit('upload-error', err.message)
      }
    }

    return {
      fileInput,
      previewUrl,
      uploading,
      uploadProgress,
      triggerUpload,
      handleFileSelect,
      onDragOver,
      onDragLeave,
      onDrop
    }
  }
}
</script>

<style scoped>
.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-wrapper {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  border: 3px dashed #e5e5ea;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  border-color: #007aff;
}

.preview-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.preview-container:hover .preview-overlay {
  opacity: 1;
}

.preview-overlay span {
  color: white;
  font-size: 14px;
  font-weight: 600;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.upload-icon {
  color: #86868b;
}

.upload-text {
  font-size: 12px;
  color: #86868b;
  font-weight: 500;
}

.upload-progress {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-bar {
  height: 4px;
  background: #e5e5ea;
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #007aff, #5856d6);
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
  color: #86868b;
  text-align: center;
}
</style>
