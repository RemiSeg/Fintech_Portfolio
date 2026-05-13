<template>
  <header class="sticky top-0 z-20 border-b border-gray-200 bg-white">
    <div class="flex h-16 items-center justify-between px-6">
      <div>
        <p class="text-sm font-semibold text-[#1a146b]">
          {{ title }}
        </p>
        <p class="text-xs text-gray-500">
          Portfolio analytics and stock performance visualization
        </p>
      </div>

      <div class="flex items-center gap-3">
        <div class="hidden text-right sm:block">
          <p class="text-sm font-medium text-gray-900">
            {{ authStore.name || "User" }}
          </p>
          <p class="text-xs text-gray-500">
            {{ authStore.email || "Signed in" }}
          </p>
        </div>

        <div class="flex h-9 w-9 items-center justify-center rounded-full bg-[#1a146b] text-sm font-semibold text-white">
          {{ initials }}
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

const route = useRoute();
const authStore = useAuthStore();

const title = computed(() => {
  if (route.name === "portfolio-detail") return "Portfolio Detail";
  if (route.name === "stock-detail") return "Stock Detail";
  if (route.name === "compare") return "Compare";
  return "Dashboard";
});

const initials = computed(() => {
  if (!authStore.name) return "U";

  return authStore.name
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();
});
</script>