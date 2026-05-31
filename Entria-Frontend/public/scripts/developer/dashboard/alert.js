export function loadSecurityAlerts() {
  const data = {
    username: "Bookie",
    failedAttempts: 2,
  };

  const alertBox = document.getElementById("security-alert");
  const alertUser = document.getElementById("alert-user");
  const alertMessage = document.getElementById("alert-message");

  console.log("alert function działa");
  console.log(alertBox);

  if (!alertBox || !alertUser || !alertMessage) {
    return;
  }

  if (data.failedAttempts > 0) {
    alertUser.textContent = data.username;
    alertMessage.textContent = ` ${data.failedAttempts} nieudanych logowań w ciągu ostatniej godziny`;

    alertBox.classList.remove("hidden");
  }
}