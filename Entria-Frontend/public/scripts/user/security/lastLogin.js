export function initLastLogin() {
  const data = {
    date: "21 marca 2026, 18:42",
    relative: "2 godziny temu",
    location: "Kraków, PL",
    device: "MacBook Pro",
    os: "macOS 14.4",
    browser: "Mozilla Firefox",
  };

  const set = (id, val) => {
    const el = document.getElementById(id);
    if (el) el.textContent = val;
  };

  set("last-login-date", data.date);
  set("last-login-relative", data.relative);
  set("last-login-location", data.location);
  set("last-login-device", data.device);
  set("last-login-os", data.os);
  set("last-login-browser", data.browser);
}