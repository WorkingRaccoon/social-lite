<template>
  <div>
    <button
        @click="themeStore.toggle()"
        class="fixed top-3 right-4 z-50 text-xl px-3 py-2 rounded-lg bg-white dark:bg-gray-700 shadow-md hover:shadow-lg transition-all">
      {{ themeStore.isDark ? '☀️' : '🌙' }}
    </button>
    <router-view></router-view>
  </div>
</template>
<script setup>
import { useThemeStore } from './stores/theme';
const themeStore = useThemeStore();
</script>
<style>
.container {
  display: flex;
  flex-direction: column;
  max-width: 300px;
  margin: 50px auto;
  gap: 10px;
}
</style>