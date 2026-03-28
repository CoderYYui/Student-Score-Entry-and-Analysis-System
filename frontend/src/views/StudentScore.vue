<template>
  <div class="student-score-view page-shell">
    <section class="score-hero surface-card">
      <div>
        <div class="hero-eyebrow">Performance Snapshot</div>
        <h2>{{ t('studentScore.title') }}</h2>
        <p>View the detailed grades of the current course, the individual performance breakdown and the overall achievement status, and quickly identify your strengths and potential risks.</p>
      </div>
      <div class="hero-actions">
        <div class="hero-chip" v-if="score">
          <strong>{{ score.totalScore?.toFixed(1) }}</strong>
          <span>{{ t('common.totalScore') }}</span>
        </div>
        <div class="hero-chip" v-if="score">
          <strong>{{ score.absenceCount ?? 0 }}</strong>
          <span>{{ t('common.absenceCount') }}</span>
        </div>
        <el-button @click="$router.push('/student')">{{ t('common.backToDashboard') }}</el-button>
      </div>
    </section>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover" class="score-card mb-4" v-loading="loading">
          <template #header>
            <div class="card-header">
              <div>
                <span class="card-title">{{ t('studentScore.detailTitle') }}</span>
                <p class="card-subtitle">The system will automatically calculate the test average score, the experimental result and the final weighted score.</p>
              </div>
              <el-tag :type="getGradeType(score?.gradeLabel)" size="large" effect="dark" v-if="score">
                {{ score?.gradeLabel || t('studentScore.unknown') }}
              </el-tag>
            </div>
          </template>
          
          <el-descriptions border :column="2" v-if="score" direction="vertical">
            <el-descriptions-item :label="t('studentScore.attendance100')">{{ score.attendanceScore }}</el-descriptions-item>
            <el-descriptions-item :label="t('studentScore.performance100')">{{ score.performanceScore }}</el-descriptions-item>
            <el-descriptions-item :label="t('studentScore.homework100')">{{ score.homeworkScore }}</el-descriptions-item>
            
            <el-descriptions-item :label="t('studentScore.testAverage')" label-class-name="highlight-label">
                <span class="calc-val">{{ testAvg }} <span class="formula">({{ t('studentScore.testFormula') }})</span></span>
            </el-descriptions-item>
            
            <el-descriptions-item :label="t('studentScore.labAttendance100')">{{ score.labAttendanceScore }}</el-descriptions-item>
            <el-descriptions-item :label="t('studentScore.labReport100')">{{ score.labReportScore }}</el-descriptions-item>
            
            <el-descriptions-item :label="t('studentScore.final100')" label-class-name="highlight-label">
                <span style="font-weight: bold;">{{ score.finalExamScore }}</span>
            </el-descriptions-item>

            <el-descriptions-item :label="t('common.absenceCount')">{{ score.absenceCount ?? 0 }}</el-descriptions-item>
            
            <el-descriptions-item :label="t('studentScore.finalWeighted')" label-class-name="total-label">
              <strong class="total-score">{{ score.totalScore?.toFixed(2) }}</strong>
            </el-descriptions-item>
          </el-descriptions>
          <el-empty v-else :description="t('studentScore.noScore')"></el-empty>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="mb-4">
          <template #header>
            <span class="card-title">{{ t('studentScore.directEvaluation') }}</span>
          </template>
          <div class="eval-box">
             <div class="eval-item">
               <span class="eval-title">{{ t('studentScore.regularPerformance') }}</span>
                 <el-tag :type="getSegmentType(dailyScore)">{{ getSegmentLabel(dailyScore) }}</el-tag>
             </div>
             <div class="eval-item">
               <span class="eval-title">{{ t('studentScore.labPerformance') }}</span>
                 <el-tag :type="getSegmentType(labScore)">{{ getSegmentLabel(labScore) }}</el-tag>
             </div>
             <div class="eval-item">
               <span class="eval-title">{{ t('studentScore.testPerformance') }}</span>
                 <el-tag :type="getSegmentType(testAvg)">{{ getSegmentLabel(testAvg) }}</el-tag>
             </div>
             <div class="eval-item">
               <span class="eval-title">{{ t('studentScore.finalPerformance') }}</span>
                 <el-tag :type="getSegmentType(score?.finalExamScore)">{{ getSegmentLabel(score?.finalExamScore) }}</el-tag>
             </div>
          </div>
        </el-card>

        <el-card shadow="hover">
          <template #header>
              <span class="card-title">{{ t('studentScore.achievementChart') }}</span>
          </template>
          <div class="chart-container">
             <canvas id="studentRadarChart" width="100%" height="250"></canvas>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import Chart from 'chart.js/auto'

const route = useRoute()
const { t } = useI18n({ useScope: 'global' })
const score = ref(null)
const loading = ref(false)
const courseId = route.params.id

