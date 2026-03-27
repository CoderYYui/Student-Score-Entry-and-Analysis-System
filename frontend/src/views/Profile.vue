<template>
  <div class="profile-page page-shell">
    <section class="profile-hero surface-card">
      <div class="profile-hero-main" v-if="user">
        <el-avatar :size="84" class="profile-avatar">{{ displayInitial }}</el-avatar>
        <div>
          <div class="hero-eyebrow">Account Overview</div>
          <h2>{{ user.name || user.username }}</h2>
          <p>查看当前登录账号的身份、审批状态和基础联系信息。</p>
        </div>
      </div>
      <el-tag type="success" size="large">{{ userRoleText }}</el-tag>
    </section>

    <el-card class="profile-card surface-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h3>{{ t('app.profile') }}</h3>
            <p>该页面用于集中查看当前账号的个人信息与使用身份。</p>
          </div>
        </div>
      </template>
      <el-empty v-if="!user" description="当前没有可用的用户信息" />
      <div v-else class="profile-grid">
        <div class="profile-panel accent-panel">
          <div class="panel-title">Basic Identity</div>
          <el-descriptions :column="1" border>
            <el-descriptions-item :label="t('login.username')">{{ user.username || '-' }}</el-descriptions-item>
            <el-descriptions-item :label="t('login.name')">{{ user.name || '-' }}</el-descriptions-item>
            <el-descriptions-item :label="t('login.role')"><el-tag type="success">{{ userRoleText }}</el-tag></el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="profile-panel muted-panel">
          <div class="panel-title">Account status</div>
          <el-descriptions :column="1" border>
            <el-descriptions-item :label="t('login.email')">{{ user.email || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="Approval Status">{{ user.status || 'APPROVED' }}</el-descriptions-item>
            <el-descriptions-item label="Login Lock">{{ user.locked ? '已锁定' : '正常' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '../store/user'

const { t } = useI18n({ useScope: 'global' })
const userStore = useUserStore()
const user = computed(() => userStore.user)
const displayInitial = computed(() => (user.value?.name || user.value?.username || 'U').slice(0, 1).toUpperCase())

const userRoleText = computed(() => {
  if (!user.value || !user.value.role) return ''
  const r = user.value.role.toLowerCase()
  return t('role.' + r)
})
</script>

<style scoped>
.profile-page {
  gap: 20px;
}

.profile-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  padding: 28px;
  background:
    linear-gradient(135deg, rgba(14, 165, 233, 0.08), rgba(20, 184, 166, 0.08)),
    rgba(255, 255, 255, 0.94);
}

.profile-hero-main {
  display: flex;
  align-items: center;
  gap: 18px;
}

.profile-avatar {
  background: linear-gradient(135deg, #0284c7, #14b8a6) !important;
  color: #fff !important;
  font-size: 30px;
  font-weight: 700;
}

.hero-eyebrow {
  color: #0284c7;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.profile-hero h2 {
  margin: 8px 0 6px;
  font-size: 30px;
}

.profile-hero p {
  margin: 0;
  color: #667085;
}

.card-header h3 {
  margin: 0;
  font-size: 22px;
}

.card-header p {
  margin: 8px 0 0;
  color: #667085;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.profile-panel {
  padding: 18px;
  border-radius: 20px;
  border: 1px solid rgba(148, 163, 184, 0.14);
}

.accent-panel {
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.96), rgba(248, 250, 252, 0.96));
}

.muted-panel {
  background: linear-gradient(180deg, rgba(249, 250, 251, 0.96), rgba(255, 255, 255, 0.96));
}

.panel-title {
  margin-bottom: 14px;
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

@media (max-width: 768px) {
  .profile-hero,
  .profile-hero-main {
    flex-direction: column;
    align-items: flex-start;
  }

  .profile-grid {
    grid-template-columns: 1fr;
  }
}
</style>
