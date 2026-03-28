<template>
    <div class="score-entry page-shell">
        <section class="score-hero surface-card">
            <div>
                <div class="hero-eyebrow">Course Workflow</div>
                <h2>{{ t('score.title') }}</h2>
                <p>Batch-enter course scores, automatically update total results, and track absences and distribution in real time.</p>
            </div>
            <div class="hero-stats">
                <div class="hero-stat">
                    <span>{{ scores.length }}</span>
                    <label>{{ t('score.student') }}</label>
                </div>
                <div class="hero-stat">
                    <span>{{ gradedCount }}</span>
                    <label>{{ t('score.total') }}</label>
                </div>
                <div class="hero-stat">
                    <span>{{ averageTotal }}</span>
                    <label>平均分</label>
                </div>
            </div>
        </section>

        <el-card class="surface-card score-table-card">
            <template #header>
                <div class="card-header">
                    <div>
                        <h3>{{ t('score.title') }}</h3>
                        <p>Edit scoring dimensions directly in the table. The system auto-saves and recalculates grades.</p>
                    </div>
                    <div class="header-actions">
                        <el-tag v-if="saveStatus" :type="saveStatusType" effect="light">{{ saveStatus }}</el-tag>
                        <el-button type="primary" @click="handleInit" :loading="initLoading">Initialize a blank document</el-button>
                    </div>
                </div>
            </template>

            <template v-if="!isCompact">
            <el-table
                :data="scores"
                class="score-table"
                style="width: 100%"
                v-loading="loading"
                border
                stripe
                height="600"
            >
        <el-table-column fixed prop="studentName" :label="t('score.student')" width="120" />
        
        <el-table-column :label="t('score.attend')" width="100">
            <template #default="scope">
                <el-input-number v-model="scope.row.attendanceScore" :min="0" :max="100" :step="1" size="small" @change="updateScore(scope.row)" />
            </template>
        </el-table-column>
        
        <el-table-column :label="t('score.perf')" width="100">
            <template #default="scope">
                <el-input-number v-model="scope.row.performanceScore" :min="0" :max="100" size="small" @change="updateScore(scope.row)" />
            </template>
        </el-table-column>
        
        <el-table-column :label="t('score.hw')" width="100">
            <template #default="scope">
                <el-input-number v-model="scope.row.homeworkScore" :min="0" :max="100" size="small" @change="updateScore(scope.row)" />
            </template>
        </el-table-column>

        <el-table-column label="Quiz scores" align="center">
            <el-table-column :label="t('score.test') + ' 1'" width="100">
                <template #default="scope">
                    <el-input-number v-model="scope.row.test1Score" :min="0" :max="100" size="small" @change="updateScore(scope.row)" />
                </template>
            </el-table-column>
            <el-table-column :label="t('score.test') + ' 2'" width="100">
                <template #default="scope">
                    <el-input-number v-model="scope.row.test2Score" :min="0" :max="100" size="small" @change="updateScore(scope.row)" />
                </template>
            </el-table-column>
            <el-table-column :label="t('score.test') + ' 3'" width="100">
                <template #default="scope">
                    <el-input-number v-model="scope.row.test3Score" :min="0" :max="100" size="small" @change="updateScore(scope.row)" />
                </template>
            </el-table-column>
            <el-table-column :label="t('score.test') + ' 4'" width="100">
                <template #default="scope">
                    <el-input-number v-model="scope.row.test4Score" :min="0" :max="100" size="small" @change="updateScore(scope.row)" />
                </template>
            </el-table-column>
        </el-table-column>

        <el-table-column label="Experimental results" align="center">
            <el-table-column :label="t('score.labAtt')" width="100">
                <template #default="scope">
                    <el-input-number v-model="scope.row.labAttendanceScore" :min="0" :max="100" size="small" @change="updateScore(scope.row)" />
                </template>
            </el-table-column>
            <el-table-column :label="t('score.labRep')" width="100">
                <template #default="scope">
                    <el-input-number v-model="scope.row.labReportScore" :min="0" :max="100" size="small" @change="updateScore(scope.row)" />
                </template>
            </el-table-column>
        </el-table-column>

        <el-table-column :label="t('score.final')" width="100">
            <template #default="scope">
                <el-input-number v-model="scope.row.finalExamScore" :min="0" :max="100" size="small" @change="updateScore(scope.row)" />
            </template>
        </el-table-column>

        <el-table-column label="Number of absences" width="100">
            <template #default="scope">
                <el-input-number v-model="scope.row.absenceCount" :min="0" :max="20" size="small" @change="updateScore(scope.row)" />
            </template>
        </el-table-column>

        <el-table-column prop="totalScore" :label="t('score.total')" width="80" fixed="right">
            <template #default="scope">
                <strong>{{ scope.row.totalScore ? scope.row.totalScore.toFixed(1) : '-' }}</strong>
            </template>
        </el-table-column>
        
        <el-table-column prop="gradeLabel" :label="t('score.grade')" width="80" fixed="right">
        <template #default="scope">
            <el-tag :type="getGradeType(scope.row.gradeLabel)">{{ formatGradeLabel(scope.row.gradeLabel) }}</el-tag>
        </template>
        </el-table-column>
            </el-table>
            </template>

            <div v-else class="mobile-score-list">
                <el-card v-for="item in scores" :key="item.id || item.studentId" class="score-mobile-card" shadow="never">
                    <div class="mobile-card-head">
                        <div>
                            <strong>{{ item.studentName }}</strong>
                            <div class="mobile-subline">ID: {{ item.studentId }}</div>
                        </div>
                        <div class="mobile-score-summary">
                            <span>{{ item.totalScore ? item.totalScore.toFixed(1) : '-' }}</span>
                            <el-tag :type="getGradeType(item.gradeLabel)">{{ formatGradeLabel(item.gradeLabel) || '-' }}</el-tag>
                        </div>
                    </div>

                    <div class="mobile-field-grid">
                        <div class="field-block">
                            <label>{{ t('score.attend') }}</label>
                            <el-input-number v-model="item.attendanceScore" :min="0" :max="100" size="small" @change="updateScore(item)" />
                        </div>
                        <div class="field-block">
                            <label>{{ t('score.perf') }}</label>
                            <el-input-number v-model="item.performanceScore" :min="0" :max="100" size="small" @change="updateScore(item)" />
                        </div>
                        <div class="field-block">
                            <label>{{ t('score.hw') }}</label>
                            <el-input-number v-model="item.homeworkScore" :min="0" :max="100" size="small" @change="updateScore(item)" />
                        </div>
                        <div class="field-block">
                            <label>{{ t('score.final') }}</label>
                            <el-input-number v-model="item.finalExamScore" :min="0" :max="100" size="small" @change="updateScore(item)" />
                        </div>
                        <div class="field-block">
                            <label>Number of absences</label>
                            <el-input-number v-model="item.absenceCount" :min="0" :max="20" size="small" @change="updateScore(item)" />
                        </div>
                    </div>

                    <div class="mobile-group-title">测验成绩</div>
                    <div class="mobile-field-grid compact-grid">
                        <div class="field-block"><label>{{ t('score.test') }} 1</label><el-input-number v-model="item.test1Score" :min="0" :max="100" size="small" @change="updateScore(item)" /></div>
                        <div class="field-block"><label>{{ t('score.test') }} 2</label><el-input-number v-model="item.test2Score" :min="0" :max="100" size="small" @change="updateScore(item)" /></div>
                        <div class="field-block"><label>{{ t('score.test') }} 3</label><el-input-number v-model="item.test3Score" :min="0" :max="100" size="small" @change="updateScore(item)" /></div>
                        <div class="field-block"><label>{{ t('score.test') }} 4</label><el-input-number v-model="item.test4Score" :min="0" :max="100" size="small" @change="updateScore(item)" /></div>
                    </div>

                    <div class="mobile-group-title">实验成绩</div>
                    <div class="mobile-field-grid compact-grid">
                        <div class="field-block"><label>{{ t('score.labAtt') }}</label><el-input-number v-model="item.labAttendanceScore" :min="0" :max="100" size="small" @change="updateScore(item)" /></div>
                        <div class="field-block"><label>{{ t('score.labRep') }}</label><el-input-number v-model="item.labReportScore" :min="0" :max="100" size="small" @change="updateScore(item)" /></div>
                    </div>
                </el-card>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, computed, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'