// Calculated based on Use Case 5 formula
const dailyScore = computed(() => {
    if (!score.value) return 0;
    return (0.3 * (score.value.attendanceScore||0) + 0.2 * (score.value.performanceScore||0) + 0.5 * (score.value.homeworkScore||0)).toFixed(1);
})

const testAvg = computed(() => {
    if (!score.value) return 0;
    return ((score.value.test1Score||0) + (score.value.test2Score||0) + (score.value.test3Score||0) + (score.value.test4Score||0)) / 4;
})

const labScore = computed(() => {
    if (!score.value) return 0;
    return (0.2 * (score.value.labAttendanceScore||0) + 0.8 * (score.value.labReportScore||0)).toFixed(1);
})

const getSegmentLabel = (val) => {
    if (val == null) return '-';
  if (val >= 90) return 'A';
  if (val >= 80) return 'B';
  if (val >= 70) return 'C';
  if (val >= 60) return 'D';
  return 'F';
}

const getSegmentType = (val) => {
    if (val == null) return 'info';
    if (val >= 90) return 'success';
    if (val >= 80) return '';
    if (val >= 70) return 'warning';
    if (val >= 60) return 'info';
    return 'danger';
}

const getGradeType = (label) => {
  const map = { A: 'Excellent', B: 'Good', C: 'Moderate', D: 'Pass', F: 'Fail' }
  const normalized = map[label] || label

  if (normalized === '优秀' || normalized === 'Excellent') return 'success'
  if (normalized === '良好' || normalized === 'Good') return ''
  if (normalized === '中等' || normalized === 'Moderate') return 'warning'
  if (normalized === '及格' || normalized === 'Pass') return 'info'
  return 'danger'
}

  const radarLabels = computed(() => [
    t('studentScore.regularPerformance'),
    t('studentScore.testPerformance'),
    t('studentScore.labPerformance'),
    t('studentScore.finalPerformance'),
    t('common.totalScore')
  ])

const fetchScore = async () => {
    loading.value = true
    try {
        const u = JSON.parse(localStorage.getItem('user'))
        const res = await axios.get(`/api/scores/course/${courseId}/student/${u.id}`)
        score.value = res.data
        renderChart()
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const renderChart = () => {
   nextTick(() => {
       const ctx = document.getElementById('studentRadarChart');
       if (ctx && score.value) {
            new Chart(ctx, {
                type: 'radar',
                data: {
                labels: radarLabels.value,
                    datasets: [{
                  label: t('studentScore.achievementChart'),
                        data: [dailyScore.value, testAvg.value, labScore.value, score.value.finalExamScore, score.value.totalScore],
                        fill: true,
                        backgroundColor: 'rgba(64, 158, 255, 0.2)',
                        borderColor: 'rgb(64, 158, 255)',
                        pointBackgroundColor: 'rgb(64, 158, 255)',
                        pointBorderColor: '#fff',
                        pointHoverBackgroundColor: '#fff',
                        pointHoverBorderColor: 'rgb(64, 158, 255)'
                    }]
                },
                options: {
                    scales: { r: { min: 0, max: 100 } },
                    maintainAspectRatio: false
                }
            })
       }
   })
}

onMounted(() => {
    fetchScore()
})
</script>

<style scoped>
.student-score-view {
  gap: 20px;
}

.score-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  padding: 28px;
  background:
    linear-gradient(135deg, rgba(14, 165, 233, 0.08), rgba(16, 185, 129, 0.08)),
    rgba(255, 255, 255, 0.94);
}

.hero-eyebrow {
  color: #0284c7;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.score-hero h2 {
  margin: 10px 0 8px;
  font-size: 30px;
}

.score-hero p {
  margin: 0;
  color: #667085;
  max-width: 720px;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  gap: 12px;
  align-items: stretch;
}

.hero-chip {
  min-width: 118px;
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.84);
  border: 1px solid rgba(14, 165, 233, 0.08);
}

.hero-chip strong {
  display: block;
  font-size: 24px;
}

.hero-chip span {
  display: block;
  margin-top: 8px;
  font-size: 13px;
  color: #667085;
}

.mb-4 { margin-bottom: 24px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }

.card-title {
  font-weight: 700;
  font-size: 16px;
}

.card-subtitle {
  margin: 8px 0 0;
  color: #667085;
  font-size: 13px;
}

:deep(.highlight-label) {
  background-color: #f0f9eb !important;
}

:deep(.total-label) {
  background-color: #ecf5ff !important;
  color: #67c23a;
  font-weight: bold;
}

.calc-val {
  font-weight: bold;
  color: #409EFF;
}

.formula {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
  font-weight: normal;
}

.total-score {
  font-size: 24px;
  color: #67c23a;
}

.eval-box {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.eval-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.eval-title {
  color: #606266;
  font-weight: 500;
}

.chart-container {
  height: 280px;
}

@media (max-width: 768px) {
  .score-hero,
  .hero-actions,
  .card-header {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
