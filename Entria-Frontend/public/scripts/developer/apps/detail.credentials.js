import { api } from "../api/apiDeveloper.js";

async function getCurrentApplicationDetails() {
    const selectedApp = JSON.parse(
        sessionStorage.getItem("selectedApplication")
    );

    if (!selectedApp?.appId) {
        return null;
    }

    return await api.getApplicationDetails(selectedApp.appId);
}

export async function loadApplicationCredentials() {
    const app = await getCurrentApplicationDetails();

    if (!app) {
        return;
    }

    document.getElementById("application-client-id").textContent =
        app.clientId ?? "";

    document.getElementById("application-redirect-uri").textContent =
        app.redirectUri ?? "";

    document.getElementById("application-created-at").textContent =
        app.createdAt ?? "";

    document.getElementById("application-last-update").textContent =
        app.updatedAt ?? "";
}