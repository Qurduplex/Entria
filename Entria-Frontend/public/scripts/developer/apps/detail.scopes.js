const mockScopes = [
  "openid",
  "email",
  "first_name",
  "last_name",
  "phone"
];

export function loadApplicationScopes() {

  const app = JSON.parse(
    sessionStorage.getItem("selectedApplication")
  );

  const container = document.getElementById("data-application-scopes");

  if (!container) {
    return;
  }

  const scopes = app?.scopes ?? mockScopes;

  container.innerHTML = scopes.map(scope => `
    <div class="rounded-lg border border-gray-300 bg-gray-100 px-5 py-2 text-sm font-medium text-gray-900">
      ${scope}
    </div>
  `).join("");

}