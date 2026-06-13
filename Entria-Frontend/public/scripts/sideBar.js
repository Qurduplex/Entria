import { initDeveloperDashboard } from "./developer/dashboard.js";
import { initDeveloperProfile } from "./developer/profile.js";
import { initDeveloperApps } from "./developer/application.js";
import { initApplicationDetails } from "./developer/application-detail.js";
import { initApplicationDetailsLogs } from "./developer/application-detail-logs.js";
import { initApplicationDevelopment } from "./developer/develop.js";
import { initUserDashboard } from "./user/dashboard.js";
import { initUserProfile } from "./user/profile.js";
import { initUserApps } from "./user/apps.js";
import { initUserHistory } from "./user/history.js";
import { initUserSecurity } from "./user/security.js";
import { api } from "./developer/api/apiDeveloper.js";
import { initUserAccess } from "./user/access.js";
import { startSessionWatcher,logout } from "./session.js";

const sidebarConfigs = {
  user: {
    footer: {
      name: "Jan Kowalski",
      email: "jan.kowalski@example.com",
      initials: "JK",
      avatarColor: "#7C6FFF",
    },
    nav: [
      { type: "section", label: "KONTO" },
      
      {
        id: "profile",
        label: "Profil",
        icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="1.8"/></svg>`,
        fragment: "fragments/profile.html",
        breadcrumb: "Konto / Profil",
        title: "Profil",
        description: "Zarządzaj swoimi danymi osobowymi i ustawieniami konta",
      },
      
      { type: "section", label: "DOSTĘP" },
      {
        id: "consents",
        label: "Zgody i dostęp",
        icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"> 
        <path d="M20.25 14.2584V20.25C20.25 20.4489 20.171 20.6397 20.0303 20.7803C19.8897 20.921 19.6989 21 19.5 21H4.5C4.30109 21 4.11032 20.921 3.96967 20.7803C3.82902 20.6397 3.75 20.4489 3.75 20.25V14.2584C3.75 14.0595 3.82902 13.8688 3.96967 13.7281C4.11032 13.5874 4.30109 13.5084 4.5 13.5084C4.69891 13.5084 4.88968 13.5874 5.03033 13.7281C5.17098 13.8688 5.25 14.0595 5.25 14.2584V19.5H18.75V14.2584C18.75 14.0595 18.829 13.8688 18.9697 13.7281C19.1103 13.5874 19.3011 13.5084 19.5 13.5084C19.6989 13.5084 19.8897 13.5874 20.0303 13.7281C20.171 13.8688 20.25 14.0595 20.25 14.2584ZM8.25 17.2584H15.75C15.9489 17.2584 16.1397 17.1794 16.2803 17.0388C16.421 16.8981 16.5 16.7073 16.5 16.5084C16.5 16.3095 16.421 16.1188 16.2803 15.9781C16.1397 15.8374 15.9489 15.7584 15.75 15.7584H8.25C8.05109 15.7584 7.86032 15.8374 7.71967 15.9781C7.57902 16.1188 7.5 16.3095 7.5 16.5084C7.5 16.7073 7.57902 16.8981 7.71967 17.0388C7.86032 17.1794 8.05109 17.2584 8.25 17.2584ZM8.7075 12.2897L15.9516 14.2284C16.0151 14.2458 16.0807 14.2547 16.1466 14.2547C16.3275 14.2532 16.5018 14.1865 16.6373 14.0666C16.7729 13.9468 16.8605 13.782 16.8842 13.6027C16.9078 13.4233 16.8658 13.2414 16.7659 13.0906C16.666 12.9398 16.515 12.8301 16.3406 12.7819L9.09375 10.8384C8.9959 10.8023 8.89158 10.7869 8.78746 10.7934C8.68334 10.7999 8.58171 10.828 8.48907 10.8759C8.39643 10.9239 8.31482 10.9906 8.24944 11.0719C8.18406 11.1532 8.13636 11.2472 8.10938 11.348C8.0824 11.4488 8.07675 11.5541 8.09277 11.6572C8.10879 11.7602 8.14614 11.8588 8.20243 11.9467C8.25872 12.0345 8.33271 12.1096 8.41968 12.1672C8.50664 12.2248 8.60467 12.2637 8.7075 12.2812V12.2897ZM10.4372 7.60874L16.9322 11.3587C17.0175 11.408 17.1117 11.44 17.2094 11.4528C17.307 11.4657 17.4063 11.4592 17.5014 11.4337C17.5966 11.4082 17.6858 11.3642 17.7639 11.3042C17.8421 11.2442 17.9076 11.1694 17.9569 11.0841C18.0558 10.9119 18.0824 10.7076 18.0309 10.5159C17.9794 10.3242 17.854 10.1607 17.6822 10.0612L11.1872 6.30655C11.1018 6.25229 11.0064 6.21585 10.9066 6.19943C10.8067 6.18302 10.7046 6.18696 10.6064 6.21102C10.5082 6.23509 10.4158 6.27878 10.3349 6.33947C10.2539 6.40015 10.1861 6.47658 10.1355 6.56416C10.0849 6.65173 10.0525 6.74864 10.0403 6.84905C10.0281 6.94947 10.0363 7.05131 10.0645 7.14846C10.0927 7.2456 10.1403 7.33604 10.2043 7.41434C10.2683 7.49263 10.3476 7.55717 10.4372 7.60405V7.60874ZM18.6244 8.82749C18.6941 8.8973 18.7768 8.95269 18.8679 8.99052C18.959 9.02834 19.0566 9.04786 19.1553 9.04795C19.2539 9.04803 19.3516 9.02869 19.4428 8.99103C19.5339 8.95336 19.6168 8.89811 19.6866 8.82843C19.7564 8.75874 19.8118 8.676 19.8496 8.5849C19.8874 8.49381 19.9069 8.39616 19.907 8.29753C19.9071 8.19889 19.8878 8.10121 19.8501 8.01005C19.8124 7.91889 19.7572 7.83605 19.6875 7.76624L14.3841 2.46937C14.2424 2.33452 14.0536 2.26036 13.8581 2.26269C13.6625 2.26502 13.4756 2.34366 13.3371 2.48184C13.1987 2.62002 13.1197 2.8068 13.1171 3.00238C13.1144 3.19795 13.1882 3.38683 13.3228 3.52874L18.6244 8.82749Z" fill="currentColor"/> 
        </svg>`,
        fragment: "fragments/access.html",
        active: true,
        breadcrumb: "Dostęp / Zgody i dostęp",
        title: "Zgody i dostęp",
        description:
          "Kontruluj jakie dane udostępniasz poszczególnym aplikacjom",
      }
    ],
  },

  developer: {
    footer: {
      name: "TechWave",
      email: "TechWave Sp.z.o.o.",
      initials: "TW",
      avatarColor: "#2D9A63",
    },
    nav: [
      { type: "section", label: "DEVELOPER" },
      // {
      //   id: "dashboard",
      //   label: "Dashboard",
      //   active: true,
      //   icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      //   <path d="M9.75 3.75H5.25C4.85218 3.75 4.47064 3.90804 4.18934 4.18934C3.90804 4.47064 3.75 4.85218 3.75 5.25V9.75C3.75 10.1478 3.90804 10.5294 4.18934 10.8107C4.47064 11.092 4.85218 11.25 5.25 11.25H9.75C10.1478 11.25 10.5294 11.092 10.8107 10.8107C11.092 10.5294 11.25 10.1478 11.25 9.75V5.25C11.25 4.85218 11.092 4.47064 10.8107 4.18934C10.5294 3.90804 10.1478 3.75 9.75 3.75ZM9.75 9.75H5.25V5.25H9.75V9.75ZM18.75 3.75H14.25C13.8522 3.75 13.4706 3.90804 13.1893 4.18934C12.908 4.47064 12.75 4.85218 12.75 5.25V9.75C12.75 10.1478 12.908 10.5294 13.1893 10.8107C13.4706 11.092 13.8522 11.25 14.25 11.25H18.75C19.1478 11.25 19.5294 11.092 19.8107 10.8107C20.092 10.5294 20.25 10.1478 20.25 9.75V5.25C20.25 4.85218 20.092 4.47064 19.8107 4.18934C19.5294 3.90804 19.1478 3.75 18.75 3.75ZM18.75 9.75H14.25V5.25H18.75V9.75ZM9.75 12.75H5.25C4.85218 12.75 4.47064 12.908 4.18934 13.1893C3.90804 13.4706 3.75 13.8522 3.75 14.25V18.75C3.75 19.1478 3.90804 19.5294 4.18934 19.8107C4.47064 20.092 4.85218 20.25 5.25 20.25H9.75C10.1478 20.25 10.5294 20.092 10.8107 19.8107C11.092 19.5294 11.25 19.1478 11.25 18.75V14.25C11.25 13.8522 11.092 13.4706 10.8107 13.1893C10.5294 12.908 10.1478 12.75 9.75 12.75ZM9.75 18.75H5.25V14.25H9.75V18.75ZM18.75 12.75H14.25C13.8522 12.75 13.4706 12.908 13.1893 13.1893C12.908 13.4706 12.75 13.8522 12.75 14.25V18.75C12.75 19.1478 12.908 19.5294 13.1893 19.8107C13.4706 20.092 13.8522 20.25 14.25 20.25H18.75C19.1478 20.25 19.5294 20.092 19.8107 19.8107C20.092 19.5294 20.25 19.1478 20.25 18.75V14.25C20.25 13.8522 20.092 13.4706 19.8107 13.1893C19.5294 12.908 19.1478 12.75 18.75 12.75ZM18.75 18.75H14.25V14.25H18.75V18.75Z" fill="currentColor"/></svg>`,
      //   fragment: "fragments/dashboard.html",
      //   breadcrumb: "Developer / Dashboard",
      //   title: "Dashboard",
      //   description: "Witaj, TechWave - oto podsumowanie twoich aplikacji",
      // },
      {
        id: "apps",
        label: "Aplikacje",
        active: true,
        icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M16.5 0H1.5C1.10218 0 0.720644 0.158035 0.43934 0.43934C0.158035 0.720644 0 1.10218 0 1.5V16.5C0 16.8978 0.158035 17.2794 0.43934 17.5607C0.720644 17.842 1.10218 18 1.5 18H16.5C16.8978 18 17.2794 17.842 17.5607 17.5607C17.842 17.2794 18 16.8978 18 16.5V1.5C18 1.10218 17.842 0.720644 17.5607 0.43934C17.2794 0.158035 16.8978 0 16.5 0ZM16.5 16.5H1.5V1.5H16.5V16.5ZM13.5 9C13.5 9.19891 13.421 9.38968 13.2803 9.53033C13.1397 9.67098 12.9489 9.75 12.75 9.75H9.75V12.75C9.75 12.9489 9.67098 13.1397 9.53033 13.2803C9.38968 13.421 9.19891 13.5 9 13.5C8.80109 13.5 8.61032 13.421 8.46967 13.2803C8.32902 13.1397 8.25 12.9489 8.25 12.75V9.75H5.25C5.05109 9.75 4.86032 9.67098 4.71967 9.53033C4.57902 9.38968 4.5 9.19891 4.5 9C4.5 8.80109 4.57902 8.61032 4.71967 8.46967C4.86032 8.32902 5.05109 8.25 5.25 8.25H8.25V5.25C8.25 5.05109 8.32902 4.86032 8.46967 4.71967C8.61032 4.57902 8.80109 4.5 9 4.5C9.19891 4.5 9.38968 4.57902 9.53033 4.71967C9.67098 4.86032 9.75 5.05109 9.75 5.25V8.25H12.75C12.9489 8.25 13.1397 8.32902 13.2803 8.46967C13.421 8.61032 13.5 8.80109 13.5 9Z" fill="currentColor"/>
        </svg>
        `,
        fragment: "fragments/apps.html",
        breadcrumb: "Developer / Aplikacje",
        title: "Aplikacje",
        description: "Zarządzaj aplikacjami OAuth2 zarejestrowanymi w Entria",
      },
      {
        id: "apps-detail",
        hidden: true,
        icon: null,
        fragment: "fragments/apps-detail.html",
        breadcrumb: "Developer / Aplikacje / Szczegóły",
        title: "Szczegóły o aplikacji ",
        description: "Szczegóły o aplikacji OAuth2 zarejestrowanymi w Entria",
      },
      {
        id: "apps-detail-logs",
        hidden: true,
        icon: null,
        fragment: "fragments/apps-detail-logs.html",
        breadcrumb: "Developer / Aplikacje / Szczegóły",
        title: "Logi logowań aplikacji",
        description: "Wszystkie próby uwierzytelnienia przez ParkFlow",
      },
      {
        id: "develop",
        hidden: true,
        icon: null,
        fragment: "fragments/develop.html",
        breadcrumb: "Developer / Aplikacje / Nowa aplikacja",
        title: "Dodaj nową aplikację",
        description:
          "Zarejestruj nową aplikację OAuth2. Client ID i Secret zostaną wygenerowane automatycznie",
      },
      {
        id: "profile",
        label: "Profil",
        icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="1.8"/></svg>`,
        fragment: "fragments/profile.html",
        breadcrumb: "Developer / Profil",
        title: "Profil",
        description: "Zarządzaj danymi i ustawieniami konta",
      },
    ],
  },
};

