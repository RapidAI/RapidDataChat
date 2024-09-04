<template>
  <a-layout class="full-height">
    <a-layout-sider theme="light">
      <a-menu mode="inline" :selectedKeys="[selectedDatabaseId]" @click="onSelectDatabase">
        <a-menu-item v-for="item in databases" :key="item.key">
          <DatabaseOutlined/>
          {{ item.databaseName }}
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout-content class="custom-content">
      <a-input v-model:value="filterKeyword" placeholder="过滤表名称" style="margin-bottom: 16px;"/>
      <a-table :columns="columns" :dataSource="filteredTables" :rowKey="record => record.tableInfoId">
        <template v-slot:bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a-button type="primary" @click="openModal('edit', record)">编辑</a-button>
              <a-popconfirm title="确定要删除这个表信息吗？" okText="确定" cancelText="取消"
                            @confirm="() => removeTable(record.tableInfoId)">
                <a-button type="primary">删除</a-button>
              </a-popconfirm>
              <a-button type="primary" @click="openAnalyzeModal(record)">AI解析</a-button>
            </a-space>
          </template>
          <template v-else>{{ record[column.dataIndex] }}</template>
        </template>
      </a-table>
      <a-modal v-model:open="isModalVisible" :title="modalType === 'add' ? '添加表信息' : '更新表信息'" :footer="null">
        <TableForm v-if="isModalVisible" ref="tableFormRef" :initialValues="currentTable" :mode="modalType"
                   @onCancel="closeModal" @onSuccess="handleFormSuccess"/>
      </a-modal>
      <a-modal v-model:open="isAnalyzeModalVisible" title="选择模型" okText="确定" cancelText="取消" @ok="handleAnalyze"
               @cancel="isAnalyzeModalVisible = false">
        <a-select v-model:value="selectedModelId" placeholder="请选择一个模型" :options="modelOptions"
                  :allow-clear="true" @change="updateSelectedModel"/>
        <a-spin v-if="isAnalyzing" tip="AI解析中..."></a-spin>
      </a-modal>
    </a-layout-content>
  </a-layout>
</template>

<script>
import {ref, computed, onMounted} from 'vue';
import TableForm from './TableForm.vue';
import {post} from '@/api';
import {message} from 'ant-design-vue';
import {DatabaseOutlined} from "@ant-design/icons-vue";
import {useMessageStore} from "@/store/MessageStoreWithBigData.js";

