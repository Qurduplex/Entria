import { api } from "../api/apiDeveloper.js";

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
        const payload = {
            currentPassword: form.currentPassword.value,
            newPassword: form.newPassword.value,
            repeatPassword: form.repeatPassword.value,
        };

        if (payload.newPassword !== payload.repeatPassword) {
            alert("Nowe hasła nie są takie same.");
            return;
        }

        try {
            await api.changePassword(payload);

            alert("Hasło zostało zmienione.");

            form.currentPassword.value = "";
            form.newPassword.value = "";
            form.repeatPassword.value = "";

        } catch (error) {
            console.error("Błąd zmiany hasła:", error);

            alert(
                error?.data?.message ||
                error?.data ||
                "Nie udało się zmienić hasła."
            );
        }
    });
}