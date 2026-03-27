<template>
  <div class="login-container">
    <div class="login-backdrop"></div>
    <div class="login-topbar">
      <div class="topbar-brand">
        <span class="brand-dot"></span>
        <span>{{ t('app.sysTitle') }}</span>
      </div>
      <el-button class="lang-switch" @click="switchLang">
        {{ locale === 'en' ? '中 / EN' : 'EN / 中' }}
      </el-button>
    </div>

    <div class="login-box">
      <section class="login-left">
        <div class="eyebrow">Academic Workflow</div>
        <h1>{{ t('app.title') }}</h1>
        <p class="hero-copy">{{ t('login.subtitle') }}</p>

        <div class="hero-metrics">
          <div class="metric-card">
            <strong>3</strong>
            <span>{{ t('role.student') }} / {{ t('role.teacher') }} / {{ t('role.secretary') }}</span>
          </div>
          <div class="metric-card">
            <strong>24h</strong>
            <span>{{ t('score.saving') }}</span>
          </div>
        </div>

        <div class="login-features">
          <div class="feature-item"><el-icon><Check /></el-icon><span>{{ t('login.f1') }}</span></div>
          <div class="feature-item"><el-icon><Check /></el-icon><span>{{ t('login.f2') }}</span></div>
          <div class="feature-item"><el-icon><Check /></el-icon><span>{{ t('login.f3') }}</span></div>
          <div class="feature-item"><el-icon><Check /></el-icon><span>{{ t('login.f4') }}</span></div>
        </div>
      </section>

      <section class="login-right">
        <div class="login-form-wrapper">
          <div class="form-badge">{{ isRegister ? t('login.registerBtn') : t('login.loginBtn') }}</div>
          <h2>{{ isRegister ? t('login.welcomeRegister') : t('login.welcome') }}</h2>
          <p class="login-subtitle">{{ isRegister ? t('login.instructionRegister') : t('login.instruction') }}</p>

          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" size="large" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                :placeholder="t('login.username')"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item prop="name" v-if="isRegister">
              <el-input
                v-model="loginForm.name"
                :placeholder="t('login.name')"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item prop="role" v-if="isRegister">
              <el-select v-model="loginForm.role" :placeholder="t('login.role')" style="width:100%">
                <el-option :label="t('role.student')" value="STUDENT"></el-option>
                <el-option :label="t('role.teacher')" value="TEACHER"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item prop="email" v-if="isRegister">
              <el-input
                v-model="loginForm.email"
                :placeholder="t('login.email')"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                :placeholder="t('login.password')"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleAuth(loginFormRef)"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="loading" class="login-btn" @click="handleAuth(loginFormRef)">
                {{ isRegister ? t('login.registerBtn') : t('login.loginBtn') }}
              </el-button>
            </el-form-item>

            <button type="button" class="toggle-auth" @click="toggleAuthMode">
              {{ isRegister ? t('login.goLogin') : t('login.goRegister') }}
            </button>
          </el-form>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { User, Lock, Check } from '@element-plus/icons-vue'

const { t, locale } = useI18n({ useScope: 'global' })
const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)
const isRegister = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  name: '',
  role: 'STUDENT',
  email: ''
})

const rules = computed(() => ({
  username: [{ required: true, message: t('login.username'), trigger: 'blur' }],
  password: [{ required: true, message: t('login.password'), trigger: 'blur' }],
  name: [{ required: true, message: t('login.name'), trigger: 'blur' }],
  email: [{ required: true, message: t('login.email'), trigger: 'blur' }]
}))

const switchLang = () => {
  locale.value = locale.value === 'en' ? 'zh' : 'en'
}

const toggleAuthMode = () => {
  isRegister.value = !isRegister.value
  loginForm.password = ''
}

