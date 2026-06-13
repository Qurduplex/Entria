import { API_BASE_URL } from "../../config.js";

const endpoints = {
  getMyProfile: "/user-profile/me",
  updateMyProfile: "/user-profile/me",
  getMyEmail: "/auth/me/email",
  changePassword: "/auth/me/change-password",


  getConsents: "/oauth/user/consents",
  revokeConsent: (clientId) => `/oauth/user/consents/${clientId}`,
};

async function request(endpointKey, options = {}) {
  const endpoint = endpoints[endpointKey];

  if (!endpoint) {
    throw new Error(`Endpoint "${endpointKey}" nie istnieje`);
  }

  const finalEndpoint =
    typeof endpoint === "function"
      ? endpoint(options.pathParams?.clientId)
      : endpoint;

  const isFormData = options.body instanceof FormData;

  const response = await fetch(`${API_BASE_URL}${finalEndpoint}`, {
    ...options,
    headers: {
      ...(!isFormData ? { "Content-Type": "application/json" } : {}),
      ...options.headers,
    },
  });

  // DELETE /consents zwraca 204 No Content -> brak body
  const data = await response.json().catch(() => null);

  if (!response.ok) {
    throw {
      status: response.status,
      data,
    };
  }

  return data;
}

export const userApi = {
  getMyProfile: () => {
    const token = localStorage.getItem("jwtToken");

    return request("getMyProfile", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },

  updateMyProfile: (payload) => {
    const token = localStorage.getItem("jwtToken");

    const formData = new FormData();
    if (payload.firstName != null)  formData.append("firstName", payload.firstName);
    if (payload.lastName != null)   formData.append("lastName", payload.lastName);
    if (payload.phoneNumber)        formData.append("phoneNumber", payload.phoneNumber);
    if (payload.birthDate)          formData.append("birthDate", payload.birthDate);
    if (payload.pesel)              formData.append("pesel", payload.pesel);
    if (payload.sex)                formData.append("sex", payload.sex);
    if (payload.profilePicture)     formData.append("profilePicture", payload.profilePicture);

    return request("updateMyProfile", {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    });
  },

  getMyEmail: () => {
    const token = localStorage.getItem("jwtToken");
 
    return request("getMyEmail", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },

    changePassword: (payload) => {
    const token = localStorage.getItem("jwtToken");
 
    return request("changePassword", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        currentPassword: payload.currentPassword,
        newPassword: payload.newPassword,
      }),
    });
  },



  // ─── ZGODY / APLIKACJE ──────────────────────────────────────────────────
  getConsents: () => {
    const token = localStorage.getItem("jwtToken");

    return request("getConsents", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },

  revokeConsent: (clientId) => {
    const token = localStorage.getItem("jwtToken");

    return request("revokeConsent", {
      method: "DELETE",
      pathParams: { clientId },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },
};

window.userApi = userApi;