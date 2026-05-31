export function initUserHistory() {

  const allEntries = [
    { app: "ParkFlow",  device: "MacBook Pro",         browser: "Mozilla Firefox", location: "Kraków, PL",   date: "2026-03-22", time: "18:42", success: true  },
    { app: "E-recepta", device: "iPhone 15",            browser: "Safari",          location: "Kraków, PL",   date: "2026-03-22", time: "09:41", success: true  },
    { app: "Entria",    device: "Nieznane urządzenie",  browser: "Chrome",          location: "Helsinki, FI", date: "2026-03-22", time: "09:41", success: false },
    { app: "E-recepta", device: "iPhone 15",            browser: "Safari",          location: "Warszawa, PL", date: "2026-03-20", time: "09:41", success: true  },
    { app: "Entria",    device: "Nieznane urządzenie",  browser: "Chrome",          location: "Helsinki, FI", date: "2026-03-20", time: "09:41", success: false },
    { app: "ParkFlow",  device: "MacBook Pro",          browser: "Chrome",          location: "Kraków, PL",   date: "2026-03-18", time: "14:20", success: true  },
    { app: "Bookie",    device: "iPhone 15",            browser: "Safari",          location: "Kraków, PL",   date: "2026-03-18", time: "11:05", success: true  },
    { app: "Entria",    device: "Nieznane urządzenie",  browser: "Firefox",         location: "Berlin, DE",   date: "2026-03-15", time: "03:12", success: false },
        { app: "ParkFlow",  device: "MacBook Pro",         browser: "Mozilla Firefox", location: "Kraków, PL",   date: "2026-03-22", time: "18:42", success: true  },
    { app: "E-recepta", device: "iPhone 15",            browser: "Safari",          location: "Kraków, PL",   date: "2026-03-22", time: "09:41", success: true  },
    { app: "Entria",    device: "Nieznane urządzenie",  browser: "Chrome",          location: "Helsinki, FI", date: "2026-03-22", time: "09:41", success: false },
    { app: "E-recepta", device: "iPhone 15",            browser: "Safari",          location: "Warszawa, PL", date: "2026-03-20", time: "09:41", success: true  },
    { app: "Entria",    device: "Nieznane urządzenie",  browser: "Chrome",          location: "Helsinki, FI", date: "2026-03-20", time: "09:41", success: false },
    { app: "ParkFlow",  device: "MacBook Pro",          browser: "Chrome",          location: "Kraków, PL",   date: "2026-03-18", time: "14:20", success: true  },
    { app: "Bookie",    device: "iPhone 15",            browser: "Safari",          location: "Kraków, PL",   date: "2026-03-18", time: "11:05", success: true  },
    { app: "Entria",    device: "Nieznane urządzenie",  browser: "Firefox",         location: "Berlin, DE",   date: "2026-03-15", time: "03:12", success: false },
  ];

  // ─── STATYSTYKI ───────────────────────────────────────────────────────────
  document.getElementById("stat-total").textContent   = allEntries.length;
  document.getElementById("stat-failed").textContent  = allEntries.filter(e => !e.success).length;
  document.getElementById("stat-devices").textContent = new Set(allEntries.map(e => e.device)).size;

  // ─── FORMAT DATY ─────────────────────────────────────────────────────────
  function formatDateHeader(dateStr) {
    const date  = new Date(dateStr);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    date.setHours(0, 0, 0, 0);
    const label = new Date(dateStr)
      .toLocaleDateString("pl-PL", { day: "numeric", month: "long", year: "numeric" })
      .toUpperCase();
    return date.getTime() === today.getTime() ? `Dziś – ${label}` : label;
  }

  // ─── OBLICZ ILE WPISÓW MIEŚCI SIĘ NA EKRANIE ────────────────────────────
  const ROW_HEIGHT    = 53;
  const DATE_HEADER_H = 44;
  const STATS_H       = 140;
  const PAGE_MARGIN   = 40;

  function calcVisibleCount() {
    const card      = document.getElementById("history-card");
    const cardTop   = card ? card.getBoundingClientRect().top : 200;
    const available = window.innerHeight - cardTop - STATS_H - PAGE_MARGIN;

    let usedHeight = 0;
    let count      = 0;
    let lastDate   = null;

    for (const entry of allEntries) {
      if (entry.date !== lastDate) {
        usedHeight += DATE_HEADER_H;
        lastDate    = entry.date;
      }
      usedHeight += ROW_HEIGHT;
      if (usedHeight > available) break;
      count++;
    }

    return Math.max(count, 3);
  }

  let visibleCount = calcVisibleCount();
  let scrollMode   = visibleCount < allEntries.length;

  // ─── RENDER LISTY ────────────────────────────────────────────────────────
  function renderList() {
    const container = document.getElementById("history-list");
    const wrapper   = document.getElementById("history-load-more-wrapper");
    const card      = document.getElementById("history-card");
    if (!container) return;

    const visible = allEntries.slice(0, visibleCount);

    const groups = [];
    visible.forEach(entry => {
      const last = groups[groups.length - 1];
      if (last && last.date === entry.date) {
        last.entries.push(entry);
      } else {
        groups.push({ date: entry.date, entries: [entry] });
      }
    });

    container.innerHTML = "";

    groups.forEach(group => {
      const header = document.createElement("div");
      header.className = "px-5 pt-5 pb-2";
      header.innerHTML = `<p class="text-[11px] uppercase tracking-wide text-[#8D8D8D] font-medium">${formatDateHeader(group.date)}</p>`;
      container.appendChild(header);

      group.entries.forEach(entry => {
        const row = document.createElement("div");
        row.className = "flex items-center gap-3 px-5 py-3.5 border-t border-gray-100";

        // 1. dot
        const dot = document.createElement("div");
        dot.className = `w-2 h-2 rounded-full shrink-0 ${entry.success ? "bg-green-500" : "bg-red-500"}`;

        // 2. nazwa aplikacji — stała szerokość
        const app = document.createElement("p");
        app.className = "text-[14px] font-semibold text-[#161619] w-[110px] shrink-0 truncate";
        app.textContent = entry.app;

        // 3. urządzenie + przeglądarka — bierze resztę miejsca
        const device = document.createElement("p");
        device.className = "flex-1 text-[13px] text-[#161619] truncate min-w-0";
        device.textContent = `${entry.device} – ${entry.browser}`;

        // prawa strona — stały blok z wyrównaną zawartością
        const right = document.createElement("div");
        right.className = "flex items-center gap-4 shrink-0 ml-4";

        // 4. lokalizacja — stała szerokość, wyrównana do prawej
        const location = document.createElement("p");
        location.className = "text-[13px] text-[#8D8D8D] w-[100px] text-right hidden md:block";
        location.textContent = entry.location;

        // 5. czas — stała szerokość, tabular
        const time = document.createElement("p");
        time.className = "text-[13px] text-[#8D8D8D] w-[40px] text-right tabular-nums";
        time.textContent = entry.time;

        // 6. badge — stała szerokość
        const badge = document.createElement("span");
        badge.className = `inline-block w-[72px] text-center text-[12px] font-medium px-2.5 py-0.5 rounded-full ${entry.success ? "bg-emerald-50 text-emerald-600" : "bg-red-50 text-red-500"}`;
        badge.textContent = entry.success ? "Sukces" : "Nieudane";

        right.append(location, time, badge);
        row.append(dot, app, device, right);
        container.appendChild(row);
      });
    });

    const hasMore = visibleCount < allEntries.length;

    if (wrapper) {
      if (hasMore) {
        wrapper.classList.remove("hidden");
        wrapper.classList.add("flex");
      } else {
        wrapper.classList.add("hidden");
        wrapper.classList.remove("flex");
      }
    }

    if (scrollMode && card) {
      const cardTop = card.getBoundingClientRect().top + window.scrollY;
      const maxH    = window.innerHeight - cardTop - PAGE_MARGIN;
      card.style.maxHeight = `${maxH}px`;
      card.style.height    = `${maxH}px`;
    } else if (card) {
      card.style.maxHeight = "";
      card.style.height    = "";
    }
  }

  // ─── LOAD MORE ────────────────────────────────────────────────────────────
  document.getElementById("history-load-more")?.addEventListener("click", () => {
    visibleCount += calcVisibleCount();
    scrollMode = true;
    renderList();
  });

  window.addEventListener("resize", () => {
    visibleCount = calcVisibleCount();
    scrollMode   = visibleCount < allEntries.length;
    renderList();
  });

  renderList();
}