export function loadAppsTable() {

  const data = [
    {
      name: "ParkFlow",
      logins: 1248,
      status: "Aktywna"
    },

    {
      name: "E-recepta",
      logins: 842,
      status: "Aktywna"
    },

    {
      name: "Bookie",
      logins: 92,
      status: "Nieaktywna"
    },

    {
      name: "Entria",
      logins: 2140,
      status: "Aktywna"
    }
  ];

  const tableBody =
    document.getElementById("apps-table-body");

  if (!tableBody) return;

  tableBody.innerHTML = "";

  data.forEach(app => {

    const isActive =
      app.status === "Aktywna";

    tableBody.innerHTML += `
        <tr class="border-b border-gray-200">

            <td class="py-5 text-lg font-medium text-gray-900 text-center">
            ${app.name}
            </td>

            <td class="py-5 text-lg text-gray-800 text-center">
            ${app.logins}
            </td>

            <td class="py-5 text-lg text-gray-800 text-center">
            ${app.status}
            </td>

        </tr>
        `;
  });
}