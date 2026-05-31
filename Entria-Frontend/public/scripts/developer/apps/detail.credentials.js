export function loadApplicationCredentials() {

  const app = JSON.parse(
    sessionStorage.getItem("selectedApplication")
  );

  if (!app) {
    return;
  }

  const elements = {
    clientId: document.getElementById("application-client-id"),
    clientSecret: document.getElementById("application-client-secret"),
    redirectUri: document.getElementById("application-redirect-uri"),
    domain: document.getElementById("application-domain"),
    description: document.getElementById("application-description"),
    createdAt: document.getElementById("application-created-at"),
    lastUpdate: document.getElementById("application-last-update"),
    copyButton: document.getElementById("copy-client-id"),
    toggleSecret: document.getElementById("toggle-client-secret"),
  };

  const secret =
    app.clientSecret ?? "entria_secret_293847239847";

  let secretVisible = false;

  if (elements.clientId) {
    elements.clientId.textContent =
      app.clientId ?? "pf_client_a3f9d2e1b8c74052";
  }

  if (elements.clientSecret) {
    elements.clientSecret.textContent =
      "••••••••••••••••";
  }

  if (elements.redirectUri) {
    elements.redirectUri.textContent =
      app.redirectUri;
  }

  if (elements.domain) {
    elements.domain.textContent =
      app.domain ?? "parkflow.pl";
  }

  if (elements.description) {
    elements.description.textContent =
      app.description;
  }

  if (elements.createdAt) {
    elements.createdAt.textContent =
      app.createdAtDetailed ?? "12 stycznia 2026, 10:32";
  }

  if (elements.lastUpdate) {
    elements.lastUpdate.textContent =
      app.lastUpdate ?? "18 marca 2026, 14:07";
  }

  if (elements.copyButton) {

    elements.copyButton.addEventListener("click", async () => {

      await navigator.clipboard.writeText(
        app.clientId ?? ""
      );

    });

  }

  if (elements.toggleSecret) {

    elements.toggleSecret.addEventListener("click", () => {

      secretVisible = !secretVisible;

      elements.clientSecret.textContent =
        secretVisible
          ? secret
          : "••••••••••••••••";

    });

  }

}