import Vue from 'vue'

import Cookies from 'js-cookie'

import Element from 'element-ui'
import './assets/styles/element-variables.scss'
import '@/assets/styles/index.scss' // global css
import '@/assets/styles/ruoyi.scss' // ruoyi css
import App from './App'
import store from './store'
import router from './router'
import plugins from './plugins' // plugins
import directive from '@/plugins/directive'

import './assets/icons' // icon

// 分页组件
import Pagination from "@/components/Pagination";
// 头部标签组件
import VueMeta from 'vue-meta'


// 全局组件挂载
Vue.component('Pagination', Pagination)

Vue.use(plugins)
Vue.use(VueMeta)
Vue.use(directive)

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
