// ----- Calculator logic -----
// State
let currentValue = "0";
let previousValue = null;
let currentOperator = null;
let justEvaluated = false;

const displayEl = document.getElementById("display");
const historyEl = document.getElementById("history");

function updateScreen() {
  displayEl.textContent = currentValue;
  historyEl.textContent =
    previousValue !== null && currentOperator
      ? `${previousValue} ${currentOperator}`
      : "";
}

function inputNumber(num) {
  if (justEvaluated) {
    currentValue = num;
    justEvaluated = false;
  } else {
    currentValue = currentValue === "0" ? num : currentValue + num;
  }
  updateScreen();
}

function inputDecimal() {
  if (justEvaluated) {
    currentValue = "0.";
    justEvaluated = false;
    updateScreen();
    return;
  }
  if (!currentValue.includes(".")) {
    currentValue += ".";
    updateScreen();
  }
}

function chooseOperator(op) {
  if (currentOperator !== null && previousValue !== null && !justEvaluated) {
    evaluate();
  }
  previousValue = currentValue;
  currentOperator = op;
  justEvaluated = false;
  currentValue = "0";
  updateScreen();
  highlightOperator(op);
}

function highlightOperator(op) {
  document.querySelectorAll(".key-op").forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.op === op);
  });
}

function evaluate() {
  if (currentOperator === null || previousValue === null) return;

  const a = parseFloat(previousValue);
  const b = parseFloat(currentValue);
  let result;

  switch (currentOperator) {
    case "+":
      result = a + b;
      break;
    case "−":
      result = a - b;
      break;
    case "×":
      result = a * b;
      break;
    case "÷":
      result = b === 0 ? "Error" : a / b;
      break;
    default:
      return;
  }

  // Round off floating point noise, keep it readable
  if (typeof result === "number") {
    result = Math.round((result + Number.EPSILON) * 1e10) / 1e10;
  }

  currentValue = String(result);
  previousValue = null;
  currentOperator = null;
  justEvaluated = true;
  highlightOperator(null);
  updateScreen();
}

function clearAll() {
  currentValue = "0";
  previousValue = null;
  currentOperator = null;
  justEvaluated = false;
  highlightOperator(null);
  updateScreen();
}

function backspace() {
  if (justEvaluated) {
    clearAll();
    return;
  }
  currentValue =
    currentValue.length > 1 ? currentValue.slice(0, -1) : "0";
  updateScreen();
}

function percent() {
  currentValue =
