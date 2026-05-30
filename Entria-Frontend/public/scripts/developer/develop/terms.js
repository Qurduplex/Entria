import {
  getApplicationDraft,
  saveApplicationDraft
} from "./aplicationDetails.js";

export function loadTerms() {
  const termsInput = document.getElementById(
    "application-terms-url"
  );

  if (!termsInput) {
    return;
  }

  const savedDraft = getApplicationDraft();

  termsInput.value = savedDraft.termsUrl ?? "";

  termsInput.addEventListener("input", () => {
    const draft = getApplicationDraft();

    draft.termsUrl = termsInput.value.trim();

    saveApplicationDraft(draft);

    console.log("Draft aplikacji:", draft);
  });
}