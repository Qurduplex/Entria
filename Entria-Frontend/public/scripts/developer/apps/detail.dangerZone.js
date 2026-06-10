import { api } from "../api/apiDeveloper.js";
import { navigateToDeveloperPage } from "../../sideBar.js";
export function loadApplicationDangerZone() {

  const app = JSON.parse(
    sessionStorage.getItem("selectedApplication")
  );

  if (!app) {
    return;
  }

  const editButton = document.getElementById(
    "edit-application-button"
  );

  const disableButton = document.getElementById(
    "disable-application-button"
  );

  const deleteButton = document.getElementById(
    "delete-application-button"
  );

  if (editButton) {

    editButton.addEventListener("click", () => {

      console.log(
        "Edycja aplikacji:",
        app.id
      );

    });

  }

  if (disableButton) {

      if (!app.active) {
          disableButton.disabled = true;

          disableButton.classList.add(
              "opacity-50",
              "cursor-not-allowed"
          );

          disableButton.textContent = "Aplikacja wyłączona";
      }

      disableButton.addEventListener("click", async () => {

          if (!app.active) {
              return;
          }

          const confirmed = confirm(
              "Czy na pewno chcesz dezaktywować aplikację?"
          );

          if (!confirmed) {
              return;
          }

          const appId = app.id || app.appId;

          try {
              await api.deactivateApplication(appId);

              app.active = false;

              sessionStorage.setItem(
                  "selectedApplication",
                  JSON.stringify(app)
              );

              disableButton.disabled = true;

              disableButton.classList.add(
                  "opacity-50",
                  "cursor-not-allowed"
              );

              disableButton.textContent =
                  "Aplikacja wyłączona";

          } catch (err) {
              console.error(err);
          }
      });
  }

  if (deleteButton) {
      deleteButton.addEventListener("click", async () => {
          const confirmed = confirm(
              "Czy na pewno chcesz trwale usunąć aplikację?"
          );

          if (!confirmed) {
              return;
          }

          const appId = app.id || app.appId;

          if (!appId) {
              console.error("Brak ID aplikacji:", app);
              alert("Nie znaleziono ID aplikacji.");
              return;
          }

          try {
              await api.deleteApplication(appId);

              alert("Aplikacja została usunięta.");
              await navigateToDeveloperPage("apps");
              sessionStorage.removeItem("selectedApplication");

          } catch (err) {
              console.error("Błąd usuwania aplikacji:", err);
              alert("Nie udało się usunąć aplikacji.");
          }
      });
  }

}