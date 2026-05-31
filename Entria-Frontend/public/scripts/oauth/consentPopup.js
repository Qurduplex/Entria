const allOAuthScopes = {
  email: {
    name: "Adres email",
    description: "Powiadomienia i logowanie"
  },

  first_name: {
    name: "Imię",
    description: "Imię użytkownika"
  },

  last_name: {
    name: "Nazwisko",
    description: "Nazwisko użytkownika"
  },

  avatar: {
    name: "Zdjęcie profilowe",
    description: "Wyświetlane w profilu aplikacji"
  },

  phone: {
    name: "Numer telefonu",
    description: "Kontakt z użytkownikiem"
  },

  pesel: {
    name: "PESEL",
    description: "Dane wrażliwe użytkownika"
  }
};

const oauthMockData = {
  appName: "ParkFlow",

  userData: {
    first_name: "Jan",
    last_name: "Kowalski",
    email: "jan.kowalski@example.com",
    avatar: "JK",
    phone: "+48 123 456 789",
    pesel: "00000000000"
  },

  requestedScopes: [
    {
      key: "email",
      required: true
    },
    {
      key: "first_name",
      required: true
    },
    {
      key: "last_name",
      required: true
    },
    {
      key: "avatar",
      required: false
    }
  ],

  grantedScopes: [
    "email",
    "first_name",
    "last_name",
    "avatar",
  ]
};

export function openOAuthPopup(mode = "register") {
  const modal = document.getElementById("oauth-popup");
  const title = document.getElementById("oauth-popup-title");
  const content = document.getElementById("oauth-popup-content");

  if (!modal || !title || !content) {
    return;
  }

  if (mode === "register") {
    title.innerHTML = `
      Rejestrujesz się w <strong>${oauthMockData.appName}</strong> – aplikacja prosi o dostęp do danych z Twojego konta Entria.
    `;

    content.innerHTML = renderRegisterConsent();
  }

  if (mode === "login") {
    title.innerHTML = `
      Logujesz się ponownie do <strong>${oauthMockData.appName}</strong> przez Entria.
    `;

    content.innerHTML = renderLoginConsent();
  }

  modal.classList.remove("hidden");
  modal.classList.add("flex");

  initOAuthPopupEvents();
}

function getRequestedScopes() {
  return oauthMockData.requestedScopes
    .map(scopeRequest => {
      const scopeData = allOAuthScopes[scopeRequest.key];

      if (!scopeData) {
        return null;
      }

      return {
        key: scopeRequest.key,
        required: scopeRequest.required,
        ...scopeData
      };
    })
    .filter(Boolean);
}

function getRequiredScopes() {
  return getRequestedScopes().filter(
    scope => scope.required
  );
}

function getOptionalScopes() {
  return getRequestedScopes().filter(
    scope => !scope.required
  );
}

function getGrantedScopes() {
  return oauthMockData.grantedScopes
    .map(scopeKey => {
      const scopeData = allOAuthScopes[scopeKey];

      if (!scopeData) {
        return null;
      }

      return {
        key: scopeKey,
        ...scopeData
      };
    })
    .filter(Boolean);
}

function hasGrantedScope(scopeKey) {
  return oauthMockData.grantedScopes.includes(scopeKey);
}

function getVisibleUserData() {
  const userData = oauthMockData.userData;

  return {
    firstName: hasGrantedScope("first_name")
      ? userData.first_name
      : null,

    lastName: hasGrantedScope("last_name")
      ? userData.last_name
      : null,

    email: hasGrantedScope("email")
      ? userData.email
      : null,

    avatar: hasGrantedScope("avatar")
      ? userData.avatar
      : null,

    phone: hasGrantedScope("phone")
      ? userData.phone
      : null,

    pesel: hasGrantedScope("pesel")
      ? userData.pesel
      : null
  };
}

function getDisplayUserName() {
  const visibleUserData = getVisibleUserData();

  const fullName = [
    visibleUserData.firstName,
    visibleUserData.lastName
  ]
    .filter(Boolean)
    .join(" ");

  if (fullName) {
    return fullName;
  }

  if (visibleUserData.email) {
    return visibleUserData.email;
  }

  return "Użytkownik Entria";
}

