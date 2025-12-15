import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

const app = createApp(App)
const pinia = createPinia()

// 全局错误拦截：静默处理资源加载失败（仅记录，不中断）
window.addEventListener('error', (e) => {
  if (e.message && e.message.includes('is not a function')) {
    console.warn('⚠️ 捕获的动态加载错误（已忽略）:', e.message)
    // 防止错误传播
    e.preventDefault()
  }
}, true)

// 处理unhandledrejection（Promise拒绝）
window.addEventListener('unhandledrejection', (e) => {
  if (e.reason && e.reason.message && e.reason.message.includes('is not a function')) {
    console.warn('⚠️ 捕获的Promise拒绝（已忽略）:', e.reason.message)
    e.preventDefault()
  }
})

app.use(ElementPlus, { locale: zhCn })
app.use(pinia)
app.use(router)

app.mount('#app')