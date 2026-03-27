<template>
  <div class="warning-center page-shell">
    <section class="warning-hero surface-card">
      <div>
        <div class="hero-eyebrow">Risk Monitor</div>
        <h2>{{ t('warning.title') }}</h2>
        <p>{{ isTeacher ? t('warning.teacherSubtitle') : t('warning.studentSubtitle') }}</p>
      </div>
      <div class="hero-summary">
        <div class="summary-chip">
          <strong>{{ warnings.length }}</strong>
          <span>{{ t('common.warning') }}</span>
        </div>
        <div class="summary-chip">
          <strong>{{ totalRiskItems }}</strong>
          <span>{{ t('common.riskItems') }}</span>
        </div>
        <el-button @click="goBack">{{ t('common.back') }}</el-button>
      </div>
    </section>

    <template v-if="isTeacher">
      <el-card shadow="never" class="surface-card">
        <template #header>
          <div class="card-header">
            <div>
              <h3>{{ t('warning.teacherList') }}</h3>
              <p>按综合分、缺勤综合识别风险学生，便于教师快速介入。</p>
            </div>
          </div>
        </template>
        <el-empty v-if="!warnings.length" :description="t('warning.noTeacherWarnings')" />
        <el-table v-else :data="warnings" stripe>
          <el-table-column prop="studentName" :label="t('warning.studentName')" width="120" />
          <el-table-column prop="totalScore" :label="t('common.totalScore')" width="100">
            <template #default="scope">{{ scope.row.totalScore?.toFixed(1) }}</template>
          </el-table-column>
          <el-table-column prop="absenceCount" :label="t('common.absenceCount')" width="80" />
          <el-table-column :label="t('warning.invalidIndicators')">
            <template #default="scope">
              <el-tag v-for="item in scope.row.riskItems" :key="item" type="danger" style="margin-right: 6px; margin-bottom: 6px;">{{ item }}</el-tag>
            </template>
          </el-table-column>
          <!-- <el-table-column prop="emotionLabel" :label="t('warning.emotionLabel')" min-width="260" /> -->
          <el-table-column :label="t('common.actions')" width="120">
            <template #default="scope">
              <el-button type="primary" link @click="openDetail(scope.row)">{{ t('common.viewDetail') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>

    <template v-else>
      <el-empty v-if="!warnings.length" :description="t('warning.noStudentWarnings')" />
      <div v-else class="student-warning-grid">
        <el-card v-for="item in warnings" :key="item.courseId" class="student-warning-card surface-card" shadow="hover">
          <div class="warning-title-row">
            <div>
              <strong>{{ item.courseName }}</strong>
              <div class="course-risk-copy">{{ item.riskItems.join('、') }}</div>
            </div>
            <el-tag type="danger">{{ t('warning.active') }}</el-tag>
          </div>
          <div class="metric-grid">
            <div class="metric-box">
              <span>{{ t('common.totalScore') }}</span>
              <strong>{{ item.totalScore?.toFixed(1) }}</strong>
            </div>
            <div class="metric-box">
              <span>{{ t('common.absenceCount') }}</span>
              <strong>{{ item.absenceCount }}</strong>
            </div>
          </div>
          <!-- <div class="emotion-block">
            <div class="emotion-label">{{ item.emotionLabel }}</div>
            <div class="emotion-detail">{{ item.communicationAdvice }}</div>
          </div> -->
        </el-card>
      </div>
    </template>

    <el-drawer v-model="drawerVisible" :title="t('warning.detailTitle')" size="420px">
      <template v-if="selectedWarning">
        <el-descriptions :column="1" border>
          <el-descriptions-item :label="t('common.student')">{{ selectedWarning.studentName }}</el-descriptions-item>
          <el-descriptions-item :label="t('common.course')">{{ selectedWarning.courseName }}</el-descriptions-item>
          <el-descriptions-item :label="t('common.regularScore')">{{ selectedWarning.regularAggregate?.toFixed(1) }}</el-descriptions-item>
          <el-descriptions-item :label="t('common.finalScore')">{{ selectedWarning.finalExamScore?.toFixed(1) }}</el-descriptions-item>
          <el-descriptions-item :label="t('common.totalScore')">{{ selectedWarning.totalScore?.toFixed(1) }}</el-descriptions-item>
          <el-descriptions-item :label="t('common.absenceCount')">{{ selectedWarning.absenceCount }}</el-descriptions-item>
          <!-- <el-descriptions-item :label="t('warning.emotionAnalysis')">{{ selectedWarning.emotionDetail }}</el-descriptions-item> -->
          <!-- <el-descriptions-item :label="t('warning.communicationAdvice')">{{ selectedWarning.communicationAdvice }}</el-descriptions-item> -->
        </el-descriptions>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const route = useRoute()
const router = useRouter()
const { t } = useI18n({ useScope: 'global' })
const currentUser = JSON.parse(localStorage.getItem('user') || 'null')
const isTeacher = computed(() => currentUser?.role === 'TEACHER')
const warnings = ref([])
const drawerVisible = ref(false)
const selectedWarning = ref(null)

const totalRiskItems = computed(() => warnings.value.reduce((count, item) => count + (item.riskItems?.length || 0), 0))

const loadWarnings = async () => {
  if (!currentUser) {
    router.push('/login')
    return
  }
  if (isTeacher.value) {
    const res = await axios.get(`/api/warnings/course/${route.params.id}`)
    warnings.value = res.data
  } else {
    const res = await axios.get(`/api/warnings/student/${currentUser.id}`)
    warnings.value = res.data
  }
}

const openDetail = (row) => {
  selectedWarning.value = row
  drawerVisible.value = true
}

const goBack = () => {
  router.push(isTeacher.value ? '/teacher' : '/student')
}

onMounted(() => {
  loadWarnings()
})
</script>

<style scoped>
.warning-center {
  gap: 20px;
}

.warning-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  padding: 28px;
  background:
    linear-gradient(135deg, rgba(239, 68, 68, 0.08), rgba(251, 191, 36, 0.08)),
    rgba(255, 255, 255, 0.94);
}

.hero-eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #dc2626;
  font-weight: 700;
}

