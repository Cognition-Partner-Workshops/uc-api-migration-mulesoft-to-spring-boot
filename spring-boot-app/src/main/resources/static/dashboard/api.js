/**
 * Employee Services API client (MOCK implementation).
 *
 * All fetch/network logic for the dashboard lives in this module. Response
 * shapes match contracts/openapi.yaml exactly, so swapping mock -> real API
 * later only requires changing this file.
 *
 * Real API interface notes:
 *   POST /oauth/token (application/x-www-form-urlencoded:
 *     grant_type=client_credentials, client_id, client_secret)
 *     -> TokenResponse { access_token, token_type: "Bearer", expires_in }
 *   The access_token is then sent as "Authorization: Bearer <token>" on all
 *   /api/employee/* calls.
 */

const MOCK_LATENCY_MS = 300;

const MOCK_DB = {
  EMP001: {
    goals: [
      'Build an Agentforce Agent using the new MCP protocol for integrations.',
      'Complete the MuleSoft to Spring Boot migration for Employee Services.',
      'Mentor two junior engineers through their first production release.',
    ],
    learningStatus: {
      employeeId: 'EMP001',
      courses: [
        { courseName: 'Spring Boot 3 Fundamentals', status: 'COMPLETED', progress: 100 },
        { courseName: 'OAuth2 and API Security', status: 'IN_PROGRESS', progress: 65 },
        { courseName: 'PostgreSQL Performance Tuning', status: 'IN_PROGRESS', progress: 30 },
        { courseName: 'Kubernetes for Developers', status: 'NOT_STARTED', progress: 0 },
      ],
    },
    payDate: { employeeId: 'EMP001', nextPayDate: '2026-08-14', payFrequency: 'Bi-Weekly' },
    ptoBalance: { employeeId: 'EMP001', balance: 96.0, used: 24.0, total: 120.0 },
  },
  EMP002: {
    goals: [
      'Ship the contract verification harness for all migrated APIs.',
      'Reduce integration test flakiness below 1%.',
    ],
    learningStatus: {
      employeeId: 'EMP002',
      courses: [
        { courseName: 'Contract Testing with RestAssured', status: 'COMPLETED', progress: 100 },
        { courseName: 'Docker Compose Deep Dive', status: 'NOT_STARTED', progress: 0 },
      ],
    },
    payDate: { employeeId: 'EMP002', nextPayDate: '2026-08-31', payFrequency: 'Monthly' },
    ptoBalance: { employeeId: 'EMP002', balance: 40.0, used: 80.0, total: 120.0 },
  },
};

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/** Error carrying an HTTP-like status and an ErrorResponse body. */
class ApiError extends Error {
  constructor(status, body) {
    super(body && body.message ? body.message : `Request failed with status ${status}`);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

function errorResponse(status, message, errorCode) {
  return new ApiError(status, {
    message,
    errorCode,
    timestamp: new Date().toISOString(),
  });
}

/**
 * POST /oauth/token -> TokenResponse
 */
async function getToken() {
  await delay(MOCK_LATENCY_MS);
  return {
    access_token: 'mock-access-token-' + Math.random().toString(36).slice(2),
    token_type: 'Bearer',
    expires_in: 3600,
  };
}

/**
 * GET /api/employee/{employeeId}/goals -> string[]
 */
async function getGoals(employeeId) {
  await delay(MOCK_LATENCY_MS);
  const record = MOCK_DB[employeeId];
  if (!record) {
    throw errorResponse(404, `No goals found for employee ${employeeId}`, 'GOALS_NOT_FOUND');
  }
  return record.goals.slice();
}

/**
 * GET /api/employee/{employeeId}/learning-status -> LearningStatus
 */
async function getLearningStatus(employeeId) {
  await delay(MOCK_LATENCY_MS);
  const record = MOCK_DB[employeeId];
  if (!record) {
    throw errorResponse(404, `No learning records found for employee ${employeeId}`, 'LEARNING_NOT_FOUND');
  }
  return JSON.parse(JSON.stringify(record.learningStatus));
}

/**
 * GET /api/employee/{employeeId}/next-pay-date -> PayDateResponse
 */
async function getNextPayDate(employeeId) {
  await delay(MOCK_LATENCY_MS);
  const record = MOCK_DB[employeeId];
  if (!record) {
    throw errorResponse(404, `Employee ${employeeId} not found`, 'EMPLOYEE_NOT_FOUND');
  }
  return Object.assign({}, record.payDate);
}

/**
 * GET /api/employee/{employeeId}/pto/balance -> PtoBalanceResponse
 */
async function getPtoBalance(employeeId) {
  await delay(MOCK_LATENCY_MS);
  const record = MOCK_DB[employeeId];
  if (!record) {
    throw errorResponse(404, `Employee ${employeeId} not found`, 'EMPLOYEE_NOT_FOUND');
  }
  return Object.assign({}, record.ptoBalance);
}

/**
 * POST /api/employee/{employeeId}/pto/schedule
 * body: PtoScheduleRequest { startDate, endDate, hours } -> PtoScheduleResponse
 */
async function schedulePto(employeeId, { startDate, endDate, hours }) {
  await delay(MOCK_LATENCY_MS);
  const record = MOCK_DB[employeeId];
  if (!record) {
    throw errorResponse(404, `Employee ${employeeId} not found`, 'EMPLOYEE_NOT_FOUND');
  }
  if (!startDate || !endDate || !(hours > 0)) {
    throw errorResponse(400, 'Invalid request: startDate, endDate and positive hours are required', 'INVALID_REQUEST');
  }
  if (new Date(endDate) < new Date(startDate)) {
    throw errorResponse(400, 'Invalid request: endDate must be on or after startDate', 'INVALID_DATES');
  }
  if (hours > record.ptoBalance.balance) {
    throw errorResponse(400, `Insufficient PTO balance: requested ${hours}h, available ${record.ptoBalance.balance}h`, 'INSUFFICIENT_BALANCE');
  }
  record.ptoBalance.balance -= hours;
  record.ptoBalance.used += hours;
  return {
    message: 'PTO scheduled successfully',
    requestId: 'REQ-' + Math.random().toString(36).slice(2, 10).toUpperCase(),
    startDate,
    endDate,
    hoursScheduled: hours,
  };
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
