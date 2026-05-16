import { navigateToDeveloperPage } from "../../sideBar.js";

export function loadAppsTable() {
  const data = [
    {
      name: "ParkFlow",
      logo: {
            initials: "PF",
            color: "#7C6FFF"
        },
      description: "System rezerwacji miejsc parkingowych",
      redirectUri: "http://parkflow.pl/auth/callback",
      logins: 847,
      createdAt: "12 sty 2026",
      status: "Aktywna",
    },
    {
      name: "Bookie",
      logo: {
            initials: "PF",
            color: "#7C6FFF"
        },
      description: "Aplikacja do rezerwacji usług",
      redirectUri: "http://bookie.pl/auth/callback",
      logins: 231,
      createdAt: "4 lut 2026",
      status: "Nieaktywna",
    },
    {
      name: "Entria",
      logo: {
            initials: "PF",
            color: "#7C6FFF"
        },
      description: "Identity platform OAuth2",
      redirectUri: "http://entria.pl/auth/callback",
      logins: 1620,
      createdAt: "18 mar 2026",
      status: "Aktywna",
    },
    {
      name: "Thermio",
      logo: {
            initials: "PF",
            color: "#7C6FFF"
        },
      description: "IoT Smart Home",
      redirectUri: "http://thermio.pl/auth/callback",
      logins: 412,
      createdAt: "21 kwi 2026",
      status: "Aktywna",
    },
    {
      name: "TaskFlow",
      logo: {
            initials: "PF",
            color: "#7C6FFF"
        },
      description: "Zarządzanie zadaniami",
      redirectUri: "http://taskflow.pl/auth/callback",
      logins: 95,
      createdAt: "2 maj 2026",
      status: "Nieaktywna",
    },
    {
      name: "SportHub",
      logo: {
            initials: "PF",
            color: "#7C6FFF"
        },
      description: "Platforma sportowa",
      redirectUri: "http://sporthub.pl/auth/callback",
      logins: 524,
      createdAt: "9 maj 2026",
      status: "Aktywna",
    },
  ];

  const tableBody = document.getElementById("apps-table-body");
  const toggleButton = document.getElementById("load-more-apps");
  const searchInput = document.getElementById("apps-search-input");

  if (!tableBody || !toggleButton || !searchInput) {
    return;
  }

  let expanded = false;
  let searchValue = "";

  function renderTable() {
    tableBody.innerHTML = "";

    const filteredData = data.filter((app) => {
      const text = `
        ${app.name}
        ${app.description}
        ${app.redirectUri}
        ${app.status}
      `.toLowerCase();

      return text.includes(searchValue.toLowerCase());
    });

    const visibleData = expanded
      ? filteredData
      : filteredData.slice(0, 4);

    visibleData.forEach((app) => {
      const isActive = app.status === "Aktywna";

      tableBody.innerHTML += `
        <tr class="border-b border-gray-200">

          <td class="px-6 py-5">
            <p class="font-semibold text-gray-900">
              ${app.name}
            </p>

            <p class="mt-1 text-sm text-gray-500">
              ${app.description}
            </p>
          </td>

          <td class="px-6 py-5 text-sm text-gray-700">
            ${app.redirectUri}
          </td>

          <td class="px-6 py-5 text-center font-semibold text-gray-900">
            ${app.logins}
          </td>

          <td class="px-6 py-5 text-center text-gray-700">
            ${app.createdAt}
          </td>

          <td class="px-6 py-5 text-center">
            <span class="font-medium ${
              isActive ? "text-green-600" : "text-red-600"
            }">
              ${app.status}
            </span>
          </td>

          <td class="px-6 py-5 text-right">
            <button
                class="app-details-button text-sm text-gray-800 hover:underline"
                data-app-name="${app.name}"
                >
                Szczegóły →
            </button>
          </td>

        </tr>
      `;
    });

    if (filteredData.length === 0) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="6" class="px-6 py-8 text-center text-sm text-gray-500">
            Nie znaleziono aplikacji
          </td>
        </tr>
      `;
    }

    toggleButton.textContent = expanded
      ? "Załaduj mniej"
      : "Załaduj więcej";

    toggleButton.classList.toggle(
      "hidden",
      filteredData.length <= 4
    );

   document.querySelectorAll(".app-details-button").forEach((button, index) => {

        button.addEventListener("click", () => {

            const selectedApp = visibleData[index];

            sessionStorage.setItem(
                "selectedApplication",
                JSON.stringify(selectedApp)
            );

            navigateToDeveloperPage("apps-detail");

        });

    });
  }

  searchInput.addEventListener("input", (event) => {
    searchValue = event.target.value;
    expanded = false;
    renderTable();
  });

  toggleButton.addEventListener("click", () => {
    expanded = !expanded;
    renderTable();
  });

  renderTable();
}