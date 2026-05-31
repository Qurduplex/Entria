import { navigateToDeveloperPage } from "../../sideBar.js";

export function loadUserProfile() {
  const data = {
    name: "Jan Kowalski",
    email: "jan.kowalski@example.com",
    profileCompletion: 60,
    avatarUrl: null,
  };

  const avatar = document.getElementById("profile-avatar");
  const name = document.getElementById("profile-name");
  const email = document.getElementById("profile-email");
  const bar = document.getElementById("profile-progress-bar");
  const label = document.getElementById("profile-progress-label");
  const btn = document.getElementById("profile-action-btn");

if (!avatar || !name || !email || !bar || !label || !btn) return;

  if (data.avatarUrl) {
    avatar.innerHTML = `
      <img
        src="${data.avatarUrl}"
        alt="${data.name}"
        class="h-full w-full rounded-full object-cover"
      />
    `;
  } else {
    const initials = data.name
      .split(" ")
      .map((w) => w[0])
      .join("")
      .toUpperCase();
    avatar.textContent = initials;
  }

  name.textContent = data.name;
  email.textContent = data.email;
  label.textContent = `Profil uzupełniony w ${data.profileCompletion}%`;

  btn.textContent = data.profileCompletion === 100 ? "Zobacz profil" : "Uzupełnij profil";
  btn.addEventListener("click", () => navigateToDeveloperPage("profile"));


  requestAnimationFrame(() => {
    bar.style.width = `${data.profileCompletion}%`;
  });
}