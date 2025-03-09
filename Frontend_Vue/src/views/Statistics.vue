<template>
  <div class="statistics-container">
    <router-link to="/myurls" class="back-button">
      <span class="arrow">←</span>Back
    </router-link>
    <h1>Short URL Statistics: {{ shortUrl }}</h1>
    <canvas ref="chartCanvas"></canvas>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import Chart from 'chart.js/auto';

const route = useRoute();
const id = route.params.id; // 从路由中获取短链接 id
const shortUrl = ref(`https://sho.rt/${id}`);

// 生成假数据：展示一周内每天的点击量，标签使用英文
const labels = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
const fakeClicks = labels.map(() => Math.floor(Math.random() * 100)); // 随机生成点击量数据

const chartCanvas = ref<HTMLCanvasElement | null>(null);

onMounted(() => {
  if (chartCanvas.value) {
    new Chart(chartCanvas.value, {
      type: 'line', // 可改为 'bar' 显示柱状图
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Clicks',
            data: fakeClicks,
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1,
            fill: true,
          },
        ],
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
          },
        },
      },
    });
  }
});
</script>

<style scoped>
.statistics-container {
  max-width: 800px;
  margin: 2rem auto;
  text-align: center;
  padding: 0 1rem;
  position: relative;
}

/* 返回按钮样式：固定定位于整个页面左侧 */
.back-button {
  position: fixed;
  top: 10%;
  left: 0;
  margin-left: 1rem;
  z-index: 5000; /* 确保按钮在所有元素上层显示 */
  display: inline-flex;
  align-items: center;
  background-color: #f0f0f0; /* 浅色背景 */
  color: #333;
  padding: 8px 12px;
  border-radius: 4px;
  text-decoration: none;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.back-button:hover {
  background-color: #e0e0e0;
}

.arrow {
  margin-right: 8px;
}

canvas {
  max-width: 100%;
  height: auto;
}
</style>
