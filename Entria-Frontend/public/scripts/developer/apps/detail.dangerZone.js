import { api } from "../api/apiDeveloper.js";
import { navigateToDeveloperPage } from "../../sideBar.js";

function mapBackendPermissionsToDraft(permissions = {}) {
    const permissionMap = {
        OPENID: "openid",
        EMAIL: "email",
        PROFILE: "profile",
        PHONE: "phone",
        PESEL: "pesel",
        BIRTHDATE: "birthdate",
        GENDER: "gender",
        PICTURE: "picture",
    };

    const draftPermissions = {};

    Object.entries(permissions).forEach(([key, value]) => {
        const frontendKey = permissionMap[key];

        if (!frontendKey) return;

        draftPermissions[frontendKey] = {
            enabled: true,
            required: value === true,
        };
    });

    return draftPermissions;
}

export function loadApplicationDangerZone() {
    const app = JSON.parse(
        sessionStorage.getItem("selectedApplication")
    );

    if (!app?.appId) {
        return;
    }

    const editButton = document.getElementById("edit-application-button");
    const disableButton = document.getElementById("disable-application-button");
    const deleteButton = document.getElementById("delete-application-button");

    if (editButton) {
        editButton.addEventListener("click", async () => {
            try {
                const applicationDetails =
                    await api.getApplicationDetails(app.appId);

                sessionStorage.setItem(
                    "editApplication",
                    JSON.stringify(applicationDetails)
                );

                sessionStorage.setItem(
                    "applicationDraft",
                    JSON.stringify({
                        appId: applicationDetails.appId,
                        name: applicationDetails.name,
                        redirectUri: applicationDetails.redirectUri,
                        logoUrl: applicationDetails.logoUrl,
                        tosPdfUrl: applicationDetails.tosPdfUrl,
                        permissions: mapBackendPermissionsToDraft(
                            applicationDetails.permissions
                        ),
                    })
                );
                await navigateToDeveloperPage("develop");

            } catch (err) {
                alert("Nie udało się otworzyć edycji aplikacji.");
            }
        });
    }

    if (disableButton) {
        if (!app.active) {
            disableButton.disabled = true;
            disableButton.classList.add("opacity-50", "cursor-not-allowed");
            disableButton.textContent = "Aplikacja wyłączona";
        }

        disableButton.addEventListener("click", async () => {
            if (!app.active) return;

            const confirmed = confirm(
                "Czy na pewno chcesz dezaktywować aplikację?"
            );

            if (!confirmed) return;

            try {
                await api.deactivateApplication(app.appId);

                app.active = false;

                sessionStorage.setItem(
                    "selectedApplication",
                    JSON.stringify(app)
                );

                disableButton.disabled = true;
                disableButton.classList.add("opacity-50", "cursor-not-allowed");
                disableButton.textContent = "Aplikacja wyłączona";

            } catch (err) {
                alert("Nie udało się dezaktywować aplikacji.");
            }
        });
    }

    if (deleteButton) {
        deleteButton.addEventListener("click", async () => {
            const confirmed = confirm(
                "Czy na pewno chcesz trwale usunąć aplikację?"
            );

            if (!confirmed) return;

            try {
                await api.deleteApplication(app.appId);

                alert("Aplikacja została usunięta.");

                sessionStorage.removeItem("selectedApplication");

                await navigateToDeveloperPage("apps");

            } catch (err) {
                alert("Nie udało się usunąć aplikacji.");
            }
        });
    }
}