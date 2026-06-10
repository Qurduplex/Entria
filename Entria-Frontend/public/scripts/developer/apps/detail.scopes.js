import { api } from "../api/apiDeveloper.js";

async function getCurrentApplicationDetails() {
    const selectedApp = JSON.parse(
        sessionStorage.getItem("selectedApplication")
    );

    console.log("SELECTED APP:", selectedApp);

    if (!selectedApp?.appId) {
        console.error("Brak selectedApplication.appId");
        return null;
    }

    const app = await api.getApplicationDetails(selectedApp.appId);

    console.log("APP DETAILS:", app);

    return app;
}

export async function loadApplicationScopes() {
    const app = await getCurrentApplicationDetails();

    const container = document.getElementById("data-application-scopes");

    if (!container) {
        console.error("Brak elementu #data-application-scopes");
        return;
    }

    if (!app?.permissions) {
        container.innerHTML = `
            <p class="text-sm text-gray-500">
                Brak uprawnień dla tej aplikacji.
            </p>
        `;
        return;
    }

    const scopes = Object.keys(app.permissions);

    container.innerHTML = scopes.map(scope => `
        <div class="rounded-lg border border-gray-300 bg-gray-100 px-5 py-2 text-sm font-medium text-gray-900">
            ${scope}
        </div>
    `).join("");
}