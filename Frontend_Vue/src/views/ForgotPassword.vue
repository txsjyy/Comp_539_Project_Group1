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
  import axios from 'axios'

  // Form state
  const email = ref('');
  const API = import.meta.env.VITE_API_BASE_URL;

  // Handle password recovery request
  const handleForgotPassword = async () => {
    try {
      const res = await axios.post(
        `${API}/api/auth/forgot-password`,
        { email: email.value },
        { headers: { 'Content-Type': 'application/json' } }
      )
      alert(res.data.message || 'Reset link sent—check your inbox!')
    } catch (err: any) {
      console.error(err)
      const msg = err.response?.data?.error || 'Failed to send, please try again!'
      alert(msg)
    }
  };
  </script>
  
  <style scoped>
  /* Authentication container layout */
  .auth-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: #f8f9fa;
    padding: 2rem;
  }
  
  /* Authentication card styling */
  .auth-card {
    background: white;
    width: 100%;
    max-width: 740px;
    padding: 7.5rem;
    border-radius: 38px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  /* Logo text styling */
  .logo-text {
    text-align: center;
    color: #1d72b8;
    font-size: 2rem;
    margin-bottom: 0.5rem;
  }
  
  /* Authentication title styling */
  .auth-title {
    text-align: center;
    font-size: 1.5rem;
    color: #1a365d;
    margin-bottom: 2rem;
  }
  
  /* Form group styling */
  .form-group {
    margin-bottom: 1.5rem;
  }
  
  /* Form label styling */
  .form-group label {
    display: block;
    color: #4a5568;
    margin-bottom: 0.5rem;
    font-size: 0.9rem;
  }
  
  /* Form input field styling */
  .form-input {
    width: 100%;
    padding: 0.75rem;
    border: 2px solid #e2e8f0;
    border-radius: 6px;
    font-size: 1rem;
    transition: border-color 0.3s;
  }
  
  /* Form input focus state */
  .form-input:focus {
    border-color: #1d72b8;
    outline: none;
  }
  
  /* Form options layout */
  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 1rem 0;
  }
  
  /* Remember me checkbox styling */
  .remember-me {
    color: #4a5568;
    font-size: 0.9rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  
  /* Forgot password link styling */
  .forgot-password {
    color: #1d72b8;
    font-size: 0.9rem;
    text-decoration: none;
  }
  
  /* Forgot password link hover state */
  .forgot-password:hover {
    text-decoration: underline;
  }
  
  /* Authentication button base styling */
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
  
  /* Primary button styling */
  .auth-button.primary {
    background-color: #1d72b8;
    color: white;
  }
  
  /* Primary button hover state */
  .auth-button.primary:hover {
    background-color: #145a8d;
    transform: translateY(-1px);
  }
  
  /* Alternative authentication text styling */
  .auth-alternative {
    text-align: center;
    margin-top: 1.5rem;
    color: #4a5568;
  }
  
  /* Authentication link styling */
  .auth-link {
    color: #1d72b8;
    text-decoration: none;
    font-weight: 500;
  }
  
  /* Authentication link hover state */
  .auth-link:hover {
    text-decoration: underline;
  }
  </style>
  
  