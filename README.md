RemiSeg: make this a single markdown file to copy paste as a read me ::


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
Each layer has a specific responsibility.
4. Architecture Overview
Frontend Vue App
       |
       | REST API
       v
Spring Boot Backend
       |
       |--- Auth Controller
       |--- Stock Controller
       |--- Portfolio Controller
       |--- Compare Controller
       |
       v
Service Layer
       |
       |--- Auth Service
       |--- Stock Service
       |--- Portfolio Service
       |--- Metrics Service
       |--- Compare Service
       |
       v
Data Access Layer
       |
       |--- Supabase Repositories
       |--- Yahoo Finance Client
       |
       v
External Systems
       |
       |--- Supabase Database
       |--- Yahoo Finance Market Data

---

## Project Purpose

Portfolio Visualizer is a personal fintech engineering project designed to demonstrate:

- Clean backend/frontend separation
- Real REST API integration
- Financial data visualization
- Portfolio analytics
- Maintainable architecture
- Professional UI/UX design
- Practical understanding of portfolio metrics

**Important:** This is not a trading application. It does not execute real trades, store brokerage accounts, or manage real financial assets. Instead, it focuses on portfolio construction, visualization, comparison, and financial analytics.

---

## Core Features

Users can:

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

## System Architecture

```
Frontend Vue App
    ↓ (REST API)
    v
Spring Boot Backend
    |
    |--- Auth Controller
    |--- Stock Controller
    |--- Portfolio Controller
    |--- Compare Controller
    |
    v
Service Layer
    |
    |--- Auth Service
    |--- Stock Service
    |--- Portfolio Service
    |--- Metrics Service
    |--- Compare Service
    |
    v
Data Access Layer
    |
    |--- Supabase Repositories
    |--- Yahoo Finance Client
    |
    v
External Systems
    |
    |--- Supabase Database
    |--- Yahoo Finance Market Data
```

---

## Backend Stack

- Java Spring Boot
- REST API
- Supabase PostgreSQL
- Yahoo Finance API for real stock prices
- Controller → Service → Repository architecture

### Backend API Endpoints

#### Auth
```http
POST /auth/signup
POST /auth/login
```

#### Stocks
```http
GET /stocks
GET /stocks/{ticker}
GET /stocks/{ticker}/history?range=1M|3M|6M|1Y|ALL
```

#### Portfolios
```http
GET /portfolios/default
GET /portfolios/user/{userId}
GET /portfolios/{portfolioId}
POST /portfolios
PUT /portfolios/{portfolioId}
DELETE /portfolios/{portfolioId}
```

#### Comparison
```http
POST /compare
```

---

## Frontend Stack

- Vue 3
- Vite
- Vue Router
- Pinia (state management)
- Axios (HTTP client)
- ApexCharts (data visualization)
- Tailwind CSS

### Frontend Architecture

```
frontend/
├── src/
│   ├── api/
│   │   ├── http.js
│   │   ├── authApi.js
│   │   ├── stocksApi.js
│   │   ├── portfoliosApi.js
│   │   └── compareApi.js
│   │
│   ├── router/
│   │   └── index.js
│   │
│   ├── stores/
│   │   ├── authStore.js
│   │   ├── portfolioStore.js
│   │   └── stockStore.js
│   │
│   ├── layouts/
│   │   └── AppLayout.vue
│   │
│   ├── pages/
│   │   ├── LoginPage.vue
│   │   ├── DashboardPage.vue
│   │   ├── PortfolioFormPage.vue
│   │   ├── PortfolioDetailPage.vue
│   │   ├── StocksPage.vue
│   │   ├── StockDetailPage.vue
│   │   └── ComparePage.vue
│   │
│   ├── components/
│   │   ├── layout/
│   │   │   ├── Sidebar.vue
│   │   │   └── Topbar.vue
│   │   │
│   │   ├── common/
│   │   │   ├── BaseButton.vue
│   │   │   ├── BaseCard.vue
│   │   │   ├── ErrorState.vue
│   │   │   ├── LoadingState.vue
│   │   │   ├── MetricCard.vue
│   │   │   └── MetricsComparisonTable.vue
│   │   │
│   │   ├── charts/
│   │   │   ├── PerformanceLineChart.vue
│   │   │   ├── StockPriceChart.vue
│   │   │   └── ComparisonLineChart.vue
│   │   │
│   │   ├── portfolios/
│   │   │   ├── PortfolioCard.vue
│   │   │   ├── PortfolioForm.vue
│   │   │   └── HoldingsTable.vue
│   │   │
│   │   └── stocks/
│   │       └── StockSummaryCard.vue
│   │
│   ├── utils/
│   │   └── formatters.js
│   │
│   ├── App.vue
│   ├── main.js
│   └── style.css
```

---

## Key Design Decisions

### Separation of Concerns

Each layer has one clear responsibility:

- **Controllers** handle HTTP requests and responses
- **Services** contain business logic and enforce rules
- **Repositories** handle database persistence
- **Clients** manage external API integration

### Real Market Data

The backend retrieves stock price data from Yahoo Finance rather than maintaining a local database. This keeps the project lightweight while making analytics realistic.

### Predefined Stock Universe

