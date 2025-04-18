<template>
  <div class="statistics-container">
    <h1 class="title">URL Statistics</h1>
    
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading">
      Loading statistics...
    </div>
    
    <!-- 错误提示 -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
    
    <!-- 统计数据展示 -->
    <div v-if="!isLoading && !error && analyticsData.length > 0" class="stats-grid">
      <div class="stat-card">
        <h3>Total Clicks</h3>
        <p class="stat-value">{{ totalClicks }}</p>
      </div>
      
      <div class="stat-card">
        <h3>Unique Visitors</h3>
        <p class="stat-value">{{ uniqueVisitors }}</p>
      </div>
      
      <div class="stat-card">
        <h3>Average Clicks per Day</h3>
        <p class="stat-value">{{ averageClicksPerDay }}</p>
      </div>
    </div>
    
    <!-- 点击详情表格 -->
    <div v-if="!isLoading && !error && analyticsData.length > 0" class="table-container">
      <h2>Click Details</h2>
      <table class="table">
        <thead>
          <tr>
            <th>Time</th>
            <th>IP Address</th>
            <th>Location</th>
            <th>Referrer</th>
            <th>Browser</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(click, index) in analyticsData" :key="index">
            <td>{{ formatDate(click.timestamp) }}</td>
            <td>{{ click.ip_address }}</td>
            <td>{{ click.geo_location }}</td>
            <td>{{ click.referrer || 'Direct' }}</td>
            <td>{{ click.userAgent }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 无数据提示 -->
    <div v-if="!isLoading && !error && analyticsData.length === 0" class="no-data">
      No click data available for this URL.
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '../stores/user';

const route = useRoute();
const userStore = useUserStore();
const urlId = route.params.id as string;

// 状态
const analyticsData = ref<any[]>([]);
const isLoading = ref(false);
const error = ref('');

// 计算属性
const totalClicks = computed(() => analyticsData.value.length);
const uniqueVisitors = computed(() => {
  const uniqueIPs = new Set(analyticsData.value.map(click => click.ip_address));
  return uniqueIPs.size;
});
const averageClicksPerDay = computed(() => {
  if (analyticsData.value.length === 0) return 0;
  
  // 计算日期范围
  const dates = analyticsData.value.map(click => new Date(click.timestamp));
  const minDate = new Date(Math.min(...dates.map(d => d.getTime())));
  const maxDate = new Date(Math.max(...dates.map(d => d.getTime())));
  
  // 计算天数差
  const daysDiff = Math.ceil((maxDate.getTime() - minDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
  
  return (analyticsData.value.length / daysDiff).toFixed(1);
});

// 获取统计数据
const fetchAnalytics = async () => {
  if (!userStore.isLoggedIn) {
    error.value = 'Please login to view statistics';
    return;
  }
  
  isLoading.value = true;
  error.value = '';
  
  try {
    const API = import.meta.env.VITE_API_BASE_URL;
    const response = await fetch(`${API}/analytics/details`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken')}`
      },
      body: JSON.stringify({
        urlId: urlId
      })
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch analytics data');
    }
    
    const data = await response.json();
    analyticsData.value = data;
  } catch (err) {
    console.error('Error fetching analytics:', err);
    error.value = 'Failed to load statistics. Please try again later.';
  } finally {
    isLoading.value = false;
  }
};

// 格式化日期
const formatDate = (timestamp: string) => {
  const date = new Date(timestamp);
  return date.toLocaleString();
};

// 组件挂载时获取数据
onMounted(() => {
  fetchAnalytics();
});
</script>

<style scoped>
.statistics-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.title {
  font-size: 2rem;
  color: #1a365d;
  margin-bottom: 2rem;
  text-align: center;
}

.loading, .no-data, .error-message {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
  color: #4b5563;
}

.error-message {
  color: #dc2626;
  background-color: #fee2e2;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background-color: white;
  border-radius: 0.5rem;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-card h3 {
  color: #4b5563;
  font-size: 1rem;
  margin-bottom: 0.5rem;
}

.stat-value {
  color: #1d72b8;
  font-size: 2rem;
  font-weight: bold;
}

.table-container {
  background-color: white;
  border-radius: 0.5rem;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.table-container h2 {
  color: #1a365d;
  margin-bottom: 1rem;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th, .table td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.table th {
  background-color: #f9fafb;
  font-weight: 500;
  color: #4b5563;
}

.table tr:hover {
  background-color: #f9fafb;
}
</style>
