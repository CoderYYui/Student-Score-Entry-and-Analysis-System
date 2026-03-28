<template>
  <div class="student-dashboard page-shell">
    <div class="welcome-banner">
      <div class="welcome-text">
        <h2>{{ t('student.welcome') }}</h2>
        <p>{{ t('student.welcomeDesc') }}</p>
      </div>
      <div class="welcome-stats">
         <div class="stat-item">
            <span class="stat-value">{{ courses.length }}</span>
            <span class="stat-label">{{ t('student.myCoursesCount') }}</span>
         </div>
      </div>
    </div>

    <div class="content-section">
      <div v-if="warnings.length" class="warning-section">
        <div class="section-title">
          <span>{{ t('student.warningCardTitle') }}</span>
        </div>
        <el-row :gutter="16" class="mb-4">
          <el-col :xs="24" :md="12" v-for="warning in warnings" :key="warning.courseId">
            <el-card class="warning-card" shadow="hover">
              <div class="warning-head">
                <strong>{{ warning.courseName }}</strong>
                <el-tag type="danger">{{ t('student.warningTag') }}</el-tag>
              </div>
              <div class="warning-body">
                <div>{{ t('student.warningReason') }}：{{ warning.riskItems.join('、') }}</div>
                <!-- <div style="margin-top: 8px;">{{ t('student.warningAdvice') }}：{{ warning.communicationAdvice }}</div> -->
              </div>
              <el-button type="danger" plain size="small" @click="goToWarnings">{{ t('common.viewDetail') }}</el-button>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div class="section-title">
        <span>{{ t('student.enrolledCourses') }}</span>
      </div>

      <div v-if="courses.length" class="course-list">
        <el-row :gutter="24">
          <el-col :xs="24" :sm="12" :lg="8" v-for="course in courses" :key="course.id" class="mb-4">
            <el-card shadow="hover" class="course-card">
              <div class="course-header">
                <div class="course-icon">
                   <el-icon><Notebook /></el-icon>
                </div>
                <div class="course-title-group">
                  <h3 class="course-title" :title="course.courseName">{{ course.courseName }}</h3>
                  <span class="course-code">{{ course.courseCode }}</span>
                </div>
                <el-tag effect="light" type="info" size="small" class="semester-tag">{{ course.semester }}</el-tag>
              </div>
              <div class="course-schedule" style="margin: 10px 0; font-size: 13px; color: #666;">
                 <div><el-icon><Calendar /></el-icon> {{ t('course.time') }}: {{ course.classTime || t('course.tbd') }}</div>
                 <div style="margin-top:4px;"><el-icon><Location /></el-icon> {{ t('course.location') }}: {{ course.location || t('course.tbd') }}</div>
              </div>
              <div class="course-actions">
                <el-button color="#409EFF" plain class="action-btn" @click="viewScores(course.id)">
                  <el-icon><Trophy /></el-icon> {{ t('student.viewScores') }}
                </el-button>
                <el-button type="success" plain class="action-btn mt-2" @click="fillSurvey(course.id)">
                  <el-icon><Tickets /></el-icon> {{ t('student.fillSurvey') }}
                </el-button>
                <el-button type="warning" plain class="action-btn" @click="askQuestion(course)">
                  <el-icon><ChatDotRound /></el-icon> {{ t('student.askQuestion') }}
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <div class="section-title" style="margin-top: 30px;">
        <span>{{ t('course.timetable') }}</span>
      </div>
      <el-card shadow="never" class="mb-4">
        <el-table :data="timetableData" border stripe style="width: 100%">
          <el-table-column prop="slot" :label="t('course.slotDay')" width="120" align="center" header-align="center">
            <template #default="{ row }">
              <strong>{{ row.slot }}</strong>
            </template>
          </el-table-column>
          <el-table-column v-for="day in dayColumns" :key="day.key" :prop="day.key" :label="day.label" align="center" min-width="150">
            <template #default="{ row }">
              <div v-if="row[day.key]" class="timetable-cell">
                <div class="t-course">{{ row[day.key].courseName }}</div>
                <div class="t-loc"><el-icon><Location /></el-icon> {{ row[day.key].location || t('course.tbd') }}</div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      </div>
      

      <el-empty v-else :description="t('student.noCourses')" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Notebook, Trophy, Tickets, Calendar, Location, ChatDotRound } from '@element-plus/icons-vue'

const courses = ref([])
const warnings = ref([])
const dayColumns = computed(() => [
  { key: '周一', label: t('course.days.mon') },
  { key: '周二', label: t('course.days.tue') },
  { key: '周三', label: t('course.days.wed') },
  { key: '周四', label: t('course.days.thu') },
  { key: '周五', label: t('course.days.fri') }
])



const timeSlots = computed(() => [
  t('course.timeSlots.slot1'),
  t('course.timeSlots.slot2'),
  t('course.timeSlots.slot3'),
  t('course.timeSlots.slot4'),
  t('course.timeSlots.slot5')
])

const normalizeSlot = (slotText) => {
  if (!slotText) {
    return ''
  }
  return slotText.replace('第', '').replace('节', '')
}

