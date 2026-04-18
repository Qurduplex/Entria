async function loadComponents() {
    const navbarContainer = document.getElementById('navbar-container');
    const footerContainer = document.getElementById('footer-container');
    const modalContainer = document.getElementById('modal-container');
    const verifyModalContainer = document.getElementById('verify-modal-container');
    const verifiedModalContainer = document.getElementById('verified-modal-container');
    const forgotPasswordModalContainer = document.getElementById('forgot-password-modal-container');
    const resetPasswordModalContainer = document.getElementById('reset-password-modal-container');

    const requests = [];

    //Komponent Nawigacji
    if (navbarContainer) {
        requests.push(
            fetch('../components/navbarMain.html')
                .then(res => res.text())
                .then(data => navbarContainer.innerHTML = data)
        );
    }

    //Komponent Stopki
    if (footerContainer) {
        requests.push(
            fetch('../components/footerMain.html')
                .then(res => res.text())
                .then(data => footerContainer.innerHTML = data)
        );
    }

    //Komponent Modala Wyboru
    if (modalContainer) {
        requests.push(
            fetch('../components/modalChooseType.html')
                .then(res => res.text())
                .then(data => modalContainer.innerHTML = data)
        );
    }

    //Komponent Weryfikacji Kodu
    if (verifyModalContainer) {
        requests.push(
            fetch('../components/modalVerifyEmail.html')
                .then(res => res.text())
                .then(data => verifyModalContainer.innerHTML = data)
        );
    }

    //Komponent Pozytywnej Weryfikacji Kodu
    if (verifiedModalContainer) {
        requests.push(
            fetch('../components/modalAccountVerifield.html')
                .then(res => res.text())
                .then(data => verifiedModalContainer.innerHTML = data)
        );
    }

    //Komponent Zapomnienie Hasła
    if (forgotPasswordModalContainer) {
        requests.push(
            fetch('../components/modalForgotPassword.html')
                .then(res => res.text())
                .then(data => forgotPasswordModalContainer.innerHTML = data)
        );
    }

    //Komponent Resetu Hasla
    if (resetPasswordModalContainer) {
        requests.push(
            fetch('../components/modalResetPassword.html')
                .then(res => res.text())
                .then(data => resetPasswordModalContainer.innerHTML = data)
        );
    }

    await Promise.all(requests);
    initModal();
    initVerifyModal();
    initVerifiedModal();
    initForgotPasswordModal();
    initForgotPasswordTrigger();
    initResetPasswordModal();
}

document.addEventListener('DOMContentLoaded', () => {
    loadComponents();
});

//Modal Wyboru
function initModal() {
    const modal = document.getElementById('chooseTypeModal');
    const openBtn = document.getElementById('openChooseTypeModal');
    const closeBtn = document.getElementById('closeChooseTypeModal');

    const registerUserBtn = document.getElementById('openRegisterUser');
    const registerDeveloperBtn = document.getElementById('openRegisterDeveloper');
    const loginBtnNavbar = document.getElementById('openLoginPageNavbar');
    const loginBtnModal = document.getElementById('openLoginPageModal');

    if (!modal) return;

    if (openBtn) {
        openBtn.addEventListener('click', () => {
            modal.classList.remove('hidden');
            modal.classList.add('flex');
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            modal.classList.add('hidden');
            modal.classList.remove('flex');
        });
    }

    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.add('hidden');
            modal.classList.remove('flex');
        }
    });

    if (registerUserBtn) {
        registerUserBtn.addEventListener('click', () => {
            window.location.href = './RegisterUser.html';
        });
    }

    if (registerDeveloperBtn) {
        registerDeveloperBtn.addEventListener('click', () => {
            window.location.href = './RegisterDeveloper.html';
        });
    }

    if (loginBtnNavbar) {
        loginBtnNavbar.addEventListener('click', () => {
            window.location.href = './LoginPage.html';
        });
    }

    if (loginBtnModal) {
        loginBtnModal.addEventListener('click', () => {
            window.location.href = './LoginPage.html';
        });
    }

    
}

//Modal Weryfikacji Kodu

let currentVerifyType = null;
const verifyData = {
    user: {
        title: "Potwierdź email",
        subtitle: "Wysłaliśmy 6-cyfrowy kod na",
    },
    developer: {
        title: "Potwierdź email developera",
        subtitle: "Wysłaliśmy 6-cyfrowy kod na",
    }
};

