import { loadUserDashboardStats } from "./dashboard/stats.js";
import { loadUserProfile } from "./dashboard/profile.js";
import { loadRecentLogins } from "./dashboard/recentLogins.js";
import { loadSecurityAlerts } from "./dashboard/securityAlert.js";

async function loadComponent(id, path) {
  const element = document.getElementById(id);
  if (!element) return;
  const res = await fetch(path);
  element.innerHTML = await res.text();
}

export async function initUserDashboard() {
  const base = "../../pages/user/fragments/dashboard";

  await loadComponent("dashboard-stats", `${base}/dashboardStats.html`);
  loadUserDashboardStats();

  await loadComponent(
    "dashboard-profile",
    `${base}/dashboardProfile.html`,
  );
  loadUserProfile();

  await loadComponent(
    "dashboard-recent-logins",
    `${base}/dashboardRecentLogin.html`,
  );
  loadRecentLogins();

   await loadComponent(
    "dashboard-security-alerts",
    `${base}/dashboardSecurityAlert.html`,
  );
  loadSecurityAlerts();
}
