import { http } from "./http";

export const stocksApi = {
  getAll() {
    return http.get("/stocks");
  },

  getByTicker(ticker) {
    return http.get(`/stocks/${ticker}`);
  },

  getHistory(ticker, range = "1Y") {
    return http.get(`/stocks/${ticker}/history`, {
      params: { range },
    });
  },
};