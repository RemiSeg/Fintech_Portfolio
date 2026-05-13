<template>
  <BaseCard>
    <div class="mb-6">
      <h2 class="text-lg font-semibold text-[#1a146b]">
        Performance Comparison
      </h2>
      <p class="mt-1 text-sm text-gray-600">
        Indexed performance series returned by the backend.
      </p>
    </div>

    <div v-if="!seriesNames.length" class="rounded-lg border border-dashed border-gray-300 p-8 text-center">
      <p class="text-sm text-gray-600">
        Select portfolios or stocks to compare.
      </p>
    </div>

    <apexchart
      v-else
      type="line"
      height="380"
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
    type: Object,
    default: () => ({}),
  },
});

const seriesNames = computed(() => Object.keys(props.series || {}));

const chartSeries = computed(() =>
  seriesNames.value.map((name) => ({
    name,
    data: props.series[name].map((point) => ({
      x: point.date,
      y: Number(point.value),
    })),
  }))
);

const chartOptions = computed(() => ({
  chart: {
    toolbar: { show: false },
    zoom: { enabled: false },
    fontFamily: "Inter, sans-serif",
  },
  stroke: {
    curve: "smooth",
    width: 2,
  },
  colors: ["#1a146b", "#006c4a", "#0a0086", "#6b7280", "#ba1a1a"],
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
    },
  },
  legend: {
    position: "top",
    horizontalAlign: "left",
    labels: {
      colors: "#374151",
    },
  },
}));
</script>