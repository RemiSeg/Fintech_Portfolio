<template>
  <main class="flex min-h-screen items-center justify-center bg-[#f8f9fa] p-6">
    <section class="w-full max-w-md rounded-lg border border-gray-200 bg-white p-8">
      <p class="text-sm font-semibold uppercase tracking-wide text-gray-500">
        Portfolio Visualizer
      </p>

      <h1 class="mt-3 text-3xl font-semibold tracking-tight text-[#1a146b]">
        {{ isSignup ? "Create account" : "Sign in" }}
      </h1>

      <p class="mt-2 text-sm text-gray-600">
        Access your portfolios, stock universe, and performance analytics.
      </p>

      <form class="mt-8 space-y-4" @submit.prevent="handleSubmit">
        <div v-if="isSignup">
          <label class="mb-1 block text-sm font-medium text-gray-700">
            Name
          </label>
          <input
            v-model.trim="form.name"
            type="text"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
            placeholder="Your name"
            required
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">
            Email
          </label>
          <input
            v-model.trim="form.email"
            type="email"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
            placeholder="you@example.com"
            required
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">
            Password
          </label>
          <input
            v-model="form.password"
            type="password"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
            placeholder="Password"
            required
          />
        </div>

        <p v-if="errorMessage" class="rounded-lg bg-red-50 p-3 text-sm text-red-800">
          {{ errorMessage }}
        </p>

        <BaseButton type="submit" class="w-full" :disabled="authStore.loading">
          {{ authStore.loading ? "Please wait..." : isSignup ? "Create account" : "Sign in" }}
        </BaseButton>
      </form>

      <div class="mt-6 border-t border-gray-100 pt-6 text-center">
        <button
          type="button"
          class="text-sm font-medium text-[#1a146b] hover:underline"
          @click="toggleMode"
        >
          {{ isSignup ? "Already have an account? Sign in" : "Need an account? Create one" }}
        </button>
      </div>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import BaseButton from "@/components/common/BaseButton.vue";

const router = useRouter();
const authStore = useAuthStore();

const isSignup = ref(false);
const errorMessage = ref("");

const form = reactive({
  name: "",
  email: "",
  password: "",
});

function toggleMode() {
  isSignup.value = !isSignup.value;
  errorMessage.value = "";
}

async function handleSubmit() {
  errorMessage.value = "";

  try {
    if (isSignup.value) {
      await authStore.signup({
        name: form.name,
        email: form.email,
        password: form.password,
      });
    } else {
      await authStore.login({
        email: form.email,
        password: form.password,
      });
    }

    router.push("/dashboard");
  } catch {
    errorMessage.value = authStore.error || "Authentication failed.";
  }
}
</script>