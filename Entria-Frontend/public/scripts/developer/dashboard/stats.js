export function loadDashboardStats() {
  const data = {
    applications: {
      total: 4,
      active: 3,
      inactive: 1
    },

    users: {
      total: 128,
      weeklyGrowth: 12
    },

    logins: {
      total: 1248,
      period: "Ostatnie 24 godziny"
    },

    apiErrors: {
      total: 7,
      critical: 2
    }
  };

  document.getElementById("apps-count").textContent =
    data.applications.total;

  document.getElementById("apps-status").textContent =
    `${data.applications.active} aktywne · ${data.applications.inactive} nieaktywna`;

  document.getElementById("users-count").textContent =
    data.users.total;

  document.getElementById("users-growth").textContent =
    `+${data.users.weeklyGrowth} w tym tygodniu`;

  document.getElementById("logins-count").textContent =
    data.logins.total;

  document.getElementById("logins-period").textContent =
    data.logins.period;

  document.getElementById("errors-count").textContent =
    data.apiErrors.total;

  document.getElementById("errors-critical").textContent =
    `${data.apiErrors.critical} krytyczne błędy`;
}