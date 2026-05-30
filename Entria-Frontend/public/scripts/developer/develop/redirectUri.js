import {
  getApplicationDraft,
  saveApplicationDraft
} from "./aplicationDetails.js";

export function loadRedirectUri() {
  const redirectInput = document.getElementById(
    "application-redirect-uri"
  );

  if (!redirectInput) {
    return;
  }

  const savedDraft = getApplicationDraft();

  redirectInput.value = savedDraft.redirectUri ?? "";

  redirectInput.addEventListener("input", () => {
    const draft = getApplicationDraft();

    draft.redirectUri = redirectInput.value.trim();

    saveApplicationDraft(draft);

    console.log("Draft aplikacji:", draft);
  });
}