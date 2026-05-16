export function loadApplicationDetailsStats() {
  const app = JSON.parse(
    sessionStorage.getItem("selectedApplication")
  );

  if (!app) {
    return;
  }

  const data = {
    logins: app.logins ?? 0,
    activeUsers: app.activeUsers ?? 214,
    failedAttempts: app.failedAttempts ?? 12,
  };

  const loginsElement = document.getElementById("details-logins");
  const activeUsersElement = document.getElementById("details-active-users");
  const failedAttemptsElement = document.getElementById("details-failed-attempts");

  if (
    !loginsElement ||
    !activeUsersElement ||
    !failedAttemptsElement
  ) {
    return;
  }

  loginsElement.textContent = data.logins;
  activeUsersElement.textContent = data.activeUsers;
  failedAttemptsElement.textContent = data.failedAttempts;

  // PÓŹNIEJ API:
  /*
  const response = await fetch(`/api/developer/apps/${app.id}/stats`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
    },
  });

  const data = await response.json();

  loginsElement.textContent = data.logins;
  activeUsersElement.textContent = data.activeUsers;
  failedAttemptsElement.textContent = data.failedAttempts;
  */
}