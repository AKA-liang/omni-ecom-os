<template>
  <div class="category-filter">
    <button 
      class="filter-btn"
      :class="{ active: selectedCategory === 'all' }"
      @click="$emit('select', 'all')"
    >
      <span class="filter-text">全部员工</span>
    </button>
    <button 
      v-for="category in categories" 
      :key="category.id"
      class="filter-btn"
      :class="{ active: selectedCategory === category.id }"
      @click="$emit('select', category.id)"
    >
      <span class="filter-icon">{{ category.icon }}</span>
      <span class="filter-text">{{ category.name }}</span>
    </button>
  </div>
</template>

<script>
export default {
  name: 'CategoryFilter',
  props: {
    categories: {
      type: Array,
      required: true
    },
    selectedCategory: {
      type: String,
      default: 'all'
    }
  },
  emits: ['select']
}
</script>

<style scoped>
.category-filter {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding: 24px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f5f5f7;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 980px;
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.filter-btn:hover {
  background: #e5e5ea;
  border-color: rgba(0, 0, 0, 0.1);
}

.filter-btn.active {
  background: #007aff;
  color: white;
  border-color: #007aff;
}

.filter-icon {
  font-size: 16px;
}

.filter-text {
  font-weight: 500;
}

@media (max-width: 768px) {
  .category-filter {
    gap: 8px;
  }

  .filter-btn {
    padding: 8px 16px;
    font-size: 13px;
  }

  .filter-icon {
    font-size: 14px;
  }
}
</style>
