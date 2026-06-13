import { api } from "../api/apiDeveloper.js";

function showPasswordError(message) {
    const errorBox = document.getElementById("password-error");

    if (!errorBox) return;

    errorBox.textContent = message;
    errorBox.classList.remove("hidden");
}

function clearPasswordError() {
    const errorBox = document.getElementById("password-error");

    if (!errorBox) return;

    errorBox.textContent = "";
    errorBox.classList.add("hidden");
}

export function initProfilePassword() {

    const form = {
        currentPassword: document.getElementById("current-password"),
        newPassword: document.getElementById("new-password"),
        repeatPassword: document.getElementById("repeat-password"),
        submitButton: document.getElementById("change-password-button"),
    };

    if (
        !form.currentPassword ||
        !form.newPassword ||
        !form.repeatPassword ||
        !form.submitButton
    ) {
        return;
    }

    form.submitButton.addEventListener("click", async () => {

        clearPasswordError();

        const payload = {
            currentPassword: form.currentPassword.value,
            newPassword: form.newPassword.value,
            repeatPassword: form.repeatPassword.value,
        };

        if (payload.newPassword !== payload.repeatPassword) {
            showPasswordError("Nowe hasła nie są takie same.");
            return;
        }

        try {
            await api.changePassword(payload);

            showPasswordError("Hasło zostało zmienione.");
            document.getElementById("password-error")
                .classList.replace("text-red-300", "text-green-300");

            form.currentPassword.value = "";
            form.newPassword.value = "";
            form.repeatPassword.value = "";

        } catch (error) {

            if (error.status === 401) {

                if (
                    error.data?.message === "Invalid current password"
                ) {
                    showPasswordError("Aktualne hasło jest nieprawidłowe.");
                    return;
                }

                if (
                    error.data?.message === "New password cannot be the same as the current password"
                ) {
                    showPasswordError("Nowe hasło nie może być takie samo jak obecne.");
                    return;
                }

                if (
                    error.data?.message === "Password must be between 8 and 32 characters long"
                ) {
                    showPasswordError("Nowe hasło musi mieć od 8 do 32 znaków.");
                    return;
                }
            }

            showPasswordError("Nie udało się zmienić hasła.");
        }
    });
}