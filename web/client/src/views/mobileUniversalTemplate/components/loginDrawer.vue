<template>
    <el-drawer class="mobile-login-wrap" v-model="drawerVisible" :append-to-body="true" :show-close="false" size="100%">
        <template #header="{ close, titleId, titleClass }">
            <div :id="titleId" :class="titleClass">
                <div class="back-btn">
                    <iconpark-icon name="arrow-go-back-fill" color="#3F4247" size="20" style="cursor: pointer"
                        @click="close"></iconpark-icon>
                </div>
            </div>
        </template>
        <div class="login-content">
            <div class="logo-wrap">
                <img v-if="logoUrl()" class="logo" style="cursor: pointer" :src="logoUrl()" />
                <img v-else class="logo" style="cursor: pointer" src="/src/assets/chatImages/pageTitle.svg" />
            </div>
            <div class="login-form">
                <div class="title">用户登录</div>
                <el-form class="login-form-content" ref="ruleFormRef" :model="ruleForm" :rules="rules"
                    label-width="auto">
                    <el-form-item prop="user">
                        <el-input type="tel" size="large" id="phone" placeholder="请输入账号" v-model="ruleForm.user"
                            :prefix-icon="User" />
                    </el-form-item>
                    <el-form-item prop="passWord">
                        <el-input type="text" size="large" id="verification-code" placeholder="请输入密码" show-password
                            v-model="ruleForm.passWord" :prefix-icon="Lock" />
                    </el-form-item>
                </el-form>
                <el-button class="login-btn" type="primary" @click="submitForm()">
                    登录
                </el-button>
            </div>
        </div>

    </el-drawer>
</template>

<script setup>
import { ref, watch, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { User, Lock } from '@element-plus/icons-vue';
import { useLoginApi } from '/@/api/login';
import md5 from 'js-md5';
import mittBus from '/@/utils/mitt';
import { Message } from 'winbox-ui-next';

const drawerVisible = ref(false);

const route = useRoute();
const logoUrl = () => {
    let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
    return appInfo ? appInfo.logo : '';
};
const ruleForm = reactive({
    user: '',
    passWord: '',
})

const rules = reactive({
    user: [
        { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    passWord: [
        { required: true, message: '请输入密码', trigger: 'blur' },
    ],
})

const ruleFormRef = ref(null);
const emit = defineEmits(['login-success']);
const submitForm = async () => {
    if (!ruleFormRef.value) return
    await ruleFormRef.value.validate(async (valid, fields) => {
        if (valid) {
            console.log('submit!')
            // 处理登录/注册请求
            try {
                const parms = {
                    username: ruleForm.user,
                    mykey: md5(ruleForm.passWord)
                }
                const res = await useLoginApi().login(parms);
                console.log(res)
                if (res.code === '000000') {
                    const userInfo = {
                        accessToken: res.data.accessToken,
                        user: res.data.user
                    };
                    localStorage.setItem('userInfo', JSON.stringify(userInfo));
                    sessionStorage.setItem('manageAccessToken', res.data.accessToken);
                    sessionStorage.setItem('userId', res.data.user.id);
                    drawerVisible.value = false;
                    Message.success('登录成功');
                    emit('login-success');
                } else {
                    Message.error(res.msg)
                    console.error('API 请求失败，错误代码:', res.code);
                }
            } catch (e) {
                console.error('error' + e);
            }
        } else {
            console.log('error submit!', fields)
        }
    })
}

onMounted(() => {
    mittBus.on('showLoginDrawer', () => {
        console.log('showLoginDrawer');
        drawerVisible.value = true
    });
})




</script>

<style lang="scss" scoped></style>
<style>
.mobile-login-wrap {
    display: flex;
    flex-direction: column;
    background: linear-gradient(to top, rgba(32, 101, 214, 0) 0%, rgba(32, 101, 214, 0.1) 100%), #FFFFFF;

    .el-drawer__header {
        padding: 0 24px;
        padding-top: 56px;
        margin-bottom: 0;
        box-sizing: content-box;

        .back-btn {
            width: 40px;
            height: 40px;
            background: rgba(134, 144, 156, 0.1);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    }

    .el-drawer__body {
        padding: 24px;
        display: flex;
        flex-direction: column;

        .login-content {
            .logo-wrap {
                height: 40px;
                margin-bottom: 28px;

                img {
                    height: 100%;
                    /* width: ; */
                }
            }

            .login-form {
                .title {
                    font-family: MiSans, MiSans;
                    font-weight: 500;
                    font-size: 20px;
                    color: #3F4247;
                    line-height: 28px;
                    text-align: left;
                    font-style: normal;
                    margin-bottom: 17px;
                }

                .login-form-content {
                    margin-bottom: 32px;

                    .el-input {
                        font-size: 16px;

                        .el-input__wrapper {
                            height: 48px;
                            border-radius: 8px;
                        }

                        .el-input__wrapper.is-focus {
                            box-shadow: none;
                            border: 1px solid #2065D6;
                        }

                        .el-input__prefix-inner .el-icon {
                            font-size: 24px;
                        }

                        .el-input__suffix-inner .el-icon {
                            font-size: 16px;
                        }
                    }
                }

                .login-btn {
                    width: 100%;
                    height: 48px;
                    background: #2065D6 !important;
                    border-radius: 8px;
                    color: #fff !important;
                    font-family: MiSans, MiSans;
                    font-weight: 500;
                    font-size: 16px;
                    color: #FFFFFF;
                    line-height: 24px;
                    font-style: normal;
                }
            }
        }
    }


}
</style>