// ─── STATE ──────────────────────────────────────────────────────────────────
let _currentMode = "user";

// ─── NAVIGATE ───────────────────────────────────────────────────────────────
async function navigate(item) {
  // Update active state
  const config = sidebarConfigs[_currentMode];

  config.nav.forEach((n) => {
    if (n.type !== "section") {
      n.active = n.id === item.id;
    }
  });

  renderSidebar();

  document.getElementById("topbar-breadcrumb").textContent =
    item.breadcrumb ?? "";
  document.getElementById("page-title").textContent = item.title ?? "";
  document.getElementById("page-description").textContent =
    item.description ?? "";

  // Load fragment
  const main = document.getElementById("page-content");
  if (!main || !item.fragment) return;

  try {
    const res = await fetch(item.fragment);
    if (!res.ok) throw new Error("Fragment not found");
    const html = await res.text();

    const doc = new DOMParser().parseFromString(html, "text/html");

    doc.querySelectorAll("script").forEach((s) => s.remove());

    main.replaceChildren(...doc.body.childNodes);

    // if (item.id === "dashboard" && _currentMode === "developer") {
    //   await initDeveloperDashboard();
    // }

    if (item.id === "profile" && _currentMode === "developer") {
      await initDeveloperProfile();
    }

    if (item.id === "apps" && _currentMode === "developer") {
      await initDeveloperApps();
    }

    if (item.id === "apps-detail" && _currentMode === "developer") {
      await initApplicationDetails();
    }

    if (item.id === "dashboard" && _currentMode === "user") {
      await initUserDashboard();
    }

    if (item.id === "profile" && _currentMode === "user") {
      await initUserProfile();
    }
    if (item.id === "apps" && _currentMode === "user") {
      await initUserApps();
    }
    if (item.id === "history" && _currentMode === "user") {
      await initUserHistory();
    }

    if (item.id === "security" && _currentMode === "user") {
      await initUserSecurity();
    }

    if (item.id === "consents" && _currentMode === "user") {
  await initUserAccess();
}

    if (item.id === "apps-detail-logs") {
      const app = JSON.parse(sessionStorage.getItem("selectedApplication"));

      if (app) {
        document.getElementById("page-description").textContent =
          `Wszystkie próby uwierzytelnienia przez ${app.name}`;
      }
    }

    if (item.id === "develop" && _currentMode === "developer") {
      await initApplicationDevelopment();
    }
  } catch {
    main.innerHTML = `<p class="text-white/40 text-sm">Nie można załadować strony.</p>`;
  }

  const pageLogo = document.getElementById("page-logo");

  if (item.id === "apps-detail") {
      const app = JSON.parse(
          sessionStorage.getItem("selectedApplication")
      );

      if (app && pageLogo) {
          pageLogo.classList.remove("hidden");
          pageLogo.classList.add("flex");

          if (app.logoUrl) {
              pageLogo.innerHTML = `
                  <img
                      src="${app.logoUrl}"
                      alt="${app.name}"
                      class="w-full h-full object-cover rounded-full"
                  />
              `;
          } else {
              pageLogo.textContent = app.name
                  ? app.name.substring(0, 2).toUpperCase()
                  : "AP";

              pageLogo.style.backgroundColor = "#7C6FFF";
          }

          document.getElementById("page-title").textContent =
              app.name ?? "";
      }
  } else if (pageLogo) {
      pageLogo.classList.add("hidden");
      pageLogo.classList.remove("flex");
  }
}

