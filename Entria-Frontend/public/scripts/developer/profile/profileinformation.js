import { api } from "../api/apiDeveloper.js";
export async function initProfileBasicData() {
    const profile = {
        first_name: document.getElementById("profile-first-name"),
        last_name: document.getElementById("profile-last-name"),
        email: document.getElementById("profile-email"),
        phone: document.getElementById("profile-phone"),
        editButton: document.getElementById("profile-edit-button"),
    };

    if (
        !profile.first_name ||
        !profile.last_name ||
        !profile.email ||
        !profile.phone ||
        !profile.editButton
    ) {
        return;
    }

    try {
        const data = await api.getMyProfile();
        const email = await api.getMyEmail();

        profile.first_name.value = data.firstName || "";
        profile.last_name.value = data.lastName || "";
        profile.email.value = email.email || "";
        profile.phone.value = data.phoneNumber || "";

    } catch (err) {
    }

    let isEditing = false;

    profile.editButton.addEventListener("click", async () => {
        isEditing = !isEditing;

        profile.first_name.disabled = !isEditing;
        profile.last_name.disabled = !isEditing;
        profile.email.disabled = !isEditing;
        profile.phone.disabled = !isEditing;

        if (isEditing) {
            profile.editButton.textContent = "Zapisz";

            profile.first_name.classList.add("border-b", "border-gray-400");
            profile.last_name.classList.add("border-b", "border-gray-400");
            profile.email.classList.add("border-b", "border-gray-400");
            profile.phone.classList.add("border-b", "border-gray-400");

        } else {

            profile.editButton.textContent = "Edytuj";

            profile.first_name.classList.remove("border-b", "border-gray-400");
            profile.last_name.classList.remove("border-b", "border-gray-400");
            profile.email.classList.remove("border-b", "border-gray-400");
            profile.phone.classList.remove("border-b", "border-gray-400");

            const payload = {
                firstName: profile.first_name.value,
                lastName: profile.last_name.value,
                phoneNumber: profile.phone.value,
            };

            try {
                const updatedProfile = await api.updateMyProfile(payload);
                showAlert("Profil został zapisany.", "success");
            } catch (err) {
                showAlert(
                    err.data?.message || "Nie udało się zapisać profilu.",
                    "error"
                );
            }
        }
    });
}