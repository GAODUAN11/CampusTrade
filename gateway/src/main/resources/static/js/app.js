const AUTH_KEY = "campustrade.auth";
const LANG_KEY = "campustrade.lang";
const LANG_CHANGED_EVENT = "campustrade:lang-changed";
const SUCCESS_CODE = 0;

const SUPPORTED_LANGUAGES = ["zh-CN", "en"];

const I18N = {
  "zh-CN": {
    "common.prev": "上一页",
    "common.next": "下一页",
    "common.pageInfoDefault": "第 1 / 1 页",
    "common.optional": "可选",
    "common.yes": "是",
    "common.no": "否",
    "common.unknown": "未知",
    "common.user": "用户",
    "common.loading": "加载中...",
    "common.requestFailed": "请求失败",

    "topbar.brand": "CampusTrade",
    "topbar.nav.marketplace": "市场",
    "topbar.nav.publish": "发布",
    "topbar.nav.myProducts": "我的商品",
    "topbar.nav.favorites": "我的收藏",
    "topbar.nav.messages": "我的消息",
    "topbar.login": "登录",
    "topbar.register": "注册",
    "topbar.logout": "退出登录",
    "topbar.language": "语言",
    "topbar.language.zhCN": "简体中文",
    "topbar.language.en": "English",

    "home.pageTitle": "CampusTrade - 校园市场",
    "home.hero.title": "校园闲置市场",
    "home.hero.desc": "浏览同学发布的二手商品，支持详情聚合和站内消息沟通。",
    "home.keyword.label": "关键词筛选",
    "home.keyword.placeholder": "输入标题/分类/地点关键词",
    "home.search": "搜索",
    "home.reset": "重置",
    "home.card.untitled": "未命名商品",
    "home.card.statusLabel": "状态",
    "home.card.createdLabel": "创建时间",
    "home.card.viewDetail": "查看详情",
    "home.empty": "当前页暂无商品。",
    "home.status.summary": "共 {pages} 页，本页显示 {count} 条。",
    "home.pageInfo": "第 {page} / {total} 页",
    "home.status.loading": "正在加载商品列表...",
    "home.status.loadFailed": "加载失败：{message}",
    "home.panel.loadFailed": "商品加载失败。",

    "productDetail.pageTitle": "CampusTrade - 商品详情",
    "productDetail.hero.title": "商品详情",
    "productDetail.hero.loading": "加载中...",
    "productDetail.section.basicInfo": "基础信息",
    "productDetail.section.seller": "卖家信息",
    "productDetail.section.description": "商品描述",
    "productDetail.section.gallery": "图片集",
    "productDetail.section.tags": "标签",
    "productDetail.section.sendMessage": "给卖家发消息",
    "productDetail.sendHint": "登录后可在该页面直接发起会话。",
    "productDetail.message.label": "消息内容",
    "productDetail.message.placeholder": "可询问价格、成色、交易地点等",
    "productDetail.message.send": "发送消息",
    "productDetail.favorite.default": "收藏",
    "productDetail.favorite.add": "加入收藏",
    "productDetail.favorite.remove": "取消收藏",
    "productDetail.openMessages": "打开我的消息",
    "productDetail.error.invalidProductId": "productId 无效，请从商品列表进入。",
    "productDetail.error.favoriteAction": "收藏操作失败：{message}",
    "productDetail.error.sellerUnavailable": "当前无法获取卖家信息。",
    "productDetail.error.messageEmpty": "消息内容不能为空。",
    "productDetail.notice.messageSent": "消息已发送。",
    "productDetail.error.sendFailed": "发送失败：{message}",
    "productDetail.error.loadFailed": "加载详情失败：{message}",
    "productDetail.default.productTitle": "商品 #{id}",
    "productDetail.basic.productId": "商品ID",
    "productDetail.basic.sellerId": "卖家ID",
    "productDetail.basic.status": "状态",
    "productDetail.basic.price": "价格",
    "productDetail.basic.originalPrice": "原价",
    "productDetail.basic.negotiable": "可议价",
    "productDetail.basic.favoriteCount": "收藏数",
    "productDetail.basic.createdAt": "发布时间",
    "productDetail.seller.nickname": "昵称",
    "productDetail.seller.school": "学校",
    "productDetail.seller.campus": "校区",
    "productDetail.seller.credit": "信用分",
    "productDetail.seller.soldCount": "成交数",
    "productDetail.seller.status": "状态",
    "productDetail.gallery.none": "暂无图片。",
    "productDetail.tags.none": "暂无标签",

    "favorites.pageTitle": "CampusTrade - 我的收藏",
    "favorites.hero.title": "我的收藏",
    "favorites.hero.desc": "查看由网关聚合后的收藏商品。",
    "favorites.notice.removed": "已取消收藏。",
    "favorites.error.removeFailed": "取消收藏失败：{message}",
    "favorites.empty": "还没有收藏商品。",
    "favorites.card.untitled": "未命名商品",
    "favorites.card.price": "价格",
    "favorites.card.status": "状态",
    "favorites.card.seller": "卖家",
    "favorites.card.favoritedAt": "收藏时间",
    "favorites.card.viewDetail": "查看详情",
    "favorites.card.remove": "取消收藏",
    "favorites.status.loading": "正在加载收藏...",
    "favorites.status.total": "收藏总数：{total}",
    "favorites.pageInfo": "第 {page} / {total} 页",
    "favorites.error.loadFailed": "加载收藏失败：{message}",
    "favorites.panel.loadFailed": "收藏列表加载失败。",

    "login.pageTitle": "CampusTrade - 登录",
    "login.title": "登录",
    "login.desc": "账号可使用用户名/手机号/邮箱。",
    "login.form.account": "账号",
    "login.form.password": "密码",
    "login.form.rememberMe": "记住我",
    "login.form.submit": "登录",
    "login.form.goRegister": "去注册",
    "login.notice.already": "你已登录为 {name}，正在跳转...",
    "login.notice.success": "登录成功，正在跳转...",
    "login.error.failed": "登录失败：{message}",

    "register.pageTitle": "CampusTrade - 注册",
    "register.title": "注册",
    "register.desc": "创建你的校园二手交易账号。",
    "register.form.username": "用户名",
    "register.form.nickname": "昵称",
    "register.form.password": "密码",
    "register.form.confirmPassword": "确认密码",
    "register.form.phone": "手机号",
    "register.form.email": "邮箱",
    "register.form.school": "学校",
    "register.form.campus": "校区",
    "register.form.agreeTerms": "我同意平台条款",
    "register.form.submit": "创建账号",
    "register.form.goLogin": "去登录",
    "register.notice.success": "注册成功，正在跳转到登录页...",
    "register.error.failed": "注册失败：{message}",

    "publish.pageTitle": "CampusTrade - 发布商品",
    "publish.hero.title": "发布商品",
    "publish.hero.desc": "通过网关接口创建和管理商品。",
    "publish.section.create": "创建商品",
    "publish.form.title": "标题",
    "publish.form.price": "价格",
    "publish.form.originalPrice": "原价",
    "publish.form.stock": "库存",
    "publish.form.category": "分类",
    "publish.form.location": "地点",
    "publish.form.description": "描述",
    "publish.form.imageUrls": "图片链接（逗号分隔）",
    "publish.form.imageUrls.placeholder": "https://... , https://...",
    "publish.form.tags": "标签（逗号分隔）",
    "publish.form.tags.placeholder": "书籍, Java, 自行车",
    "publish.form.negotiable": "可议价",
    "publish.form.submit": "发布",
    "publish.section.quickShelf": "快速上下架",
    "publish.quick.productId": "商品ID",
    "publish.quick.onShelf": "上架",
    "publish.quick.offShelf": "下架",
    "publish.notice.success": "发布成功。商品ID：{id}。可访问：{url}",
    "publish.error.failed": "发布失败：{message}",
    "publish.error.invalidProductId": "请先输入有效的商品ID。",
    "publish.notice.onShelfSuccess": "上架成功。",
    "publish.notice.offShelfSuccess": "下架成功。",
    "publish.error.shelfFailed": "上下架操作失败：{message}",

    "messages.pageTitle": "CampusTrade - 我的消息",
    "messages.hero.title": "我的消息",
    "messages.hero.desc": "网关聚合会话列表与消息详情。",
    "messages.section.conversations": "会话列表",
    "messages.section.thread": "会话详情",
    "messages.reply.label": "回复内容",
    "messages.reply.placeholder": "输入消息内容",
    "messages.reply.send": "发送回复",
    "messages.empty.conversations": "暂无会话。",
    "messages.empty.thread": "当前会话暂无消息。",
    "messages.card.unknownProduct": "未知商品",
    "messages.card.user": "用户 {id}",
    "messages.card.unreadAndTime": "未读：{unread} | {time}",
    "messages.thread.title": "会话 #{id}",
    "messages.thread.fromTo": "来自 {from} -> {to} | {time}",
    "messages.error.loadMessages": "加载消息失败：{message}",
    "messages.panel.loadThreadFailed": "会话详情加载失败。",
    "messages.error.selectConversation": "请先选择一个会话。",
    "messages.error.emptyReply": "回复内容不能为空。",
    "messages.error.noReceiver": "无法确定该会话的接收方。",
    "messages.notice.replySent": "回复已发送。",
    "messages.error.replyFailed": "发送回复失败：{message}",
    "messages.inbox.unread": "未读：{count}",
    "messages.error.loadInbox": "加载收件箱失败：{message}",

    "myProducts.pageTitle": "CampusTrade - 我的商品",
    "myProducts.hero.title": "我发布的商品",
    "myProducts.hero.desc": "管理商品、编辑详情、切换上下架状态。",
    "myProducts.edit.title": "编辑商品",
    "myProducts.edit.desc": "点击商品卡片中的“编辑”可将当前详情载入表单。",
    "myProducts.form.productId": "商品ID",
    "myProducts.form.status": "状态",
    "myProducts.status.noChange": "不修改",
    "myProducts.form.title": "标题",
    "myProducts.form.price": "价格",
    "myProducts.form.originalPrice": "原价",
    "myProducts.form.stock": "库存",
    "myProducts.form.category": "分类",
    "myProducts.form.location": "地点",
    "myProducts.form.description": "描述",
    "myProducts.form.imageUrls": "图片链接（逗号分隔）",
    "myProducts.form.tags": "标签（逗号分隔）",
    "myProducts.form.negotiable": "可议价",
    "myProducts.form.save": "保存修改",
    "myProducts.form.clear": "清空",
    "myProducts.error.pickEditFirst": "请先点击“编辑”选择商品。",
    "myProducts.notice.updated": "商品已更新。",
    "myProducts.error.updateFailed": "更新失败：{message}",
    "myProducts.notice.loadedForEdit": "已加载商品 {id} 到编辑表单。",
    "myProducts.error.loadDetailFailed": "加载商品详情失败：{message}",
    "myProducts.notice.onShelf": "商品已上架。",
    "myProducts.notice.offShelf": "商品已下架。",
    "myProducts.error.shelfFailed": "上下架操作失败：{message}",
    "myProducts.confirm.delete": "删除商品 #{id}？这是软删除，商品会被隐藏。",
    "myProducts.notice.deleted": "商品已删除。",
    "myProducts.error.deleteFailed": "删除失败：{message}",
    "myProducts.empty": "还没有发布商品。请先去发布页创建商品。",
    "myProducts.card.untitled": "未命名商品",
    "myProducts.card.id": "ID",
    "myProducts.card.status": "状态",
    "myProducts.card.category": "分类",
    "myProducts.card.created": "创建时间",
    "myProducts.card.view": "查看",
    "myProducts.card.edit": "编辑",
    "myProducts.card.onShelf": "上架",
    "myProducts.card.offShelf": "下架",
    "myProducts.card.delete": "删除",
    "myProducts.status.loading": "正在加载我的商品...",
    "myProducts.status.total": "我的商品总数：{total}",
    "myProducts.pageInfo": "第 {page} / {total} 页",
    "myProducts.error.loadFailed": "加载我的商品失败：{message}",
    "myProducts.panel.loadFailed": "我的商品加载失败。",
  },
  en: {
    "common.prev": "Prev",
    "common.next": "Next",
    "common.pageInfoDefault": "Page 1 / 1",
    "common.optional": "Optional",
    "common.yes": "Yes",
    "common.no": "No",
    "common.unknown": "Unknown",
    "common.user": "User",
    "common.loading": "Loading...",
    "common.requestFailed": "Request failed",

    "topbar.brand": "CampusTrade",
    "topbar.nav.marketplace": "Marketplace",
    "topbar.nav.publish": "Publish",
    "topbar.nav.myProducts": "My Products",
    "topbar.nav.favorites": "Favorites",
    "topbar.nav.messages": "Messages",
    "topbar.login": "Login",
    "topbar.register": "Register",
    "topbar.logout": "Logout",
    "topbar.language": "Language",
    "topbar.language.zhCN": "简体中文",
    "topbar.language.en": "English",

    "home.pageTitle": "CampusTrade - Marketplace",
    "home.hero.title": "Campus Marketplace",
    "home.hero.desc": "Browse second-hand items from students, with detail aggregation and direct messaging.",
    "home.keyword.label": "Keyword Filter",
    "home.keyword.placeholder": "Type title/category/location keywords",
    "home.search": "Search",
    "home.reset": "Reset",
    "home.card.untitled": "Untitled Product",
    "home.card.statusLabel": "Status",
    "home.card.createdLabel": "Created",
    "home.card.viewDetail": "View Detail",
    "home.empty": "No products found in current page.",
    "home.status.summary": "Total {pages} pages, {count} item(s) displayed on this page.",
    "home.pageInfo": "Page {page} / {total}",
    "home.status.loading": "Loading product list...",
    "home.status.loadFailed": "Load failed: {message}",
    "home.panel.loadFailed": "Failed to load products.",

    "productDetail.pageTitle": "CampusTrade - Product Detail",
    "productDetail.hero.title": "Product Detail",
    "productDetail.hero.loading": "Loading...",
    "productDetail.section.basicInfo": "Basic Info",
    "productDetail.section.seller": "Seller",
    "productDetail.section.description": "Description",
    "productDetail.section.gallery": "Gallery",
    "productDetail.section.tags": "Tags",
    "productDetail.section.sendMessage": "Send Message To Seller",
    "productDetail.sendHint": "If logged in, you can start a conversation from this product page.",
    "productDetail.message.label": "Message",
    "productDetail.message.placeholder": "Ask about price, condition, and pickup details",
    "productDetail.message.send": "Send Message",
    "productDetail.favorite.default": "Favorite",
    "productDetail.favorite.add": "Add Favorite",
    "productDetail.favorite.remove": "Remove Favorite",
    "productDetail.openMessages": "Open My Messages",
    "productDetail.error.invalidProductId": "Invalid productId. Please open detail from marketplace list.",
    "productDetail.error.favoriteAction": "Favorite action failed: {message}",
    "productDetail.error.sellerUnavailable": "Seller info is unavailable.",
    "productDetail.error.messageEmpty": "Message cannot be empty.",
    "productDetail.notice.messageSent": "Message sent.",
    "productDetail.error.sendFailed": "Send message failed: {message}",
    "productDetail.error.loadFailed": "Load detail failed: {message}",
    "productDetail.default.productTitle": "Product #{id}",
    "productDetail.basic.productId": "Product ID",
    "productDetail.basic.sellerId": "Seller ID",
    "productDetail.basic.status": "Status",
    "productDetail.basic.price": "Price",
    "productDetail.basic.originalPrice": "Original",
    "productDetail.basic.negotiable": "Negotiable",
    "productDetail.basic.favoriteCount": "Favorite Count",
    "productDetail.basic.createdAt": "Created",
    "productDetail.seller.nickname": "Nickname",
    "productDetail.seller.school": "School",
    "productDetail.seller.campus": "Campus",
    "productDetail.seller.credit": "Credit",
    "productDetail.seller.soldCount": "Sold Count",
    "productDetail.seller.status": "Status",
    "productDetail.gallery.none": "No images.",
    "productDetail.tags.none": "No tags",

    "favorites.pageTitle": "CampusTrade - Favorites",
    "favorites.hero.title": "My Favorites",
    "favorites.hero.desc": "View all favorite products aggregated by gateway.",
    "favorites.notice.removed": "Favorite removed.",
    "favorites.error.removeFailed": "Remove favorite failed: {message}",
    "favorites.empty": "No favorites yet.",
    "favorites.card.untitled": "Untitled Product",
    "favorites.card.price": "Price",
    "favorites.card.status": "Status",
    "favorites.card.seller": "Seller",
    "favorites.card.favoritedAt": "Favorited at",
    "favorites.card.viewDetail": "View Detail",
    "favorites.card.remove": "Remove",
    "favorites.status.loading": "Loading favorites...",
    "favorites.status.total": "Total: {total}",
    "favorites.pageInfo": "Page {page} / {total}",
    "favorites.error.loadFailed": "Load favorites failed: {message}",
    "favorites.panel.loadFailed": "Failed to load favorites.",

    "login.pageTitle": "CampusTrade - Login",
    "login.title": "Login",
    "login.desc": "Use username/phone/email as account.",
    "login.form.account": "Account",
    "login.form.password": "Password",
    "login.form.rememberMe": "Remember me",
    "login.form.submit": "Login",
    "login.form.goRegister": "Go Register",
    "login.notice.already": "Already logged in as {name}. Redirecting...",
    "login.notice.success": "Login successful, redirecting...",
    "login.error.failed": "Login failed: {message}",

    "register.pageTitle": "CampusTrade - Register",
    "register.title": "Register",
    "register.desc": "Create your campus trading account.",
    "register.form.username": "Username",
    "register.form.nickname": "Nickname",
    "register.form.password": "Password",
    "register.form.confirmPassword": "Confirm Password",
    "register.form.phone": "Phone",
    "register.form.email": "Email",
    "register.form.school": "School",
    "register.form.campus": "Campus",
    "register.form.agreeTerms": "I agree to terms",
    "register.form.submit": "Create Account",
    "register.form.goLogin": "Go Login",
    "register.notice.success": "Register successful, redirecting to login...",
    "register.error.failed": "Register failed: {message}",

    "publish.pageTitle": "CampusTrade - Publish Product",
    "publish.hero.title": "Publish Product",
    "publish.hero.desc": "Create and manage your product through gateway APIs.",
    "publish.section.create": "Create Product",
    "publish.form.title": "Title",
    "publish.form.price": "Price",
    "publish.form.originalPrice": "Original Price",
    "publish.form.stock": "Stock",
    "publish.form.category": "Category",
    "publish.form.location": "Location",
    "publish.form.description": "Description",
    "publish.form.imageUrls": "Image URLs (comma separated)",
    "publish.form.imageUrls.placeholder": "https://... , https://...",
    "publish.form.tags": "Tags (comma separated)",
    "publish.form.tags.placeholder": "book, java, bike",
    "publish.form.negotiable": "Negotiable",
    "publish.form.submit": "Publish",
    "publish.section.quickShelf": "Quick Shelf Control",
    "publish.quick.productId": "Product ID",
    "publish.quick.onShelf": "On Shelf",
    "publish.quick.offShelf": "Off Shelf",
    "publish.notice.success": "Publish successful. Product ID: {id}. Open detail: {url}",
    "publish.error.failed": "Publish failed: {message}",
    "publish.error.invalidProductId": "Please input a valid product ID first.",
    "publish.notice.onShelfSuccess": "On shelf operation succeeded.",
    "publish.notice.offShelfSuccess": "Off shelf operation succeeded.",
    "publish.error.shelfFailed": "Shelf operation failed: {message}",

    "messages.pageTitle": "CampusTrade - Messages",
    "messages.hero.title": "My Messages",
    "messages.hero.desc": "Conversation list and message details aggregated by gateway.",
    "messages.section.conversations": "Conversations",
    "messages.section.thread": "Thread",
    "messages.reply.label": "Reply",
    "messages.reply.placeholder": "Type your message",
    "messages.reply.send": "Send Reply",
    "messages.empty.conversations": "No conversations.",
    "messages.empty.thread": "No messages in this conversation.",
    "messages.card.unknownProduct": "Unknown Product",
    "messages.card.user": "User {id}",
    "messages.card.unreadAndTime": "Unread: {unread} | {time}",
    "messages.thread.title": "Thread #{id}",
    "messages.thread.fromTo": "From {from} -> {to} | {time}",
    "messages.error.loadMessages": "Load messages failed: {message}",
    "messages.panel.loadThreadFailed": "Failed to load thread.",
    "messages.error.selectConversation": "Please select a conversation first.",
    "messages.error.emptyReply": "Reply content cannot be empty.",
    "messages.error.noReceiver": "Cannot resolve receiver for this conversation.",
    "messages.notice.replySent": "Reply sent.",
    "messages.error.replyFailed": "Send reply failed: {message}",
    "messages.inbox.unread": "Unread: {count}",
    "messages.error.loadInbox": "Load inbox failed: {message}",

    "myProducts.pageTitle": "CampusTrade - My Products",
    "myProducts.hero.title": "My Published Products",
    "myProducts.hero.desc": "Manage your products, edit details, and switch shelf status.",
    "myProducts.edit.title": "Edit Product",
    "myProducts.edit.desc": "Click edit on any item to load its current detail into this form.",
    "myProducts.form.productId": "Product ID",
    "myProducts.form.status": "Status",
    "myProducts.status.noChange": "No Change",
    "myProducts.form.title": "Title",
    "myProducts.form.price": "Price",
    "myProducts.form.originalPrice": "Original Price",
    "myProducts.form.stock": "Stock",
    "myProducts.form.category": "Category",
    "myProducts.form.location": "Location",
    "myProducts.form.description": "Description",
    "myProducts.form.imageUrls": "Image URLs (comma separated)",
    "myProducts.form.tags": "Tags (comma separated)",
    "myProducts.form.negotiable": "Negotiable",
    "myProducts.form.save": "Save Update",
    "myProducts.form.clear": "Clear",
    "myProducts.error.pickEditFirst": "Please pick a product by clicking Edit first.",
    "myProducts.notice.updated": "Product updated.",
    "myProducts.error.updateFailed": "Update failed: {message}",
    "myProducts.notice.loadedForEdit": "Loaded product {id} into edit form.",
    "myProducts.error.loadDetailFailed": "Load product detail failed: {message}",
    "myProducts.notice.onShelf": "Product is on shelf now.",
    "myProducts.notice.offShelf": "Product is off shelf now.",
    "myProducts.error.shelfFailed": "Shelf operation failed: {message}",
    "myProducts.confirm.delete": "Delete product #{id}? This is a soft delete and the item will be hidden.",
    "myProducts.notice.deleted": "Product deleted.",
    "myProducts.error.deleteFailed": "Delete failed: {message}",
    "myProducts.empty": "No published products yet. Go to publish first.",
    "myProducts.card.untitled": "Untitled Product",
    "myProducts.card.id": "ID",
    "myProducts.card.status": "Status",
    "myProducts.card.category": "Category",
    "myProducts.card.created": "Created",
    "myProducts.card.view": "View",
    "myProducts.card.edit": "Edit",
    "myProducts.card.onShelf": "On Shelf",
    "myProducts.card.offShelf": "Off Shelf",
    "myProducts.card.delete": "Delete",
    "myProducts.status.loading": "Loading my products...",
    "myProducts.status.total": "Total my products: {total}",
    "myProducts.pageInfo": "Page {page} / {total}",
    "myProducts.error.loadFailed": "Load my products failed: {message}",
    "myProducts.panel.loadFailed": "Failed to load my products.",
  },
};

