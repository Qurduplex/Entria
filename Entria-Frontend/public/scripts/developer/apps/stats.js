import { api } from "../api/apiDeveloper.js";

export async function loadAppsStats() {
    const appsTotal = document.getElementById("apps-total");
    const appsActive = document.getElementById("apps-active");
    const appsTotalActiveCard = document.getElementById("apps-total-active-card");

    if (!appsTotal || !appsActive || !appsTotalActiveCard) {
        return;
    }

    try {
        const applications = await api.getDeveloperApps();

        const totalApplications = applications.length;

        const activeApplications = applications.filter(app => app.active).length;

        appsTotal.textContent = totalApplications;
        appsActive.textContent = activeApplications;
        appsTotalActiveCard.textContent = totalApplications;

    } catch (err) {
        appsTotal.textContent = "0";
        appsActive.textContent = "0";
        appsTotalActiveCard.textContent = "0";
    }
}