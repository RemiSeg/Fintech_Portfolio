<template>
  <aside class="hidden min-h-screen w-64 border-r border-gray-200 bg-white px-4 py-6 md:block">
    <RouterLink to="/dashboard" class="mb-8 block px-2">
      <p class="text-lg font-bold text-[#1a146b]">Portfolio Visualizer</p>
      <p class="mt-1 text-xs font-semibold uppercase tracking-wide text-gray-500">
        Analytics Dashboard
      </p>
    </RouterLink>

    <nav class="space-y-1">
      <RouterLink
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="block rounded-lg px-3 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-gray-50 hover:text-[#1a146b]"
        active-class="bg-gray-50 text-[#1a146b] border-r-2 border-[#1a146b]"
      >
        {{ item.label }}
      </RouterLink>
    </nav>

    <div class="mt-8 border-t border-gray-100 pt-4">
      <button
        class="w-full rounded-lg px-3 py-2 text-left text-sm font-medium text-gray-600 transition-colors hover:bg-gray-50 hover:text-[#1a146b]"
        @click="handleLogout"
      >
        Logout
      </button>
    </div>
  </aside>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

const router = useRouter();
const authStore = useAuthStore();

const navItems = [
  { label: "Dashboard", to: "/dashboard" },
  { label: "Stocks", to: "/stocks" },
  { label: "Compare", to: "/compare" },
];

function handleLogout() {
  authStore.logout();
  router.push("/login");
}
</script>