function normalizeLanguage(raw) {
  if (!raw) {
    return null;
  }
  const value = String(raw).trim().toLowerCase();
  if (value.startsWith("zh")) {
    return "zh-CN";
  }
  if (value.startsWith("en")) {
    return "en";
  }
  return null;
}

function detectDefaultLanguage() {
  const fromBrowser = normalizeLanguage(window.navigator?.language || "en");
  return fromBrowser || "en";
}

function readLanguage() {
  try {
    const stored = normalizeLanguage(localStorage.getItem(LANG_KEY));
    if (stored && SUPPORTED_LANGUAGES.includes(stored)) {
      return stored;
    }
  } catch (error) {
    console.warn("Failed to read language preference:", error);
  }
  return detectDefaultLanguage();
}

let currentLanguage = readLanguage();
setDocumentLanguage(currentLanguage);

function setDocumentLanguage(language) {
  document.documentElement.lang = language === "zh-CN" ? "zh-CN" : "en";
}

function persistLanguage(language) {
  try {
    localStorage.setItem(LANG_KEY, language);
  } catch (error) {
    console.warn("Failed to persist language preference:", error);
  }
}

function emitLanguageChanged(language) {
  window.dispatchEvent(new CustomEvent(LANG_CHANGED_EVENT, { detail: { language } }));
}

