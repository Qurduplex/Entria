import { API_BASE_URL } from "../../config.js"; 

const endpoints = {
  getMyProfile: "/user-profile/me",
  updateMyProfile: "/user-profile/me",
};

async function request(endpointKey, options = {}) {
  const endpoint = endpoints[endpointKey];

  if (!endpoint) {
    throw new Error(`Endpoint "${endpointKey}" nie istnieje`);
  }

  const isFormData = options.body instanceof FormData;

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      ...(!isFormData ? { "Content-Type": "application/json" } : {}),
      ...options.headers,
    },
  });

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    throw { status: response.status, data };
  }

  return data;
}

export const userApi = {
  getMyProfile: () => {
    const token = localStorage.getItem("jwtToken");
    return request("getMyProfile", {
      method: "GET",
      headers: { Authorization: `Bearer ${token}` },
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
      headers: { Authorization: `Bearer ${token}` },
      body: formData,
    });
  },
};

window.userApi = userApi;