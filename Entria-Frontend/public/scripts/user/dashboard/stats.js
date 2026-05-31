export function loadUserDashboardStats() {
  const data = {
    apps: {
      count: 4,
      sub: "3 aktywne",
    },
    alerts: {
      count: 10,
    },
    logins: {
      count: 47,
      sub: "ostatnie 30 dni",
    },
  };

  const appsCount = document.getElementById("stats-apps-count");
  const appsSub = document.getElementById("stats-apps-sub");
  const alertsCount = document.getElementById("stats-alerts-count");
  const alertsSub = document.getElementById("stats-alerts-sub");
  const loginsCount = document.getElementById("stats-logins-count");
  const loginsSub = document.getElementById("stats-logins-sub");

  if (appsCount) appsCount.textContent = data.apps.count;
  if (appsSub) appsSub.textContent = data.apps.sub;
  if (alertsCount) alertsCount.textContent = data.alerts.count;
  if (alertsSub) alertsSub.textContent = data.alerts.count === 1 ? "wymaga uwagi" : data.alerts.count === 0 ? "brak alertów" : "wymagają uwagi";
  if (loginsCount) loginsCount.textContent = data.logins.count;
  if (loginsSub) loginsSub.textContent = data.logins.sub;
}