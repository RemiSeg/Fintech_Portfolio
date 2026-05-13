<template>
  <AppLayout>
    <div class="space-y-8">
      <section>
        <button
          type="button"
          class="mb-3 text-sm font-medium text-gray-600 hover:text-[#1a146b]"
          @click="router.push('/dashboard')"
        >
          ← Back to dashboard
        </button>

        <h1 class="text-3xl font-semibold tracking-tight text-[#1a146b]">
          {{ isEditMode ? "Edit Portfolio" : "Create Portfolio" }}
        </h1>

        <p class="mt-2 max-w-3xl text-sm text-gray-600">
          Build a portfolio using the backend-supported stock universe. You can create up to 3 portfolios, each with up to 10 holdings.
        </p>
      </section>

      <ErrorState v-if="pageError" :message="pageError" />

      <LoadingState
        v-if="loading"
        message="Loading portfolio form..."
      />

      <PortfolioForm
        v-else
        :initial-portfolio="initialPortfolio"
        :stocks="stockStore.sortedStocks"
        :submitting="submitting"
        :error-message="formError"
        :submit-label="isEditMode ? 'Update portfolio' : 'Create portfolio'"
        @submit="handleSubmit"
      />
    </div>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import AppLayout from "@/layouts/AppLayout.vue";
import ErrorState from "@/components/common/ErrorState.vue";
import LoadingState from "@/components/common/LoadingState.vue";
import PortfolioForm from "@/components/portfolios/PortfolioForm.vue";

import { useAuthStore } from "@/stores/authStore";
import { usePortfolioStore } from "@/stores/portfolioStore";
import { useStockStore } from "@/stores/stockStore";

const route = useRoute();
const router = useRouter();

const authStore = useAuthStore();
const portfolioStore = usePortfolioStore();
const stockStore = useStockStore();

const loading = ref(false);
const submitting = ref(false);
const pageError = ref("");
const formError = ref("");

const isEditMode = computed(() => route.name === "portfolio-edit");

const initialPortfolio = computed(() =>
  isEditMode.value ? portfolioStore.selectedPortfolio : null
);

async function loadFormData() {
  loading.value = true;
  pageError.value = "";

  try {
    await stockStore.fetchStocks();

    if (isEditMode.value) {
      await portfolioStore.fetchPortfolioById(route.params.portfolioId);

      if (portfolioStore.selectedPortfolio?.isDefault) {
        pageError.value = "Default portfolios cannot be edited.";
      }
    }
  } catch (error) {
    pageError.value =
      error.response?.data?.error || "Could not load portfolio form data.";
  } finally {
    loading.value = false;
  }
}

async function handleSubmit(formPayload) {
  submitting.value = true;
  formError.value = "";

  try {
    let savedPortfolio;

    if (isEditMode.value) {
      savedPortfolio = await portfolioStore.updatePortfolio(
        route.params.portfolioId,
        formPayload
      );
    } else {
      savedPortfolio = await portfolioStore.createPortfolio({
        userId: authStore.userId,
        ...formPayload,
      });
    }

    router.push(`/portfolios/${savedPortfolio.id}`);
  } catch (error) {
    formError.value =
      error.response?.data?.error || "Could not save portfolio.";
  } finally {
    submitting.value = false;
  }
}

onMounted(loadFormData);
</script>