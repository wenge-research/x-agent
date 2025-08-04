<template>
  <div class="clearfix">
    <van-field
      v-model="text"
      :required="required"
      readonly
      is-link
      :class="readonly ? 'dict-select-readonly' : ''"
      autocomplete="off"
      @click="showComponentTypePicker"
      :type="multiple ? 'textarea' : 'text'"
      :autosize="multiple"
      rows="1"
      :label-width="labelWidth"
      :label="label"
      :colon="colon"
      :rules="rules"
      :placeholder="placeholder"
      :disabled="readonly"
      right-icon="arrow"
    />
    <van-popup
      v-model:show="showPicker"
      round
      position="bottom"
      style="height: 400px"
      :close-on-click-overlay="false"
      :lazy-render="false"
      class="vanpickerMul"
    >
      <template v-if="multiple">
        <!--多选弹出层-->
        <div
          style="
            display: flex;
            justify-content: space-between;
            padding: 0 10px;
            background: linear-gradient(
              180deg,
              rgba(22, 158, 154, 0.2) 0%,
              rgba(22, 158, 154, 0) 100%
            );
          "
        >
          <div
            @click="onDictSelectCancel"
            class="cancel"
            style="
              margin-bottom: 10px;
              margin-top: 10px;
              color: #969799;
              cursor: pointer;
              text-align: center;
            "
          >
            取消
          </div>
          <div
            @click="onDictMultipleSelectConfirm"
            class="confirm"
            style="
              margin-bottom: 10px;
              margin-top: 10px;
              color: #1989FA;
              cursor: pointer;
              text-align: center;
            "
          >
            确认
          </div>
        </div>
        <van-checkbox-group v-model="checkedList">
          <van-cell-group>
            <van-cell
              v-for="(element, index) in dataList"
              :key="element.value"
              clickable
              :title="element.text"
              @click="toggle(index)"
            >
              <template #right-icon>
                <van-checkbox :name="JSON.stringify(element)" @click.stop />
              </template>
            </van-cell>
          </van-cell-group>
        </van-checkbox-group>
      </template>

      <template v-else>
        <!--单选弹出层-->
        <van-picker
          ref="dictSelectPicker"
          show-toolbar
          v-model="realValuePickerList"
          :columns="dataList"
          @confirm="onDictSelectConfirm"
          @cancel="onDictSelectCancel"
        />
      </template>
    </van-popup>
  </div>
</template>

<script>
import { ref, watch } from "vue";

export default {
  name: "DictSelect",
  props: {
    dictType: {
      type: String,
      default: "",
    },
    dictDataList: {
      type: Array,
      default: () => [],
    },
    label: {
      type: String,
      default: "字典选择器",
    },
    labelWidth: {
      type: Number,
      default: 100,
    },
    colon: {
      type: Boolean,
      default: false,
    },
    placeholder: {
      type: String,
      default: "请选择",
    },
    textColumn: {
      type: String,
      default: "dictLabel",
    },
    valueColumn: {
      type: [String, Number],
      default: "dictValue",
    },
    rules: {
      type: Array,
      default: () => [],
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    required: {
      type: Boolean,
      default: false,
    },
    multiple: {
      type: Boolean,
      default: false,
    },
    modelValue: {
      required: false,
    },
  },
  setup(props, { emit }) {
    const showPicker = ref(false);
    const text = ref("");
    const realValue = ref("");
    const realValuePickerList = ref([]);
    const dataList = ref([]);
    const checkedList = ref([]);

    const createDictData = (data) => {
      dataList.value = data.map((item) => ({
        text: item[props.textColumn],
        value: item[props.valueColumn], // Ensure conversion to string
      }));
      reloadData();
    };

    const reloadData = () => {
      if (realValue.value) {
        const realValueList = realValue.value.split(",");
        checkedList.value = [];
        text.value = "";
        realValueList.forEach((value) => {
          const currentItem = dataList.value.find((item) => item.value === value);
          if (currentItem) {
            if (props.multiple) {
              checkedList.value.push(
                JSON.stringify({
                  text: currentItem.text,
                  value: value,
                })
              );
            } else {
              realValuePickerList.value.push(value);
            }
            if (text.value === "") {
              text.value = currentItem.text;
            } else {
              text.value += "," + currentItem.text;
            }
          }
        });
      }
    };

    const showComponentTypePicker = () => {
      reloadData();
      showPicker.value = true;
    };

    const onDictSelectCancel = () => {
      showPicker.value = false;
    };

    const onDictSelectConfirm = ({ selectedOptions }) => {
      if (selectedOptions[0]) {
        text.value = selectedOptions[0].text.toString();
        realValue.value = selectedOptions[0].value.toString();
        realValuePickerList.value = [realValue.value];
        emit("update:modelValue", realValue.value);
        emit("changeData", selectedOptions[0]);
        showPicker.value = false;
      }
    };

    const onDictMultipleSelectConfirm = () => {
      const temTextArray = [];
      const temValueArray = [];
      checkedList.value.forEach((item) => {
        const parsedItem = JSON.parse(item);
        temTextArray.push(parsedItem.text);
        temValueArray.push(parsedItem.value);
      });
      text.value = temTextArray.join(",");
      realValue.value = temValueArray.join(",");
      emit("update:modelValue", realValue.value);
      emit("changeData", { text: text.value, value: realValue.value });
      showPicker.value = false;
    };

    const toggle = (index) => {
      checkedList.value[index] = !checkedList.value[index];
    };

    watch(
      () => props.modelValue,
      (newValue) => {
        if (typeof newValue === "string" || typeof newValue === "number") {
          realValue.value = newValue.toString();
          reloadData();
        } else {
          realValue.value = "";
        }
      },
      { immediate: true }
    );

    watch(
      () => props.dictDataList,
      (newData) => {
        if (newData && newData.length > 0) {
          createDictData(newData);
        }
      },
      { immediate: true }
    );

    return {
      showPicker,
      text,
      realValue,
      realValuePickerList,
      dataList,
      checkedList,
      showComponentTypePicker,
      onDictSelectCancel,
      onDictSelectConfirm,
      onDictMultipleSelectConfirm,
      toggle,
    };
  },
};
</script>

<style scoped>
/* .van-cell {
  // padding: 10px 20px;
} */
.dict-select-readonly {
  pointer-events: none;
}
.van-cell--required::before {
  position: absolute;
  left: -3px;
  color: #ee0a24;
  font-size: 14px;
  content: "*";
}

/deep/ .van-cell__right-icon {
  display: none;
}
/deep/ .van-field__right-icon {
  margin-right: -8px !important;
}
.cancel {
  font-size: 16px;
}

.confirm {
  font-size: 16px;
}

.vanpickerMul {
  /deep/ .van-cell {
    flex-direction: row !important;
    padding: 10px 15px !important;
  }
}
/deep/ .van-checkbox-group {
  height: 356px;
  overflow-y: auto;
}
</style>
