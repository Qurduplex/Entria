import {loadApplicationDetails} from "./develop/aplicationDetails.js";
import { loadRedirectUri } from "./develop/redirectUri.js";
import { loadTerms } from "./develop/terms.js";
import { loadApplicationLogo } from "./develop/logo.js";
import { loadPermissions } from "./develop/permission.js";
import { loadApplicationActions } from "./develop/applicationActions.js";

async function loadComponent(id, path) {
  const element = document.getElementById(id);
  if (!element) return;

  const res = await fetch(path);
  element.innerHTML = await res.text();
}

export async function initApplicationDevelopment() {
    
    await loadComponent(
        "application-details-container",
        "../../pages/developer/fragments/develop/applicationDetails.html"
    );
    loadApplicationDetails();

    await loadComponent(
        "application-logo-container",
        "../../pages/developer/fragments/develop/applicationLogo.html"
    );
    loadApplicationLogo();

    await loadComponent(
        "redirect-uri-container",
        "../../pages/developer/fragments/develop/redirectUri.html"
    );
    loadRedirectUri();

    await loadComponent(
        "terms-container",
        "../../pages/developer/fragments/develop/terms.html"
    );
    loadTerms();

    await loadComponent(
        "permissions-container",
        "../../pages/developer/fragments/develop/permission.html"
    );
    loadPermissions();

    loadApplicationActions();
}

