<template>
  <AppLayout>
    <div class="space-y-8">
      <section class="flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <h1 class="text-3xl font-semibold tracking-tight text-[#1a146b]">
            Stocks
          </h1>
          <p class="mt-2 max-w-3xl text-sm text-gray-600">
            Browse the backend-supported stock universe used for portfolio construction, stock analysis, and comparisons.
          </p>
        </div>

        <BaseButton variant="secondary" @click="router.push('/compare')">
          Compare Stocks
        </BaseButton>
      </section>

      <ErrorState v-if="pageError" :message="pageError" />

      <section class="grid gap-4 md:grid-cols-3">
        <MetricCard
          label="Supported Stocks"
          :value="stockStore.stocks.length"
        />

        <MetricCard
          label="Sectors"
          :value="sectorCount"
        />

        <MetricCard
          label="Exchanges"
          :value="exchangeCount"
        />
      </section>

      <BaseCard>
        <div class="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-[#1a146b]">
              Stock Universe
            </h2>
            <p class="mt-1 text-sm text-gray-600">
              Select a stock to view its historical price chart and security profile.
            </p>
          </div>

          <input
            v-model.trim="search"
            type="text"
            placeholder="Search ticker, company, sector..."
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10 md:w-80"
          />
        </div>
      </BaseCard>

      <LoadingState
        v-if="stockStore.loading"
        message="Loading stocks..."
      />

      <template v-else>
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <StockSummaryCard
            v-for="stock in filteredStocks"
            :key="stock.ticker"
            :stock="stock"
          />
        </div>

        <BaseCard v-if="filteredStocks.length === 0">
          <p class="text-sm text-gray-600">
            No stocks match your search.
          </p>
        </BaseCard>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import AppLayout from "@/layouts/AppLayout.vue";
import BaseButton from "@/components/common/BaseButton.vue";
import BaseCard from "@/components/common/BaseCard.vue";
import ErrorState from "@/components/common/ErrorState.vue";
import LoadingState from "@/components/common/LoadingState.vue";
import MetricCard from "@/components/common/MetricCard.vue";
import StockSummaryCard from "@/components/stocks/StockSummaryCard.vue";

import { useStockStore } from "@/stores/stockStore";

const router = useRouter();
const stockStore = useStockStore();

const search = ref("");
const pageError = ref("");

const filteredStocks = computed(() => {
  const query = search.value.toLowerCase();

  if (!query) return stockStore.sortedStocks;

  return stockStore.sortedStocks.filter((stock) => {
    return (
      stock.ticker.toLowerCase().includes(query) ||
      stock.companyName.toLowerCase().includes(query) ||
      stock.sector.toLowerCase().includes(query) ||
      stock.exchange.toLowerCase().includes(query)
    );
  });
});

const sectorCount = computed(
  () => new Set(stockStore.stocks.map((stock) => stock.sector)).size
);

const exchangeCount = computed(
  () => new Set(stockStore.stocks.map((stock) => stock.exchange)).size
);

async function loadStocks() {
  pageError.value = "";

  try {
    await stockStore.fetchStocks();
  } catch (error) {
    pageError.value = error.response?.data?.error || "Could not load stocks.";
  }
}

onMounted(loadStocks);
</script>