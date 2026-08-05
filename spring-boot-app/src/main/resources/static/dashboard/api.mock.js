function nextPayDate() {
  const date = new Date();
  date.setDate(date.getDate() + 14);
  return date.toISOString().slice(0, 10);
}

const fixtures = {
  EMP001: {
    goals: ["Deliver the employee services migration with contract parity.", "Complete the platform security learning path.", "Document the next quarter delivery plan."],
    learning: { employeeId: "EMP001", courses: [{ courseName: "Spring Boot foundations", status: "COMPLETED", progress: 100 }, { courseName: "API design and observability", status: "IN_PROGRESS", progress: 68 }, { courseName: "Leadership essentials", status: "NOT_STARTED", progress: 0 }] },
    pay: { employeeId: "EMP001", nextPayDate: nextPayDate(), payFrequency: "Biweekly" },
    pto: { employeeId: "EMP001", balance: 56, used: 24, total: 80 }
  },
  EMP002: {
    goals: ["Launch the new customer onboarding experience.", "Improve quarterly reporting workflows.", "Mentor a new team member through their first release."],
    learning: { employeeId: "EMP002", courses: [{ courseName: "Customer experience strategy", status: "COMPLETED", progress: 100 }, { courseName: "Data storytelling", status: "IN_PROGRESS", progress: 42 }] },
    pay: { employeeId: "EMP002", nextPayDate: nextPayDate(), payFrequency: "Biweekly" },
    pto: { employeeId: "EMP002", balance: 32, used: 48, total: 80 }
  }
};

const wait = (value) => new Promise((resolve) => setTimeout(() => resolve(structuredClone(value)), 360));
function fixture(employeeId) {
  if (!fixtures[employeeId]) throw new Error(`Employee ${employeeId} was not found`);
  return fixtures[employeeId];
}
export async function getToken() { return wait("mock-token"); }
export async function getGoals(employeeId) { return wait(fixture(employeeId).goals); }
export async function getLearningStatus(employeeId) { return wait(fixture(employeeId).learning); }
export async function getNextPayDate(employeeId) { return wait(fixture(employeeId).pay); }
export async function getPtoBalance(employeeId) { return wait(fixture(employeeId).pto); }
export async function schedulePto(employeeId, payload) {
  const data = fixture(employeeId);
  if (payload.hours > data.pto.balance) {
    return Promise.reject(new Error(`Requested ${payload.hours} hours exceeds the available PTO balance of ${data.pto.balance} hours.`));
  }
  data.pto.balance -= Number(payload.hours);
  data.pto.used += Number(payload.hours);
  return wait({ message: "PTO request scheduled successfully.", requestId: `PTO-${Date.now()}`, ...payload, hoursScheduled: Number(payload.hours) });
}
