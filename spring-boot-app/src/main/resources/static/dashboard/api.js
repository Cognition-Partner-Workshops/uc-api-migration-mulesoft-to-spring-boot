/**
 * Employee Services API client.
 *
 * All fetch/network logic for the dashboard lives in this module. Response
 * shapes match contracts/openapi.yaml:
 *   POST /oauth/token (application/x-www-form-urlencoded:
 *     grant_type=client_credentials, client_id, client_secret)
 *     -> TokenResponse { access_token, token_type: "Bearer", expires_in }
 *   The access_token is sent as "Authorization: Bearer <token>" on all
 *   /api/employee/* calls.
 */

const CLIENT_ID = 'demo-client';
const CLIENT_SECRET = 'demo-secret';

/** Error carrying an HTTP status and an ErrorResponse body. */
class ApiError extends Error {
  constructor(status, body) {
    super(body && body.message ? body.message : `Request failed with status ${status}`);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

let cachedToken = null;
let tokenExpiresAt = 0;

async function parseBody(response) {
  try {
    return await response.json();
  } catch (e) {
    return null;
  }
}

/**
 * POST /oauth/token -> TokenResponse
 */
async function getToken() {
  const response = await fetch('/oauth/token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      grant_type: 'client_credentials',
      client_id: CLIENT_ID,
      client_secret: CLIENT_SECRET,
    }),
  });
  const body = await parseBody(response);
  if (!response.ok) {
    throw new ApiError(response.status, body);
  }
  cachedToken = body.access_token;
  tokenExpiresAt = Date.now() + (body.expires_in - 60) * 1000;
  return body;
}

async function ensureToken() {
  if (!cachedToken || Date.now() >= tokenExpiresAt) {
    await getToken();
  }
  return cachedToken;
}

async function apiFetch(path, options = {}, retry = true) {
  const token = await ensureToken();
  const headers = Object.assign({ Authorization: 'Bearer ' + token }, options.headers || {});
  const response = await fetch(path, Object.assign({}, options, { headers }));
  if (response.status === 401 && retry) {
    cachedToken = null;
    return apiFetch(path, options, false);
  }
  const body = await parseBody(response);
  if (!response.ok) {
    throw new ApiError(response.status, body);
  }
  return body;
}

/**
 * GET /api/employee/{employeeId}/goals -> string[]
 */
function getGoals(employeeId) {
  return apiFetch(`/api/employee/${encodeURIComponent(employeeId)}/goals`);
}

/**
 * GET /api/employee/{employeeId}/learning-status -> LearningStatus
 */
function getLearningStatus(employeeId) {
  return apiFetch(`/api/employee/${encodeURIComponent(employeeId)}/learning-status`);
}

/**
 * GET /api/employee/{employeeId}/next-pay-date -> PayDateResponse
 */
function getNextPayDate(employeeId) {
  return apiFetch(`/api/employee/${encodeURIComponent(employeeId)}/next-pay-date`);
}

/**
 * GET /api/employee/{employeeId}/pto/balance -> PtoBalanceResponse
 */
function getPtoBalance(employeeId) {
  return apiFetch(`/api/employee/${encodeURIComponent(employeeId)}/pto/balance`);
}

/**
 * POST /api/employee/{employeeId}/pto/schedule
 * body: PtoScheduleRequest { startDate, endDate, hours } -> PtoScheduleResponse
 */
function schedulePto(employeeId, { startDate, endDate, hours }) {
  return apiFetch(`/api/employee/${encodeURIComponent(employeeId)}/pto/schedule`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ startDate, endDate, hours }),
  });
}

window.EmployeeApi = {
  getToken,
  getGoals,
  getLearningStatus,
  getNextPayDate,
  getPtoBalance,
  schedulePto,
  ApiError,
};
