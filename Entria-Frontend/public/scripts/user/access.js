import { userApi } from "./api/apiUser.js";
import { renderConsents } from "./access/consents.js";
import { renderLogoutAllCard } from "./access/logoutAll.js";

export async function initUserAccess() {
  const list = document.getElementById("consents-list");
  const logoutContainer = document.getElementById("logout-all-container");
  if (!list) return;

  showLoading(list);

  let consents = [];
  try {
    consents = await userApi.getConsents();
  } catch (err) {
    console.error("Nie udało się pobrać zgód:", err);
    showError(list);
    return;
  }

  renderConsents(list, consents);
  renderLogoutAllCard(logoutContainer);
}

function showLoading(list) {
  list.replaceChildren();
  const p = document.createElement("p");
  p.className = "text-[13px] text-[#8D8D8D] px-1";
  p.textContent = "Ładowanie…";
  list.appendChild(p);
}

function showError(list) {
  list.replaceChildren();
  const p = document.createElement("p");
  p.className = "text-[13px] text-red-500 px-1";
  p.textContent = "Nie udało się pobrać listy aplikacji.";
  list.appendChild(p);
}