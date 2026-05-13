<template>
  <AppLayout>
    <div class="space-y-8">
      <section class="flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <button
            type="button"
            class="mb-3 text-sm font-medium text-gray-600 hover:text-[#1a146b]"
            @click="router.push('/stocks')"
          >
            ← Back to stocks
          </button>

          <div class="flex flex-wrap items-center gap-3">
            <span class="rounded-full bg-indigo-50 px-3 py-1 text-xs font-semibold text-[#1a146b]">
              {{ ticker }}
            </span>

            <h1 class="text-3xl font-semibold tracking-tight text-[#1a146b]">
              {{ stock?.companyName || "Stock Detail" }}
            </h1>
          </div>

          <p class="mt-2 text-sm text-gray-600">
            Historical stock performance and security information from the supported universe.
          </p>
        </div>

        <BaseButton
          variant="secondary"
          @click="router.push({ name: 'compare', query: { ticker } })"
        >
          Compare
        </BaseButton>
      </section>

      <ErrorState v-if="pageError" :message="pageError" />

      <LoadingState
        v-if="loading"
        message="Loading stock data..."
      />

      <template v-else-if="stock">
        <section class="grid gap-4 md:grid-cols-3">
          <MetricCard
            label="Ticker"
            :value="stock.ticker"
          />

          <MetricCard
            label="Exchange"
            :value="stock.exchange"
          />

          <MetricCard
            label="Sector"
            :value="stock.sector"
          />
        </section>

        <StockPriceChart
          v-model="selectedRange"
          :series="stockStore.stockHistory"
          :ticker="stock.ticker"
        />

        <BaseCard>
          <h2 class="text-lg font-semibold text-[#1a146b]">
            Security Profile
          </h2>

          <div class="mt-6 grid gap-4 md:grid-cols-2">
            <div class="rounded-lg border border-gray-200 bg-gray-50 p-4">
              <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
                Company Name
              </p>
              <p class="mt-2 text-sm font-medium text-gray-900">
                {{ stock.companyName }}
              </p>
            </div>

            <div class="rounded-lg border border-gray-200 bg-gray-50 p-4">
              <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
                Ticker
              </p>
              <p class="numeric mt-2 text-sm font-medium text-gray-900">
                {{ stock.ticker }}
              </p>
            </div>

            <div class="rounded-lg border border-gray-200 bg-gray-50 p-4">
              <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
                Sector
              </p>
              <p class="mt-2 text-sm font-medium text-gray-900">
                {{ stock.sector }}
              </p>
            </div>

            <div class="rounded-lg border border-gray-200 bg-gray-50 p-4">
              <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
                Exchange
              </p>
              <p class="mt-2 text-sm font-medium text-gray-900">
                {{ stock.exchange }}
              </p>
            </div>
          </div>
        </BaseCard>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import AppLayout from "@/layouts/AppLayout.vue";
import BaseButton from "@/components/common/BaseButton.vue";
import BaseCard from "@/components/common/BaseCard.vue";
import ErrorState from "@/components/common/ErrorState.vue";
import LoadingState from "@/components/common/LoadingState.vue";
import MetricCard from "@/components/common/MetricCard.vue";
import StockPriceChart from "@/components/charts/StockPriceChart.vue";

import { useStockStore } from "@/stores/stockStore";

const route = useRoute();
const router = useRouter();
const stockStore = useStockStore();

const selectedRange = ref("1Y");
const pageError = ref("");
const loading = ref(false);

const ticker = computed(() => String(route.params.ticker || "").toUpperCase());
const stock = computed(() => stockStore.selectedStock);

async function loadStockData() {
  loading.value = true;
  pageError.value = "";

  try {
    await Promise.all([
      stockStore.fetchStockByTicker(ticker.value),
      stockStore.fetchStockHistory(ticker.value, selectedRange.value),
    ]);
  } catch (error) {
    pageError.value =
      error.response?.data?.error || "Could not load stock data.";
  } finally {
    loading.value = false;
  }
}

async function loadHistoryOnly() {
  pageError.value = "";

  try {
    await stockStore.fetchStockHistory(ticker.value, selectedRange.value);
  } catch (error) {
    pageError.value =
      error.response?.data?.error || "Could not load stock history.";
  }
}

onMounted(loadStockData);

watch(
  () => route.params.ticker,
  () => {
    if (route.name === "stock-detail") {
      loadStockData();
    }
  }
);

watch(selectedRange, loadHistoryOnly);
</script>