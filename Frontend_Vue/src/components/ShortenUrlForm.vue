<!-- src/components/ShortenUrlForm.vue -->
<template>
  <div class="container">
    <div class="content-box">
      <!-- 输入表单 -->
      <template v-if="!showResult">
        <h2>Creat your short URLs</h2>
        <p class="description">SnapLink is a free tool to shorten URLs. Enter the URL to find out how many clicks it has received so far.</p>

        <input
          v-model="longUrl"
          placeholder="Enter long link here"
          class="input-box"
        />
        <input
          v-model="customAlias"
          placeholder="Enter alias"
          class="input-box"
        />

        <button @click="shortenUrl" class="shorten-button">
          Shorten URL
        </button>
      </template>

      <!-- 结果展示 -->
      <div v-else class="result">
        <h2>Creat your short URLs</h2>
        <p class="description">SnapLink is a free tool to shorten URLs. Enter the URL to find out how many clicks it has received so far.</p>

        <div class="url-group">
          <div class="url-item">
            <p class="url-label">Original URL</p>
            <a :href="originalUrl" target="_blank" class="original-url">{{ originalUrl }}</a>
          </div>
          <div class="url-item">
            <p class="url-label">Shortened URL</p>
            <a :href="shortenedUrl" target="_blank" class="shortened-url">{{ shortenedUrl }}</a>
          </div>
        </div>

        <div class="action-buttons">
          <button @click="showStatistics" class="stats-button">Show Statistics</button>
          <button @click="resetForm" class="another-button">Shorten another</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const longUrl = ref("");
const customAlias = ref("");
const originalUrl = ref("");
const shortenedUrl = ref("");
const showResult = ref(false);

const shortenUrl = () => {
  if (!longUrl.value) {
    alert("Please enter a valid URL");
    return;
  }

  originalUrl.value = longUrl.value;
  const baseUrl = "http://snap.link/";
  const path = customAlias.value || Math.random().toString(36).substr(2, 6);
  shortenedUrl.value = baseUrl + path;
  showResult.value = true;
};

const resetForm = () => {
  longUrl.value = "";
  customAlias.value = "";
  showResult.value = false;
};

const showStatistics = () => {
  alert("Statistics feature coming soon!");
};
</script>

<style scoped>
/* 保持原有基础样式 */
.container {
  display: flex;
  justify-content: center;
  padding: 50px 20px;
  background-color: #f8f9fa;
  min-height: calc(100vh - 80px);
}

.content-box {
  width: 100%;
  max-width: 600px;
  background: white;
  padding: 40px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

h2 {
  margin-bottom: 20px;
  font-size: 32px;
  color: #1a5f8c;
  letter-spacing: -0.5px;
}

.description {
  font-size: 16px;
  color: #666;
  margin-bottom: 35px;
  line-height: 1.6;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.input-box {
  width: 100%;
  margin: 18px 0;
  padding: 16px;
  border: 2px solid #d1d5db;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.input-box:focus {
  border-color: #1d72b8;
  outline: none;
}

.shorten-button {
  background-color: #1d72b8;
  color: white;
  padding: 16px 32px;
  font-size: 18px;
  font-weight: 500;
  border-radius: 8px;
  margin-top: 25px;
  width: 100%;
  transition: all 0.3s;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.shorten-button:hover {
  background-color: #145a8d;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(29, 114, 184, 0.3);
}

/* 新增结果展示样式 */
.result {
  text-align: center;
}

.url-group {
  background: #f1f5f9;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.url-item {
  margin-bottom: 20px;
}

.url-item:last-child {
  margin-bottom: 0;
}

.url-label {
  color: #64748b;
  font-size: 14px;
  margin-bottom: 8px;
}

.original-url {
  color: #1d72b8;
  word-break: break-all;
  text-decoration: none;
  display: block;
  padding: 8px;
  background: white;
  border-radius: 4px;
}

.shortened-url {
  color: #10b981;
  font-weight: 500;
  word-break: break-all;
  text-decoration: none;
  display: block;
  padding: 8px;
  background: white;
  border-radius: 4px;
}

.action-buttons {
  margin-top: 30px;
  display: flex;
  gap: 15px;
}

.stats-button {
  flex: 1;
  background: #1d72b8;
  color: white;
  padding: 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.another-button {
  flex: 1;
  background: white;
  color: #1d72b8;
  padding: 12px;
  border: 2px solid #1d72b8;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.stats-button:hover {
  background: #145a8d;
  transform: translateY(-1px);
}

.another-button:hover {
  background: #f8f9fa;
  transform: translateY(-1px);
}
</style>
