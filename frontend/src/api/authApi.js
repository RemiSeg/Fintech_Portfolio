import { http } from "./http";

export const authApi = {
  signup(payload) {
    return http.post("/auth/signup", payload);
  },

  login(payload) {
    return http.post("/auth/login", payload);
  },
};