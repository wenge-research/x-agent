<template>
  <div class="largeModel">
    <el-select
      :placeholder="$t('selectPlaceholder')"
      v-model="modelId"
      class="assModel"
      @change="onChange"
    >
      <div
        slot="prefix"
        class="selectPrice el-input__icon"
        v-if="modelId"
      >
        <img
          style="
            width: 18px;
            height: 18px;
            margin: 11px 8px 11px 4px;
          "
          :src="getImg(modelId)"
        />
      </div>
      <el-option
        v-for="item in modleOptions"
        :key="item.modelId"
        :label="item.modelName"
        :value="item.modelId"
      >
        <div
          style="
            display: flex;
            align-items: center;
            height: 34px;
          "
        >
          <img
            style="
              width: 18px;
              height: 18px;
              margin-right: 8px;
            "
            :src="chatGptIdList[item.manufacturer]"
          />
          <span class="modelName">{{ item.modelName }}</span>
        </div>
      </el-option>
    </el-select>
  </div>
</template>

<script>

export default {
  name: "modelSelect",
  props: ["id"],
  data() {
    return {
      modelId:'',
      chatGptIdList: {
        智谱清言: require("@/assets/images/zpqy.png"),
        通义千问: require("@/assets/images/tongyi.png"),
        文心一言: require("@/assets/images/wxyy.png"),
        DeepSeek: require("@/assets/images/deepseek.png"),
        Kimi: require("@/assets/images/kimi.png"),
        雅意: require("@/assets/images/yayi.png"),
        豆包: require("@/assets/images/doubao.png"),
        百川: require("@/assets/images/baichuan.png"),
        星火: require("@/assets/images/xinghuo.png"),
        openAI: require("@/assets/images/openai.png"),
        MINIMAX: require("@/assets/images/MiniMax.png")
      },
    };
  },
  computed: {
    modleOptions() {
      return this.$store.state?.workflow?.modleOptions;
    }
  },
  mounted() {
    this.modelId = this.id
  },
  methods: {
    getImg(){
      if(this.modelId){
        let manufacturer = this.$store.state?.workflow?.modleOptions.find(item=>item.modelId==this.modelId).manufacturer
        return this.chatGptIdList[manufacturer]
      }
    },
    onChange(val){
      this.$emit('change',val)
    }
  },
  beforeDestroy() {},
};
</script>

