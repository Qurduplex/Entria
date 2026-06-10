import { api } from "./apiAuth.js";
import { showAlert } from "./alert.js";



async function loadComponents() {
    const navbarContainer = document.getElementById('navbar-container');
    const footerContainer = document.getElementById('footer-container');
    const modalContainer = document.getElementById('modal-container');
    const verifyModalContainer = document.getElementById('verify-modal-container');
    const verifiedModalContainer = document.getElementById('verified-modal-container');
    const forgotPasswordModalContainer = document.getElementById('forgot-password-modal-container');
    const resetPasswordModalContainer = document.getElementById('reset-password-modal-container');

    const requests = [];

    if (navbarContainer) {
        requests.push(
            fetch('../components/navbarMain.html')
                .then(res => res.text())
                .then(data => navbarContainer.innerHTML = data)
        );
    }

    if (footerContainer) {
        requests.push(
            fetch('../components/footerMain.html')
                .then(res => res.text())
                .then(data => footerContainer.innerHTML = data)
        );
    }

    if (modalContainer) {
        requests.push(
            fetch('../components/modalChooseType.html')
                .then(res => res.text())
                .then(data => modalContainer.innerHTML = data)
        );
    }

    if (verifyModalContainer) {
        requests.push(
            fetch('../components/modalVerifyEmail.html')
                .then(res => res.text())
                .then(data => verifyModalContainer.innerHTML = data)
        );
    }

    if (verifiedModalContainer) {
        requests.push(
            fetch('../components/modalAccountVerifield.html')
                .then(res => res.text())
                .then(data => verifiedModalContainer.innerHTML = data)
        );
    }

    if (forgotPasswordModalContainer) {
        requests.push(
            fetch('../components/modalForgotPassword.html')
                .then(res => res.text())
                .then(data => forgotPasswordModalContainer.innerHTML = data)
        );
    }

    if (resetPasswordModalContainer) {
        requests.push(
            fetch('../components/modalResetPassword.html')
                .then(res => res.text())
                .then(data => resetPasswordModalContainer.innerHTML = data)
        );
    }

    await Promise.all(requests);

    initRegisterForm();
    initLoginForm();
    initModal();
    initVerifyModal();
    initVerifiedModal();
    initForgotPasswordModal();
    initForgotPasswordTrigger();
    initResetPasswordModal();
}

document.addEventListener("DOMContentLoaded", () => {

    loadComponents();

    const jwtToken =
        localStorage.getItem("jwtToken");

    const refreshToken =
        localStorage.getItem("refreshToken");

    if (jwtToken && refreshToken) {
        startTokenRefresh();
    }
});

let currentRegisterType = sessionStorage.getItem("registerType") || "user";

function initRegisterForm() {
    const loginLink = document.getElementById("openLoginPage");

    if (loginLink) {
        loginLink.addEventListener("click", (e) => {
            e.preventDefault();
            window.location.href = "./LoginPage.html";
        });
    }

    const userForm = document.getElementById("userRegisterForm");
    const developerForm = document.getElementById("developerRegisterForm");

    if (userForm) {
        userForm.addEventListener("submit", handleRegister);
    }

    if (developerForm) {
        developerForm.addEventListener("submit", handleRegister);
    }
}

async function handleRegister(e) {
    e.preventDefault();

    const form = e.target;
    const email = form.email.value.trim();

    const registerType = form.id === "developerRegisterForm"
        ? "developer"
        : "user";

    const registerData = {
        email: email,
        password: form.password.value,
        firstName: form.first_name?.value.trim() || "",
        lastName: form.last_name?.value.trim() || "",
        userRole: registerType === "developer" ? "DEVELOPER" : "USER",
        termsAccepted: form.checkReg.checked,
    };

    try {
        await api.register(registerData);

        sessionStorage.setItem("profileDraft", JSON.stringify({ email }));

        showAlert("Konto zostało utworzone. Sprawdź kod na emailu.", "success");

        openVerifyModal(registerType, email);

        sessionStorage.removeItem("registerType");
    } catch (err) {
        console.error("Błąd rejestracji:", err);
        showAlert(err.data?.message || "Nie udało się utworzyć konta.", "error");
    }
}

