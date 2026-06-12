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

                openClientSecretModal(response);
            } catch (err) {
                console.error("Błąd tworzenia aplikacji:", err);

                console.log("STATUS:", err.status);
                console.log("DATA:", err.data);
                console.log("STRING:", JSON.stringify(err.data, null, 2));
            }
        });
    }
}

function openClientSecretModal(response) {
    console.log("OPEN MODAL RESPONSE:", response);

    const modal = document.getElementById("client-secret-modal");
    console.log("MODAL:", modal);

    if (!modal) {
        alert("Nie znaleziono modala client-secret-modal w HTML");
        return;
    }

    document.getElementById("created-client-id").value =
        response.clientId || response.client_id || "";

    document.getElementById("created-client-secret").value =
        response.clientSecret || response.client_secret || "";

    document.getElementById("created-authorize-url").value =
        response.authorizeUrl || response.authorize_url || "";

    modal.classList.remove("hidden");
    modal.classList.add("flex");

    initClientSecretModalActions();
}

function initClientSecretModalActions() {
    const copyButton = document.getElementById("copy-client-secret");
    const closeButton = document.getElementById("close-client-secret-modal");

    if (copyButton) {
        copyButton.onclick = async () => {
            const secret = document.getElementById("created-client-secret").value;

            await navigator.clipboard.writeText(secret);

            copyButton.textContent = "Skopiowano";
        };
    }

    if (closeButton) {
        closeButton.onclick = async () => {
            await navigateToDeveloperPage("apps");
        };
    }
}