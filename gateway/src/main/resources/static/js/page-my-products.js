import {
  apiRequest,
  applyI18n,
  csvToArray,
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

renderTopbar("my-products");
applyI18n();

if (!isLoggedIn()) {
  gotoLogin();
}

const notice = document.getElementById("myProductNotice");
const statusBar = document.getElementById("myProductStatus");
const grid = document.getElementById("myProductGrid");
const pageInfo = document.getElementById("pageInfo");
const editForm = document.getElementById("editForm");

const state = {
  pageNo: 1,
  pageSize: 8,
  totalPages: 1,
  records: [],
  total: 0,
};

document.getElementById("btnPrev").addEventListener("click", () => {
  if (state.pageNo > 1) {
    state.pageNo -= 1;
    loadMyProducts();
  }
});

document.getElementById("btnNext").addEventListener("click", () => {
  if (state.pageNo < state.totalPages) {
    state.pageNo += 1;
    loadMyProducts();
  }
});

document.getElementById("btnClear").addEventListener("click", clearEditForm);

onLanguageChanged(() => {
  applyI18n();
  renderCards(state.records || []);
  statusBar.textContent = t("myProducts.status.total", { total: state.total || 0 });
  pageInfo.textContent = t("myProducts.pageInfo", { page: state.pageNo, total: state.totalPages });
});

editForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  hideNotice(notice);

  const productId = Number(document.getElementById("productId").value);
  if (!Number.isFinite(productId) || productId <= 0) {
    showNotice(notice, t("myProducts.error.pickEditFirst"), true);
    return;
  }

  const payload = {
    productId,
    title: readNullableText("title"),
    description: readNullableText("description"),
    price: readNullableNumber("price"),
    originalPrice: readNullableNumber("originalPrice"),
    category: readNullableText("category"),
    location: readNullableText("location"),
    negotiable: document.getElementById("negotiable").checked,
    stock: readNullableInt("stock"),
    imageUrls: csvToArray(document.getElementById("imageUrls").value),
    tags: csvToArray(document.getElementById("tags").value),
    status: readNullableText("status"),
  };

  try {
    await apiRequest(`/api/v1/me/products/${productId}`, { method: "PUT", body: payload }, { auth: true });
    showNotice(notice, t("myProducts.notice.updated"));
    await loadMyProducts();
  } catch (error) {
    showNotice(notice, t("myProducts.error.updateFailed", { message: error.message }), true);
  }
});

function readNullableText(id) {
  const value = document.getElementById(id).value.trim();
  return value ? value : null;
}

function readNullableNumber(id) {
  const value = document.getElementById(id).value.trim();
  if (!value) {
    return null;
  }
  const number = Number(value);
  return Number.isFinite(number) ? number : null;
}

function readNullableInt(id) {
  const value = document.getElementById(id).value.trim();
  if (!value) {
    return null;
  }
  const number = Number(value);
  return Number.isInteger(number) ? number : null;
}

function fillEditForm(detail) {
  const product = detail?.product || {};
  document.getElementById("productId").value = product.productId || "";
  document.getElementById("status").value = product.status || "";
  document.getElementById("title").value = product.title || "";
  document.getElementById("description").value = detail?.description || "";
  document.getElementById("price").value = product.price ?? "";
  document.getElementById("originalPrice").value = product.originalPrice ?? "";
  document.getElementById("category").value = product.category || "";
  document.getElementById("location").value = product.location || "";
  document.getElementById("stock").value = "";
  document.getElementById("negotiable").checked = product.negotiable === true;
  document.getElementById("imageUrls").value = Array.isArray(detail?.imageUrls) ? detail.imageUrls.join(", ") : "";
  document.getElementById("tags").value = Array.isArray(detail?.tags) ? detail.tags.join(", ") : "";
}

function clearEditForm() {
  editForm.reset();
  document.getElementById("productId").value = "";
}

async function loadProductDetailForEdit(productId) {
  hideNotice(notice);
  try {
    const detail = await apiRequest(`/api/v1/me/products/${productId}`, { method: "GET" }, { auth: true });
    fillEditForm(detail);
    showNotice(notice, t("myProducts.notice.loadedForEdit", { id: productId }));
    window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" });
  } catch (error) {
    showNotice(notice, t("myProducts.error.loadDetailFailed", { message: error.message }), true);
  }
}