Users can only add stocks from a predefined list stored in Supabase. This:
- Prevents invalid ticker additions
- Simplifies validation
- Ensures data quality
- Makes the frontend experience clean

### Portfolio Metrics

The system calculates three key metrics:

- **Total Return**: How much the investment grew or declined
- **Volatility**: How much returns fluctuate (risk measure)
- **Maximum Drawdown**: The largest peak-to-trough decline

---

## Business Rules

### Authentication & Users
- Simple signup and login
- User identity stored in Supabase
- User-specific portfolio ownership

### Portfolio Constraints
- Maximum 3 portfolios per user
- Maximum 10 stocks per portfolio
- Portfolio weights must sum to 100%
- Stocks must come from predefined universe

### Stock Management
- Predefined stock universe in Supabase
- Real historical data from Yahoo Finance
- Support for multiple time ranges (1M, 3M, 6M, 1Y, ALL)

---

## Data Flow Examples

### User Creates a Portfolio

1. Frontend sends `POST /portfolios`
2. Backend validates user authentication
3. Service checks portfolio limit (< 3)
4. Repository creates portfolio in Supabase
5. Backend returns created portfolio

### User Adds Stock to Portfolio

1. Frontend sends `POST /portfolios/{id}/stocks`
2. Backend verifies portfolio ownership
3. Service checks stock limit (< 10 per portfolio)
4. Service validates stock exists in universe
5. Repository saves holding in Supabase
6. Backend returns updated portfolio

### User Views Portfolio Metrics

1. Frontend requests portfolio analytics
2. Service loads portfolio holdings from Supabase
3. Client retrieves historical prices from Yahoo Finance
4. Metrics service calculates return, volatility, max drawdown
5. Backend returns portfolio with metrics and performance data

### User Compares Two Portfolios

1. Frontend sends comparison request
2. Backend identifies comparison type
3. Service loads portfolio data from Supabase
4. Client retrieves historical prices
5. Metrics service calculates metrics for each portfolio
6. Backend returns structured comparison response

---

## What This Project Demonstrates

- Clean, layered backend architecture
- REST API design and integration
- Real database persistence (Supabase)
- External API integration (Yahoo Finance)
- Financial data processing and analytics
- Business rule enforcement
- Frontend-ready service design
- Vue 3 component architecture
- State management with Pinia
- Professional UI/UX with Tailwind CSS
- Full-stack product thinking

---

## Scope

### What This Application Does

- Portfolio creation and management
- Stock visualization and analytics
- Portfolio and stock comparison
- Real-time financial metrics
- Professional analytics dashboard

### What This Application Does NOT Do

- Real money trading
- Buy/sell orders or transactions
- Broker integration
- Cash balance management
- Production-grade risk management
- Real-time streaming data

---

## Running the Project

### Backend Prerequisites

- Java 17+
- Maven
- Supabase project
- Internet connection for Yahoo Finance

### Backend Setup

```bash
# Install dependencies
mvn clean install

# Set environment variables
export SUPABASE_URL=your_supabase_project_url
export SUPABASE_KEY=your_supabase_key

# Run backend
mvn spring-boot:run

# Backend runs on http://localhost:8080
```

### Frontend Prerequisites

- Node.js 16+
- npm or yarn

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev

# Frontend runs on http://localhost:5173
```

---

## Project Status

### Backend
Functional core implementation  
Authentication system   
Portfolio CRUD operations  
Stock universe management  
Yahoo Finance integration  
Portfolio metrics calculation  
Comparison service  
REST API ready for frontend  

### Frontend
Vue 3 architecture  
Component hierarchy  
State management (Pinia)  
API integration layer  
Chart visualizations  
Portfolio management UI  
Comparison interface  

---

## Technology Stack

**Backend**
- Java
- Spring Boot
- Maven
- REST APIs

**Frontend**
- Vue 3
- Vite
- Vue Router
- Pinia
- Axios
- ApexCharts
- Tailwind CSS

**Database & Data**
- Supabase (PostgreSQL)
- Yahoo Finance API

---

## Why This Project Matters

This is more than basic CRUD development. It demonstrates the ability to:

- Design a complete backend system
- Integrate external financial data sources
- Build meaningful analytics functionality
- Create a professional REST API
- Combine backend and frontend into a cohesive product
- Understand financial domain concepts
- Build clean, maintainable architecture

From a recruiter perspective, this project is valuable because it connects software engineering with fintech reasoning—especially relevant for roles in:

- Backend software engineering
- Fintech engineering
- Full-stack development
- API design and integration
- Data-driven applications

---

## Engineering Principles

**Separation of Concerns** – Each layer has one responsibility  
**Business Logic in Services** – Controllers stay thin  
**External Data Isolation** – Market data logic is modular  
**Database Simplicity** – Store state, not history  
**Realistic Scope** – Complex enough to show depth, focused enough to understand  

---

## Future Improvements

- JWT-based authentication
- Advanced metrics (Sharpe ratio, Beta, Correlation)
- Sector allocation analysis
- Portfolio optimization algorithms
- Benchmark comparison
- Watchlists
- Performance caching
- Docker deployment
- CI/CD pipeline
- Production deployment

---

**Status:** Full-stack implementation ready for deployment. Backend serves REST API. Frontend consumes API and provides professional portfolio visualization interface.
