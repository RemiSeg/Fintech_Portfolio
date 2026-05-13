import { defineStore } from "pinia";
import { authApi } from "@/api/authApi";

const STORAGE_KEY = "auth";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    userId: null,
    name: null,
    email: null,
    token: null,
    loading: false,
    error: null,
  }),

  getters: {
    isAuthenticated: (state) => Boolean(state.userId),
    user: (state) =>
      state.userId
        ? {
            userId: state.userId,
            name: state.name,
            email: state.email,
            token: state.token,
          }
        : null,
  },

  actions: {
    loadFromStorage() {
      const saved = JSON.parse(localStorage.getItem(STORAGE_KEY) || "null");

      if (!saved) return;

      this.userId = saved.userId;
      this.name = saved.name;
      this.email = saved.email;
      this.token = saved.token;
    },

    saveSession(authResponse) {
      this.userId = authResponse.userId;
      this.name = authResponse.name;
      this.email = authResponse.email;
      this.token = authResponse.token;

      localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({
          userId: this.userId,
          name: this.name,
          email: this.email,
          token: this.token,
        })
      );
    },

    async login(payload) {
      this.loading = true;
      this.error = null;

      try {
        const response = await authApi.login(payload);
        this.saveSession(response.data);
        return response.data;
      } catch (error) {
        this.error = error.response?.data?.error || "Login failed.";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async signup(payload) {
      this.loading = true;
      this.error = null;

      try {
        const response = await authApi.signup(payload);
        this.saveSession(response.data);
        return response.data;
      } catch (error) {
        this.error = error.response?.data?.error || "Signup failed.";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    logout() {
      this.userId = null;
      this.name = null;
      this.email = null;
      this.token = null;
      this.error = null;

      localStorage.removeItem(STORAGE_KEY);
    },
  },
});