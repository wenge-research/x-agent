export default {

    //存储数据
    state: {
        isCollapse: false,
        currentMenu: null,
        tabsList: [
            {
                path: '/',
                name: 'home',
                label: '首页',
                icon: 'home'
            }
        ],
        KonwledgeList: {},
        widthSide: 240,
        isAddedToApp: false,
    },
    //调用方法
    mutations: {
        collapseMenu(state) {
            state.isCollapse = !state.isCollapse
        },
        //选择标签 选择面包屑
        selectMenu(state, val) {
            if (val.name === 'home') {
                state.currentMenu = null
            } else {
                state.currentMenu = val
                //如果等于-1说明tabsList不存在那么插入，否则什么都不做
                let result = state.tabsList.findIndex(item => item.name === val.name)
                result === -1 ? state.tabsList.push(val) : ''

            }
            // val.name === 'home' ? (state.currentMenu = null) : (state.currentMenu = val)
        },
        //关闭标签
        closeTab(state, val) {
            let result = state.tabsList.findIndex(item => item.name === val.name)
            state.tabsList.splice(result, 1)
        },
        setKonwledge(state, val) {
            state.KonwledgeList = val
        },
        setWidthSide(state, val) {
            state.widthSide = val
        },
        setIsAddedToApp(state, val) {
            state.isAddedToApp = val
        },

    },
    actions: {}
}
