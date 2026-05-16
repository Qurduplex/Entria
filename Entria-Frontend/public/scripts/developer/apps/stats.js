export function loadAppsStats() {
  const data = {
    totalApplications: 4,
    loginsLast30Days: 1024,
    activeApplications: 3,
  };

  const appsTotal = document.getElementById("apps-total");
  const appsLogins = document.getElementById("apps-logins");
  const appsActive = document.getElementById("apps-active");

  if (!appsTotal || !appsLogins || !appsActive) {
    return;
  }

  appsTotal.textContent = data.totalApplications;
  appsLogins.textContent = data.loginsLast30Days;
  appsActive.textContent = data.activeApplications;

  /*
  const response = await fetch("/api/developer/apps/stats", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
    }
  });

  const data = await response.json();
  */
}