import { apiRequest, applyI18n, formatMoney, formatTime, onLanguageChanged, renderTopbar, t } from "./app.js";

renderTopbar("home");
applyI18n();

const state = {
  pageNum: 1,
  pageSize: 8,
  totalPages: 1,
  records: [],
  keyword: "",
};

const productGrid = document.getElementById("productGrid");
const listStatus = document.getElementById("listStatus");
const pageInfo = document.getElementById("pageInfo");
const keywordInput = document.getElementById("keywordInput");

document.getElementById("btnPrev").addEventListener("click", () => {
  if (state.pageNum > 1) {
    state.pageNum -= 1;
    loadPage();
  }
});

document.getElementById("btnNext").addEventListener("click", () => {
  if (state.pageNum < state.totalPages) {
    state.pageNum += 1;
    loadPage();
  }
});

document.getElementById("btnSearch").addEventListener("click", () => {
  state.keyword = (keywordInput.value || "").trim();
  state.pageNum = 1;
  loadPage();
});

document.getElementById("btnReset").addEventListener("click", () => {
  keywordInput.value = "";
  state.keyword = "";
  state.pageNum = 1;
  loadPage();
});

onLanguageChanged(() => {
  applyI18n();
  renderCards();
});

function cardTemplate(item) {
  const cover = item.coverImageUrl
    ? `<img class="cover" src="${item.coverImageUrl}" alt="cover" />`
    : `<div class="cover"></div>`;

  return `
    <article class="card">
      ${cover}
      <h3>${item.title || t("home.card.untitled")}</h3>
      <div class="meta">${item.category || "-"} | ${item.location || "-"}</div>
      <div class="meta">${t("home.card.statusLabel")}: ${item.status || "-"}</div>
      <div class="price">${formatMoney(item.price)}</div>
      <div class="meta">${t("home.card.createdLabel")}: ${formatTime(item.createdAt)}</div>
      <div class="card-actions">
        <a class="btn btn-primary" href="/product-detail.html?productId=${item.productId}">${t("home.card.viewDetail")}</a>
      </div>
    </article>
  `;
}

function renderCards() {
  if (!state.records.length) {
    productGrid.innerHTML = `<article class="panel">${t("home.empty")}</article>`;
  } else {
    productGrid.innerHTML = state.records.map(cardTemplate).join("");
  }

  listStatus.textContent = t("home.status.summary", {
    pages: state.totalPages,
    count: state.records.length,
  });
  pageInfo.textContent = t("home.pageInfo", { page: state.pageNum, total: state.totalPages });
}

async function loadPage() {
  listStatus.textContent = t("home.status.loading");
  try {
    const keywordQuery = state.keyword ? `&keyword=${encodeURIComponent(state.keyword)}` : "";
    const pageData = await apiRequest(
      `/api/v1/products/page?pageNum=${state.pageNum}&pageSize=${state.pageSize}${keywordQuery}`,
      { method: "GET" }
    );

    state.records = pageData?.records || [];
    state.totalPages = Math.max(pageData?.totalPages || 1, 1);

    if (state.pageNum > state.totalPages) {
      state.pageNum = state.totalPages;
      return loadPage();
    }
    renderCards();
  } catch (error) {
    listStatus.textContent = t("home.status.loadFailed", { message: error.message });
    productGrid.innerHTML = `<article class="panel">${t("home.panel.loadFailed")}</article>`;
  }
}

loadPage();