function readAuthState() {
  try {
    const raw = localStorage.getItem(AUTH_KEY);
    if (!raw) {
      return null;
    }
    return JSON.parse(raw);
  } catch (error) {
    console.warn("Failed to parse auth state:", error);
    return null;
  }
}

function writeAuthState(state) {
  localStorage.setItem(AUTH_KEY, JSON.stringify(state));
}

function replaceTemplate(template, params = {}) {
  return String(template).replace(/\{(\w+)}/g, (_, key) => {
    if (params[key] === undefined || params[key] === null) {
      return "";
    }
    return String(params[key]);
  });
}

export function getLanguage() {
  return currentLanguage;
}

export function setLanguage(language) {
  const normalized = normalizeLanguage(language);
  if (!normalized || !SUPPORTED_LANGUAGES.includes(normalized)) {
    return currentLanguage;
  }
  if (normalized === currentLanguage) {
    return currentLanguage;
  }
  currentLanguage = normalized;
  persistLanguage(currentLanguage);
  setDocumentLanguage(currentLanguage);
  applyI18n(document);
  emitLanguageChanged(currentLanguage);
  return currentLanguage;
}

export function onLanguageChanged(handler) {
  if (typeof handler !== "function") {
    return () => {};
  }
  const wrapped = (event) => handler(event.detail?.language || currentLanguage);
  window.addEventListener(LANG_CHANGED_EVENT, wrapped);
  return () => window.removeEventListener(LANG_CHANGED_EVENT, wrapped);
}

