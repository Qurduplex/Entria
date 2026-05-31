import { openOAuthPopup } from "./consentPopup.js";

document.addEventListener("DOMContentLoaded", async () => {
  const container = document.getElementById("oauth-popup-container");

  const res = await fetch("../../components/oauth/consentPopup.html");
  container.innerHTML = await res.text();

  openOAuthPopup("login");
});