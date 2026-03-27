<template>
  <el-config-provider :locale="elLocale">
    <div v-if="isLoginRoute" class="login-route-shell">
      <router-view />
    </div>

    <div v-else class="app-shell">
      <aside v-if="!isMobile" class="shell-sidebar">
        <div class="brand-panel">
          <div class="brand-mark">
            <el-icon><Platform /></el-icon>
          </div>
          <div>
            <div class="brand-title">{{ t('app.title') }}</div>
            <div class="brand-subtitle">{{ t('app.sysTitle') }}</div>
          </div>
        </div>

        <el-scrollbar class="sidebar-scrollbar">
          <div class="sidebar-section-label">Navigation</div>
          <el-menu
            :default-active="$route.fullPath"
            :default-openeds="defaultOpeneds"
            class="sidebar-menu"
            router
            background-color="transparent"
            text-color="#5b6b7f"
            active-text-color="#0f766e"
          >
            <template v-for="entry in navEntries" :key="entry.index">
              <el-menu-item v-if="entry.type === 'item'" :index="entry.index">
                <el-icon><component :is="entry.icon" /></el-icon>
                <template #title>{{ entry.label }}</template>
              </el-menu-item>
              <el-sub-menu v-else :index="entry.index">
                <template #title>
                  <el-icon><component :is="entry.icon" /></el-icon>
                  <span>{{ entry.label }}</span>
                </template>
                <el-menu-item v-for="item in entry.items" :key="item.index" :index="item.index">
                  <el-icon><component :is="item.icon" /></el-icon>
                  <template #title>{{ item.label }}</template>
                </el-menu-item>
              </el-sub-menu>
            </template>
          </el-menu>
        </el-scrollbar>

        <div class="sidebar-footer">
          <div class="sidebar-footer-label">{{ userRoleZh }}</div>
          <div class="sidebar-footer-value">{{ userDisplayName }}</div>
        </div>
      </aside>

      <el-drawer
        v-model="mobileDrawerVisible"
        direction="ltr"
        size="280px"
        :with-header="false"
        class="mobile-nav-drawer"
      >
        <div class="brand-panel mobile-brand-panel">
          <div class="brand-mark">
            <el-icon><Platform /></el-icon>
          </div>
          <div>
            <div class="brand-title">{{ t('app.title') }}</div>
            <div class="brand-subtitle">{{ t('app.sysTitle') }}</div>
          </div>
        </div>
        <el-menu
          :default-active="$route.fullPath"
          :default-openeds="defaultOpeneds"
          class="sidebar-menu mobile-sidebar-menu"
          router
          @select="mobileDrawerVisible = false"
        >
          <template v-for="entry in navEntries" :key="entry.index">
            <el-menu-item v-if="entry.type === 'item'" :index="entry.index">
              <el-icon><component :is="entry.icon" /></el-icon>
              <template #title>{{ entry.label }}</template>
            </el-menu-item>
            <el-sub-menu v-else :index="entry.index">
              <template #title>
                <el-icon><component :is="entry.icon" /></el-icon>
                <span>{{ entry.label }}</span>
              </template>
              <el-menu-item v-for="item in entry.items" :key="item.index" :index="item.index">
                <el-icon><component :is="item.icon" /></el-icon>
                <template #title>{{ item.label }}</template>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-drawer>

      <div class="shell-main">
        <header class="shell-header">
          <div class="header-leading">
            <el-button v-if="isMobile" circle class="menu-trigger" @click="mobileDrawerVisible = true">
              <el-icon><Menu /></el-icon>
            </el-button>
            <div class="page-intro">
              <div class="page-eyebrow">{{ userRoleZh }}</div>
              <h1>{{ currentTitle }}</h1>
              <p>{{ currentSubtitle }}</p>
            </div>
          </div>

          <div class="header-actions">
            <el-button class="lang-toggle" @click="switchLang">
              {{ locale === 'en' ? '中 / EN' : 'EN / 中' }}
            </el-button>
            <div class="user-pill">
              <el-avatar class="user-avatar">{{ userDisplayName.slice(0, 1) }}</el-avatar>
              <div class="user-copy">
                <strong>{{ userDisplayName }}</strong>
                <span>{{ userRoleZh }}</span>
              </div>
            </div>
            <el-dropdown trigger="click">
              <el-button circle class="more-trigger">
                <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="logout">{{ t('app.logout') }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </header>

        <main class="shell-content">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </main>
      </div>
    </div>
  </el-config-provider>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from './store/user'
import axios from 'axios'
import {
  ArrowDown,
  Avatar,
  Connection,
  DocumentCopy,
  Menu,
  Notebook,
  Platform,
  Plus,
  Reading,
  Setting,
  Tickets,
  User,
  Warning
} from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import en from 'element-plus/dist/locale/en.mjs'

const { t, locale } = useI18n({ useScope: 'global' })
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const mobileDrawerVisible = ref(false)
const isMobile = ref(window.innerWidth < 992)

const elLocale = computed(() => locale.value === 'en' ? en : zhCn)
const userSession = computed(() => userStore.user)
const isLoginRoute = computed(() => route.path === '/login')

const routeTitleMap = {
  login: 'login.title',
  teacher: 'teacher.myCourses',
  student: 'student.enrolledCourses',
  'score-entry': 'score.title',
  'student-score': 'studentScore.title',
  analysis: 'analysis.title',
  messages: 'message.title',
  warnings: 'warning.title',
  'course-warnings': 'warning.title',
  survey: 'survey.title',
  profile: 'app.profilePageTitle',
  settings: 'app.settingsPageTitle'
}

const routeSubtitleMap = {
  teacher: 'teacher.welcomeDesc',
  student: 'student.welcomeDesc',
  messages: 'message.subtitle',
  warnings: 'warning.studentSubtitle',
  'course-warnings': 'warning.teacherSubtitle',
  login: 'login.subtitle'
}

const secretaryRouteMeta = {
  create: {
    title: 'secretary.tabs.createCourse',
    subtitle: 'secretary.createCourse.pageDesc'
  },
  enroll: {
    title: 'secretary.tabs.enrollment',
    subtitle: 'secretary.enroll.pageDesc'
  },
  list: {
    title: 'secretary.tabs.courseList',
    subtitle: 'secretary.list.pageDesc'
  },
  approve: {
    title: 'secretary.tabs.approveUsers',
    subtitle: 'secretary.approve.pageDesc'
  },
  teachers: {
    title: 'secretary.management.teacherTitle',
    subtitle: 'secretary.management.teacherDesc'
  },
  students: {
    title: 'secretary.management.studentTitle',
    subtitle: 'secretary.management.studentDesc'
  }
}

const userRoleZh = computed(() => {
  if (userSession.value?.role === 'TEACHER') return t('role.teacher')
  if (userSession.value?.role === 'SECRETARY') return t('role.secretary')
  if (userSession.value?.role === 'STUDENT') return t('role.student')
  return t('role.user')
})

const userRoleEn = computed(() => userSession.value?.role || 'LOGIN')
const userDisplayName = computed(() => userSession.value?.name || userSession.value?.username || t('role.user'))
const currentTitle = computed(() => {
  if (route.name === 'secretary') {
    const tab = route.query.tab || 'create'
    return t(secretaryRouteMeta[tab]?.title || 'secretary.welcome')
  }
  return t(routeTitleMap[route.name] || 'app.title')
})

const currentSubtitle = computed(() => {
  if (route.name === 'secretary') {
    const tab = route.query.tab || 'create'
    return t(secretaryRouteMeta[tab]?.subtitle || 'secretary.welcomeDesc')
  }
  const subtitleKey = routeSubtitleMap[route.name]
  return subtitleKey ? t(subtitleKey) : t('app.sysTitle')
})

const navEntries = computed(() => {
  const entries = [
    { type: 'item', index: `/${userRoleEn.value.toLowerCase()}`, label: t('menu.home'), icon: Menu }
  ]

  if (userSession.value?.role === 'SECRETARY') {
    entries.push(
      {
        type: 'group',
        index: 'secretary-courses',
        label: t('menu.courseGroup'),
        icon: DocumentCopy,
        items: [
          { index: '/secretary?tab=create', label: t('secretary.tabs.createCourse'), icon: Plus },
          { index: '/secretary?tab=enroll', label: t('secretary.tabs.enrollment'), icon: Connection },
          { index: '/secretary?tab=list', label: t('secretary.tabs.courseList'), icon: Reading }
        ]
      },
      {
        type: 'group',
        index: 'secretary-people',
        label: t('menu.peopleGroup'),
        icon: User,
        items: [
          { index: '/secretary?tab=approve', label: t('secretary.tabs.approveUsers'), icon: Tickets },
          { index: '/secretary?tab=teachers', label: t('secretary.management.teacherTitle'), icon: Avatar },
          { index: '/secretary?tab=students', label: t('secretary.management.studentTitle'), icon: Reading }
        ]
      }
    )
  }

  if (userSession.value?.role === 'TEACHER') {
    entries.push({ type: 'item', index: '/teacher', label: t('menu.myCourses'), icon: Reading })
  }

  if (userSession.value?.role === 'STUDENT') {
    entries.push(
      {
        type: 'group',
        index: 'student-study',
        label: t('menu.studyGroup'),
        icon: Notebook,
        items: [
          { index: '/student', label: t('menu.myCourses'), icon: Notebook },
          { index: '/warnings', label: t('menu.warnings'), icon: Warning }
        ]
      }
    )
  }

  entries.push(
    {
      type: 'group',
      index: 'account-group',
      label: t('menu.accountGroup'),
      icon: Setting,
      items: [
        { index: '/profile', label: t('app.profile'), icon: Avatar },
        { index: '/settings', label: t('app.settings'), icon: Setting }
      ]
    }
  )

  return entries
})

const defaultOpeneds = computed(() => navEntries.value.filter(entry => entry.type === 'group').map(entry => entry.index))

const switchLang = () => {
  locale.value = locale.value === 'en' ? 'zh' : 'en'
}

const updateScreenState = () => {
  isMobile.value = window.innerWidth < 992
  if (!isMobile.value) {
    mobileDrawerVisible.value = false
  }
}

const updateDocumentTitle = () => {
  document.title = `${currentTitle.value} | ${t('app.title')}`
}

const logout = () => {
  userStore.logout()
  mobileDrawerVisible.value = false
  router.push('/login')
}

onMounted(() => {
  window.addEventListener('resize', updateScreenState)
  updateScreenState()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateScreenState)
})

