<template>
  <div class="analysis-container page-shell">
    <section class="analysis-hero surface-card">
      <div>
        <div class="hero-eyebrow">Insight Center</div>
        <h2>{{ t('analysis.title') }}</h2>
        <p>Aggregate score distribution, objective achievement, and satisfaction data to quickly identify teaching improvement opportunities.</p>
      </div>
      <div class="hero-actions">
        <div class="hero-chip">
          <strong>{{ totalStudents }}</strong>
          <span>样本人数</span>
        </div>
        <div class="hero-chip">
          <strong>{{ objectiveCount }}</strong>
          <span>{{ t('analysis.objective') }}</span>
        </div>
        <el-button type="primary" @click="downloadReport" :loading="downloading">{{ t('analysis.generateBtn') }}</el-button>
      </div>
    </section>

    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="stat-title">Pass rate</div>
          <div class="stat-value dark">{{ passRate }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="stat-title">Average achievement rate</div>
          <div class="stat-value dark">{{ averageAchievement }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="stat-title">Questionnaire sample</div>
          <div class="stat-value dark">{{ surveyTotal }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="stat-title">High-risk zone</div>
          <div class="stat-value dark">{{ riskBucketCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!hasAnalysisData" description="当前课程还没有可用于分析的成绩数据" class="surface-card empty-state" />

    <template v-else>

    <!-- 顶栏统计 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card blue">
          <div class="stat-title">{{ t('analysis.sMax') }}</div>
          <div class="stat-value">{{ formatStat(courseStats.max_score) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card green">
          <div class="stat-title">{{ t('analysis.sMin') }}</div>
          <div class="stat-value">{{ formatStat(courseStats.min_score) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card orange">
          <div class="stat-title">{{ t('analysis.sAvg') }}</div>
          <div class="stat-value">{{ formatStat(courseStats.avg_score, 2) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card purple">
          <div class="stat-title">{{ t('analysis.sDev') }}</div>
          <div class="stat-value">{{ formatStat(courseStats.std_dev, 2) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 直接评价结果分布情况 -->
    <el-card shadow="always" class="mb-4 analysis-section">
      <template #header><div class="section-title">{{ t('analysis.sectionDirect') }}</div></template>
      <el-table :data="directEvalTable" stripe style="width: 100%" border>
        <el-table-column prop="id" :label="t('analysis.tId')"  width="80" align="center" />
        <el-table-column prop="method" :label="t('analysis.tMethod')"  width="120" align="center" />
        <el-table-column prop="excellent" :label="t('analysis.tL1')"  align="center" />
        <el-table-column prop="good" :label="t('analysis.tL2')"  align="center" />
        <el-table-column prop="moderate" :label="t('analysis.tL3')"  align="center" />
        <el-table-column prop="pass" :label="t('analysis.tL4')"  align="center" />
        <el-table-column prop="fail" :label="t('analysis.tL5')"  align="center" />
      </el-table>
    </el-card>

    <!-- 总体评价结果 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :xl="12">
        <el-card shadow="always" class="analysis-section" style="height: 100%;">
          <template #header><div class="section-title">{{ t('analysis.sectionOverall') }}</div></template>
          <el-table :data="overallEvalTable" stripe border>
            <el-table-column prop="range" :label="t('analysis.scoreRange')" />
            <el-table-column prop="r90" label="90~100" align="center" />
            <el-table-column prop="r80" label="80~89" align="center" />
            <el-table-column prop="r70" label="70~79" align="center" />
            <el-table-column prop="r60" label="60~69" align="center" />
            <el-table-column prop="r50" :label="t('analysis.below59')" align="center" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :xl="12">
        <el-card shadow="always" class="analysis-section" style="height: 100%;">
          <template #header><div class="section-title">{{ t('analysis.indirectSection') }}</div></template>
          <el-table :data="indirectEvalTable" stripe border>
            <el-table-column prop="obj" :label="t('analysis.objective')" />
            <el-table-column prop="vSatisfied" :label="t('analysis.verySatisfied')" align="center" />
            <el-table-column prop="satisfied" :label="t('analysis.compareSatisfied')" align="center" />
            <el-table-column prop="ok" :label="t('analysis.satisfied')" align="center" />
            <el-table-column prop="basic" :label="t('analysis.basicSatisfied')" align="center" />
            <el-table-column prop="un" :label="t('analysis.unsatisfied')" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 达成度分析 -->
    <el-card shadow="always" class="mb-4 analysis-section">
      <template #header><div class="section-title">{{ t('analysis.objectiveSummary') }}</div></template>
      <el-row :gutter="20">
        <el-col :xs="24" :xl="6">
          <el-table :data="objAchieveTable" stripe border>
            <el-table-column prop="obj" :label="t('analysis.objective')" />
            <el-table-column prop="val" :label="t('analysis.finalAchievement')" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.val > 0.75 ? 'success' : 'warning'">{{ scope.row.val }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
            <div style="margin-top:20px;">
                <canvas id="achieveChart" width="400" height="300"></canvas>
            </div>
        </el-col>
        <el-col :xs="24" :xl="18">
          <el-table :data="studentAchieveTable" stripe border>
            <el-table-column prop="obj" :label="t('analysis.objectiveRange')" />
            <el-table-column prop="r0" label="[0,0.6)" align="center" />
            <el-table-column prop="r6" label="[0.6,0.7)" align="center" />
            <el-table-column prop="r7" label="[0.7,0.8)" align="center" />
            <el-table-column prop="r8" label="[0.8,0.9)" align="center" />
            <el-table-column prop="r9" label="[0.9,1.0]" align="center" />
          </el-table>
          
          <!-- AI 分析结论总结 -->
          <div class="ai-report-box">
             <h4>{{ t('analysis.aiSummary') }}</h4>
             <p>{{ t('analysis.reportIntro') }}<strong>{{ averageAchievement }}</strong>。</p>
             <p>{{ t('analysis.reportFindings') }}</p>
             <ul>
               <li>{{ summaryLine1 }}</li>
               <li>{{ summaryLine2 }}</li>
               <li>{{ summaryLine3 }}</li>
             </ul>
          </div>
        </el-col>
      </el-row>
    </el-card>

    </template>

  </div>
</template>

<script setup>
import { computed, ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import Chart from 'chart.js/auto'
import { ElMessage } from 'element-plus'

const route = useRoute()
const { t } = useI18n({ useScope: 'global' })
const courseStats = ref({})
const downloading = ref(false)
const chartInstance = ref(null)

// Hardcoded explicit values for the very specific use case scenario requested by the user
const directEvalTable = ref([])

const overallEvalTable = ref([])

const indirectEvalTable = ref([])

const objAchieveTable = ref([])

const studentAchieveTable = ref([])

const objectiveCount = computed(() => objAchieveTable.value.length)
const totalStudents = computed(() => Number(courseStats.value.total_students || 0))
const surveyTotal = computed(() => Number(indirectCounts.value.total || 0))
const averageAchievement = computed(() => {
  if (!objAchieveTable.value.length) {
    return '--'
  }
  const total = objAchieveTable.value.reduce((sum, item) => sum + Number(item.rawVal || 0), 0)
  return (total / objAchieveTable.value.length).toFixed(4)
})
const passRate = computed(() => {
  if (!totalStudents.value) {
    return '--'
  }
  const scoreRanges = overallEvalRaw.value
  const passCount = scoreRanges.reduce((sum, item) => sum + (item.score_range === '0-59' ? 0 : Number(item.student_count || 0)), 0)
  return `${((passCount / totalStudents.value) * 100).toFixed(1)}%`
})
const riskBucketCount = computed(() => {
  const failBucket = overallEvalRaw.value.find(item => item.score_range === '0-59')
  return failBucket ? `${failBucket.student_count} people` : '0 people'
})
const hasAnalysisData = computed(() => totalStudents.value > 0)
const overallEvalRaw = ref([])
const indirectCounts = ref({ counts: {}, total: 0 })
const summaryLine1 = computed(() => {
  if (!totalStudents.value) {
    return 'No sufficient score samples are available for analysis yet.'
  }
  return `This course includes ${totalStudents.value} student samples. The average score is ${formatStat(courseStats.value.avg_score, 2)}, and the overall pass rate is ${passRate.value}.`
})
const summaryLine2 = computed(() => {
  if (averageAchievement.value === '--') {
    return 'Objective achievement will be generated automatically after scores are entered.'
  }
  return `The average objective achievement is ${averageAchievement.value}. You may use the direct-evaluation distribution to pinpoint weak areas.`
})
const summaryLine3 = computed(() => {
  if (!surveyTotal.value) {
    return 'No satisfaction survey samples are available yet. Indirect evaluation will be completed after students submit surveys.'
  }
  return `${surveyTotal.value} satisfaction surveys have been collected and included in the indirect evaluation results.`
})

const downloadReport = async () => {
    downloading.value = true
    try {
        const res = await fetch(`/api/analysis/report/${route.params.id}/download`, { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } })
        if (res.ok) {
            const blob = await res.blob()
            const url = window.URL.createObjectURL(blob)
            const a = document.createElement('a')
            a.href = url
            a.download = `Course_Report_${route.params.id}.pdf`
            document.body.appendChild(a)
            a.click()
            a.remove()
            window.URL.revokeObjectURL(url)
            ElMessage.success(t('analysis.downloadSuccess'))
        } else {
             ElMessage.warning(t('analysis.downloadWait'))
        }
    } catch {
        // Fallback for demo UX
        ElMessage.info(t('analysis.downloadWait'))
    } finally {
        downloading.value = false
    }
}

onMounted(() => {
    fetchData()
  })
  
  
  const fetchData = async () => {
    try {
      const res = await axios.get(`/api/analysis/course/${route.params.id}/full`)
      const data = res.data
      if (data.courseStats) {
        courseStats.value = data.courseStats
      }
      
      if (data.directEvaluation) {
          directEvalTable.value = data.directEvaluation.map((d, i) => ({
            id: i+1, 
            method: translateAssessmentMethod(d.assessment_method),
            excellent: appendPeople(d.excellent || 0),
            good: appendPeople(d.good || 0),
            moderate: appendPeople(d.medium || 0),
            pass: appendPeople(d.pass || 0),
            fail: appendPeople(d.fail || 0)
          }))
        }
      
      if (data.scoreDistribution) {
          let sdArray = Array.isArray(data.scoreDistribution) ? data.scoreDistribution : [];
          overallEvalRaw.value = sdArray
          let getCount = (range) => {
              let found = sdArray.find(item => item.score_range === range);
              return appendPeople(found ? found.student_count : 0);
          }
          overallEvalTable.value = [{
            range: t('analysis.scoreRange'),
            r90: getCount('90-100'),
            r80: getCount('80-89'),
            r70: getCount('70-79'),
            r60: getCount('60-69'),
            r50: getCount('0-59')
          }]
        }

      if (data.indirectEvaluation && data.indirectEvaluation.counts) {
          let c = data.indirectEvaluation.counts;
          indirectCounts.value = data.indirectEvaluation
          indirectEvalTable.value = [{
            obj: t('analysis.overallSatisfaction'),
            vSatisfied: appendPeople(c.VERY_SATISFIED || 0),
            satisfied: appendPeople(c.SATISFIED || 0),
            ok: appendPeople(0),
            basic: appendPeople(c.NEUTRAL || 0),
            un: appendPeople(c.DISSATISFIED || 0)
          }]
        }

      if (data.objectiveAchievement) {
           objAchieveTable.value = Object.keys(data.objectiveAchievement).map(key => {
             return {
                 obj: key.replace('objective_', `${t('analysis.objective')}`),
                 rawVal: Number(data.objectiveAchievement[key] || 0),
                 val: parseFloat(data.objectiveAchievement[key]).toFixed(4)
             }
           })
        }

      if (data.objectiveDistribution) {
          studentAchieveTable.value = Object.keys(data.objectiveDistribution).map(key => {
             let dist = data.objectiveDistribution[key];
             return {
                 obj: key.replace('Objective ', `${t('analysis.objective')}`),
                 r0: appendPeople(dist['0-0.6'] || 0),
                 r6: appendPeople(dist['0.6-0.7'] || 0),
                 r7: appendPeople(dist['0.7-0.8'] || 0),
                 r8: appendPeople(dist['0.8-0.9'] || 0),
                 r9: appendPeople(dist['0.9-1.0'] || 0)
             }
          })
        }

      initChart()
    } catch (e) {
      console.error(e)
    }
  }

const appendPeople = (count) => `${count}${t('analysis.peopleSuffix')}`

const formatStat = (value, digits = 0) => {
  if (value === null || value === undefined || value === '') {
    return '--'
  }
  const numericValue = Number(value)
  if (Number.isNaN(numericValue)) {
    return '--'
  }
  return digits > 0 ? numericValue.toFixed(digits) : numericValue.toFixed(0)
}

const initChart = () => {
  nextTick(() => {
    const canvas = document.getElementById('achieveChart')
    if (!canvas || !objAchieveTable.value.length) {
      return
    }
    if (chartInstance.value) {
      chartInstance.value.destroy()
    }
    chartInstance.value = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: objAchieveTable.value.map(item => item.obj),
        datasets: [{
          label: t('analysis.finalAchievement'),
          data: objAchieveTable.value.map(item => Number(item.rawVal || 0)),
          backgroundColor: ['#2563eb', '#0ea5e9', '#14b8a6', '#f59e0b'],
          borderRadius: 10,
          maxBarThickness: 46
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            min: 0,
            max: 1,
            ticks: {
              callback: (value) => Number(value).toFixed(1)
            }
          }
        },
        plugins: {
          legend: {
            display: false
          }
        }
      }
    })
  })
}

const translateAssessmentMethod = (method) => {
  if (typeof method !== 'string') return method
  return method
    .replace('Regular', t('analysis.regularMethod'))
    .replace('Tests', t('analysis.testsMethod'))
    .replace('Lab', t('analysis.labMethod'))
    .replace('Final Exam', t('analysis.finalExamMethod'))
}


</script>

<style scoped>
.analysis-container {
  gap: 20px;
}

.analysis-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  padding: 28px;
  background:
    linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(249, 115, 22, 0.08)),
    rgba(255, 255, 255, 0.94);
}

.hero-eyebrow {
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.analysis-hero h2 {
  margin: 10px 0 8px;
  font-size: 30px;
}

.analysis-hero p {
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
  border: 1px solid rgba(37, 99, 235, 0.08);
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

.stat-card {
  text-align: center;
  border: none;
  border-radius: 22px;
  color: white;
}
.summary-card {
  border-radius: 22px;
  border: 1px solid rgba(37, 99, 235, 0.08);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.96));
}
.stat-card.blue { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-card.green { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.stat-card.orange { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.stat-card.purple { background: linear-gradient(135deg, #c471f5 0%, #fa71cd 100%); }

.stat-title { font-size: 14px; opacity: 0.9; margin-bottom: 10px; }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-value.dark { color: #0f172a; }

.empty-state {
  padding: 36px;
}

.section-title {
  font-size: 17px;
  font-weight: 700;
  color: #303133;
  border-left: 4px solid #2563eb;
  padding-left: 10px;
}

.ai-report-box {
  background: linear-gradient(180deg, #f8fbff, #f4f7fb);
  border-radius: 18px;
  padding: 18px 20px;
  margin-top: 20px;
  border: 1px solid #dbeafe;
}
.ai-report-box h4 {
  margin-top: 0;
  color: #409EFF;
  display: flex;
  align-items: center;
}
.ai-report-box h4::before {
  content: '✨';
  margin-right: 8px;
}
.ai-report-box p {
  line-height: 1.6;
  color: #606266;
  margin-bottom: 10px;
}
.ai-report-box ul {
  padding-left: 20px;
  color: #606266;
  line-height: 1.8;
}

@media (max-width: 768px) {
  .analysis-hero,
  .hero-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>

