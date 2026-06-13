import { api } from "../api/apiDeveloper.js";

function formatDate(dateString) {
    if (!dateString) return "-";

    const date = new Date(dateString);

    return date.toLocaleString("pl-PL", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
    });
}

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

    document.getElementById("application-authorize-url").textContent =
        app.authorizeUrl ?? "";

    document.getElementById("application-created-at").textContent =
        formatDate(app.createdAt);

    document.getElementById("application-last-update").textContent =
        formatDate(app.updatedAt);

    document.getElementById("application-client-secret").textContent =
        "••••••••••••••••";
}

export function loadApplicationActions() {
    const copyClientIdButton =
        document.getElementById("copy-client-id");

    const copyClientSecretButton =
        document.getElementById("copy-client-secret");

    const refreshClientSecretButton =
        document.getElementById("refresh-client-secret");

    const copyRedirectUriButton =  
        document.getElementById("copy-redirect-uri");

    const copyAuthorizeUrlButton =
        document.getElementById("copy-authorize-url");

    const refreshAuthorizeUrlButton = document.getElementById(
        "refresh-authorize-url"
    );

    if (copyClientIdButton) {
        copyClientIdButton.addEventListener("click", async () => {
            const clientId =
                document.getElementById("application-client-id")
                    ?.textContent || "";

            await navigator.clipboard.writeText(clientId);
        });
    }

    if (copyClientSecretButton) {
        copyClientSecretButton.addEventListener("click", async () => {
            const clientSecret =
                document.getElementById("application-client-secret")
                    ?.textContent || "";

            if (
                !clientSecret ||
                clientSecret.includes("••••")
            ) {
                alert(
                    "Najpierw wygeneruj Client Secret."
                );
                return;
            }

            await navigator.clipboard.writeText(clientSecret);

            alert("Client Secret skopiowany.");
        });
    }

    if (refreshClientSecretButton) {
        refreshClientSecretButton.addEventListener(
            "click",
            async () => {
                try {
                    const selectedApp = JSON.parse(
                        sessionStorage.getItem(
                            "selectedApplication"
                        )
                    );

                    if (!selectedApp?.appId) {
                        return;
                    }

                    const response =
                        await api.regenerateClientSecret(
                            selectedApp.appId
                        );

                    document.getElementById(
                        "application-client-secret"
                    ).textContent =
                        response.clientSecret ?? "";

                    document.getElementById(
                        "application-client-id"
                    ).textContent =
                        response.clientId ?? "";

                    alert(
                        "Client Secret został wygenerowany ponownie."
                    );
                } catch (error) {
                    console.error(error);

                    alert(
                        "Nie udało się wygenerować Client Secret."
                    );
                }
            }
        );
    }

    if (copyRedirectUriButton) {
        copyRedirectUriButton.addEventListener("click", async () => {
            const redirectUri =
                document.getElementById("application-redirect-uri")
                    ?.textContent || "";

            await navigator.clipboard.writeText(redirectUri);
        });
    }

    if (copyAuthorizeUrlButton) {
        copyAuthorizeUrlButton.addEventListener("click", async () => {
            const authorizeUrl =
                document.getElementById("application-authorize-url")
                    ?.textContent || "";

            await navigator.clipboard.writeText(authorizeUrl);
        });
    }

    if (refreshAuthorizeUrlButton) {
        refreshAuthorizeUrlButton.addEventListener(
            "click",
            async () => {
                try {
                    const selectedApp = JSON.parse(
                        sessionStorage.getItem(
                            "selectedApplication"
                        )
                    );

                    if (!selectedApp?.appId) {
                        return;
                    }

                    const response =
                        await api.regenerateAuthorizeUrl(
                            selectedApp.appId
                        );

                    document.getElementById(
                        "application-authorize-url"
                    ).textContent =
                        response.authorizeUrl ?? "";

                    alert(
                        "Authorize URL został wygenerowany ponownie."
                    );
                } catch (error) {
                    console.error(error);

                    alert(
                        "Nie udało się wygenerować Authorize URL."
                    );
                }
            }
        );
    }
}