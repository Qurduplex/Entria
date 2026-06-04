import { formatTimestamp } from "../../dateUtils.js";

export function initSecurityAlerts() {
  const data = [
    { type: "danger",  message: "Logowanie z nowego urządzenia", description: "iPhone 14 Pro – Kraków, PL – Safari", timestamp: Date.now() - 1000 * 60 * 60 * 9,  isNew: true },
    { type: "info",    message: "Nowa aplikacja uzyskała dostęp", description: "ParkFlow poprosiło o dostęp do profilu i email", timestamp: Date.now() - 1000 * 60 * 60 * 27, isNew: true },
    { type: "success", message: "Hasło zostało zmienione",        description: "Zmiana z przeglądarki Chrome, Kraków", timestamp: Date.now() - 1000 * 60 * 60 * 24 * 5, isNew: true },
    { type: "danger",  message: "Nieudana próba logowania",       description: "Nieznana lokalizacja – Frankfurt, DE", timestamp: Date.now() - 1000 * 60 * 60 * 24 * 8, isNew: false },
  ];

  const styles = {
    danger:  { box: "bg-[#FFF1F1] border-red-200/60",     dot: "bg-red-500" },
    info:    { box: "bg-[#F5F4F9] border-[#A89DFF]",       dot: "bg-[#7C5CFC]" },
    success: { box: "bg-emerald-50 border-green-400/30",   dot: "bg-green-500" },
  };

  const list = document.getElementById("security-alert-list");
  const badge = document.getElementById("alerts-new-badge");
  if (!list) return;

  const newCount = data.filter((a) => a.isNew).length;
  if (badge) badge.textContent = `${newCount} nowe`;

  list.innerHTML = "";
  data.forEach((a) => {
    const s = styles[a.type] || styles.info;
    list.innerHTML += `
      <div class="flex items-start justify-between gap-4 rounded-xl border ${s.box} px-4 py-3">
        <div class="flex items-start gap-3 min-w-0">
          <span class="mt-1.5 h-2 w-2 rounded-full shrink-0 ${s.dot}"></span>
          <div class="min-w-0">
            <p class="text-[14px] font-medium text-[#161619]">${a.message}</p>
            <p class="text-[13px] text-[#8D8D8D] mt-0.5">${a.description}</p>
          </div>
        </div>
        <span class="text-[12px] text-[#8D8D8D] shrink-0">${formatTimestamp(a.timestamp)}</span>
      </div>`;
  });
}