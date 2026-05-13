<template>
  <form class="space-y-6" @submit.prevent="handleSubmit">
    <BaseCard>
      <div class="space-y-4">
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">
            Portfolio name
          </label>
          <input
            v-model.trim="form.name"
            type="text"
            required
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
            placeholder="Example: Long-Term Growth"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">
            Description
          </label>
          <textarea
            v-model.trim="form.description"
            rows="3"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
            placeholder="Short description of the portfolio objective."
          />
        </div>
      </div>
    </BaseCard>

    <BaseCard>
      <div class="mb-6 flex flex-col justify-between gap-3 md:flex-row md:items-center">
        <div>
          <h2 class="text-lg font-semibold text-[#1a146b]">
            Holdings
          </h2>
          <p class="mt-1 text-sm text-gray-600">
            Add 1 to 10 stocks. Weights must sum to exactly 100%.
          </p>
        </div>

        <BaseButton
          type="button"
          variant="secondary"
          :disabled="form.holdings.length >= 10"
          @click="addHolding"
        >
          Add holding
        </BaseButton>
      </div>

      <div class="space-y-3">
        <div
          v-for="(holding, index) in form.holdings"
          :key="index"
          class="grid gap-3 rounded-lg border border-gray-200 bg-gray-50 p-4 md:grid-cols-[1fr_160px_auto]"
        >
          <div>
            <label class="mb-1 block text-xs font-semibold uppercase tracking-wide text-gray-500">
              Stock
            </label>
            <select
              v-model="holding.ticker"
              required
              class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
            >
              <option value="" disabled>Select stock</option>
              <option
                v-for="stock in availableStocksFor(index)"
                :key="stock.ticker"
                :value="stock.ticker"
              >
                {{ stock.ticker }} — {{ stock.companyName }}
              </option>
            </select>
          </div>

          <div>
            <label class="mb-1 block text-xs font-semibold uppercase tracking-wide text-gray-500">
              Weight
            </label>
            <input
              v-model.number="holding.weight"
              type="number"
              min="0.01"
              max="100"
              step="0.01"
              required
              class="numeric w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm outline-none focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
              placeholder="25"
            />
          </div>

          <div class="flex items-end">
            <BaseButton
              type="button"
              variant="danger"
              :disabled="form.holdings.length <= 1"
              @click="removeHolding(index)"
            >
              Remove
            </BaseButton>
          </div>
        </div>
      </div>

      <div class="mt-6 rounded-lg border p-4" :class="weightStatusClass">
        <div class="flex flex-col gap-1 md:flex-row md:items-center md:justify-between">
          <p class="text-sm font-medium">
            Total weight: <span class="numeric">{{ totalWeight.toFixed(2) }}%</span>
          </p>

          <p class="text-sm">
            {{ weightStatusMessage }}
          </p>
        </div>
      </div>
    </BaseCard>

    <ErrorState v-if="errorMessage" :message="errorMessage" />

    <div class="flex flex-col-reverse gap-3 md:flex-row md:justify-end">
      <BaseButton type="button" variant="secondary" @click="router.push('/dashboard')">
        Cancel
      </BaseButton>

      <BaseButton type="submit" :disabled="submitting || !isValid">
        {{ submitting ? "Saving..." : submitLabel }}
      </BaseButton>
    </div>
  </form>
</template>

<script setup>
import { computed, reactive, watch } from "vue";
import { useRouter } from "vue-router";

import BaseButton from "@/components/common/BaseButton.vue";
import BaseCard from "@/components/common/BaseCard.vue";
import ErrorState from "@/components/common/ErrorState.vue";

const props = defineProps({
  initialPortfolio: {
    type: Object,
    default: null,
  },
  stocks: {
    type: Array,
    required: true,
  },
  submitting: {
    type: Boolean,
    default: false,
  },
  errorMessage: {
    type: String,
    default: "",
  },
  submitLabel: {
    type: String,
    default: "Save portfolio",
  },
});

const emit = defineEmits(["submit"]);

const router = useRouter();

const form = reactive({
  name: "",
  description: "",
  holdings: [{ ticker: "", weight: 100 }],
});

const totalWeight = computed(() =>
  form.holdings.reduce((sum, holding) => sum + Number(holding.weight || 0), 0)
);

const hasDuplicateTickers = computed(() => {
  const tickers = form.holdings
    .map((holding) => holding.ticker)
    .filter(Boolean);

  return new Set(tickers).size !== tickers.length;
});

const isWeightValid = computed(() => Math.abs(totalWeight.value - 100) < 0.01);

const isValid = computed(() => {
  return (
    form.name.length > 0 &&
    form.holdings.length >= 1 &&
    form.holdings.length <= 10 &&
    form.holdings.every((holding) => holding.ticker && Number(holding.weight) > 0) &&
    !hasDuplicateTickers.value &&
    isWeightValid.value
  );
});

const weightStatusClass = computed(() => {
  if (isWeightValid.value) {
    return "border-emerald-200 bg-emerald-50 text-emerald-800";
  }

  return "border-red-200 bg-red-50 text-red-800";
});

const weightStatusMessage = computed(() => {
  if (hasDuplicateTickers.value) {
    return "Each stock can only appear once.";
  }

  if (isWeightValid.value) {
    return "Portfolio allocation is valid.";
  }

  return "Weights must sum to 100%.";
});

function hydrateForm(portfolio) {
  if (!portfolio) return;

  form.name = portfolio.name || "";
  form.description = portfolio.description || "";
  form.holdings = portfolio.holdings?.length
    ? portfolio.holdings.map((holding) => ({
        ticker: holding.ticker,
        weight: Number(holding.weight),
      }))
    : [{ ticker: "", weight: 100 }];
}

function addHolding() {
  if (form.holdings.length >= 10) return;

  form.holdings.push({
    ticker: "",
    weight: 0,
  });
}

function removeHolding(index) {
  if (form.holdings.length <= 1) return;

  form.holdings.splice(index, 1);
}

function availableStocksFor(index) {
  const selectedTickers = form.holdings
    .map((holding, holdingIndex) => (holdingIndex === index ? null : holding.ticker))
    .filter(Boolean);

  return props.stocks.filter((stock) => !selectedTickers.includes(stock.ticker));
}

function handleSubmit() {
  if (!isValid.value) return;

  emit("submit", {
    name: form.name,
    description: form.description,
    holdings: form.holdings.map((holding) => ({
      ticker: holding.ticker,
      weight: Number(holding.weight),
    })),
  });
}

watch(
  () => props.initialPortfolio,
  (portfolio) => hydrateForm(portfolio),
  { immediate: true }
);
</script>