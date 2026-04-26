const API_BASE_URL = "http://localhost:8080/api";

const endpoints = {
    register: "/auth/register",
    login: "/auth/login",

    requestVerificationCode: "/auth/verification-code/request",
    verifyCode: "/auth/verification-code/verify",

    requestResetPasswordCode: "/auth/reset-password-code/request",
    resetPassword: "/auth/reset-password-code/reset",

    refreshToken: "/auth/refresh-token",
    logout: "/auth/logout",
    logoutAll: "/auth/logout/all",
};

async function request(endpointKey, options = {}) {
    const endpoint = endpoints[endpointKey];

    if (!endpoint) {
        throw new Error(`Endpoint "${endpointKey}" nie istnieje`);
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        headers: {
            "Content-Type": "application/json",
            ...options.headers,
        },
        ...options,
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

export const api = {
    register: (payload) =>
        request("register", {
            method: "POST",
            body: JSON.stringify(payload),
        }),

    login: (payload) =>
        request("login", {
            method: "POST",
            body: JSON.stringify(payload),
        }),

    requestVerificationCode: (payload) =>
        request("requestVerificationCode", {
            method: "POST",
            body: JSON.stringify(payload),
        }),

    verifyCode: (payload) =>
        request("verifyCode", {
            method: "POST",
            body: JSON.stringify(payload),
        }),

    requestResetPasswordCode: (payload) =>
        request("requestResetPasswordCode", {
            method: "POST",
            body: JSON.stringify(payload),
        }),

    resetPassword: (payload) =>
        request("resetPassword", {
            method: "POST",
            body: JSON.stringify(payload),
        }),
};