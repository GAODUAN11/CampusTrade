import { apiRequest, applyI18n, hideNotice, onLanguageChanged, renderTopbar, showNotice, t } from "./app.js";

renderTopbar("");
applyI18n();

const notice = document.getElementById("registerNotice");
const form = document.getElementById("registerForm");

onLanguageChanged(() => {
  applyI18n();
});

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  hideNotice(notice);

  const payload = {
    username: document.getElementById("username").value.trim(),
    nickname: document.getElementById("nickname").value.trim(),
    password: document.getElementById("password").value.trim(),
    confirmPassword: document.getElementById("confirmPassword").value.trim(),
    phone: document.getElementById("phone").value.trim() || null,
    email: document.getElementById("email").value.trim() || null,
    school: document.getElementById("school").value.trim() || null,
    campus: document.getElementById("campus").value.trim() || null,
    agreeTerms: document.getElementById("agreeTerms").checked,
  };

  try {
    await apiRequest("/api/v1/auth/register", {
      method: "POST",
      body: payload,
    });
    showNotice(notice, t("register.notice.success"));
    setTimeout(() => {
      window.location.href = "/login.html";
    }, 800);
  } catch (error) {
    showNotice(notice, t("register.error.failed", { message: error.message }), true);
  }
});
