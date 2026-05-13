<template>
  <AppLayout>
    <div class="space-y-8">
      <section class="flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <h1 class="text-3xl font-semibold tracking-tight text-[#1a146b]">
            Compare
          </h1>
          <p class="mt-2 max-w-3xl text-sm text-gray-600">
            Compare portfolios and individual stocks using backend-generated performance series and metrics.
          </p>
        </div>

        <BaseButton
          variant="secondary"
          :disabled="selectedItems.length === 0"
          @click="clearSelection"
        >
          Reset
        </BaseButton>
      </section>

      <ErrorState v-if="pageError" :message="pageError" />

      <BaseCard>
        <div class="grid gap-6 lg:grid-cols-3">
          <div>
            <label class="mb-2 block text-sm font-medium text-gray-700">
              Range
            </label>
            <select
              v-model="range"
              class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
            >
              <option value="1M">1M</option>
              <option value="3M">3M</option>
              <option value="6M">6M</option>
              <option value="1Y">1Y</option>
              <option value="ALL">ALL</option>
            </select>
          </div>

          <div>
            <label class="mb-2 block text-sm font-medium text-gray-700">
              Portfolios
            </label>
            <select
              v-model="selectedPortfolioId"
              class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
            >
              <option value="">Add portfolio...</option>
              <option
                v-for="portfolio in availablePortfolios"
                :key="portfolio.id"
                :value="portfolio.id"
              >
                {{ portfolio.name }}
              </option>
            </select>

            <BaseButton
              class="mt-3 w-full"
              variant="secondary"
              :disabled="!selectedPortfolioId"
              @click="addPortfolio"
            >
              Add Portfolio
            </BaseButton>
          </div>

          <div>
            <label class="mb-2 block text-sm font-medium text-gray-700">
              Stocks
            </label>
            <select
              v-model="selectedTicker"
              class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
            >
              <option value="">Add stock...</option>
              <option
                v-for="stock in availableStocks"
                :key="stock.ticker"
                :value="stock.ticker"
              >
                {{ stock.ticker }} — {{ stock.companyName }}
              </option>
            </select>

            <BaseButton
              class="mt-3 w-full"
              variant="secondary"
              :disabled="!selectedTicker"
              @click="addStock"
            >
              Add Stock
            </BaseButton>
          </div>
        </div>

        <div class="mt-6 border-t border-gray-100 pt-6">
          <div class="mb-3 flex items-center justify-between">
            <p class="text-sm font-semibold text-gray-900">
              Selected items
            </p>

            <button
              type="button"
              class="text-sm font-medium text-gray-500 hover:text-[#1a146b]"
              @click="clearSelection"
            >
              Clear
            </button>
          </div>

          <div v-if="selectedItems.length === 0" class="rounded-lg border border-dashed border-gray-300 p-4">
            <p class="text-sm text-gray-600">
              Add at least one portfolio or stock.
            </p>
          </div>

          <div v-else class="flex flex-wrap gap-2">
            <button
              v-for="item in selectedItems"
              :key="`${item.type}-${item.id}`"
              type="button"
              class="rounded-full border border-gray-200 bg-gray-50 px-3 py-1 text-sm font-medium text-gray-700 hover:border-[#ba1a1a] hover:text-[#ba1a1a]"
              @click="removeItem(item)"
            >
              {{ item.label }} ×
            </button>
          </div>
        </div>

        <div class="mt-6 flex justify-end">
          <BaseButton
            :disabled="comparisonLoading || selectedItems.length === 0"
            @click="runComparison"
          >
            {{ comparisonLoading ? "Comparing..." : "Run Comparison" }}
          </BaseButton>
        </div>
      </BaseCard>

      <LoadingState
        v-if="initialLoading"
        message="Loading comparison inputs..."
      />

      <template v-else>
        <ComparisonLineChart :series="comparison.series" />
        <MetricsComparisonTable :metrics="comparison.metrics" />
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRoute } from "vue-router";

import AppLayout from "@/layouts/AppLayout.vue";
import BaseButton from "@/components/common/BaseButton.vue";
import BaseCard from "@/components/common/BaseCard.vue";
import ErrorState from "@/components/common/ErrorState.vue";
import LoadingState from "@/components/common/LoadingState.vue";
import ComparisonLineChart from "@/components/charts/ComparisonLineChart.vue";
import MetricsComparisonTable from "@/components/common/MetricsComparisonTable.vue";

