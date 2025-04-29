<template>
  <div class="container">
    <!-- 100% 宽度的导航栏 -->
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

    <!-- 页面主要内容 -->
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
/* 让整个页面填充满屏幕 */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}

/* 让 #app 充满整个页面 */
#app {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 让导航栏铺满顶部 */
.navbar {
  width: 100%;
  height: 60px; /* 固定高度 */
  background-color: #1d72b8;
  display: flex;
  justify-content: space-around; /* 让选项均匀分布 */
  align-items: center;
  position: fixed; /* 固定在顶部 */
  top: 0;
  left: 0;
  z-index: 1000;
}

/* 让导航栏的文字可见 */
.navbar nav {
  display: flex;
  width: 100%;
  justify-content: space-around; /* 选项均匀分布 */
  align-items: center;
}

/* 导航栏选项样式 */
.navbar nav a {
  color: white;
  text-decoration: none;
  font-size: 18px;
  font-weight: bold;
  padding: 10px;
}

/* 让 `ShortenUrlForm.vue` 居中 */
.content {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
  margin-top: 60px; /* 避免内容被导航栏遮挡 */
}
</style>



