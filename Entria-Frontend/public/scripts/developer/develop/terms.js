import {
    getApplicationDraft,
    saveApplicationDraft
} from "./aplicationDetails.js";

export function loadTerms() {
    const termsInput = document.getElementById("application-terms-pdf");
    const fileName = document.getElementById("application-terms-file-name");

    if (!termsInput || !fileName) {
        return;
    }

    termsInput.addEventListener("change", () => {
        const file = termsInput.files[0];

        if (!file) return;

        if (file.type !== "application/pdf") {
            alert("Regulamin musi być plikiem PDF.");
            termsInput.value = "";
            fileName.textContent = "Nie wybrano pliku";
            return;
        }

        const maxSize = 5 * 1024 * 1024;

        if (file.size > maxSize) {
            alert("PDF nie może być większy niż 5 MB.");
            termsInput.value = "";
            fileName.textContent = "Nie wybrano pliku";
            return;
        }

        window.applicationTosPdfFile = file;

        const draft = getApplicationDraft();

        draft.tosPdf = {
            name: file.name,
            type: file.type,
            size: file.size,
        };

        saveApplicationDraft(draft);

        fileName.textContent = `${file.name} • ${(file.size / 1024).toFixed(0)} KB`;

        console.log("Draft aplikacji:", draft);
    });
}