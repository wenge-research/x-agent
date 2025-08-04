<template>
    <el-dialog custom-class="no-header-dialog" :show-close="false" width="390px" v-model="visible" 
        @close="handleClose" :close-on-press-escape="false">
        <div class="dialog-box">
            <div class="box-right">
                <div>
                    <img src="../../../assets/login/sign.png" alt="">
                </div>
                <div class="info-box">
                    <div class="title">账号登录</div>
                    <div class="auth-container">
                        <div class="input-group">
                            <el-input type="tel" size="large" id="phone" placeholder="请输入账号" v-model="user" required
                                @blur="validateInput" :prefix-icon="User" />
                            <div v-if="showError" class="error-message">
                                请输入账号
                            </div>
                        </div>
                        <div class="input-group code-box">
                            <el-input type="text" size="large" id="verification-code" placeholder="请输入密码" show-password
                                v-model="passWord" :prefix-icon="Lock" required />
                        </div>
                        <!-- <div class="checkbox-group">
                            <input type="checkbox" v-model="agreedToTerms" required />
                            <div class="text">
                                未注册的手机号将自动注册，勾选即代表您已阅读并同意 <a href="#" @click.prevent="showTermsModal">用户协议</a> 和 <a
                                    href="#" @click.prevent="showTermsModal">隐私政策</a>
                            </div>
                        </div> -->
                        <button class="login-btn" @click="handleSubmit">登录</button>
                        <!-- <div v-if="termsModalVisible" class="terms-modal">
                            <p>这里是用户协议和隐私政策的内容...</p>
                            <button @click="hideTermsModal">关闭</button>
                        </div> -->
                    </div>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script lang="ts" setup>
import { User, Lock } from '@element-plus/icons-vue';
import { defineProps, ref, computed } from 'vue';
import { login } from '/@/api/chat';
import { emit } from 'vue';
import { useLoginApi } from '/@/api/login';
import { Message } from 'winbox-ui-next';
import md5 from 'js-md5';
const user = ref<string>('');   //账号
const passWord = ref<string>(''); //密码
const passWordStr = ref<string>('');  //密码加密
const termsModalVisible = ref<boolean>(false);

//父组件传值监控登录窗口的显示
const props = defineProps({
    modelValue: {
        default: false
    }
})
/** 弹框状态绑定 */
const emit = defineEmits(['update:modelValue']);
const visible = computed({
    get() {
        return props.modelValue
    },
    set(newValue) {
        emit("update:modelValue", newValue)
    }
})
/** 关闭弹框 */
const handleClose = () => {
    visible.value = false
};
//是否账号输入校验文字提示
const showError = ref<boolean>(false);
const validateInput = () => {
    if (!user.value.trim()) {
        showError.value = true
    } else {
        showError.value = false
    }
}


//账号密码登录
interface UserCredentials {
    username: string;
    mykey: string;
}
const handleSubmit = async () => {
    passWordStr.value = md5(passWord.value);
    if (!user.value) return;
    // 处理登录/注册请求
    try {
        const parms: UserCredentials = {
            username: user.value,
            mykey: passWordStr.value
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
            visible.value = false;
            Message.success('登录成功');
            emit('login-success');
        } else {
            Message.error(res.msg)
            console.error('API 请求失败，错误代码:', res.code);
        }
    } catch (e) {
        console.error('error' + e);
    }
};

const showTermsModal = () => {
    termsModalVisible.value = true;
};

const hideTermsModal = () => {
    termsModalVisible.value = false;
};

</script>

<style scoped lang="scss">
.dialog-box {
    display: flex;
    background: linear-gradient(rgba(32, 101, 214, 0.1) 0, rgba(32, 101, 214, 0) 100%) #FFFFFF;
    border-radius: 8px;

    .box-right {
        box-sizing: border-box;
        width: 384px;
        padding-left: 48px;
        padding-top: 40px;
        padding-right: 48px;

        .info-box {
            margin-top: 24px;

            .title {
                font-size: 16px;
                color: #3F4247;
            }
        }

        .auth-container {
            margin-top: 16px;
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        .input-group {
            border-radius: 8px;
            margin-bottom: 15px;


            label {
                display: inline-block;
                width: 50px;
            }

            .error-message {
                color: #ff4d4f;
                font-size: 12px;
                margin-top: 4px;
                height: 18px;
                line-height: 18px;
            }
        }

        .code-box {
            position: relative;

            .code {
                position: absolute;
                bottom: 8px;
                right: 10px;
                font-size: 14px;
                color: #1747E5;
                cursor: pointer;
            }
        }


        .input-group input {
            width: calc(100%);
            padding: 8px;
            box-sizing: border-box;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        .login-btn {
            margin-top: 10px;
            height: 40px;
            background: #2065D6;
            border-radius: 8px;
            margin-bottom: 50px;
        }

        .checkbox-group {
            margin-top: 20px;
            display: flex;
            align-items: center;
            margin-bottom: 24px;

            .text {
                color: #797F8A;
                font-size: 12px;
            }
        }

        .checkbox-group input[type="checkbox"] {
            margin-right: 8px;
        }

        .checkbox-group label {
            flex: 1;
        }

        .checkbox-group a {
            color: blue;
            text-decoration: none;
            cursor: pointer;
        }

        .checkbox-group a:hover {
            text-decoration: underline;
        }

        button {
            width: 100%;
            padding: 10px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        button:hover {
            background-color: #0056b3;
        }

        .terms-modal {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            z-index: 1000;
        }

        .terms-modal p {
            margin-bottom: 10px;
        }

        .terms-modal button {
            width: auto;
            margin-top: 10px;
        }
    }
}

.no-header-dialog .el-dialog__header {
    display: none !important;
}
</style>