<template>
    <div class="Ec-x6-icon">
        <el-drawer
            title=""
            :visible.sync="drawerVisible"
            :modal="false"
            :modal-append-to-body="false"
            :direction="direction"
            v-if="drawerVisible"
            size="500px"
            style="
                position: absolute;
                width: 500px;
                box-sizing: border-box;
                right: 0;
                left: inherit;
            "
            :show-close="false"
        >
            <div
                style="
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                "
            >
                <HeadTool
                    v-if="drawerVisible"
                    :label="sourceData.label"
                    :imgWidth="24"
                    :imgHeight="24"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                    @testNodes="openRunDrawer"
                    imgSuffix="yiyouagent"
                />
                <div
                    style="
                        width: 1px;
                        height: 20px;
                        background: #d3d9e6;
                        margin: 0 12px 12px;
                    "
                ></div>
                <iconpark-icon
                    name="close-line"
                    size="18"
                    color="#828894"
                    @click="closeDrawer"
                    style="margin-bottom: 10px; cursor: pointer"
                ></iconpark-icon>
            </div>
            <div class="sub" v-if="nodeStoreData.toolData.introduce">{{nodeStoreData.toolData.introduce}}</div>
            <div class="drawer-content">
              <el-collapse v-model="activeNames">
                <!-- <el-collapse-item name="0" :title="$t('model')">
                  <modelSelect v-if="drawerVisible" :key="appForm.modelId" :id="appForm.modelId" @change="modelChange">
                  </modelSelect>
                </el-collapse-item> -->
                <el-collapse-item name="1">
                  <template #title>
                    <div class="systemPromptTitle">
                      <div>适用场景</div>
                    </div>
                  </template>
                  <el-input v-model="appForm.sence" @change="changeAgentParams" type="textarea" placeholder="请输入适用场景" :rows="3"
                    ></el-input>
                </el-collapse-item>
              </el-collapse>
            </div>
        </el-drawer>
    </div>
</template>

<script>
// mixins
import drawerMixins from "./drawerMixins";
import modelSelect from "@/components/ModelSelect.vue";

export default {
    props: ["componentParams"],
    name: "runDrawer",
    components: { modelSelect },
    mixins: [drawerMixins],
    data() {
        return {
            activeNames: ["0", "1", "2", "3", "4"],
            appForm: {
              sence: "",
            },
            
        };
    },
    watch: {
        
    },
    mounted() {},
    methods: {
      
    },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