function openVerifyModal(type = "", email = "") {
    const modal = document.getElementById("verifyModal");
    const current = verifyData[type];

    if (!modal || !current) {
        console.error("Verify modal nie istnieje albo typ jest niepoprawny:", type);
        return;
    }

    currentVerifyType = type;

    document.getElementById("verifyTitle").textContent = current.title;
    document.getElementById("verifySubtitle").textContent = current.subtitle;
    document.getElementById("verifyEmail").textContent = email;

    const codeInputs = modal.querySelectorAll(".verify-code-input");
    codeInputs.forEach(input => input.value = "");

    modal.classList.remove("hidden");
    modal.classList.add("flex");

    if (codeInputs.length > 0) {
        codeInputs[0].focus();
    }
}

function closeVerifyModal() {
    const modal = document.getElementById("verifyModal");
    if (!modal) return;

    modal.classList.add("hidden");
    modal.classList.remove("flex");
}

function initVerifyModal() {
    const modal = document.getElementById("verifyModal");
    const form = document.getElementById("verifyForm");

    if (!modal || !form) return;

    const codeInputs = modal.querySelectorAll(".verify-code-input");

    codeInputs.forEach((input, index) => {
        input.addEventListener("input", (e) => {
            let value = e.target.value.toUpperCase().replace(/[^A-Z0-9]/g, "");
            e.target.value = value;

            if (value && index < codeInputs.length - 1) {
                codeInputs[index + 1].focus();
            }
        });

        input.addEventListener("keydown", (e) => {
            if (e.key === "Backspace" && !input.value && index > 0) {
                codeInputs[index - 1].focus();
            }
        });

        input.addEventListener("paste", (e) => {
            e.preventDefault();

            const pasted = (e.clipboardData || window.clipboardData)
                .getData("text")
                .toUpperCase()
                .replace(/[^A-Z0-9]/g, "")
                .slice(0, codeInputs.length);

            pasted.split("").forEach((char, i) => {
                if (codeInputs[i]) {
                    codeInputs[i].value = char;
                }
            });

            const nextIndex = Math.min(pasted.length, codeInputs.length - 1);
            if (codeInputs[nextIndex]) {
                codeInputs[nextIndex].focus();
            }
        });
    });

    modal.addEventListener("click", (e) => {
        if (e.target === modal) {
            closeVerifyModal();
        }
    });

    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const code = Array.from(codeInputs).map(input => input.value).join("");
        console.log("Kod weryfikacyjny:", code);

        openVerifiedModal(currentVerifyType);

        closeVerifyModal();
    });
}

//Modal Pozytywnej Weryfikacji Kodu
const verifiedData = {
    user: {
        title: "Konto aktywne",
        subtitle: "Rejestracja zakończona. Możesz teraz uzupełnić dodatkowe dane w zakładce Profil.",
        button: "Przejdź do panelu →",
        link: "Uzupełnij profil teraz"
    },
    developer: {
        title: "Konto developerskie zarejestrowane",
        subtitle: "Konto developerskie zostało aktywowane. Możesz teraz skonfigurować integracje i zaprosić użytkowników.",
        button: "Przejdź do panelu →",
        link: "Uzupełnij profil"
    }
};

function openVerifiedModal(type = "user") {
    const modal = document.getElementById("verifiedModal");
    const current = verifiedData[type];

    if (!modal || !current) return;

    document.getElementById("successTitle").textContent = current.title;
    document.getElementById("successSubtitle").textContent = current.subtitle;
    document.getElementById("successButton").textContent = current.button;
    document.getElementById("successLink").textContent = current.link;

    modal.classList.remove("hidden");
    modal.classList.add("flex");
}

function closeVerifiedModal() {
    const modal = document.getElementById("verifiedModal");
    if (!modal) return;

    modal.classList.add("hidden");
    modal.classList.remove("flex");
}

function initVerifiedModal() {
    const closeBtn = document.getElementById("closeVerifiedModal");
    const modal = document.getElementById("verifiedModal");

    if (closeBtn) {
        closeBtn.addEventListener("click", closeVerifiedModal);
    }

    if (modal) {
        modal.addEventListener("click", (e) => {
            if (e.target.id === "verifiedModal") {
                closeVerifiedModal();
            }
        });
    }
}

