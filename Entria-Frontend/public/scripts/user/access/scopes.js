import { cloneTemplate, ref } from "./templates.js";

export const scopeLabels = {
  openid:    { name: "Identyfikator konta" },
  profile:   { name: "Imię i nazwisko" },
  email:     { name: "Adres e-mail" },
  phone:     { name: "Numer telefonu" },
  pesel:     { name: "PESEL", sensitive: true },
  birthdate: { name: "Data urodzenia" },
  gender:    { name: "Płeć" },
  picture:   { name: "Zdjęcie profilowe" },
};

export function normalizeScope(authority) {
  return authority.startsWith("SCOPE_") ? authority.slice(6) : authority;
}

export function appInitials(name) {
  if (!name) return "?";
  const parts = name.trim().split(/\s+/);
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[1][0]).toUpperCase();
}

/**
 * Buduje wiersz pojedynczego uprawnienia z <template id="tpl-scope-row">.
 * @param {string} scope
 * @returns {HTMLElement}
 */
export function buildScopeRow(scope) {
  const meta = scopeLabels[scope] || { name: scope };

  const row = cloneTemplate("tpl-scope-row");
  ref(row, "scope-name").textContent = meta.name;

  if (meta.sensitive) {
    ref(row, "scope-sensitive").classList.remove("hidden");
  }

  return row;
}