<template>
    <el-drawer class="mobile-right-menu-wrap" v-model="drawerVisible" :show-close="false" size="90%">
        <template #header="{ close, titleId, titleClass }">
            <div :id="titleId" :class="titleClass">
                <div class="user-icon-wrap">
                    <iconpark-icon name="user-3-fill" color="#797F8A" size="22"></iconpark-icon>
                </div>
                <div class="user-name">
                    <span v-if="!isToken" @click="toLogin">点击登录</span>
                    <div class="user-text-wrap" v-else>
                        <span class="user-text">{{ userName }}</span>
                        <iconpark-icon name="logout-box-r-line" color="#3F4247" size="24"
                            @click="sureOutLogin"></iconpark-icon>
                    </div>
                </div>
            </div>
            <div class="close-btn">
                <iconpark-icon name="close-large-line" color="#3F4247" size="18" style="cursor: pointer"
                    @click="close"></iconpark-icon>
            </div>
        </template>
        <div class="btn-box">
            <!-- <div class="btn-item" style="color: #3F4247;border: 1px solid #3F4247;">
                <div class="btn-item-icon">
                    <iconpark-icon name="phone-line-djlmkb1p" color="#3F4247" size="18"></iconpark-icon>
                </div>
                发起语音
            </div> -->
            <div class="btn-item" style="color: #2065D6;border: 1px solid #2065D6;" @click="newChat">
                <div class="btn-item-icon">
                    <iconpark-icon name="chat-new-line" color="#2065D6" size="18"></iconpark-icon>
                </div>
                新对话
            </div>
        </div>
        <div class="history-dialogue-title">历史对话</div>
        <div class="history-dialogue-wrap" v-if="isToken">
            <div class="history-dialogue-list" v-if="dialogueList.length" v-loading="loading">
                <div v-for="(item, index) in dialogueList" :key="index" class="dialogueList-item"
                    @click="chatClick(item)">
                    <div class="name">{{ item.question }}</div>
                    <div class="btn">
                        <!-- <div>{{ item.createTime }}</div> -->
                        <img class="chatImg" :src="chatLine" @click.stop="recordDelete(item)" />
                    </div>
                </div>
            </div>
            <div class="no-data" v-else>
                <img src="/@/assets/nodataImg.png" />
                <span>暂无数据</span>
            </div>
           
        </div>
        <div class="history-dialogue-wrap" v-else>
          <div class="no-data">
            <img src="/@/assets/nodataImg.png" />
            <span>登录后可查看</span>
          </div>
        </div>
        <div class="draw-footer">
            <el-popover placement="top" trigger="click" :show-arrow="true">
                <div class="disclaimer-title" style="margin-left:8px;">免责声明</div>
                <div v-html="getmzsm()" class="disclaimer-txt" style="margin-left:8px;"></div>
                <template #reference>
                    <div class="footer-info">
                        <iconpark-icon style="margin-right: 8px;" name="information-line" size="24"
                            color="#797F8A"></iconpark-icon>
                        免责声明
                    </div>
                </template>
            </el-popover>
        </div>
        <LoginDrawer @login-success="checkLoginStatus" />
    </el-drawer>
</template>

<script setup>
import { ref, watch, onMounted, defineAsyncComponent } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import mittBus from '/@/utils/mitt';
import { useChatStore } from '/@/stores/chat';
import { Message } from 'winbox-ui-next';
import { ElMessage } from 'element-plus';
import { SuccessFilled } from '@element-plus/icons-vue'
import chatLine from '/@/assets/ai/delete-bin-4-line.svg';
import { recordGetRecord, recordLogicDelete } from '/@/api/chat';
const chatStore = useChatStore();
const LoginDrawer = defineAsyncComponent(() => import('./loginDrawer.vue'));

const drawerVisible = ref(false);
const userName = ref('')
const toLogin = () => {
    console.log('toLogin');
    mittBus.emit('showLoginDrawer')
}

const route = useRoute();
const getmzsm = () => {
    let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
    let disclaimer = appInfo ? appInfo.disclaimer : '暂无数据';
    return disclaimer?.replace(/\n/g, '<br /><p style="margin-bottom: 6px"></p>');
};

const isToken = ref(false);
const checkLoginStatus = () => {
    // drawerVisible.value = false
    const storedUserInfoString = localStorage.getItem('userInfo');
    if (storedUserInfoString) {
        const storedUserInfo = JSON.parse(storedUserInfoString);
        isToken.value = storedUserInfo?.accessToken ? true : false;
        userName.value = storedUserInfo?.user.userName || ''
        isToken.value && recordGetRecordList()
    } else {
        isToken.value = false;
    }

}

