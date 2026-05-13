<template>
  <BaseCard>
    <div class="mb-6">
      <h2 class="text-lg font-semibold text-[#1a146b]">
        Holdings
      </h2>
      <p class="mt-1 text-sm text-gray-600">
        Current portfolio allocation.
      </p>
    </div>

    <div v-if="!holdings.length" class="rounded-lg border border-dashed border-gray-300 p-8 text-center">
      <p class="text-sm text-gray-600">
        No holdings available.
      </p>
    </div>

    <div v-else class="overflow-x-auto">
      <table class="w-full border-collapse text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-gray-500">
              Ticker
            </th>
            <th class="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-gray-500">
              Company
            </th>
            <th class="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
              Weight
            </th>
          </tr>
        </thead>

        <tbody class="divide-y divide-gray-100">
          <tr
            v-for="holding in holdings"
            :key="holding.ticker"
            class="hover:bg-gray-50"
          >
            <td class="px-4 py-4">
              <RouterLink
                :to="`/stocks/${holding.ticker}`"
                class="rounded bg-indigo-50 px-2 py-1 text-sm font-semibold text-[#1a146b] hover:underline"
              >
                {{ holding.ticker }}
              </RouterLink>
            </td>

            <td class="px-4 py-4 text-sm text-gray-800">
              {{ holding.companyName }}
            </td>

            <td class="numeric px-4 py-4 text-right text-sm font-semibold text-gray-900">
              {{ formatNumber(holding.weight, 2) }}%
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </BaseCard>
</template>

<script setup>
import BaseCard from "@/components/common/BaseCard.vue";
import { formatNumber } from "@/utils/formatters";

defineProps({
  holdings: {
    type: Array,
    required: true,
  },
});
</script>