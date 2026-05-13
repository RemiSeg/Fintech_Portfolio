<template>
  <AppLayout>
    <div class="space-y-8">
      <section class="flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <h1 class="text-3xl font-semibold tracking-tight text-[#1a146b]">
            Dashboard
          </h1>
          <p class="mt-2 text-sm text-gray-600">
            View default portfolios, your saved portfolios, and the supported stock universe.
          </p>
        </div>

        <BaseButton
          :disabled="portfolioStore.userPortfolios.length >= 3"
          @click="goToCreatePortfolio"
        >
          Create Portfolio
        </BaseButton>
      </section>

      <ErrorState v-if="pageError" :message="pageError" />

      <section class="grid gap-4 md:grid-cols-3">
        <MetricCard
          label="Default Portfolios"
          :value="portfolioStore.defaultPortfolios.length"
        />

        <MetricCard
          label="Your Portfolios"
          :value="`${portfolioStore.userPortfolios.length}/3`"
        />

        <MetricCard
          label="Supported Stocks"
          :value="stockStore.stocks.length"
        />
      </section>

      <LoadingState
        v-if="loading"
        message="Loading dashboard data..."
      />

      <template v-else>
        <section class="space-y-4">
          <div class="flex flex-col justify-between gap-3 md:flex-row md:items-end">
            <div>
              <h2 class="text-xl font-semibold text-[#1a146b]">
                Your Portfolios
              </h2>
              <p class="mt-1 text-sm text-gray-600">
                Portfolios created by your account. Limit: 3 portfolios.
              </p>
            </div>

            <RouterLink
              v-if="portfolioStore.userPortfolios.length < 3"
              to="/portfolios/new"
              class="text-sm font-semibold text-[#1a146b] hover:underline"
            >
              Create new
            </RouterLink>
          </div>

          <BaseCard v-if="portfolioStore.userPortfolios.length === 0">
            <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
              <div>
                <h3 class="font-semibold text-gray-900">
                  No user portfolios yet
                </h3>
                <p class="mt-1 text-sm text-gray-600">
                  Create your first portfolio using up to 10 supported stocks.
                </p>
              </div>

              <BaseButton @click="goToCreatePortfolio">
                Create Portfolio
              </BaseButton>
            </div>
          </BaseCard>

          <BaseCard v-else-if="portfolioStore.userPortfolios.length >= 3">
            <p class="text-sm text-gray-700">
              You have reached the limit of 3 user portfolios. Edit or delete an existing portfolio to create another one.
            </p>
          </BaseCard>

          <div v-if="portfolioStore.userPortfolios.length > 0" class="grid gap-4 md:grid-cols-3">
            <PortfolioCard
              v-for="portfolio in portfolioStore.userPortfolios"
              :key="portfolio.id"
              :portfolio="portfolio"
            />
          </div>
        </section>

        <section class="space-y-4">
          <div>
            <h2 class="text-xl font-semibold text-[#1a146b]">
              Default Portfolios
            </h2>
            <p class="mt-1 text-sm text-gray-600">
              Backend-provided templates with real performance available on detail pages.
            </p>
          </div>

          <div class="grid gap-4 md:grid-cols-3">
            <PortfolioCard
              v-for="portfolio in portfolioStore.defaultPortfolios"
              :key="portfolio.id"
              :portfolio="portfolio"
            />
          </div>
        </section>

        <section class="space-y-4">
          <div class="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
            <div>
              <h2 class="text-xl font-semibold text-[#1a146b]">
                Supported Stock Universe
              </h2>
              <p class="mt-1 text-sm text-gray-600">
                Stocks available for portfolio construction and comparison.
              </p>
            </div>

            <div class="flex flex-col gap-2 sm:flex-row">
              <input
                v-model.trim="stockSearch"
                type="text"
                placeholder="Search ticker or company..."
                class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10 md:w-72"
              />

              <RouterLink
                to="/stocks"
                class="inline-flex items-center justify-center rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-[#1a146b] hover:bg-gray-50"
              >
                View all
              </RouterLink>
            </div>
          </div>

          <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
            <StockSummaryCard
              v-for="stock in filteredStocks.slice(0, 10)"
              :key="stock.ticker"
              :stock="stock"
            />
          </div>

          <BaseCard v-if="filteredStocks.length === 0">
            <p class="text-sm text-gray-600">
              No stocks match your search.
            </p>
          </BaseCard>
        </section>
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
import PortfolioCard from "@/components/portfolios/PortfolioCard.vue";
import StockSummaryCard from "@/components/stocks/StockSummaryCard.vue";

import { useAuthStore } from "@/stores/authStore";
import { usePortfolioStore } from "@/stores/portfolioStore";
import { useStockStore } from "@/stores/stockStore";

const router = useRouter();

const authStore = useAuthStore();
const portfolioStore = usePortfolioStore();
const stockStore = useStockStore();

const loading = ref(false);
const pageError = ref("");
const stockSearch = ref("");

const filteredStocks = computed(() => {
  const query = stockSearch.value.toLowerCase();

  if (!query) return stockStore.sortedStocks;

  return stockStore.sortedStocks.filter((stock) => {
    return (
      stock.ticker.toLowerCase().includes(query) ||
      stock.companyName.toLowerCase().includes(query) ||
      stock.sector.toLowerCase().includes(query)
    );
  });
});

function goToCreatePortfolio() {
  if (portfolioStore.userPortfolios.length >= 3) return;
  router.push("/portfolios/new");
}

async function loadDashboard() {
  loading.value = true;
  pageError.value = "";

  try {
    await Promise.all([
      portfolioStore.fetchDefaultPortfolios(),
      portfolioStore.fetchUserPortfolios(authStore.userId),
      stockStore.fetchStocks(),
    ]);
  } catch (error) {
    pageError.value =
      error.response?.data?.error || "Could not load dashboard data.";
  } finally {
    loading.value = false;
  }
}

onMounted(loadDashboard);
</script>