import {
  apiRequest,
  applyI18n,
  formatMoney,
  formatTime,
  gotoLogin,
  hideNotice,
  isLoggedIn,
  onLanguageChanged,
  renderTopbar,
  showNotice,
  t,
} from "./app.js";

renderTopbar("favorites");
applyI18n();

if (!isLoggedIn()) {
  gotoLogin();
}

const notice = document.getElementById("favoriteNotice");
const status = document.getElementById("favoriteStatus");
const grid = document.getElementById("favoriteGrid");
const pageInfo = document.getElementById("pageInfo");

const state = {
  pageNo: 1,
  pageSize: 10,
  totalPages: 1,
  records: [],
  total: 0,
};

document.getElementById("btnPrev").addEventListener("click", () => {
  if (state.pageNo > 1) {
    state.pageNo -= 1;
    loadFavorites();
  }
});

document.getElementById("btnNext").addEventListener("click", () => {
  if (state.pageNo < state.totalPages) {
    state.pageNo += 1;
    loadFavorites();
  }
});

onLanguageChanged(() => {
  applyI18n();
  renderItems(state.records || []);
  status.textContent = t("favorites.status.total", { total: state.total || 0 });
  pageInfo.textContent = t("favorites.pageInfo", { page: state.pageNo, total: state.totalPages });
});

async function removeFavorite(productId) {
  try {
    await apiRequest(`/api/v1/me/favorites/${productId}`, { method: "DELETE" }, { auth: true });
    showNotice(notice, t("favorites.notice.removed"));
    loadFavorites();
  } catch (error) {
    showNotice(notice, t("favorites.error.removeFailed", { message: error.message }), true);
  }
}

function renderItems(items) {
  if (!items.length) {
    grid.innerHTML = `<article class="panel">${t("favorites.empty")}</article>`;
    return;
  }

  grid.innerHTML = items
    .map((item) => {
      const favorite = item.favorite || {};
      const product = item.product || {};
      const seller = item.seller || {};
      return `
        <article class="card">
          ${product.coverImageUrl ? `<img class="cover" src="${product.coverImageUrl}" alt="cover" />` : `<div class="cover"></div>`}
          <h3>${product.title || favorite.productTitle || t("favorites.card.untitled")}</h3>
          <div class="meta">${t("favorites.card.price")}: ${formatMoney(product.price || favorite.productPrice)}</div>
          <div class="meta">${t("favorites.card.status")}: ${product.status || favorite.productStatus || "-"}</div>
          <div class="meta">${t("favorites.card.seller")}: ${seller.nickname || seller.userId || "-"}</div>
          <div class="meta">${t("favorites.card.favoritedAt")}: ${formatTime(favorite.createdAt)}</div>
          <div class="card-actions">
            <a class="btn btn-primary" href="/product-detail.html?productId=${favorite.productId || product.productId}">${t("favorites.card.viewDetail")}</a>
            <button class="btn btn-danger btn-remove" data-product-id="${favorite.productId || product.productId}">${t("favorites.card.remove")}</button>
          </div>
        </article>
      `;
    })
    .join("");

  grid.querySelectorAll(".btn-remove").forEach((btn) => {
    btn.addEventListener("click", () => {
      const productId = Number(btn.getAttribute("data-product-id"));
      if (productId > 0) {
        removeFavorite(productId);
      }
    });
  });
}

async function loadFavorites() {
  hideNotice(notice);
  status.textContent = t("favorites.status.loading");
  try {
    const page = await apiRequest(
      `/api/v1/me/favorites?pageNo=${state.pageNo}&pageSize=${state.pageSize}`,
      { method: "GET" },
      { auth: true }
    );
    state.totalPages = Math.max(page?.totalPages || 1, 1);
    state.total = page?.total || 0;
    state.records = page?.records || [];
    if (state.pageNo > state.totalPages) {
      state.pageNo = state.totalPages;
      return loadFavorites();
    }
    renderItems(state.records);
    status.textContent = t("favorites.status.total", { total: state.total });
    pageInfo.textContent = t("favorites.pageInfo", { page: state.pageNo, total: state.totalPages });
  } catch (error) {
    status.textContent = "";
    showNotice(notice, t("favorites.error.loadFailed", { message: error.message }), true);
    grid.innerHTML = `<article class="panel">${t("favorites.panel.loadFailed")}</article>`;
  }
}

loadFavorites();
