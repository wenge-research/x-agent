<template>
    <el-dialog class="edit-phone-dialog" width="480px" :close-on-click-modal="false" :visible.sync="editPhoneDialog" @close="cancel">
        <div class="passwordModule-content">
            <div class="flex justify-between" >
            <div class="xiaoBiaoTi mb20" >绑定手机号</div>
            </div>				  
                <div class="content" style="margin-bottom: 20px;">
                <el-input
                v-model="smsLoginForm.phoneNum"
                placeholder="请输入手机号"
                class="shouJiInput"
                >
                <div slot="prepend" class="xuanZeQianZhui">
                  <el-select v-model="dianHuanQianZhui"  >
                    <el-option label="+86" value="86"></el-option>
                  </el-select>
                </div>
                </el-input>
            </div>
            <div class="content">
                <el-input
                v-model="smsLoginForm.code"
                placeholder="请输入验证码"
                >
                <el-button type="text" slot="suffix"  @click="countDown" :disabled="!isSend" :loading="yanZhenMaLoading" >{{codeName}}</el-button>
                </el-input>
            </div>				 
            <div class="mt25" >
            <div class="mb15"><el-button style="width:100%;" type="primary" @click="addUpdatePhone">{{ $t('confirm') }}</el-button></div>
            <div><el-button style="width:100%;" plain @click="cancel" >{{ $t('cancel') }}</el-button></div>
            </div>
        </div>
    </el-dialog>
</template>
<script>
import {
        sendSmsAuthentication,
		updatePhone
	} from "@/api/user";
    
export default {
	components: {
	},
    data() {
        return {
			 yanZhenMaLoading:false,
			 isSend: true,
			 codeName: "获取验证码",
			 totalTime: 300, //一般是60
			 yanZhengMadingshiqi:'', //定时器
           
			smsLoginForm:{
				phoneNum:"",
				codeKey:"",
				code:""
				
			},
            editPhoneDialog: false,
			dianHuanQianZhui: "86",
        }
    },
	//生命周期 - 挂载完成（可以访问DOM元素）
	mounted() {
	},
    methods: {
        open(){
            this.editPhoneDialog = true
        },
		// 验证码限流
		  countDown() {
		  if (!this.isSend) return
		  
		  // 判断手机号是否符合格式
		  const regex = /^(13[0-9]|14[01456879]|15[0-3,5-9]|16[2567]|17[0-8]|18[0-9]|19[0-3,5-9])\d{8}$/;
		  let aaa = regex.test(this.smsLoginForm.phoneNum);
		  if (!aaa) {
		    this.$message({
		      message: '请输入正确的手机号',
		      type: "warning",
		    });
		    return false;
		  }
		
		  // 先进行截止
		  this.isSend = false;
		
		  this.yanZhenMaLoading = true;
		  // 获取手机号的接口
		  sendSmsAuthentication({
		    phoneNum: this.smsLoginForm.phoneNum,
		  }).then((res) => {
        if(res.code === "0000001"){
          this.$message.warning(res.msg);
        }else{
		      this.smsLoginForm.codeKey = res.data;
        }
		    // 获取验证码成功后，进行倒计时
		    this.codeName = this.totalTime + 'S'
		    if(this.yanZhengMadingshiqi){
		      clearInterval(this.yanZhengMadingshiqi)
		    }
		    this.yanZhengMadingshiqi = setInterval(() => {
		      this.totalTime--
		      this.codeName = this.totalTime + 'S'
		      if (this.totalTime < 1) {
		        clearInterval(this.yanZhengMadingshiqi)
		        this.codeName = '获取'
		        this.totalTime = 300
		        this.isSend = true 
		      }
		    }, 1000)
		
		
		  }).catch((err) => {
		    this.$message({
		      message: '接口异常',
		      type: "error", 
		    })
		    this.isSend = true;
		  }).finally(() => {
		    this.yanZhenMaLoading = false;
		  });
		  
		  
		},
		
		cancel(){
            this.editPhoneDialog = false
            this.$router.push({
              name: "appmanage",
            });
        },
		addUpdatePhone(){
			updatePhone(this.smsLoginForm).then((data) => {
				if (data.code == "000000") {
					this.cancel()
				} else {
					this.$message.warning(data.msg);
				}
			}).finally(() => {
				
			});
		},
	}
}
</script>
<style lang="scss" scoped>
.outer {
    width: 100%;
    height: 100%;
    padding: 9px 24px 24px;
    font-family: MiSans, MiSans;

    .inner {
        width: 100%;
        height: calc(100% - 70px);
        // background: rgba(249, 250, 251, 1);
        // border-radius: 4px;
        // border: 1px solid rgba(239, 239, 239, 1);
        padding: 24px;
    }

    .title {
        height: 70px;
        line-height: 40px;
        padding: 16px 0 8px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 22px;
        color: #383d47;
    }

    .form-box {
        width: 960px;
        margin: 0 auto;

        .info-box {
            margin-bottom: 40px;

            .info-title {
                font-size: 20px;
                margin-bottom: 24px;
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 20px;
                color: #1D2129;
                line-height: 28px;
                text-align: left;
                font-style: normal;
            }

            .info-link-card {
                background: #fff;
                border-bottom: 1px solid #E7E7E7;
                height: 48px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding-right: 12px;

                // cursor: pointer;
                .info-link-label {
                    width: 98px;
                }

                .info-link-content {
                    flex: 1;
                    color: #86909C;
                    ;
                }

                .el-button {
                    padding: 0;

                    .el-icon-arrow-right {
                        color: #1D2129;
                    }

                }
            }

            .info-card {
                display: flex;
                align-items: center;
                justify-content: space-between;

                .left-card {
                    display: flex;
                    flex: 1;
                    overflow: hidden;

                    .user-logo {
                        margin-right: 28px;

                        img {
                            width: 104px;
                            height: 104px;
                        }
                    }

                    .user-info {
                        flex: 1;
                        overflow: hidden;

                        .user-name {
                            width: 100%;
                            font-family: MiSans, MiSans;
                            font-weight: 400;
                            font-size: 24px;
                            color: #1D2129;
                            line-height: 32px;
                            text-align: left;
                            font-style: normal;
                            margin-bottom: 12px;
                            margin-top: 14px;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;
                        }

                        .user-tag-list {
                            display: flex;
                            align-items: center;
                            flex-wrap: wrap;

                            .list-item {
                                padding: 6px 12px;
                                font-family: MiSans, MiSans;
                                font-weight: 400;
                                font-size: 14px;
                                color: #1D2129;
                                line-height: 20px;
                                text-align: left;
                                font-style: normal;
                                background: rgba(188, 193, 204, 0.2);
                                border-radius: 4px;
                                margin-right: 8px;
                                margin-bottom: 8px;
                            }
                        }
                    }
                }

                .right-card {
                    .edit-btn {
                        height: 40px;
                        color: rgba(29, 33, 41, 1);
                        border-radius: 4px;
                        border: 1px solid #E7E7E7;
                        padding: 0 20px;

                        ::v-deep span {
                            display: flex;
                            align-items: center;
                            line-height: 16px;
                        }
                    }
                }
            }
        }

        .loginOut-btn {
            display: flex;
            align-items: center;
            justify-content: center;

            .el-button {
                height: 40px;
                background: rgba(255, 235, 232, 1);
                color: rgba(245, 63, 63, 1);
                border: 0;
                width: 100%;
            }
        }
    }
}
.passwordModule-content {
	 padding: 0px 40px 20px;
   .xuanZeQianZhui{
      width: 50px;
    }
}
.xiaoBiaoTi{
  font-family: MiSans, MiSans;
  font-size: 18px;
  color: #494E57;
}
.edit-user-dialog {
    ::v-deep .el-dialog {
        min-width: 560px;
    }

    ::v-deep .el-dialog__body {
        padding: 0;
    }

    ::v-deep .el-dialog__header {
        padding-left: 32px;
        padding-right: 32px;
        color: #1D2129;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        color: #1D2129;
        line-height: 32px;
        text-align: left;
        font-style: normal;
        text-transform: none;

        .el-dialog__headerbtn {
            right: 32px;
            top: 28px;
        }
    }
}

