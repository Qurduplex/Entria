import { loadSecurityAlerts } from "./dashboard/alert.js";
import { loadDashboardStats } from "./dashboard/stats.js";
import { loadLoginsChart } from "./dashboard/chart.js";
import { loadAppsTable } from "./dashboard/appstable.js";

async function loadComponent(id, path) {
  const element = document.getElementById(id);
  if (!element) return;

  const res = await fetch(path);
  element.innerHTML = await res.text();
}

export async function initDeveloperDashboard() {

  await loadComponent(
    "dashboard-alerts",
    "../../pages/developer/fragments/dashboard/dashboardAlerts.html"

  );
  loadSecurityAlerts();

  await loadComponent(
    "dashboard-stats",
    "../../pages/developer/fragments/dashboard/dashboardStats.html"
  );
  loadDashboardStats();

  await loadComponent(
    "dashboard-chart",
    "../../pages/developer/fragments/dashboard/dashboardChart.html"
  );
  loadLoginsChart();

  await loadComponent(
    "dashboard-apps-table",
    "../../pages/developer/fragments/dashboard/dashboardAppsTable.html"
  );
  loadAppsTable();

}