watch(locale, (value) => {
  localStorage.setItem('lang', value)
  axios.defaults.headers.common['Accept-Language'] = value
  updateDocumentTitle()
}, { immediate: true })

watch(() => route.fullPath, () => {
  mobileDrawerVisible.value = false
  updateDocumentTitle()
}, { immediate: true })
</script>

<style>
:root {
  --shell-bg: #f3efe6;
  --shell-surface: rgba(255, 250, 244, 0.78);
  --shell-border: rgba(148, 163, 184, 0.22);
  --shell-card: rgba(255, 255, 255, 0.92);
  --shell-primary: #0f766e;
  --shell-text: #1f2937;
  --shell-muted: #667085;
  --shell-shadow: 0 24px 60px rgba(102, 86, 58, 0.12);
}

* {
  box-sizing: border-box;
}

html,
body,
#app {
  min-height: 100%;
}

body {
  margin: 0;
  font-family: 'Avenir Next', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: var(--shell-text);
  background:
    radial-gradient(circle at top left, rgba(15, 118, 110, 0.12), transparent 32%),
    radial-gradient(circle at right 20%, rgba(217, 119, 6, 0.12), transparent 24%),
    linear-gradient(180deg, #f8f3ea 0%, #f3efe6 100%);
  -webkit-font-smoothing: antialiased;
}

.login-route-shell {
  min-height: 100vh;
}

.app-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 20px;
  padding: 20px;
}

