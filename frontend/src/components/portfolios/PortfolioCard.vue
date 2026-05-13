<template>
  <RouterLink
    :to="`/portfolios/${portfolio.id}`"
    class="block rounded-lg border border-gray-200 bg-white p-5 transition-colors hover:border-[#1a146b]"
  >
    <div class="flex items-start justify-between gap-4">
      <div>
        <p class="text-lg font-semibold text-[#1a146b]">
          {{ portfolio.name }}
        </p>

        <p class="mt-1 line-clamp-2 text-sm text-gray-600">
          {{ portfolio.description || "No description provided." }}
        </p>
      </div>

      <span
        class="rounded-full px-2.5 py-1 text-xs font-semibold"
        :class="portfolio.isDefault ? 'bg-indigo-50 text-[#1a146b]' : 'bg-emerald-50 text-[#006c4a]'"
      >
        {{ portfolio.isDefault ? "Default" : "User" }}
      </span>
    </div>

    <div class="mt-5 border-t border-gray-100 pt-4">
      <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
        Holdings
      </p>

      <p class="numeric mt-1 text-2xl font-semibold text-gray-900">
        {{ portfolio.numberOfHoldings ?? 0 }}
      </p>
    </div>
  </RouterLink>
</template>

<script setup>
defineProps({
  portfolio: {
    type: Object,
    required: true,
  },
});
</script>