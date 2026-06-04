import { initTwoFactor } from "./security/twoFactor.js";
import { initLastLogin } from "./security/lastLogin.js";
import { initSecurityAlerts } from "./security/alerts.js";
import { initActiveSessions } from "./security/sessions.js";

async function loadComponent(id, path) {
  const element = document.getElementById(id);
  if (!element) return;
  const res = await fetch(path);
  element.innerHTML = await res.text();
}

export async function initUserSecurity() {
  const base = "../../pages/user/fragments/security";

  await loadComponent("security-2fa", `${base}/security2fa.html`);
  initTwoFactor();

  await loadComponent("security-last-login", `${base}/securityLastLogin.html`);
  initLastLogin();

  await loadComponent("security-alerts", `${base}/securityAlerts.html`);
  initSecurityAlerts();

  await loadComponent("security-sessions", `${base}/securitySessions.html`);
  initActiveSessions();
}