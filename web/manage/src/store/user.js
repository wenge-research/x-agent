import Cookie from 'js-cookie'
export default {
  state: {
    token: '',
    showAside: true,
  },
  mutations: {
    //存放token
    setToken(state, val) {
      const date = new Date();  
      date.setMonth(date.getMonth() + 1); // 将月份增加1 
      state.token = val
      Cookie.set('token', val, {
        expires: date
      })
    },
    //清空token
    clearToken(state) {
      state.token = ''
      Cookie.remove('token')
    },
    //获取token
    getToken(state) {
      state.token = Cookie.get('token')
    }
  },
  actions: {}
}
