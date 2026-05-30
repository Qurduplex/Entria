import { navigateToDeveloperPage } from "../../sideBar.js";

let isListenerAdded = false;

export function loadAppsDevelop() {
 console.log("initDeveloperApps działa");
    document.onclick = async (event) => {
        console.log("Kliknięto w:", event.target);

        const addButton = event.target.closest("#add-application-button");

        if (!addButton) {
            return;
        }

        console.log("To jest przycisk dodawania");

        await navigateToDeveloperPage("develop");
    };
}