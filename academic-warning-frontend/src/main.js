import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

// 全局错误捕获
window.addEventListener('error', (e) => {
  // 过滤掉第三方扩展/插件的错误
  if (e.target && e.target.tagName === 'IMG') {
    console.warn('⚠️ 资源加载失败（可能是扩展冲突）:', e.target.src)
    return // 不中断，继续运行
  }
  
  const errorMsg = e.message || e.toString() || '未知错误'
  const errorObj = e.error || e.filename || ''
  console.warn('⚠️ 全局错误捕获:', errorMsg, errorObj)
  console.error('完整错误:', e)
  e.preventDefault()
}, true)

window.addEventListener('unhandledrejection', (e) => {
  const reason = e.reason || '未知Promise错误'
  console.warn('⚠️ Promise拒绝:', reason)
  console.error('完整Promise错误:', e)
  e.preventDefault()
})

try {
  const app = createApp(App)
  const pinia = createPinia()
  app.use(ElementPlus, { locale: zhCn })
  app.use(pinia)
  app.use(router)
  app.mount('#app')
} catch (err) {
  console.error('应用初始化失败:', err)
  // 页面仍然显示，即使有错误
}