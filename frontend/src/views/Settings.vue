<template>
  <div class="settings-page page-shell">
    <section class="settings-hero surface-card">
      <div>
        <div class="hero-eyebrow">Workspace Preferences</div>
        <h2>{{ t('app.settings') }}</h2>
        <p>Switch the system language and view the current app version. Settings are saved in your browser.</p>
      </div>
      <div class="hero-chip">
        <strong>v1.0.0</strong>
        <span>{{ t('app.version') }}</span>
      </div>
    </section>

    <div class="settings-grid">
      <el-card class="settings-card surface-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div>
              <h3>Interface Preferences</h3>
              <p>Chinese and English switching is currently supported, and the page title and component copy will be updated synchronously after switching.</p>
            </div>
          </div>
        </template>
        <el-form label-position="top">
          <el-form-item :label="t('app.language')">
            <el-radio-group v-model="localeValue" class="lang-group">
              <el-radio-button label="zh">中文</el-radio-button>
              <el-radio-button label="en">English</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="settings-card surface-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div>
              <h3>System Information</h3>
              <p>Facilitates the confirmation of the current running version and the local configuration storage method.</p>
            </div>
          </div>
        </template>
        <el-descriptions :column="1" border>
          <el-descriptions-item :label="t('app.version')">v1.0.0</el-descriptions-item>
          <el-descriptions-item label="Language persistence">localStorage</el-descriptions-item>
          <el-descriptions-item label="Current language">{{ localeValue === 'zh' ? '中文' : 'English' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n({ useScope: 'global' })
const localeValue = computed({
  get: () => locale.value,
  set: (value) => {
    locale.value = value
    localStorage.setItem('lang', value)
  }
})
</script>

<style scoped>
.settings-page {
  gap: 20px;
}

.settings-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  padding: 28px;
  background:
    linear-gradient(135deg, rgba(99, 102, 241, 0.08), rgba(14, 165, 233, 0.08)),
    rgba(255, 255, 255, 0.94);
}

.hero-eyebrow {
  color: #4f46e5;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.settings-hero h2 {
  margin: 8px 0 6px;
  font-size: 30px;
}

.settings-hero p {
  margin: 0;
  color: #667085;
}

.hero-chip {
  min-width: 120px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(79, 70, 229, 0.1);
}

.hero-chip strong {
  display: block;
  font-size: 28px;
}

.hero-chip span {
  display: block;
  margin-top: 8px;
  color: #667085;
}

.settings-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.card-header h3 {
  margin: 0;
  font-size: 22px;
}

.card-header p {
  margin: 8px 0 0;
  color: #667085;
}

.lang-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

@media (max-width: 768px) {
  .settings-hero {
    flex-direction: column;
  }

  .settings-grid {
    grid-template-columns: 1fr;
  }
}
</style>
