const MONTHS_EN = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];
const MONTHS_ES = [
  "enero",
  "febrero",
  "marzo",
  "abril",
  "mayo",
  "junio",
  "julio",
  "agosto",
  "septiembre",
  "octubre",
  "noviembre",
  "diciembre",
];
const MONTHS_SHORT_EN = [
  "Jan",
  "Feb",
  "Mar",
  "Apr",
  "May",
  "Jun",
  "Jul",
  "Aug",
  "Sep",
  "Oct",
  "Nov",
  "Dec",
];
const MONTHS_SHORT_ES = [
  "ene",
  "feb",
  "mar",
  "abr",
  "may",
  "jun",
  "jul",
  "ago",
  "sep",
  "oct",
  "nov",
  "dic",
];
const DAYS_EN = [
  "Sunday",
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
];
const DAYS_ES = [
  "domingo",
  "lunes",
  "martes",
  "miércoles",
  "jueves",
  "viernes",
  "sábado",
];

// helper — extract date parts
function parts(dateInput) {
  const d = new Date(dateInput);
  return {
    d,
    day: String(d.getDate()).padStart(2, "0"),
    month: String(d.getMonth() + 1).padStart(2, "0"),
    year: d.getFullYear(),
    monthIndex: d.getMonth(),
    dayIndex: d.getDay(),
    hours: d.getHours(),
    minutes: d.getMinutes(),
  };
}

// Style 1 — short numeric: "28/03/2026"
export function formatDateShort(dateInput, dateFormat) {
  if (!dateInput) return "—";
  const { day, month, year } = parts(dateInput);
  switch (dateFormat) {
    case "MM/DD/YYYY":
      return `${month}/${day}/${year}`;
    case "YYYY-MM-DD":
      return `${year}-${month}-${day}`;
    case "DD MMM YYYY":
      return `${day}/${month}/${year}`; // fallback to default
    case "DD/MM/YYYY":
    default:
      return `${day}/${month}/${year}`;
  }
}

// Style 2 — medium abbreviated: "21 dic 2025" or "Dec 21 2025"
export function formatDateMedium(dateInput, dateFormat, uiLanguage) {
  if (!dateInput) return "—";
  const { day, year, monthIndex } = parts(dateInput);
  const isEs = uiLanguage === "es";
  const monthShort = isEs
    ? MONTHS_SHORT_ES[monthIndex]
    : MONTHS_SHORT_EN[monthIndex];

  // order depends on date format preference
  if (dateFormat === "MM/DD/YYYY") {
    return `${monthShort} ${day} ${year}`;
  }
  return `${day} ${monthShort} ${year}`;
}

// Style 3 — long full: "viernes, 21 de diciembre de 2025"
export function formatDateLong(dateInput, uiLanguage) {
  if (!dateInput) return "—";
  const { day, year, monthIndex, dayIndex } = parts(dateInput);
  const isEs = uiLanguage === "es";

  if (isEs) {
    const dayName = DAYS_ES[dayIndex];
    const monthName = MONTHS_ES[monthIndex];
    return `${dayName}, ${parseInt(day)} de ${monthName} de ${year}`;
  } else {
    const dayName = DAYS_EN[dayIndex];
    const monthName = MONTHS_EN[monthIndex];
    return `${dayName}, ${monthName} ${parseInt(day)} ${year}`;
  }
}

// Special — day + month only, no year: "30 de marzo" or "March 30"
export function formatDayMonth(dateInput, uiLanguage) {
  if (!dateInput) return "—";
  const { day, monthIndex } = parts(dateInput);
  const isEs = uiLanguage === "es";

  if (isEs) {
    return `${parseInt(day)} de ${MONTHS_ES[monthIndex]}`;
  } else {
    return `${MONTHS_EN[monthIndex]} ${parseInt(day)}`;
  }
}

// Special — month + year only: "Febrero 2026" or "February 2026"
export function formatMonthYear(dateInput, uiLanguage) {
  if (!dateInput) return "—";
  const { year, monthIndex } = parts(dateInput);
  const isEs = uiLanguage === "es";
  const monthName = isEs
    ? MONTHS_ES[monthIndex].charAt(0).toUpperCase() +
      MONTHS_ES[monthIndex].slice(1)
    : MONTHS_EN[monthIndex];
  return `${monthName} ${year}`;
}

// Time only: "13:10" or "1:10 PM"
export function formatTime(dateInput, timeFormat) {
  if (!dateInput) return "—";
  const { hours, minutes } = parts(dateInput);
  const mins = String(minutes).padStart(2, "0");

  if (timeFormat === "12h") {
    const period = hours >= 12 ? "PM" : "AM";
    const h = hours % 12 || 12;
    return `${h}:${mins} ${period}`;
  }
  return `${String(hours).padStart(2, "0")}:${mins}`;
}

// Timestamp for created/updated: "28/03/2026, 13:10"
export function formatTimestamp(dateInput, dateFormat, timeFormat) {
  if (!dateInput) return "—";
  return `${formatDateShort(dateInput, dateFormat)}, ${formatTime(dateInput, timeFormat)}`;
}