export function t(key, params = {}) {
  const langPack = I18N[currentLanguage] || I18N.en;
  const fallbackPack = I18N.en;
  const template = langPack[key] ?? fallbackPack[key] ?? key;
  return replaceTemplate(template, params);
}

export function applyI18n(root = document) {
  if (!root) {
    return;
  }
  const base = root instanceof Element || root instanceof Document ? root : document;

  base.querySelectorAll("[data-i18n]").forEach((el) => {
    const key = el.getAttribute("data-i18n");
    if (key) {
      el.textContent = t(key);
    }
  });

  base.querySelectorAll("[data-i18n-placeholder]").forEach((el) => {
    const key = el.getAttribute("data-i18n-placeholder");
    if (key) {
      el.setAttribute("placeholder", t(key));
    }
  });

  base.querySelectorAll("[data-i18n-title]").forEach((el) => {
    const key = el.getAttribute("data-i18n-title");
    if (key) {
      el.setAttribute("title", t(key));
    }
  });

  base.querySelectorAll("[data-i18n-value]").forEach((el) => {
    const key = el.getAttribute("data-i18n-value");
    if (key) {
      el.setAttribute("value", t(key));
    }
  });
}

export function getAuth() {
  return readAuthState();
}

export function getToken() {
  return readAuthState()?.accessToken || null;
}

