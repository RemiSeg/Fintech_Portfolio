<template>
  <form class="space-y-6" @submit.prevent="handleSubmit">
    <div class="grid gap-6 lg:grid-cols-[minmax(0,1fr)_360px]">
      <!-- Main form column -->
      <div class="space-y-6">
        <BaseCard>
          <div class="mb-6">
            <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
              Step 1
            </p>
            <h2 class="mt-1 text-xl font-semibold text-[#1a146b]">
              Portfolio Setup
            </h2>
            <p class="mt-1 text-sm text-gray-600">
              Give your portfolio a clear name and optional investment objective.
            </p>
          </div>

          <div class="space-y-4">
            <div>
              <label class="mb-1 block text-sm font-medium text-gray-700">
                Portfolio name
              </label>
              <input
                v-model.trim="form.name"
                type="text"
                required
                class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2.5 text-sm outline-none transition focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
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
                class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2.5 text-sm outline-none transition focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
                placeholder="Example: Concentrated large-cap technology allocation with long-term growth objective."
              />
            </div>
          </div>
        </BaseCard>

        <BaseCard>
          <div class="mb-6 flex flex-col justify-between gap-4 md:flex-row md:items-start">
            <div>
              <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
                Step 2
              </p>
              <h2 class="mt-1 text-xl font-semibold text-[#1a146b]">
                Holdings Builder
              </h2>
              <p class="mt-1 text-sm text-gray-600">
                Select up to 10 stocks and assign portfolio weights.
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
              class="rounded-xl border border-gray-200 bg-white p-4 transition hover:border-gray-300"
            >
              <div class="mb-3 flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <div class="flex h-8 w-8 items-center justify-center rounded-full bg-indigo-50 text-xs font-semibold text-[#1a146b]">
                    {{ index + 1 }}
                  </div>

                  <div>
                    <p class="text-sm font-semibold text-gray-900">
                      Holding {{ index + 1 }}
                    </p>
                    <p class="text-xs text-gray-500">
                      Choose one stock and its allocation.
                    </p>
                  </div>
                </div>

                <button
                  type="button"
                  :disabled="form.holdings.length <= 1"
                  class="text-sm font-medium text-gray-400 transition hover:text-[#ba1a1a] disabled:cursor-not-allowed disabled:opacity-40"
                  @click="removeHolding(index)"
                >
                  Remove
                </button>
              </div>

              <div class="grid gap-3 md:grid-cols-[minmax(0,1fr)_150px]">
                <div>
                  <label class="mb-1 block text-xs font-semibold uppercase tracking-wide text-gray-500">
                    Stock
                  </label>

                  <select
                    v-model="holding.ticker"
                    required
                    class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2.5 text-sm outline-none transition focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
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
                    Weight %
                  </label>

                  <input
                    v-model.number="holding.weight"
                    type="number"
                    min="0.01"
                    max="100"
                    step="0.01"
                    required
                    class="numeric w-full rounded-lg border border-gray-300 bg-white px-3 py-2.5 text-sm outline-none transition focus:border-[#1a146b] focus:ring-2 focus:ring-[#1a146b]/10"
                    placeholder="25"
                  />
                </div>
              </div>
            </div>
          </div>

          <div class="mt-5 flex flex-col gap-3 rounded-lg bg-gray-50 p-4 md:flex-row md:items-center md:justify-between">
            <div>
              <p class="text-sm font-medium text-gray-900">
                Holdings used: {{ form.holdings.length }}/10
              </p>
              <p class="mt-1 text-xs text-gray-500">
                Each stock can only appear once.
              </p>
            </div>

            <button
              type="button"
              class="text-sm font-semibold text-[#1a146b] hover:underline disabled:cursor-not-allowed disabled:text-gray-400 disabled:no-underline"
              :disabled="form.holdings.length >= 10"
              @click="addHolding"
            >
              + Add another holding
            </button>
          </div>
        </BaseCard>
      </div>

      <!-- Summary column -->
      <aside class="space-y-6 lg:sticky lg:top-24 lg:self-start">
        <BaseCard>
          <div>
            <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
              Live Summary
            </p>
            <h2 class="mt-1 text-xl font-semibold text-[#1a146b]">
              Allocation
            </h2>
          </div>

          <div class="mt-6 rounded-xl border p-5" :class="allocationStatusClass">
            <p class="text-xs font-semibold uppercase tracking-wide">
              Total Weight
            </p>
            <p class="numeric mt-2 text-4xl font-semibold">
              {{ totalWeight.toFixed(2) }}%
            </p>
            <p class="mt-2 text-sm">
              {{ allocationStatusMessage }}
            </p>
          </div>

          <div class="mt-6 space-y-4">
            <div
              v-for="holding in selectedHoldings"
              :key="holding.ticker"
              class="space-y-1"
            >
              <div class="flex items-center justify-between gap-3 text-sm">
                <span class="font-medium text-gray-800">
                  {{ holding.ticker }}
                </span>
                <span class="numeric text-gray-500">
                  {{ Number(holding.weight || 0).toFixed(2) }}%
                </span>
              </div>

              <div class="h-2 overflow-hidden rounded-full bg-gray-100">
                <div
                  class="h-full rounded-full bg-[#1a146b]"
                  :style="{ width: `${Math.min(Number(holding.weight || 0), 100)}%` }"
                />
              </div>
            </div>

            <div
              v-if="selectedHoldings.length === 0"
              class="rounded-lg border border-dashed border-gray-300 p-4 text-center"
            >
              <p class="text-sm text-gray-500">
                Select a stock to preview allocation.
              </p>
            </div>
          </div>
        </BaseCard>

        <BaseCard>
          <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
            Validation
          </p>

          <p
            class="mt-3 text-sm font-semibold"
            :class="isValid ? 'text-[#006c4a]' : 'text-[#ba1a1a]'"
          >
            {{ isValid ? "Portfolio is ready to save." : "Portfolio is not ready yet." }}
          </p>

          <ul class="mt-4 space-y-2 text-sm text-gray-600">
            <li
              v-for="item in validationChecklist"
              :key="item.label"
              class="flex items-start gap-2"
            >
              <span
                class="mt-0.5 flex h-5 w-5 items-center justify-center rounded-full text-xs font-bold"
                :class="item.valid ? 'bg-emerald-50 text-[#006c4a]' : 'bg-red-50 text-[#ba1a1a]'"
              >
                {{ item.valid ? "✓" : "!" }}
              </span>
              <span>{{ item.label }}</span>
            </li>
          </ul>
        </BaseCard>
      </aside>
    </div>

    <ErrorState v-if="errorMessage" :message="errorMessage" />

    <!-- Bottom action bar -->
    <div class="sticky bottom-16 z-10 rounded-xl border border-gray-200 bg-white/95 p-4 shadow-sm backdrop-blur md:bottom-4">
      <div class="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <div>
          <p class="text-sm font-medium text-gray-900">
            {{ isValid ? "Ready to save." : "Complete the setup to continue." }}
          </p>
          <p class="mt-1 text-xs text-gray-500">
            The backend will still validate the portfolio before saving.
          </p>
        </div>

        <div class="flex flex-col-reverse gap-3 sm:flex-row">
          <BaseButton type="button" variant="secondary" @click="router.push(cancelTarget)">
            Cancel
          </BaseButton>

          <BaseButton type="submit" :disabled="submitting || !isValid">
            {{ submitting ? "Saving..." : submitLabel }}
          </BaseButton>
        </div>
      </div>
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
  cancelTarget: {
    type: String,
    default: "/dashboard",
  },
});

