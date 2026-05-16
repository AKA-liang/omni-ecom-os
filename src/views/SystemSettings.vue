<template>
  <div class="system-settings">
    <div class="page-header">
      <h1>系统设置</h1>
    </div>

    <div class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 站点配置 -->
    <div v-if="activeTab === 'site'" class="tab-content">
      <div v-if="siteLoading" class="loading">加载中...</div>
      <div v-else class="form-card">
        <div class="form-group">
          <label>站点名称</label>
          <input v-model="siteForm.siteName" placeholder="请输入站点名称" />
        </div>
        <div class="form-group">
          <label>副标题</label>
          <input v-model="siteForm.siteSubtitle" placeholder="请输入副标题" />
        </div>
        <div class="form-group">
          <label>Logo URL</label>
          <input v-model="siteForm.logoUrl" placeholder="请输入Logo链接" />
        </div>
        <button class="btn btn-primary" @click="saveSiteConfig" :disabled="siteSaving">
          {{ siteSaving ? '保存中...' : '保存配置' }}
        </button>
        <p v-if="siteMsg" :class="['msg', siteMsgType]">{{ siteMsg }}</p>
      </div>
    </div>

    <!-- 管理员信息 -->
    <div v-if="activeTab === 'boss'" class="tab-content">
      <div v-if="bossLoading" class="loading">加载中...</div>
      <div v-else class="form-card">
        <div class="form-header">
          <h2>管理员信息</h2>
          <button
            v-if="!bossEditing"
            class="btn btn-outline"
            @click="startEditBoss"
          >
            <Pencil :size="16" /> 编辑
          </button>
        </div>
        <div class="boss-grid">
          <div class="form-group">
            <label>姓名</label>
            <input v-model="bossForm.name" :disabled="!bossEditing" placeholder="请输入姓名" />
          </div>
          <div class="form-group">
            <label>职位</label>
            <input v-model="bossForm.position" :disabled="!bossEditing" placeholder="请输入职位" />
          </div>
          <div class="form-group">
            <label>部门</label>
            <input v-model="bossForm.department" :disabled="!bossEditing" placeholder="请输入部门" />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="bossForm.email" :disabled="!bossEditing" placeholder="请输入邮箱" />
          </div>
          <div class="form-group">
            <label>电话</label>
            <input v-model="bossForm.phone" :disabled="!bossEditing" placeholder="请输入电话" />
          </div>
          <div class="form-group full-width">
            <label>个人简介</label>
            <textarea v-model="bossForm.bio" :disabled="!bossEditing" placeholder="请输入个人简介" rows="3"></textarea>
          </div>
        </div>
        <div v-if="bossEditing" class="form-actions">
          <button class="btn btn-primary" @click="saveBoss" :disabled="bossSaving">
            {{ bossSaving ? '保存中...' : '保存' }}
          </button>
          <button class="btn btn-outline" @click="cancelEditBoss">取消</button>
        </div>
        <p v-if="bossMsg" :class="['msg', bossMsgType]">{{ bossMsg }}</p>
      </div>
    </div>

    <!-- 分类管理 -->
    <div v-if="activeTab === 'category'" class="tab-content">
      <div v-if="catLoading" class="loading">加载中...</div>
      <div v-else>
        <div class="form-header">
          <h2>分类列表</h2>
          <button class="btn btn-primary" @click="startAddCategory">
            <Plus :size="16" /> 添加分类
          </button>
        </div>

        <div v-if="catEditing" class="form-card cat-form">
          <div class="form-header">
            <h3>{{ catForm.id ? '编辑分类' : '添加分类' }}</h3>
            <button class="btn-icon" @click="cancelCatEdit"><X :size="18" /></button>
          </div>
          <div class="cat-form-grid">
            <div class="form-group">
              <label>名称</label>
              <input v-model="catForm.name" placeholder="分类名称" />
            </div>
            <div class="form-group">
              <label>图标 (Emoji)</label>
              <input v-model="catForm.icon" placeholder="🎨" class="emoji-input" />
            </div>
            <div class="form-group full-width">
              <label>描述</label>
              <input v-model="catForm.description" placeholder="分类描述" />
            </div>
          </div>
          <button class="btn btn-primary" @click="saveCategory" :disabled="catSaving">
            {{ catSaving ? '保存中...' : '保存' }}
          </button>
        </div>

        <table class="cat-table" v-if="categories.length">
          <thead>
            <tr>
              <th>图标</th>
              <th>名称</th>
              <th>描述</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cat in categories" :key="cat.id">
              <td class="icon-cell">{{ cat.icon }}</td>
              <td>{{ cat.name }}</td>
              <td>{{ cat.description }}</td>
              <td class="action-cell">
                <button class="btn-icon" @click="startEditCategory(cat)"><Pencil :size="15" /></button>
                <button class="btn-icon btn-danger" @click="deleteCategory(cat)"><Trash2 :size="15" /></button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else class="empty">暂无分类</div>

        <p v-if="catMsg" :class="['msg', catMsgType]">{{ catMsg }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api.js'
import { X, Plus, Pencil, Trash2 } from 'lucide-vue-next'

const tabs = [
  { key: 'site', label: '站点配置' },
  { key: 'boss', label: '管理员信息' },
  { key: 'category', label: '分类管理' }
]

const activeTab = ref('site')

// --- 站点配置 ---
const siteForm = ref({ siteName: '', siteSubtitle: '', logoUrl: '' })
const siteLoading = ref(false)
const siteSaving = ref(false)
const siteMsg = ref('')
const siteMsgType = ref('success')

async function loadSiteConfig() {
  siteLoading.value = true
  try {
    const data = await api.getSiteConfig()
    siteForm.value = {
      siteName: data.siteName || '',
      siteSubtitle: data.siteSubtitle || '',
      logoUrl: data.logoUrl || ''
    }
  } catch {
    siteMsg.value = '加载站点配置失败'
    siteMsgType.value = 'error'
  } finally {
    siteLoading.value = false
  }
}

async function saveSiteConfig() {
  siteSaving.value = true
  siteMsg.value = ''
  try {
    await api.updateSiteConfig(siteForm.value)
    siteMsg.value = '站点配置已保存'
    siteMsgType.value = 'success'
  } catch {
    siteMsg.value = '保存失败'
    siteMsgType.value = 'error'
  } finally {
    siteSaving.value = false
  }
}

// --- 管理员信息 ---
const bossForm = ref({ name: '', position: '', department: '', email: '', phone: '', bio: '' })
const bossOriginal = ref({})
const bossLoading = ref(false)
const bossSaving = ref(false)
const bossEditing = ref(false)
const bossMsg = ref('')
const bossMsgType = ref('success')

async function loadBoss() {
  bossLoading.value = true
  try {
    const data = await api.getBoss()
    bossForm.value = {
      name: data.name || '',
      position: data.position || '',
      department: data.department || '',
      email: data.email || '',
      phone: data.phone || '',
      bio: data.bio || ''
    }
  } catch {
    bossMsg.value = '加载管理员信息失败'
    bossMsgType.value = 'error'
  } finally {
    bossLoading.value = false
  }
}

function startEditBoss() {
  bossOriginal.value = { ...bossForm.value }
  bossEditing.value = true
}

function cancelEditBoss() {
  bossForm.value = { ...bossOriginal.value }
  bossEditing.value = false
}

async function saveBoss() {
  bossSaving.value = true
  bossMsg.value = ''
  try {
    await api.updateBoss(bossForm.value)
    bossEditing.value = false
    bossMsg.value = '管理员信息已保存'
    bossMsgType.value = 'success'
  } catch {
    bossMsg.value = '保存失败'
    bossMsgType.value = 'error'
  } finally {
    bossSaving.value = false
  }
}

// --- 分类管理 ---
const categories = ref([])
const catLoading = ref(false)
const catSaving = ref(false)
const catEditing = ref(false)
const catForm = ref({ id: null, name: '', icon: '', description: '' })
const catMsg = ref('')
const catMsgType = ref('success')

async function loadCategories() {
  catLoading.value = true
  try {
    categories.value = await api.getCategories()
  } catch {
    catMsg.value = '加载分类失败'
    catMsgType.value = 'error'
  } finally {
    catLoading.value = false
  }
}

function startAddCategory() {
  catForm.value = { id: null, name: '', icon: '', description: '' }
  catEditing.value = true
}

function startEditCategory(cat) {
  catForm.value = { id: cat.id, name: cat.name, icon: cat.icon, description: cat.description }
  catEditing.value = true
}

function cancelCatEdit() {
  catEditing.value = false
}

async function saveCategory() {
  catSaving.value = true
  catMsg.value = ''
  try {
    if (catForm.value.id) {
      await api.updateCategory(catForm.value.id, catForm.value)
    } else {
      await api.createCategory(catForm.value)
    }
    catEditing.value = false
    catMsg.value = catForm.value.id ? '分类已更新' : '分类已添加'
    catMsgType.value = 'success'
    await loadCategories()
  } catch {
    catMsg.value = '保存失败'
    catMsgType.value = 'error'
  } finally {
    catSaving.value = false
  }
}

async function deleteCategory(cat) {
  if (!confirm(`确定要删除分类「${cat.name}」吗？`)) return
  catMsg.value = ''
  try {
    await api.deleteCategory(cat.id)
    catMsg.value = '分类已删除'
    catMsgType.value = 'success'
    await loadCategories()
  } catch {
    catMsg.value = '删除失败'
    catMsgType.value = 'error'
  }
}

onMounted(() => {
  loadSiteConfig()
  loadBoss()
  loadCategories()
})
</script>

<style scoped>
.system-settings {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 24px;
}

.page-header h1 {
  font-size: 1.75rem;
  font-weight: 700;
  background: linear-gradient(135deg, #7c3aed, #3b82f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 24px;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 28px;
}

.tab-btn {
  padding: 8px 22px;
  border-radius: 999px;
  border: 2px solid transparent;
  background: #f3f0ff;
  color: #6d28d9;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s;
}

.tab-btn:hover {
  background: #ede9fe;
}

.tab-btn.active {
  background: linear-gradient(135deg, #7c3aed, #3b82f6);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4px 14px rgba(124, 58, 237, 0.3);
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-card {
  background: #fff;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.form-header h2 {
  font-size: 1.15rem;
  font-weight: 700;
  color: #1e1b4b;
  margin: 0;
}

.form-header h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #1e1b4b;
  margin: 0;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  color: #4c1d95;
  margin-bottom: 6px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1.5px solid #ddd6fe;
  border-radius: 10px;
  font-size: 0.9rem;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: #faf7ff;
  box-sizing: border-box;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.12);
}

.form-group input:disabled,
.form-group textarea:disabled {
  background: #f5f3ff;
  color: #6b7280;
  cursor: not-allowed;
}

.boss-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 20px;
}

.boss-grid .full-width {
  grid-column: 1 / -1;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 4px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 20px;
  border: none;
  border-radius: 10px;
  font-size: 0.88rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: linear-gradient(135deg, #7c3aed, #3b82f6);
  color: #fff;
  box-shadow: 0 3px 12px rgba(124, 58, 237, 0.25);
}

.btn-primary:hover:not(:disabled) {
  box-shadow: 0 5px 18px rgba(124, 58, 237, 0.35);
  transform: translateY(-1px);
}

.btn-outline {
  background: transparent;
  color: #7c3aed;
  border: 1.5px solid #c4b5fd;
}

.btn-outline:hover:not(:disabled) {
  background: #ede9fe;
}

.btn-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  background: #f3f0ff;
  color: #7c3aed;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-icon:hover {
  background: #ede9fe;
}

.btn-icon.btn-danger {
  color: #ef4444;
}

.btn-icon.btn-danger:hover {
  background: #fee2e2;
}

.cat-form {
  margin-bottom: 24px;
  border: 2px solid #ddd6fe;
}

.cat-form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 20px;
}