.shell-sidebar {
  display: flex;
  flex-direction: column;
  padding: 20px 18px;
  border: 1px solid var(--shell-border);
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(255, 252, 247, 0.96), rgba(248, 244, 236, 0.9));
  backdrop-filter: blur(18px);
  box-shadow: var(--shell-shadow);
}

.brand-panel {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 8px 8px 18px;
}

.brand-mark {
  width: 52px;
  height: 52px;
  display: grid;
  place-items: center;
  border-radius: 18px;
  background: linear-gradient(135deg, #0f766e, #14b8a6);
  color: #fff;
  font-size: 24px;
  box-shadow: 0 14px 30px rgba(15, 118, 110, 0.22);
}

.brand-title {
  font-size: 17px;
  font-weight: 700;
  line-height: 1.4;
}

.brand-subtitle {
  color: var(--shell-muted);
  font-size: 12px;
  margin-top: 4px;
}

.sidebar-scrollbar {
  flex: 1;
}

.sidebar-section-label {
  padding: 8px 10px 10px;
  color: #8a7d68;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.sidebar-menu {
  border-right: none !important;
}

.sidebar-menu.el-menu {
  background: transparent;
}

.sidebar-menu .el-menu-item {
  height: 48px;
  margin: 6px 0;
  border-radius: 16px;
  font-weight: 600;
}

.sidebar-menu .el-sub-menu {
  margin: 8px 0;
}

.sidebar-menu .el-sub-menu__title {
  height: 46px;
  border-radius: 16px;
  font-weight: 700;
  color: #415060;
}

.sidebar-menu .el-sub-menu__title:hover {
  background: rgba(15, 118, 110, 0.06);
}

.sidebar-menu .el-menu--inline {
  background: transparent;
}

.sidebar-menu .el-menu--inline .el-menu-item {
  margin-left: 12px;
}

.sidebar-menu .el-menu-item:hover {
  background: rgba(15, 118, 110, 0.08);
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(90deg, rgba(15, 118, 110, 0.14), rgba(15, 118, 110, 0.04));
}

.sidebar-footer {
  margin-top: 18px;
  padding: 16px 18px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(15, 118, 110, 0.12), rgba(251, 191, 36, 0.12));
}