function renderUserAvatar() {
  const visibleUserData = getVisibleUserData();

  if (visibleUserData.avatar) {
    return `
      <div class="flex h-11 w-11 items-center justify-center rounded-full bg-[#A89DFF] text-sm font-semibold text-white">
        ${visibleUserData.avatar}
      </div>
    `;
  }

  return `
    <div class="flex h-11 w-11 items-center justify-center rounded-full bg-[#3A3A3D] text-sm font-semibold text-white">
      ?
    </div>
  `;
}

function renderUserAccountBox() {
  const visibleUserData = getVisibleUserData();

  const fullName = [
    visibleUserData.firstName,
    visibleUserData.lastName
  ]
    .filter(Boolean)
    .join(" ");

  return `
    <div class="flex items-center gap-4 rounded-full border border-gray-500 bg-[#222225] px-5 py-3">
      ${renderUserAvatar()}

      <div>
        ${
          fullName
            ? `<p class="font-semibold">${fullName}</p>`
            : ""
        }

        ${
          visibleUserData.email
            ? `<p class="text-sm text-gray-400">${visibleUserData.email}</p>`
            : ""
        }

        ${
          visibleUserData.phone
            ? `<p class="text-sm text-gray-400">${visibleUserData.phone}</p>`
            : ""
        }

        ${
          visibleUserData.pesel
            ? `<p class="text-sm text-gray-400">PESEL: ${visibleUserData.pesel}</p>`
            : ""
        }

        ${
          !fullName &&
          !visibleUserData.email &&
          !visibleUserData.phone &&
          !visibleUserData.pesel
            ? `<p class="font-semibold">Użytkownik Entria</p>`
            : ""
        }
      </div>
    </div>
  `;
}

function renderRegisterConsent() {
  const requiredScopes = getRequiredScopes();
  const optionalScopes = getOptionalScopes();

  return `
    <div class="space-y-6">

      ${
        requiredScopes.length
          ? `
            <div>
              <h3 class="mb-4 text-sm font-semibold uppercase text-gray-400">
                Wymagane
              </h3>

              ${requiredScopes.map(scope => `
                <div class="flex items-center justify-between border-b border-gray-600 py-4">
                  <div>
                    <p class="font-semibold">${scope.name}</p>
                    <p class="text-sm text-gray-400">${scope.description}</p>
                  </div>

                  <span class="rounded-full border border-[#A89DFF] bg-[#6B65A14F] px-3 py-1 text-xs text-[#A89DFF]">
                    WYMAGANE
                  </span>
                </div>
              `).join("")}
            </div>
          `
          : ""
      }

      ${
        optionalScopes.length
          ? `
            <div>
              <h3 class="mb-4 text-sm font-semibold uppercase text-gray-400">
                Opcjonalne
              </h3>

              ${optionalScopes.map(scope => `
                <div class="flex items-center justify-between border-b border-gray-600 py-4">
                  <div>
                    <p class="font-semibold">${scope.name}</p>
                    <p class="text-sm text-gray-400">${scope.description}</p>
                  </div>

                  <label class="relative inline-flex cursor-pointer items-center">
                    <input
                      type="checkbox"
                      class="peer sr-only"
                      data-optional-scope="${scope.key}"
                      checked
                    />

                    <div
                      class="h-6 w-11 rounded-full bg-gray-700 transition peer-checked:bg-[#A89DFF]"
                    ></div>

                    <div
                      class="absolute left-[2px] h-5 w-5 rounded-full bg-white transition-transform duration-200 peer-checked:translate-x-5"
                    ></div>
                  </label>
                </div>
              `).join("")}
            </div>
          `
          : ""
      }

      <button
        id="oauth-accept-button"
        class="w-full rounded-xl border border-gray-400 py-3 text-white hover:bg-white/5"
      >
        Zarejestruj się w ${oauthMockData.appName}
      </button>

      <button
        id="oauth-cancel-button"
        class="w-full rounded-xl border border-gray-400 py-3 text-white hover:bg-white/5"
      >
        Anuluj
      </button>

      <p class="text-center text-sm text-gray-500">
        Rejestrując się akceptujesz
        <span class="font-semibold text-[#A89DFF]">
          regulamin ${oauthMockData.appName}
        </span>
        oraz
        <span class="font-semibold text-[#A89DFF]">
          politykę prywatności Entria
        </span>.
      </p>

    </div>
  `;
}

