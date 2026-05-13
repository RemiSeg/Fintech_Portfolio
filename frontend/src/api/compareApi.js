import { http } from "./http";

export const compareApi = {
  compare(payload) {
    return http.post("/compare", payload);
  },
};