const scores = ref([])
const route = useRoute()
const { t } = useI18n({ useScope: 'global' })
const courseId = route.params.id
const loading = ref(false)
const initLoading = ref(false)
const saveStatus = ref('')
const saveStatusType = ref('info')
const isCompact = ref(window.innerWidth < 768)

const gradedCount = computed(() => scores.value.filter(item => item.totalScore !== null && item.totalScore !== undefined).length)

const averageTotal = computed(() => {
    const validScores = scores.value
        .map(item => Number(item.totalScore))
        .filter(item => !Number.isNaN(item))
    if (!validScores.length) return '-'
    return (validScores.reduce((sum, item) => sum + item, 0) / validScores.length).toFixed(1)
})

const fetchScores = async () => {
    loading.value = true
    try {
        const res = await axios.get(`/api/scores/course/${courseId}`)
        scores.value = res.data
    } catch (e) {
        ElMessage.error(t('score.error'))
    } finally {
        loading.value = false
    }
}

const handleInit = async () => {
    initLoading.value = true
    try {
        await axios.post(`/api/scores/course/${courseId}/reset`)
        ElMessage.success('Initialization successful! The latest student roster has been synced.')
        await fetchScores()
    } catch(e) {
        ElMessage.error('初始化失败')
    } finally {
        initLoading.value = false
    }
}

