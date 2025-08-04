<template>
  <div class="information-view" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
    <div class="info-box">
      <div class="title-box">
        <div class="close">
          <van-icon name="arrow-left" color="#fff" @click="back" />
        </div>
        <div class="title">基本信息</div>
      </div>
    </div>
    <div class="con-box">
      <div class="top">
        <div class="success-box">
          <img src="/src/assets/img/success.png" alt="" />
          <div class="title">信息提交成功</div>
          <div class="sub-title" v-if="userInfo.checkStatus == 'waiting'">请耐心等待审核结果</div>
          <div class="sub-title" v-if="userInfo.checkStatus == 'pass'">审核通过</div>
          <div class="sub-title" v-if="userInfo.checkStatus == 'reject'">审核不通过</div>
        </div>
      </div>
      <div class="bottom">
        <div class="form-title">你提交的信息</div>
        <div class="info-item" v-for="(item, index) in userInfoMapping" :key="index">
          <div class="info-label">{{ item.label }}</div>
          <div class="info-content">{{ userInfo[item.key] }}</div>
        </div>
        <div class="btns">
          <van-button
            @click="backHome"
            round
            style="
              margin-right: 16px;
              border-radius: 24px;
              border: 1px solid #169e9a;
              flex: 1;
              background: none;
              color: #169e9a;
            "
            type="success"
            >返回首页</van-button
          >
          <van-button
            @click="toEdit"
            round
            style="
              border-radius: 24px;
              border: 1px solid #c4c6cc;
              flex: 1;
              background: none;
              color: #383d47;
            "
            type="success"
            >重新修改</van-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter, useRoute } from "vue-router";
import { Message } from "winbox-ui-next";
import { ref, onMounted } from "vue";
import { fillInPersonal, getUserDetail } from "/@/api/manage/index";
const router = useRouter();
const route = useRoute();
const userInfoMapping = ref([
  {
    label: "人员类型",
    key: "userTypeName",
  },
  {
    label: "姓名",
    key: "username",
  },
]);
const userInfo = ref({});
const govList = ref({
   "gov": "政务人员",
   "gov-street": "政务人员-街道工作人员",
   "gov-community": "政务人员-社区工作人员",
});
onMounted(() => {
  getUserDetailFun();
});
// 返回
const back = () => {
  router.back();
};
// 返回首页
const backHome = () => {
  // router.push('/previewChat/zgc');
  if(getAppDetail()?.mobileTemplateRoute == 'assistantMobile') {
    router.push(`/assistantHome/${getAppDetail()?.applicationCode}`);
  } else {
    router.push(`/previewChat/${getAppDetail()?.applicationCode}`);
  }
  
};
// 重新修改
const toEdit = () => {
  router.push(`/personalCenterEdit/${getAppDetail()?.applicationCode}`);
};
const getAppDetail = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo ? appInfo : "";
};
const touchStartX = ref(0);
const handleTouchStart = (event) => {
	touchStartX.value = event.touches[0].clientX;
};

