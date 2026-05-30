import {
  getApplicationDraft,
  saveApplicationDraft
} from "./aplicationDetails.js";

export function loadPermissions() {
  const permissionCards = document.querySelectorAll(
    "[data-permission-card]"
  );

  if (!permissionCards.length) {
    return;
  }

  const draft = getApplicationDraft();

  if (!draft.permissions) {
    draft.permissions = {};
  }

  permissionCards.forEach(card => {
    const checkbox = card.querySelector("[data-permission]");
    const requiredButton = card.querySelector(
      '[data-permission-option="required"]'
    );
    const optionalButton = card.querySelector(
      '[data-permission-option="optional"]'
    );

    if (!checkbox || !requiredButton || !optionalButton) {
      return;
    }

    const permissionName = checkbox.dataset.permission;

    if (!draft.permissions[permissionName]) {
      draft.permissions[permissionName] = {
        enabled: checkbox.checked,
        required: true
      };
    }

    const savedPermission = draft.permissions[permissionName];

    checkbox.checked = savedPermission.enabled;

    renderPermissionState(
      checkbox,
      requiredButton,
      optionalButton,
      savedPermission.required
    );

    checkbox.addEventListener("change", () => {
      const currentDraft = getApplicationDraft();

      if (!currentDraft.permissions) {
        currentDraft.permissions = {};
      }

      const oldPermission =
        currentDraft.permissions[permissionName] || {};

      currentDraft.permissions[permissionName] = {
        enabled: checkbox.checked,
        required: oldPermission.required ?? true
      };

      saveApplicationDraft(currentDraft);

      renderPermissionState(
        checkbox,
        requiredButton,
        optionalButton,
        currentDraft.permissions[permissionName].required
      );

      console.log("Draft aplikacji:", currentDraft);
    });

    requiredButton.addEventListener("click", () => {
      if (!checkbox.checked || permissionName === "openid") {
        return;
      }

      updatePermission(
        permissionName,
        true,
        checkbox,
        requiredButton,
        optionalButton
      );
    });

    optionalButton.addEventListener("click", () => {
      if (!checkbox.checked || permissionName === "openid") {
        return;
      }

      updatePermission(
        permissionName,
        false,
        checkbox,
        requiredButton,
        optionalButton
      );
    });
  });

  saveApplicationDraft(draft);
}

function updatePermission(
  permissionName,
  isRequired,
  checkbox,
  requiredButton,
  optionalButton
) {
  const draft = getApplicationDraft();

  if (!draft.permissions) {
    draft.permissions = {};
  }

  draft.permissions[permissionName] = {
    enabled: checkbox.checked,
    required: isRequired
  };

  saveApplicationDraft(draft);

  renderPermissionState(
    checkbox,
    requiredButton,
    optionalButton,
    isRequired
  );

  console.log("Draft aplikacji:", draft);
}

function renderPermissionState(
  checkbox,
  requiredButton,
  optionalButton,
  isRequired
) {
  const isEnabled = checkbox.checked;
  const isOpenId = checkbox.dataset.permission === "openid";

  resetButton(requiredButton);
  resetButton(optionalButton);

  if (isOpenId) {
    requiredButton.classList.add(
      "bg-[#161619]",
      "text-white",
      "cursor-not-allowed"
    );

    optionalButton.classList.add(
      "text-gray-400",
      "opacity-40",
      "cursor-not-allowed"
    );

    return;
  }

  if (!isEnabled) {
    requiredButton.classList.add(
      "text-gray-400",
      "opacity-40",
      "cursor-not-allowed"
    );

    optionalButton.classList.add(
      "text-gray-400",
      "opacity-40",
      "cursor-not-allowed"
    );

    return;
  }

  requiredButton.classList.add("cursor-pointer");
  optionalButton.classList.add("cursor-pointer");

  if (isRequired) {
    requiredButton.classList.add(
      "bg-[#161619]",
      "text-white"
    );

    optionalButton.classList.add("text-gray-500");
  } else {
    optionalButton.classList.add(
      "bg-[#161619]",
      "text-white"
    );

    requiredButton.classList.add("text-gray-500");
  }
}

function resetButton(button) {
  button.classList.remove(
    "bg-[#161619]",
    "text-white",
    "text-gray-500",
    "text-gray-400",
    "opacity-40",
    "cursor-not-allowed",
    "cursor-pointer"
  );
}