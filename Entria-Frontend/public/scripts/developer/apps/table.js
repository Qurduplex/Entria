import { navigateToDeveloperPage } from "../../sideBar.js";
import { api } from "../api/apiDeveloper.js";

export async function loadAppsTable() {
    let data = [];

    try {
        const response = await api.getDeveloperApps();

        data = response.map((app) => ({
            appId: app.appId,
            name: app.name,
            logoUrl: app.logoUrl,
            logo: {
                initials: app.name
                    ? app.name.substring(0, 2).toUpperCase()
                    : "AP",
                color: "#7C6FFF",
            },
            description: "Brak opisu",
            redirectUri: "Brak danych",
            logins: "-",
            createdAt: "Brak danych",
            status: app.active ? "Aktywna" : "Nieaktywna",
            active: app.active,
        }));

        console.log("APPS:", data);

    } catch (err) {
        console.error("Nie udało się pobrać aplikacji:", err);
        data = [];
    }

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
                            data-app-id="${app.appId}"
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

        toggleButton.classList.toggle("hidden", filteredData.length <= 4);

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