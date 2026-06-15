import { showAlert } from "../alert.js";
import { userApi } from "../user/api/apiUser.js";

export async function initUserProfile() {
  const data = {
    firstName: "",
    lastName: "",
    email: localStorage.getItem("userEmail") || "",
    phone: "",
    birthdate: "",
    gender: "",
    pesel: "",
    avatarUrl: null,
  };

  // ─── ŁADOWANIE Z API ───────────────────────────────────────────────────────
  try {
    const profile = await userApi.getMyProfile();
    data.firstName = profile.firstName || "";
    data.lastName = profile.lastName || "";
    data.phone = profile.phoneNumber || "";
    data.birthdate = profile.birthDate || "";
    data.pesel = profile.pesel || "";
    data.gender = profile.sex || ""; // "M" / "F"
    data.avatarUrl = profile.profilePictureUrl || null;
  } catch (err) {
    console.error("Nie udało się pobrać profilu:", err);
    showAlert("Nie udało się pobrać danych profilu.", "error");
  }

  try {
    const emailData = await userApi.getMyEmail();
    data.email = emailData.email || data.email;
  } catch (err) {
    console.error("Nie udało się pobrać adresu email: ", err);
  }

  // ─── AVATAR ──────────────────────────────────────────────────────────────
  const avatar = document.getElementById("profile-avatar");
  const deletePicBtn = document.getElementById("profile-delete-btn");
  let pendingAvatarFile = null; // plik do wysłania

  function renderAvatar() {
    if (!avatar) return;
    if (data.avatarUrl) {
      avatar.innerHTML = `<img src="${data.avatarUrl}" alt="Avatar" class="w-full h-full object-cover" />`;
    } else {
      const f =
        document.getElementById("input-firstname")?.value || data.firstName;
      const l =
        document.getElementById("input-lastname")?.value || data.lastName;
      avatar.textContent = `${f[0] || ""}${l[0] || ""}`.toUpperCase();
    }
    if (deletePicBtn) {
      deletePicBtn.disabled = !data.avatarUrl;
      deletePicBtn.classList.toggle("opacity-40", !data.avatarUrl);
      deletePicBtn.classList.toggle("cursor-not-allowed", !data.avatarUrl);
      deletePicBtn.classList.toggle("pointer-events-none", !data.avatarUrl);
    }
  }

  renderAvatar();

  const uploadBtn = document.getElementById("profile-upload-btn");
  if (uploadBtn) {
    const fileInput = document.createElement("input");
    fileInput.type = "file";
    fileInput.accept = "image/*";
    fileInput.style.display = "none";
    document.body.appendChild(fileInput);

    uploadBtn.addEventListener("click", () => fileInput.click());
    fileInput.addEventListener("change", () => {
      const file = fileInput.files[0];
      if (!file) return;
      pendingAvatarFile = file; // zapamiętaj do wysłki
      const reader = new FileReader();
      reader.onload = (e) => {
        data.avatarUrl = e.target.result;
        renderAvatar();
        hasChanges = true;
        if (indicator) indicator.classList.remove("hidden");
        updateSaveBtn();
      };
      reader.readAsDataURL(file);
      fileInput.value = "";
    });
  }

  if (deletePicBtn) {
    deletePicBtn.addEventListener("click", () => {
      data.avatarUrl = null;
      pendingAvatarFile = null;
      renderAvatar();
    });
  }

  // ─── NAGŁÓWEK ────────────────────────────────────────────────────────────
  const nameEl = document.getElementById("profile-name");
  const emailEl = document.getElementById("profile-email");
  if (nameEl) nameEl.textContent = `${data.firstName} ${data.lastName}`.trim();
  if (emailEl)
    emailEl.textContent = data.email ? `Zalogowany przez ${data.email}` : "";

  // ─── INPUTY ──────────────────────────────────────────────────────────────
  const fields = {
    "input-firstname": data.firstName,
    "input-lastname": data.lastName,
    "input-phone": data.phone,
    "input-birthdate": data.birthdate,
    "input-pesel": data.pesel,
  };

  Object.entries(fields).forEach(([id, value]) => {
    const el = document.getElementById(id);
    if (el) el.value = value;
  });

  const genderSelect = document.getElementById("input-gender");
  if (genderSelect) genderSelect.value = data.gender; // "M"/"F"/""


  const emailInput = document.getElementById("input-email");
  if (emailInput) {
    emailInput.value = data.email;
    emailInput.readOnly = true;
    emailInput.disabled = true; // wypada z payloadu
    emailInput.classList.add("opacity-60", "cursor-not-allowed");
  }

  // ─── WALIDACJA ───────────────────────────────────────────────────────────
  const validators = {
    "input-firstname": {
      required: true,
      regex: /^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż'\- ]{2,40}$/,
      message: "Imię nie może zawierać cyfr ani znaków specjalnych",
    },
    "input-lastname": {
      required: true,
      regex: /^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż'\- ]{2,40}$/,
      message: "Nazwisko nie może zawierać cyfr ani znaków specjalnych",
    },
    "input-phone": {
      regex: /^\d{9}$/,
      message: "Numer telefonu musi mieć dokładnie 9 cyfr",
    },
    "input-birthdate": {
      validate: validateBirthdate,
    },
    "input-pesel": {
      validate: (v) => {
        if (/^0+$/.test(v)) return { valid: true };
        return validatePesel(v);
      },
    },
  };

  // Input filters
  applyInputFilter("input-firstname", (v) =>
    v.replace(/[^A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż'\- ]/g, ""),
  );
  applyInputFilter("input-lastname", (v) =>
    v.replace(/[^A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż'\- ]/g, ""),
  );
  applyInputFilter("input-phone", (v) => v.replace(/\D/g, "").slice(0, 9));
  applyInputFilter("input-pesel", (v) => v.replace(/\D/g, "").slice(0, 11));

  Object.keys(validators).forEach((id) => {
    const el = document.getElementById(id);
    if (!el) return;
    el.addEventListener("blur", () => validateField(id));
    el.addEventListener("input", () => {
      const hint = getHint(id);
      if (hint && !hint.classList.contains("hidden")) validateField(id);
      updateSaveBtn();
    });
    if (el.type === "date") {
      el.addEventListener("change", () => {
        validateField(id);
        updateSaveBtn();
      });
    }
  });

  // ─── PRZYCISK ZAPISZ ─────────────────────────────────────────────────────
  const indicator = document.getElementById("unsaved-indicator");
  const saveBtn = document.getElementById("btn-save-profile");
  let hasChanges = false;

  function updateSaveBtn() {
    if (!saveBtn) return;
    saveBtn.disabled = !hasChanges;
    saveBtn.classList.toggle("opacity-40", !hasChanges);
    saveBtn.classList.toggle("cursor-not-allowed", !hasChanges);
    saveBtn.classList.toggle("pointer-events-none", !hasChanges);
  }

  document.querySelectorAll(".input-field").forEach((input) => {
    if (input.disabled) return; // pomiń email
    input.addEventListener("input", () => {
      hasChanges = true;
      if (indicator) indicator.classList.remove("hidden");
      updateSaveBtn();
    });
    input.addEventListener("change", () => {
      hasChanges = true;
      if (indicator) indicator.classList.remove("hidden");
      updateSaveBtn();
    });
  });

  updateSaveBtn();

  // ─── ZAPISZ ──────────────────────────────────────────────────────────────
  if (saveBtn) {
    saveBtn.addEventListener("click", async () => {
      const results = Object.keys(validators).map(validateField);
      if (!results.every(Boolean)) return;

      const phone = document.getElementById("input-phone")?.value.trim() || "";
      const peselVal =
        document.getElementById("input-pesel")?.value.trim() || "";
      const genderVal = document.getElementById("input-gender")?.value || ""; // "M"/"F"/""

      const payload = {
        firstName: document.getElementById("input-firstname")?.value.trim(),
        lastName: document.getElementById("input-lastname")?.value.trim(),
        phoneNumber: phone || undefined,
        birthDate:
          document.getElementById("input-birthdate")?.value || undefined,
        pesel: peselVal && !/^0+$/.test(peselVal) ? peselVal : undefined,
        sex: genderVal || undefined,
        profilePicture: pendingAvatarFile || undefined,
      };

      saveBtn.disabled = true;
      try {
        const updated = await userApi.updateMyProfile(payload);

        // zsynchronizuj lokalny stan
        if (updated) {
          data.avatarUrl = updated.profilePictureUrl || data.avatarUrl;
          if (nameEl)
            nameEl.textContent =
              `${updated.firstName} ${updated.lastName}`.trim();
        }
        pendingAvatarFile = null;
        hasChanges = false;
        if (indicator) indicator.classList.add("hidden");
        updateSaveBtn();
        showAlert("Zmiany zostały zapisane.", "success");
      } catch (e) {
        const msg =
          e?.data?.error ||
          (e?.data && Object.values(e.data)[0]) ||
          "Nie udało się zapisać zmian.";
        showAlert(msg, "error");
        updateSaveBtn();
      }
    });
  }

  // ─── ZMIEŃ HASŁO (bez zmian — wciąż mock) ────────────────────────────────
  const passwordBtn = document.getElementById("btn-change-password");
  const inputCurrent = document.getElementById("input-current");
  const inputNew = document.getElementById("input-new");
  const inputRepeat = document.getElementById("input-repeat");
  const passwordError = document.getElementById("password-error");

  function showPasswordError(message, success = false) {
    if (!passwordError) return;
    passwordError.textContent = message;
    passwordError.classList.remove("hidden", "text-red-500", "text-green-600");
    passwordError.classList.add(success ? "text-green-600" : "text-red-500");
  }
  function clearPasswordError() {
    if (!passwordError) return;

    passwordError.textContent = "";

    passwordError.classList.add("hidden");
  }

  function updatePasswordBtn() {
    if (!passwordBtn) return;
    const filled = inputCurrent?.value && inputNew?.value && inputRepeat?.value;
    passwordBtn.disabled = !filled;
    passwordBtn.classList.toggle("opacity-40", !filled);
    passwordBtn.classList.toggle("cursor-not-allowed", !filled);
    passwordBtn.classList.toggle("pointer-events-none", !filled);
  }

  [inputCurrent, inputNew, inputRepeat].forEach((el) => {
    el?.addEventListener("input", updatePasswordBtn);
  });
  updatePasswordBtn();

  if (passwordBtn) {
    passwordBtn.addEventListener("click", async () => {
      clearPasswordError();

      const payload = {
        currentPassword: inputCurrent.value,

        newPassword: inputNew.value,

        repeatPassword: inputRepeat.value,
      };

      if (payload.newPassword !== payload.repeatPassword) {
        showPasswordError("Nowe hasła nie są takie same.");

        return;
      }

      passwordBtn.disabled = true;

      try {
        await userApi.changePassword(payload);

        showPasswordError("Hasło zostało zmienione.", true);
        showAlert("Hasło zostało zmienione.", "success");

        inputCurrent.value = "";

        inputNew.value = "";

        inputRepeat.value = "";

        updatePasswordBtn();
      } catch (error) {
        if (error.status === 401) {
          if (error.data?.message === "Invalid current password") {
            showPasswordError("Aktualne hasło jest nieprawidłowe.");

            updatePasswordBtn();

            return;
          }

          if (
            error.data?.message ===
            "New password cannot be the same as the current password"
          ) {
            showPasswordError("Nowe hasło nie może być takie samo jak obecne.");

            updatePasswordBtn();

            return;
          }

          if (
            error.data?.message ===
            "Password must be between 8 and 32 characters long"
          ) {
            showPasswordError("Nowe hasło musi mieć od 8 do 32 znaków.");

            updatePasswordBtn();

            return;
          }
        }

        showPasswordError("Nie udało się zmienić hasła.");
        

        updatePasswordBtn();
      }
    });
  }

  // ─── HELPERY WALIDACJI ───────────────────────────────────────────────────
  function getHint(id) {
    return document.querySelector(`.input-hint[data-for="${id}"]`);
  }

  function setError(id, message) {
    const el = document.getElementById(id);
    const hint = getHint(id);
    if (el) el.classList.add("is-invalid");
    if (hint) {
      hint.textContent = message;
      hint.classList.remove("hidden");
      hint.style.color = "#EF4444";
      hint.style.fontSize = "12px";
    }
  }

  function clearError(id) {
    const el = document.getElementById(id);
    const hint = getHint(id);
    if (el) el.classList.remove("is-invalid");
    if (hint) hint.classList.add("hidden");
  }

  function validateField(id) {
    const el = document.getElementById(id);
    if (!el) return true;
    const value = el.value.trim();
    const rule = validators[id];
    if (!rule) return true;

    if (value === "") {
      if (rule.required) {
        setError(id, "To pole jest wymagane");
        return false;
      }
      clearError(id);
      return true;
    }

    let valid = true;
    let message = "";

    if (rule.validate) {
      const result = rule.validate(value);
      valid = result.valid;
      message = result.message || "";
    } else if (rule.regex) {
      valid = rule.regex.test(value);
      message = rule.message;
    }

    if (valid) clearError(id);
    else setError(id, message);
    return valid;
  }

  // ─── DELETE ACCOUNT POPUP (bez zmian) ────────────────────────────────────
  const deleteAccountBtn = document.getElementById("btn-delete-account");
  if (deleteAccountBtn) {
    deleteAccountBtn.addEventListener("click", () => {
      const overlay = document.createElement("div");
      overlay.className =
        "fixed inset-0 z-[9999] bg-black/40 backdrop-blur-sm flex items-center justify-center";
      overlay.innerHTML = `
        <div class="bg-white rounded-2xl p-7 max-w-[420px] w-[calc(100%-32px)] shadow-[0_8px_40px_rgba(0,0,0,0.18)]">
          <div class="flex items-center gap-3 mb-4">
            <div class="w-9 h-9 rounded-full bg-red-100 flex items-center justify-center shrink-0">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6"/>
              </svg>
            </div>
            <p class="text-[15px] font-semibold text-[#161619]">Usuń konto</p>
          </div>
          <p class="text-[13.5px] text-[#444] leading-relaxed mb-2">
            Czy na pewno chcesz trwale usunąć konto? Tej operacji <span class="font-semibold text-[#161619]">nie można cofnąć</span>.
          </p>
          <ul class="text-[13px] text-[#666] leading-[1.75] mb-6 pl-5 list-disc">
            <li>Stracisz dostęp do wszystkich aplikacji połączonych przez Entria</li>
            <li>Nie będziesz mógł logować się przez Entria do żadnego serwisu</li>
            <li>Wszystkie Twoje dane zostaną trwale usunięte</li>
          </ul>
          <div class="flex justify-end gap-2.5">
            <button id="delete-cancel" class="btn-secondary">Anuluj</button>
            <button id="delete-confirm" class="px-[18px] py-[9px] rounded-[10px] bg-red-500 text-white text-[13px] font-medium hover:bg-red-600 transition-colors cursor-pointer">Tak, usuń konto</button>
          </div>
        </div>`;
      overlay.addEventListener("click", (e) => {
        if (e.target === overlay) overlay.remove();
      });
      overlay
        .querySelector("div")
        .addEventListener("click", (e) => e.stopPropagation());
      overlay
        .querySelector("#delete-cancel")
        .addEventListener("click", () => overlay.remove());
      overlay.querySelector("#delete-confirm").addEventListener("click", () => {
        // TODO: API call
        overlay.remove();
      });
      document.body.appendChild(overlay);
    });
  }
}

// ─── INPUT FILTER ─────────────────────────────────────────────────────────────
function applyInputFilter(id, filterFn) {
  const el = document.getElementById(id);
  if (!el) return;
  el.addEventListener("input", () => {
    const pos = el.selectionStart;
    const before = el.value;
    const after = filterFn(before);
    if (before !== after) {
      el.value = after;
      const diff = before.length - after.length;
      el.setSelectionRange(pos - diff, pos - diff);
    }
  });
}

// ─── BIRTHDATE VALIDATOR ──────────────────────────────────────────────────────
function validateBirthdate(value) {
  const [year, month, day] = value.split("-").map(Number);
  if (!year || !month || !day)
    return { valid: false, message: "Niepoprawna data urodzenia" };

  const date = new Date(year, month - 1, day);
  if (
    date.getFullYear() !== year ||
    date.getMonth() !== month - 1 ||
    date.getDate() !== day
  )
    return { valid: false, message: "Niepoprawna data urodzenia" };

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  if (date >= today)
    return {
      valid: false,
      message: "Data urodzenia nie może być z przyszłości",
    };

  let age = today.getFullYear() - date.getFullYear();
  const m = today.getMonth() - date.getMonth();
  if (m < 0 || (m === 0 && today.getDate() < date.getDate())) age--;
  if (age < 13)
    return { valid: false, message: "Musisz mieć co najmniej 13 lat" };

  return { valid: true };
}

// ─── PESEL VALIDATOR ──────────────────────────────────────────────────────────
function validatePesel(pesel) {
  if (!/^\d{11}$/.test(pesel))
    return { valid: false, message: "PESEL musi mieć dokładnie 11 cyfr" };

  const yy = parseInt(pesel.slice(0, 2), 10);
  let mm = parseInt(pesel.slice(2, 4), 10);
  const dd = parseInt(pesel.slice(4, 6), 10);

  let year;
  if (mm >= 1 && mm <= 12) {
    year = 1900 + yy;
  } else if (mm >= 21 && mm <= 32) {
    year = 2000 + yy;
    mm -= 20;
  } else if (mm >= 81 && mm <= 92) {
    year = 1800 + yy;
    mm -= 80;
  } else if (mm >= 41 && mm <= 52) {
    year = 2100 + yy;
    mm -= 40;
  } else if (mm >= 61 && mm <= 72) {
    year = 2200 + yy;
    mm -= 60;
  } else
    return { valid: false, message: "Nieprawidłowy miesiąc w numerze PESEL" };

  const date = new Date(year, mm - 1, dd);
  if (
    date.getFullYear() !== year ||
    date.getMonth() !== mm - 1 ||
    date.getDate() !== dd
  )
    return {
      valid: false,
      message: "Numer PESEL zawiera niepoprawną datę urodzenia",
    };

  const weights = [1, 3, 7, 9, 1, 3, 7, 9, 1, 3];
  let sum = 0;
  for (let i = 0; i < 10; i++) sum += parseInt(pesel[i], 10) * weights[i];
  const checksum = (10 - (sum % 10)) % 10;
  if (checksum !== parseInt(pesel[10], 10))
    return { valid: false, message: "Niepoprawna suma kontrolna numeru PESEL" };

  return { valid: true };
}
