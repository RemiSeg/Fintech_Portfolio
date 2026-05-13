import { defineStore } from "pinia";
import { stocksApi } from "@/api/stocksApi";

export const useStockStore = defineStore("stock", {
  state: () => ({
    stocks: [],
    selectedStock: null,
    stockHistory: [],
    loading: false,
    error: null,
  }),

  getters: {
    sortedStocks: (state) =>
      [...state.stocks].sort((a, b) => a.ticker.localeCompare(b.ticker)),
  },

  actions: {
    async fetchStocks() {
      this.loading = true;
      this.error = null;

      try {
        const response = await stocksApi.getAll();
        this.stocks = response.data;
        return response.data;
      } catch (error) {
        this.error = error.response?.data?.error || "Could not load stocks.";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async fetchStockByTicker(ticker) {
      this.loading = true;
      this.error = null;

      try {
        const response = await stocksApi.getByTicker(ticker);
        this.selectedStock = response.data;
        return response.data;
      } catch (error) {
        this.error = error.response?.data?.error || "Could not load stock.";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async fetchStockHistory(ticker, range = "1Y") {
      this.loading = true;
      this.error = null;

      try {
        const response = await stocksApi.getHistory(ticker, range);
        this.stockHistory = response.data.map((point) => ({
          date: point.date,
          value: point.closePrice,
        }));
        return this.stockHistory;
      } catch (error) {
        this.error =
          error.response?.data?.error || "Could not load stock history.";
        throw error;
      } finally {
        this.loading = false;
      }
    },
  },
});