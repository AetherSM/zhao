<template>
  <div class="chat-container">
    <div class="contact-list">
      <div class="contact-header">
        <span>联系人</span>
        <el-button type="primary" :icon="Plus" circle size="small" @click="openSearchDialog" />
      </div>

      <div class="contact-items">
        <div
          v-for="contact in contacts"
          :key="contact.id"
          class="contact-item"
          :class="{ active: selectedContact?.id === contact.id }"
          @click="selectContact(contact)"
        >
          <div class="contact-avatar">{{ contact.realName?.charAt(0) }}</div>
          <div class="contact-info">
            <div class="contact-name">{{ contact.realName }}</div>
            <div class="contact-role">账号: {{ contact.username }}</div>
          </div>
        </div>
        <div v-if="contacts.length === 0" class="no-result">
          暂无联系人，请点击右上角添加
        </div>
      </div>
    </div>

    <div class="chat-window" v-if="selectedContact">
      <div class="chat-header">
        与 {{ selectedContact.realName }} 聊天中
      </div>
      <div class="message-list" ref="messageList">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-item"
          :class="{ 'mine': msg.fromUserId == currentUserId }"
        >
          <div class="message-content">{{ msg.content }}</div>
          <div class="message-time">{{ formatTime(msg.createTime) }}</div>
        </div>
      </div>
      <div class="chat-input">
        <textarea
          v-model="inputMsg"
          placeholder="请输入消息..."
          @keyup.enter.prevent="handleSend"
        ></textarea>
        <button @click="handleSend" :disabled="!inputMsg.trim()">发送</button>
      </div>
    </div>
    <div class="chat-empty" v-else>
      请在左侧选择联系人开始聊天
    </div>

    <!-- 添加联系人弹窗 -->
    <el-dialog v-model="showSearchDialog" title="查找联系人" width="450px" @closed="handleSearchClear">
      <div class="search-dialog-content">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索姓名或用户名..."
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>

        <div class="search-results-list" v-loading="searching">
          <div v-if="searchResults.length > 0">
            <div
              v-for="user in searchResults"
              :key="user.id"
              class="search-result-item"
            >
              <div class="user-avatar">{{ user.realName?.charAt(0) }}</div>
              <div class="user-info">
                <div class="user-name">{{ user.realName }}</div>
                <div class="user-account">账号: {{ user.username }}</div>
              </div>
              <el-button type="primary" size="small" @click="addContact(user)">添加</el-button>
            </div>
          </div>
          <div v-else-if="searchKeyword && !searching" class="no-result">
            未找到相关用户
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { getContacts, getChatHistory, sendMessage } from '../api/chat'
import { useAuthStore } from '../store/auth'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const currentUserId = authStore.user?.userId
const contacts = ref([])
const searchResults = ref([])
const searchKeyword = ref('')
const showSearchDialog = ref(false)
const searching = ref(false)
const selectedContact = ref(null)
const messages = ref([])
const inputMsg = ref('')
const messageList = ref(null)
let timer = null

// 初始只获取已有的联系人列表（这里简化逻辑：先显示全部非当前用户，后续可优化为只显示有聊天记录的人）
const fetchContacts = async () => {
  try {
    const res = await getContacts()
    contacts.value = res || []
  } catch (e) {
    console.error('Fetch contacts error:', e)
  }
}

const openSearchDialog = () => {
  showSearchDialog.value = true
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = []
    return
  }
  searching.value = true
  try {
    const res = await getContacts(searchKeyword.value)
    searchResults.value = res || []
  } catch (e) {
    console.error('Search error:', e)
    ElMessage.error('搜索失败')
  } finally {
    searching.value = false
  }
}

const handleSearchClear = () => {
  searchKeyword.value = ''
  searchResults.value = []
}

const addContact = (user) => {
  // 添加联系人逻辑：选中并开始聊天
  selectContact(user)
  showSearchDialog.value = false
  ElMessage.success(`已添加联系人: ${user.realName}`)
}

const selectContact = async (contact) => {
  selectedContact.value = contact
  await fetchHistory()
  scrollToBottom()
}

const fetchHistory = async () => {
  if (!selectedContact.value) return
  try {
    const res = await getChatHistory(selectedContact.value.id)
    messages.value = res || []
  } catch (e) {
    console.error('Fetch history error:', e)
  }
}

const handleSend = async () => {
  if (!inputMsg.value.trim() || !selectedContact.value) return
  const data = {
    toUserId: selectedContact.value.id,
    content: inputMsg.value
  }
  try {
    await sendMessage(data)
    inputMsg.value = ''
    await fetchHistory()
    scrollToBottom()
  } catch (e) {
    ElMessage.error('发送失败')
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  })
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return new Date(timeStr).toLocaleString()
}

onMounted(() => {
  fetchContacts()
  timer = setInterval(fetchHistory, 5000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 120px);
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.contact-list {
  width: 280px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.contact-header {
  padding: 15px;
  font-weight: bold;
  border-bottom: 1px solid #eee;
  background: #f8f9fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contact-items {
  flex: 1;
  overflow-y: auto;
}

.contact-item {
  display: flex;
  padding: 12px 15px;
  cursor: pointer;
  align-items: center;
  transition: background 0.2s;
}

.contact-item:hover {
  background: #f0f2f5;
}

.contact-item.active {
  background: #e6f7ff;
}

.contact-avatar {
  width: 40px;
  height: 40px;
  background: #1890ff;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-right: 12px;
}

.contact-info {
  flex: 1;
  overflow: hidden;
}

.contact-name {
  font-size: 14px;
  font-weight: 500;
}

.contact-role {
  font-size: 12px;
  color: #999;
}

.no-result {
  padding: 20px;
  text-align: center;
  color: #999;
  font-size: 13px;
}

.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 15px;
  font-weight: bold;
  border-bottom: 1px solid #eee;
}

.message-list {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f5f5f5;
}

.message-item {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.message-item.mine {
  align-items: flex-end;
}

.message-content {
  max-width: 70%;
  padding: 10px 15px;
  background: #fff;
  border-radius: 8px;
  font-size: 14px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.mine .message-content {
  background: #95ec69;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 5px;
}

.chat-input {
  padding: 15px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.chat-input textarea {
  flex: 1;
  height: 60px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: none;
  font-family: inherit;
}

.chat-input button {
  width: 80px;
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.chat-input button:disabled {
  background: #ccc;
}

.chat-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 16px;
}

/* 搜索弹窗样式 */
.search-dialog-content {
  padding: 0 10px;
}

.search-results-list {
  margin-top: 15px;
  max-height: 300px;
  overflow-y: auto;
}

.search-result-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.search-result-item:last-child {
  border-bottom: none;
}

.user-avatar {
  width: 36px;
  height: 36px;
  background: #409eff;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
}

.user-account {
  font-size: 12px;
  color: #999;
}
</style>
