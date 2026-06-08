const API_BASE_URL = "http://localhost:8080/api";

const endpoints = {
    getMyProfile: "/user-profile/me",
    updateMyProfile: "/user-profile/me",

    getDeveloperApps: "/apps/app-list",
    registerApplication: "/apps/register-application",
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
        const userId = getUserIdFromToken();
        const role = getUserRoleFromToken();

        const formData = new FormData();

        formData.append("name", draft.name || "");
        formData.append("redirectUri", draft.redirectUri || "");

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
            profile_picture: "PROFILE_PICTURE",
        };

        Object.entries(draft.permissions || {}).forEach(([key, value]) => {
            if (!value.enabled) return;

            const backendKey = permissionMap[key];

            formData.append(
                `permissions[${backendKey}]`,
                String(value.required)
            );
        });

        return request("registerApplication", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`,
            },
            body: formData,
        });
    },
};

window.api = api;