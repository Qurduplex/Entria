import { loadAppsStats } from "./apps/stats.js";
import { loadAppsTable } from "./apps/table.js";
import { loadAppsDevelop } from "./apps/toolbar.js";

async function loadComponent(id, path) {
  const element = document.getElementById(id);
  if (!element) return;

  const res = await fetch(path);
  element.innerHTML = await res.text();
}

export async function initDeveloperApps() {
  await loadComponent(
    "apps-stats",
    "../../pages/developer/fragments/apps/appsStats.html"
  );
  loadAppsStats();

  await loadComponent(
    "apps-toolbar",
    "../../pages/developer/fragments/apps/appsToolbar.html"
  );
  loadAppsDevelop();
  
  await loadComponent(
    "apps-table",
    "../../pages/developer/fragments/apps/appsTable.html"
  );
  loadAppsTable();
}