const normalizeDayKey = (dayText = '') => {
  const day = String(dayText).trim()
  const dayMap = {
    '周一': '周一', '周二': '周二', '周三': '周三', '周四': '周四', '周五': '周五',
    'Mon': '周一', 'Monday': '周一',
    'Tue': '周二', 'Tuesday': '周二',
    'Wed': '周三', 'Wednesday': '周三',
    'Thu': '周四', 'Thursday': '周四',
    'Fri': '周五', 'Friday': '周五'
  }
  const normalized = day.replace(',', '')
  return dayMap[normalized] || ''
}

const parseClassTime = (classTime) => {
  if (!classTime) return null
  const text = String(classTime).trim()

  // 中文: "周一 第1-2节" / "周一 1-2"
  let m = text.match(/^(周[一二三四五])\s*(?:第)?\s*(\d+\s*-\s*\d+)\s*(?:节)?$/)
  if (m) {
    return { day: normalizeDayKey(m[1]), slot: m[2].replace(/\s+/g, '') }
  }

  // 英文: "Monday, Periods 7-8" / "Mon 7-8" / "Friday, 1-2"
  m = text.match(/^(Monday|Mon|Tuesday|Tue|Wednesday|Wed|Thursday|Thu|Friday|Fri)[,\s]*(?:Periods?)?\s*(\d+\s*-\s*\d+)$/i)
  if (m) {
    const dayRaw = m[1]
    const dayNorm = dayRaw.charAt(0).toUpperCase() + dayRaw.slice(1).toLowerCase()
    return { day: normalizeDayKey(dayNorm), slot: m[2].replace(/\s+/g, '') }
  }

  return null
}

const timetableData = computed(() => {
  const data = timeSlots.value.map(slot => ({
    slot,
    '周一': null,
    '周二': null,
    '周三': null,
    '周四': null,
    '周五': null
  }));

  courses.value.forEach(course => {
  const parsed = parseClassTime(course.classTime)
  if (!parsed) return

  const row = data.find(r => normalizeSlot(r.slot) === parsed.slot)
  if (row && Object.prototype.hasOwnProperty.call(row, parsed.day)) {
      row[parsed.day] = course
    }
  })


  return data;
});

const router = useRouter()
const { t } = useI18n({ useScope: 'global' })

const fetchEnrolledCourses = async () => {
    try {
        const uStr = localStorage.getItem('user')
        if (!uStr) {
           router.push('/login')
           return
        }
        const u = JSON.parse(uStr)
        const res = await axios.get(`/api/courses/student/${u.id}`)
        courses.value = res.data
    } catch (e) {
        console.error(e)
    }
}

const viewScores = (courseId) => {
    router.push(`/course/${courseId}/my-score`)
}

const fillSurvey = (courseId) => {
    router.push(`/survey/${courseId}`)
}

const askQuestion = (course) => {
  router.push(`/course/${course.id}/messages?teacherId=${course.teacherId}`)
}

const goToWarnings = () => {
  router.push('/warnings')
}

const fetchWarnings = async () => {
  const uStr = localStorage.getItem('user')
  if (!uStr) return
  const u = JSON.parse(uStr)
  const res = await axios.get(`/api/warnings/student/${u.id}`)
  warnings.value = res.data
}

onMounted(() => {
    fetchEnrolledCourses()
  fetchWarnings()
})
</script>

<style scoped>
.student-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-banner {
  background: linear-gradient(135deg, #0f766e 0%, #16a34a 100%);
  border-radius: 28px;
  padding: 32px 36px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 20px 40px rgba(22, 163, 74, 0.16);
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 15px;
  opacity: 0.88;
  max-width: 720px;
  line-height: 1.7;
}

.welcome-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 140px;
  background: rgba(255, 255, 255, 0.14);
  padding: 18px 26px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
}

.section-title {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}
.section-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 18px;
  background: linear-gradient(180deg, #16a34a, #34d399);
  margin-right: 10px;
  border-radius: 999px;
}

.mb-4 {
  margin-bottom: 24px;
}

.warning-card {
  border-left: 4px solid #f56c6c;
  border-radius: 22px;
}

.warning-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.warning-body {
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
}

.course-card {
  border-radius: 22px;
  border: 1px solid rgba(22, 163, 74, 0.08);
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 18px 36px rgba(22, 163, 74, 0.12) !important;
  border-color: #3cba92;
}

.course-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 25px;
  position: relative;
}

.course-icon {
  width: 46px;
  height: 46px;
  background: linear-gradient(135deg, rgba(22, 163, 74, 0.14), rgba(52, 211, 153, 0.18));
  color: #16a34a;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  margin-right: 15px;
}

.course-title-group {
  flex: 1;
  overflow: hidden;
}

.course-title {
  margin: 0 0 6px 0;
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-code {
  font-size: 13px;
  color: #909399;
}

.semester-tag {
  position: absolute;
  right: 0;
  top: 0;
}

.course-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: auto;
}

.action-btn {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
  height: 40px;
}

.mt-2 {
  margin-top: 8px;
}

.timetable-cell {
  background-color: #f0fdf4;
  padding: 10px;
  border-radius: 12px;
  border-left: 4px solid #16a34a;
  text-align: left;
}
.t-course {
  font-weight: bold;
  font-size: 13px;
  color: #303133;
  margin-bottom: 4px;
}
.t-loc {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 768px) {
  .welcome-banner {
    flex-direction: column;
    align-items: flex-start;
    gap: 18px;
    padding: 26px 22px;
  }

  .welcome-stats,
  .stat-item {
    width: 100%;
  }
}

</style>