function getRoleFromToken(token) {
    const payload = token.split(".")[1];
    const decodedPayload = JSON.parse(atob(payload));
    return decodedPayload.role;
}

function initLoginForm() {
    const form = document.getElementById("LoginForm");
    if (!form) return;
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const email = document.getElementById("LoginEmail").value.trim();
        const password = document.getElementById("LoginPassword").value.trim();
        if (!email || !password) {
            showAlert("Uzupełnij email i hasło.", "warning");
            return;
        }
        try{
            const response = await api.login({
                email: email,
                password: password
            });
            localStorage.setItem("jwtToken", response.jwtToken);
            localStorage.setItem("refreshToken", response.refreshToken);
            localStorage.setItem("expiresAt", response.expiresAt);

            startTokenRefresh();

            const role = getRoleFromToken(response.jwtToken);

            if (role === "DEVELOPER") {
                window.location.href = "./developer/DeveloperLayout.html";
            } else {
                window.location.href = "./user/UserLayout.html";
            }
        } catch (err) {
            console.error("Błąd logowania:", err);
            showAlert(err.data?.message || "Nie udało się zalogować.", "error");
        }
    });
}

let refreshInterval = null;

async function refreshAccessToken() {
    const refreshToken = localStorage.getItem("refreshToken");

    if (!refreshToken) {
        return;
    }

    try {
        const response = await api.refreshToken({
            refreshToken: refreshToken
        });

        console.log("REFRESH RESPONSE:", response);

        localStorage.setItem(
            "jwtToken",
            response.jwtToken
        );

        localStorage.setItem(
            "expiresAt",
            response.expiresAt
        );

        console.log(
            "Nowy token wygasa:",
            response.expiresAt
        );

    } catch (err) {
        console.error(
            "Nie udało się odświeżyć tokena:",
            err
        );

        localStorage.removeItem("jwtToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("expiresAt");

        showAlert(
            "Sesja wygasła. Zaloguj się ponownie.",
            "error"
        );

        window.location.href =
            "./LoginPage.html";
    }
}

function startTokenRefresh() {

    if (refreshInterval) {
        clearInterval(refreshInterval);
    }

    refreshInterval = setInterval(() => {
        refreshAccessToken();
    }, 5 * 60 * 1000 - 1);
}

function initModal() {
    const modal = document.getElementById('chooseTypeModal');
    const openBtns = document.querySelectorAll('.openChooseTypeModal');
    const closeBtn = document.getElementById('closeChooseTypeModal');
    const registerUserBtn = document.getElementById('openRegisterUser');
    const registerDeveloperBtn = document.getElementById('openRegisterDeveloper');
    const loginBtnNavbar = document.getElementById('openLoginPageNavbar');
    const loginBtnModal = document.getElementById('openLoginPageModal');
    if (!modal) return;
    openBtns.forEach((openBtn) => {
        openBtn.addEventListener('click', (e) => {
            e.preventDefault();
            modal.classList.remove('hidden');
            modal.classList.add('flex');
        });
    });
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
        registerUserBtn.addEventListener("click", () => {
            sessionStorage.setItem("registerType", "user");
            window.location.href = './RegisterUser.html';
        });
    }
    if (registerDeveloperBtn) {
        registerDeveloperBtn.addEventListener("click", () => {
            sessionStorage.setItem("registerType", "developer");
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
    const resendCodeLink = document.getElementById("resendCode");
    if (resendCodeLink) {
        resendCodeLink.addEventListener("click", async (e) => {
            e.preventDefault();
            const email = document.getElementById("verifyEmail").textContent.trim();
            if (!email) {
                showAlert("Brak adresu email.", "warning");
                return;
            }
            try {
                const response = await api.requestVerificationCode({
                    email: email
                });
                showAlert("Kod został wysłany ponownie.", "success");
            } catch (err) {
                console.error("Błąd ponownego wysyłania kodu:", err);
                showAlert(err.data?.message || "Nie udało się wysłać kodu ponownie.", "error");
            }
        });
    }

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

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const code = Array.from(codeInputs).map(input => input.value).join("");
        const email = document.getElementById("verifyEmail").textContent.trim();
        if (code.length !== 6) {
            showAlert("Wpisz pełny 6-znakowy kod.", "warning");
            return;
        }
        try {
            const response = await api.verifyCode({
                email: email,
                verificationCode: code
            });
            showAlert("Email został potwierdzony.", "success");
            openVerifiedModal(currentVerifyType);
            closeVerifyModal();
        } catch (err) {
            console.error("Błąd weryfikacji:", err);
            showAlert(err.data?.message || "Nieprawidłowy kod.", "error");
        }
    });
}

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
    const successButton = document.getElementById("successButton");
    if (closeBtn) {
        closeBtn.addEventListener("click", closeVerifiedModal);
    }
    
    if (successButton) {
        successButton.addEventListener("click", () => {
            window.location.href = './LoginPage.html';

        });
    }

    if (modal) {
        modal.addEventListener("click", (e) => {
            if (e.target.id === "verifiedModal") {
                closeVerifiedModal();
            }
        });
    }
}

