<template>
  <div class="message-center page-shell">
    <section class="message-hero surface-card">
      <div>
        <div class="hero-eyebrow">Communication Hub</div>
        <h2>{{ t('message.title') }}</h2>
        <p>{{ t('message.subtitle') }}</p>
      </div>
      <div class="hero-meta">
        <div class="hero-chip">
          <strong>{{ threads.length }}</strong>
          <span>{{ isStudent ? t('message.history') : t('message.teacherQueue') }}</span>
        </div>
        <div class="hero-chip">
          <strong>{{ pendingCount }}</strong>
          <span>{{ t('message.pending') }}</span>
        </div>
        <el-button @click="goBack">{{ t('common.back') }}</el-button>
      </div>
    </section>

    <template v-if="isStudent">
      <div class="message-grid">
        <el-card shadow="never" class="surface-card compose-card">
          <template #header>
            <span>{{ t('message.newQuestion') }}</span>
          </template>
          <el-form label-position="top">
            <el-form-item :label="t('message.questionText')">
              <el-input v-model="questionForm.questionText" type="textarea" :rows="5" :placeholder="t('message.questionPlaceholder')" />
            </el-form-item>
            <el-form-item :label="t('message.uploadAttachment')">
              <el-upload :auto-upload="false" :show-file-list="false" :before-upload="handleQuestionFile">
                <el-button>{{ t('message.chooseImage') }}</el-button>
              </el-upload>
              <div v-if="questionForm.questionAttachment" class="attachment-preview">
                <img v-if="isImageAttachment(questionForm.questionAttachment)" :src="questionForm.questionAttachment" alt="attachment" />
                <a v-else :href="questionForm.questionAttachment" target="_blank" rel="noreferrer">{{ t('message.viewAttachment') }}</a>
              </div>
            </el-form-item>
            <el-button type="primary" @click="submitQuestion">{{ t('message.submit') }}</el-button>
          </el-form>
        </el-card>

        <el-card shadow="never" class="surface-card history-card">
          <template #header>
            <span>{{ t('message.history') }}</span>
          </template>
          <el-empty v-if="!threads.length" :description="studentEmptyDescription" />
          <div v-else class="thread-list">
            <div v-for="item in threads" :key="item.id" class="thread-item">
              <div class="thread-meta">
                <div>
                  <strong>{{ item.courseName || t('message.currentCourse') }}</strong>
                  <div class="sub-meta">{{ item.replyText ? t('message.replied') : t('message.pending') }}</div>
                </div>
                <el-tag :type="item.status === 'REPLIED' ? 'success' : 'warning'">{{ item.status === 'REPLIED' ? t('message.replied') : t('message.pending') }}</el-tag>
              </div>
              <div class="bubble student-bubble">{{ item.questionText }}</div>
              <img v-if="item.questionAttachment && isImageAttachment(item.questionAttachment)" :src="item.questionAttachment" class="bubble-image" alt="question attachment" />
              <a v-else-if="item.questionAttachment" :href="item.questionAttachment" class="attachment-link" target="_blank" rel="noreferrer">{{ t('message.studentAttachment') }}</a>
              <div v-if="item.replyText" class="bubble teacher-bubble">{{ item.replyText }}</div>
              <img v-if="item.replyAttachment && isImageAttachment(item.replyAttachment)" :src="item.replyAttachment" class="bubble-image" alt="reply attachment" />
              <a v-else-if="item.replyAttachment" :href="item.replyAttachment" class="attachment-link" target="_blank" rel="noreferrer">{{ t('message.teacherAttachment') }}</a>
            </div>
          </div>
        </el-card>
      </div>
    </template>

    <template v-else>
      <el-card shadow="never" class="surface-card teacher-queue-card">
        <template #header>
          <span>{{ t('message.teacherQueue') }}</span>
        </template>
        <el-empty v-if="!threads.length" :description="teacherEmptyDescription" />
        <div v-else class="thread-list">
          <div v-for="item in threads" :key="item.id" class="thread-item teacher-thread">
            <div class="thread-meta">
              <div>
                <strong>{{ item.studentName }}</strong>
                <span class="sub-meta">{{ item.courseName }}</span>
              </div>
              <el-tag :type="item.status === 'REPLIED' ? 'success' : 'danger'">{{ item.status === 'REPLIED' ? t('message.replied') : t('message.pending') }}</el-tag>
            </div>
            <div class="bubble student-bubble">{{ item.questionText }}</div>
            <img v-if="item.questionAttachment && isImageAttachment(item.questionAttachment)" :src="item.questionAttachment" class="bubble-image" alt="question attachment" />
            <a v-else-if="item.questionAttachment" :href="item.questionAttachment" class="attachment-link" target="_blank" rel="noreferrer">{{ t('message.studentAttachment') }}</a>
            <div class="reply-panel">
              <el-input v-model="replyDrafts[item.id].replyText" type="textarea" :rows="3" :placeholder="t('message.replyPlaceholder')" />
              <div class="reply-actions">
                <el-upload :auto-upload="false" :show-file-list="false" :before-upload="(file) => handleReplyFile(file, item.id)">
                  <el-button size="small">{{ t('message.uploadAttachment') }}</el-button>
                </el-upload>
                <el-button type="primary" size="small" @click="submitReply(item.id)">{{ t('message.sendReply') }}</el-button>
              </div>
              <img v-if="replyDrafts[item.id].replyAttachment && isImageAttachment(replyDrafts[item.id].replyAttachment)" :src="replyDrafts[item.id].replyAttachment" class="bubble-image" alt="reply draft attachment" />
              <a v-else-if="replyDrafts[item.id].replyAttachment" :href="replyDrafts[item.id].replyAttachment" class="attachment-link" target="_blank" rel="noreferrer">{{ t('message.draftAttachment') }}</a>
            </div>
            <div v-if="item.replyText" class="bubble teacher-bubble">{{ item.replyText }}</div>
            <img v-if="item.replyAttachment && isImageAttachment(item.replyAttachment)" :src="item.replyAttachment" class="bubble-image" alt="reply attachment" />
            <a v-else-if="item.replyAttachment" :href="item.replyAttachment" class="attachment-link" target="_blank" rel="noreferrer">{{ t('message.teacherAttachment') }}</a>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const { t } = useI18n({ useScope: 'global' })
