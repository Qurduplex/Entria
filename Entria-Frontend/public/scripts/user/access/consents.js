import { cloneTemplate, ref } from "./templates.js";
import { appInitials, normalizeScope, buildScopeRow } from "./scopes.js";
import { openRevokePopup } from "./revokePopup.js";

/**
 * Renderuje listę zgód do #consents-list.
 * Trzyma własny stan `consents`, żeby po cofnięciu odświeżyć widok.
 *
 * @param {HTMLElement} list   – kontener #consents-list
 * @param {Array} initial      – początkowa lista zgód z API
 */
export function renderConsents(list, initial) {
  let consents = initial;

  function draw() {
    list.replaceChildren();

    if (!consents || consents.length === 0) {
      list.appendChild(cloneTemplate("tpl-consents-empty"));
      return;
    }

    consents.forEach((app) => list.appendChild(buildConsentCard(app)));
  }

  function buildConsentCard(app) {
    const card = cloneTemplate("tpl-consent-card");

    // ── Avatar / logo ──
    const avatar = ref(card, "app-avatar");
    if (app.appLogoUrl) {
      const img = document.createElement("img");
      img.src = app.appLogoUrl;
      img.alt = app.appName || "";
      img.className = "w-full h-full object-cover";
      avatar.appendChild(img);
    } else {
      avatar.textContent = appInitials(app.appName);
    }

    // ── Nazwa + redirect ──
    ref(card, "app-name").textContent = app.appName || "Nieznana aplikacja";
    ref(card, "app-redirect").textContent = app.redirectUri || "";

    // ── Lista scope ──
    const scopesList = ref(card, "scopes-list");
    const scopes = (app.grantedAuthorities || []).map(normalizeScope);

    if (scopes.length === 0) {
      ref(card, "scopes-empty").classList.remove("hidden");
    } else {
      scopes.forEach((scope) => scopesList.appendChild(buildScopeRow(scope)));
    }

    // ── Toggle szczegółów ──
    const toggleBtn = ref(card, "consent-toggle");
    const detail = ref(card, "consent-detail");
    toggleBtn.addEventListener("click", () => {
      const open = !detail.classList.contains("hidden");
      detail.classList.toggle("hidden", open);
      toggleBtn.textContent = open ? "Szczegóły" : "Zwiń";
    });

    // ── Cofnij dostęp ──
    ref(card, "consent-revoke").addEventListener("click", () => {
      openRevokePopup(app, (revoked) => {
        consents = consents.filter((c) => c.clientId !== revoked.clientId);
        draw();
      });
    });

    return card;
  }

  draw();
}