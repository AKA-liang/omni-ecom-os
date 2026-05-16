<template>
  <div class="image-upload">
    <div class="upload-area" @click="triggerUpload" @dragover.prevent="onDragOver" @dragleave="onDragLeave" @drop.prevent="onDrop">
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        @change="handleFileSelect"
        style="display: none"
      />
      
      <div v-if="!previewUrl" class="upload-placeholder">
        <div class="upload-icon">📷</div>
        <p class="upload-text">点击或拖拽上传图片</p>
        <p class="upload-hint">支持 JPG, PNG, GIF, WebP 格式</p>
      </div>
      
      <div v-else class="preview-container">
        <img :src="previewUrl" alt="预览" class="preview-image" />
        <div class="preview-overlay">
          <button class="change-btn" @click.stop="triggerUpload">
            更换图片
          </button>
          <button class="remove-btn" @click.stop="removeImage">
            删除
          </button>
        </div>
      </div>
    </div>
    
    <div v-if="uploading" class="upload-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <p class="progress-text">上传中... {{ uploadProgress }}%</p>
    </div>
    
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import api from '../services/api.js';

export default {
  name: 'ImageUpload',
  props: {
    currentAvatar: {
      type: String,
      default: ''
    }
  },
  emits: ['upload-success', 'upload-error'],
  setup(props, { emit }) {
    const fileInput = ref(null);
    const previewUrl = ref('');
    const uploading = ref(false);
    const uploadProgress = ref(0);
    const error = ref('');

    const triggerUpload = () => {
      fileInput.value?.click();
    };

    const handleFileSelect = (event) => {
      const file = event.target.files[0];
      if (file) {
        processFile(file);
      }
    };

    const onDragOver = (event) => {
      event.dataTransfer.dropEffect = 'copy';
    };

    const onDragLeave = () => {
    };

    const onDrop = (event) => {
      const file = event.dataTransfer.files[0];
      if (file && file.type.startsWith('image/')) {
        processFile(file);
      } else {
        error.value = '请上传图片文件';
      }
    };

    const processFile = (file) => {
      error.value = '';
      
      if (file.size > 10 * 1024 * 1024) {
        error.value = '文件大小不能超过10MB';
        emit('upload-error', error.value);
        return;
      }
      
      const reader = new FileReader();
      reader.onload = (e) => {
        previewUrl.value = e.target.result;
        uploadFile(file);
      };
      reader.readAsDataURL(file);
    };

    const uploadFile = async (file) => {
      uploading.value = true;
      uploadProgress.value = 0;
      error.value = '';

      try {
        const fakeProgress = setInterval(() => {
          if (uploadProgress.value < 90) {
            uploadProgress.value += 10;
          }
        }, 100);

        const fileUrl = await api.uploadAvatar(file);
        
        clearInterval(fakeProgress);
        uploadProgress.value = 100;

        setTimeout(() => {
          uploading.value = false;
          emit('upload-success', fileUrl);
        }, 500);
      } catch (err) {
        uploading.value = false;
        error.value = err.message || '上传失败';
        emit('upload-error', error.value);
      }
    };

    const removeImage = () => {
      previewUrl.value = '';
      fileInput.value.value = '';
      if (props.currentAvatar) {
        emit('upload-success', '');
      }
    };

    return {
      fileInput,
      previewUrl,
      uploading,
      uploadProgress,
      error,
      triggerUpload,
      handleFileSelect,
      onDragOver,
      onDragLeave,
      onDrop,
      removeImage
    };
  }
};
</script>

<style scoped>
.image-upload {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.upload-area {
  width: 100%;
  height: 300px;
  border: 3px dashed #e5e5ea;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.upload-area:hover {
  border-color: #007aff;
  background: #f5f5f7;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.upload-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.upload-text {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 14px;
  color: #86868b;
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
  gap: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.preview-container:hover .preview-overlay {
  opacity: 1;
}

.change-btn,
.remove-btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.change-btn {
  background: #007aff;
  color: white;
}

.change-btn:hover {
  background: #0051d5;
}

.remove-btn {
  background: #ff3b30;
  color: white;
}

.remove-btn:hover {
  background: #d32f2f;
}

.upload-progress {
  margin-top: 16px;
}

.progress-bar {
  height: 8px;
  background: #e5e5ea;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #007aff, #5856d6);
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 14px;
  color: #86868b;
  margin-top: 8px;
  text-align: center;
}

.error-message {
  margin-top: 12px;
  padding: 12px;
  background: #ffebee;
  color: #c62828;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
}
</style>
