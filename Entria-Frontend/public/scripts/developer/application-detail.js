import { loadApplicationDetailsData } from "./apps/applicationDetailsData.js";
import { loadApplicationDetailsStats } from "./apps/detail.stats.js";
import { loadApplicationCredentials } from "./apps/detail.credentials.js";
async function loadComponent(id, path) {
  const element = document.getElementById(id);
  if (!element) return;

  const res = await fetch(path);
  element.innerHTML = await res.text();
}

export async function initApplicationDetails() {

  await loadComponent(
    "application-details-stats",
    "../../pages/developer/fragments/apps/detailStats.html"
  );
  loadApplicationDetailsStats();

  await loadComponent(
    "application-credentials",
    "../../pages/developer/fragments/apps/detailCredentials.html"
  );
  loadApplicationCredentials();

  await loadComponent(
    "application-scopes",
    "../../pages/developer/fragments/application-details/applicationScopes.html"
  );

  await loadComponent(
    "application-logs",
    "../../pages/developer/fragments/application-details/applicationLogs.html"
  );

  await loadComponent(
    "application-danger-zone",
    "../../pages/developer/fragments/application-details/applicationDangerZone.html"
  );
  loadApplicationDetailsData();
}