import { defineStore } from "pinia";
import { portfoliosApi } from "@/api/portfoliosApi";

export const usePortfolioStore = defineStore("portfolio", {
  state: () => ({
    defaultPortfolios: [],
    userPortfolios: [],
    selectedPortfolio: null,
    loading: false,
    error: null,
  }),

  getters: {
    allPortfolioSummaries: (state) => [
      ...state.defaultPortfolios,
      ...state.userPortfolios,
    ],
  },

  actions: {
    async fetchDefaultPortfolios() {
      this.loading = true;
      this.error = null;

      try {
        const response = await portfoliosApi.getDefault();
        this.defaultPortfolios = response.data;
        return response.data;
      } catch (error) {
        this.error =
          error.response?.data?.error || "Could not load default portfolios.";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async fetchUserPortfolios(userId) {
      if (!userId) return [];

      this.loading = true;
      this.error = null;

      try {
        const response = await portfoliosApi.getByUser(userId);
        this.userPortfolios = response.data;
        return response.data;
      } catch (error) {
        this.error =
          error.response?.data?.error || "Could not load user portfolios.";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async fetchPortfolioById(portfolioId) {
      this.loading = true;
      this.error = null;

      try {
        const response = await portfoliosApi.getById(portfolioId);
        this.selectedPortfolio = response.data;
        return response.data;
      } catch (error) {
        this.error =
          error.response?.data?.error || "Could not load portfolio details.";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async createPortfolio(payload) {
      this.loading = true;
      this.error = null;

      try {
        const response = await portfoliosApi.create(payload);
        this.userPortfolios.push({
          id: response.data.id,
          name: response.data.name,
          description: response.data.description,
          isDefault: response.data.isDefault,
          numberOfHoldings: response.data.holdings?.length || 0,
        });
        return response.data;
      } catch (error) {
        this.error =
          error.response?.data?.error || "Could not create portfolio.";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updatePortfolio(portfolioId, payload) {
      this.loading = true;
      this.error = null;

      try {
        const response = await portfoliosApi.update(portfolioId, payload);
        this.selectedPortfolio = response.data;

        this.userPortfolios = this.userPortfolios.map((portfolio) =>
          portfolio.id === portfolioId
            ? {
                id: response.data.id,
                name: response.data.name,
                description: response.data.description,
                isDefault: response.data.isDefault,
                numberOfHoldings: response.data.holdings?.length || 0,
              }
            : portfolio
        );

        return response.data;
      } catch (error) {
        this.error =
          error.response?.data?.error || "Could not update portfolio.";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async deletePortfolio(portfolioId) {
      this.loading = true;
      this.error = null;

      try {
        await portfoliosApi.remove(portfolioId);
        this.userPortfolios = this.userPortfolios.filter(
          (portfolio) => portfolio.id !== portfolioId
        );

        if (this.selectedPortfolio?.id === portfolioId) {
          this.selectedPortfolio = null;
        }
      } catch (error) {
        this.error =
          error.response?.data?.error || "Could not delete portfolio.";
        throw error;
      } finally {
        this.loading = false;
      }
    },
  },
});