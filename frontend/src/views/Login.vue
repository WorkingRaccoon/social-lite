<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import axios from 'axios';

const router = useRouter();
const phone = ref('');
const password = ref('');
const errorMessage = ref('');
const authStore = useAuthStore();

const login = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/auth/token', {
      phone: phone.value,
      password: password.value
    });

    authStore.login({
      token: response.data.token,
      userId: response.data.userId,
      userName: response.data.userName
    });

    router.push('/forum');

  } catch (error) {
    if (error.response && error.response.status === 401) {
      errorMessage.value = '手機號碼或密碼錯誤';
    } else {
      errorMessage.value = '登入失敗，請稍後再試';
    }
  }
};
</script>

<template>
  <div class="min-h-screen bg-gray-100 dark:bg-gray-900 flex items-center justify-center">
    <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-8 w-full max-w-sm">

      <!-- 標題 -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white">社群平台</h1>
        <p class="text-gray-400 dark:text-gray-500 mt-1 text-sm">請登入您的帳號</p>
      </div>

      <!-- 輸入欄位 -->
      <div class="flex flex-col gap-4 mb-4">
        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-300 mb-1">手機號碼</label>
          <input
              v-model="phone"
              type="text"
              placeholder="09xxxxxxxx"
              class="w-full border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700"
          />
        </div>
        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-300 mb-1">密碼</label>
          <input
              v-model="password"
              type="password"
              placeholder="請輸入密碼"
              class="w-full border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700"
          />
        </div>
      </div>

      <!-- 錯誤訊息 -->
      <p v-if="errorMessage" class="text-red-500 text-sm mb-4 text-center">
        {{ errorMessage }}
      </p>

      <!-- 登入按鈕 -->
      <button
          @click="login"
          class="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded-lg transition-colors mb-4"
      >
        登入
      </button>

      <!-- 分隔線 -->
      <div class="flex items-center gap-2 mb-4">
        <div class="flex-1 h-px bg-gray-200"></div>
        <span class="text-gray-400 dark:text-gray-500 text-sm">或</span>
        <div class="flex-1 h-px bg-gray-200"></div>
      </div>

      <!-- 前往註冊 -->
      <p class="text-center text-sm text-gray-500 dark:text-gray-400">
        還沒有帳號嗎？
        <router-link to="/register" class="text-blue-500 hover:underline font-medium">
          立即註冊
        </router-link>
      </p>

    </div>
  </div>
</template>
