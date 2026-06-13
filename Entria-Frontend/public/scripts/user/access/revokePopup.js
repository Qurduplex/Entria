import { userApi } from "../api/apiUser.js";
import { showAlert } from "../../alert.js";
import { cloneTemplate, ref } from "./templates.js";

/**
 * Otwiera popup potwierdzenia cofnięcia zgody.
 *
 * @param {object} app
 * @param {() => void} onRevoked
 */
export function openRevokePopup(app, onRevoked) {
  const overlay = cloneTemplate("tpl-revoke-popup");

  ref(overlay, "app-name").textContent = app.appName || "tej aplikacji";

  const dialog = ref(overlay, "dialog");
  const cancelBtn = ref(overlay, "cancel");
  const confirmBtn = ref(overlay, "confirm");

  const close = () => overlay.remove();

  overlay.addEventListener("click", (e) => {
    if (e.target === overlay) close();
  });
  dialog.addEventListener("click", (e) => e.stopPropagation());
  cancelBtn.addEventListener("click", close);

  confirmBtn.addEventListener("click", async () => {
    confirmBtn.disabled = true;
    try {
      await userApi.revokeConsent(app.clientId);
      close();
      onRevoked?.(app);
      showAlert("Dostęp został cofnięty.", "success");
    } catch (err) {
      console.error("Nie udało się cofnąć zgody:", err);
      showAlert(err?.data?.error || "Nie udało się cofnąć dostępu.", "error");
      confirmBtn.disabled = false;
    }
  });

  document.body.appendChild(overlay);
}