const emit = defineEmits(["submit"]);
const router = useRouter();

const form = reactive({
  name: "",
  description: "",
  holdings: [{ ticker: "", weight: 100 }],
});

const selectedHoldings = computed(() =>
  form.holdings.filter((holding) => holding.ticker)
);

const totalWeight = computed(() =>
  form.holdings.reduce((sum, holding) => sum + Number(holding.weight || 0), 0)
);

const hasDuplicateTickers = computed(() => {
  const tickers = form.holdings
    .map((holding) => holding.ticker)
    .filter(Boolean);

  return new Set(tickers).size !== tickers.length;
});

const hasValidName = computed(() => form.name.trim().length > 0);

const hasValidHoldings = computed(() =>
  form.holdings.every((holding) => holding.ticker && Number(holding.weight) > 0)
);

const isWeightValid = computed(() => Math.abs(totalWeight.value - 100) < 0.01);

const isValid = computed(() => {
  return (
    hasValidName.value &&
    form.holdings.length >= 1 &&
    form.holdings.length <= 10 &&
    hasValidHoldings.value &&
    !hasDuplicateTickers.value &&
    isWeightValid.value
  );
});

const allocationStatusClass = computed(() => {
  if (hasDuplicateTickers.value) {
    return "border-red-200 bg-red-50 text-red-800";
  }

  if (isWeightValid.value) {
    return "border-emerald-200 bg-emerald-50 text-emerald-800";
  }

  return "border-red-200 bg-red-50 text-red-800";
});

const allocationStatusMessage = computed(() => {
  if (hasDuplicateTickers.value) {
    return "Duplicate stocks are not allowed.";
  }

  if (isWeightValid.value) {
    return "Weights are correctly allocated.";
  }

  if (totalWeight.value < 100) {
    return `Missing ${(100 - totalWeight.value).toFixed(2)}%.`;
  }

  return `Overallocated by ${(totalWeight.value - 100).toFixed(2)}%.`;
});

const validationChecklist = computed(() => [
  {
    label: "Portfolio name is provided",
    valid: hasValidName.value,
  },
  {
    label: "Every holding has a selected stock and positive weight",
    valid: hasValidHoldings.value,
  },
  {
    label: "No duplicate stocks",
    valid: !hasDuplicateTickers.value,
  },
  {
    label: "Total allocation equals 100%",
    valid: isWeightValid.value,
  },
  {
    label: "Portfolio has 10 holdings or fewer",
    valid: form.holdings.length <= 10,
  },
]);

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