export default {
  components: {DatabaseOutlined, TableForm},
  setup() {
    // 表数据和模态框状态
    const tables = ref([]), isModalVisible = ref(false), currentTable = ref({}), modalType = ref('add');
    const selectedDatabaseId = ref('0'), isAnalyzeModalVisible = ref(false), models = ref([]), modelOptions = ref([]);
    const selectedModelId = ref(null), selectTable = ref({}), isAnalyzing = ref(false), databases = ref([]);
    const selectedDatabase = ref({});  // 定义selectedDatabase
    const messageStore = useMessageStore();
    const filterKeyword = ref("");  // 过滤关键字

    // 计算属性：根据过滤关键字筛选表数据
    const filteredTables = computed(() => {
      return tables.value.filter(table => table.tableName.includes(filterKeyword.value.trim()));
    });

    // 获取所有数据库信息
    const fetchDatabases = async () => {
      const res = await post('/databases/getAll');
      if (res?.success) {
        databases.value = res.data.map(db => ({...db, key: String(db.databaseInfoId)}));
        if (databases.value.length) {
          selectedDatabaseId.value = databases.value[0].key;
          selectedDatabase.value = databases.value[0];  // 填充selectedDatabase
          await fetchTables();
        }
      }
    };

    // 获取所有表信息
    const fetchTables = async () => {
      const res = await post('/tables/getAllByDatabaseId', {databaseInfoId: selectedDatabaseId.value});
      if (res?.success) tables.value = res.data;
    };

    // 选择数据库
    const onSelectDatabase = async ({key}) => {
      selectedDatabaseId.value = key;
      selectedDatabase.value = databases.value.find(db => db.key === key);  // 填充selectedDatabase
      await fetchTables();
    };

    // 获取所有AI模型
    const fetchModels = async () => {
      try {
        const response = await post('/model/getAll');
        models.value = response.data;
        modelOptions.value = models.value.map(model => ({value: model.modelId, label: model.modelName}));
        selectedModelId.value = models.value[0].modelId;
      } catch (error) {
        console.error('获取AI模型失败:', error);
      }
    };

    // 更新选中的模型
    const updateSelectedModel = (e) => {
      selectedModelId.value = e;
    };
    // AI解析table
    const handleAnalyze = async () => {
      if (selectedModelId.value == null) {
        return message.error('请选择一个模型');
      }

      isAnalyzing.value = true;

      try {
        // 获取字段信息
        await Promise.all(tables.value.map(async (table) => {
          const response = await post('/columns/getAllByTableId', table);
          if (response?.success) {
            table.columns = response.data;
          }
        }));

        selectedDatabase.value.tables = tables.value;
        const sys = messageStore.databaseCreateSQLTableStatements4Analyze([selectedDatabase.value]);
        const model = models.value.find(model => model.modelId === selectedModelId.value);

        let prompt = `根据数据库信息仔细思考,找出 ${selectTable.value.tableName} 表与其他表之间的关联关系,生成一个详细的解释说明"\n`;
        prompt += `返回json数据结构\n`;
        prompt += `{\n`;
        prompt += `   analysis: "该表与其他表之间的关系..."\n`;
        prompt += `   summary: "{作用:...,关联表:[{表名:...,关联字段:...,表示:...}...]}"\n`;
        prompt += `}\n`;
        model.messages = [{role: 'system', content: sys}, {role: 'user', content: prompt}]

        const res = await post('/chat/generatebyModel', model);
        if (res?.success) {
          selectTable.value.tableDescription = extractJsonFromResponse(res.data.content);
          const updateResponse = await post('/tables/update', selectTable.value);
          if (updateResponse?.success) {
            message.success('AI解析成功');
          }
        } else {
          message.error(`AI解析失败`);
        }
      } catch (error) {
        message.error(`AI解析失败: ${error.message}`);
      } finally {
        isAnalyzing.value = false;
        isAnalyzeModalVisible.value = false;
        selectedModelId.value = null;
      }
    };

    const extractJsonFromResponse = (response) => {
      try {
        // 匹配出 response 中的 JSON 部分
        const match = response.match(/```json\n([\s\S]*?)\n```/);
        if (match) {
          // 解析出 JSON 对象
          const jsonObject = JSON.parse(match[1]);
          // 打印解析出的 JSON 对象
          console.log('解析出的 JSON 对象:', jsonObject);
          // 返回 analysis 部分
          return JSON.stringify(jsonObject.summary);
        } else {
          return null;
        }
      } catch (error) {
        // 解析 JSON 失败时的错误处理
        console.error('解析JSON失败:', error);
        return null;
      }
    }

    // 表格列配置
    const columns = [
      {title: '表名称', dataIndex: 'tableName', align: 'center'},
      {title: '表注释', dataIndex: 'tableComment', align: 'center'},
      {title: '表说明', dataIndex: 'tableDescription', align: 'center'},
      {title: '操作', dataIndex: 'action', align: 'center'}
    ];

    // 打开添加或编辑模态框
    const openModal = (type, record) => {
      modalType.value = type;
      currentTable.value = {...record};
      isModalVisible.value = true;
    };

    // 关闭添加或编辑模态框
    const closeModal = () => {
      isModalVisible.value = false;
      currentTable.value = {};
    };

    // 处理表单提交成功
    const handleFormSuccess = async (tableData) => {
      isModalVisible.value = false;
      if (modalType.value === 'add') tables.value.push(tableData);
      else tables.value = tables.value.map(item => item.tableInfoId === tableData.tableInfoId ? tableData : item);
      await fetchTables();
    };

    // 删除表信息
    const removeTable = async (tableInfoId) => {
      const res = await post('/tables/delete', {tableInfoId});
      if (res?.success) {
        message.success('删除成功');
        tables.value = tables.value.filter(item => item.tableInfoId !== tableInfoId);
        await fetchTables();
      } else message.error('删除失败');
    };

    // 打开AI解析模态框
    const openAnalyzeModal = (record) => {
      selectTable.value = record;
      isAnalyzeModalVisible.value = true;
      fetchModels();
    };

    onMounted(fetchDatabases);

    return {
      columns,
      isModalVisible,
      tables,
      openModal,
      closeModal,
      handleFormSuccess,
      currentTable,
      modalType,
      databases,
      selectedDatabaseId,
      onSelectDatabase,
      removeTable,
      isAnalyzeModalVisible,
      modelOptions,
      selectedModelId,
      handleAnalyze,
      updateSelectedModel,
      openAnalyzeModal,
      isAnalyzing,
      selectedDatabase,
      filterKeyword, // 添加 filterKeyword 到返回值中
      filteredTables // 添加 filteredTables 到返回值中
    };
  }
};
</script>

<style scoped>
.custom-content {
  padding: 24px;
  background: #fff;
}

.full-height {
  height: 100%;
}
</style>