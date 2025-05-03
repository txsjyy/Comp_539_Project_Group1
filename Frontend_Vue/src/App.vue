<template>
  <div class="container">
    <!-- Navigation bar with 100% width -->
    <header class="navbar">
      <nav>
        <router-link to="/">HOME</router-link>
        <router-link to="/myurls">MyURLs</router-link>
        <router-link to="/plans">Plans</router-link>
        <router-link v-if="!userStore.isLoggedIn" to="/login" class="sign-button">
          Sign in
        </router-link>
      </nav>
    </header>

    <!-- Main content -->
    <main class="content">
      <router-view></router-view>
    </main>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useUserStore } from './stores/user';

const router = useRouter();
const userStore = useUserStore();

const logout = () => {
  userStore.logout();
  router.push('/login');
};
</script>
<style>
/* Make the entire page fill the screen */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}

/* Make #app fill the entire page */
#app {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* Make the navigation bar fill the top */
.navbar {
  width: 100%;
  height: 60px;
  background-color: #1d72b8;
  display: flex;
  justify-content: space-around;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
}

/* Make navigation bar text visible */
.navbar nav {
  display: flex;
  width: 100%;
  justify-content: space-around;
  align-items: center;
}

/* Navigation bar option styles */
.navbar nav a {
  color: white;
  text-decoration: none;
  font-size: 18px;
  font-weight: bold;
  padding: 10px;
}

/* Center `ShortenUrlForm.vue` */
.content {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
  margin-top: 60px;
}
</style>



