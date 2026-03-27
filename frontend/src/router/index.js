import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import TeacherDashboard from '../views/TeacherDashboard.vue'
import StudentDashboard from '../views/StudentDashboard.vue'
import SecretaryDashboard from '../views/SecretaryDashboard.vue'
import ScoreEntry from '../views/ScoreEntry.vue'
import Analysis from '../views/Analysis.vue'
import Survey from '../views/Survey.vue'
import StudentScore from '../views/StudentScore.vue'
import Profile from '../views/Profile.vue'
import Settings from '../views/Settings.vue'
import MessageCenter from '../views/MessageCenter.vue'
import WarningCenter from '../views/WarningCenter.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/teacher',
      name: 'teacher',
      component: TeacherDashboard
    },
    {
      path: '/student',
      name: 'student',
      component: StudentDashboard
    },
    {
      path: '/secretary',
      name: 'secretary',
      component: SecretaryDashboard
    },
    {
      path: '/course/:id/scores',
      name: 'score-entry',
      component: ScoreEntry
    },
    {
      path: '/course/:id/my-score',
      name: 'student-score',
      component: StudentScore
    },
    {
      path: '/course/:id/analysis',
      name: 'analysis',
      component: Analysis
    },
    {
      path: '/course/:id/messages',
      name: 'messages',
      component: MessageCenter
    },
    {
      path: '/course/:id/warnings',
      name: 'course-warnings',
      component: WarningCenter
    },
    {
      path: '/survey/:id',
      name: 'survey',
      component: Survey
    },
    {
      path: '/warnings',
      name: 'warnings',
      component: WarningCenter
    },
        {
      path: '/profile',
      name: 'profile',
      component: Profile
    },
    {
      path: '/settings',
      name: 'settings',
      component: Settings
    },
    {
      path: '/',
      redirect: '/login'
    }
  ]
})

export default router
