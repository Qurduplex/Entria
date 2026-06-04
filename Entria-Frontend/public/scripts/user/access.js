const scopeLabels = {
  full_name: { name: "Imię i nazwisko" },
  email:     { name: "Adres e-mail" },
  phone:     { name: "Numer telefonu" },
  avatar:    { name: "Zdjęcie profilowe" },
  pesel:     { name: "PESEL", sensitive: true },
};

const userData = {
  full_name: "Jan Kowalski",
  email: "jan@example.com",
  phone: "+48 *** *** 172",
  avatar: "JK",
  pesel: "00000000000",
};

export function initUserAccess() {
  const consents = [
    {
      name: "ParkFlow", initials: "PF", color: "#7C6FFF",
      consentDate: "12 sty 2026", status: "active",
      scopes: [
        { key: "full_name", granted: true,  required: true },
        { key: "email",     granted: true,  required: true },
        { key: "phone",     granted: true,  required: false },
        { key: "avatar",    granted: false, required: false },
      ],
    },
    {
      name: "E-recepta", initials: "ER", color: "#2D9A63",
      consentDate: "3 mar 2025", status: "active",
      scopes: [
        { key: "full_name", granted: true, required: true },
        { key: "email",     granted: true, required: true },
        { key: "phone",     granted: true, required: false },
        { key: "pesel",     granted: true, required: true },
      ],
    },
    {
      name: "Bookie", initials: "Bk", color: "#8D8D8D",
      consentDate: "10 lutego 2026", status: "revoked",
      scopes: [
        { key: "full_name", granted: true, required: true },
        { key: "email",     granted: true, required: true },
      ],
    },
  ];

  const list = document.getElementById("consents-list");
  if (!list) return;

  list.innerHTML = "";

  consents.forEach((app) => {
    const isActive = app.status === "active";

    const statusBadge = isActive
      ? `<span class="text-[12px] font-medium text-green-600 bg-emerald-50 px-3 py-1 rounded-full">Aktywna</span>`
      : `<span class="text-[12px] font-medium text-red-500 bg-red-50 px-3 py-1 rounded-full">Cofnięto</span>`;

    const dateLabel = isActive
      ? `Zgoda od ${app.consentDate}`
      : `Cofnięta ${app.consentDate}`;

    const scopeRows = app.scopes.map(renderScopeRow).join("");

    const card = document.createElement("div");
    card.className = "card";
    card.innerHTML = `
      <div class="card-header justify-between">
        <div class="flex items-center gap-3 min-w-0">
          <div class="w-10 h-10 rounded-xl flex items-center justify-center text-white text-[13px] font-semibold shrink-0"
               style="background-color: ${isActive ? app.color : "#D1D5DB"};">
            ${app.initials}
          </div>
          <p class="text-[15px] font-semibold ${isActive ? "text-[#161619]" : "text-[#9CA3AF]"}">${app.name}</p>
        </div>

        <div class="flex items-center gap-4 shrink-0">
          <div class="text-right">
            <p class="text-[12px] text-[#8D8D8D] mb-1">${dateLabel}</p>
            ${statusBadge}
          </div>
          <button class="consent-toggle-btn btn-primary">Szczegóły</button>
        </div>
      </div>

      <div class="consent-detail hidden border-t border-gray-100 px-5 py-1">
        ${scopeRows}
      </div>`;


    list.appendChild(card);

    const btn = card.querySelector(".consent-toggle-btn");
    const detail = card.querySelector(".consent-detail");

    btn.addEventListener("click", () => {
      const open = !detail.classList.contains("hidden");
      detail.classList.toggle("hidden", open);
      btn.textContent = open ? "Szczegóły" : "Zwiń";
    });

    card.querySelectorAll("[data-reveal]").forEach((revealBtn) => {
      revealBtn.addEventListener("click", () => {
        const valueEl = revealBtn.closest("[data-scope-row]").querySelector("[data-value]");
        const shown = valueEl.textContent === valueEl.dataset.real;
        valueEl.textContent = shown ? valueEl.dataset.masked : valueEl.dataset.real;
        revealBtn.textContent = shown ? "Pokaż" : "Ukryj";
      });
    });
  });
}

function renderScopeRow(scope) {
  const meta = scopeLabels[scope.key];
  if (!meta) return "";

  const icon = scope.granted
    ? `<span class="text-green-600">✓</span>`
    : `<span class="text-[#8D8D8D]">✕</span>`;

  let value;
  if (!scope.granted) {
    value = `<span class="text-[13px] text-[#8D8D8D]">Odmówiono</span>`;
  } else if (meta.sensitive) {
    const masked = "•••••••••••";
    const real = userData[scope.key] ?? "";
    value = `
      <span class="flex items-center gap-2">
        <span data-value data-masked="${masked}" data-real="${real}" class="text-[13px] text-[#161619] tabular-nums">${masked}</span>
        <button data-reveal class="text-[12px] text-[#7C6FFF] hover:underline cursor-pointer">Pokaż</button>
      </span>`;
  } else {
    value = `<span class="text-[13px] text-[#161619]">${userData[scope.key] ?? ""}</span>`;
  }

  return `
    <div data-scope-row class="flex items-center justify-between gap-4 py-3 border-b border-gray-100 last:border-0">
      <span class="flex items-center gap-3 text-[14px] text-[#161619]">${icon}${meta.name}</span>
      ${value}
    </div>`;
}