// ─── HELPER: klonowanie <template> z fragmentu access.html ──────────────────

/**
 * Klonuje zawartość <template id="..."> i zwraca pierwszy element.
 * @param {string} templateId
 * @returns {HTMLElement}
 */
export function cloneTemplate(templateId) {
  const tpl = document.getElementById(templateId);

  if (!tpl || !(tpl instanceof HTMLTemplateElement)) {
    throw new Error(`Template "${templateId}" nie istnieje lub nie jest <template>`);
  }

  const fragment = tpl.content.cloneNode(true);
  return fragment.firstElementChild;
}

/**
 * Skrót do data-atrybutów: el.querySelector(`[data-${name}]`)
 * @param {HTMLElement} root
 * @param {string} name
 * @returns {HTMLElement | null}
 */
export function ref(root, name) {
  return root.querySelector(`[data-${name}]`);
}