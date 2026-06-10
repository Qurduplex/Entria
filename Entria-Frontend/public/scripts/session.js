const API_BASE_URL = "http://localhost:8080/api";

const SKEW_MS = 60 * 1000;        // odnów gdy zostało < 1 min
const ACTIVITY_WINDOW_MS = 60 * 1000; // "aktywny" = interakcja w ostatniej minucie
const CHECK_INTERVAL_MS = 15 * 1000;  // jak często sprawdzać

let lastActivity = Date.now();
let refreshPromise = null;
let intervalId = null;

// ─── ODŚWIEŻANIE ──────────────────────────────────────────────────────────
async function doRefresh() {
  const refreshToken = localStorage.getItem("refreshToken");
  if (!refreshToken) throw new Error("Brak refresh tokenu");

  const res = await fetch(`${API_BASE_URL}/auth/refresh-token`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken }),
  });
  if (!res.ok) throw new Error("Refresh nieudany");

  const data = await res.json();
  localStorage.setItem("jwtToken", data.jwtToken);
  if (data.expiresAt) localStorage.setItem("expiresAt", data.expiresAt);
  return data.jwtToken;
}

// pojedyncza obietnica refreshu — chroni przed równoległymi wywołaniami
export function refreshToken() {
  if (!refreshPromise) {
    refreshPromise = doRefresh().finally(() => { refreshPromise = null; });
  }
  return refreshPromise;
}

export function logout() {
  stopSessionWatcher();
  localStorage.removeItem("jwtToken");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("expiresAt");
  window.location.href = "/pages/auth/login.html"; // dostosuj ścieżkę
}

// ─── PROAKTYWNE ODNAWIANIE ────────────────────────────────────────────────
function msUntilExpiry() {
  const expiresAt = localStorage.getItem("expiresAt");
  if (!expiresAt) return Infinity;        // brak danych → nie wymuszaj
  return new Date(expiresAt).getTime() - Date.now();
}

function isUserActive() {
  return Date.now() - lastActivity < ACTIVITY_WINDOW_MS;
}

async function tick() {
  if (!localStorage.getItem("jwtToken")) return;

  const remaining = msUntilExpiry();

  // zostało mało czasu I user aktywny → odśwież
  if (remaining < SKEW_MS && remaining > 0 && isUserActive()) {
    try {
      await refreshToken();
    } catch {
      // refresh padł (np. refresh token wygasł) → koniec sesji
      logout();
    }
  }
}

// ─── START / STOP ─────────────────────────────────────────────────────────
function markActivity() { lastActivity = Date.now(); }

export function startSessionWatcher() {
  if (intervalId) return; // już działa

  ["click", "keydown", "scroll", "mousemove", "touchstart"].forEach((ev) =>
    window.addEventListener(ev, markActivity, { passive: true })
  );

  intervalId = setInterval(tick, CHECK_INTERVAL_MS);
  tick(); // pierwszy check od razu
}

export function stopSessionWatcher() {
  if (intervalId) {
    clearInterval(intervalId);
    intervalId = null;
  }
}