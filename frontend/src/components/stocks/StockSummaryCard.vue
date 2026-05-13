<template>
  <RouterLink
    :to="`/stocks/${stock.ticker}`"
    class="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#1a146b]"
  >
    <div class="flex items-start justify-between gap-3">
      <div>
        <p class="font-semibold text-[#1a146b]">
          {{ stock.ticker }}
        </p>

        <p class="mt-1 text-sm text-gray-700">
          {{ stock.companyName }}
        </p>
      </div>

      <span class="rounded-full bg-gray-50 px-2 py-1 text-xs font-medium text-gray-600">
        {{ stock.exchange }}
      </span>
    </div>

    <p class="mt-4 text-xs font-semibold uppercase tracking-wide text-gray-500">
      {{ stock.sector }}
    </p>
  </RouterLink>
</template>

<script setup>
defineProps({
  stock: {
    type: Object,
    required: true,
  },
});
</script>