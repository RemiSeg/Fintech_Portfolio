<template>
  <AppLayout>
    <div class="space-y-8">
      <section class="flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <button
            type="button"
            class="mb-3 text-sm font-medium text-gray-600 hover:text-[#1a146b]"
            @click="router.push('/dashboard')"
          >
            ← Back to dashboard
          </button>

          <h1 class="text-3xl font-semibold tracking-tight text-[#1a146b]">
            {{ portfolio?.name || "Portfolio Detail" }}
          </h1>

          <p class="mt-2 max-w-3xl text-sm text-gray-600">
            {{ portfolio?.description || "Portfolio performance, holdings, and risk metrics." }}
          </p>
        </div>

        <div v-if="portfolio" class="flex flex-col gap-3 md:items-end">
          <div
            class="rounded-full px-3 py-1 text-xs font-semibold"
            :class="portfolio.isDefault ? 'bg-indigo-50 text-[#1a146b]' : 'bg-emerald-50 text-[#006c4a]'"
          >
            {{ portfolio.isDefault ? "Default Portfolio" : "User Portfolio" }}
          </div>

          <div class="flex flex-wrap gap-2">
            <BaseButton
              variant="secondary"
              @click="router.push({ name: 'compare', query: { portfolioId: portfolio.id } })"
            >
              Compare
            </BaseButton>

            <template v-if="!portfolio.isDefault">
              <BaseButton
                variant="secondary"
                @click="router.push(`/portfolios/${portfolio.id}/edit`)"
              >
                Edit
              </BaseButton>

              <BaseButton
                variant="danger"
                :disabled="deleting"
                @click="deleteCurrentPortfolio"
              >
                {{ deleting ? "Deleting..." : "Delete" }}
              </BaseButton>
            </template>
          </div>
        </div>
      </section>

      <ErrorState v-if="pageError" :message="pageError" />

      <LoadingState
        v-if="portfolioStore.loading && !portfolio"
        message="Loading portfolio details..."
      />

      <template v-else-if="portfolio">
        <section class="grid gap-4 md:grid-cols-3">
          <MetricCard
            label="Total Return"
            :value="formatPercent(portfolio.metrics?.totalReturn)"
            :tone="getMetricTone(portfolio.metrics?.totalReturn)"
          />

          <MetricCard
            label="Volatility"
            :value="formatPercent(portfolio.metrics?.volatility)"
            description="Historical risk estimate"
          />

          <MetricCard
            label="Max Drawdown"
            :value="formatPercent(portfolio.metrics?.maxDrawdown)"
            :tone="getMetricTone(portfolio.metrics?.maxDrawdown)"
            description="Peak-to-trough decline"
          />
        </section>

        <PerformanceLineChart
          :series="portfolio.performance || []"
          :name="portfolio.name"
        />

        <section class="grid gap-6 lg:grid-cols-[2fr_1fr]">
          <HoldingsTable :holdings="portfolio.holdings || []" />

          <BaseCard>
            <h2 class="text-lg font-semibold text-[#1a146b]">
              Allocation Summary
            </h2>

            <p class="mt-1 text-sm text-gray-600">
              Weight distribution across holdings.
            </p>

            <div class="mt-6 space-y-4">
              <div
                v-for="holding in portfolio.holdings"
                :key="holding.ticker"
                class="space-y-1"
              >
                <div class="flex justify-between gap-4 text-sm">
                  <span class="font-medium text-gray-800">
                    {{ holding.ticker }}
                  </span>
                  <span class="numeric text-gray-600">
                    {{ formatNumber(holding.weight, 2) }}%
                  </span>
                </div>

                <div class="h-2 overflow-hidden rounded-full bg-gray-100">
                  <div
                    class="h-full rounded-full bg-[#1a146b]"
                    :style="{ width: `${Number(holding.weight)}%` }"
                  />
                </div>
              </div>
            </div>
          </BaseCard>
        </section>
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
import PerformanceLineChart from "@/components/charts/PerformanceLineChart.vue";
import HoldingsTable from "@/components/portfolios/HoldingsTable.vue";

import { usePortfolioStore } from "@/stores/portfolioStore";
import {
  formatNumber,
  formatPercent,
  getMetricTone,
} from "@/utils/formatters";

const route = useRoute();
const router = useRouter();
const portfolioStore = usePortfolioStore();

const pageError = ref("");
const deleting = ref(false);

const portfolio = computed(() => portfolioStore.selectedPortfolio);

async function loadPortfolio() {
  pageError.value = "";

  try {
    await portfolioStore.fetchPortfolioById(route.params.portfolioId);
  } catch (error) {
    pageError.value =
      error.response?.data?.error || "Could not load portfolio details.";
  }
}

async function deleteCurrentPortfolio() {
  if (!portfolio.value || portfolio.value.isDefault) return;

  const confirmed = window.confirm(
    `Delete "${portfolio.value.name}"? This action cannot be undone.`
  );

  if (!confirmed) return;

  deleting.value = true;
  pageError.value = "";

  try {
    await portfolioStore.deletePortfolio(portfolio.value.id);
    router.push("/dashboard");
  } catch (error) {
    pageError.value =
      error.response?.data?.error || "Could not delete portfolio.";
  } finally {
    deleting.value = false;
  }
}

onMounted(loadPortfolio);

watch(
  () => route.params.portfolioId,
  () => {
    if (route.name === "portfolio-detail") {
      loadPortfolio();
    }
  }
);
</script>