.sidebar-footer-label {
  color: var(--shell-muted);
  font-size: 12px;
}

.sidebar-footer-value {
  margin-top: 6px;
  font-size: 15px;
  font-weight: 700;
}

.shell-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.shell-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  padding: 22px 26px;
  border-radius: 28px;
  border: 1px solid var(--shell-border);
  background: var(--shell-surface);
  backdrop-filter: blur(18px);
  box-shadow: 0 18px 40px rgba(124, 96, 54, 0.08);
}

.header-leading,
.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-intro {
  min-width: 0;
}

.page-eyebrow {
  color: var(--shell-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.page-intro h1 {
  margin: 6px 0 4px;
  font-size: clamp(24px, 2vw, 32px);
  line-height: 1.2;
}

.page-intro p {
  margin: 0;
  color: var(--shell-muted);
  max-width: 720px;
}

.menu-trigger,
.more-trigger,
.lang-toggle {
  border-color: rgba(15, 118, 110, 0.18) !important;
  background: rgba(255, 255, 255, 0.8) !important;
}

.lang-toggle {
  border-radius: 999px !important;
  font-weight: 700;
  color: var(--shell-primary) !important;
}

.user-pill {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  padding: 8px 14px 8px 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(15, 118, 110, 0.1);
}

.user-avatar {
  background: linear-gradient(135deg, #0f766e, #14b8a6) !important;
  color: #fff !important;
}

.user-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.user-copy strong {
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.user-copy span {
  color: var(--shell-muted);
  font-size: 12px;
}

.shell-content {
  min-width: 0;
}

.page-shell {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-panel,
.section-card,
.surface-card,
.el-card {
  border: 1px solid rgba(148, 163, 184, 0.14) !important;
  border-radius: 24px !important;
  background: rgba(255, 255, 255, 0.92) !important;
  box-shadow: 0 20px 45px rgba(125, 102, 69, 0.08) !important;
}

.el-card__header {
  padding: 18px 22px !important;
  border-bottom: 1px solid rgba(148, 163, 184, 0.16) !important;
  background: transparent !important;
}

.el-card__body {
  padding: 22px !important;
}

.el-table {
  border-radius: 18px !important;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.8);
}

.el-table th.el-table__cell {
  background: #f8faf7 !important;
  color: #415060;
  font-weight: 700;
}

.el-table .el-table__row:hover td.el-table__cell {
  background: rgba(15, 118, 110, 0.04) !important;
}

.el-button--primary {
  --el-button-bg-color: #0f766e;
  --el-button-border-color: #0f766e;
  --el-button-hover-bg-color: #0b5f59;
  --el-button-hover-border-color: #0b5f59;
  --el-button-active-bg-color: #094a45;
  --el-button-active-border-color: #094a45;
}

.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.28s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(18px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-18px);
}

@media (max-width: 991px) {
  .app-shell {
    grid-template-columns: 1fr;
    padding: 14px;
  }

  .shell-header {
    padding: 18px;
    flex-direction: column;
    align-items: stretch;
  }

  .header-leading,
  .header-actions {
    justify-content: space-between;
    flex-wrap: wrap;
  }

  .user-pill {
    flex: 1;
  }

  .mobile-brand-panel {
    padding: 10px 8px 22px;
  }

  .mobile-sidebar-menu {
    border-right: none;
  }
}

@media (max-width: 640px) {
  .header-actions {
    gap: 10px;
  }

  .user-pill {
    width: 100%;
  }

  .page-intro h1 {
    font-size: 22px;
  }

  .el-card__body {
    padding: 16px !important;
  }
}
</style>










