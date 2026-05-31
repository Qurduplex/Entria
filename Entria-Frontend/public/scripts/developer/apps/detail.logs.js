import { navigateToDeveloperPage } from "../../sideBar.js";

const mockLoginLogs = [
  {
    time: "22.03.2026 18:42",
    user: "jan.kowalski@example.com",
    ip: "83.22.14.201",
    status: "Sukces"
  },
  {
    time: "22.03.2026 18:42",
    user: "jan.kowalski@example.com",
    ip: "83.22.14.201",
    status: "Sukces"
  },
  {
    time: "22.03.2026 18:42",
    user: "jan.kowalski@example.com",
    ip: "83.22.14.201",
    status: "Sukces"
  },
  {
    time: "22.03.2026 18:42",
    user: "jan.kowalski@example.com",
    ip: "83.22.14.201",
    status: "Sukces"
  },
  {
    time: "22.03.2026 18:42",
    user: "jan.kowalski@example.com",
    ip: "83.22.14.201",
    status: "Sukces"
  },
  {
    time: "22.03.2026 18:42",
    user: "jan.kowalski@example.com",
    ip: "83.22.14.201",
    status: "Sukces"
  }
];

export function loadApplicationLogs() {

  const app = JSON.parse(
    sessionStorage.getItem("selectedApplication")
  );

  const container = document.getElementById(
    "application-login-logs"
  );

  const showAllButton = document.getElementById(
    "show-all-logs-button"
  );

  if (!container) {
    return;
  }

  const logs =
    (app?.loginLogs ?? mockLoginLogs).slice(0, 5);

  container.innerHTML = logs.map(log => `
    <tr class="border-b border-gray-300 last:border-b-0">
      <td class="py-4">${log.time}</td>
      <td class="py-4 font-semibold">${log.user}</td>
      <td class="py-4">${log.ip}</td>
      <td class="py-4">${log.status}</td>
    </tr>
  `).join("");

  if (showAllButton) {

    showAllButton.addEventListener("click", async () => {
      if (app) {
        sessionStorage.setItem(
          "selectedApplication",
          JSON.stringify(app)
        );
      }

      await navigateToDeveloperPage(
        "apps-detail-logs"
      );
    });

  }

}