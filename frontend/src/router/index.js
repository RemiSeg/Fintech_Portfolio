import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

const routes = [
  {
    path: "/",
    redirect: "/dashboard",
  },
  {
    path: "/login",
    name: "login",
    component: () => import("@/pages/LoginPage.vue"),
    meta: { public: true },
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: () => import("@/pages/DashboardPage.vue"),
  },
  {
    path: "/portfolios/new",
    name: "portfolio-create",
    component: () => import("@/pages/PortfolioFormPage.vue"),
  },
  {
    path: "/portfolios/:portfolioId",
    name: "portfolio-detail",
    component: () => import("@/pages/PortfolioDetailPage.vue"),
  },
  {
    path: "/portfolios/:portfolioId/edit",
    name: "portfolio-edit",
    component: () => import("@/pages/PortfolioFormPage.vue"),
  },
  {
    path: "/stocks/:ticker",
    name: "stock-detail",
    component: () => import("@/pages/StockDetailPage.vue"),
  },
  {
    path: "/compare",
    name: "compare",
    component: () => import("@/pages/ComparePage.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to) => {
  const authStore = useAuthStore();

  if (!to.meta.public && !authStore.isAuthenticated) {
    return "/login";
  }

  if (to.name === "login" && authStore.isAuthenticated) {
    return "/dashboard";
  }

  return true;
});

export default router;