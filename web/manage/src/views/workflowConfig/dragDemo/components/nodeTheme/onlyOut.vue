<template>
    <div class="node" :id="node.id">
        <div>
            <div class="node-content">
                <HeadTool
                    :label="label"
                    @remove="removeHandler"
                    @testNodes="openRunDrawer"
                :showSub="showSub"
                :showDraw="true"
                @hideNode="hideCurNode"
                    @copy="copyHandler"
                    @input="inputChange"
                    imgSuffix="kaishi"
                />
                <div v-show="showSub">
                  <div class="model flex justify-between mb8">
                      <div class="model-label">输入</div>
                      <div class="model-right flex items-center">
                          <ul
                              class="card-list flex items-center flex-wrap"
                              v-if="variables && variables.length"
                          >
                              <li
                                  v-for="(el, index) in variables"
                                  :key="index"
                                  class="name flex items-center"
                                  :title="el.name"
                                  v-show="el.name !== 'CONVERSATION_NAME'"
                              >
                                  <iconpark-icon
                                      :name="getIcon(el.type)"
                                      class="icon"
                                      size="14"
                                  ></iconpark-icon>
                                  <span>
                                      {{ el.name}}
                                  </span>
                              </li>
                              <li v-if="appForm.fileFlag"
                              >
                                  <iconpark-icon
                                      :name="getIcon(el.type)"
                                      class="icon"
                                      size="14"
                                  ></iconpark-icon>
                                  <span>
                                    upload_file
                                  </span>
                              </li>
                          </ul>
                          <div v-else class="no-config">未配置输入</div>
                      </div>
                  </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
// mixins
import nodeThemeMixins from "./nodeThemeMixins";
export default {
    name: "paramsFilter",

    inject: ["getGraph", "getNode"],
    mixins: [nodeThemeMixins],
    data() {
        return {};
    },
    mounted() {},
    methods: {},
};
</script>
<style lang="scss" src="./common.scss" scoped></style>
