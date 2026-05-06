<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import axios from 'axios';

const router = useRouter();
const authStore = useAuthStore();

const posts = ref([]);
const showPostForm = ref(false);
const newPost = ref({ content: '', image: null });
const imageFile = ref(null);

const loadPosts = async () => {
  const response = await axios.get('http://localhost:8080/api/posts', {
    headers: { Authorization: `Bearer ${authStore.token}` }
  });
  posts.value = response.data;
};

const openPostForm = () => {
  showPostForm.value = true;
  newPost.value = { content: '', image: null };
};

const handleImageUpload = (event) => {
  imageFile.value = event.target.files[0];
};

const submitPost = async () => {
  try {
    const formData = new FormData();
    formData.append('content', newPost.value.content);
    if (imageFile.value) {
      formData.append('image', imageFile.value);
    }

    await axios.post('http://localhost:8080/api/posts', formData, {
      headers: {
        Authorization: `Bearer ${authStore.token}`,
        'Content-Type': 'multipart/form-data'
      }
    });

    showPostForm.value = false;
    await loadPosts();
  } catch (error) {
    console.error('發文失敗', error);
  }
};

const goToPost = (postId) => {
  router.push(`/forum/post/${postId}`);
};

const truncate = (content) => {
  return content.length > 100 ? content.substring(0, 100) + '...' : content;
};

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('zh-TW');
};

const logout = async () => {
  try {
    await axios.delete('http://localhost:8080/api/auth/token', {
      headers: { Authorization: `Bearer ${authStore.token}` }
    });
  } catch (error) {
    console.error('登出失敗', error);
  } finally {
    authStore.logout();
    router.push('/');
  }
};

onMounted(() => {
  loadPosts();
});
</script>
<template>
  <div class="min-h-screen bg-gray-100 dark:bg-gray-900">
    <!-- 頂部導覽列 -->
    <div class="bg-white dark:bg-gray-800 shadow-sm sticky top-0 z-10">
      <div class="max-w-2xl mx-auto px-4 py-4 flex justify-between items-center">
        <h1 class="text-2xl font-bold text-gray-800 dark:text-white">社群平台</h1>
        <div class="flex items-center gap-3">
          <span class="text-gray-500 text-sm">{{ authStore.userName }}</span>
          <button
              @click="openPostForm"
              class="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 text-sm font-medium transition-colors"
          >
            新增發文
          </button>
          <button
              @click="logout"
              class="bg-gray-100 text-gray-600 px-4 py-2 rounded-lg hover:bg-gray-200 text-sm font-medium transition-colors"
          >
            登出
          </button>
        </div>
      </div>
    </div>

    <div class="max-w-2xl mx-auto px-4 py-6">
      <!-- 新增發文表單 -->
      <div v-if="showPostForm" class="bg-white dark:bg-gray-800 dark:border-gray-700 border border-gray-200 rounded-2xl p-5 mb-6 shadow-sm">
        <h2 class="font-semibold text-gray-700 dark:text-gray-200 mb-3">新增發文</h2>
        <textarea
            v-model="newPost.content"
            placeholder="說些什麼..."
            class="w-full h-28 p-3 border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg resize-none mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
        ></textarea>
        <input
            type="file"
            accept="image/*"
            @change="handleImageUpload"
            class="w-full text-sm text-gray-500 file:mr-3 file:py-1 file:px-3 file:rounded-lg file:border-0 file:bg-blue-50 file:text-blue-500 hover:file:bg-blue-100 mb-3"
        />
        <div class="flex gap-2">
          <button
              @click="submitPost"
              class="bg-blue-500 text-white px-5 py-2 rounded-lg hover:bg-blue-600 text-sm font-medium transition-colors"
          >
            發文
          </button>
          <button
              @click="showPostForm = false"
              class="bg-gray-100 text-gray-600 px-5 py-2 rounded-lg hover:bg-gray-200 text-sm font-medium transition-colors"
          >
            取消
          </button>
        </div>
      </div>

      <!-- 發文列表 -->
      <div class="flex flex-col gap-4">
        <div
            v-for="post in posts"
            :key="post.post_id"
            @click="goToPost(post.post_id)"
            class="bg-white dark:bg-gray-800 dark:border-gray-700 flex gap-4 p-4 border border-gray-200 rounded-2xl cursor-pointer hover:shadow-md transition-shadow"
        >
          <img
              v-if="post.image"
              :src="'http://localhost:8080' + post.image"
              class="w-24 h-24 object-cover rounded-xl flex-shrink-0"
          />
          <!-- 沒有圖片時顯示佔位區塊 -->
          <div
              v-else
              class="w-24 h-24 bg-gray-100 rounded-xl flex-shrink-0 flex items-center justify-center"
          >
            <span class="text-gray-300 text-3xl">📝</span>
          </div>

          <div class="flex-1 min-w-0 flex flex-col justify-between">
            <div>
              <div class="flex items-center gap-2 mb-1">
                <img
                    v-if="post.cover_image"
                    :src="'http://localhost:8080' + post.cover_image"
                    class="w-8 h-8 rounded-full object-cover"
                />
                <div
                    v-else
                    class="w-8 h-8 rounded-full bg-gray-300 dark:bg-gray-600 flex items-center justify-center text-gray-500 text-sm font-bold"
                >
                  {{ post.user_name.charAt(0).toUpperCase() }}
                </div>
                <p class="font-semibold text-gray-800 dark:text-white">{{ post.user_name }}</p>
              </div>
              <div class="h-px bg-gray-300 dark:bg-gray-700 mb-2 mt-2"></div>
              <p class="text-gray-500 text-sm line-clamp-2">{{ truncate(post.content) }}</p>
            </div>
            <div class="flex items-center gap-3 mt-2">
              <p class="text-gray-300 text-xs">{{ formatDate(post.created_at) }}</p>
              <p class="text-gray-400 text-xs">💬 {{ post.comment_count }} 則留言</p>
            </div>
          </div>
        </div>

        <!-- 沒有發文時 -->
        <div v-if="posts.length === 0" class="text-center text-gray-400 py-16">
          <p class="text-4xl mb-3">📭</p>
          <p>目前還沒有任何發文</p>
          <p class="text-sm mt-1">成為第一個發文的人吧！</p>
        </div>
      </div>
    </div>
  </div>
</template>
