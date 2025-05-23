<!-- User registration view component -->
<template>
    <div class="auth-container">
      <div class="auth-card">
        <h1 class="logo-text">SnapLink</h1>
        <h2 class="auth-title">Sign Up</h2>

        <form @submit.prevent="handleSignUp" class="auth-form">
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

          <div class="form-group">
            <label>Password</label>
            <input
              v-model="password"
              type="password"
              required
              class="form-input"
              placeholder="••••••••"
            />
          </div>

          <div class="form-group">
            <label>Confirm Password</label>
            <input
              v-model="confirmPassword"
              type="password"
              required
              class="form-input"
              placeholder="••••••••"
            />
          </div>
          <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

          <button type="submit" class="auth-button primary">
            Sign Up
          </button>
        </form>

        <router-link to="/login" class="auth-link">
          Already have an account? Sign In
        </router-link>
      </div>
    </div>
  </template>

  <script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Form state management
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const subscriptionPlan = ref('free');
const isLoading = ref(false);
const errorMessage = ref('');

// Handle user registration
const handleSignUp = async () => {
  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Passwords do not match';
    return;
  }

  if (!isValidEmail(email.value)) {
    errorMessage.value = 'Please enter a valid email address';
    return;
  }

  try {
    isLoading.value = true;
    errorMessage.value = '';
    const API = import.meta.env.VITE_API_BASE_URL;
    const response = await fetch(`${API}/api/auth/signup`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: email.value,
        email: email.value,
        password: password.value,
        confirmPassword: confirmPassword.value,
        subscriptionPlan: subscriptionPlan.value
      }),
    });

    const data = await response.json();

    if (!response.ok) {
      errorMessage.value = data.error || 'Registration failed';
      return;
    }

    alert('Registration successful! Please log in.');
    router.push('/login');
  } catch (error) {
    console.error('Registration error:', error);
    errorMessage.value = 'Registration failed. Please try again later.';
  } finally {
    isLoading.value = false;
  }
};

// Validate email format
const isValidEmail = (email: string) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
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

