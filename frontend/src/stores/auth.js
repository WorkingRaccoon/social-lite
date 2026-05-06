import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || null,
        userId: localStorage.getItem('userId') || null,
        userName: localStorage.getItem('userName') || null,
    }),

    getters: {
        isLoggedIn: (state) => !!state.token,
    },

    actions: {
        login(data) {
            this.token = data.token;
            this.userId = data.userId;
            this.userName = data.userName;

            // 同步存進 localStorage
            localStorage.setItem('token', data.token);
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('userName', data.userName);
        },

        logout() {
            this.token = null;
            this.userId = null;
            this.userName = null;

            // 清除 localStorage
            localStorage.removeItem('token');
            localStorage.removeItem('userId');
            localStorage.removeItem('userName');
        }
    }
});