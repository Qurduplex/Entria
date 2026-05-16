import { initProfilePassword } from "./profile/password.js";
import { initProfileBasicData } from "./profile/profileinformation.js";

async function loadComponent(id, path) {
  const element = document.getElementById(id);
  if (!element) return;

  const res = await fetch(path);
  element.innerHTML = await res.text();
}

export async function initDeveloperProfile() {

    await loadComponent(
    "profile-basic-data",
    "../../pages/developer/fragments/profile/profileBasicData.html"
    );
    initProfileBasicData();

    await loadComponent(
    "profile-password",
    "../../pages/developer/fragments/profile/profilePassword.html"
    );
    initProfilePassword();

}