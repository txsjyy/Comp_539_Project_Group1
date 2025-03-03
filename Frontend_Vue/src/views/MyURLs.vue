<template>
    <div class="container">
      <h1 class="title">My Shortened URLs</h1>
  
      <!-- 搜索框 -->
      <input v-model="searchQuery" class="search-input" placeholder="Search URLs..." />
  
      <div class="table-container">
        <table class="table">
          <thead>
            <tr>
              <th class="th">Short URL</th>
              <th class="th">Original URL</th>
              <th class="th">Clicks</th>
              <th class="th">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="url in paginatedUrls" :key="url.id" class="tr">
              <td class="td">
                <a :href="url.shortUrl" target="_blank" class="link">
                  {{ url.shortUrl }}
                </a>
              </td>
              <td class="td td-left">{{ url.originalUrl }}</td>
              <td class="td">{{ url.clickCount }}</td>
              <td class="td">
                <button @click="copyUrl(url.shortUrl)" class="button">Copy</button>
                <button @click="editUrl(url)" class="edit-button">Edit</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
  
      <!-- 分页控件 -->
      <div class="pagination">
        <button @click="prevPage" :disabled="currentPage === 1" class="page-button">← Prev</button>
        <span class="page-info">Page {{ currentPage }} of {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages" class="page-button">Next →</button>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed } from "vue";
  
  // 示例数据
  const urls = ref(
    Array.from({ length: 30 }, (_, i) => ({
      id: String(i + 1),
      shortUrl: `https://sho.rt/${Math.random().toString(36).substr(2, 6)}`,
      originalUrl: `https://example.com/page${i + 1}`,
      clickCount: Math.floor(Math.random() * 100),
    }))
  );
  
  const searchQuery = ref("");
  const currentPage = ref(1);
  const itemsPerPage = 10;
  
  // 过滤搜索结果
  const filteredUrls = computed(() =>
    urls.value.filter(
      (url) =>
        url.shortUrl.includes(searchQuery.value) || url.originalUrl.includes(searchQuery.value)
    )
  );
  
  // 计算分页数据
  const totalPages = computed(() => Math.ceil(filteredUrls.value.length / itemsPerPage));
  
  const paginatedUrls = computed(() =>
    filteredUrls.value.slice((currentPage.value - 1) * itemsPerPage, currentPage.value * itemsPerPage)
  );
  
  const nextPage = () => {
    if (currentPage.value < totalPages.value) currentPage.value++;
  };
  
  const prevPage = () => {
    if (currentPage.value > 1) currentPage.value--;
  };
  
  const copyUrl = (url: string) => {
    navigator.clipboard.writeText(url).then(() => alert("Copied to clipboard!"));
  };
  
  const editUrl = (url: { id: string; shortUrl: string }) => {
    alert(`Edit feature for ${url.shortUrl} (to be implemented)`);
  };
  </script>
  
  <style scoped>
  /* 页面整体样式 */
  .container {
    padding: 40px;
    width: 100%;
    background-color: #f8f9fa;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  /* 标题样式 */
  .title {
    font-size: 42px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #2c3e50;
  }
  
  /* 搜索框 */
  .search-input {
    width: 60%;
    padding: 12px;
    font-size: 18px;
    border: 1px solid #bdc3c7;
    border-radius: 8px;
    margin-bottom: 20px;
    outline: none;
    transition: all 0.3s ease;
  }
  
  .search-input:focus {
    border-color: #3498db;
  }
  
  /* 表格外层容器 */
  .table-container {
    overflow-x: auto;
    width: 90%;
    max-width: 1200px;
    background: white;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  }
  
  /* 表格样式 */
  .table {
    width: 100%;
    border-collapse: collapse;
    font-size: 20px;
  }
  
  /* 表头样式 */
  .th {
    background-color: #2c3e50;
    color: white;
    padding: 16px;
    text-align: center;
    font-weight: 600;
  }
  
  /* 表格行样式 */
  .tr:hover {
    background-color: #ecf0f1;
    transition: 0.3s;
  }
  
  /* 单元格样式 */
  .td {
    border: 1px solid #ddd;
    padding: 16px;
    text-align: center;
  }
  
  /* 左对齐的单元格 */
  .td-left {
    text-align: left;
    max-width: 300px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  /* 超链接样式 */
  .link {
    color: #2980b9;
    font-weight: 600;
    text-decoration: none;
  }
  
  .link:hover {
    text-decoration: underline;
  }
  
  /* 按钮样式 */
  .button, .edit-button {
    padding: 10px 16px;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    margin: 0 5px;
  }
  
  .button {
    background-color: #3498db;
  }
  
  .button:hover {
    background-color: #2980b9;
  }
  
  .edit-button {
    background-color: #95a5a6;
  }
  
  .edit-button:hover {
    background-color: #7f8c8d;
  }
  
  /* 分页控件 */
  .pagination {
    margin-top: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .page-button {
    padding: 10px 16px;
    background-color: #7f8c8d;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 18px;
    cursor: pointer;
    transition: all 0.2s;
  }
  
  .page-button:hover {
    background-color: #636e72;
  }
  
  .page-button:disabled {
    background-color: #bdc3c7;
    cursor: not-allowed;
  }
  
  .page-info {
    font-size: 20px;
    font-weight: 600;
    color: #2c3e50;
  }
  </style>
  