<template>
  <div class="container">
    <h1 class="title">My Shortened URLs</h1>

    <!-- Error message display -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <!-- Search input -->
    <input v-model="searchQuery" class="search-input" placeholder="Search URLs..." />

    <!-- Loading state -->
    <div v-if="isLoading" class="loading">
      Loading...
    </div>

    <!-- Data table -->
    <div v-else class="table-container">
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
              <!-- Copy button with event object for mouse position -->
              <button @click="copyUrl(url.shortUrl, $event)" class="button">Copy</button>
              <button @click="triggerEdit(url)" class="edit-button">Edit</button>
              <button @click="showStatistics(url)" class="statistics-button">Statistics</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination controls -->
    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1" class="page-button">← Prev</button>
      <span class="page-info">Page {{ currentPage }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages" class="page-button">Next →</button>
    </div>
  </div>

  <!-- Edit modal, only allows Short URL modification -->
  <div v-if="isEditing" class="modal-overlay">
    <div class="modal">
      <h2>Edit Short URL</h2>
      <label>Short URL</label>
      <input v-if="editingUrl" v-model="editingUrl.shortUrl" type="text" />
      <label>Original URL</label>
      <div class="non-editable-url">{{ editingUrl?.originalUrl ?? 'N/A' }}</div>
      <div class="modal-actions">
        <button @click="saveEdit" class="button">Save</button>
        <button @click="cancelEdit" class="edit-button">Cancel</button>
      </div>
    </div>
  </div>

  <!-- Copy toast notification, positioned based on mouse click -->
  <div
    v-if="copySuccess"
    class="copy-toast"
    :style="{ top: copyPosition.top, left: copyPosition.left }"
  >
    Copied
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

// Interface definition for URL object
interface URL {
  id: string;
  shortUrl: string;
  originalUrl: string;
  clickCount: number;
}

const router = useRouter()
const userStore = useUserStore()

// Data state management
const urls = ref<URL[]>([]);
const isLoading = ref(false);
const error = ref('');

// Fetch user's URLs from the API
const fetchUserUrls = async () => {
  const storedUser = localStorage.getItem('user') || sessionStorage.getItem('user');
  if (!storedUser) {
    error.value = 'User not logged in';
    return;
  }

  const user = JSON.parse(storedUser);
  const userId = user.id;

  isLoading.value = true;
  error.value = '';

  try {
    const API = import.meta.env.VITE_API_BASE_URL;
    const response = await fetch(`${API}/search?query=${userId}`);

    if (!response.ok) {
      throw new Error('Failed to fetch data');
    }

    const data = await response.json();

    urls.value = data.map((item: any) => ({
      id: item.shortCode,
      shortUrl: `${API.replace(/\/$/, '')}/${item.shortCode}`,
      originalUrl: item.longUrl,
      clickCount: item.clickCount
    }));
  } catch (err) {
    console.error('Failed to fetch URL list:', err);
    error.value = 'Failed to fetch URL list. Please try again later.';
  } finally {
    isLoading.value = false;
  }
};

// Fetch data when component is mounted
onMounted(() => {
  fetchUserUrls();
});

// Search and pagination state
const searchQuery = ref("");
const currentPage = ref(1);
const itemsPerPage = 10;

// Filter URLs based on search query
const filteredUrls = computed(() =>
  urls.value.filter(
    (url) =>
      url.shortUrl.includes(searchQuery.value) ||
      url.originalUrl.includes(searchQuery.value)
  )
);

// Calculate pagination data
const totalPages = computed(() => Math.ceil(filteredUrls.value.length / itemsPerPage));
const paginatedUrls = computed(() =>
  filteredUrls.value.slice((currentPage.value - 1) * itemsPerPage, currentPage.value * itemsPerPage)
);

// Pagination navigation
const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};

// Copy functionality state
const copySuccess = ref(false);
const copyPosition = ref({ top: "0px", left: "0px" });

// Copy URL to clipboard and show toast notification
const copyUrl = (url: string, event: MouseEvent) => {
  navigator.clipboard.writeText(url).then(() => {
    // Set toast position with offset to avoid mouse cursor
    copyPosition.value = {
      top: event.clientY + 10 + "px",
      left: event.clientX + 10 + "px",
    };
    copySuccess.value = true;
    setTimeout(() => {
      copySuccess.value = false;
    }, 2000);
  });
};

