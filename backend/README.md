# Fintech Portfolio Backend

## Overview

A clean, modular Spring Boot API for a portfolio visualization system. This backend enables users to create and manage investment portfolios, compare performance metrics, and visualize historical returns—all in a **read-only finance simulation environment** with no real transactions or trading capabilities.

### Key Capabilities
- Create and manage multiple portfolios
- Compare portfolios and stocks side-by-side
- Visualize performance over time
- Compute key financial metrics (return, volatility, drawdown)
- Fetch real-time stock data via Yahoo Finance

---

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 21 |
| Spring Boot | 3.5 |
| Database | Supabase (PostgreSQL) |
| External API | Yahoo Finance |
| Build Tool | Maven |

---

## Architecture

```
┌─────────────┐
│ Controller  │ (REST endpoints, validation)
└──────┬──────┘
       │
┌──────▼──────┐
│  Service    │ (Business logic, metrics)
└──────┬──────┘
       │
┌──────▼──────────────┐
│   Repository        │ (Supabase queries)
└──────┬──────────────┘
       │
   ┌───┴──────────────────────┐
   │                          │
┌──▼────────────┐   ┌────────▼──┐
│  Supabase DB  │   │ Yahoo API  │
│  (PostgreSQL) │   │  (Prices)  │
└───────────────┘   └────────────┘
```

### Layers

**Controller**
- REST endpoint definitions
- Input validation
- Request/response handling

**Service**
- Business logic & portfolio rules
- Financial metrics computation
- Data orchestration

**Repository**
- Supabase REST API integration
- Query execution

**Clients**
- `SupabaseClient`: Database operations
- `YahooFinanceClient`: Stock price fetching

### Database Schema

**Tables:**
- `users` - User accounts
- `portfolios` - Portfolio metadata
- `portfolio_holdings` - Stock holdings per portfolio
- `stocks` - Stock reference data

**Constraints:**
- Max 3 portfolios per user
- Max 10 stocks per portfolio
- Portfolio weights must sum to 100%

### External Data

Stock prices are fetched **dynamically** from Yahoo Finance:
```
GET https://query1.finance.yahoo.com/v8/finance/chart/{ticker}
```
⚠️ **Note:** Price data is not persisted in the database.

---

## API Endpoints

### Authentication
```
POST   /auth/signup              Register new user
POST   /auth/login               Login user
```

### Stocks
```
GET    /stocks                   List all stocks
GET    /stocks/{ticker}          Get stock details
GET    /stocks/{ticker}/history  Get historical data (range: 1M, 3M, 1Y, 5Y)
```

### Portfolios
```
GET    /portfolios/default       Get default portfolio
GET    /portfolios/user/{userId} List user portfolios
GET    /portfolios/{portfolioId} Get portfolio details
POST   /portfolios               Create new portfolio
PUT    /portfolios/{portfolioId} Update portfolio
DELETE /portfolios/{portfolioId} Delete portfolio
```

### Comparison
```
POST   /compare                  Compare portfolios/stocks
```
Supports comparisons:
- Portfolio vs Portfolio
- Stock vs Stock
- Portfolio vs Stock

### Metrics
All metrics are computed **dynamically** (not stored):
- **Total Return** (%)
- **Volatility** (annualized standard deviation)
- **Max Drawdown** (%)

---

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+
- Supabase account with PostgreSQL database

### Installation

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd Fintech_Portfolio/backend
   ```

2. **Configure environment variables**
   Create a `.env` file in the root directory:
   ```bash
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_KEY=your_service_role_key
   ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Verify it's running**
   ```bash
   curl http://localhost:8080/health
   ```

---

## Testing

Run all tests:
```bash
./mvnw test
```

### Test Coverage
- Service layer logic
- Input validation
- Financial metrics computation
- Integration tests

---

## Design Philosophy

✅ **Minimal Database** - Only metadata stored; prices fetched on-demand  
✅ **API-Driven** - Yahoo Finance as single source of truth  
✅ **Separation of Concerns** - Clear controller → service → repository flow  
✅ **Production-Ready** - Enterprise-grade structure and patterns  
✅ **Portfolio-First** - Core logic centered on portfolio management  

---

## Future Improvements

- [ ] Redis caching layer for stock data
- [ ] JWT authentication tokens
- [ ] Pagination & rate limiting
- [ ] Frontend integration
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Docker containerization
- [ ] Performance analytics dashboard

---

## License

[Add your license here]

## Contact

For questions or issues, please open a GitHub issue.