const handleTouchEnd = (event) => {
	const touchEndX = event.changedTouches[0].clientX;
	const deltaX = touchEndX - touchStartX.value;

	// 如果从左向右滑动超过一定距离，返回上一页
  if (deltaX > 50) {
		var ua = navigator.userAgent.toLowerCase();
		// 判断是否在微信浏览器内
		if (ua.match(/MicroMessenger/i) == 'micromessenger') {
			wx.miniProgram.getEnv((res) => {
				if (res.miniprogram) {
					wx.miniProgram.navigateBack();
				} else {
					console.log('不在小程序内');
					router.push(`/homePersonalCenter/${getAppDetail()?.applicationCode}`); 
				}
			});
		} else {
			console.log('不在微信浏览器内');
      router.push(`/homePersonalCenter/${getAppDetail()?.applicationCode}`);
		}
	}
};
// 查询部门信息树
const getUserDetailFun = async () => {
  try {
    const res = await getUserDetail({
      userId: sessionStorage.getItem("userId"),
    });
    userInfo.value = res.data;
    userInfo.value.userTypeName = "";
    if (res.data.userType == "resident") {
      userInfo.value.userTypeName = "居民";
      let arr = [
        {
          label: "联系电话",
          key: "phone",
        },
        {
          label: "身份证号",
          key: "idNo",
        },
      ];
      userInfoMapping.value = userInfoMapping.value.concat(arr);
    }
    if (res.data.userType == 'staff-street') {
      userInfo.value.userTypeName = '街道工作人员';
      let arr = [
        {
          label: "部门",
          key: "deptName",
        },
        {
          label: "职务",
          key: "workPosition",
        },
        {
          label: "主要工作",
          key: "mainDuty",
        },
        {
          label: "联系电话",
          key: "phone",
        },
        {
          label: "座机",
          key: "landline",
        },
        {
          label: "工作状态",
          key: "workStatusName",
        },
      ];
      userInfoMapping.value = userInfoMapping.value.concat(arr);
    }
    if (res.data.userType == 'staff-community') {
      userInfo.value.userTypeName = '社区工作人员';
      let arr = [
        {
          label: "部门",
          key: "deptName",
        },
        {
          label: "职务",
          key: "workPosition",
        },
        {
          label: "主要工作",
          key: "mainDuty",
        },
        {
          label: "联系电话",
          key: "phone",
        },
        {
          label: "座机",
          key: "landline",
        },
        {
          label: "工作状态",
          key: "workStatusName",
        },
      ];
      userInfoMapping.value = userInfoMapping.value.concat(arr);
    }
    if (res.data.userType.indexOf('gov') > -1) {
      userInfo.value.userTypeName = govList.value[res.data.userType];
      let arr = [
        {
          label: "部门",
          key: "deptName",
        },
        {
          label: "职务",
          key: "workPosition",
        },
        {
          label: "主要工作",
          key: "mainDuty",
        },
        {
          label: "联系电话",
          key: "phone",
        },
        {
          label: "座机",
          key: "landline",
        },
        {
          label: "工作状态",
          key: "workStatusName",
        },
      ];
      userInfoMapping.value = userInfoMapping.value.concat(arr);
    }
    console.log(userInfoMapping.value);

    let workStatusColumns = [
      {
        text: "在职",
        value: "on",
      },
      {
        text: "离职",
        value: "off",
      },
      {
        text: "调离",
        value: "transferred",
      },
      {
        text: "休假",
        value: "retired",
      },
      {
        text: "病休",
        value: "sick",
      },
      {
        text: "退休",
        value: "retire",
      },
    ];
    userInfo.value.workStatusName =
      workStatusColumns.find((item) => item.value == userInfo.value.workStatus)?.text ||
      "";
    sessionStorage.setItem("userInfo", JSON.stringify(userInfo.value));
  } catch (err) {
    throw new Error(err);
  }
};
</script>

<style lang="scss" scoped>
.information-view {
  width: 100%;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  background: #428389;

  .info-box {
    width: 100%;
    height: auto;

    .title-box {
      width: 100%;
      height: 48px;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;

      .title {
        font-weight: 500;
        font-size: 18px;
        font-weight: bold;
        color: #ffffff;
      }

      .close {
        flex-shrink: 0;
        position: absolute;
        width: 20px;
        height: 20px;
        left: 25px;
      }
    }
  }

  .con-box {
    width: 100%;
    height: calc(100vh - 48px);
    overflow-y: scroll;
    background: #ffffff;
    border-radius: 8px 8px 0px 0px;
    .top {
      height: 184px;
      width: 100%;
      display: flex;
      justify-content: center;
      .success-box {
        height: 100%;
        padding: 18px 0;
        text-align: center;
        img {
          margin: 0 auto;
          width: 96px;
          height: 96px;
        }
        .title {
          font-family: MiSans, MiSans;
          font-weight: 700;
          font-size: 18px;
          color: #434649;
          margin-top: 8px;
        }
        .sub-title {
          font-size: 14px;
          color: #b4bccc;
          margin-top: 4px;
        }
      }
    }
    .bottom {
      padding: 24px 16px;
      .form-title {
        font-weight: 700;
        font-size: 16px;
        color: #434649;
      }
      .info-item {
        display: flex;
        align-items: center;
        margin-top: 16px;
        .info-label {
          width: 80px;
          font-size: 16px;
          color: #797991;
        }
        .info-content {
          font-size: 16px;
          color: #434649;
        }
      }
    }
    .btns {
      display: flex;
      align-items: center;
      margin-top: 36px;
    }
  }
}
</style>
