import {
  apiRequest,
  applyI18n,
  csvToArray,
  gotoLogin,
  hideNotice,
  isLoggedIn,
  onLanguageChanged,
  renderTopbar,
  showNotice,
  t,
} from "./app.js";

renderTopbar("publish");
applyI18n();

if (!isLoggedIn()) {
  gotoLogin();
}

const notice = document.getElementById("publishNotice");
const form = document.getElementById("publishForm");
const manageProductIdInput = document.getElementById("manageProductId");

onLanguageChanged(() => {
  applyI18n();
});

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  hideNotice(notice);

  const payload = {
    title: document.getElementById("title").value.trim(),
    description: document.getElementById("description").value.trim(),
    price: Number(document.getElementById("price").value),
    originalPrice: parseOptionalNumber(document.getElementById("originalPrice").value),
    category: document.getElementById("category").value.trim() || null,
    location: document.getElementById("location").value.trim() || null,
    negotiable: document.getElementById("negotiable").checked,
    stock: parseOptionalInteger(document.getElementById("stock").value, 1),
    imageUrls: csvToArray(document.getElementById("imageUrls").value),
    tags: csvToArray(document.getElementById("tags").value),
  };

  try {
    const product = await apiRequest("/api/v1/me/products", { method: "POST", body: payload }, { auth: true });
    const detailUrl = `/product-detail.html?productId=${product.productId}`;
    showNotice(
      notice,
      t("publish.notice.success", {
        id: product.productId,
        url: detailUrl,
      })
    );
    form.reset();
  } catch (error) {
    showNotice(notice, t("publish.error.failed", { message: error.message }), true);
  }
});

document.getElementById("btnOnShelf").addEventListener("click", () => changeShelf("on"));
document.getElementById("btnOffShelf").addEventListener("click", () => changeShelf("off"));

async function changeShelf(mode) {
  hideNotice(notice);
  const productId = Number(manageProductIdInput.value);
  if (!Number.isFinite(productId) || productId <= 0) {
    showNotice(notice, t("publish.error.invalidProductId"), true);
    return;
  }

  const url = mode === "on"
    ? `/api/v1/me/products/${productId}/on-shelf`
    : `/api/v1/me/products/${productId}/off-shelf`;

  try {
    await apiRequest(url, { method: "PUT" }, { auth: true });
    showNotice(notice, mode === "on" ? t("publish.notice.onShelfSuccess") : t("publish.notice.offShelfSuccess"));
  } catch (error) {
    showNotice(notice, t("publish.error.shelfFailed", { message: error.message }), true);
  }
}

function parseOptionalNumber(value) {
  if (!value || !value.trim()) {
    return null;
  }
  const n = Number(value);
  return Number.isFinite(n) ? n : null;
}

function parseOptionalInteger(value, fallback) {
  const n = Number(value);
  if (!Number.isInteger(n) || n <= 0) {
    return fallback;
  }
  return n;
}
