import {
  apiRequest,
  applyI18n,
  getCurrentUser,
  hideNotice,
  onLanguageChanged,
  query,
  renderTopbar,
  sanitizeReturnUrl,
  setLogin,
  showNotice,
  t,
} from "./app.js";

renderTopbar("");
applyI18n();

const notice = document.getElementById("loginNotice");
const form = document.getElementById("loginForm");

const currentUser = getCurrentUser();
if (currentUser) {
  showNotice(
    notice,
    t("login.notice.already", {
      name: currentUser.nickname || currentUser.username,
    })
  );
  setTimeout(() => {
    window.location.href = sanitizeReturnUrl(query("returnUrl"));
  }, 800);
}

onLanguageChanged(() => {
  applyI18n();
});

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  hideNotice(notice);

  const account = document.getElementById("account").value.trim();
  const password = document.getElementById("password").value.trim();
  const rememberMe = document.getElementById("rememberMe").checked;

  try {
    const loginData = await apiRequest("/api/v1/auth/login", {
      method: "POST",
      body: { account, password, rememberMe },
    });

    setLogin(loginData);
    showNotice(notice, t("login.notice.success"));
    setTimeout(() => {
      window.location.href = sanitizeReturnUrl(query("returnUrl"));
    }, 500);
  } catch (error) {
    showNotice(notice, t("login.error.failed", { message: error.message }), true);
  }
});