const updateScore = async (score) => {
    saveStatus.value = t('score.saving')
    saveStatusType.value = 'warning'
    try {
        const res = await axios.post('/api/scores/update', score)
        // Update local data with calculations from server
        Object.assign(score, res.data)
        saveStatus.value = t('score.saved')
        saveStatusType.value = 'success'
        setTimeout(() => saveStatus.value = '', 2000)
    } catch (e) {
        saveStatus.value = t('score.error')
        saveStatusType.value = 'danger'
        ElMessage.error(t('score.error'))
    }
}

const formatGradeLabel = (label) => {
  if (!label) return ''
  const map = {
    A: 'Excellent',
    B: 'Good',
    C: 'Moderate',
    D: 'Pass',
    F: 'Fail',
    // 兼容旧数据（如果库里还有中文/英文全称）
    '优秀': 'Excellent',
    '良好': 'Good',
    '中等': 'Moderate',
    '及格': 'Pass',
    '不及格': 'Fail',
    Excellent: 'Excellent',
    Good: 'Good',
    Moderate: 'Moderate',
    Pass: 'Pass',
    Fail: 'Fail'
  }
  return map[label] || String(label)
}

const getGradeType = (label) => {
  const normalized = formatGradeLabel(label)
  if (normalized === 'Excellent') return 'success'
  if (normalized === 'Good') return ''
  if (normalized === 'Moderate') return 'warning'
  if (normalized === 'Pass') return 'info'
  return 'danger'
}



const updateViewport = () => {
    isCompact.value = window.innerWidth < 768
}

onMounted(() => {
    window.addEventListener('resize', updateViewport)
    updateViewport()
    fetchScores()
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', updateViewport)
})
</script>

<style scoped>
.score-entry {
    gap: 20px;
}

.score-hero {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 20px;
    padding: 28px;
    background:
        linear-gradient(135deg, rgba(15, 118, 110, 0.08), rgba(250, 204, 21, 0.08)),
        rgba(255, 255, 255, 0.94);
}

.hero-eyebrow {
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    color: #0f766e;
    font-weight: 700;
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

.hero-stats {
    display: grid;
    grid-template-columns: repeat(3, minmax(110px, 1fr));
    gap: 14px;
    width: min(100%, 420px);
}

.hero-stat {
    padding: 18px 16px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.82);
    border: 1px solid rgba(15, 118, 110, 0.08);
}

.hero-stat span {
    display: block;
    font-size: 28px;
    font-weight: 700;
    color: #111827;
}

.hero-stat label {
    display: block;
    margin-top: 8px;
    color: #667085;
    font-size: 13px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 16px;
}

.card-header h3 {
    margin: 0;
    font-size: 22px;
}

.card-header p {
    margin: 8px 0 0;
    color: #667085;
}

.header-actions {
    display: flex;
    align-items: center;
    gap: 10px;
}

.score-table-card :deep(.el-card__body) {
    padding-top: 0 !important;
}

.score-table {
    margin-top: 4px;
}

.mobile-score-list {
    display: grid;
    gap: 16px;
}

.score-mobile-card {
    border-radius: 20px;
}

.mobile-card-head {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: flex-start;
    margin-bottom: 16px;
}

.mobile-subline {
    margin-top: 6px;
    color: #667085;
    font-size: 13px;
}

.mobile-score-summary {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 8px;
}

.mobile-score-summary span {
    font-size: 26px;
    font-weight: 700;
    color: #0f172a;
}

.mobile-field-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 14px;
}

.compact-grid {
    margin-top: 10px;
}

.field-block {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.field-block label {
    font-size: 13px;
    color: #667085;
    font-weight: 600;
}

.field-block :deep(.el-input-number) {
    width: 100%;
}

.mobile-group-title {
    margin: 18px 0 10px;
    font-size: 14px;
    font-weight: 700;
    color: #0f766e;
}

@media (max-width: 960px) {
    .score-hero,
    .card-header {
        flex-direction: column;
    }

    .hero-stats {
        width: 100%;
    }

    .header-actions {
        width: 100%;
        justify-content: space-between;
    }
}

@media (max-width: 640px) {
    .hero-stats {
        grid-template-columns: 1fr;
    }

    .header-actions {
        flex-direction: column;
        align-items: stretch;
    }

    .mobile-card-head,
    .mobile-score-summary {
        align-items: flex-start;
        flex-direction: column;
    }

    .mobile-field-grid {
        grid-template-columns: 1fr;
    }
}
</style>

