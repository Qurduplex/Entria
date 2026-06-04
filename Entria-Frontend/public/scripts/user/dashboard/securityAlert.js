import { formatTimestamp } from "../../dateUtils.js";
import { navigateToDeveloperPage } from "../../sideBar.js";

export function loadSecurityAlerts() {
  const data = [
    { message: "Logowanie z nowego urządzenia",    description: "iPhone 14 Pro - Warszawa", timestamp: Date.now()},
    { message: "Logowanie z nowego urządzenia",    description: "iPhone 14 Pro - Warszawa", timestamp: Date.now() - 1000 * 60 * 60 * 27},
    { message: "Nieudana próba logowania",    description: "Nieznane urządzenie - Franfrut, DE", timestamp: Date.now() - 1000 * 60 * 60 * 60},
    { message: "Nowa aplikacja uzyskała dostęp",    description: "ParkFlow poprosiło o profil i email", timestamp: Date.now() - 1000 * 60 * 60 * 80},
  ];

  const list = document.getElementById("security-alert-list");
  if (!list) return;

  list.innerHTML = "";

  data.forEach((entry) => {
   
    list.innerHTML += `
      <div class="flex items-center justify-between py-3 border-b border-black/[0.05] last:border-0">
        <div class="min-w-0">
          <p class="text-sm font-medium text-[#161619] truncate">${entry.message}</p>
          <p class="text-xs text-[#8D8D8D] mt-0.5">${entry.description}</p>
        </div>
        <p class="text-xs text-[#8D8D8D] mt-0.5">${formatTimestamp(entry.timestamp)}</p>
      </div>
    `;
  });

   document.querySelector("#dashboard-security-alerts a")
  ?.addEventListener("click", (e) => {
    e.preventDefault();
    navigateToDeveloperPage("security");
  });
}