const handleAuth = async (formEl) => {
  if (!formEl) return
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const endpoint = isRegister.value ? '/api/auth/register' : '/api/auth/login'
        const response = await fetch(endpoint, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(loginForm)
        })
        const data = await response.json()
        if (data.success) {
          if (isRegister.value) {
            ElMessage.success(t('login.registerSuccess'))
            loginForm.password = ''
            loginForm.email = ''
            isRegister.value = false
          } else {
            ElMessage.success(t('login.success'))
            userStore.setUser(data.user, data.token)

            const role = data.user.role
            if (role === 'TEACHER') {
              router.push('/teacher')
            } else if (role === 'STUDENT') {
              router.push('/student')
            } else if (role === 'SECRETARY') {
              router.push('/secretary')
            } else {
              router.push('/')
            }
          }
        } else {
          ElMessage.error(data.message || t('login.failed'))
        }
      } catch (error) {
        console.error('Auth error:', error)
        ElMessage.error(t('login.offline'))
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  padding: 28px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #163a36 0%, #0f172a 55%, #3f2e1f 100%);
}

.login-backdrop {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 15% 20%, rgba(45, 212, 191, 0.18), transparent 28%),
    radial-gradient(circle at 85% 18%, rgba(251, 191, 36, 0.16), transparent 24%),
    radial-gradient(circle at 70% 80%, rgba(255, 255, 255, 0.08), transparent 18%);
}

.login-topbar {
  position: absolute;
  top: 22px;
  left: 28px;
  right: 28px;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.topbar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
}

.brand-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2dd4bf, #fbbf24);
  box-shadow: 0 0 16px rgba(45, 212, 191, 0.8);
}

.lang-switch {
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.22);
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.login-box {
  position: relative;
  z-index: 1;
  width: min(1120px, 100%);
  min-height: 680px;
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 32px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(22px);
  box-shadow: 0 30px 70px rgba(0, 0, 0, 0.32);
}

.login-left {
  padding: 72px 56px;
  color: #f8fafc;
  background:
    linear-gradient(160deg, rgba(13, 148, 136, 0.88), rgba(15, 23, 42, 0.72)),
    linear-gradient(135deg, #134e4a, #0f172a);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  color: rgba(255, 255, 255, 0.72);
}

.login-left h1 {
  margin: 18px 0 16px;
  font-size: clamp(34px, 4vw, 52px);
  line-height: 1.12;
}

.hero-copy {
  margin: 0;
  max-width: 520px;
  font-size: 17px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.82);
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin: 34px 0;
}

.metric-card {
  padding: 18px 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.14);
}

.metric-card strong {
  display: block;
  font-size: 28px;
  margin-bottom: 8px;
}

.metric-card span {
  color: rgba(255, 255, 255, 0.74);
  line-height: 1.6;
}

.login-features {
  display: grid;
  gap: 14px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.feature-item .el-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  color: #0f766e;
  background: #ecfeff;
}

.login-right {
  padding: 48px 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, rgba(255, 251, 245, 0.96), rgba(255, 255, 255, 0.9));
}

.login-form-wrapper {
  width: min(100%, 380px);
}

.form-badge {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(15, 118, 110, 0.1);
  color: #0f766e;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.login-form-wrapper h2 {
  margin: 18px 0 10px;
  color: #111827;
  font-size: 32px;
}

.login-subtitle {
  margin: 0 0 28px;
  color: #6b7280;
  line-height: 1.7;
}

.login-form :deep(.el-input__wrapper),
.login-form :deep(.el-select__wrapper) {
  min-height: 48px;
  border-radius: 16px;
  box-shadow: 0 0 0 1px rgba(148, 163, 184, 0.18) inset;
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 700;
}

.toggle-auth {
  width: 100%;
  margin-top: 8px;
  padding: 0;
  border: none;
  background: transparent;
  color: #0f766e;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.toggle-auth:hover {
  text-decoration: underline;
}

@media (max-width: 980px) {
  .login-container {
    padding: 86px 18px 18px;
  }

  .login-topbar {
    left: 18px;
    right: 18px;
  }

  .login-box {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .login-left,
  .login-right {
    padding: 32px 24px;
  }
}

@media (max-width: 640px) {
  .hero-metrics {
    grid-template-columns: 1fr;
  }

  .login-left h1 {
    font-size: 32px;
  }

  .topbar-brand span:last-child {
    display: none;
  }
}
</style>

