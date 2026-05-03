export function showAlert(message, type = "success") {
    const container = document.getElementById("toast-container");
    if (!container) return;

    const styles = {
        success: "bg-green-500/15 border-green-400/30 text-green-200",
        error: "bg-red-500/15 border-red-400/30 text-red-200",
        warning: "bg-yellow-500/15 border-yellow-400/30 text-yellow-200",
        info: "bg-blue-500/15 border-blue-400/30 text-blue-200"
    };

    const toast = document.createElement("div");

    toast.className = `
        min-w-[260px] max-w-[360px]
        border
        px-5 py-4
        rounded-[16px]
        text-[14px] font-medium
        backdrop-blur-xl
        shadow-[0_20px_60px_rgba(0,0,0,0.35)]
        transition-all duration-300
        ${styles[type] || styles.info}
        translate-x-5 opacity-0
    `;

    toast.textContent = message;

    container.appendChild(toast);

    requestAnimationFrame(() => {
        toast.classList.remove("translate-x-5", "opacity-0");
    });

    setTimeout(() => {
        toast.classList.add("opacity-0", "translate-x-5");
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}