// ─── RENDER ─────────────────────────────────────────────────────────────────
function renderSidebar() {
  const config = sidebarConfigs[_currentMode];
  const nav = document.getElementById("sidebar-nav");
  const footer = document.getElementById("sidebar-footer");
  if (!nav || !footer) return;

  nav.innerHTML = config.nav
    .filter((item) => !item.hidden)
    .map((item) => {
      if (item.type === "section") {
        return `<div class="px-2 pt-6 pb-2 text-[14px] font-regular tracking-[0.12em] text-[#8D8D8D]">${item.label}</div>`;
      }
      const isActive = item.active;
      return `
            <a href="#"
               class="sidebar-nav-item flex items-center gap-3 px-3 py-[9px] rounded-[10px] text-[14px] mb-0.5 transition-colors
                      ${
                        isActive
                          ? "bg-[#7C6FFF]/15 text-white"
                          : "text-white/55 hover:bg-white/[0.04] hover:text-white/80"
                      }"
               data-id="${item.id}">
                <span class="w-[18px] h-[18px] flex-shrink-0 ${isActive ? "text-[#A89DFF]" : "text-white/30"}">${item.icon}</span>
                <span>${item.label}</span>
            </a>
        `;
    })
    .join("");

  // Attach click handlers
  nav.querySelectorAll(".sidebar-nav-item").forEach((el) => {
    el.addEventListener("click", (e) => {
      e.preventDefault();
      const id = el.dataset.id;
      const item = config.nav.find((n) => n.id === id);
      if (item) navigate(item);
    });
  });

  const f = config.footer;
  footer.innerHTML = `
    <div class="relative">
      <div id="user-menu-popup"
           class="hidden absolute bottom-full left-0 right-0 mb-2 rounded-[10px] border border-white/10 bg-[#17171C] p-1 shadow-[0_8px_40px_rgba(0,0,0,0.35)]">
        <button id="logout-btn"
                class="flex w-full items-center gap-2 rounded-[8px] px-3 py-2 text-[13px] text-white/70 transition-colors hover:bg-white/5 hover:text-white">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          Wyloguj
        </button>
      </div>

      <button id="user-menu-trigger"
              class="flex w-full items-center gap-3 rounded-[10px] px-1 py-1 text-left transition-colors hover:bg-white/[0.04]">
        <div class="w-8 h-8 rounded-full flex-shrink-0 flex items-center justify-center text-[11px] font-semibold text-white"
             style="background-color: ${f.avatarColor}">
          ${f.initials}
        </div>
        <div class="min-w-0 flex-1">
          <p class="text-[13px] font-medium text-white truncate">${f.name}</p>
          <p class="text-[11px] text-white/35 truncate">${f.email}</p>
        </div>
        <span class="text-white/30 text-[18px] leading-none shrink-0">⋯</span>
      </button>
    </div>
  `;

   const trigger = footer.querySelector("#user-menu-trigger");
  const popup = footer.querySelector("#user-menu-popup");
  const logoutBtn = footer.querySelector("#logout-btn");

  if (trigger && popup) {
    trigger.addEventListener("click", (e) => {
      e.stopPropagation();
      popup.classList.toggle("hidden");
    });

    // klik poza menu zamyka
    document.addEventListener("click", (e) => {
      if (!footer.contains(e.target)) popup.classList.add("hidden");
    });
  }

  if (logoutBtn) {
    logoutBtn.addEventListener("click", logout);
  }
}

