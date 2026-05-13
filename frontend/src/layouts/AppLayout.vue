<template>
  <div class="min-h-screen bg-[#f8f9fa] text-[#191c1d]">
    <div class="flex">
      <Sidebar />

      <div class="min-h-screen flex-1">
        <Topbar />

        <main class="mx-auto max-w-7xl px-6 py-8">
          <slot />
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import Sidebar from "@/components/layout/Sidebar.vue";
import Topbar from "@/components/layout/Topbar.vue";
</script>