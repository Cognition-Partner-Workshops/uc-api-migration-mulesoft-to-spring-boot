/* Dashboard UI logic. All network/mock calls go through window.EmployeeApi (api.js). */
(function () {
  const api = window.EmployeeApi;

  const employeeInput = document.getElementById('employee-id');
  const loadBtn = document.getElementById('load-btn');
  const goalsBody = document.getElementById('goals-body');
  const learningBody = document.getElementById('learning-body');
  const payBody = document.getElementById('pay-body');
  const ptoBalanceBody = document.getElementById('pto-balance-body');
  const ptoForm = document.getElementById('pto-form');
  const ptoFeedback = document.getElementById('pto-feedback');
  const ptoSubmit = document.getElementById('pto-submit');

  function currentEmployeeId() {
    return employeeInput.value.trim() || 'EMP001';
  }

  function setLoading(el) {
    el.innerHTML = '<p class="muted">Loading…</p>';
  }

  function setError(el, err) {
    const msg = err && err.message ? err.message : 'Something went wrong';
    el.innerHTML = '';
    const div = document.createElement('div');
    div.className = 'error-state';
    div.textContent = msg;
    el.appendChild(div);
  }

  function formatDate(isoDate) {
    const d = new Date(isoDate + 'T00:00:00');
    if (isNaN(d)) return isoDate;
    return d.toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
  }

  function renderGoals(goals) {
    goalsBody.innerHTML = '';
    if (!goals.length) {
      goalsBody.innerHTML = '<p class="muted">No goals set.</p>';
      return;
    }
    const ul = document.createElement('ul');
    ul.className = 'goals-list';
    goals.forEach((g) => {
      const li = document.createElement('li');
      li.textContent = g;
      ul.appendChild(li);
    });
    goalsBody.appendChild(ul);
  }

  function renderLearning(learning) {
    learningBody.innerHTML = '';
    const courses = learning.courses || [];
    if (!courses.length) {
      learningBody.innerHTML = '<p class="muted">No courses assigned.</p>';
      return;
    }
    courses.forEach((c) => {
      const wrap = document.createElement('div');
      wrap.className = 'course';

      const header = document.createElement('div');
      header.className = 'course-header';

      const name = document.createElement('span');
      name.className = 'course-name';
      name.textContent = c.courseName;

      const badge = document.createElement('span');
      badge.className = 'badge ' + String(c.status).toLowerCase();
      badge.textContent = String(c.status).replace('_', ' ');

      header.appendChild(name);
      header.appendChild(badge);

      const track = document.createElement('div');
      track.className = 'progress-track';
      const fill = document.createElement('div');
      fill.className = 'progress-fill' + (c.status === 'COMPLETED' ? ' completed' : '');
      fill.style.width = Math.max(0, Math.min(100, c.progress || 0)) + '%';
      track.appendChild(fill);

      wrap.appendChild(header);
      wrap.appendChild(track);
      learningBody.appendChild(wrap);
    });
  }

  function renderPay(pay) {
    payBody.innerHTML =
      '<div class="stat-row">' +
      '<div class="stat"><span class="value">' + formatDate(pay.nextPayDate) + '</span><span class="label">Next pay date</span></div>' +
      '<div class="stat"><span class="value">' + pay.payFrequency + '</span><span class="label">Pay frequency</span></div>' +
      '</div>';
  }

  function renderPtoBalance(pto) {
    ptoBalanceBody.innerHTML =
      '<div class="stat-row">' +
      '<div class="stat"><span class="value">' + pto.balance + '</span><span class="label">Available (hrs)</span></div>' +
      '<div class="stat"><span class="value">' + pto.used + '</span><span class="label">Used (hrs)</span></div>' +
      '<div class="stat"><span class="value">' + pto.total + '</span><span class="label">Total (hrs)</span></div>' +
      '</div>';
  }

  async function loadPanel(bodyEl, fetcher, renderer) {
    setLoading(bodyEl);
    try {
      renderer(await fetcher());
    } catch (err) {
      setError(bodyEl, err);
    }
  }

  function loadDashboard() {
    const id = currentEmployeeId();
    loadPanel(goalsBody, () => api.getGoals(id), renderGoals);
    loadPanel(learningBody, () => api.getLearningStatus(id), renderLearning);
    loadPanel(payBody, () => api.getNextPayDate(id), renderPay);
    loadPanel(ptoBalanceBody, () => api.getPtoBalance(id), renderPtoBalance);
  }

  function showFeedback(kind, message) {
    ptoFeedback.className = 'feedback ' + kind;
    ptoFeedback.textContent = message;
  }

  ptoForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const startDate = document.getElementById('pto-start').value;
    const endDate = document.getElementById('pto-end').value;
    const hours = parseFloat(document.getElementById('pto-hours').value);

    if (!startDate || !endDate || isNaN(hours)) {
      showFeedback('error', 'Please fill in start date, end date and hours.');
      return;
    }

    ptoSubmit.disabled = true;
    try {
      const res = await api.schedulePto(currentEmployeeId(), { startDate, endDate, hours });
      showFeedback('success', res.message + ' (' + res.requestId + '): ' + res.hoursScheduled +
        ' hours from ' + res.startDate + ' to ' + res.endDate + '.');
      ptoForm.reset();
      loadPanel(ptoBalanceBody, () => api.getPtoBalance(currentEmployeeId()), renderPtoBalance);
    } catch (err) {
      showFeedback('error', err && err.message ? err.message : 'Failed to schedule PTO.');
    } finally {
      ptoSubmit.disabled = false;
    }
  });

  loadBtn.addEventListener('click', loadDashboard);
  employeeInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') loadDashboard();
  });

  loadDashboard();
})();