// ─── PUBLIC API ──────────────────────────────────────────────────────────────
export function switchMode(mode) {
  if (!sidebarConfigs[mode]) return;
  _currentMode = mode;
  renderSidebar();
}

export function setActive(id) {
  const config = sidebarConfigs[_currentMode];
  config.nav.forEach((item) => {
    if (item.type !== "section") item.active = item.id === id;
  });
  renderSidebar();
}

export function getCurrentMode() {
  return _currentMode;
}

export async function navigateToDeveloperPage(id) {
  const config = sidebarConfigs[_currentMode];
  const item = config.nav.find((n) => n.id === id);

  if (item) {
    await navigate(item);
  }
}

// ─── MOBILE ──────────────────────────────────────────────────────────────────
export function toggleSidebar() {
  document.getElementById("sidebar")?.classList.toggle("sidebar-open");
  document.getElementById("sidebar-overlay")?.classList.toggle("hidden");
}

export function closeSidebar() {
  document.getElementById("sidebar")?.classList.remove("sidebar-open");
  document.getElementById("sidebar-overlay")?.classList.add("hidden");
}

// ─── INIT ────────────────────────────────────────────────────────────────────
function getInitials(firstName, lastName) {
    return `${firstName?.[0] || ""}${lastName?.[0] || ""}`.toUpperCase();
}
export async function initSidebar(mode = "user") {
  const token = localStorage.getItem("jwtToken");

  let payload = null;
  try {
    payload = JSON.parse(atob(token.split(".")[1]));
  } catch {
    payload = null;
  }

  if (!payload) {
    window.location.href = "/pages/LoginPage.html";
    return;
  }

  // wygasły token → login
  if (payload.exp && payload.exp * 1000 < Date.now()) {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("expiresAt");
    window.location.href = "/pages/LoginPage.html";
    return;
  }

  const requiredRole = mode === "developer" ? "DEVELOPER" : "USER";

  if (payload.role !== requiredRole) {
    window.location.href = payload.role === "DEVELOPER"
      ? "/pages/developer/DeveloperLayout.html"
      : "/pages/user/UserLayout.html";
    return;
  }
  startSessionWatcher();
    _currentMode = mode;

    const container = document.getElementById("sidebar-container");
    if (container) {
        const res = await fetch("../../components/sideBar.html");
        container.innerHTML = await res.text();
    }

    try {
        const profile = await api.getMyProfile();

        const fullName = `${profile.firstName || ""} ${profile.lastName || ""}`.trim();

        sidebarConfigs[_currentMode].footer = {
            name: fullName || "Użytkownik",
            email: localStorage.getItem("userEmail") || "",
            initials: getInitials(profile.firstName, profile.lastName) || "U",
            avatarColor: "#2D9A63",
        };

    } catch (err) {
        console.error("Nie udało się pobrać profilu:", err);
    }

    renderSidebar();

    document
        .getElementById("sidebar-overlay")
        ?.addEventListener("click", closeSidebar);

    const config = sidebarConfigs[_currentMode];
    const defaultItem = config.nav.find((n) => n.type !== "section" && n.active);

    if (defaultItem) await navigate(defaultItem);
}
