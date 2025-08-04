import Vue from 'vue'
import Vuex from 'vuex'
import tab from './tab'
import workflow from './workflow'
import user from './user'
import debug from './debug'
Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    tab,
    workflow,
    user,
    debug,
  }
})
