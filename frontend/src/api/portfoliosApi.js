import { http } from "./http";

export const portfoliosApi = {
  getDefault() {
    return http.get("/portfolios/default");
  },

  getByUser(userId) {
    return http.get(`/portfolios/user/${userId}`);
  },

  getById(portfolioId) {
    return http.get(`/portfolios/${portfolioId}`);
  },

  create(payload) {
    return http.post("/portfolios", payload);
  },

  update(portfolioId, payload) {
    return http.put(`/portfolios/${portfolioId}`, payload);
  },

  remove(portfolioId) {
    return http.delete(`/portfolios/${portfolioId}`);
  },
};