<template>
  <BaseCard>
    <div class="mb-6 flex flex-col justify-between gap-3 md:flex-row md:items-center">
      <div>
        <h2 class="text-lg font-semibold text-[#1a146b]">
          Price History
        </h2>
        <p class="mt-1 text-sm text-gray-600">
          Historical close price from backend stock history.
        </p>
      </div>

      <div class="flex rounded-lg bg-gray-100 p-1">
        <button
          v-for="option in ranges"
          :key="option"
          type="button"
          class="rounded-md px-3 py-1 text-xs font-semibold transition-colors"
          :class="modelValue === option ? 'bg-white text-[#1a146b]' : 'text-gray-500 hover:text-[#1a146b]'"
          @click="$emit('update:modelValue', option)"
        >
          {{ option }}
        </button>
      </div>
    </div>

    <div v-if="!series.length" class="rounded-lg border border-dashed border-gray-300 p-8 text-center">
      <p class="text-sm text-gray-600">
        No price history available.
      </p>
    </div>

    <apexchart
      v-else
      type="line"
      height="360"
      :options="chartOptions"
      :series="chartSeries"
    />
  </BaseCard>
</template>

<script setup>
import { computed } from "vue";
import BaseCard from "@/components/common/BaseCard.vue";

const props = defineProps({
  series: {
    type: Array,
    required: true,
  },
  ticker: {
    type: String,
    required: true,
  },
  modelValue: {
    type: String,
    required: true,
  },
});

defineEmits(["update:modelValue"]);

const ranges = ["1M", "3M", "6M", "1Y", "ALL"];

const chartSeries = computed(() => [
  {
    name: props.ticker,
    data: props.series.map((point) => ({
      x: point.date,
      y: Number(point.value),
    })),
  },
]);

const chartOptions = computed(() => ({
  chart: {
    toolbar: {
      show: false,
    },
    zoom: {
      enabled: false,
    },
    fontFamily: "Inter, sans-serif",
  },
  stroke: {
    curve: "smooth",
    width: 2,
  },
  colors: ["#1a146b"],
  grid: {
    borderColor: "#e5e7eb",
    strokeDashArray: 4,
  },
  dataLabels: {
    enabled: false,
  },
  markers: {
    size: 0,
  },
  xaxis: {
    type: "datetime",
    labels: {
      style: {
        colors: "#6b7280",
        fontSize: "12px",
      },
    },
    axisBorder: {
      color: "#e5e7eb",
    },
    axisTicks: {
      color: "#e5e7eb",
    },
  },
  yaxis: {
    labels: {
      formatter: (value) => Number(value).toFixed(2),
      style: {
        colors: "#6b7280",
        fontSize: "12px",
      },
    },
  },
  tooltip: {
    x: {
      format: "yyyy-MM-dd",
    },
    y: {
      formatter: (value) => `$${Number(value).toFixed(2)}`,
      title: {
        formatter: () => "Close:",
      },
    },
  },
}));
</script>