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

    disableButton.addEventListener("click", () => {

      console.log(
        "Wyłączenie aplikacji:",
        app.id
      );

    });

  }

  if (deleteButton) {

    deleteButton.addEventListener("click", () => {

      const confirmed = confirm(
        "Czy na pewno chcesz usunąć aplikację?"
      );

      if (!confirmed) {
        return;
      }

      console.log(
        "Usunięcie aplikacji:",
        app.id
      );

    });

  }

}