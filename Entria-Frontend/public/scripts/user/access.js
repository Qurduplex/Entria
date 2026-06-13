import { userApi } from "../user/api/apiUser.js";
import { api } from "../apiAuth.js";
import { showAlert } from "../alert.js";

// ─── MAPA SCOPE -> ETYKIETA ──────────────────────────────────────────────────
// Backend zwraca authorities jako "SCOPE_email", "SCOPE_pesel" itd. (OAuthPermission).
const scopeLabels = {
  openid:    { name: "Identyfikator konta" },
  profile:   { name: "Imię i nazwisko" },
  email:     { name: "Adres e-mail" },
  phone:     { name: "Numer telefonu" },
  pesel:     { name: "PESEL", sensitive: true },
  birthdate: { name: "Data urodzenia" },
  gender:    { name: "Płeć" },
  picture:   { name: "Zdjęcie profilowe" },
};

function normalizeScope(authority) {
  return authority.startsWith("SCOPE_") ? authority.slice(6) : authority;
}

function appInitials(name) {
  if (!name) return "?";
  const parts = name.trim().split(/\s+/);
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[1][0]).toUpperCase();
}

export async function initUserAccess() {
  const list = document.getElementById("consents-list");
  if (!list) return;

  list.innerHTML = `<p class="text-[13px] text-[#8D8D8D] px-1">Ładowanie…</p>`;

  let consents = [];
  try {
    consents = await userApi.getConsents();
  } catch (err) {
    console.error("Nie udało się pobrać zgód:", err);
    list.innerHTML = `<p class="text-[13px] text-red-500 px-1">Nie udało się pobrać listy aplikacji.</p>`;
    return;
  }

  renderConsents(consents);
  renderLogoutAllCard();

  // ─── RENDER LISTY ZGÓD ─────────────────────────────────────────────────
  function renderConsents(items) {
    list.innerHTML = "";

    if (!items || items.length === 0) {
      list.innerHTML = `
        <div class="card">
          <div class="px-5 py-8 text-center">
            <p class="text-[14px] font-medium text-[#161619] mb-1">Brak połączonych aplikacji</p>
            <p class="text-[13px] text-[#8D8D8D]">Aplikacje, którym udzielisz dostępu przez Entria, pojawią się tutaj.</p>
          </div>
        </div>`;
      return;
    }

    items.forEach((app) => {
      const initials = appInitials(app.appName);
      const scopes = (app.grantedAuthorities || []).map(normalizeScope);
      const scopeRows = scopes.map(renderScopeRow).join("");

      const card = document.createElement("div");
      card.className = "card";
      card.innerHTML = `
        <div class="card-header justify-between">
          <div class="flex items-center gap-3 min-w-0">
            <div class="w-10 h-10 rounded-xl flex items-center justify-center text-white text-[13px] font-semibold shrink-0 overflow-hidden"
                 style="background-color:#7C6FFF;">
              ${
                app.appLogoUrl
                  ? `<img src="${app.appLogoUrl}" alt="${app.appName}" class="w-full h-full object-cover" />`
                  : initials
              }
            </div>
            <div class="min-w-0">
              <p class="text-[15px] font-semibold text-[#161619] truncate">${app.appName || "Nieznana aplikacja"}</p>
              <p class="text-[12px] text-[#8D8D8D] truncate">${app.redirectUri || ""}</p>
            </div>
          </div>

          <button class="consent-toggle-btn shrink-0 bg-[#161619] text-white text-[13px] font-medium px-4 py-2 rounded-xl hover:bg-[#2a2a2e] transition-colors cursor-pointer">
            Szczegóły
          </button>
        </div>

        <div class="consent-detail hidden border-t border-gray-100 px-5 py-1">
          <p class="text-[11px] uppercase tracking-wide text-[#8D8D8D] mt-3 mb-1">Udostępnione dane</p>
          ${scopeRows || `<p class="text-[13px] text-[#8D8D8D] py-3">Brak szczegółowych uprawnień.</p>`}

          <div class="flex justify-end py-4 border-t border-gray-100 mt-1">
            <button class="consent-revoke-btn bg-[#161619] text-white text-[13px] font-medium px-5 py-2.5 rounded-xl hover:bg-red-600 transition-colors cursor-pointer">
              Cofnij dostęp
            </button>
          </div>
        </div>`;

      list.appendChild(card);

      const toggleBtn = card.querySelector(".consent-toggle-btn");
      const detail = card.querySelector(".consent-detail");
      toggleBtn.addEventListener("click", () => {
        const open = !detail.classList.contains("hidden");
        detail.classList.toggle("hidden", open);
        toggleBtn.textContent = open ? "Szczegóły" : "Zwiń";
      });

      const revokeBtn = card.querySelector(".consent-revoke-btn");
      revokeBtn.addEventListener("click", () => openRevokePopup(app));
    });
  }

  // ─── POPUP: COFNIJ DOSTĘP ──────────────────────────────────────────────
  function openRevokePopup(app) {
    const overlay = document.createElement("div");
    overlay.className = "fixed inset-0 z-[9999] bg-black/40 backdrop-blur-sm flex items-center justify-center";
    overlay.innerHTML = `
      <div class="bg-white rounded-2xl p-7 max-w-[460px] w-[calc(100%-32px)] shadow-[0_8px_40px_rgba(0,0,0,0.14)]">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-9 h-9 rounded-full bg-red-100 flex items-center justify-center shrink-0">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6"/>
            </svg>
          </div>
          <p class="text-[15px] font-semibold text-[#161619]">Cofnąć dostęp dla ${app.appName || "tej aplikacji"}?</p>
        </div>
        <p class="text-[13.5px] text-[#444] leading-relaxed mb-2">
          Aplikacja straci dostęp do udostępnionych danych. Przy następnym logowaniu przez Entria poprosi Cię o zgodę ponownie.
        </p>
        <div class="bg-[#F5F4F9] rounded-xl px-4 py-3 mb-6 border-l-4 border-[#A89DFF]">
          <p class="text-[13px] text-[#666] leading-relaxed">Twoje konto w samym serwisie pozostaje bez zmian – cofnięcie dotyczy tylko zgody w Entria.</p>
        </div>
        <div class="flex justify-end gap-2.5">
          <button id="revoke-cancel" class="btn-secondary">Anuluj</button>
          <button id="revoke-confirm" class="px-[18px] py-[9px] rounded-[10px] bg-red-500 text-white text-[13px] font-medium hover:bg-red-600 transition-colors cursor-pointer">Cofnij dostęp</button>
        </div>
      </div>`;

    overlay.addEventListener("click", (e) => { if (e.target === overlay) overlay.remove(); });
    overlay.querySelector("div").addEventListener("click", (e) => e.stopPropagation());
    overlay.querySelector("#revoke-cancel").addEventListener("click", () => overlay.remove());

    const confirmBtn = overlay.querySelector("#revoke-confirm");
    confirmBtn.addEventListener("click", async () => {
      confirmBtn.disabled = true;
      try {
        await userApi.revokeConsent(app.clientId);
        consents = consents.filter((c) => c.clientId !== app.clientId);
        overlay.remove();
        renderConsents(consents);
        showAlert("Dostęp został cofnięty.", "success");
      } catch (err) {
        console.error("Nie udało się cofnąć zgody:", err);
        showAlert(err?.data?.error || "Nie udało się cofnąć dostępu.", "error");
        confirmBtn.disabled = false;
      }
    });

    document.body.appendChild(overlay);
  }

  // ─── KARTA: WYLOGUJ ZE WSZYSTKICH URZĄDZEŃ ─────────────────────────────
  function renderLogoutAllCard() {
    const parent = list.parentElement;
    if (!parent || parent.querySelector("#logout-all-card")) return;

    const card = document.createElement("div");
    card.id = "logout-all-card";
    card.className = "card";
    card.innerHTML = `
      <div class="card-header">
        <div class="card-icon">
          <svg width="23" height="21" viewBox="0 0 23 21" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20.3462 0H2.65385C1.95 0 1.27499 0.276562 0.777294 0.768845C0.279601 1.26113 0 1.92881 0 2.625V14.875C0 15.5712 0.279601 16.2389 0.777294 16.7312C1.27499 17.2234 1.95 17.5 2.65385 17.5H20.3462C21.05 17.5 21.725 17.2234 22.2227 16.7312C22.7204 16.2389 23 15.5712 23 14.875V2.625C23 1.92881 22.7204 1.26113 22.2227 0.768845C21.725 0.276562 21.05 0 20.3462 0ZM21.2308 14.875C21.2308 15.1071 21.1376 15.3296 20.9717 15.4937C20.8058 15.6578 20.5808 15.75 20.3462 15.75H2.65385C2.41923 15.75 2.19423 15.6578 2.02833 15.4937C1.86243 15.3296 1.76923 15.1071 1.76923 14.875V2.625C1.76923 2.39294 1.86243 2.17038 2.02833 2.00628C2.19423 1.84219 2.41923 1.75 2.65385 1.75H20.3462C20.5808 1.75 20.8058 1.84219 20.9717 2.00628C21.1376 2.17038 21.2308 2.39294 21.2308 2.625V14.875ZM15.9231 20.125C15.9231 20.3571 15.8299 20.5796 15.664 20.7437C15.4981 20.9078 15.2731 21 15.0385 21H7.96154C7.72692 21 7.50192 20.9078 7.33602 20.7437C7.17012 20.5796 7.07692 20.3571 7.07692 20.125C7.07692 19.8929 7.17012 19.6704 7.33602 19.5063C7.50192 19.3422 7.72692 19.25 7.96154 19.25H15.0385C15.2731 19.25 15.4981 19.3422 15.664 19.5063C15.8299 19.6704 15.9231 19.8929 15.9231 20.125Z" fill="#7C6FFF"/>
          </svg>
        </div>
        <div>
          <p class="card-title">Sesje i urządzenia</p>
          <p class="card-subtitle">Zakończ wszystkie aktywne sesje na wszystkich urządzeniach</p>
        </div>
      </div>
      <div class="card-body">
        <button id="logout-all-btn"
                class="w-full text-center text-red-500 text-[14px] font-semibold py-3 px-5 rounded-xl border border-red-200 hover:bg-red-50 transition-colors cursor-pointer">
          Wyloguj z wszystkich urządzeń
        </button>
      </div>`;

    parent.appendChild(card);

    card.querySelector("#logout-all-btn").addEventListener("click", () => openLogoutAllPopup());
  }

  function openLogoutAllPopup() {
    const overlay = document.createElement("div");
    overlay.className = "fixed inset-0 z-[9999] bg-black/40 backdrop-blur-sm flex items-center justify-center";
    overlay.innerHTML = `
      <div class="bg-white rounded-2xl p-6 max-w-[400px] w-[calc(100%-32px)] shadow-[0_8px_40px_rgba(0,0,0,0.14)]">
        <h2 class="text-[16px] font-semibold text-[#161619] mb-1.5">Wylogować ze wszystkich urządzeń?</h2>
        <p class="text-[13px] text-[#8D8D8D] leading-relaxed mb-5">
          Wszystkie aktywne sesje zostaną natychmiast zakończone. Na tym urządzeniu również zostaniesz wylogowany.
        </p>
        <div class="flex justify-end gap-2.5">
          <button id="logoutall-cancel" class="px-[18px] py-[9px] rounded-[10px] border border-[#E0E0E0] bg-white text-[13px] font-medium text-[#444] hover:bg-[#F5F5F5] transition-colors cursor-pointer">Anuluj</button>
          <button id="logoutall-confirm" class="px-[18px] py-[9px] rounded-[10px] bg-red-500 text-white text-[13px] font-medium hover:bg-red-600 transition-colors cursor-pointer">Tak, wyloguj wszędzie</button>
        </div>
      </div>`;

    overlay.addEventListener("click", (e) => { if (e.target === overlay) overlay.remove(); });
    overlay.querySelector("div").addEventListener("click", (e) => e.stopPropagation());
    overlay.querySelector("#logoutall-cancel").addEventListener("click", () => overlay.remove());

    const confirmBtn = overlay.querySelector("#logoutall-confirm");
    confirmBtn.addEventListener("click", async () => {
      confirmBtn.disabled = true;
      try {
        await api.logoutAll();
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("expiresAt");
        localStorage.removeItem("userEmail");
        window.location.href = "/login.html";
      } catch (err) {
        console.error("Nie udało się wylogować ze wszystkich urządzeń:", err);
        showAlert(err?.data?.message || "Nie udało się wylogować.", "error");
        confirmBtn.disabled = false;
      }
    });

    document.body.appendChild(overlay);
  }
}

// ─── RENDER POJEDYNCZEGO UPRAWNIENIA ────────────────────────────────────────
function renderScopeRow(scope) {
  const meta = scopeLabels[scope] || { name: scope };

  const sensitiveTag = meta.sensitive
    ? `<span class="text-[11px] font-medium text-red-500 bg-red-50 px-2 py-0.5 rounded-full">Wrażliwe</span>`
    : "";

  return `
    <div class="flex items-center justify-between gap-4 py-3 border-b border-gray-100 last:border-0">
      <span class="flex items-center gap-3 text-[14px] text-[#161619]">
        <span class="text-green-600">✓</span>${meta.name}
      </span>
      ${sensitiveTag}
    </div>`;
}