function createPopup(html) {
  const overlay = document.createElement("div");
  overlay.className = "fixed inset-0 z-[9999] bg-black/40 backdrop-blur-sm flex items-center justify-center";
  overlay.innerHTML = `<div class="bg-white rounded-2xl p-7 max-w-[480px] w-[calc(100%-32px)] shadow-[0_8px_40px_rgba(0,0,0,0.14)]">${html}</div>`;
  overlay.addEventListener("click", (e) => { if (e.target === overlay) overlay.remove(); });
  overlay.querySelector("div").addEventListener("click", (e) => e.stopPropagation());
  document.body.appendChild(overlay);
  return overlay;
}

const methods = {
  "toggle-sms":     { name: "Kod SMS",            on: "Włączyć logowanie kodem SMS?",            off: "Wyłączyć logowanie kodem SMS?" },
  "toggle-passkey": { name: "Klucz bezpieczeństwa", on: "Włączyć logowanie kluczem bezpieczeństwa?", off: "Wyłączyć logowanie kluczem bezpieczeństwa?" },
};

export function initTwoFactor() {
  Object.keys(methods).forEach((id) => {
    const input = document.getElementById(id);
    if (!input) return;

    input.addEventListener("change", (e) => {
      // cofamy zmianę aż użytkownik potwierdzi
      const desired = input.checked;
      input.checked = !desired;

      const cfg = methods[id];
      const overlay = createPopup(`
        <h2 class="text-[18px] font-semibold text-[#161619] mb-2">${desired ? cfg.on : cfg.off}</h2>
        <p class="text-[13.5px] text-[#444] leading-relaxed mb-6">
          ${desired
            ? `Przy następnym logowaniu poprosimy o dodatkowe potwierdzenie metodą „${cfg.name}".`
            : `Twoje konto będzie chronione wyłącznie hasłem. Zalecamy pozostawienie weryfikacji dwuetapowej włączonej.`}
        </p>
        <div class="flex justify-end gap-2.5">
          <button id="popup-cancel" class="btn-secondary">Anuluj</button>
          <button id="popup-confirm" class="btn-primary">${desired ? "Włącz" : "Wyłącz"}</button>
        </div>
      `);

      overlay.querySelector("#popup-cancel").addEventListener("click", () => overlay.remove());
      overlay.querySelector("#popup-confirm").addEventListener("click", () => {
        // TODO: API call — zapis stanu 2FA
        input.checked = desired;
        overlay.remove();
      });
    });
  });
}