export function getCurrentUser() {
  return readAuthState()?.user || null;
}

export function getCurrentUserId() {
  return getCurrentUser()?.userId || null;
}

export function isLoggedIn() {
  return !!getToken();
}

export function setLogin(loginResponse) {
  writeAuthState({
    tokenType: loginResponse.tokenType || "Bearer",
    accessToken: loginResponse.accessToken,
    expiresAt: loginResponse.expiresAt || null,
    user: loginResponse.user || null,
  });
}

export async function logout() {
  const token = getToken();
  if (token) {
    try {
      await apiRequest("/api/v1/auth/logout", { method: "POST" }, { auth: true, throwOnError: false });
    } catch (error) {
      console.warn("Logout request failed:", error);
    }
  }
  clearLogin();
}

export function clearLogin() {
  localStorage.removeItem(AUTH_KEY);
}

function currentLocale() {
  return currentLanguage === "zh-CN" ? "zh-CN" : "en-US";
}

export function formatMoney(value) {
  if (value === null || value === undefined || value === "") {
    return "-";
  }
  const number = Number(value);
  if (Number.isNaN(number)) {
    return String(value);
  }
  return new Intl.NumberFormat(currentLocale(), {
    style: "currency",
    currency: "CNY",
    currencyDisplay: "symbol",
  }).format(number);
}

