import { api } from "../api/apiDeveloper.js";
import { navigateToDeveloperPage } from "../../sideBar.js";

export function loadApplicationActions() {
    const cancelButton = document.getElementById("cancel-create-application");
    const createButton = document.getElementById("create-application-button");

    const editApplication = JSON.parse(
        sessionStorage.getItem("editApplication")
    );
    console.log("EDIT MODE:", editApplication);

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
                console.log("DRAFT:", draft);
                console.log("EDIT:", editApplication);
                if (editApplication) {
                    response = await api.updateApplication({
                        ...draft,
                        appId: draft.appId || editApplication.appId || editApplication.id,
                    });

                    alert("Zmiany zostały zapisane.");
                    console.log("UPDATED APPLICATION:", response);
                } else {
                    response = await api.registerApplication(draft);

                    console.log("REGISTERED APPLICATION:", response);
                }

                sessionStorage.removeItem("applicationDraft");
                sessionStorage.removeItem("editApplication");

                window.applicationLogoFile = null;
                window.applicationTosPdfFile = null;

                await navigateToDeveloperPage("apps");

            } catch (err) {
                console.error("Błąd zapisu aplikacji:", err);

                console.log("STATUS:", err.status);
                console.log("DATA:", err.data);
                console.log("STRING:", JSON.stringify(err.data, null, 2));

                alert("Nie udało się zapisać aplikacji.");
            }
        });
    }
}