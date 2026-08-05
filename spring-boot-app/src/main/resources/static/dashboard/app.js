import { getGoals, getLearningStatus, getNextPayDate, getPtoBalance, schedulePto } from "./api.js";

const select = document.querySelector("#employee-select");
const panels = {
  goals: { element: document.querySelector("#goals-content"), load: getGoals, render: renderGoals },
  learning: { element: document.querySelector("#learning-content"), load: getLearningStatus, render: renderLearning },
  pay: { element: document.querySelector("#pay-content"), load: getNextPayDate, render: renderPay },
  pto: { element: document.querySelector("#pto-content"), load: getPtoBalance, render: renderPto }
};

function loading(element) {
  element.innerHTML = '<div class="loading">Loading information…</div>';
}

function errorState(element, error, retry) {
  element.innerHTML = `<div class="error">${escapeHtml(error.message)}</div><button class="retry" type="button">Try again</button>`;
  element.querySelector(".retry").addEventListener("click", retry);
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (char) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#039;"
  }[char]));
}

function renderGoals(goals) {
  return `<ul class="goal-list">${goals.map((goal) => `<li>${escapeHtml(goal)}</li>`).join("")}</ul>`;
}

function renderLearning(data) {
  return data.courses.map((course) => `
    <div class="course">
      <div class="course-line">
        <span class="course-name">${escapeHtml(course.courseName)}</span>
        <span class="status status-${course.status.toLowerCase().replace(/_/g, "-")}">
          ${course.status.replace(/_/g, " ")}
        </span>
      </div>
      <div class="progress-track">
        <div class="progress-bar" style="width:${course.progress}%"></div>
      </div>
      <div class="progress-caption">${course.progress}% complete</div>
    </div>`).join("");
}

function renderPay(data) {
  return `
    <div class="pay-card">
      <div class="pay-date">${formatDate(data.nextPayDate)}</div>
      <div class="pay-frequency">${escapeHtml(data.payFrequency)} pay schedule</div>
    </div>`;
}

function renderPto(data) {
  return `
    <div class="stats">
      <div class="stat">
        <div class="stat-value">${data.balance}h</div>
        <div class="stat-label">Available</div>
      </div>
      <div class="stat">
        <div class="stat-value">${data.used}h</div>
        <div class="stat-label">Used</div>
      </div>
      <div class="stat">
        <div class="stat-value">${data.total}h</div>
        <div class="stat-label">Annual total</div>
      </div>
    </div>`;
}

function formatDate(value) {
  return new Intl.DateTimeFormat("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric"
  }).format(new Date(`${value}T00:00:00`));
}

async function loadPanel(name) {
  const panel = panels[name];
  const employeeId = select.value;
  loading(panel.element);
  try {
    panel.element.innerHTML = panel.render(await panel.load(employeeId));
  } catch (error) {
    errorState(panel.element, error, () => loadPanel(name));
  }
}

function loadAll() {
  Object.keys(panels).forEach(loadPanel);
}

select.addEventListener("change", loadAll);
document.querySelector("#pto-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const button = form.querySelector("button");
  const message = document.querySelector("#form-message");
  const payload = Object.fromEntries(new FormData(form));
  payload.hours = Number(payload.hours);
  message.className = "form-message";
  message.textContent = "Submitting request…";
  button.disabled = true;
  try {
    const result = await schedulePto(select.value, payload);
    message.className = "form-message success";
    message.textContent = result.message;
    form.reset();
    loadPanel("pto");
  } catch (error) {
    message.className = "form-message form-error";
    message.textContent = error.message;
  } finally {
    button.disabled = false;
  }
});
loadAll();
