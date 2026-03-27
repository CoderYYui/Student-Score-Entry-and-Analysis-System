<template>
  <div class="survey-container page-shell">
    <section class="survey-hero surface-card">
      <div>
        <div class="hero-eyebrow">Feedback Form</div>
        <h2>{{ t('survey.title') }}</h2>
        <p>{{ t('survey.desc') }}</p>
      </div>
      <el-button @click="$router.push('/student')">{{ t('common.back') }}</el-button>
    </section>

    <el-card class="survey-card" shadow="always">
        <template #header>
            <div class="card-header">
        <div>
          <h3>{{ t('survey.title') }}</h3>
          <p class="card-subtitle">问卷结果将用于课程满意度统计与教学改进建议生成。</p>
        </div>
            </div>
        </template>
        <div class="survey-desc">
      <p>{{ t('survey.desc') }}</p>
        </div>
        <el-form label-position="top">
      <el-form-item :label="t('survey.qDirect')" required>
                <el-radio-group v-model="survey.satisfactionLevel" class="custom-radio-group">
          <el-radio border value="非常满意">{{ t('survey.opt1') }}</el-radio>
          <el-radio border value="比较满意">{{ t('analysis.compareSatisfied') }} (4)</el-radio>
          <el-radio border value="满意">{{ t('survey.opt3') }}</el-radio>
          <el-radio border value="基本满意">{{ t('survey.opt4') }}</el-radio>
          <el-radio border value="不满意">{{ t('survey.opt5') }}</el-radio>
                </el-radio-group>
            </el-form-item>
            
      <el-form-item :label="t('survey.qMethods')">
                <el-input
                    v-model="survey.comment"
                    type="textarea"
                    :rows="4"
          :placeholder="t('survey.ph')"
                />
            </el-form-item>

            <el-button type="primary" :loading="submitting" @click="submitSurvey" size="large" class="submit-btn">
        {{ t('survey.submitBtn') }}
            </el-button>
        </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const { t } = useI18n({ useScope: 'global' })
const courseId = route.params.id

const survey = ref({
    satisfactionLevel: '满意',
    comment: ''
})

const submitting = ref(false)

const submitSurvey = async () => {
    submitting.value = true
    try {
        const u = JSON.parse(localStorage.getItem('user'))
        await axios.post('/api/survey/submit', {
            studentId: u.id,
            courseId: courseId,
            satisfactionLevel: survey.value.satisfactionLevel,
            comment: survey.value.comment
        })
        ElMessage.success(t('survey.success'))
        setTimeout(() => {
           router.push('/student')
        }, 1000)
    } catch (e) {
        ElMessage.error(t('survey.fail'))
    } finally {
        submitting.value = false
    }
}
</script>

<style scoped>
.survey-container {
  gap: 20px;
}

.survey-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  padding: 28px;
  background:
    linear-gradient(135deg, rgba(245, 158, 11, 0.08), rgba(16, 185, 129, 0.08)),
    rgba(255, 255, 255, 0.94);
}

.hero-eyebrow {
  color: #d97706;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.survey-hero h2 {
  margin: 10px 0 8px;
  font-size: 30px;
}

.survey-hero p {
  margin: 0;
  color: #667085;
  max-width: 720px;
  line-height: 1.7;
}

.survey-card {
  width: min(100%, 860px);
  margin: 0 auto;
  border-radius: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #303133;
}

.card-subtitle {
  margin: 8px 0 0;
  color: #667085;
  font-size: 13px;
}

.survey-desc {
  background: linear-gradient(135deg, #fff7ed, #fffbeb);
  padding: 18px 20px;
  border-radius: 16px;
  color: #e6a23c;
  margin-bottom: 24px;
  line-height: 1.6;
  border: 1px solid #fde68a;
}

.custom-radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}
.custom-radio-group .el-radio {
  margin-right: 0;
  width: 100%;
  border-radius: 14px;
  padding: 0 20px;
  height: 50px;
  display: flex;
  align-items: center;
}
.custom-radio-group .el-radio.is-checked {
  background-color: #f0fdf4;
  border-color: #22c55e;
}
.submit-btn {
  width: 100%;
  margin-top: 20px;
  border-radius: 14px;
  height: 48px;
}

@media (max-width: 768px) {
  .survey-hero,
  .card-header {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
