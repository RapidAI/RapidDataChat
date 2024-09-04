<template>
  <a-layout-content class="custom-content">
    <div class="header">
      <div class="title">数据库管理</div>
      <a-button type="primary" @click="showModal('add')">添加数据库信息</a-button>
    </div>
    <a-table :columns="columns" :dataSource="databases" :rowKey="record => record.databaseInfoId" class="custom-table">
      <template v-slot:bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a-button type="primary" @click="showModal('edit', record)">编辑</a-button>
            <a-popconfirm title="确定要删除这个数据库信息吗？" okText="确定" cancelText="取消"
                          @confirm="() => deleteDatabaseInfo(record.databaseInfoId)">
              <a-button type="primary">删除</a-button>
            </a-popconfirm>
            <a-popconfirm title="确定要同步这个数据库信息吗？" okText="确定" cancelText="取消"
                          @confirm="() => syncDatabaseInfo(record)">
              <a-button type="primary">同步元信息</a-button>
            </a-popconfirm>
            <a-button type="primary" @click="showAnalyzeModal(record)">AI解析</a-button>
          </a-space>
        </template>
        <template v-else-if="column.dataIndex === 'databaseType'">
          <svg class="icon">
            <use :xlink:href="`#icon-${databaseTypes[record[column.dataIndex]].icon}`"></use>
          </svg>
          {{ databaseTypes[record[column.dataIndex]].label }}
        </template>
        <template v-else-if="['syncTime', 'analyzeTime'].includes(column.dataIndex)">
          {{ formatDate(record[column.dataIndex]) }}
        </template>
        <template v-else>
          {{ record[column.dataIndex] }}
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="isModalVisible" :title="modalType === 'add' ? '添加数据库信息' : '更新数据库信息'" :footer="null">
      <DatabaseForm v-if="isModalVisible" ref="databaseFormRef" :initialValues="currentDatabase" :mode="modalType" @onCancel="handleCancel"/>
    </a-modal>
    <a-modal v-model:open="isAnalyzeModalVisible" title="选择模型" okText="确定" cancelText="取消"
             @ok="handleAnalyze" @cancel="handleCancelAnalyze" :confirmLoading="isAnalyzing">
      <a-select v-model:value="selectedModelId" placeholder="请选择一个模型" :options="modelOptions" :allow-clear="true" @change="handleModelChange"/>
      <p v-if="currentDatabase.databaseName">数据库名称：{{ currentDatabase.databaseName }}</p>
      <p v-if="currentDatabase.tables && currentDatabase.tables.length > 0">当前解析的表数量：{{ currentDatabase.tables.length }}</p>
      <p v-if="isAnalyzing">数据量较大的时候, 解析需要等待一段时间，请耐心等待。</p>
      <a-progress v-if="isAnalyzing && analyzeProgress < 100" :percent="analyzeProgress" status="active"/>
      <a-spin v-if="isAnalyzing && analyzeProgress === 100" tip="AI解析中..."/>
    </a-modal>
  </a-layout-content>
</template>


<script>
import {ref, onMounted} from 'vue';
import moment from 'moment';
import DatabaseForm from './DatabaseForm.vue';
import {post} from '@/api';
import {message} from 'ant-design-vue';
import {databaseTypes} from './databaseTypes';
import {useMessageStore} from "@/store/MessageStoreWithBigData.js";

