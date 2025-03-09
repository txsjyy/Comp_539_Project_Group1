<!-- src/views/ForgotPassword.vue -->
<template>
    <div class="auth-container">
      <div class="auth-card">
        <h1 class="logo-text">SnapLink</h1>
        <h2 class="auth-title">Forgot Password</h2>
  
        <form @submit.prevent="handleForgotPassword" class="auth-form">
          <div class="form-group">
            <label>Email</label>
            <input
              v-model="email"
              type="email"
              required
              class="form-input"
              placeholder="user@example.com"
            />
          </div>
          <button type="submit" class="auth-button primary">
            Send Reset Link
          </button>
        </form>
  
        <router-link to="/login" class="auth-link">
          Back to Login
        </router-link>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue';
  
  const email = ref('');
  
  const handleForgotPassword = async () => {
    // 在这里调用后端 API 发送重置密码链接
    try {
      // 示例: await api.post('/api/forgot-password', { email: email.value })
      alert('The reset password link has been sent to your email.');
    } catch (error) {
      console.error(error);
      alert('Failed to send, please try again!');
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
  
  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 1rem 0;
  }
  
  .remember-me {
    color: #4a5568;
    font-size: 0.9rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  
  .forgot-password {
    color: #1d72b8;
    font-size: 0.9rem;
    text-decoration: none;
  }
  
  .forgot-password:hover {
    text-decoration: underline;
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
  
  .auth-button.primary:hover {
    background-color: #145a8d;
    transform: translateY(-1px);
  }
  
  .auth-alternative {
    text-align: center;
    margin-top: 1.5rem;
    color: #4a5568;
  }
  
  .auth-link {
    color: #1d72b8;
    text-decoration: none;
    font-weight: 500;
  }
  
  .auth-link:hover {
    text-decoration: underline;
  }
  </style>
  
  