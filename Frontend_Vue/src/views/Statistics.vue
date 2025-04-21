<template>
  <div class="statistics-container">
    <router-link to="/myurls" class="back-button">
      <span class="arrow">←</span> Back
    </router-link>

    <h1>Short URL Statistics: https://snaplink.txsjyy.net/{{ shortCode }}</h1>

    <div v-if="analyticsData.length">
      <h3>Total Clicks: {{ analyticsData.length }}</h3>
      <h3>Unique Visitors: {{ uniqueVisitors }}</h3>

      <div class="controls">
        <label for="range">Select Range:</label>
        <select id="range" v-model="selectedRange" @change="updateChart">
          <option value="3">Last 7 Days</option>
          <option value="7">Last 15 Days</option>
          <option value="14">Last 29 Days</option>
        </select>
        <button @click="downloadChart">Download Chart</button>
      </div>

      <canvas ref="chartCanvas2" width="600" height="300" style="margin-bottom: 40px;"></canvas>

      <table class="analytics-table">
        <thead>
          <tr>
            <th>Referrer</th>
            <th>Geo Location</th>
            <th>User Agent</th>
            <th>IP Address</th>
            <th>Timestamp</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(entry, index) in analyticsData" :key="index">
            <td>{{ entry.referrer || 'N/A' }}</td>
            <td>{{ entry.geo_location || 'N/A' }}</td>
            <td>{{ entry.userAgent || 'N/A' }}</td>
            <td>{{ entry.ip_address }}</td>
            <td>{{ new Date(entry.timestamp).toLocaleString() }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else>
      <p>Loading or no data found.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue';
import { useRoute } from 'vue-router';
import Chart from 'chart.js/auto';

interface AnalyticsEntry {
  referrer: string;
  geo_location: string;
  userAgent: string;
  ip_address: string;
  timestamp: string;
}

const route = useRoute();
const shortCode = route.params.id as string;

const analyticsData = ref<AnalyticsEntry[]>([]);
const chartCanvas2 = ref<HTMLCanvasElement | null>(null);
let chart2: Chart | null = null;

const selectedRange = ref(3); // -3 ~ +3

const API = 'https://snaplink-dot-rice-comp-539-spring-2022.uk.r.appspot.com';

const formatTimestamp = (timestamp: string) => {
  return new Date(timestamp).toLocaleString();
};

const uniqueVisitors = computed(() => {
  const uniqueIps = new Set(analyticsData.value.map(entry => entry.ip_address));
  return uniqueIps.size;
});

const fetchAnalytics = async () => {
  try {
    const response = await fetch(`${API}/analytics/details`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('accessToken') || ''}`
      },
      body: JSON.stringify({ shortCode })
    });

    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const data = await response.json();
    analyticsData.value = data;
  } catch (error) {
    console.error('Failed to load analytics data:', error);
  }
};

const renderClickTimelineChart = (data: AnalyticsEntry[]) => {
  if (!chartCanvas2.value || !data.length) return;
  if (chart2) chart2.destroy();

  const dateCounts: Record<string, number> = {};
  const dateList = data.map(entry => {
    const dateStr = new Date(entry.timestamp).toISOString().split('T')[0];
    dateCounts[dateStr] = (dateCounts[dateStr] || 0) + 1;
    return dateStr;
  });

  const centralDateStr = Object.entries(dateCounts).sort((a, b) => b[1] - a[1])[0][0];
  const centralDate = new Date(centralDateStr);

  const labels: string[] = [];
  const clicksPerDay: Record<string, number> = {};

  for (let i = -selectedRange.value; i <= selectedRange.value; i++) {
    const tempDate = new Date(centralDate);
    tempDate.setDate(tempDate.getDate() + i);
    const label = tempDate.toISOString().split('T')[0];
    labels.push(label);
    clicksPerDay[label] = 0;
  }

  data.forEach(entry => {
    const day = new Date(entry.timestamp).toISOString().split('T')[0];
    if (clicksPerDay[day] !== undefined) {
      clicksPerDay[day]++;
    }
  });

  chart2 = new Chart(chartCanvas2.value, {
    type: 'line',
    data: {
      labels,
      datasets: [
        {
          label: 'Clicks per Day',
          data: labels.map(label => clicksPerDay[label]),
          borderColor: '#2196f3',
          backgroundColor: 'rgba(33, 150, 243, 0.2)',
          fill: true,
        }
      ]
    },
    options: {
      responsive: true,
      scales: {
        y: { beginAtZero: true, ticks: { stepSize: 1 } }
      }
    }
  });
};

const updateChart = () => {
  if (analyticsData.value.length > 0) {
    renderClickTimelineChart(analyticsData.value);
  }
};

const downloadChart = () => {
  if (chartCanvas2.value) {
    const link = document.createElement('a');
    link.href = chartCanvas2.value.toDataURL('image/png');
    link.download = `shorturl-statistics-${shortCode}.png`;
    link.click();
  }
};

onMounted(async () => {
  await fetchAnalytics();
  await nextTick();

  if (chartCanvas2.value && analyticsData.value.length > 0) {
    renderClickTimelineChart(analyticsData.value);
  } else {
    console.warn("Chart draw skipped: canvas or data not ready.");
  }
});
</script>

<style scoped>
.statistics-container {
  max-width: 960px;
  margin: 40px auto;
  padding: 0 20px;
}

.back-button {
  display: inline-block;
  margin-bottom: 20px;
  color: #1d72b8;
  text-decoration: none;
}

.controls {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.analytics-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 40px;
}

.analytics-table th,
.analytics-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

.analytics-table th {
  background-color: #f4f4f4;
}
</style>