// Edit functionality state
const isEditing = ref(false);
const editingUrl = ref<{ id: string; shortUrl: string; originalUrl: string; clickCount: number } | null>(null);

// Trigger edit modal with URL data
const triggerEdit = (url: { id: string; shortUrl: string; originalUrl: string; clickCount: number }) => {
  // Create a copy to avoid direct modification of original data
  editingUrl.value = { ...url };
  isEditing.value = true;
};

// Save edited URL changes
const saveEdit = async () => {
  if (!editingUrl.value) return;
  const extractShortCode = (fullUrl: string) => {
    try {
      return fullUrl.split('/').pop() || '';
    } catch {
      return '';
    }
  };
  const oldCode = extractShortCode(editingUrl.value.id);  // ensure it's just the code
  const newCode = editingUrl.value.shortUrl.trim();

  if (!editingUrl.value) return;

  try {
    const API = import.meta.env.VITE_API_BASE_URL;
    const response = await fetch(`${API}/update-shortcode`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',  // <-- explicitly set this header
      },
      body: JSON.stringify({ oldCode, newCode })
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Update failed');
    }

    // Update local list
    const index = urls.value.findIndex((u) => u.id === editingUrl.value!.id);
    if (index !== -1) {
      urls.value[index].id = editingUrl.value.shortUrl;
      urls.value[index].shortUrl = `${API.replace(/\/$/, '')}/${editingUrl.value.shortUrl}`;
    }

    isEditing.value = false;
    editingUrl.value = null;
  } catch (err) {
    console.error('Failed to update URL:', err);
    error.value = 'Update failed: ' + (err as Error).message;
  }
};

// Cancel edit
const cancelEdit = () => {
  isEditing.value = false;
  editingUrl.value = null;
};

// Show statistics page
const showStatistics = (url: { id: string }) => {
  router.push({ name: 'statistics', params: { id: url.id } })
};

</script>

<style scoped>
/* Page overall style */
.container {
  padding: 40px;
  width: 100%;
  background-color: #f8f9fa;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Title style */
.title {
  font-size: 42px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #2c3e50;
}

/* Search input */
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

/* Table outer container */
.table-container {
  overflow-x: auto;
  width: 90%;
  max-width: 1200px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

/* Table style */
.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 20px;
}

/* Table header style */
.th {
  background-color: #2c3e50;
  color: white;
  padding: 16px;
  text-align: center;
  font-weight: 600;
}

/* Table row style */
.tr:hover {
  background-color: #ecf0f1;
  transition: 0.3s;
}

/* Table cell style */
.td {
  border: 1px solid #ddd;
  padding: 16px;
  text-align: center;
}

/* Left aligned cell */
.td-left {
  text-align: left;
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Link style */
.link {
  color: #2980b9;
  font-weight: 600;
  text-decoration: none;
}
.link:hover {
  text-decoration: underline;
}

/* Button style */
.button,
.edit-button,
.statistics-button {
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

/* Statistics button style */
.statistics-button {
  background-color: #27ae60;
}
.statistics-button:hover {
  background-color: #1e8449;
}

/* Pagination controls */
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

/* Edit modal style */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  min-width: 300px;
}
.modal h2 {
  margin-bottom: 10px;
  color: #2c3e50;
}
.modal label {
  display: block;
  margin-top: 10px;
  font-size: 16px;
  color: #2c3e50;
}
.modal input {
  width: 100%;
  padding: 8px;
  margin-top: 5px;
  font-size: 16px;
  border: 1px solid #bdc3c7;
  border-radius: 4px;
}
.non-editable-url {
  padding: 8px;
  margin-top: 5px;
  font-size: 16px;
  border: 1px solid #bdc3c7;
  border-radius: 4px;
  background-color: #f8f9fa;
  color: #2c3e50;
}
.modal-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* Copy toast style, positioned based on mouse click */
.copy-toast {
  position: fixed;
  background-color: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 16px;
  pointer-events: none;
}

/* Error message style */
.error-message {
  background-color: #fee2e2;
  color: #dc2626;
  padding: 1rem;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
  width: 60%;
  text-align: center;
}

/* Loading state style */
.loading {
  text-align: center;
  padding: 2rem;
  color: #4b5563;
  font-size: 1.1rem;
}
</style>
