<template>
  <div class="min-h-screen bg-gray-100 dark:bg-gray-900">
    <!-- 頂部導覽列 -->
    <div class="bg-white dark:bg-gray-800 shadow-sm sticky top-0 z-10">
      <div class="max-w-2xl mx-auto px-4 py-6">
        <button
            @click="router.push('/forum')"
            class="text-gray-500 dark:text-gray-300 hover:text-gray-700 flex items-center gap-1 text-sm font-medium transition-colors"
        >
          ← 返回社群平台
        </button>
      </div>
    </div>

    <div class="max-w-2xl mx-auto px-4 py-6">
      <!-- 文章內容 -->
      <div v-if="post" class="bg-white dark:bg-gray-800 dark:border-gray-700 border border-gray-200 rounded-2xl p-6 mb-6 shadow-sm">
        <div class="flex justify-between items-start mb-4">
          <div>
            <p class="font-bold text-lg text-gray-800 dark:text-white">{{ post.user_name }}</p>
            <p class="text-gray-400 text-sm">{{ formatDate(post.created_at) }}</p>
            <p v-if="post.updated_at !== post.created_at" class="text-gray-300 text-xs">
              已編輯於 {{ formatDate(post.updated_at) }}
            </p>
          </div>
          <!-- 只有自己的文章才顯示編輯和刪除 -->
          <div v-if="post.user_id == authStore.userId" class="flex gap-2">
            <button
                @click="openEditForm"
                class="bg-yellow-400 text-white px-3 py-1 rounded-lg hover:bg-yellow-500 text-sm font-medium transition-colors"
            >
              編輯
            </button>
            <button
                @click="deletePost"
                class="bg-red-500 text-white px-3 py-1 rounded-lg hover:bg-red-600 text-sm font-medium transition-colors"
            >
              刪除
            </button>
          </div>
        </div>

        <img
            v-if="post.image"
            :src="'http://localhost:8080' + post.image"
            class="w-full rounded-xl mb-4 object-cover max-h-96"
        />

        <p class="text-gray-700 dark:text-gray-300 whitespace-pre-wrap leading-relaxed">{{ post.content }}</p>
      </div>

      <!-- 編輯表單 -->
      <div v-if="showEditForm" class="bg-white dark:bg-gray-800 dark:border-yellow-600 border border-yellow-300 rounded-2xl p-5 mb-6 shadow-sm">
        <h2 class="font-semibold text-gray-700 dark:text-gray-200 mb-3">編輯文章</h2>
        <textarea
            v-model="editContent"
            class="w-full h-28 p-3 border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg resize-none mb-3 focus:outline-none focus:ring-2 focus:ring-yellow-400 text-gray-700 text-sm"
        ></textarea>
        <div class="flex gap-2">
          <button
              @click="submitEdit"
              class="bg-yellow-400 text-white px-5 py-2 rounded-lg hover:bg-yellow-500 text-sm font-medium transition-colors"
          >
            儲存
          </button>
          <button
              @click="showEditForm = false"
              class="bg-gray-100 text-gray-600 px-5 py-2 rounded-lg hover:bg-gray-200 text-sm font-medium transition-colors"
          >
            取消
          </button>
        </div>
      </div>

      <!-- 留言區 -->
      <div class="bg-white dark:bg-gray-800 dark:border-gray-700 border border-gray-200 rounded-2xl p-5 shadow-sm">
        <h2 class="font-bold text-lg text-gray-800 dark:text-white mb-4">
          留言
          <span class="text-gray-400 text-sm font-normal ml-1">{{ comments.length }} 則</span>
        </h2>

        <!-- 新增留言 -->
        <div class="mb-5">
          <textarea
              v-model="newComment"
              placeholder="新增留言..."
              class="w-full h-20 p-3 border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white rounded-lg resize-none mb-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700 text-sm"
          ></textarea>
          <button
              @click="submitComment"
              class="bg-blue-500 text-white px-5 py-2 rounded-lg hover:bg-blue-600 text-sm font-medium transition-colors"
          >
            留言
          </button>
        </div>

        <!-- 分隔線 -->
        <div class="h-px bg-gray-100 mb-4"></div>

        <!-- 留言列表 -->
        <div class="flex flex-col gap-3">
          <div
              v-for="comment in comments"
              :key="comment.comment_id"
              class="bg-gray-50 dark:bg-gray-700 rounded-xl p-4"
          >
            <p class="font-semibold text-sm text-gray-800 dark:text-white mb-1">{{ comment.user_name }}</p>
            <p class="text-gray-600 dark:text-gray-300 text-sm leading-relaxed">{{ comment.content }}</p>
            <p class="text-gray-300 text-xs mt-2">{{ formatDate(comment.created_at) }}</p>
          </div>

          <!-- 沒有留言時 -->
          <div v-if="comments.length === 0" class="text-center text-gray-400 py-8">
            <p class="text-3xl mb-2">💬</p>
            <p class="text-sm">還沒有留言，成為第一個留言的人！</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const post = ref(null);
const comments = ref([]);
const newComment = ref('');
const showEditForm = ref(false);
const editContent = ref('');

const postId = route.params.id;

const loadPost = async () => {
  const response = await axios.get(`http://localhost:8080/api/posts/${postId}`, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  });
  post.value = response.data;
};

const loadComments = async () => {
  const response = await axios.get(`http://localhost:8080/api/posts/${postId}/comments`, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  });
  comments.value = response.data;
};

const openEditForm = () => {
  editContent.value = post.value.content;
  showEditForm.value = true;
};

const submitEdit = async () => {
  try {
    await axios.put(`http://localhost:8080/api/posts/${postId}`, {
      content: editContent.value,
      image: post.value.image
    }, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    });
    showEditForm.value = false;
    await loadPost();
  } catch (error) {
    console.error('編輯失敗', error);
  }
};

const deletePost = async () => {
  if (!confirm('確定要刪除這篇文章嗎？')) return;
  try {
    await axios.delete(`http://localhost:8080/api/posts/${postId}`, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    });
    router.push('/forum');
  } catch (error) {
    console.error('刪除失敗', error);
  }
};

const submitComment = async () => {
  if (!newComment.value.trim()) return;
  try {
    await axios.post(`http://localhost:8080/api/posts/${postId}/comments`, {
      content: newComment.value
    }, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    });
    newComment.value = '';
    await loadComments();
  } catch (error) {
    console.error('留言失敗', error);
  }
};

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('zh-TW');
};

onMounted(async () => {
  await loadPost();
  await loadComments();
});
</script>