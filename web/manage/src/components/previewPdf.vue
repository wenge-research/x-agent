<template>
    <div class="preview_content">
        <vue-pdf-app :pdf="fileUrl" :theme="'light'"
      :config="{toolbar: false}"></vue-pdf-app>
    </div>
</template>
<script>
import VuePdfApp from "vue-pdf-app";
import "vue-pdf-app/dist/icons/main.css";
export default {
    components: {
        VuePdfApp,
    },
    props: {
      fileUrl:String
    },
    data() {
        return {
        };
    },
};
</script>
​
<style lang="scss" scoped>
@mixin text-ellipsis($line: 2) {
    overflow: hidden;
    word-break: break-all;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: $line;
    -webkit-box-orient: vertical;
}
.preview_content {
    height: calc(100vh - 210px);
    margin: 0 auto;
    width: 100%;
    border-radius: 4px;
    border: 1px solid #e4e8ee;
    .w-spin {
        width: 100%;
        height: calc(100% - 6px);
    }
    :deep() {
        .w-spin-mask {
            background-color: #fff !important;
        }
    }

    #myFrame {
        height: 100%;
        width: 100%;
        overflow: auto;
    }
}
.w-btn {
    border-radius: 4px !important;
}
.title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    line-height: 22px;
    color: #181b49;
}
.content {
    color: #646479;
    padding-left: 32px;
}
.pop-footer {
    display: flex;
    justify-content: flex-end;
    overflow: hidden;
    margin-top: 10px;
}
.dialog-footer {
    display: flex;
    justify-content: center;
    .btn-left,
    .btn-right,
    .left-btn-box {
        display: flex;
    }
    .btn-left .left-btn-box {
        margin-right: 4px;
    }
}
.w-input-wrapper {
    width: 600px;
}
.title {
    max-width: 600px;
    font-size: 20px;
    color: #181b49;
    font-weight: 700;
    @include text-ellipsis(1);
}
</style>
<style lang="scss">
.pdf-app.light {
    background: #fff !important;
}
.pdf-app .pdfViewer .page {
    box-sizing: content-box !important;
    background-clip: content-box !important;
    -o-border-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAATCAYAAAByUDbMAAAA1ElEQVQ4jbWUWw6EIAxFy2NFs/8NzR4UJhpqLsdi5mOmSSMUOfYWqv3S0gMr4XlYH/64gZa/gN3ANYA7KAXALt4ktoQ5MI9YxqaG8bWmsIysMuT6piSQCa4whZThCu8CM4zP9YJaKci9jicPq3NcBWYoPMGUlhG7ivtkB+gVyFY75wXghOvh8t5mto1Mdim6e+MBqH6XsY+YAwjpq3vGF7weTWQptLEDVCZvPTMl5JZZsdh47FHW6qFMyvLYqjcnmdFfY9Xk/KDOlzCusX2mi/ofM7MPkzBcSp4Q1/wAAAAASUVORK5CYII=)
        9 9 repeat !important;
    border-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAATCAYAAAByUDbMAAAA1ElEQVQ4jbWUWw6EIAxFy2NFs/8NzR4UJhpqLsdi5mOmSSMUOfYWqv3S0gMr4XlYH/64gZa/gN3ANYA7KAXALt4ktoQ5MI9YxqaG8bWmsIysMuT6piSQCa4whZThCu8CM4zP9YJaKci9jicPq3NcBWYoPMGUlhG7ivtkB+gVyFY75wXghOvh8t5mto1Mdim6e+MBqH6XsY+YAwjpq3vGF7weTWQptLEDVCZvPTMl5JZZsdh47FHW6qFMyvLYqjcnmdFfY9Xk/KDOlzCusX2mi/ofM7MPkzBcSp4Q1/wAAAAASUVORK5CYII=)
        9 9 repeat !important;
    background-color: #fff !important;
}
.pdf-app #loadingBar .progress {
    display: none;
}
#viewerContainer {
    &::-webkit-scrollbar-thumb {
        background-color: #fff;
    }
    &:hover::-webkit-scrollbar-thumb {
        background-color: rgba(144, 147, 153, 0.3);
    }
}
</style>
