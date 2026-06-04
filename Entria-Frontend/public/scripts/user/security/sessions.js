function createPopup(html) {
  const overlay = document.createElement("div");
  overlay.className = "fixed inset-0 z-[9999] bg-black/40 backdrop-blur-sm flex items-center justify-center";
  overlay.innerHTML = `<div class="bg-white rounded-2xl p-7 max-w-[480px] w-[calc(100%-32px)] shadow-[0_8px_40px_rgba(0,0,0,0.14)]">${html}</div>`;
  overlay.addEventListener("click", (e) => { if (e.target === overlay) overlay.remove(); });
  overlay.querySelector("div").addEventListener("click", (e) => e.stopPropagation());
  document.body.appendChild(overlay);
  return overlay;
}

export function initActiveSessions() {
  const sessions = [
    { id: 1, name: "MacBook Pro",   browser: "Mozilla Firefox", location: "Kraków, PL", lastUsed: "Aktywne teraz", current: true  },
    { id: 2, name: "iPhone 14 Pro", browser: "Safari",          location: "Kraków, PL", lastUsed: "3 godz. temu",  current: false },
  ];

  const list = document.getElementById("sessions-list");
  const logoutAllBtn = document.getElementById("logout-all-btn");
  if (!list) return;

  function render() {
    list.innerHTML = "";
    sessions.forEach((s) => {
      const row = document.createElement("div");
      row.className = `flex items-center justify-between gap-4 rounded-xl border px-4 py-3 ${s.current ? "border-[#A89DFF] bg-[#F5F4F9]" : "border-gray-200"}`;
      row.innerHTML = `
        <div class="flex items-center gap-3 min-w-0">
          <div class="w-10 h-10 rounded-xl flex items-center justify-center shrink-0 ${s.current ? "bg-[#EEF0FF] text-[#7C5CFC]" : "bg-gray-100 text-gray-400"}">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4" stroke-linecap="round"/></svg>
          </div>
          <div class="min-w-0">
            <p class="text-[14px] font-semibold text-[#161619] truncate">${s.name} – ${s.browser}</p>
            <p class="text-[13px] text-[#8D8D8D]">${s.location} – ${s.lastUsed}</p>
          </div>
        </div>
        ${s.current ? `<span class="text-[12px] font-medium text-[#7C5CFC] bg-[#EEF0FF] px-3 py-1 rounded-full shrink-0">Bieżąca</span>` : ""}`;
      list.appendChild(row);
    });
  }

  render();

  logoutAllBtn?.addEventListener("click", () => {
    const overlay = createPopup(`
      <h2 class="text-[18px] font-semibold text-[#161619] mb-2">Wylogować ze wszystkich urządzeń?</h2>
      <p class="text-[13.5px] text-[#444] leading-relaxed mb-6">
        Wszystkie aktywne sesje zostaną zakończone, łącznie z bieżącą. Trzeba będzie zalogować się ponownie.
      </p>
      <div class="flex justify-end gap-2.5">
        <button id="popup-cancel" class="btn-secondary">Anuluj</button>
        <button id="popup-confirm" class="px-[18px] py-[9px] rounded-[10px] bg-red-500 text-white text-[13px] font-medium hover:bg-red-600 transition-colors cursor-pointer">Tak, wyloguj wszędzie</button>
      </div>
    `);
    overlay.querySelector("#popup-cancel").addEventListener("click", () => overlay.remove());
    overlay.querySelector("#popup-confirm").addEventListener("click", () => {
      // TODO: API call — api.logoutAll() z apiAuth (endpoint już zdefiniowany)
      overlay.remove();
    });
  });
}