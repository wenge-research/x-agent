<template>
    <div class="tooltip-component">
        <!-- <el-popover
            :placement="placement"
            width="200"
            trigger="hover"
            :content="remark">
            <template #reference>
                <iconpark-icon  name="question-line"></iconpark-icon>
            </template>
        </el-popover> -->
        <span class="tooltip-component-icon" :title="remark" ><iconpark-icon :color="iconColor" name="question-line"></iconpark-icon></span>
    </div>
</template>
<script>
export default {
    props: {
        remark: {
            type: String,
            default: ''
        },
        placement: {
            type: String,
            default: 'right'
        },
        iconColor: {
            type: String,
            default: '#a6aab3'
        }
    }
}
</script>
<style lang="scss" scoped>
.tooltip-component{
    display: inline-block;
    user-select: none;
    .tooltip-component-icon{
        cursor: default;
    }
}
</style>
