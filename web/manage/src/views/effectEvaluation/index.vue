<template>
    <div class="effectEvaluation-view">
        <transition name="el-fade-in-linear">
           <effectEvaluationTable v-if="!createEvaluationTaskVisible" @createEvaluation="createEvaluation"></effectEvaluationTable>
        </transition>
        <transition name="el-fade-in-linear">
            <createEvaluationTask v-if="createEvaluationTaskVisible" @closePage="closePage"></createEvaluationTask>
        </transition>
    </div>
</template>

<script>
import effectEvaluationTable from './components/effectEvaluationTable.vue';
import createEvaluationTask from './components/createEvaluationTask.vue'

export default {
    components: {
        effectEvaluationTable,
        createEvaluationTask
    },
    data(){
        return {
            createEvaluationTaskVisible: false
        }
    },
    methods: {
        createEvaluation(){
            this.createEvaluationTaskVisible = true;
        },
        closePage(){
            this.createEvaluationTaskVisible = false;
        }
    }
}

</script>

<style lang="scss" scoped>
.effectEvaluation-view{
    width: 100%;
    height: 100%;
}
</style>