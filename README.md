# Fintech Portfolio Visualization Backend

A Java Spring Boot backend for a fintech portfolio visualization platform that allows users to create portfolios, add stocks from a predefined universe, calculate portfolio performance metrics, compare portfolios and stocks, and retrieve real market data through external financial APIs.

This project was built as a personal fintech engineering project to demonstrate backend architecture, API design, financial data processing, portfolio analytics, and clean service-oriented development.

---

## 1. Project Purpose

The goal of this project is to build the backend foundation for a portfolio visualization application.

The application is not a trading platform. It does not execute real trades, store brokerage accounts, or manage real financial assets. Instead, it focuses on portfolio construction, visualization, comparison, and financial analytics.

Users can:

- Create an account and log in
- Create up to 3 personal portfolios
- Add up to 10 stocks per portfolio
- Select stocks from a predefined stock universe stored in Supabase
- View real stock information and historical price data
- Calculate portfolio-level metrics
- Compare portfolios against other portfolios or individual stocks
- Use the backend through a REST API consumed by a Vue frontend

The project is designed to show how a clean backend can support a real fintech-style product with proper separation of concerns, external API integration, persistence, and analytics.

---

## 2. Core Features

### Authentication

The backend provides simple authentication for users.

Current authentication functionality includes:

- User signup
- User login
- User identity stored in Supabase
- User-specific portfolio ownership

This is intentionally lightweight for the scope of the personal project. The goal is to support portfolio ownership and separate user data cleanly.

---

### Stock Universe

The application uses a predefined list of stocks stored in Supabase.

This design choice keeps the application controlled and reliable:

- Users cannot add random invalid tickers
- The frontend can display a clean list of supported stocks
- Backend validation is simpler and safer
- Portfolio analytics are based on known symbols

The backend exposes endpoints to:

- List available stocks
- Retrieve details for one stock
- Retrieve historical price data for one stock

Historical prices are retrieved from Yahoo Finance rather than permanently stored in the database.

---

### Portfolio Management

Users can create and manage portfolios.

Business rules:

- Each user can create a maximum of 3 portfolios
- Each portfolio can contain a maximum of 10 stocks
- Each stock in a portfolio can have an allocation or quantity depending on the backend model
- Portfolios are persisted in Supabase
- Portfolio ownership is enforced through the backend service layer

Portfolio operations include:

- Create portfolio
- Read portfolio
- List user portfolios
- Update portfolio
- Delete portfolio
- Add stocks to a portfolio
- Remove stocks from a portfolio

The portfolio module is one of the central parts of the backend because it connects user data, stock data, Supabase persistence, and financial analytics.

---

### Real Market Data

The backend retrieves stock price data from Yahoo Finance.

This allows the application to calculate real financial metrics without maintaining a local market-data database.

The backend uses Yahoo Finance for:

- Historical stock prices
- Stock return calculations
- Portfolio performance calculations
- Single-stock comparison
- Portfolio comparison

This keeps the project lightweight while still making the analytics realistic.

---

### Portfolio Metrics

The backend calculates key portfolio and stock metrics.

Supported metrics include:

- Total return
- Volatility
- Maximum drawdown

These metrics are useful because they show both return and risk.

#### Total Return

Total return measures how much the value of a stock or portfolio changed over a period.

It answers:

> “How much did this investment grow or decline?”

#### Volatility

Volatility measures how much returns fluctuate over time.

It answers:

> “How risky or unstable was this investment?”

#### Maximum Drawdown

Maximum drawdown measures the largest peak-to-trough decline.

It answers:

> “What was the worst loss from a previous high?”

Together, these metrics provide a simple but meaningful view of performance and risk.

---

### Portfolio and Stock Comparison

The comparison service allows the backend to compare:

- Portfolio vs portfolio
- Portfolio vs stock
- Stock vs stock

The comparison module uses the same market-data and analytics logic as the portfolio service.

This feature makes the application more useful from a product perspective because users can evaluate different strategies, baskets of stocks, or individual assets.

---

## 3. Backend Architecture

The backend follows a layered Spring Boot architecture.

The main layers are:

```text
Controllers
    ↓
Services
    ↓
Repositories / External API Clients
    ↓
Supabase / Yahoo Finance