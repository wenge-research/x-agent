<template>
    <div class="storage-page">
        <div class="header-storage">
            <div class="name">储存策略</div>
            <el-select v-model="optionsValue" placeholder="请选择" style="width: 210px;">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
        </div>
        <!-- :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" -->
        <div>
            <el-table :data="processedTableData" style="width: 960px;" class="custom-header" size="medium"
                max-height="620px" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" row-key="id">
                <el-table-column prop="parms" label="响应参数" width="446" height="80">
                </el-table-column>
                <el-table-column label="映射字段名" width="446">
                    <template #default="{ row }">
                        <el-input v-model="row.name" size="mini" :placeholder="row.placeholder || '输入映射的字段名，未填写则不储存该参数'"
                            @change="handleNameChange(row)" :disabled="row.children && row.children.length > 0" />
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div class="btn-box">
            <el-button plain @click="cancel">取消</el-button>
            <el-button type="primary" @click="submit">确定</el-button>
        </div>
    </div>
</template>

<script>
import { addOrUpdatKnowledgeApi } from "./../../../api/Kbm";
let idCounter = 1;
export default {
    props: {
        responseData: {
            type: Object,
            required: true,
            default: () => ({}) // 默认空对象
        },
        nextPageParms: {
            type: Object,
            required: true,
            default: () => ({}) // 默认空对象
        },
    },
    data() {
        return {
            optionsValue: '0',
            options: [
                { value: '0', label: '全量更新' },
                { value: '1', label: '增量更新' },
            ],
            defaultPlaceholder: '输入映射的字段名，未填写则不储存该参数',
            tableData: []
        }
    },
    computed: {
        processedTableData() {
            // 重置ID计数器（如果需要）
            idCounter = 1;

            // 将对象转换为数组
            const entries = Object.entries(this.responseData);

            // 如果没有数据，返回默认结构
            if (entries.length === 0) {
                return [
                    {
                        id: idCounter++,
                        parms: 'success',
                        name: '',
                        placeholder: '输入映射的字段名，未填写则不储存该参数'
                    },
                    {
                        id: idCounter++,
                        parms: 'name',
                        name: '',
                    },
                    {
                        id: idCounter++,
                        parms: 'subtitle',
                        name: '',
                    },
                    {
                        id: idCounter++,
                        parms: 'update_time',
                        name: '',
                    },
                    {
                        id: idCounter++,
                        parms: 'data',
                        name: '',
                    }
                ];
            }

            // 转换对象为表格数据结构
            return this.objectToTableData(this.responseData);
        }
    },
    methods: {
        // 递归将对象转换为表格数据结构
        objectToTableData(obj, isTopLevel = true) {
            return Object.entries(obj).map(([key, value]) => {
                if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
                    // 如果是对象，递归处理并作为子节点
                    // 只在顶层使用字段名，子层使用点号连接
                    return {
                        id: idCounter++,
                        parms: isTopLevel ? key : `${key}`, // 只在顶层使用字段名
                        name: '', // 留空以供用户输入
                        placeholder: '输入映射的字段名，未填写则不储存该参数',
                        children: this.objectToTableData(value, false)
                    };
                } else {
                    // 基本类型值
                    return {
                        id: idCounter++,
                        parms: isTopLevel ? key : `${key}`, // 只在顶层使用字段名
                        name: '', // 留空以供用户输入
                        placeholder: '输入映射的字段名，未填写则不储存该参数'
                    };
                }
            });
        },

        handleNameChange(row) {
            // 处理名称变化的逻辑
            console.log('Name changed:', row);
            // 这里可以添加你的自定义逻辑
        },

        async addOrUpdatKnowledge() {
            const data = JSON.stringify(this.processedTableData);
            const parmas = this.nextPageParms;
            parmas.storageType = this.optionsValue;
            parmas.responseConfigDesc = data;
            try {
                const res = await addOrUpdatKnowledgeApi(parmas);
                if(res.code === "000000") {
                    this.$message.success("保存成功");
                    this.$emit('close-closeDrawer');
                };
            } catch (error) {
                console.log('error',error)
            }
        },
        submit() {
            this.addOrUpdatKnowledge()
        },
        cancel() {
            this.$emit('close-drawer'); // 触发自定义事件
        }
    }
}
</script>

<style scoped lang="scss">
.storage-page {
    position: relative;
    // height: 100vh;
}

.btn-box {
    position: fixed;
    bottom: 30px;
    /* 距离视口底部的距离 */
    right: 30px;
    z-index: 10;
}

.header-storage {
    display: flex;
    align-items: center;
    margin-bottom: 24px;

    .name {
        margin-right: 16px;
        font-weight: 400;
        font-size: 14px;
        color: #1D2129;
    }
}

::v-deep .custom-header .el-table__header th {
    background-color: #F2F3F5;
    height: 40px;
}

::v-deep .el-table .el-table__cell {
    padding: 0 !important;
}

::v-deep .custom-header .el-table__row {
    height: 56px;
}

/* 调整缩进量 */
::v-deep .el-table__indent {
    padding-left: 20px;
    /* 根据层级深度自动增加缩进 */
}
</style>