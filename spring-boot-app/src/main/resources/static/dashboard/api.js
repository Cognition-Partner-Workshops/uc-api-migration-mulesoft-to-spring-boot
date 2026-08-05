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

async function realGetToken() { return fetchToken(); }

async function realApiRequest(path, options = {}, retried = false) {
  const token = await fetchToken();
  const response = await fetch(path, { ...options, headers: { ...(options.headers || {}), Authorization: `Bearer ${token}` } });
  if (response.status === 401 && !retried) {
    tokenCache = null;
    return realApiRequest(path, options, true);
  }
  return readResponse(response);
}

async function realGetGoals(employeeId) { return realApiRequest(`/api/employee/${encodeURIComponent(employeeId)}/goals`); }
async function realGetLearningStatus(employeeId) { return realApiRequest(`/api/employee/${encodeURIComponent(employeeId)}/learning-status`); }
async function realGetNextPayDate(employeeId) { return realApiRequest(`/api/employee/${encodeURIComponent(employeeId)}/next-pay-date`); }
async function realGetPtoBalance(employeeId) { return realApiRequest(`/api/employee/${encodeURIComponent(employeeId)}/pto/balance`); }
async function realSchedulePto(employeeId, payload) {
  return realApiRequest(`/api/employee/${encodeURIComponent(employeeId)}/pto/schedule`, {
    method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload)
  });
}

let getTokenImpl = realGetToken;
let getGoalsImpl = realGetGoals;
let getLearningStatusImpl = realGetLearningStatus;
let getNextPayDateImpl = realGetNextPayDate;
let getPtoBalanceImpl = realGetPtoBalance;
let schedulePtoImpl = realSchedulePto;

// MOCK-SWITCH-START
import * as mockApi from "./api.mock.js";
const USE_MOCK = true;
if (USE_MOCK) {
  getTokenImpl = mockApi.getToken;
  getGoalsImpl = mockApi.getGoals;
  getLearningStatusImpl = mockApi.getLearningStatus;
  getNextPayDateImpl = mockApi.getNextPayDate;
  getPtoBalanceImpl = mockApi.getPtoBalance;
  schedulePtoImpl = mockApi.schedulePto;
}
// MOCK-SWITCH-END

export async function getToken() { return getTokenImpl(); }
export async function getGoals(employeeId) { return getGoalsImpl(employeeId); }
export async function getLearningStatus(employeeId) { return getLearningStatusImpl(employeeId); }
export async function getNextPayDate(employeeId) { return getNextPayDateImpl(employeeId); }
export async function getPtoBalance(employeeId) { return getPtoBalanceImpl(employeeId); }
export async function schedulePto(employeeId, payload) { return schedulePtoImpl(employeeId, payload); }
