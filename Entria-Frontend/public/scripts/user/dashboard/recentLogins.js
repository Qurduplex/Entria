import { formatTimestamp } from "../../dateUtils.js";
import { navigateToDeveloperPage } from "../../sideBar.js";

export function loadRecentLogins() {
  const data = [
    // { app: "ParkFlow",  device: "MacBook Pro", timestamp: Date.now() - 1000 * 60 * 30,          status: "Sukces"   },
    // { app: "E-recepta", device: "MacBook Pro", timestamp: Date.now() - 1000 * 60 * 180,         status: "Sukces"   },
    { app: "Bookie",    device: "MacBook Pro", timestamp: Date.now() - 1000 * 60 * 60 * 27,     status: "Nieudane" },
    { app: "Bookie",    device: "Nieznane",    timestamp: Date.now() - 1000 * 60 * 60 * 24 * 6, status: "Nieudane" },
  ];

  const list = document.getElementById("recent-logins-list");
  if (!list) return;

  list.innerHTML = "";

  data.forEach((entry) => {
    const isSuccess = entry.status === "Sukces";
    const statusClass = isSuccess
      ? "bg-emerald-50 text-emerald-600"
      : "bg-red-50 text-red-500";

    list.innerHTML += `
      <div class="flex items-center justify-between py-3 border-b border-black/[0.05] last:border-0">
        <div class="min-w-0">
          <p class="text-sm font-medium text-[#161619] truncate">${entry.app}</p>
          <p class="text-xs text-[#8D8D8D] mt-0.5">${entry.device} · ${formatTimestamp(entry.timestamp)}</p>
        </div>
        <span class="ml-4 shrink-0 rounded-full px-2.5 py-0.5 text-xs font-medium ${statusClass}">
          ${entry.status}
        </span>
      </div>
    `;
  });

  document.querySelector("#dashboard-recent-logins a")
  ?.addEventListener("click", (e) => {
    e.preventDefault();
    navigateToDeveloperPage("history");
  });
}