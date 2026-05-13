<template>
  <BaseCard>
    <div class="mb-6 flex flex-col justify-between gap-3 md:flex-row md:items-center">
      <div>
        <h2 class="text-lg font-semibold text-[#1a146b]">
          Performance Over Time
        </h2>
        <p class="mt-1 text-sm text-gray-600">
          Indexed portfolio value, starting at 100.
        </p>
      </div>
    </div>

    <div v-if="!series.length" class="rounded-lg border border-dashed border-gray-300 p-8 text-center">
      <p class="text-sm text-gray-600">
        No performance data available.
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
  name: {
    type: String,
    default: "Portfolio",
  },
});

const chartSeries = computed(() => [
  {
    name: props.name,
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
      formatter: (value) => Number(value).toFixed(0),
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
      formatter: (value) => Number(value).toFixed(2),
      title: {
        formatter: () => "Value:",
      },
    },
  },
}));
</script>