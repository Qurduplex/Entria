/**
 * Based on timestamp decides shown text
 */
export function formatTimestamp(ts) {
  const date = new Date(ts);
  const now = new Date();

  const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const startOfYesterday = new Date(startOfToday - 86400000);

  const hh = String(date.getHours()).padStart(2, "0");
  const mm = String(date.getMinutes()).padStart(2, "0");
  const time = `${hh}:${mm}`;

  if (date >= startOfToday) {
    return `dziś ${time}`;
  }

  if (date >= startOfYesterday) {
    return `wczoraj ${time}`;
  }

  const diffDays = Math.floor((startOfToday - date) / 86400000);

  if (diffDays < 30) {
    return `${diffDays} dni temu`;
  }

  const dd = String(date.getDate()).padStart(2, "0");
  const mo = String(date.getMonth() + 1).padStart(2, "0");
  return `${dd}.${mo}.${date.getFullYear()}`;
}