function renderLoginConsent() {
  const grantedScopes = getGrantedScopes();
  const displayUserName = getDisplayUserName();

  return `
    <div class="space-y-6">

      <div>
        <h3 class="mb-3 text-sm font-semibold uppercase text-gray-400">
          Konto
        </h3>

        ${renderUserAccountBox()}

        <p class="mt-3 text-sm text-gray-400">
          Nie to konto?
          <span class="cursor-pointer text-[#A89DFF] font-bold">
            Użyj innego
          </span>
        </p>
      </div>

      <div>
        <h3 class="mb-4 text-sm font-semibold uppercase text-gray-400">
          Dostęp który ${oauthMockData.appName} posiada
        </h3>

        ${grantedScopes.map(scope => `
          <div class="flex items-center justify-between border-b border-gray-600 py-4">
            <p class="font-semibold">${scope.name}</p>

            <span>
              <svg width="19" height="14" viewBox="0 0 19 14" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M18.531 1.28104L6.53104 13.281C6.46139 13.3508 6.37867 13.4061 6.28762 13.4438C6.19657 13.4816 6.09898 13.501 6.00042 13.501C5.90186 13.501 5.80426 13.4816 5.71321 13.4438C5.62216 13.4061 5.53945 13.3508 5.46979 13.281L0.219792 8.03104C0.0790615 7.89031 0 7.69944 0 7.50042C0 7.30139 0.0790615 7.11052 0.219792 6.96979C0.360523 6.82906 0.551394 6.75 0.750417 6.75C0.94944 6.75 1.14031 6.82906 1.28104 6.96979L6.00042 11.6901L17.4698 0.219792C17.6105 0.0790612 17.8014 -1.48284e-09 18.0004 0C18.1994 1.48284e-09 18.3903 0.0790612 18.531 0.219792C18.6718 0.360522 18.7508 0.551394 18.7508 0.750417C18.7508 0.94944 18.6718 1.14031 18.531 1.28104Z" fill="#1D9E75"/>
              </svg>
            </span>
          </div>
        `).join("")}
      </div>

      <button
        id="oauth-continue-button"
        class="w-full rounded-xl border border-gray-400 py-3 text-white hover:bg-white/5"
      >
        Kontynuuj jako ${displayUserName}
      </button>

      <button
        id="oauth-cancel-button"
        class="w-full rounded-xl border border-gray-400 py-3 text-white hover:bg-white/5"
      >
        Anuluj
      </button>

      <p class="text-center text-sm text-gray-500">
        Zarządzaj dostępem aplikacji w
        <span class="text-[#A89DFF] font-bold">
          panelu Entria
        </span>.
      </p>

    </div>
  `;
}

function initOAuthPopupEvents() {
  const modal = document.getElementById("oauth-popup");

  document
    .getElementById("oauth-cancel-button")
    ?.addEventListener("click", closeOAuthPopup);

  document
    .getElementById("oauth-accept-button")
    ?.addEventListener("click", () => {
      const selectedOptionalScopes = Array.from(
        document.querySelectorAll("[data-optional-scope]")
      )
        .filter(input => input.checked)
        .map(input => input.dataset.optionalScope);

      const acceptedScopes = [
        ...getRequiredScopes().map(scope => scope.key),
        ...selectedOptionalScopes
      ];

      console.log(
        "Zaakceptowane scope:",
        acceptedScopes
      );

      closeOAuthPopup();
    });

  document
    .getElementById("oauth-continue-button")
    ?.addEventListener("click", () => {
      console.log(
        "Kontynuuj logowanie przez:",
        oauthMockData.appName
      );

      closeOAuthPopup();
    });

  modal?.addEventListener("click", event => {
    if (event.target === modal) {
      closeOAuthPopup();
    }
  });
}

function closeOAuthPopup() {
  const modal = document.getElementById("oauth-popup");

  if (!modal) {
    return;
  }

  modal.classList.add("hidden");
  modal.classList.remove("flex");
}