import { api } from "../../apiAuth.js";
import { showAlert } from "../../alert.js";
import { cloneTemplate, ref } from "./templates.js";

/**
 * Wstawia kartę "Wyloguj ze wszystkich urządzeń" do podanego kontenera.
 * @param {HTMLElement} container – #logout-all-container
 */
export function renderLogoutAllCard(container) {
  if (!container || container.childElementCount > 0) return;

  const card = cloneTemplate("tpl-logout-all-card");
  ref(card, "logout-all").addEventListener("click", openLogoutAllPopup);
  container.appendChild(card);
}

function openLogoutAllPopup() {
  const overlay = cloneTemplate("tpl-logout-all-popup");

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
      await api.logoutAll();
      localStorage.removeItem("jwtToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("expiresAt");
      localStorage.removeItem("userEmail");
      window.location.href = "/pages/LoginPage.html";
    } catch (err) {
      console.error("Nie udało się wylogować ze wszystkich urządzeń:", err);
      showAlert(err?.data?.message || "Nie udało się wylogować.", "error");
      confirmBtn.disabled = false;
    }
  });

  document.body.appendChild(overlay);
}