import {
    getApplicationDraft,
    saveApplicationDraft
} from "./aplicationDetails.js";

export function loadApplicationLogo() {
    const logoInput = document.getElementById("application-logo");
    const uploadState = document.getElementById("logo-upload-state");
    const previewState = document.getElementById("logo-preview-state");
    const previewImage = document.getElementById("application-logo-preview");
    const previewName = document.getElementById("application-logo-name");
    const previewSize = document.getElementById("application-logo-size");
    const changeButton = document.getElementById("change-logo-button");
    const removeButton = document.getElementById("remove-logo-button");

    if (
        !logoInput ||
        !uploadState ||
        !previewState ||
        !previewImage ||
        !previewName ||
        !previewSize
    ) {
        return;
    }

    const draft = getApplicationDraft();

    if (draft.logo?.dataUrl) {
        showLogoPreview(
            draft.logo,
            uploadState,
            previewState,
            previewImage,
            previewName,
            previewSize
        );
    } else if (draft.logoUrl) {
        showLogoPreview(
            {
                name: "Aktualne logo",
                size: 0,
                dataUrl: draft.logoUrl,
            },
            uploadState,
            previewState,
            previewImage,
            previewName,
            previewSize
        );
    }

    logoInput.addEventListener("change", () => {
        const file = logoInput.files[0];

        if (!file) return;

        const allowedTypes = ["image/png", "image/svg+xml"];

        if (!allowedTypes.includes(file.type)) {
            alert("Logo musi być w formacie PNG lub SVG.");
            logoInput.value = "";
            return;
        }

        const maxSize = 2 * 1024 * 1024;

        if (file.size > maxSize) {
            alert("Logo nie może być większe niż 2 MB.");
            logoInput.value = "";
            return;
        }

        window.applicationLogoFile = file;

        const reader = new FileReader();

        reader.onload = () => {
            const currentDraft = getApplicationDraft();

            currentDraft.logo = {
                name: file.name,
                type: file.type,
                size: file.size,
                dataUrl: reader.result,
            };

            saveApplicationDraft(currentDraft);

            showLogoPreview(
                currentDraft.logo,
                uploadState,
                previewState,
                previewImage,
                previewName,
                previewSize
            );

            console.log("Draft aplikacji:", currentDraft);
        };

        reader.readAsDataURL(file);
    });

    changeButton?.addEventListener("click", () => {
        logoInput.click();
    });

    removeButton?.addEventListener("click", () => {
        const currentDraft = getApplicationDraft();

        delete currentDraft.logo;
        window.applicationLogoFile = null;

        saveApplicationDraft(currentDraft);

        logoInput.value = "";

        previewState.classList.add("hidden");
        uploadState.classList.remove("hidden");

        console.log("Usunięto logo:", currentDraft);
    });
}

function showLogoPreview(
    logo,
    uploadState,
    previewState,
    previewImage,
    previewName,
    previewSize
) {
    uploadState.classList.add("hidden");
    previewState.classList.remove("hidden");

    previewImage.src = logo.dataUrl;
    previewName.textContent = logo.name;
    previewSize.textContent = logo.size
        ? `${(logo.size / 1024).toFixed(0)} KB`
        : "Zapisane logo";
}