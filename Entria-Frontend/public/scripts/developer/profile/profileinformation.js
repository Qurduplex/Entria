export function initProfileBasicData() {

  const data = {
    first_name : "Anna",
    last_name : "Developer",
    email: "kontakt@techwave.pl",
    phone: "+48 123 456 789",
  };

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

  profile.first_name.value = data.first_name;
  profile.last_name.value = data.last_name;
  profile.email.value = data.email;
  profile.phone.value = data.phone;

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
        first_name: profile.first_name.value,
        last_name: profile.last_name.value,
        email: profile.email.value,
        phone: profile.phone.value,
      };

      console.log(payload);

      // MOCK API REQUEST
      /*
      await fetch("/api/profile/update", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });
      */
    }
  });
}