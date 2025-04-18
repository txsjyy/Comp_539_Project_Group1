<!-- src/views/ResetPassword.vue -->
<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1 class="logo-text">SnapLink</h1>
      <h2 class="auth-title">Reset Password</h2>

      <form @submit.prevent="handleResetPassword" class="auth-form">
        <div class="form-group">
          <label>New Password</label>
          <input
            v-model="password"
            type="password"
            required
            class="form-input"
            placeholder="••••••••"
          />
        </div>

        <div class="form-group">
          <label>Confirm New Password</label>
          <input
            v-model="confirmPassword"
            type="password"
            required
            class="form-input"
            placeholder="••••••••"
          />
        </div>

        <button type="submit" class="auth-button primary" :disabled="isLoading">
          {{ isLoading ? 'Processing...' : 'Reset Password' }}
        </button>
      </form>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <router-link to="/login" class="auth-link">
        Back to Login
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const route = useRoute();
const token = route.params.token as string;

const password = ref('');
const confirmPassword = ref('');
const isLoading = ref(false);
const errorMessage = ref('');

const handleResetPassword = async () => {
  // Clear old error message
  errorMessage.value = '';

  // Validate password
  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Passwords do not match';
    return;
  }

  if (password.value.length < 8) {
    errorMessage.value = 'Password must be at least 8 characters long';
    return;
  }

  try {
    isLoading.value = true;
    const API = import.meta.env.VITE_API_BASE_URL;
    const response = await axios.post(
      `${API}/api/auth/reset-password`,
      {
        token: token,
        password: password.value
      },
      {
        headers: { 'Content-Type': 'application/json' }
      }
    );

    alert('Password reset successful! Please login with your new password.');
    router.push('/login');
  } catch (error: any) {
    console.error('Reset password error:', error);
    errorMessage.value = error.response?.data?.error || 'Password reset failed, please try again';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 2rem;
}

.auth-card {
  background: white;
  width: 100%;
  max-width: 740px;
  padding: 7.5rem;
  border-radius: 38px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.logo-text {
  text-align: center;
  color: #1d72b8;
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.auth-title {
  text-align: center;
  font-size: 1.5rem;
  color: #1a365d;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  color: #4a5568;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-input:focus {
  border-color: #1d72b8;
  outline: none;
}

.auth-button {
  width: 100%;
  padding: 0.75rem;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.auth-button.primary {
  background-color: #1d72b8;
  color: white;
}

.auth-button.primary:hover:not(:disabled) {
  background-color: #145a8d;
  transform: translateY(-1px);
}

.auth-button.primary:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.error-message {
  color: #dc2626;
  text-align: center;
  margin-top: 1rem;
  font-size: 0.9rem;
}

.auth-link {
  display: block;
  text-align: center;
  color: #1d72b8;
  text-decoration: none;
  font-weight: 500;
  margin-top: 1.5rem;
}

.auth-link:hover {
  text-decoration: underline;
}
</style> 