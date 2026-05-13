# Portfolio Visualizer Frontend

Clean Vue.js frontend for a fintech portfolio visualization system.

This frontend connects to a completed Java Spring Boot backend and provides a simple, professional interface for portfolio creation, stock visualization, and portfolio/stock comparison.

The goal of the frontend is not to build a trading platform. It is a visualization and analytics layer for exploring portfolios, stock history, and financial metrics.

---

## Project Purpose

Portfolio Visualizer is a personal fintech engineering project designed to demonstrate:

- Clean backend/frontend separation
- Real REST API integration
- Financial data visualization
- Portfolio analytics
- Maintainable Vue architecture
- Professional UI/UX design
- Practical understanding of portfolio metrics

The frontend consumes only backend-supported data. There is no mock financial data in the production UI.

---

## System Context

The backend is already implemented and running separately.

Backend stack:

- Java Spring Boot
- REST API
- Supabase PostgreSQL via REST
- Yahoo Finance API for real stock prices
- Controller → Service → Repository architecture

Frontend stack:

- Vue 3
- Vite
- Vue Router
- Pinia
- Axios
- ApexCharts
- Tailwind CSS

---

## Core Features

The frontend allows users to:

1. Sign up and log in
2. View default portfolios
3. View user-created portfolios
4. Create up to 3 portfolios
5. Add up to 10 stocks per portfolio
6. Assign portfolio weights that sum to 100%
7. View portfolio performance over time
8. View portfolio metrics:
   - Total return
   - Volatility
   - Max drawdown
9. View supported stocks
10. View stock historical price charts
11. Compare:
   - Portfolio vs portfolio
   - Stock vs stock
   - Portfolio vs stock

---

## Important Product Scope

This is not a trading application.

The system does not support:

- Real trading
- Buy/sell orders
- Transactions
- Cash balances
- Broker integrations
- Real-money execution

The application is focused only on:

- Portfolio creation
- Portfolio visualization
- Stock visualization
- Portfolio and stock comparison
- Financial analytics

---

## Backend API Used

The frontend connects to the following backend endpoints.

### Auth

```http
POST /auth/signup
POST /auth/login