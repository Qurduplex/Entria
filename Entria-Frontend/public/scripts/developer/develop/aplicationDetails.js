export function loadApplicationDetails() {
  const nameInput = document.getElementById("application-name");
  const domainInput = document.getElementById("application-domain");
  const descriptionInput = document.getElementById("application-description");

  if (!nameInput || !domainInput || !descriptionInput) {
    return;
  }

  const savedDraft = getApplicationDraft();

  nameInput.value = savedDraft.name ?? "";
  domainInput.value = savedDraft.domain ?? "";
  descriptionInput.value = savedDraft.description ?? "";

  function saveDetails() {
    const draft = getApplicationDraft();

    draft.name = nameInput.value.trim();
    draft.domain = domainInput.value.trim();
    draft.description = descriptionInput.value.trim();

    saveApplicationDraft(draft);

    console.log("Draft aplikacji:", draft);
  }

  nameInput.addEventListener("input", saveDetails);
  domainInput.addEventListener("input", saveDetails);
  descriptionInput.addEventListener("input", saveDetails);
}

export function getApplicationDraft() {
  return JSON.parse(
    sessionStorage.getItem("applicationDraft")
  ) || {};
}

export function saveApplicationDraft(draft) {
  sessionStorage.setItem(
    "applicationDraft",
    JSON.stringify(draft)
  );
}