.cat-form-grid .full-width {
  grid-column: 1 / -1;
}

.emoji-input {
  font-size: 1.2rem !important;
  text-align: center;
}

.cat-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
}

.cat-table thead {
  background: linear-gradient(135deg, #7c3aed, #3b82f6);
}

.cat-table th {
  padding: 12px 18px;
  text-align: left;
  font-size: 0.82rem;
  font-weight: 700;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.cat-table td {
  padding: 12px 18px;
  font-size: 0.9rem;
  color: #374151;
  border-bottom: 1px solid #f3f0ff;
}

.cat-table tbody tr:last-child td {
  border-bottom: none;
}

.cat-table tbody tr:hover {
  background: #faf7ff;
}

.icon-cell {
  font-size: 1.3rem;
  text-align: center;
  width: 56px;
}

.action-cell {
  display: flex;
  gap: 6px;
  width: 80px;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #9ca3af;
  font-size: 0.95rem;
}

.msg {
  margin-top: 14px;
  font-size: 0.85rem;
  font-weight: 600;
}

.msg.success {
  color: #059669;
}

.msg.error {
  color: #ef4444;
}

.loading {
  text-align: center;
  padding: 48px;
  color: #7c3aed;
  font-size: 0.95rem;
  font-weight: 600;
}
</style>
