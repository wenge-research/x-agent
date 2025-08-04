<template>
    <div class="outer">
        <div class="title">个人中心</div>
        <div class="inner">
            <div class="form-box">
                <div class="info-box">
                    <div class="info-title">基本信息</div>
                    <div class="info-card">
                        <div class="left-card">
                            <div class="user-logo">
                                <!-- {{ infoData }} -->
                                <img src="@/assets/images/userlogo.png" v-if="infoData.avatar==null||infoData.avatar==''" alt="">
                                <img :src="infoData.avatar" v-else alt="">
                            </div>
                            <div class="user-info">
                                <div class="user-name">{{ infoData.accountName }}</div>
                                <div class="user-tag-list" v-if="tagNameList.length>0">
                                    <div class="list-item" v-for="(item,index) in labelList" :key="index">{{ item }}</div>
                                </div>
                            </div>
                        </div>
                        <div class="right-card">
                            <el-button class="edit-btn" type="text" @click="editDialog = true">
                                <iconpark-icon style="margin-right: 3px;" color="#1D2129" size="14"
                                    name="edit-box-line"></iconpark-icon>
                                编辑
                            </el-button>
                        </div>
                    </div>
                </div>
                <div class="info-box">
                    <div class="info-title">设置</div>
                    <div class="info-link-card" style="border-top: 1px solid #E7E7E7;">
                        <div class="info-link-label">手机号</div>
                        <div class="info-link-content">{{ maskPhoneNumber(infoData.phone) }}</div>
                        <el-button type="text" style="color: #1747E5 ;" @click="editPhoneDialog = true">更换</el-button>
                    </div>
                    <div class="info-link-card">
                        <div class="info-link-label">邮箱</div>
                        <div class="info-link-content">{{ infoData.email ? infoData.email : '-' }}</div>
                        <el-button type="text" style="color: #1747E5 ;" @click="editEmailDialog = true">{{ infoData.email ? '更换' : '绑定' }}</el-button>
                    </div>
                    <div class="info-link-card">
                        <div class="info-link-label">密码</div>
                        <el-button type="text" style="color: #1747E5 ;" @click="passwordModuleVisible = true">更改密码</el-button>
                    </div>
                    <div class="info-link-card" style="cursor: pointer;"
                        @click="$router.push({ name: 'userAgreement' })">
                        <div class="info-link-label">用户协议</div>
                        <el-button type="text"><i class="el-icon-arrow-right"></i></el-button>
                    </div>
                    <div class="info-link-card" style="cursor: pointer;"
                        @click="$router.push({ name: 'privacyAgreement' })">
                        <div class="info-link-label">隐私政策</div>
                        <el-button type="text"><i class="el-icon-arrow-right"></i></el-button>
                    </div>
                </div>
                <div class="loginOut-btn">
                    <el-button size="small" @click="quite">退出登录</el-button>
                </div>

            </div>
        </div>


        <el-dialog class="edit-user-dialog" title="编辑个人信息" :visible.sync="editDialog" width="30%">
            <div class="editContent">
                <div class="form-box">
                    <div class="form-item">
                        <div class="form-label">头像</div>
                        <div class="content">
                            <div class="info">支持jpg、jpeg、png格式图片，大小2MB及以内</div>
                            <div class="upload-box">
                                <img src="@/assets/images/userlogo.png" v-if="infoData.avatar==null||infoData.avatar==''" alt="">
								<img :src="infoData.avatar" v-else alt="">
                                <el-upload class="upload-demo" :action="actionUrl" :show-file-list="false"
                                    :before-upload="beforeupload" :on-success="handleLogoSuccess" accept=".jpg, .jpeg, .png" :limit="1">
                                    <el-button size="small"><iconpark-icon name="image-add-line" color="#1D2129"
                                            size="14" style="margin-right: 6px;"></iconpark-icon>上传图片</el-button>
                                </el-upload>
                            </div>
                        </div>
                    </div>
                    <div class="form-item">
                        <div class="form-label">用户名</div>
                        <div class="content">
                            <el-input type="text" placeholder="请输入内容" v-model="infoData.accountName" maxlength="20"
                                show-word-limit></el-input>
                        </div>
                    </div>
                    <div class="form-item">
                        <div class="form-label">标签</div>
                        <div class="content tag-add-content">
                            <el-input v-model="tagName" placeholder="请输入"></el-input>
                            <el-button type="text" @click="addtagName"><iconpark-icon name="add-line" size="14"
                                    color="#fff"></iconpark-icon></el-button>
                        </div>
                        <div class="tag-list" v-if="tagNameList.length>0">
                            <div class="tag-item" v-for="(item,index) in tagNameList" :key="index">{{item}}<iconpark-icon name="close-large-line" size="14" color="#86909C"
                                    style="margin-left: 10px;cursor: pointer;" @click="remove(index)"></iconpark-icon></div>
                        </div>
                    </div>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="editDialog = false">取 消</el-button>
                <el-button type="primary" @click="addUpdateCurrentAccount">确 定</el-button>
            </span>
        </el-dialog>
        <passwordModule :visible.sync="passwordModuleVisible" :type="passwordXiuGaiLeiXing"></passwordModule>
        <el-dialog class="edit-phone-dialog" width="480px" :visible.sync="editPhoneDialog">
            <div class="passwordModule-content">
              <div class="flex justify-between" >
            	<div class="xiaoBiaoTi mb20" >{{ $t("upPhone") }}</div>
              </div>				  
            	 <div class="content" style="margin-bottom: 20px;">
            	  <el-input
            	    v-model="smsLoginForm.phoneNum"
            	    placeholder="请输入手机号"
            	    class="shouJiInput"
            	  >
            	    <div slot="prepend" class="xuanZeQianZhui" >
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
            	<div><el-button style="width:100%;" plain @click="editPhoneDialog = false" >{{ $t('cancel') }}</el-button></div>
              </div>
            </div>
        </el-dialog>
		 <el-dialog class="edit-phone-dialog" width="480px" :visible.sync="editEmailDialog">
				<div class="passwordModule-content">
				  <div class="flex justify-between" >
					<div class="xiaoBiaoTi mb20" >{{ $t("bindEmail") }}</div>
				  </div>				  
					 <div class="content">
					  <el-input
						type="text"
						v-model="infoData.email"
						placeholder="请输入邮箱"
					  />
					</div>				 
				  <div class="mt25" >
					<div class="mb15"><el-button style="width:100%;" type="primary" @click="addUpdateCurrentAccount">{{ $t('confirm') }}</el-button></div>
					<div><el-button style="width:100%;" plain @click="editEmailDialog = false" >{{ $t('cancel') }}</el-button></div>
				  </div>
				</div>
		 </el-dialog>
    </div>