const courseId = route.params.id
const currentUser = JSON.parse(localStorage.getItem('user') || 'null')
const isStudent = computed(() => currentUser?.role === 'STUDENT')
const threads = ref([])
const courseTeacherId = ref(route.query.teacherId ? Number(route.query.teacherId) : null)
const questionForm = reactive({
  questionText: '',
  questionAttachment: ''
})
const replyDrafts = reactive({})

const pendingCount = computed(() => threads.value.filter(item => item.status !== 'REPLIED').length)
const studentEmptyDescription = computed(() => courseTeacherId.value ? t('message.noHistory') : '当前课程尚未绑定任课教师，暂时无法发起答疑')
const teacherEmptyDescription = computed(() => '当前课程还没有学生提问，后续新消息会优先显示在这里')

const uploadAttachment = async (file) => {
  const formData = new FormData()
  formData.append('file', file)
  const res = await axios.post('/api/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
  return res.data.url
}

const ensureReplyDraft = (id) => {
  if (!replyDrafts[id]) {
    replyDrafts[id] = { replyText: '', replyAttachment: '' }
  }
}

const loadThreads = async () => {
  if (!currentUser) {
    router.push('/login')
    return
  }
  const url = isStudent.value
    ? `/api/messages/course/${courseId}/student/${currentUser.id}`
    : `/api/messages/course/${courseId}/teacher/${currentUser.id}`
  const res = await axios.get(url)
  threads.value = res.data
  threads.value.forEach(item => ensureReplyDraft(item.id))
}

const ensureTeacherId = async () => {
  if (courseTeacherId.value) {
    return courseTeacherId.value
  }
  const res = await axios.get(`/api/courses/${courseId}`)
  courseTeacherId.value = res.data?.teacherId ? Number(res.data.teacherId) : null
  return courseTeacherId.value
}

const handleQuestionFile = async (file) => {
  questionForm.questionAttachment = await uploadAttachment(file)
  ElMessage.success(t('message.uploadSuccess'))
  return false
}

const handleReplyFile = async (file, id) => {
  ensureReplyDraft(id)
  replyDrafts[id].replyAttachment = await uploadAttachment(file)
  ElMessage.success(t('message.uploadSuccess'))
  return false
}

const isImageAttachment = (url) => /\.(png|jpg|jpeg|gif|webp|bmp|svg)(\?|$)/i.test(url || '')

const submitQuestion = async () => {
  if (!questionForm.questionText.trim()) {
    ElMessage.warning(t('message.fillQuestion'))
    return
  }
  const teacherId = await ensureTeacherId()
  if (!teacherId) {
    ElMessage.warning('当前课程尚未绑定教师，暂时无法提交答疑')
    return
  }
  await axios.post('/api/messages/question', {
    courseId,
    studentId: currentUser.id,
    teacherId,
    questionText: questionForm.questionText,
    questionAttachment: questionForm.questionAttachment
  })
  ElMessage.success(t('message.questionSuccess'))
  questionForm.questionText = ''
  questionForm.questionAttachment = ''
  await loadThreads()
}

const submitReply = async (id) => {
  ensureReplyDraft(id)
  if (!replyDrafts[id].replyText.trim()) {
    ElMessage.warning(t('message.fillReply'))
    return
  }
  await axios.post(`/api/messages/${id}/reply`, replyDrafts[id])
  ElMessage.success(t('message.replySuccess'))
  replyDrafts[id] = { replyText: '', replyAttachment: '' }
  await loadThreads()
}

const goBack = () => {
  router.push(isStudent.value ? '/student' : '/teacher')
}

onMounted(() => {
  loadThreads()
})
</script>

<style scoped>
.message-center {
  gap: 20px;
}

.message-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  padding: 28px;
  background:
    linear-gradient(135deg, rgba(15, 118, 110, 0.08), rgba(59, 130, 246, 0.08)),
    rgba(255, 255, 255, 0.94);
}

