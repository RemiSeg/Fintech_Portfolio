import { createApp } from "vue";
import { createPinia } from "pinia";
import VueApexCharts from "vue3-apexcharts";

import App from "./App.vue";
import router from "./router";
import { useAuthStore } from "@/stores/authStore";
import "./style.css";

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);
app.use(VueApexCharts);

const authStore = useAuthStore();
authStore.loadFromStorage();

app.mount("#app");