//Modal Zapomnienia Hasla
function openForgotPasswordModal() {
    console.log("klik działa");

    const modal = document.getElementById("forgotPasswordModal");
    console.log("modal znaleziony:", modal);

    if (!modal) {
        console.error("Nie znaleziono forgotPasswordModal");
        return;
    }

    console.log("klasy przed:", modal.className);

    modal.classList.remove("hidden");
    modal.classList.add("flex");

    console.log("klasy po:", modal.className);

    const emailInput = document.getElementById("forgotPasswordEmail");
    if (emailInput) {
        emailInput.value = "";
        emailInput.focus();
    }
}

function initForgotPasswordTrigger() {
    const forgotBtn = document.getElementById("openForgotPasswordModal");

    if (!forgotBtn) return;

    forgotBtn.addEventListener("click", (e) => {
        e.preventDefault();
        openForgotPasswordModal();
    });
}

function closeForgotPasswordModal() {
    const modal = document.getElementById("forgotPasswordModal");
    if (!modal) return;

    modal.classList.add("hidden");
    modal.classList.remove("flex");
}

function initForgotPasswordModal() {
    const modal = document.getElementById("forgotPasswordModal");
    const form = document.getElementById("forgotPasswordForm");
    const backBtn = document.getElementById("backToLoginFromForgot");

    if (!modal || !form) return;

    modal.addEventListener("click", (e) => {
        if (e.target === modal) {
            closeForgotPasswordModal();
        }
    });

    if (backBtn) {
        backBtn.addEventListener("click", () => {
            closeForgotPasswordModal();
        });
    }

    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const emailInput = document.getElementById("forgotPasswordEmail");
        const email = emailInput ? emailInput.value.trim() : "";

        console.log("Email do resetu hasła:", email);

        openResetPasswordModal();
        closeForgotPasswordModal();
    });
}

//Modal Resetu Hasla
function openResetPasswordModal() {
    const modal = document.getElementById("resetPasswordModal");
    if (!modal) {
        console.error("Nie znaleziono resetPasswordModal");
        return;
    }

    modal.classList.remove("hidden");
    modal.classList.add("flex");

    const newPasswordInput = document.getElementById("newPassword");
    const confirmPasswordInput = document.getElementById("confirmNewPassword");

    if (newPasswordInput) newPasswordInput.value = "";
    if (confirmPasswordInput) confirmPasswordInput.value = "";

    if (newPasswordInput) newPasswordInput.focus();
}

function closeResetPasswordModal() {
    const modal = document.getElementById("resetPasswordModal");
    if (!modal) return;

    modal.classList.add("hidden");
    modal.classList.remove("flex");
}

function togglePasswordVisibility(inputId) {
    const input = document.getElementById(inputId);
    if (!input) return;

    input.type = input.type === "password" ? "text" : "password";
}

function initResetPasswordModal() {
    const modal = document.getElementById("resetPasswordModal");
    const form = document.getElementById("resetPasswordForm");
    const toggleNew = document.getElementById("toggleNewPassword");
    const toggleConfirm = document.getElementById("toggleConfirmPassword");

    if (!modal || !form) return;

    modal.addEventListener("click", (e) => {
        if (e.target === modal) {
            closeResetPasswordModal();
        }
    });

    if (toggleNew) {
        toggleNew.addEventListener("click", () => {
            togglePasswordVisibility("newPassword");
        });
    }

    if (toggleConfirm) {
        toggleConfirm.addEventListener("click", () => {
            togglePasswordVisibility("confirmNewPassword");
        });
    }

    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const newPassword = document.getElementById("newPassword")?.value.trim() || "";
        const confirmPassword = document.getElementById("confirmNewPassword")?.value.trim() || "";

        console.log("Nowe hasło:", newPassword);
        console.log("Potwierdzenie hasła:", confirmPassword);

        if (newPassword.length < 8) {
            alert("Hasło musi mieć co najmniej 8 znaków.");
            return;
        }

        if (newPassword !== confirmPassword) {
            alert("Hasła nie są takie same.");
            return;
        }

        ///

        closeResetPasswordModal();
    });
}