.editContent {
    padding: 0 32px;
    padding-bottom: 50px;

    .form-box {
        width: 100%;

        .form-item {
            margin-bottom: 24px;

            .form-label {
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 14px;
                color: #1D2129;
                line-height: 20px;
                text-align: left;
                font-style: normal;
                margin-bottom: 8px;
            }

            .content {
                .info {
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 14px;
                    color: #86909C;
                    line-height: 20px;
                    text-align: left;
                    font-style: normal;
                    margin-bottom: 8px;
                }

                .upload-box {
                    display: flex;
                    align-items: center;

                    img {
                        width: 88px;
                        height: 88px;
                        margin-right: 16px;
                    }

                    .upload-demo {
                        .el-button {
                            padding: 0;
                            width: 104px;
                            height: 32px;
                            border-radius: 4px;
                            border: 1px solid #E7E7E7;

                            ::v-deep span {
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                color: #1D2129;
                                font-size: 14px;
                            }
                        }
                    }
                }
            }

            .tag-add-content {
                display: flex;
                align-items: center;
                margin-bottom: 16px;

                .el-input {
                    margin-right: 8px;
                }

                .el-button {
                    background: #1747E5;
                    border-radius: 4px;
                    height: 40px;
                    width: 40px;
                    color: #fff;
                }
            }

            .tag-list {
                display: flex;
                align-items: center;
                flex-wrap: wrap;

                .tag-item {
                    padding: 6px 12px;
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 14px;
                    color: #1D2129;
                    line-height: 20px;
                    background: rgba(188, 193, 204, 0.2);
                    border-radius: 4px;
                    margin-right: 8px;
                    margin-bottom: 8px;
                    display: flex;
                    align-items: center;
                }

            }
        }
    }
}
</style>