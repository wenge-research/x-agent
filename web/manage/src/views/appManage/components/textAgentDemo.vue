<template>
    <div class="text-agent-demo">
        <agentPattern :model="appForm.type" ></agentPattern>
        <modelAlgorithmPlugin :appConfigForm="appForm"></modelAlgorithmPlugin>
    </div>

</template>
<script>
import modelAlgorithmPlugin from "@/views/systemManage/pluginManage/components/modelAlgorithmPlugin.vue";
import agentPattern from "@/views/appManage/components/agentPattern.vue";
export default {
    name: 'textAgentDemo',
    components: {
        modelAlgorithmPlugin,
        agentPattern
    },
    props: {
        
        appForm: Object
    }
}
</script>
<style scoped lang="scss">
.text-agent-demo{
    height: 100%;
}

</style>