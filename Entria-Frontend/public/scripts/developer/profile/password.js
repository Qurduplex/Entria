export function initProfilePassword() {

    const data = {
        currentPassword: "Test123!",
        newPassword: "NewPassword123!",
        repeatPassword: "NewPassword123!",
    };

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


    form.currentPassword.value = data.currentPassword;
    form.newPassword.value = data.newPassword;
    form.repeatPassword.value = data.repeatPassword;

    form.submitButton.addEventListener("click", async () => {

        const payload = {
        currentPassword: form.currentPassword.value,
        newPassword: form.newPassword.value,
        repeatPassword: form.repeatPassword.value,
        };

        console.log(payload);
    });
}