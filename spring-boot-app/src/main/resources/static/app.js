const EMPLOYEE_IDS = ["1001", "1002", "1003"];

let accessToken = null;
let selectedEmployeeId = null;

const employeeList = document.getElementById("employee-list");
const errorBanner = document.getElementById("error-banner");
const placeholder = document.getElementById("placeholder");
const employeeDetails = document.getElementById("employee-details");
const employeeTitle = document.getElementById("employee-title");
const goalsContent = document.getElementById("goals-content");
const learningContent = document.getElementById("learning-content");
const payDateContent = document.getElementById("pay-date-content");
const ptoContent = document.getElementById("pto-content");
const ptoForm = document.getElementById("pto-form");
const ptoResult = document.getElementById("pto-result");

async function fetchToken() {
  const response = await fetch("oauth/token", {
    method: "POST",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: new URLSearchParams({
      grant_type: "client_credentials",
      client_id: "demo-client",
      client_secret: "demo-secret"
    })
  });
  const body = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw createApiError(response.status, body);
  }
  accessToken = body.access_token;
  return accessToken;
}

async function apiFetch(path, options = {}, retry = true) {
  if (!accessToken) {
    await fetchToken();
  }

  const headers = new Headers(options.headers || {});
  headers.set("Authorization", `Bearer ${accessToken}`);
  const response = await fetch(path, {...options, headers});
  const body = await response.json().catch(() => ({}));

  if (response.status === 401 && retry) {
    accessToken = null;
    await fetchToken();
    return apiFetch(path, options, false);
  }
  if (!response.ok) {
    throw createApiError(response.status, body);
  }
  return body;
}

function createApiError(status, body) {
  const error = new Error(body.message || `Request failed (${status})`);
  error.status = status;
  error.body = body;
  return error;
}

function showGlobalError(message) {
  errorBanner.textContent = message;
  errorBanner.hidden = !message;
}

function renderEmployeeButtons() {
  employeeList.replaceChildren();
  EMPLOYEE_IDS.forEach((employeeId) => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "employee-button";
    button.textContent = `Employee ${employeeId}`;
    button.addEventListener("click", () => selectEmployee(employeeId));
    employeeList.append(button);
  });
}

async function selectEmployee(employeeId) {
  selectedEmployeeId = employeeId;
  document.querySelectorAll(".employee-button").forEach((button) => {
    button.classList.toggle("selected", button.textContent === `Employee ${employeeId}`);
  });
  placeholder.hidden = true;
  employeeDetails.hidden = false;
  employeeTitle.textContent = `Employee ${employeeId}`;
  ptoResult.hidden = true;
  showGlobalError("");
  renderLoadingState();

  const paths = [
    `api/employee/${employeeId}/goals`,
    `api/employee/${employeeId}/learning-status`,
    `api/employee/${employeeId}/next-pay-date`,
    `api/employee/${employeeId}/pto/balance`
  ];
  const results = await Promise.allSettled(paths.map((path) => apiFetch(path)));
  if (selectedEmployeeId !== employeeId) {
    return;
  }

  const [goals, learning, payDate, pto] = results;
  renderGoals(goals);
  renderLearning(learning);
  renderPayDate(payDate);
  renderPtoBalance(pto);
  const failed = results.find((result) => result.status === "rejected");
  if (failed) {
    showGlobalError(failed.reason.message || "Request failed");
  }
}

function renderLoadingState() {
  goalsContent.textContent = "Loading…";
  learningContent.textContent = "Loading…";
  payDateContent.textContent = "Loading…";
  ptoContent.textContent = "Loading…";
}

function renderGoals(result) {
  if (result.status === "rejected") {
    goalsContent.textContent = result.reason.message;
    return;
  }
  const list = document.createElement("ul");
  list.className = "goals-list";
  result.value.forEach((goal) => {
    const item = document.createElement("li");
    item.textContent = goal;
    list.append(item);
  });
  goalsContent.replaceChildren(list);
}

function renderLearning(result) {
  if (result.status === "rejected") {
    learningContent.textContent = result.reason.message;
    return;
  }
  const table = document.createElement("table");
  table.innerHTML = `
    <thead><tr><th>Course</th><th>Status</th><th>Progress</th></tr></thead>
    <tbody></tbody>
  `;
  const body = table.querySelector("tbody");
  result.value.courses.forEach((course) => {
    const row = document.createElement("tr");
    const statusClass = course.status.toLowerCase().replace("_", "-");
    row.innerHTML = `
      <td>${escapeHtml(course.courseName)}</td>
      <td><span class="status-badge ${statusClass}">${escapeHtml(course.status.replace("_", " "))}</span></td>
      <td>
        <div class="progress-value">${course.progress}%</div>
        <div class="progress-track" aria-label="${course.progress}% complete">
          <span style="width: ${course.progress}%"></span>
        </div>
      </td>
    `;
    body.append(row);
  });
  learningContent.replaceChildren(table);
}

function renderPayDate(result) {
  if (result.status === "rejected") {
    payDateContent.textContent = result.reason.message;
    return;
  }
  payDateContent.innerHTML = `
    <p class="large-value">${escapeHtml(result.value.nextPayDate)}</p>
    <p class="muted">${escapeHtml(result.value.payFrequency)}</p>
  `;
}

function renderPtoBalance(result) {
  if (result.status === "rejected") {
    ptoContent.textContent = result.reason.message;
    return;
  }
  const balance = result.value;
  ptoContent.innerHTML = `
    <p class="large-value">${formatHours(balance.balance)} hours available</p>
    <p class="muted">${formatHours(balance.used)} used of ${formatHours(balance.total)} total hours</p>
  `;
}

async function refreshPtoBalance() {
  const result = await apiFetch(`api/employee/${selectedEmployeeId}/pto/balance`);
  renderPtoBalance({status: "fulfilled", value: result});
}

async function submitPto(event) {
  event.preventDefault();
  ptoResult.hidden = true;
  const formData = new FormData(ptoForm);
  const request = {
    startDate: formData.get("startDate"),
    endDate: formData.get("endDate"),
    hours: Number(formData.get("hours"))
  };
  try {
    const response = await apiFetch(`api/employee/${selectedEmployeeId}/pto/schedule`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(request)
    });
    ptoResult.className = "form-result success";
    ptoResult.textContent = `${response.message} — Request ${response.requestId} (${response.hoursScheduled}h, ${response.startDate}→${response.endDate})`;
    ptoResult.hidden = false;
    await refreshPtoBalance();
  } catch (error) {
    ptoResult.className = "form-result failure";
    ptoResult.textContent = error.message;
    ptoResult.hidden = false;
    showGlobalError(error.message);
  }
}

function formatHours(value) {
  return Number.isInteger(value) ? value : Number(value).toFixed(1);
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

document.addEventListener("DOMContentLoaded", async () => {
  renderEmployeeButtons();
  ptoForm.addEventListener("submit", submitPto);
  try {
    await fetchToken();
  } catch (error) {
    showGlobalError(error.message);
  }
});
