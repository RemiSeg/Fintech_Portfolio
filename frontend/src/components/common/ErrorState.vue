<template>
  <div class="rounded-lg border border-red-200 bg-red-50 p-6">
    <p class="text-sm font-medium text-red-800">{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    default: "Something went wrong.",
  },
});
</script>