<template>
    <div class="upload" :class="fileLists.length > 0 ? '' : 'nodata'">
      <w-upload
          list-type="picture-card"
          :file-list="fileLists"
          :custom-request="uploadRequest"
          accept=".pdf,.docx,.doc"
          multiple
          :limit=8
        >
          <template #upload-button>
            <div class="w-upload-picture-card" v-if="fileLists.length">
              <div class="w-upload-picture-card-text">
                <CoolShangchuan  size="24" color="rgb(var(--primary-6))"/>
                <div class="themeColor" style="margin-top: 10px;color: #355EFF;">上传附件</div>
              </div>
            </div>
            <div class="upload-button-nodata"  v-if="!fileLists.length">
                
                <div class="tipUpload">
                  <i><img :src="uploadImg" alt=""></i>
                  <div> 拖拽或<span class="themeColor" style="color: #355EFF;">点击上传</span>文件，最多 8 个文件，支持pdf、docx、doc，单个文件最大上传大小10M。</div>
                </div>
                <!-- <div class="tipUpload" v-else>
                    <CoolJiazai spin size="24"/>
                    <span>上传中...</span>
                </div> -->
                
            </div>
            </template>
            <template #upload-item="{ fileItem }">
              <div class="w-upload-list-picture" v-if="fileLists.length > 0 && fileItem.status != 'error' ">
                <div class="w-upload-list-picture-new">
                  <img :src="fileImg" alt="">
                  <i class="typeIcon">
                    <CoolChenggong v-if="fileItem.fileUrl !== ''" size="24" />
                    <CoolJiazai spin v-else size="24"/>
                  </i>
                  <span class="name"> {{fileItem.fileName}}</span>
                </div>
                <div class="w-upload-list-picture-mask">
                  <div class="w-upload-list-picture-operation">
                    <span class="w-upload-icon w-upload-icon-remove" @click="beforeRemove(fileItem)">
                      <CoolShanchu  size="24" color="rgb(var(--primary-6))"/>
                    </span>
                  </div>
                </div>
               
              </div>
            </template>
    </w-upload>
    </div>
</template>

<script lang="ts" setup>
  import { onMounted, reactive,ref, computed,defineExpose, nextTick,watch } from 'vue';
  import { Modal } from 'winbox-ui-next'
  import { delFile } from '/@/api/chat'
  import { Session } from '/@/utils/storage';
  import { useRoute } from 'vue-router';
  import uploadImg from '/@/assets/chat/upload.svg';
  import fileImg from '/@/assets/chat/file.png';
  import { useChatStore } from '/@/stores/chat';
  import { Message } from 'winbox-ui-next'

  const chatStore = useChatStore();

  const loading = ref(false)
  const mark = ref(true)
  const route = useRoute();

  const { conversationId } = route.params as { conversationId: string }


  const fileLists = computed(() => chatStore.fileList);

const delay = () => {
  return new Promise((resolve, reject) => {
    setInterval(() => {
      if (route.params.conversationId) {
        resolve()
      }
    }, 100);
  }) 
}

  const  uploadRequest = async (option) => {
    const { onProgress, onError, onSuccess, fileItem} = option
    if(fileItem.file.size > 10000000){
      Message.warning('最大上传文件不超10M')
      return onError();
    }
    if(!route.params.conversationId && mark.value ){
      mark.value = false
      await chatStore.addHistory({appId: route.params.appId},{ name: '新建会话'});
    }
    await delay()
    setList(option)
   

    const xhr = new XMLHttpRequest();
    loading.value = true
    if (xhr.upload) {
      xhr.upload.onprogress = function (event) {
        let percent;
        if (event.total > 0) {
          percent = (event.loaded / event.total) * 100;
        }
        onProgress(parseInt(percent, 10), event);
      };
    }
    xhr.onerror = function error(e) {
      onError(e);
    };
    xhr.onload = function onload() {
      if (xhr.status < 200 || xhr.status >= 300) {
        return onError(xhr.responseText);
      }
      uploadSuccessNew(xhr.response)
      onSuccess(xhr.response);

    };

    const formData = new FormData();
    formData.append('files', fileItem.file);
    formData.append('conversationId', route.params.conversationId);
    xhr.open('post', `api/dialogue/uploadBatch/${route.params.conversationId}`, true);
    xhr.setRequestHeader('Authorization', Session.get('token'))
    xhr.send(formData);

    return {
      abort () {
        xhr.abort()
      }
    }
  }


  const beforeRemove = (file) => {
    // return new Promise((resolve, reject) => {
    //     Modal.confirm({
    //       content: `确认删除 ${file.fileName}？`,
    //       onOk: () => {
    //         removeFile(file.id)
    //       },
    //       onCancel: () => reject('cancel'),
    //     });
    // });
    removeFile(file.id)
  }
  const removeFile = async(id) => {
     let res = await delFile(id);
     if(res.code === 200){
      chatStore.delFileList(id)
        
     }
     
  }

  const uploadSuccess = (res) => {
      loading.value = false
      chatStore.setFileList({
        fileName: JSON.parse(res.response).data[0].fileName,
        id: JSON.parse(res.response).data[0].id,
      });
  }
  const uploadSuccessNew = (res) => {
      chatStore.fileList.forEach((item) => {
        if(item.fileName == JSON.parse(res).data[0].fileName && !item.fileUrl){
          item.loading = false
          item.id =  JSON.parse(res).data[0].id
          item.fileUrl = '111'
        }
      })
  }
  

  const setList = (res) => {
      // loading.value = false
      chatStore.setFileList({
        fileName: res.fileItem.name,
        fileUrl:'',
        id: 0,
        uid: res.fileItem.uid
      });
  }
  
  
  onMounted(() => {
    //getListFile()
  })

  // defineExpose({ getListFile })

watch(
() => route.params.conversationId,
(newVal: any) => {
	mark.value = true
}
);

</script>

<style scoped lang="scss">
  .upload {
    display: flex;
    align-items: center;
    // background: rgba(255,255,255,0.5);

    .themeColor: {
      color: var(--w-color-primary);
    }
    :deep(.w-upload-list-picture){
      display: flex;
      align-items: center;
      width: 11.6%;
      min-width: 99px;
    }
    :deep(.w-upload-list-picture-new){
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      position: relative;
      img{
        height: 48px;
        width: initial;
        margin-bottom: 8px;
      }
      .name{
        width: 108px;
        word-wrap: break-word;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        color: #646479;
        font-size: var(--font14);
        padding: 0 2px;
        height: 38px;
      }
      .typeIcon{
        position: absolute;
        left: 60px;
        top: 30px;

      }
    }
    :deep(.w-upload-list-picture){
      line-height: initial;
    }
    .w-upload-list-picture-mask{
      background: rgba(53, 94, 255, 0.06);
      border-radius: 12px;
    }
    .w-upload-list-picture-operation{
      height: 100%;
      align-items: center;
    }
    :deep(.w-upload-list-type-picture-card){
      width: 100% !important;
    }
    .w-upload-icon-remove{
      width: 48px;
      height: 48px;
      background: #fff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .w-upload-picture-card{
      border-radius: 8px;
      border: 1px solid #E4E8EE;
      background: none;
    }
    .upload-button-nodata{
      display: flex;
      width: 900px;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      padding: 9px 0;
    }
    .tipUpload{
      width: 900px;
      display: flex;
      flex-direction: column;
      align-items: center;
    }

  }
  .nodata .w-upload-wrapper.w-upload-wrapper-type-picture-card {
    justify-content: center;
    :deep(.w-upload-list-type-picture-card){
      display: flex;
      justify-content: center;
    }
  }

</style>