let resetPasswordEmail = "";

function openForgotPasswordModal() {
    const modal = document.getElementById("forgotPasswordModal");

    if (!modal) {
        console.error("Nie znaleziono forgotPasswordModal");
        return;
    }
    modal.classList.remove("hidden");
    modal.classList.add("flex");
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

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const emailInput = document.getElementById("forgotPasswordEmail");
        const email = emailInput ? emailInput.value.trim() : "";
        if (!email) {
            showAlert("Podaj adres email.", "warning");
            return;
        }
        try {
            await api.requestResetPasswordCode({
                email: email
            });
            resetPasswordEmail = email;
            showAlert("Kod resetu został wysłany na email.", "success");
            openResetPasswordModal();
            closeForgotPasswordModal();
        } catch (err) {
            console.error("Błąd requestu resetu:", err);
            showAlert(err.data?.message || "Nie udało się wysłać kodu.", "error");
        }
    });
}

function openResetPasswordModal() {
    const modal = document.getElementById("resetPasswordModal");
    if (!modal) {
        console.error("Nie znaleziono resetPasswordModal");
        return;
    }
    modal.classList.remove("hidden");
    modal.classList.add("flex");
    const codeInput = document.getElementById("resetPasswordCode");
    const newPasswordInput = document.getElementById("newPassword");
    const confirmPasswordInput = document.getElementById("confirmNewPassword");
    if (codeInput) codeInput.value = "";
    if (newPasswordInput) newPasswordInput.value = "";
    if (confirmPasswordInput) confirmPasswordInput.value = "";
    if (codeInput) {
        codeInput.focus();
    }
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

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const code = document.getElementById("resetPasswordCode")?.value.trim() || "";
        const newPassword = document.getElementById("newPassword")?.value.trim() || "";
        const confirmPassword = document.getElementById("confirmNewPassword")?.value.trim() || "";

        if (!resetPasswordEmail) {
            showAlert("Brak adresu email. Rozpocznij reset hasła ponownie.", "warning");
            closeResetPasswordModal();
            openForgotPasswordModal();
            return;
        }

        if (!code) {
            showAlert("Podaj kod resetu hasła.", "warning");
            return;
        }

        if (newPassword.length < 8) {
            showAlert("Hasło musi mieć co najmniej 8 znaków.", "warning");
            return;
        }

        if (newPassword !== confirmPassword) {
            showAlert("Hasła nie są takie same.", "warning");
            return;
        }

        try {
            await api.resetPassword({
                email: resetPasswordEmail,
                resetPasswordCode: code,
                password: newPassword
            });
            showAlert("Hasło zostało zmienione. Możesz się zalogować.", "success");
            resetPasswordEmail = "";
            closeResetPasswordModal();

        } catch (err) {
            console.error("Błąd resetu hasła:", err);
            showAlert(err.data?.message || "Nie udało się zmienić hasła.", "error");
        }
    });
}