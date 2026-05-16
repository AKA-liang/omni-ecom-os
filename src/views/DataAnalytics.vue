<template>
  <div class="analytics-page">
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon purple">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ employees.length }}</span>
          <span class="stat-label">智能体总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ onlineRate }}%</span>
          <span class="stat-label">在线率</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon blue">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20V10"/><path d="M18 20V4"/><path d="M6 20v-4"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ avgAccuracy }}%</span>
          <span class="stat-label">平均准确率</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ totalTasks }}</span>
          <span class="stat-label">总完成任务数</span>
        </div>
      </div>
    </div>

    <div class="charts-grid">
      <div class="chart-card">
        <h3 class="chart-title">分类分布饼图</h3>
        <div ref="categoryChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h3 class="chart-title">状态分布饼图</h3>
        <div ref="statusChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h3 class="chart-title">任务完成数排行</h3>
        <div ref="tasksChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h3 class="chart-title">准确率对比</h3>
        <div ref="accuracyChartRef" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import api from '../services/api.js'
import { statusMap } from '../data/employees.js'

export default {
  name: 'DataAnalytics',
  setup() {
    const employees = ref([])
    const categories = ref([])

    const categoryChartRef = ref(null)
    const statusChartRef = ref(null)
    const tasksChartRef = ref(null)
    const accuracyChartRef = ref(null)

    let categoryChart = null
    let statusChart = null
    let tasksChart = null
    let accuracyChart = null

    const onlineRate = computed(() => {
      if (employees.value.length === 0) return '0.0'
      const online = employees.value.filter(e => e.status === 'working' || e.status === 'online').length
      return ((online / employees.value.length) * 100).toFixed(1)
    })

    const avgAccuracy = computed(() => {
      if (employees.value.length === 0) return '0.0'
      const total = employees.value.reduce((sum, e) => {
        return sum + parseFloat(e.accuracy) || 0
      }, 0)
      return (total / employees.value.length).toFixed(1)
    })

    const totalTasks = computed(() => {
      return employees.value.reduce((sum, e) => sum + (e.tasksCompleted || 0), 0)
    })

    const categoryNameMap = {
      ecommerce: '电商',
      programming: '编程',
      training: '培训'
    }

    const categoryColorMap = {
      ecommerce: '#667eea',
      programming: '#764ba2',
      training: '#f093fb'
    }

    function initCategoryChart() {
      if (!categoryChartRef.value) return
      categoryChart = echarts.init(categoryChartRef.value)

      const countMap = {}
      employees.value.forEach(e => {
        countMap[e.category] = (countMap[e.category] || 0) + 1
      })

      const data = Object.entries(countMap).map(([key, value]) => ({
        name: categoryNameMap[key] || key,
        value,
        itemStyle: { color: categoryColorMap[key] || '#999' }
      }))

      categoryChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          bottom: 10,
          textStyle: { color: '#666' }
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: true,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}\n{c}人',
            fontSize: 13
          },
          emphasis: {
            label: { show: true, fontSize: 16, fontWeight: 'bold' }
          },
          data
        }]
      })
    }

    function initStatusChart() {
      if (!statusChartRef.value) return
      statusChart = echarts.init(statusChartRef.value)

      const countMap = {}
      employees.value.forEach(e => {
        countMap[e.status] = (countMap[e.status] || 0) + 1
      })

      const data = Object.entries(countMap).map(([key, value]) => ({
        name: statusMap[key]?.label || key,
        value,
        itemStyle: { color: statusMap[key]?.color || '#999' }
      }))

      statusChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          bottom: 10,
          textStyle: { color: '#666' }
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: true,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}\n{c}人',
            fontSize: 13
          },
          emphasis: {
            label: { show: true, fontSize: 16, fontWeight: 'bold' }
          },
          data
        }]
      })
    }

    function initTasksChart() {
      if (!tasksChartRef.value) return
      tasksChart = echarts.init(tasksChartRef.value)

      const sorted = [...employees.value]
        .sort((a, b) => b.tasksCompleted - a.tasksCompleted)
        .slice(0, 10)
        .reverse()

      tasksChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: 100,
          right: 40,
          top: 10,
          bottom: 20
        },
        xAxis: {
          type: 'value',
          axisLabel: { color: '#999' },
          splitLine: { lineStyle: { type: 'dashed', color: '#eee' } }
        },
        yAxis: {
          type: 'category',
          data: sorted.map(e => e.name),
          axisLabel: { color: '#333', fontSize: 13 },
          axisLine: { show: false },
          axisTick: { show: false }
        },
        series: [{
          type: 'bar',
          data: sorted.map(e => e.tasksCompleted),
          barWidth: 20,
          itemStyle: {
            borderRadius: [0, 6, 6, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ])
          },
          label: {
            show: true,
            position: 'right',
            color: '#666',
            fontSize: 12
          }
        }]
      })
    }

    function initAccuracyChart() {
      if (!accuracyChartRef.value) return
      accuracyChart = echarts.init(accuracyChartRef.value)

      const sorted = [...employees.value]
        .sort((a, b) => parseFloat(a.accuracy) - parseFloat(b.accuracy))

      accuracyChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: (params) => {
            const p = params[0]
            return `${p.name}<br/>准确率: ${p.value}%`
          }
        },
        grid: {
          left: 100,
          right: 60,
          top: 10,
          bottom: 20
        },
        xAxis: {
          type: 'value',
          min: 90,
          max: 100,
          axisLabel: { color: '#999', formatter: '{value}%' },
          splitLine: { lineStyle: { type: 'dashed', color: '#eee' } }
        },
        yAxis: {
          type: 'category',
          data: sorted.map(e => e.name),
          axisLabel: { color: '#333', fontSize: 13 },
          axisLine: { show: false },
          axisTick: { show: false }
        },
        series: [{
          type: 'bar',
          data: sorted.map(e => parseFloat(e.accuracy)),
          barWidth: 20,
          itemStyle: {
            borderRadius: [0, 6, 6, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#43e97b' },
              { offset: 1, color: '#38f9d7' }
            ])
          },
          label: {
            show: true,
            position: 'right',
            color: '#666',
            fontSize: 12,
            formatter: '{c}%'
          }
        }]
      })
    }

    function handleResize() {
      categoryChart?.resize()
      statusChart?.resize()
      tasksChart?.resize()
      accuracyChart?.resize()
    }

    onMounted(async () => {
      try {
        const [emps, cats] = await Promise.all([
          api.getEmployees(),
          api.getCategories?.() || Promise.resolve([])
        ])
        employees.value = emps
        categories.value = cats
      } catch (err) {
        console.error('加载数据失败:', err)
        return
      }

      initCategoryChart()
      initStatusChart()
      initTasksChart()
      initAccuracyChart()

      window.addEventListener('resize', handleResize)
    })

    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      categoryChart?.dispose()
      statusChart?.dispose()
      tasksChart?.dispose()
      accuracyChart?.dispose()
      categoryChart = null
      statusChart = null
      tasksChart = null
      accuracyChart = null
    })

    return {
      employees,
      categories,
      onlineRate,
      avgAccuracy,
      totalTasks,
      categoryChartRef,
      statusChartRef,
      tasksChartRef,
      accuracyChartRef
    }
  }
}
</script>

<style scoped>
.analytics-page {
  max-width: 1600px;
  margin: 0 auto;
  padding: 32px 40px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon.purple {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.stat-icon.green {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  color: #fff;
}

.stat-icon.blue {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  color: #fff;
}

.stat-icon.orange {
  background: linear-gradient(135deg, #fa709a, #fee140);
  color: #fff;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #86868b;
  font-weight: 500;
  margin-top: 4px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.chart-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.chart-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

.chart-container {
  width: 100%;
  min-height: 350px;
}

@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .analytics-page {
    padding: 20px 16px;
  }

  .stats-row {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }

  .stat-card {
    padding: 16px;
    gap: 12px;
  }

  .stat-icon {
    width: 44px;
    height: 44px;
    border-radius: 10px;
  }

  .stat-icon svg {
    width: 20px;
    height: 20px;
  }

  .stat-value {
    font-size: 22px;
  }

  .charts-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .chart-container {
    min-height: 300px;
  }
}
</style>
