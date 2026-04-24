import {
  apiRequest,
  applyI18n,
  formatMoney,
  formatTime,
  getCurrentUserId,
  gotoLogin,
  hideNotice,
  isLoggedIn,
  onLanguageChanged,
  query,
  renderTopbar,
  showNotice,
  t,
} from "./app.js";

renderTopbar("home");
applyI18n();

const notice = document.getElementById("detailNotice");
const state = {
  productId: Number(query("productId")),
  detail: null,
};

const dom = {
  title: document.getElementById("productTitle"),
  subline: document.getElementById("productSubline"),
  basicInfo: document.getElementById("basicInfo"),
  sellerInfo: document.getElementById("sellerInfo"),
  desc: document.getElementById("descriptionText"),
  gallery: document.getElementById("gallery"),
  tags: document.getElementById("tagList"),
  btnToggleFavorite: document.getElementById("btnToggleFavorite"),
  btnSendMessage: document.getElementById("btnSendMessage"),
  messageContent: document.getElementById("messageContent"),
};

if (!Number.isFinite(state.productId) || state.productId <= 0) {
  showNotice(notice, t("productDetail.error.invalidProductId"), true);
} else {
  loadDetail();
}

onLanguageChanged(() => {
  applyI18n();
  if (state.detail) {
    renderDetail(state.detail);
  }
});

dom.btnToggleFavorite.addEventListener("click", async () => {
  if (!isLoggedIn()) {
    gotoLogin();
    return;
  }
  if (!state.detail) {
    return;
  }

  const favorited = !!state.detail.favorited;
  try {
    if (favorited) {
      await apiRequest(`/api/v1/me/favorites/${state.productId}`, { method: "DELETE" }, { auth: true });
    } else {
      await apiRequest(`/api/v1/me/favorites/${state.productId}`, { method: "POST" }, { auth: true });
    }
    await loadDetail();
  } catch (error) {
    showNotice(notice, t("productDetail.error.favoriteAction", { message: error.message }), true);
  }
});

dom.btnSendMessage.addEventListener("click", async () => {
  if (!isLoggedIn()) {
    gotoLogin();
    return;
  }
  if (!state.detail?.seller?.userId) {
    showNotice(notice, t("productDetail.error.sellerUnavailable"), true);
    return;
  }

  const content = dom.messageContent.value.trim();
  if (!content) {
    showNotice(notice, t("productDetail.error.messageEmpty"), true);
    return;
  }

  try {
    await apiRequest(
      "/api/v1/me/messages/send",
      {
        method: "POST",
        body: {
          productId: state.productId,
          receiverId: state.detail.seller.userId,
          content,
          messageType: "TEXT",
        },
      },
      { auth: true }
    );
    dom.messageContent.value = "";
    showNotice(notice, t("productDetail.notice.messageSent"));
  } catch (error) {
    showNotice(notice, t("productDetail.error.sendFailed", { message: error.message }), true);
  }
});

function renderDetail(data) {
  state.detail = data;
  const product = data?.product || {};
  const seller = data?.seller || {};

  dom.title.textContent = product.title || t("productDetail.default.productTitle", { id: state.productId });
  dom.subline.textContent = `${product.category || "-"} | ${product.location || "-"} | ${formatMoney(product.price)}`;

  dom.basicInfo.innerHTML = `
    <div>${t("productDetail.basic.productId")}: ${product.productId || "-"}</div>
    <div>${t("productDetail.basic.sellerId")}: ${product.sellerId || "-"}</div>
    <div>${t("productDetail.basic.status")}: ${product.status || "-"}</div>
    <div>${t("productDetail.basic.price")}: ${formatMoney(product.price)}</div>
    <div>${t("productDetail.basic.originalPrice")}: ${formatMoney(product.originalPrice)}</div>
    <div>${t("productDetail.basic.negotiable")}: ${product.negotiable === true ? t("common.yes") : t("common.no")}</div>
    <div>${t("productDetail.basic.favoriteCount")}: ${product.favoriteCount ?? "-"}</div>
    <div>${t("productDetail.basic.createdAt")}: ${formatTime(product.createdAt)}</div>
  `;

  dom.sellerInfo.innerHTML = `
    <div>${t("productDetail.basic.sellerId")}: ${seller.userId || "-"}</div>
    <div>${t("productDetail.seller.nickname")}: ${seller.nickname || "-"}</div>
    <div>${t("productDetail.seller.school")}: ${seller.school || "-"}</div>
    <div>${t("productDetail.seller.campus")}: ${seller.campus || "-"}</div>
    <div>${t("productDetail.seller.credit")}: ${seller.creditScore ?? "-"}</div>
    <div>${t("productDetail.seller.soldCount")}: ${seller.soldCount ?? "-"}</div>
    <div>${t("productDetail.seller.status")}: ${seller.status || "-"}</div>
  `;

  dom.desc.textContent = data?.description || "-";

  const images = Array.isArray(data?.imageUrls) ? data.imageUrls : [];
  dom.gallery.innerHTML = images.length
    ? images.map((url) => `<img src="${url}" alt="product image" />`).join("")
    : `<div class="meta">${t("productDetail.gallery.none")}</div>`;

  const tags = Array.isArray(data?.tags) ? data.tags : [];
  dom.tags.innerHTML = tags.length
    ? tags.map((tag) => `<span class="chip">${tag}</span>`).join("")
    : `<span class="chip">${t("productDetail.tags.none")}</span>`;

  const favorited = !!data?.favorited;
  dom.btnToggleFavorite.textContent = favorited
    ? t("productDetail.favorite.remove")
    : t("productDetail.favorite.add");
  dom.btnToggleFavorite.className = favorited ? "btn btn-danger" : "btn btn-primary";
}

async function loadDetail() {
  hideNotice(notice);
  try {
    const userId = getCurrentUserId();
    const userQuery = userId ? `?userId=${encodeURIComponent(userId)}` : "";
    const data = await apiRequest(`/api/v1/products/${state.productId}/detail${userQuery}`, { method: "GET" });
    renderDetail(data);
  } catch (error) {
    showNotice(notice, t("productDetail.error.loadFailed", { message: error.message }), true);
  }
}
