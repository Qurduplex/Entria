import { loadApplicationDetailsData } from "./apps/applicationDetailsData.js";
import { loadApplicationDetailsStats } from "./apps/detail.stats.js";
import { loadApplicationCredentials } from "./apps/detail.credentials.js";
import { loadApplicationScopes } from "./apps/detail.scopes.js";
import { loadApplicationLogs } from "./apps/detail.logs.js";
import { loadApplicationDangerZone } from "./apps/detail.dangerZone.js";

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
    "../../pages/developer/fragments/apps/detailScopes.html"
  );
  loadApplicationScopes();

  await loadComponent(
    "application-danger-zone",
    "../../pages/developer/fragments/apps/detailZone.html"
  );
  loadApplicationDangerZone();

  loadApplicationDetailsData();
}