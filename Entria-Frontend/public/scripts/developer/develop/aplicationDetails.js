export function loadApplicationDetails() {
    const nameInput = document.getElementById("application-name");

    if (!nameInput) {
        return;
    }

    const savedDraft = getApplicationDraft();

    nameInput.value = savedDraft.name ?? "";

    function saveDetails() {
        const draft = getApplicationDraft();

        draft.name = nameInput.value.trim();

        saveApplicationDraft(draft);
    }

    nameInput.addEventListener("input", saveDetails);
}

export function getApplicationDraft() {
    return JSON.parse(sessionStorage.getItem("applicationDraft")) || {};
}

export function saveApplicationDraft(draft) {
    sessionStorage.setItem("applicationDraft", JSON.stringify(draft));
}