const dialogueList = ref([]);
const loading = ref(false);
const recordGetRecordList = async () => {
    let res = await recordGetRecord({
        applicationId: localStorage.getItem(`${route.params.appId}appId`),
        pageNo: 1,
        pageSize: 999,
        verifyDeptId: "",
        verifyStatus: "",
        text: "",
        conversationId: "",
        deleted: 0
    });
    dialogueList.value = Object.values(
        res.data.list.reduce((acc, current) => {
            if (
                !acc[current.conversationId] ||
                new Date(acc[current.conversationId].createTime).getTime() >
                new Date(current.createTime).getTime()
            ) {
                acc[current.conversationId] = current;
            }
            return acc;
        }, {})
    );
    dialogueList.value.sort((a, b) => Number(b.conversationId) - Number(a.conversationId));
};

const chatClick = (item) => {
    drawerVisible.value = false
    chatStore.getChatRecordsList(item.conversationId);
};

const recordDelete = async (item) => {
    loading.value = true;
    let res = await recordLogicDelete({
        applicationId: localStorage.getItem(`${route.params.appId}appId`),
        conversationId: item.conversationId,
        dialogueId: item.dialogueId,
    });
    if (res.code == '000000') {
        ElMessage({
            message: '删除成功',
            type: 'success',
        });
        setTimeout(() => {
            recordGetRecordList();
        }, 1010);
    }
    loading.value = false;
};

//退出登录
const sureOutLogin = () => {
    localStorage.removeItem('userInfo');
    dialogueList.value = []
    checkLoginStatus();
    Message.success('您已退出登录');
};

const newChat = () => {
    chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
    drawerVisible.value = false
    ElMessage({
        icon: SuccessFilled,
        message: '已是最新对话',
        customClass: 'custom-message',
    })
};

onMounted(() => {
    mittBus.on('showMenu', () => {
        console.log('showMenu');
        dialogueList.value = []
        drawerVisible.value = true
        
    });
    checkLoginStatus()
})




</script>


<style lang="scss">
.mobile-right-menu-wrap {
    border-radius: 12px 0px 0px 12px;
    display: flex;
    flex-direction: column;

    .el-drawer__header {
        padding: 0 16px;
        padding-top: 44px;
        height: 64px;
        margin-bottom: 0;
        box-sizing: content-box;
        width: calc(100% - 32px);

        .el-drawer__title {
            display: flex;
            align-items: center;
            width: calc(100% - 40px);

            .user-icon-wrap {
                width: 48px;
                height: 48px;
                background: #EBEDF0;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 50%;
                margin-right: 12px;
            }

            .user-name {
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 18px;
                color: #3F4247;
                line-height: 28px;
                text-align: left;
                font-style: normal;
                width: calc(100% - 60px);

                .user-text-wrap {
                    display: flex;
                    align-items: center;

                    .user-text {
                        margin-right: 13px;
                        max-width: 75%;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                    }
                }
            }
        }

        .close-btn {
            width: 40px;
            height: 40px;
            background: rgba(32, 101, 214, 0.1);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    }

    .el-drawer__body {
        padding: 16px;
        display: flex;
        flex-direction: column;
    }

    .btn-box {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .btn-item {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 146px;
            height: 48px;
            font-size: 16px;
            border-radius: 8px;

            .btn-item-icon {
                margin-right: 6px;
                margin-top: 6px;
            }
        }
    }

    .history-dialogue-title {
        padding-top: 24px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #3F4247;
        line-height: 28px;
        text-align: left;
        font-style: normal;
        margin-bottom: 16px;
    }

    .history-dialogue-wrap {
        flex: 1;
        display: flex;
        flex-direction: column;
        overflow: auto;

        .no-data {
            flex: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
            font-weight: 400;
            font-size: 16px;
            color: #3F4247;

            img {
                margin-top: 73px;
                margin-bottom: 8px;
            }
        }
    }

    .history-dialogue-list {
        .dialogueList-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            height: 40px;
            // background: #F4F6F9;
            border-radius: 4px;
            margin-bottom: 8px;
            width: 100%;
            .name {
                flex: 1;
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 16px;
                color: #3F4247;
                line-height: 24px;
                text-align: left;
                font-style: normal;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }

            .btn {
                width: 32px;
                height: 32px;
                display: flex;
                align-items: center;
                justify-content: center;

                img {
                    width: 17px;
                    height: 17px;
                }
            }
        }

        .dialogueList-item:hover {
            background: #F4F6F9;
        }
    }

    .footer-info {
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 400;
        font-size: 16px;
        color: #797F8A;
    }

}
</style>