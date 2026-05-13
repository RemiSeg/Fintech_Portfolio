<template>
  <div class="rounded-lg border border-gray-200 bg-white p-8 text-center">
    <p class="text-sm font-medium text-gray-700">{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    default: "Loading...",
  },
});
</script>