.warning-hero h2 {
  margin: 10px 0 8px;
  font-size: 30px;
}

.warning-hero p {
  margin: 0;
  color: #667085;
  max-width: 680px;
  line-height: 1.7;
}

.hero-summary {
  display: flex;
  gap: 12px;
  align-items: stretch;
}

.summary-chip {
  min-width: 118px;
  padding: 16px 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(239, 68, 68, 0.08);
}

.summary-chip strong {
  display: block;
  font-size: 24px;
  margin-bottom: 6px;
}

.summary-chip span {
  color: #667085;
  font-size: 13px;
}

.card-header h3 {
  margin: 0;
  font-size: 22px;
}

.card-header p {
  margin: 8px 0 0;
  color: #667085;
}

.student-warning-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 18px;
}

.student-warning-card {
  border-left: 4px solid #f56c6c;
}

.warning-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.course-risk-copy {
  margin-top: 8px;
  color: #667085;
  line-height: 1.6;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.metric-box {
  padding: 14px 16px;
  border-radius: 16px;
  background: #fff7ed;
}

.metric-box span {
  display: block;
  color: #9a3412;
  font-size: 13px;
}

.metric-box strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: #111827;
}

.emotion-block {
  margin-top: 16px;
  padding: 14px;
  background: #fff7e6;
  border-radius: 16px;
}

.emotion-label {
  font-weight: 600;
  color: #d46b08;
  margin-bottom: 6px;
}

.emotion-detail {
  color: #8c8c8c;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .warning-hero,
  .hero-summary {
    flex-direction: column;
    align-items: stretch;
  }

  .metric-grid {
    grid-template-columns: 1fr;
  }
}
</style>