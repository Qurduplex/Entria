import { api } from "../api/apiDeveloper.js";
import { navigateToDeveloperPage } from "../../sideBar.js";

export function loadApplicationActions() {
    const cancelButton = document.getElementById("cancel-create-application");
    const createButton = document.getElementById("create-application-button");

    const editApplication = JSON.parse(
        sessionStorage.getItem("editApplication")
    );

    if (createButton) {
        createButton.textContent = editApplication
            ? "Zapisz zmiany"
            : "Utwórz aplikację";
    }

    if (cancelButton) {
        cancelButton.addEventListener("click", async () => {
            sessionStorage.removeItem("applicationDraft");
            sessionStorage.removeItem("editApplication");

            window.applicationLogoFile = null;
            window.applicationTosPdfFile = null;

            await navigateToDeveloperPage("apps");
        });
    }

    if (createButton) {
        createButton.addEventListener("click", async () => {
            const draft = JSON.parse(
                sessionStorage.getItem("applicationDraft")
            ) || {};

            draft.logoFile = window.applicationLogoFile || null;
            draft.tosPdfFile = window.applicationTosPdfFile || null;

            const redirectUri =
                draft.redirectUri ||
                draft.redirectUris?.[0] ||
                "";

            draft.redirectUri = redirectUri;

            if (!draft.name || draft.name.length < 3 || draft.name.length > 30) {
                alert("Nazwa aplikacji musi mieć od 3 do 30 znaków.");
                return;
            }

            if (!draft.redirectUri) {
                alert("Podaj Redirect URI.");
                return;
            }

            if (!editApplication && !draft.logoFile) {
                alert("Dodaj logo aplikacji.");
                return;
            }

            if (!editApplication && !draft.tosPdfFile) {
                alert("Dodaj regulamin PDF.");
                return;
            }

            try {
                let response;
                if (editApplication) {
                    const { permissions, ...draftWithoutPermissions } = draft;

                    response = await api.updateApplication({
                        ...draftWithoutPermissions,
                        appId: draft.appId || editApplication.appId || editApplication.id,
                    });

                    alert("Zmiany zostały zapisane.");
                } else {
                    response = await api.registerApplication(draft);
                }

                sessionStorage.removeItem("applicationDraft");
                sessionStorage.removeItem("editApplication");

                window.applicationLogoFile = null;
                window.applicationTosPdfFile = null;

                await navigateToDeveloperPage("apps");

            } catch (err) {
                alert("Nie udało się zapisać aplikacji.");
            }
        });
    }
}