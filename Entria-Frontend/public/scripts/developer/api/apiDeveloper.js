import { API_BASE_URL } from "../../config.js"; 

const endpoints = {
    getMyProfile: "/user-profile/me",
    updateMyProfile: "/user-profile/me",
    getMyEmail: "/auth/me/email",
    changePassword: "/auth/me/change-password",

    getDeveloperApps: "/apps/app-list",
    registerApplication: "/apps/register-application",
    getApplicationDetails: (appId) => `/apps/details/${appId}`,
    deactivateApplication: (appId) => `/apps/deactivate/${appId}`,
    deleteApplication: (appId) => `/apps/delete/${appId}`,
    updateApplication: "/apps/update-application",
    regenerateClientSecret: "/apps/regenerate-client-secret",
    regenerateAuthorizeUrl: "/apps/regenerate-authorize-url",
};

async function request(endpointKey, options = {}) {
    const endpoint = endpoints[endpointKey];

    if (!endpoint) {
        throw new Error(`Endpoint "${endpointKey}" nie istnieje`);
    }

    const finalEndpoint =
        typeof endpoint === "function"
            ? endpoint(options.pathParams?.appId)
            : endpoint;

    const isFormData = options.body instanceof FormData;

    const response = await fetch(`${API_BASE_URL}${finalEndpoint}`, {
        ...options,
        headers: {
            ...(!isFormData ? { "Content-Type": "application/json" } : {}),
            ...options.headers,
        },
    });

    const data = await response.json().catch(() => null);

    if (!response.ok) {
        throw {
            status: response.status,
            data,
        };
    }

    return data;
}

function getPayloadFromToken() {
    const token = localStorage.getItem("jwtToken");
    return JSON.parse(atob(token.split(".")[1]));
}

function getUserIdFromToken() {
    return getPayloadFromToken().sub;
}

function getUserRoleFromToken() {
    return getPayloadFromToken().role;
}

function mapDraftPermissionsToBackend(permissions = {}) {
    const permissionMap = {
        openid: "OPENID",
        email: "EMAIL",
        profile: "PROFILE",
        phone: "PHONE",
        pesel: "PESEL",
        birthdate: "BIRTHDATE",
        gender: "GENDER",
        picture: "PICTURE",
    };

    const backendPermissions = {};

    Object.entries(permissions).forEach(([key, value]) => {
        if (!value.enabled) return;

        const backendKey = permissionMap[key];
        if (!backendKey) return;

        backendPermissions[backendKey] = value.required;
    });

    return backendPermissions;
}

export const api = {
    getMyProfile: () => {
        console.log("JWT Z LOCAL STORAGE:", localStorage.getItem("jwtToken"));
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

        formData.append("firstName", payload.firstName || "");
        formData.append("lastName", payload.lastName || "");
        formData.append("phoneNumber", payload.phoneNumber || "");

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

    getDeveloperApps: () => {
        const token = localStorage.getItem("jwtToken");
        const userId = getUserIdFromToken();
        const role = getUserRoleFromToken();

        return request("getDeveloperApps", {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    },

    registerApplication: (draft) => {
        const token = localStorage.getItem("jwtToken");

        const formData = new FormData();

        formData.append("name", draft.name || "");

        const redirectUri = draft.redirectUri || draft.redirectUris?.[0] || "";
        formData.append("redirectUri", redirectUri);

        if (draft.logoFile) {
            formData.append("logo", draft.logoFile);
        }

        if (draft.tosPdfFile) {
            formData.append("tosPdf", draft.tosPdfFile);
        }

        const permissionMap = {
            profile: "PROFILE",
            email: "EMAIL",
            phone: "PHONE",
            pesel: "PESEL",
            birthdate: "BIRTHDATE",
            gender: "GENDER",
            picture: "PICTURE",
            first_name: "PROFILE",
            last_name: "PROFILE",
        };

        Object.entries(draft.permissions || {}).forEach(([key, value]) => {
            if (!value.enabled) return;

            const backendScope = permissionMap[key];
            if (!backendScope) return;

            formData.append(`permissions[${backendScope}]`, String(value.required));
        });

        for (const [key, value] of formData.entries()) {
            console.log(key, value);
        }

        return request("registerApplication", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`,
            },
            body: formData,
        });
    },
    

    updateApplication: (payload) => {
        const token = localStorage.getItem("jwtToken");

        const formData = new FormData();

        formData.append("appId", payload.appId);
        formData.append("name", payload.name);
        formData.append("redirectUri", payload.redirectUri);

        if (payload.logoFile) {
            formData.append("logo", payload.logoFile);
        }

        if (payload.tosPdfFile) {
            formData.append("tosPdf", payload.tosPdfFile);
        }

        Object.entries(mapDraftPermissionsToBackend(payload.permissions))
            .forEach(([key, value]) => {
                formData.append(`permissions[${key}]`, String(value));
            });

        return request("updateApplication", {
            method: "PATCH",
            headers: {
                Authorization: `Bearer ${token}`,
            },
            body: formData,
        });
    },

    getApplicationDetails: (appId) => {
        const token = localStorage.getItem("jwtToken");

        return request("getApplicationDetails", {
            method: "GET",
            pathParams: {
                appId,
            },
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    },
    deactivateApplication: (appId) => {
        const token = localStorage.getItem("jwtToken");
        const userId = getUserIdFromToken();
        const role = getUserRoleFromToken();

        return request("deactivateApplication", {
            method: "PATCH",
            pathParams: {
                appId,
            },
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    },
    deleteApplication: (appId) => {
        const token = localStorage.getItem("jwtToken");
        const userId = getUserIdFromToken();
        const role = getUserRoleFromToken();

        return request("deleteApplication", {
            method: "DELETE",
            pathParams: {
                appId,
            },
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    },
    regenerateClientSecret: (appId) => {
        const userId = getUserIdFromToken();
        const role = getUserRoleFromToken();

        return request("regenerateClientSecret", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            body: JSON.stringify({
                appId,
            }),
        });
    },
    regenerateAuthorizeUrl: (appId) => {
        const userId = getUserIdFromToken();
        const role = getUserRoleFromToken();

        return request("regenerateAuthorizeUrl", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            body: JSON.stringify({
                appId,
            }),
        });
    },
};

window.api = api;