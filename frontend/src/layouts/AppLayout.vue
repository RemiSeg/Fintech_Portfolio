<template>
  <div class="min-h-screen bg-[#f8f9fa] text-[#191c1d]">
    <div class="flex">
      <Sidebar />

      <div class="min-h-screen flex-1 pb-20 md:pb-0">
        <Topbar />

        <main class="mx-auto max-w-7xl px-4 py-6 md:px-6 md:py-8">
          <slot />
        </main>
      </div>
    </div>

    <nav class="fixed bottom-0 left-0 right-0 z-30 grid h-16 grid-cols-4 border-t border-gray-200 bg-white md:hidden">
      <RouterLink
        v-for="item in mobileItems"
        :key="item.to"
        :to="item.to"
        class="flex items-center justify-center text-xs font-semibold text-gray-500"
        active-class="text-[#1a146b] bg-gray-50"
      >
        {{ item.label }}
      </RouterLink>
    </nav>
  </div>
</template>

<script setup>
import Sidebar from "@/components/layout/Sidebar.vue";
import Topbar from "@/components/layout/Topbar.vue";

const mobileItems = [
  { label: "Home", to: "/dashboard" },
  { label: "Stocks", to: "/stocks" },
  { label: "Compare", to: "/compare" },
  { label: "Create", to: "/portfolios/new" },
];
</script>