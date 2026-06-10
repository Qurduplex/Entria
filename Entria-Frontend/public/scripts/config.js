const isLocal = ["localhost", "127.0.0.1"].includes(location.hostname);

export const API_BASE_URL = isLocal
  ? "http://localhost:8080/api"
  : "https://..../api";