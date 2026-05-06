<script setup>
import axios from 'axios';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const username = ref('')
const phone = ref('')
const email = ref('')
const password = ref('')
const errorMessage = ref('')
const intro = ref('')
const selectedFile = ref(null)

const router = useRouter()

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0]
}

const handleRegister = async () => {
  if (!username.value || !phone.value || !password.value) {
    errorMessage.value = "請填寫 * 提示欄位!";
    return;
  }

  const formData = new FormData();
  formData.append('username', username.value);
  formData.append('phone', phone.value);
  formData.append('email', email.value);
  formData.append('password', password.value);
  formData.append('intro', intro.value);
  if (selectedFile.value) {
    formData.append('coverImage', selectedFile.value);
  }

  try {
    await axios.post('http://localhost:8080/api/users', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    alert('註冊成功！跳轉至登入頁面');
    await router.push('/');
  } catch (err) {
    if (err.response) {
      errorMessage.value = err.response.data || '註冊失敗，請稍後再試';
    } else {
      errorMessage.value = '無法連線到伺服器';
    }
  }
};
</script>
<template>
  <div class="min-h-screen bg-gray-100 dark:bg-gray-900 flex items-center justify-center py-8">
    <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-8 w-full max-w-sm">

      <!-- 標題 -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white">建立帳號</h1>
        <p class="text-gray-400 dark:text-gray-500 mt-1 text-sm">填寫以下資料完成註冊</p>
      </div>

      <!-- 輸入欄位 -->
      <div class="flex flex-col gap-4 mb-4">
        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-300 mb-1">使用者名稱 <span class="text-red-400">*</span></label>
          <input
              v-model="username"
              type="text"
              placeholder="請輸入名稱"
              class="w-full border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700"
          />
        </div>

        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-300 mb-1">手機號碼 <span class="text-red-400">*</span></label>
          <input
              v-model="phone"
              type="text"
              placeholder="09xxxxxxxx"
              class="w-full border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700"
          />
        </div>

        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-300 mb-1">電子郵件 <span class="text-red-400">*</span></label>
          <input
              v-model="email"
              type="text"
              placeholder="example@email.com"
              class="w-full border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700"
          />
        </div>

        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-300 mb-1">密碼 <span class="text-red-400">*</span></label>
          <input
              v-model="password"
              type="password"
              placeholder="請設定密碼"
              class="w-full border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700"
          />
        </div>

        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-300 mb-1">自我介紹</label>
          <textarea
              v-model="intro"
              placeholder="簡單介紹一下自己..."
              class="w-full border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700 resize-none h-20"
          ></textarea>
        </div>

        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-300 mb-1">封面照片</label>
          <input
              type="file"
              accept="image/*"
              @change="handleFileChange"
              class="w-full text-sm text-gray-500 file:mr-3 file:py-1 file:px-3 file:rounded-lg file:border-0 file:bg-blue-50 file:text-blue-500 hover:file:bg-blue-100"
          />
        </div>
      </div>

      <!-- 錯誤訊息 -->
      <p v-if="errorMessage" class="text-red-500 text-sm mb-4 text-center">
        {{ errorMessage }}
      </p>

      <!-- 註冊按鈕 -->
      <button
          @click="handleRegister"
          class="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded-lg transition-colors mb-4"
      >
        確認註冊
      </button>

      <!-- 分隔線 -->
      <div class="flex items-center gap-2 mb-4">
        <div class="flex-1 h-px bg-gray-200"></div>
        <span class="text-gray-400 dark:text-gray-500 text-sm">或</span>
        <div class="flex-1 h-px bg-gray-200"></div>
      </div>

      <!-- 返回登入 -->
      <p class="text-center text-sm text-gray-500 dark:text-gray-400">
        已經有帳號了？
        <router-link to="/" class="text-blue-500 hover:underline font-medium">
          返回登入
        </router-link>
      </p>

    </div>
  </div>
</template>
