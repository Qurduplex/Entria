export function loadLoginsChart() {

  const data = [
    { date: "22.03", parkflow: 120, erecepta: 44, bookie: 1 },
    { date: "23.03", parkflow: 98, erecepta: 51, bookie: 1 },
    { date: "24.03", parkflow: 145, erecepta: 38, bookie: 0 },
    { date: "25.03", parkflow: 110, erecepta: 60, bookie: 1 },
    { date: "26.03", parkflow: 160, erecepta: 42, bookie: 0 },
    { date: "27.03", parkflow: 134, erecepta: 55, bookie: 1 },
    { date: "28.03", parkflow: 80, erecepta: 22, bookie: 0 },
  ];

  const series = [
    { key: "parkflow", label: "ParkFlow", color: "#7C7AF2" },
    { key: "erecepta", label: "E-recepta", color: "#16A085" },
    { key: "bookie", label: "Bookie", color: "#EF6C45" },
  ];

  const legend = document.getElementById("chart-legend");

  if (legend) {

    legend.innerHTML = "";

    series.forEach(item => {

      legend.innerHTML += `
        <div class="flex items-center gap-2">
          <div 
            class="h-3 w-3 rounded-full"
            style="background:${item.color}"
          ></div>

          <span class="text-gray-700">
            ${item.label}
          </span>
        </div>
      `;
    });
  }

  const canvas = document.getElementById("logins-chart");

  if (!canvas) return;

  const ctx = canvas.getContext("2d");

  canvas.width = canvas.offsetWidth;
  canvas.height = canvas.offsetHeight;

  const padding = {
    top: 45,
    right: 35,
    bottom: 35,
    left: 45,
  };

  const chartWidth =
    canvas.width - padding.left - padding.right;

  const chartHeight =
    canvas.height - padding.top - padding.bottom;

  const maxValue = 170;

  ctx.clearRect(0, 0, canvas.width, canvas.height);

  ctx.font = "12px Arial";
  ctx.fillStyle = "#9CA3AF";

  for (let i = 0; i <= 4; i++) {

    const value = i * 40;

    const y =
      padding.top +
      chartHeight -
      (value / maxValue) * chartHeight;

    ctx.beginPath();
    ctx.strokeStyle = "#F1F5F9";
    ctx.lineWidth = 1;
    ctx.moveTo(padding.left, y);
    ctx.lineTo(canvas.width - padding.right, y);
    ctx.stroke();

    ctx.fillText(value, 15, y + 4);
  }

  data.forEach((item, index) => {

    const x =
      padding.left +
      (index * chartWidth) / (data.length - 1);

    ctx.beginPath();
    ctx.strokeStyle = "#F1F5F9";
    ctx.moveTo(x, padding.top);
    ctx.lineTo(x, canvas.height - padding.bottom);
    ctx.stroke();

    ctx.fillStyle = "#6B7280";
    ctx.fillText(item.date, x - 15, canvas.height - 10);
  });

  function getPoint(item, index, key) {

    return {
      x:
        padding.left +
        (index * chartWidth) / (data.length - 1),

      y:
        padding.top +
        chartHeight -
        (item[key] / maxValue) * chartHeight,
    };
  }

  function drawSmoothLine(points, color) {

    ctx.beginPath();
    ctx.strokeStyle = color;
    ctx.lineWidth = 2;
    ctx.lineJoin = "round";
    ctx.lineCap = "round";

    ctx.moveTo(points[0].x, points[0].y);

    for (let i = 0; i < points.length - 1; i++) {

      const current = points[i];
      const next = points[i + 1];

      const midX = (current.x + next.x) / 2;

      ctx.bezierCurveTo(
        midX,
        current.y,
        midX,
        next.y,
        next.x,
        next.y
      );
    }

    ctx.stroke();
  }

  series.forEach(s => {

    const points = data.map((item, index) =>
      getPoint(item, index, s.key)
    );

    drawSmoothLine(points, s.color);

    points.forEach(point => {

      ctx.beginPath();
      ctx.fillStyle = s.color;

      ctx.arc(
        point.x,
        point.y,
        4,
        0,
        Math.PI * 2
      );

      ctx.fill();
    });
  });
}