.hero-eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #0f766e;
  font-weight: 700;
}

.message-hero h2 {
  margin: 10px 0 8px;
  font-size: 30px;
}

.message-hero p {
  margin: 0;
  color: #667085;
  max-width: 680px;
  line-height: 1.7;
}

.hero-meta {
  display: flex;
  align-items: stretch;
  gap: 12px;
}

.hero-chip {
  min-width: 118px;
  padding: 16px 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(15, 118, 110, 0.08);
}

.hero-chip strong {
  display: block;
  font-size: 24px;
  margin-bottom: 6px;
}

.hero-chip span {
  color: #667085;
  font-size: 13px;
}

.message-grid {
  display: grid;
  grid-template-columns: minmax(320px, 420px) minmax(0, 1fr);
  gap: 20px;
}

.thread-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.thread-item {
  padding: 18px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.92));
}

.thread-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.sub-meta {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
}

.bubble {
  padding: 12px 14px;
  border-radius: 16px;
  margin-bottom: 10px;
  line-height: 1.6;
}

.student-bubble {
  background: #fdf6ec;
  border-left: 4px solid #e6a23c;
}

.teacher-bubble {
  background: #f0f9eb;
  border-left: 4px solid #67c23a;
}

.reply-panel {
  margin: 12px 0;
  padding: 14px;
  border-radius: 16px;
  background: #f8fafc;
  border: 1px solid rgba(148, 163, 184, 0.16);
}

.reply-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.attachment-preview img,
.bubble-image {
  max-width: min(100%, 280px);
  max-height: 180px;
  border-radius: 12px;
  display: block;
  margin-top: 8px;
  border: 1px solid #ebeef5;
}

.attachment-link {
  display: inline-block;
  margin-top: 8px;
  color: #409eff;
  text-decoration: none;
}

.attachment-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .message-hero,
  .hero-meta,
  .message-grid,
  .reply-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .message-grid {
    display: flex;
  }
}
</style>