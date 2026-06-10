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

        if (app.logoUrl) {
            logo.innerHTML = `
                <img
                    src="${app.logoUrl}"
                    alt="${app.name}"
                    class="w-full h-full object-cover rounded-full"
                />
            `;
        } else {
            logo.textContent = app.name
                ? app.name.substring(0, 2).toUpperCase()
                : "AP";

            logo.style.backgroundColor = "#7C6FFF";
        }
    }

    if (name) {
        name.textContent = app.name ?? "";
    }
}