let tokenCache = null;

function apiError(response, body) {
  const message = body && typeof body.message === "string" ? body.message : `${response.status} ${response.statusText || "Request failed"}`;
  return new Error(message);
}

async function readResponse(response) {
  let body = null;
  try { body = await response.json(); } catch { /* Empty response bodies are valid for some HTTP failures. */ }
  if (!response.ok) throw apiError(response, body);
  return body;
}

async function fetchToken() {
  if (tokenCache) return tokenCache;
  const response = await fetch("/oauth/token", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({ grant_type: "client_credentials", client_id: "demo-client", client_secret: "demo-secret" })
  });
  const body = await readResponse(response);
  tokenCache = body.access_token;
  return tokenCache;
}

async function apiRequest(path, options = {}, retried = false) {
  const token = await fetchToken();
  const response = await fetch(path, { ...options, headers: { ...(options.headers || {}), Authorization: `Bearer ${token}` } });
  if (response.status === 401 && !retried) {
    tokenCache = null;
    return apiRequest(path, options, true);
  }
  return readResponse(response);
}

export async function getToken() { return fetchToken(); }
export async function getGoals(employeeId) { return apiRequest(`/api/employee/${encodeURIComponent(employeeId)}/goals`); }
export async function getLearningStatus(employeeId) { return apiRequest(`/api/employee/${encodeURIComponent(employeeId)}/learning-status`); }
export async function getNextPayDate(employeeId) { return apiRequest(`/api/employee/${encodeURIComponent(employeeId)}/next-pay-date`); }
export async function getPtoBalance(employeeId) { return apiRequest(`/api/employee/${encodeURIComponent(employeeId)}/pto/balance`); }
export async function schedulePto(employeeId, payload) {
  return apiRequest(`/api/employee/${encodeURIComponent(employeeId)}/pto/schedule`, {
    method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload)
  });
}
