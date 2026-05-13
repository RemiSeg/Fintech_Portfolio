export function formatPercent(value) {
  if (value === null || value === undefined || Number.isNaN(Number(value))) {
    return "—";
  }

  const number = Number(value);
  const sign = number > 0 ? "+" : "";

  return `${sign}${number.toFixed(2)}%`;
}

export function formatNumber(value, decimals = 2) {
  if (value === null || value === undefined || Number.isNaN(Number(value))) {
    return "—";
  }

  return Number(value).toFixed(decimals);
}

export function getMetricTone(value) {
  const number = Number(value);

  if (Number.isNaN(number)) return "neutral";
  if (number > 0) return "positive";
  if (number < 0) return "negative";

  return "neutral";
}