export function formatTime(value) {
  if (!value) {
    return "-";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return String(value);
  }
  return date.toLocaleString(currentLocale());
}

export function csvToArray(csvText) {
  if (!csvText || !csvText.trim()) {
    return [];
  }
  return csvText
    .split(",")
    .map((v) => v.trim())
    .filter((v) => v.length > 0);
}

export function query(name) {
  const value = new URLSearchParams(window.location.search).get(name);
  return value ? value.trim() : null;
}

export function sanitizeReturnUrl(raw) {
  if (!raw) {
    return "/index.html";
  }
  try {
    const decoded = decodeURIComponent(raw);
    if (decoded.startsWith("/") && !decoded.startsWith("//")) {
      return decoded;
    }
  } catch (error) {
    console.warn("Failed to decode returnUrl:", error);
  }
  return "/index.html";
}

export function gotoLogin() {
  const back = `${window.location.pathname}${window.location.search || ""}`;
  window.location.href = `/login.html?returnUrl=${encodeURIComponent(back)}`;
}

export function requireLogin() {
  if (!isLoggedIn()) {
    gotoLogin();
    return false;
  }
  return true;
}

function authHeader() {
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export async function apiRequest(url, options = {}, ext = {}) {
  const { auth = false, throwOnError = true } = ext;
  const finalOptions = { ...options };

  const headers = {
    ...(options.headers || {}),
  };

  if (auth) {
    Object.assign(headers, authHeader());
  }

  if (options.body && typeof options.body === "object" && !(options.body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
    finalOptions.body = JSON.stringify(options.body);
  }

  finalOptions.headers = headers;

  const response = await fetch(url, finalOptions);
  let payload = null;
  try {
    payload = await response.json();
  } catch (error) {
    if (throwOnError) {
      throw new Error(`${t("common.requestFailed")}: ${response.status}`);
    }
    return null;
  }

  if (payload.code !== SUCCESS_CODE) {
    if (payload.code === 401) {
      clearLogin();
    }
    if (throwOnError) {
      throw new Error(payload.message || t("common.requestFailed"));
    }
    return null;
  }

  return payload.data;
}

export function showNotice(target, message, isError = false) {
  target.className = `notice${isError ? " error" : ""}`;
  target.textContent = message;
  target.hidden = false;
}

export function hideNotice(target) {
  target.hidden = true;
  target.textContent = "";
}

function topbarLink(href, labelKey, active) {
  return `<a class="nav-link ${active ? "active" : ""}" href="${href}" data-i18n="${labelKey}">${t(labelKey)}</a>`;
}

function languageOptions() {
  return `
    <option value="zh-CN">${t("topbar.language.zhCN")}</option>
    <option value="en">${t("topbar.language.en")}</option>
  `;
}

export function renderTopbar(activePage) {
  const topbar = document.getElementById("topbar");
  if (!topbar) {
    return;
  }

  const user = getCurrentUser();
  const userName = user?.nickname || user?.username || t("common.user");

  const authArea = isLoggedIn()
    ? `
      <span class="user-chip">${userName}</span>
      <button class="btn btn-ghost" id="btnLogout" data-i18n="topbar.logout">${t("topbar.logout")}</button>
    `
    : `
      <a class="btn btn-ghost" href="/login.html" data-i18n="topbar.login">${t("topbar.login")}</a>
      <a class="btn btn-primary" href="/register.html" data-i18n="topbar.register">${t("topbar.register")}</a>
    `;

  topbar.className = "topbar";
  topbar.innerHTML = `
    <div class="topbar-inner">
      <div class="brand"><a href="/index.html" data-i18n="topbar.brand">${t("topbar.brand")}</a></div>
      <nav class="nav">
        ${topbarLink("/index.html", "topbar.nav.marketplace", activePage === "home")}
        ${topbarLink("/publish.html", "topbar.nav.publish", activePage === "publish")}
        ${topbarLink("/my-products.html", "topbar.nav.myProducts", activePage === "my-products")}
        ${topbarLink("/favorites.html", "topbar.nav.favorites", activePage === "favorites")}
        ${topbarLink("/messages.html", "topbar.nav.messages", activePage === "messages")}
      </nav>
      <div class="topbar-right">
        <label class="lang-label" for="languageSwitcher" data-i18n="topbar.language">${t("topbar.language")}</label>
        <select id="languageSwitcher" class="lang-select">${languageOptions()}</select>
        ${authArea}
      </div>
    </div>
  `;

  applyI18n(topbar);

  const logoutBtn = document.getElementById("btnLogout");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", async () => {
      await logout();
      window.location.href = "/index.html";
    });
  }

  const languageSwitcher = document.getElementById("languageSwitcher");
  if (languageSwitcher) {
    languageSwitcher.value = currentLanguage;
    languageSwitcher.addEventListener("change", () => {
      const nextLanguage = languageSwitcher.value;
      setLanguage(nextLanguage);
      renderTopbar(activePage);
    });
  }
}

persistLanguage(currentLanguage);
applyI18n(document);