</template>
<script>
	import passwordModule from "@/views/Login/passwordModule.vue"
import {
		currentAccount,
		changPw,
		updateCurrentAccount,
        sendSmsAuthentication,
		updatePhone
	} from "@/api/user";
import { logout } from "@/api";
export default {
	components: {
	  passwordModule
	},
    data() {
        return {
			actionUrl: `${process.env.VUE_APP_BASE_API}/matterGuideFiled/uploadPic`,
            infoData: {},
            editDialog: false,
			passwordModuleVisible: false,
			 yanZhenMaLoading:false,
			 smsCodeKey:'',
			 isSend: true,
			 codeName: "获取验证码",
			 totalTime: 60, //一般是60
			 yanZhengMadingshiqi:'', //定时器
			 passwordXiuGaiLeiXing: "xiuGai",
            form: {

            },
			smsLoginForm:{
				phoneNum:"",
				codeKey:"",
				code:""
				
			},
			labelList:[],
			tagNameList:[],
            tagName: "",
            editPhoneDialog: false,
			editEmailDialog: false,
			dianHuanQianZhui: "86",
        }
    },
	//生命周期 - 挂载完成（可以访问DOM元素）
	mounted() {
		this.getUser();
	},
    methods: {
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
		    
		    this.smsLoginForm.codeKey = res.data;
		
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
		        this.totalTime = 60
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
		remove(index){
			this.tagNameList.splice(index, 1);			
		},
		getUser(){
			currentAccount().then((data) => {
				if (data.code == "000000") {
					this.infoData = data.data
					if(this.infoData.lable!=''&&this.infoData.lable!=null){
						this.tagNameList = this.infoData.lable.split(',')
						this.labelList = this.infoData.lable.split(',')
					}
				} else {
					this.$message.warning(data.msg);
				}
			}).finally(() => {
				
			});
		},
		addUpdateCurrentAccount(){
			this.infoData.lable = this.tagNameList.toString()
			updateCurrentAccount(this.infoData).then((data) => {
				if (data.code == "000000") {
					this.editDialog = false
					this.editEmailDialog = false
					this.getUser()
				} else {
					this.$message.warning(data.msg);
				}
			}).finally(() => {
				
			});
		},
		addUpdatePhone(){
			updatePhone(this.smsLoginForm).then((data) => {
				if (data.code == "000000") {
					this.editPhoneDialog = false
					this.getUser()
				} else {
					this.$message.warning(data.msg);
				}
			}).finally(() => {
				
			});
		},
		addtagName(){
      if(this.tagName){
        this.tagNameList.push(this.tagName)
			  this.tagName = ''
      } else {
        this.$message.warning('请输入标签名称');
      }
		},
        beforeupload(file) {

        },
		handleLogoSuccess(response, file, fileList) {
			if ((response.code = "000000")) {
				console.log('执行到这',response.data)
				this.infoData.avatar =
					response.data 				
			} else {				
				this.infoData.avatar = "";				
			}
		},
        maskPhoneNumber(phone) {
            if (!phone) return '';
            return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
        },
        quite() {
            this.$confirm("确认退出登录吗?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                confirmButtonClass: "confirm-ok",
                cancelButtonClass: "confirm-cancel",
            }).then(async () => {
                const res = await logout();
                console.log(res);
                sessionStorage.clear();
                this.$router.replace({
                    name: "login",
                });
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