export default {
  components: {
    DatabaseForm
  },
  setup() {
    const databases = ref([]);
    const isModalVisible = ref(false);
    const isAnalyzeModalVisible = ref(false);
    const isAnalyzing = ref(false);
    const analyzeProgress = ref(0); // 添加解析进度
    const currentDatabase = ref({});
    const modalType = ref('add');
    const models = ref([]);
    const modelOptions = ref([]);
    const selectedModelId = ref(null);
    const messageStore = useMessageStore();
    const columns = [
      {title: '数据库名称', dataIndex: 'databaseName', align: 'center'},
      {title: '主机地址', dataIndex: 'host', align: 'center'},
      {title: '端口', dataIndex: 'port', align: 'center'},
      {title: '用户名', dataIndex: 'username', align: 'center'},
      {title: '数据库类型', dataIndex: 'databaseType', align: 'center'},
      {title: '说明', dataIndex: 'databaseDescription', align: 'center'},
      {title: '同步时间', dataIndex: 'syncTime', align: 'center'},
      {title: '解析时间', dataIndex: 'analyzeTime', align: 'center'},
      {title: '操作', dataIndex: 'action', align: 'center'}
    ];

    const loadDatabases = async () => {
      const res = await post('/databases/getAll');
      if (res && res.success) {
        databases.value = res.data;
      }
    };

    const fetchModels = async () => {
      try {
        const response = await post('/model/getAll');
        if (response && response.success) {
          models.value = response.data;
          modelOptions.value = models.value.map(model => ({
            value: model.modelId,
            label: model.modelName
          }));
          selectedModelId.value = models.value[0].modelId;
        }
      } catch (error) {
        console.error('获取AI模型失败:', error);
      }
    };

    onMounted(() => {
      loadDatabases();
      fetchModels();
    });

    const showModal = (type, database = {}) => {
      modalType.value = type;
      currentDatabase.value = {...database};
      isModalVisible.value = true;
    };

    const showAnalyzeModal = database => {
      currentDatabase.value = {...database};
      isAnalyzeModalVisible.value = true;
    };

    const handleCancel = () => {
      isModalVisible.value = false;
      currentDatabase.value = {};
      loadDatabases();
    };

    const handleModelChange = e => {
      selectedModelId.value = e;
    };

    const handleCancelAnalyze = () => {
      isAnalyzeModalVisible.value = false;
      selectedModelId.value = null;
    };

    const formatDate = date => date ? moment(date).format('YYYY-MM-DD HH:mm:ss') : '-';

    const deleteDatabaseInfo = async databaseInfoId => {
      const res = await post('/databases/delete', {databaseInfoId});
      if (res && res.success) {
        message.success('删除成功');
        loadDatabases();
      } else {
        message.error('删除失败');
      }
    };

    const syncDatabaseInfo = async database => {
      const res = await post('/databases/sync', database);
      if (res && res.success) {
        message.success('同步成功');
        loadDatabases();
      } else {
        message.error('同步失败');
      }
    };

    const handleAnalyze = async () => {
      if (selectedModelId.value == null) return message.error('请选择一个模型');
      isAnalyzing.value = true;
      analyzeProgress.value = 0;

      try {
        const tableresponse = await post('/tables/getAllByDatabaseId', currentDatabase.value);
        const tables = tableresponse ? tableresponse.data : [];
        const totalTables = tables.length;
        let completedTables = 0; // 用于追踪已完成的任务数

        const updateProgress = () => {
          completedTables += 1;
          analyzeProgress.value = Math.floor((completedTables / totalTables) * 100);
        };

        await Promise.all(tables.map(async (table) => {
          const response = await post('/columns/getAllByTableId', table);
          if (response?.success) table.columns = response.data;

          currentDatabase.value.tables = tables;
          const sys = messageStore.databaseCreateSQLTableStatements4Analyze([currentDatabase.value]);
          const model = models.value.find(m => m.modelId === selectedModelId.value);
          const prompt = `根据数据库信息仔细思考,找出 ${table.tableName} 表与其他表之间的关联关系,生成一个详细的解释说明"\n返回json数据结构\n{\n   analysis: "该表与其他表之间的关系..."\n   summary: "{作用:...,关联表:[{表名:...,关联字段:...,表示:...}...]}"\n}\n`;
          model.messages = [{role: 'system', content: sys}, {role: 'user', content: prompt}];
          const res = await post('/chat/generatebyModel', model);
          updateProgress(); // 每个异步操作完成后更新进度

          if (res?.success) {
            table.tableDescription = extractJsonFromResponse(res.data.content);
            await post('/tables/update', table);
          } else {
            message.error('AI解析失败');
          }

        }));

        // 更新解析时间 当前时间
        currentDatabase.value.analyzeTime = new Date();
        const res = await post('/databases/update', currentDatabase.value);
        if (res.success) {
          message.success('AI解析成功');
        }
        loadDatabases();
      } catch (error) {
        message.error(`AI解析失败: ${error.message}`);
      } finally {
        isAnalyzing.value = false;
        isAnalyzeModalVisible.value = false;
        selectedModelId.value = null;
      }
    };

    const extractJsonFromResponse = response => {
      try {
        const match = response.match(/```json\n([\s\S]*?)\n```/);
        if (match) {
          const jsonObject = JSON.parse(match[1]);
          return JSON.stringify(jsonObject.summary);
        }
        return null;
      } catch (error) {
        console.error('解析JSON失败:', error);
        return null;
      }
    };

    return {
      columns,
      databases,
      isModalVisible,
      isAnalyzeModalVisible,
      isAnalyzing,
      analyzeProgress,
      showModal,
      showAnalyzeModal,
      handleCancel,
      handleCancelAnalyze,
      deleteDatabaseInfo,
      syncDatabaseInfo,
      handleAnalyze,
      currentDatabase,
      modalType,
      databaseTypes,
      formatDate,
      modelOptions,
      selectedModelId,
      handleModelChange
    };
  }
};
</script>

<style scoped>
.icon {
  width: 16px;
  height: 16px;
  fill: currentColor;
  margin-right: 8px;
}
</style>