import { navigateToDeveloperPage } from "../../sideBar.js";

export function loadApplicationActions() {
  const cancelButton = document.getElementById(
    "cancel-create-application"
  );

  const createButton = document.getElementById(
    "create-application-button"
  );

  if (cancelButton) {
    cancelButton.addEventListener("click", async () => {

      sessionStorage.removeItem(
        "applicationDraft"
      );

      await navigateToDeveloperPage(
        "apps"
      );
    });
  }

  if (createButton) {
    createButton.addEventListener("click", async () => {

      const draft = JSON.parse(
        sessionStorage.getItem("applicationDraft")
      );

      console.log(
        "Aplikacja do zapisania:",
        draft
      );

      /*
      Tutaj później:
      await createApplication(draft);
      */

      sessionStorage.removeItem(
        "applicationDraft"
      );

      await navigateToDeveloperPage(
        "apps"
      );
    });
  }
}