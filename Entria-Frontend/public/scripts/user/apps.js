export function initUserApps() {

  // ─── DANE (mock — zamień na API) ──────────────────────────────────────────
  const apps = [
    { id: 1, name: "ParkFlow",  domain: "parkflow.com", initials: "PF", color: "#7C6FFF", connectedAt: "12 sty 2026", lastUsed: "Dziś, 18:42",  lastUsedRecent: true,  blocked: false },
    { id: 2, name: "E-recepta", domain: "jahowl.pl",    initials: "Er", color: "#2D9A63", connectedAt: "12 sty 2026", lastUsed: "Dziś, 18:42",  lastUsedRecent: true,  blocked: false },
    { id: 3, name: "Bookie",    domain: "bookie.io",    initials: "Bk", color: "#8D8D8D", connectedAt: "12 sty 2026", lastUsed: "47 dni temu",  lastUsedRecent: false, blocked: true  },
  ];

  const devices = [
    { id: 1, name: "MacBook Pro",        browser: "Chrome",  location: "Kraków, PL",   lastUsed: "Dziś, 18:42",  sessionActive: true  },
    { id: 2, name: "iPhone 14 Pro",      browser: "Safari",  location: "Kraków, PL",   lastUsed: "Dziś, 18:42",  sessionActive: true  },
    { id: 3, name: "Windows – Dell XPS", browser: "Firefox", location: "Warszawa, PL", lastUsed: "47 dni temu",  sessionActive: false },
  ];

  // ─── POPUP HELPER — tworzy overlay w body ────────────────────────────────
  function createPopup(html) {
    const overlay = document.createElement("div");
    overlay.className = "fixed inset-0 z-[9999] bg-black/40 backdrop-blur-sm flex items-center justify-center";
    overlay.innerHTML = `
      <div class="bg-white rounded-2xl p-7 max-w-[480px] w-[calc(100%-32px)] shadow-[0_8px_40px_rgba(0,0,0,0.14)]">
        ${html}
      </div>`;
    overlay.addEventListener("click", (e) => {
      if (e.target === overlay) overlay.remove();
    });
    overlay.querySelector("div").addEventListener("click", (e) => e.stopPropagation());
    document.body.appendChild(overlay);
    return overlay;
  }

  // ─── RENDER APPS ─────────────────────────────────────────────────────────
  function renderApps() {
    const appsList  = document.getElementById("apps-list");
    const appsBadge = document.getElementById("apps-count-badge");
    if (!appsList) return;
    if (appsBadge) appsBadge.textContent = `${apps.length} połączone`;
    appsList.innerHTML = "";

    apps.forEach(app => {
      const row = document.createElement("div");
      row.className = "flex items-center justify-between px-5 py-4 gap-4";

      const left = document.createElement("div");
      left.className = "flex items-center gap-4 w-[220px] shrink-0";
      const avatarIcon = document.createElement("div");
      avatarIcon.className = "w-10 h-10 rounded-xl flex-shrink-0 flex items-center justify-center text-white text-[13px] font-semibold";
      avatarIcon.style.backgroundColor = app.blocked ? "#D1D5DB" : app.color;
      avatarIcon.textContent = app.initials;
      const avatarText = document.createElement("div");
      avatarText.className = "min-w-0";
      const appName = document.createElement("p");
      appName.className = `text-[14px] font-semibold ${app.blocked ? "text-[#9CA3AF]" : "text-[#161619]"}`;
      appName.textContent = app.name;
      const appDomain = document.createElement("p");
      appDomain.className = "text-[12px] text-[#8D8D8D]";
      appDomain.textContent = app.domain;
      avatarText.append(appName, appDomain);
      left.append(avatarIcon, avatarText);

      const middle = document.createElement("div");
      middle.className = "hidden sm:flex flex-1 items-center justify-center gap-12";

      const col1 = document.createElement("div");
      col1.className = "text-center";
      const col1Label = document.createElement("p");
      col1Label.className = "text-[11px] uppercase tracking-wide text-[#8D8D8D] mb-0.5";
      col1Label.textContent = app.blocked ? "Odłączono" : "Połączono";
      const col1Val = document.createElement("p");
      col1Val.className = "text-[13px] text-[#161619]";
      col1Val.textContent = app.connectedAt;
      col1.append(col1Label, col1Val);

      const col2 = document.createElement("div");
      col2.className = "text-center";
      const col2Label = document.createElement("p");
      col2Label.className = "text-[11px] uppercase tracking-wide text-[#8D8D8D] mb-0.5";
      col2Label.textContent = "Ostatnie użycie";
      const col2Val = document.createElement("p");
      col2Val.className = `text-[13px] font-medium ${app.lastUsedRecent && !app.blocked ? "text-green-600" : "text-[#161619]"}`;
      col2Val.textContent = app.lastUsed;
      col2.append(col2Label, col2Val);

      middle.append(col1, col2);

      const btn = document.createElement("button");
      btn.className = `shrink-0 ${app.blocked ? "btn-secondary" : "btn-primary"}`;
      btn.textContent = app.blocked ? "Przywróć dostęp" : "Zablokuj dostęp";
      btn.addEventListener("click", () => {
        if (app.blocked) {
          // TODO: API call
          app.blocked = false;
          renderApps();
        } else {
          openBlockPopup(app);
        }
      });

      row.append(left, middle, btn);
      appsList.appendChild(row);
    });
  }

  // ─── RENDER DEVICES ──────────────────────────────────────────────────────
  function renderDevices() {
    const devicesList    = document.getElementById("devices-list");
    const devicesBadge   = document.getElementById("devices-count-badge");
    const devicesSummary = document.getElementById("devices-summary");
    if (!devicesList) return;

    const activeCount  = devices.filter(d => d.sessionActive).length;
    const expiredCount = devices.filter(d => !d.sessionActive).length;
    if (devicesBadge)   devicesBadge.textContent  = `${devices.length} urządzenia`;
    if (devicesSummary) devicesSummary.textContent = `${activeCount} aktywne sesje  ·  ${expiredCount} wygasła`;

    devicesList.innerHTML = "";

    devices.forEach(device => {
      const row = document.createElement("div");
      row.className = "flex items-center justify-between px-5 py-4 gap-4";

      const left = document.createElement("div");
      left.className = "flex items-center gap-4 w-[220px] shrink-0";
      const icon = document.createElement("div");
      icon.className = `w-10 h-10 rounded-xl flex-shrink-0 flex items-center justify-center ${device.sessionActive ? "bg-[#EEF0FF] text-[#7C5CFC]" : "bg-gray-100 text-gray-400"}`;
      icon.innerHTML = `<svg width="18" height="18" viewBox="0 0 24 24"></svg>`;
      const nameWrap = document.createElement("div");
      nameWrap.className = "min-w-0";
      const devName = document.createElement("p");
      devName.className = "text-[14px] font-semibold text-[#161619]";
      devName.textContent = device.name;
      const devInfo = document.createElement("p");
      devInfo.className = "text-[12px] text-[#8D8D8D]";
      devInfo.textContent = `${device.browser} – ${device.location}`;
      nameWrap.append(devName, devInfo);
      left.append(icon, nameWrap);

      const middle = document.createElement("div");
      middle.className = "hidden sm:flex flex-1 items-center justify-center gap-12";

      const col1 = document.createElement("div");
      col1.className = "text-center";
      const col1Label = document.createElement("p");
      col1Label.className = "text-[11px] uppercase tracking-wide text-[#8D8D8D] mb-0.5";
      col1Label.textContent = "Ostatnie użycie";
      const col1Val = document.createElement("p");
      col1Val.className = "text-[13px] text-[#161619]";
      col1Val.textContent = device.lastUsed;
      col1.append(col1Label, col1Val);

      const col2 = document.createElement("div");
      col2.className = "text-center";
      const col2Label = document.createElement("p");
      col2Label.className = "text-[11px] uppercase tracking-wide text-[#8D8D8D] mb-0.5";
      col2Label.textContent = "Sesja";
      const col2Val = document.createElement("p");
      col2Val.className = `text-[13px] font-medium ${device.sessionActive ? "text-green-600" : "text-[#8D8D8D]"}`;
      col2Val.textContent = device.sessionActive ? "Aktywna" : "Wygasła";
      col2.append(col2Label, col2Val);

      middle.append(col1, col2);

      const btn = document.createElement("button");
      btn.textContent = "Wyloguj";
      if (device.sessionActive) {
        btn.className = "shrink-0 btn-primary";
        btn.addEventListener("click", () => openLogoutPopup(device));
      } else {
        btn.className = "shrink-0 btn-secondary opacity-40 cursor-not-allowed pointer-events-none";
        btn.disabled = true;
      }

      row.append(left, middle, btn);
      devicesList.appendChild(row);
    });
  }

  renderDevices();
  renderApps();

  // ─── POPUP: ZABLOKUJ DOSTĘP ───────────────────────────────────────────────
  function openBlockPopup(app) {
    const overlay = createPopup(`
      <div class="w-10 h-10 rounded-xl bg-red-50 flex items-center justify-center mb-5">
        <svg width="20" height="20" viewBox="0 0 24 24"></svg>
      </div>
      <h2 class="text-[18px] font-semibold text-[#161619] mb-2">
        Zablokować dostęp dla <strong>${app.name}</strong>?
      </h2>
      <p class="text-[13.5px] text-[#444] leading-relaxed mb-4">
        Aplikacja <strong>${app.name}</strong> (${app.domain}) nie będzie mogła logować Cię przez Entria do momentu przywrócenia dostępu.
      </p>
      <div class="bg-[#F5F4F9] rounded-xl px-4 py-3 mb-6 border-l-4 border-[#A89DFF]">
        <p class="text-[13px] text-[#161619] font-semibold mb-0.5">Twoje konto w serwisie pozostaje bez zmian.</p>
        <p class="text-[13px] text-[#666] leading-relaxed">Blokada dotyczy wyłącznie możliwości logowania przez Entria – dane, które aplikacja już posiada nie są usuwane.</p>
      </div>
      <div class="flex justify-end gap-2.5">
        <button id="popup-cancel" class="btn-secondary">Anuluj</button>
        <button id="popup-confirm" class="btn-primary">Zablokuj dostęp</button>
      </div>
    `);

    overlay.querySelector("#popup-cancel").addEventListener("click", () => overlay.remove());
    overlay.querySelector("#popup-confirm").addEventListener("click", () => {
      // TODO: API call
      app.blocked = true;
      overlay.remove();
      renderApps();
    });
  }

  // ─── POPUP: WYLOGUJ URZĄDZENIE ────────────────────────────────────────────
  function openLogoutPopup(device) {
    const overlay = createPopup(`
      <div class="w-10 h-10 rounded-xl bg-[#EEF0FF] flex items-center justify-center mb-5">
        <svg width="20" height="20" viewBox="0 0 24 24"></svg>
      </div>
      <h2 class="text-[18px] font-semibold text-[#161619] mb-2">
        Wylogować urządzenie?
      </h2>
      <p class="text-[13.5px] text-[#444] leading-relaxed mb-6">
        Sesja na urządzeniu <strong>${device.name}</strong> (${device.browser} – ${device.location}) zostanie natychmiast zakończona.
      </p>
      <div class="flex justify-end gap-2.5">
        <button id="popup-cancel" class="btn-secondary">Anuluj</button>
        <button id="popup-confirm" class="btn-primary">Tak, wyloguj</button>
      </div>
    `);

    overlay.querySelector("#popup-cancel").addEventListener("click", () => overlay.remove());
    overlay.querySelector("#popup-confirm").addEventListener("click", () => {
      // TODO: API call
      device.sessionActive = false;
      overlay.remove();
      renderDevices();
    });
  }
}