import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Forum from '../views/Forum.vue'
import PostDetail from '../views/PostDetail.vue'

const routes = [
    { path: '/', name: 'Login', component: Login },
    { path: '/Register', name: 'Register', component: Register },
    { path: '/forum', name: 'Forum', component: Forum, meta: { requiresAuth: true } },
    { path: '/forum/post/:id', name: 'PostDetail', component: PostDetail, meta: { requiresAuth: true } }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.meta.requiresAuth && !localStorage.getItem('token')) {
        next('/');
    } else {
        next();
    }
})

export default router