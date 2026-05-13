<template>
  <BaseCard>
    <div class="mb-6">
      <h2 class="text-lg font-semibold text-[#1a146b]">
        Risk & Return Metrics
      </h2>
      <p class="mt-1 text-sm text-gray-600">
        Backend-calculated total return, volatility, and max drawdown.
      </p>
    </div>

    <div v-if="!metricNames.length" class="rounded-lg border border-dashed border-gray-300 p-8 text-center">
      <p class="text-sm text-gray-600">
        No metrics available yet.
      </p>
    </div>

    <div v-else class="overflow-x-auto">
      <table class="w-full border-collapse text-left">
        <thead>
          <tr class="border-b border-gray-200 bg-gray-50">
            <th class="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-gray-500">
              Asset
            </th>
            <th class="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
              Total Return
            </th>
            <th class="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
              Volatility
            </th>
            <th class="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
              Max Drawdown
            </th>
          </tr>
        </thead>

        <tbody class="divide-y divide-gray-100">
          <tr v-for="name in metricNames" :key="name" class="hover:bg-gray-50">
            <td class="px-4 py-4 text-sm font-semibold text-[#1a146b]">
              {{ name }}
            </td>

            <td
              class="numeric px-4 py-4 text-right text-sm font-semibold"
              :class="getValueClass(metrics[name]?.totalReturn)"
            >
              {{ formatPercent(metrics[name]?.totalReturn) }}
            </td>

            <td class="numeric px-4 py-4 text-right text-sm text-gray-800">
              {{ formatPercent(metrics[name]?.volatility) }}
            </td>

            <td class="numeric px-4 py-4 text-right text-sm font-semibold text-[#ba1a1a]">
              {{ formatPercent(metrics[name]?.maxDrawdown) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </BaseCard>
</template>

<script setup>
import { computed } from "vue";
import BaseCard from "@/components/common/BaseCard.vue";
import { formatPercent } from "@/utils/formatters";

const props = defineProps({
  metrics: {
    type: Object,
    default: () => ({}),
  },
});

const metricNames = computed(() => Object.keys(props.metrics || {}));

function getValueClass(value) {
  const number = Number(value);

  if (number > 0) return "text-[#006c4a]";
  if (number < 0) return "text-[#ba1a1a]";

  return "text-gray-800";
}
</script>