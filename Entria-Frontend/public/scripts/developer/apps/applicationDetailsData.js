export function loadApplicationDetailsData() {

  const app = JSON.parse(
    sessionStorage.getItem("selectedApplication")
  );

  if (!app) {
    return;
  }

  const logo = document.getElementById("application-logo");
  const name = document.getElementById("application-name");

  if (logo) {
    logo.textContent = app.logo.initials;
    logo.style.backgroundColor = app.logo.color;
  }

  if (name) {
    name.textContent = app.name;
  }

}