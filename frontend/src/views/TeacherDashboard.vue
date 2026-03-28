<template>
  <div class="teacher-dashboard page-shell">
    <div class="welcome-banner">
      <div class="welcome-text">
        <h2>{{ t('teacher.welcome') }}</h2>
        <p>{{ t('teacher.welcomeDesc') }}</p>
      </div>
      <div class="welcome-stats">
         <div class="stat-item">
            <span class="stat-value">{{ courses.length }}</span>
            <span class="stat-label">{{ t('teacher.semesterCourses') }}</span>
         </div>
      </div>
    </div>

    <div class="content-section">
      <div class="section-title">
        <span>{{ t('teacher.myCourses') }}</span>
      </div>

      <div v-if="courses.length" class="course-grid">
        <el-row :gutter="24">
          <el-col :xs="24" :sm="12" :lg="8" v-for="course in courses" :key="course.id" class="mb-4">
            <el-card shadow="hover" class="course-card">
              <div class="course-header">
                <div class="course-icon">
                   <el-icon><Reading /></el-icon>
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
                <el-button type="primary" plain class="action-btn" @click="goToScores(course.id)">
                  <el-icon><EditPen /></el-icon> {{ t('teacher.enterScores') }}
                </el-button>
                <el-button type="success" plain class="action-btn" @click="goToAnalysis(course.id)">
                  <el-icon><DataAnalysis /></el-icon> {{ t('teacher.viewAnalysis') }}
                </el-button>
                <el-button type="warning" plain class="action-btn" @click="goToWarnings(course.id)">
                  <el-icon><Warning /></el-icon> {{ t('teacher.viewWarnings') }}
                </el-button>
                <el-button type="info" plain class="action-btn" @click="goToMessages(course.id)">
                  <el-icon><ChatDotRound /></el-icon> {{ t('teacher.viewMessages') }}
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
      

      <el-empty v-else :description="t('teacher.noCourses')" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useUserStore } from '../store/user'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Reading, EditPen, DataAnalysis, Calendar, Location, Warning, ChatDotRound } from '@element-plus/icons-vue'

const courses = ref([])
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

  // 1) 中文格式: "周一 第1-2节" 或 "周一 1-2"
  let m = text.match(/^(周[一二三四五])\s*(?:第)?\s*(\d+\s*-\s*\d+)\s*(?:节)?$/)
  if (m) {
    return {
      day: normalizeDayKey(m[1]),
      slot: m[2].replace(/\s+/g, '')
    }
  }

  // 2) 英文格式: "Monday, Periods 7-8" / "Mon 7-8" / "Friday, 1-2"
  m = text.match(/^(Monday|Mon|Tuesday|Tue|Wednesday|Wed|Thursday|Thu|Friday|Fri)[,\s]*(?:Periods?)?\s*(\d+\s*-\s*\d+)$/i)
  if (m) {
    const dayRaw = m[1]
    const dayNorm = dayRaw.charAt(0).toUpperCase() + dayRaw.slice(1).toLowerCase()
    return {
      day: normalizeDayKey(dayNorm),
      slot: m[2].replace(/\s+/g, '')
    }
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

const userStore = useUserStore()
const router = useRouter()
const { t } = useI18n({ useScope: 'global' })

onMounted(async () => {
    let u = userStore.user
    if (!u) {
       const localU = localStorage.getItem('user')
       if (localU) {
          u = JSON.parse(localU)
       } else {
          router.push('/login')
          return
       }
    }
    const res = await axios.get(`/api/courses/teacher/${u.id}`)
    courses.value = res.data
})

const goToScores = (courseId) => {
    router.push(`/course/${courseId}/scores`)
}

const goToAnalysis = (courseId) => {
    router.push(`/course/${courseId}/analysis`)
}

const goToWarnings = (courseId) => {
  router.push(`/course/${courseId}/warnings`)
}

const goToMessages = (courseId) => {
  router.push(`/course/${courseId}/messages`)
}
</script>

<style scoped>
.teacher-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-banner {
  background: linear-gradient(135deg, #134e4a 0%, #0f766e 48%, #115e59 100%);
  border-radius: 28px;
  padding: 32px 36px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 20px 40px rgba(15, 118, 110, 0.18);
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
  background: rgba(255, 255, 255, 0.12);
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
  opacity: 0.85;
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
  height: 20px;
  background: linear-gradient(180deg, #0f766e, #14b8a6);
  margin-right: 10px;
  border-radius: 999px;
}

.mb-4 {
  margin-bottom: 24px;
}

.course-card {
  border-radius: 22px;
  border: 1px solid rgba(15, 118, 110, 0.08);
  transition: all 0.3s;
  height: 100%;
}
.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 36px rgba(15, 118, 110, 0.12) !important;
}

.course-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  position: relative;
}

.course-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, rgba(15, 118, 110, 0.14), rgba(20, 184, 166, 0.14));
  color: #0f766e;
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
  margin: 0 0 5px 0;
  font-size: 16px;
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

.course-body {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  align-items: center;
  color: #606266;
  font-size: 13px;
  margin-bottom: 8px;
}
.info-row:last-child {
  margin-bottom: 0;
}
.info-row .el-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #909399;
}

.course-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.action-btn {
  flex: 1 1 calc(50% - 6px);
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
}

.timetable-cell {
  background-color: #f0fdfa;
  padding: 10px;
  border-radius: 12px;
  border-left: 4px solid #14b8a6;
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
  .course-actions {
    width: 100%;
  }

  .stat-item,
  .action-btn {
    width: 100%;
  }
}

</style>