import { compareApi } from "@/api/compareApi";
import { useAuthStore } from "@/stores/authStore";
import { usePortfolioStore } from "@/stores/portfolioStore";
import { useStockStore } from "@/stores/stockStore";

const route = useRoute();

const authStore = useAuthStore();
const portfolioStore = usePortfolioStore();
const stockStore = useStockStore();

const range = ref("1Y");
const selectedPortfolioId = ref("");
const selectedTicker = ref("");

const selectedPortfolioIds = ref([]);
const selectedTickers = ref([]);

const initialLoading = ref(false);
const comparisonLoading = ref(false);
const pageError = ref("");

const comparison = reactive({
  series: {},
  metrics: {},
});

const availablePortfolios = computed(() =>
  portfolioStore.allPortfolioSummaries.filter(
    (portfolio) => !selectedPortfolioIds.value.includes(portfolio.id)
  )
);

const availableStocks = computed(() =>
  stockStore.sortedStocks.filter(
    (stock) => !selectedTickers.value.includes(stock.ticker)
  )
);

const selectedItems = computed(() => {
  const portfolios = selectedPortfolioIds.value.map((id) => {
    const portfolio = portfolioStore.allPortfolioSummaries.find((item) => item.id === id);

    return {
      type: "portfolio",
      id,
      label: portfolio?.name || id,
    };
  });

  const stocks = selectedTickers.value.map((ticker) => ({
    type: "stock",
    id: ticker,
    label: ticker,
  }));

  return [...portfolios, ...stocks];
});

function addPortfolio() {
  if (!selectedPortfolioId.value) return;

  selectedPortfolioIds.value.push(selectedPortfolioId.value);
  selectedPortfolioId.value = "";
}

function addStock() {
  if (!selectedTicker.value) return;

  selectedTickers.value.push(selectedTicker.value);
  selectedTicker.value = "";
}

function removeItem(item) {
  if (item.type === "portfolio") {
    selectedPortfolioIds.value = selectedPortfolioIds.value.filter(
      (id) => id !== item.id
    );
  }

  if (item.type === "stock") {
    selectedTickers.value = selectedTickers.value.filter(
      (ticker) => ticker !== item.id
    );
  }

  if (selectedItems.value.length <= 1) {
    comparison.series = {};
    comparison.metrics = {};
  }
}

function clearSelection() {
  selectedPortfolioIds.value = [];
  selectedTickers.value = [];
  comparison.series = {};
  comparison.metrics = {};
}

async function runComparison() {
  comparisonLoading.value = true;
  pageError.value = "";

  try {
    const response = await compareApi.compare({
      portfolioIds: selectedPortfolioIds.value,
      tickers: selectedTickers.value,
      range: range.value,
    });

    comparison.series = response.data.series || {};
    comparison.metrics = response.data.metrics || {};
  } catch (error) {
    pageError.value =
      error.response?.data?.error || "Could not run comparison.";
  } finally {
    comparisonLoading.value = false;
  }
}

async function loadInputs() {
  initialLoading.value = true;
  pageError.value = "";

  try {
    await Promise.all([
      portfolioStore.fetchDefaultPortfolios(),
      portfolioStore.fetchUserPortfolios(authStore.userId),
      stockStore.fetchStocks(),
    ]);

    if (route.query.ticker) {
      const ticker = String(route.query.ticker).toUpperCase();

      if (stockStore.stocks.some((stock) => stock.ticker === ticker)) {
        selectedTickers.value = [ticker];
      }
    }

    if (route.query.portfolioId) {
      const portfolioId = String(route.query.portfolioId);

      if (
        portfolioStore.allPortfolioSummaries.some(
          (portfolio) => portfolio.id === portfolioId
        )
      ) {
        selectedPortfolioIds.value = [portfolioId];
      }
    }

    if (selectedItems.value.length > 0) {
      await runComparison();
    }
  } catch (error) {
    pageError.value =
      error.response?.data?.error || "Could not load comparison inputs.";
  } finally {
    initialLoading.value = false;
  }
}

onMounted(loadInputs);
</script>