export async function initApplicationDetailsLogs() {
  loadApplicationLoginLogsPage();
}

const logsByApplication = {
  ParkFlow: [
    {
      time: "22.03.2026 18:42",
      user: "anna.nowak@firma.pl",
      ip: "91.45.23.110",
      location: "Kraków, PL",
      status: "success"
    },
    {
      time: "22.03.2026 16:03",
      user: "marek.zielinski@firma.pl",
      ip: "185.12.44.9",
      location: "Gdańsk, PL",
      status: "error"
    }
  ],

  Entria: [
    {
      time: "21.03.2026 13:25",
      user: "client@shoply.pl",
      ip: "192.168.1.15",
      location: "Poznań, PL",
      status: "success"
    },
    {
      time: "20.03.2026 10:45",
      user: "admin@shoply.pl",
      ip: "77.88.11.23",
      location: "Wrocław, PL",
      status: "error"
    }
  ],

  Bookie: [
    {
      time: "19.03.2026 09:12",
      user: "lekarz@medvisit.pl",
      ip: "91.11.24.10",
      location: "Warszawa, PL",
      status: "success"
    }
  ]
};

let currentLogsPage = 1;
const logsPerPage = 8;

function loadApplicationLoginLogsPage() {
  const table = document.getElementById("login-logs-table");
  if (!table) return;

  const app = JSON.parse(
    sessionStorage.getItem("selectedApplication")
  );

  const appName = app?.name;

  const applicationLogs =
    logsByApplication[appName] ?? [];

  const pageDescription = document.getElementById("page-description");

  if (pageDescription && appName) {
    pageDescription.textContent =
      `Wszystkie próby uwierzytelnienia przez ${appName}`;
  }

  const searchInput = document.getElementById("logs-search");
  const statusFilter = document.getElementById("logs-status-filter");
  const sortSelect = document.getElementById("logs-sort");

  const prevButton = document.getElementById("prev-logs-page");
  const nextButton = document.getElementById("next-logs-page");
  const clearButton = document.getElementById("clear-logs-filters-button");
  const exportButton = document.getElementById("export-logs-button");

  function parseLogDate(dateString) {
    const [datePart, timePart] = dateString.split(" ");
    const [day, month, year] = datePart.split(".");
    const [hour, minute] = timePart.split(":");

    return new Date(year, month - 1, day, hour, minute);
  }

  function getFilteredLogs() {
    let logs = [...applicationLogs];

    const search = searchInput.value.toLowerCase();
    const status = statusFilter.value;
    const sort = sortSelect.value;

    logs = logs.filter(log => {
      const matchesSearch =
        log.user.toLowerCase().includes(search) ||
        log.ip.toLowerCase().includes(search);

      const matchesStatus =
        status === "all" || log.status === status;

      return matchesSearch && matchesStatus;
    });

    logs.sort((a, b) => {
      const dateA = parseLogDate(a.time);
      const dateB = parseLogDate(b.time);

      return sort === "newest"
        ? dateB - dateA
        : dateA - dateB;
    });

    return logs;
  }

  function renderLogs() {
    const logs = getFilteredLogs();

    const totalPages = Math.max(
      1,
      Math.ceil(logs.length / logsPerPage)
    );

    if (currentLogsPage > totalPages) {
      currentLogsPage = totalPages;
    }

    const start = (currentLogsPage - 1) * logsPerPage;
    const visibleLogs = logs.slice(start, start + logsPerPage);

    table.innerHTML = visibleLogs.map(log => `
      <tr class="border-b border-gray-300 last:border-b-0">
        <td class="py-3">${log.time}</td>
        <td class="py-3">${log.user}</td>
        <td class="py-3">${log.ip}</td>
        <td class="py-3">${log.location}</td>
        <td class="py-3 font-semibold ${
          log.status === "success"
            ? "text-green-700"
            : "text-red-600"
        }">
          ${log.status === "success" ? "Sukces" : "Błąd"}
        </td>
      </tr>
    `).join("");

    document.getElementById("success-logs-count").textContent =
      logs.filter(log => log.status === "success").length;

    document.getElementById("error-logs-count").textContent =
      logs.filter(log => log.status === "error").length;

    document.getElementById("logs-page-info").textContent =
      `Strona ${currentLogsPage} z ${totalPages}`;

    prevButton.disabled = currentLogsPage === 1;
    nextButton.disabled = currentLogsPage === totalPages;

    prevButton.classList.toggle("opacity-50", prevButton.disabled);
    nextButton.classList.toggle("opacity-50", nextButton.disabled);
  }

  function resetAndRender() {
    currentLogsPage = 1;
    renderLogs();
  }

  searchInput.addEventListener("input", resetAndRender);
  statusFilter.addEventListener("change", resetAndRender);
  sortSelect.addEventListener("change", resetAndRender);

  prevButton.addEventListener("click", () => {
    if (currentLogsPage > 1) {
      currentLogsPage--;
      renderLogs();
    }
  });

  nextButton.addEventListener("click", () => {
    const totalPages = Math.ceil(
      getFilteredLogs().length / logsPerPage
    );

    if (currentLogsPage < totalPages) {
      currentLogsPage++;
      renderLogs();
    }
  });

  clearButton.addEventListener("click", () => {
    searchInput.value = "";
    statusFilter.value = "all";
    sortSelect.value = "newest";

    resetAndRender();
  });

  exportButton.addEventListener("click", () => {
    const logs = getFilteredLogs();

    const csv = [
      ["Czas", "Użytkownik", "IP", "Lokalizacja", "Status"],
      ...logs.map(log => [
        log.time,
        log.user,
        log.ip,
        log.location,
        log.status === "success" ? "Sukces" : "Błąd"
      ])
    ]
      .map(row => row.join(";"))
      .join("\n");

    const blob = new Blob([csv], {
      type: "text/csv;charset=utf-8;"
    });

    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = `${appName ?? "aplikacja"}_login_logs.csv`;
    link.click();

    URL.revokeObjectURL(link.href);
  });

  renderLogs();
}