async function changeShelf(productId, toOnShelf) {
  hideNotice(notice);
  const url = toOnShelf
    ? `/api/v1/me/products/${productId}/on-shelf`
    : `/api/v1/me/products/${productId}/off-shelf`;
  try {
    await apiRequest(url, { method: "PUT" }, { auth: true });
    showNotice(notice, toOnShelf ? t("myProducts.notice.onShelf") : t("myProducts.notice.offShelf"));
    await loadMyProducts();
  } catch (error) {
    showNotice(notice, t("myProducts.error.shelfFailed", { message: error.message }), true);
  }
}

async function deleteProduct(productId) {
  hideNotice(notice);
  const sure = window.confirm(t("myProducts.confirm.delete", { id: productId }));
  if (!sure) {
    return;
  }

  try {
    await apiRequest(`/api/v1/me/products/${productId}`, { method: "DELETE" }, { auth: true });
    showNotice(notice, t("myProducts.notice.deleted"));
    await loadMyProducts();
    const currentEditId = Number(document.getElementById("productId").value);
    if (currentEditId === productId) {
      clearEditForm();
    }
  } catch (error) {
    showNotice(notice, t("myProducts.error.deleteFailed", { message: error.message }), true);
  }
}

function renderCards(records) {
  if (!records.length) {
    grid.innerHTML = `<article class="panel">${t("myProducts.empty")}</article>`;
    return;
  }

  grid.innerHTML = records
    .map((item) => {
      return `
        <article class="card">
          ${item.coverImageUrl ? `<img class="cover" src="${item.coverImageUrl}" alt="cover" />` : `<div class="cover"></div>`}
          <h3>${item.title || t("myProducts.card.untitled")}</h3>
          <div class="meta">${t("myProducts.card.id")}: ${item.productId}</div>
          <div class="meta">${t("myProducts.card.status")}: ${item.status || "-"}</div>
          <div class="meta">${t("myProducts.card.category")}: ${item.category || "-"}</div>
          <div class="meta">${t("myProducts.card.created")}: ${formatTime(item.createdAt)}</div>
          <div class="price">${formatMoney(item.price)}</div>
          <div class="card-actions">
            <a class="btn btn-ghost" href="/product-detail.html?productId=${item.productId}">${t("myProducts.card.view")}</a>
            <button class="btn btn-primary btn-edit" data-id="${item.productId}">${t("myProducts.card.edit")}</button>
            <button class="btn btn-ghost btn-on" data-id="${item.productId}">${t("myProducts.card.onShelf")}</button>
            <button class="btn btn-danger btn-off" data-id="${item.productId}">${t("myProducts.card.offShelf")}</button>
            <button class="btn btn-danger btn-delete" data-id="${item.productId}">${t("myProducts.card.delete")}</button>
          </div>
        </article>
      `;
    })
    .join("");

  grid.querySelectorAll(".btn-edit").forEach((el) => {
    el.addEventListener("click", () => loadProductDetailForEdit(Number(el.getAttribute("data-id"))));
  });
  grid.querySelectorAll(".btn-on").forEach((el) => {
    el.addEventListener("click", () => changeShelf(Number(el.getAttribute("data-id")), true));
  });
  grid.querySelectorAll(".btn-off").forEach((el) => {
    el.addEventListener("click", () => changeShelf(Number(el.getAttribute("data-id")), false));
  });
  grid.querySelectorAll(".btn-delete").forEach((el) => {
    el.addEventListener("click", () => deleteProduct(Number(el.getAttribute("data-id"))));
  });
}

async function loadMyProducts() {
  hideNotice(notice);
  statusBar.textContent = t("myProducts.status.loading");
  try {
    const page = await apiRequest(
      `/api/v1/me/products/page?pageNo=${state.pageNo}&pageSize=${state.pageSize}`,
      { method: "GET" },
      { auth: true }
    );
    state.totalPages = Math.max(page?.totalPages || 1, 1);
    state.total = page?.total || 0;
    state.records = page?.records || [];
    if (state.pageNo > state.totalPages) {
      state.pageNo = state.totalPages;
      return loadMyProducts();
    }
    renderCards(state.records);
    statusBar.textContent = t("myProducts.status.total", { total: state.total });
    pageInfo.textContent = t("myProducts.pageInfo", { page: state.pageNo, total: state.totalPages });
  } catch (error) {
    statusBar.textContent = "";
    showNotice(notice, t("myProducts.error.loadFailed", { message: error.message }), true);
    grid.innerHTML = `<article class="panel">${t("myProducts.panel.loadFailed")}</article>`;
  }
}

loadMyProducts();
