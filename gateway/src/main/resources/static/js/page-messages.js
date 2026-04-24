import {
  apiRequest,
  applyI18n,
  formatTime,
  getCurrentUserId,
  gotoLogin,
  hideNotice,
  isLoggedIn,
  onLanguageChanged,
  renderTopbar,
  showNotice,
  t,
} from "./app.js";

renderTopbar("messages");
applyI18n();

if (!isLoggedIn()) {
  gotoLogin();
}

const notice = document.getElementById("messageNotice");
const inboxSummary = document.getElementById("inboxSummary");
const conversationList = document.getElementById("conversationList");
const messageThread = document.getElementById("messageThread");
const threadTitle = document.getElementById("threadTitle");
const replyContent = document.getElementById("replyContent");

const state = {
  inbox: null,
  selected: null,
};

document.getElementById("btnReply").addEventListener("click", sendReply);

onLanguageChanged(async () => {
  applyI18n();
  renderInbox();
  if (state.selected?.conversation?.conversationId) {
    await selectConversation(state.selected);
  } else {
    threadTitle.textContent = t("messages.section.thread");
    messageThread.innerHTML = "";
  }
  inboxSummary.textContent = t("messages.inbox.unread", { count: state.inbox?.unreadCount ?? 0 });
});

function calcReceiverId(conversation) {
  const current = getCurrentUserId();
  if (!conversation) {
    return null;
  }
  if (conversation.buyerId === current) {
    return conversation.sellerId;
  }
  if (conversation.sellerId === current) {
    return conversation.buyerId;
  }
  return null;
}

function renderInbox() {
  const conversations = state.inbox?.conversations || [];
  if (!conversations.length) {
    conversationList.innerHTML = `<div class="meta">${t("messages.empty.conversations")}</div>`;
    return;
  }

  conversationList.innerHTML = conversations
    .map((item) => {
      const c = item.conversation || {};
      const name = item.counterpartUser?.nickname
        || item.counterpartUser?.username
        || t("messages.card.user", { id: item.counterpartUserId || "-" });
      const active = state.selected?.conversation?.conversationId === c.conversationId ? "active" : "";
      return `
        <article class="conversation-item ${active}" data-id="${c.conversationId}">
          <div><strong>${name}</strong></div>
          <div class="meta">${c.productTitle || t("messages.card.unknownProduct")}</div>
          <div class="meta">${c.lastMessagePreview || "-"}</div>
          <div class="meta">${t("messages.card.unreadAndTime", { unread: c.unreadCount ?? 0, time: formatTime(c.lastMessageTime) })}</div>
        </article>
      `;
    })
    .join("");

  conversationList.querySelectorAll(".conversation-item").forEach((el) => {
    el.addEventListener("click", () => {
      const id = Number(el.getAttribute("data-id"));
      const selected = conversations.find((item) => item.conversation?.conversationId === id);
      if (selected) {
        selectConversation(selected);
      }
    });
  });
}

function renderThread(messages) {
  if (!messages.length) {
    messageThread.innerHTML = `<div class="meta">${t("messages.empty.thread")}</div>`;
    return;
  }
  const current = getCurrentUserId();
  messageThread.innerHTML = messages
    .map((msg) => {
      const mine = msg.senderId === current ? "mine" : "";
      return `
        <article class="bubble ${mine}">
          <div class="meta">${t("messages.thread.fromTo", { from: msg.senderId, to: msg.receiverId, time: formatTime(msg.sentAt) })}</div>
          <div>${msg.content || ""}</div>
        </article>
      `;
    })
    .join("");
}

async function selectConversation(item) {
  state.selected = item;
  renderInbox();
  const c = item.conversation || {};
  threadTitle.textContent = t("messages.thread.title", { id: c.conversationId || "-" });

  try {
    const messages = await apiRequest(`/api/v1/me/messages/conversations/${c.conversationId}`, { method: "GET" }, { auth: true });
    renderThread(messages || []);
  } catch (error) {
    showNotice(notice, t("messages.error.loadMessages", { message: error.message }), true);
    messageThread.innerHTML = `<div class="meta">${t("messages.panel.loadThreadFailed")}</div>`;
  }
}

async function sendReply() {
  hideNotice(notice);
  if (!state.selected) {
    showNotice(notice, t("messages.error.selectConversation"), true);
    return;
  }

  const content = replyContent.value.trim();
  if (!content) {
    showNotice(notice, t("messages.error.emptyReply"), true);
    return;
  }

  const conversation = state.selected.conversation || {};
  const receiverId = state.selected.counterpartUserId || calcReceiverId(conversation);
  if (!receiverId) {
    showNotice(notice, t("messages.error.noReceiver"), true);
    return;
  }

  try {
    await apiRequest(
      "/api/v1/me/messages/send",
      {
        method: "POST",
        body: {
          conversationId: conversation.conversationId,
          productId: conversation.productId,
          receiverId,
          messageType: "TEXT",
          content,
        },
      },
      { auth: true }
    );
    replyContent.value = "";
    showNotice(notice, t("messages.notice.replySent"));
    await loadInbox();
    const reselected = (state.inbox?.conversations || []).find(
      (it) => it.conversation?.conversationId === conversation.conversationId
    );
    if (reselected) {
      await selectConversation(reselected);
    }
  } catch (error) {
    showNotice(notice, t("messages.error.replyFailed", { message: error.message }), true);
  }
}

async function loadInbox() {
  hideNotice(notice);
  try {
    const inbox = await apiRequest("/api/v1/me/messages", { method: "GET" }, { auth: true });
    state.inbox = inbox || {};
    inboxSummary.textContent = t("messages.inbox.unread", { count: state.inbox.unreadCount ?? 0 });
    renderInbox();
    if (!state.selected && (state.inbox.conversations || []).length > 0) {
      await selectConversation(state.inbox.conversations[0]);
    }
  } catch (error) {
    showNotice(notice, t("messages.error.loadInbox", { message: error.message }), true);
  }
}

loadInbox();
