import { api } from "../api/apiDeveloper.js";
import { navigateToDeveloperPage } from "../../sideBar.js";

export function loadApplicationActions() {
    const cancelButton = document.getElementById("cancel-create-application");
    const createButton = document.getElementById("create-application-button");

    if (cancelButton) {
        cancelButton.addEventListener("click", async () => {
            sessionStorage.removeItem("applicationDraft");

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

            if (!draft.logoFile) {
                alert("Dodaj logo aplikacji.");
                return;
            }

            if (!draft.tosPdfFile) {
                alert("Dodaj regulamin PDF.");
                return;
            }

            console.log("Aplikacja do zapisania:", draft);

            try {
                console.log("PERMISSIONS:", draft.permissions);
                const response = await api.registerApplication(draft);

                console.log("REGISTERED APPLICATION:", response);

                sessionStorage.removeItem("applicationDraft");

                window.applicationLogoFile = null;
                window.applicationTosPdfFile = null;

                await navigateToDeveloperPage("apps");
            } catch (err) {
                console.error("Błąd tworzenia aplikacji:", err);

                console.log("STATUS:", err.status);
                console.log("DATA:", err.data);
                console.log("STRING:", JSON.stringify(err.data, null, 2));
            }
        });
    }
}