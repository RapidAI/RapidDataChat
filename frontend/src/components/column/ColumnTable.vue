<template>
  <a-layout class="full-height">
    <!-- 数据库选择 -->
    <a-layout-sider theme="light">
      <a-menu
          v-if="treeData.length > 0"
          mode="inline"
          :selectedKeys="[selectedDatabaseId]"
          @click="onSelectDatabase"
      >
        <a-menu-item v-for="item in treeData" :key="item.key">
          <DatabaseOutlined/>
          {{ item.title }}
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <!-- 表选择 -->
    <a-layout-sider width="220" theme="light">
      <a-menu
          :inlineIndent="2"
          v-if="tableData.length > 0"
          mode="inline"
          :selectedKeys="[selectedTableId]"
          @click="onSelectTable"
      >
        <a-menu-item v-for="table in tableData" :key="table.key">
          <AppstoreOutlined/>
          {{ table.title }}
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <!-- 字段信息展示 -->
    <a-layout-content class="custom-content">
      <a-table :columns="columns" :dataSource="columnsData" :rowKey="record => record.columnInfoId" class="custom-table">
        <template v-slot:bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a-button type="primary" @click="showModal('edit', record)">编辑</a-button>
              <a-popconfirm title="确定要删除这个字段信息吗？" okText="确定" cancelText="取消"
                            @confirm="() => deleteColumnInfo(record.columnInfoId)">
                <a-button type="primary">删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
          <template v-else>
            {{ record[column.dataIndex] }}
          </template>
        </template>
      </a-table>
      <!-- 编辑模态框 -->
      <a-modal v-model:open="isModalVisible" :title="modalType === 'add' ? '添加字段信息' : '更新字段信息'" :footer="null">
        <column-form v-if="isModalVisible" ref="columnFormRef" :initialValues="currentColumn" :mode="modalType"
                     @onCancel="handleCancel" @onSuccess="handleSuccess" table-info-id=""/>
      </a-modal>
    </a-layout-content>
  </a-layout>
</template>

<script>
import {ref, onMounted} from 'vue';
import ColumnForm from './ColumnForm.vue';
import {post} from '@/api';
import {message} from 'ant-design-vue';
import {AppstoreOutlined, DatabaseOutlined} from "@ant-design/icons-vue";

export default {
  components: {
    AppstoreOutlined,
    DatabaseOutlined,
    ColumnForm
  },
  setup() {
    // 定义响应式数据
    const columnsData = ref([]);
    const isModalVisible = ref(false);
    const currentColumn = ref({});
    const modalType = ref('add');
    const selectedDatabaseId = ref('0');
    const selectedTableId = ref('0');
    const tableData = ref([]);
    const treeData = ref([]);

    // 定义表格列配置
    const columns = [
      {title: '字段名', dataIndex: 'columnName', align: 'center'},
      {title: '字段注释', dataIndex: 'columnComment', align: 'center'},
      {title: '字段说明', dataIndex: 'columnDescription', align: 'center'},
      {title: '数据类型', dataIndex: 'dataType', align: 'center'},
      {title: '操作', dataIndex: 'action', align: 'center'}
    ];

    // 加载数据库数据
    const loadDatabase = async () => {
      const res = await post('/databases/getAll');
      if (res && res.success) {
        treeData.value = res.data.map(db => ({
          title: db.databaseName,
          key: String(db.databaseInfoId)
        }));

        if (treeData.value.length > 0) {
          selectedDatabaseId.value = treeData.value[0].key;
          await loadTables();
        }
      }
    };

    // 加载表数据
    const loadTables = async () => {
      const res = await post('/tables/getAllByDatabaseId', {databaseInfoId: selectedDatabaseId.value});
      if (res && res.success) {
        tableData.value = res.data.map(table => ({
          title: table.tableName,
          key: String(table.tableInfoId),
          dbId: selectedDatabaseId.value
        }));

        if (tableData.value.length > 0) {
          selectedTableId.value = tableData.value[0].key;
          await loadColumns();
        }
      }
    };

    // 加载字段数据
    const loadColumns = async () => {
      const res = await post('/columns/getAllByTableId', {tableInfoId: selectedTableId.value});
      if (res && res.success) {
        columnsData.value = res.data;
      }
    };

    // 选择数据库
    const onSelectDatabase = ({key}) => {
      selectedDatabaseId.value = key;
      loadTables();
    };

    // 选择表
    const onSelectTable = ({key}) => {
      selectedTableId.value = key;
      loadColumns();
    };

    // 显示模态框
    const showModal = (type, record) => {
      modalType.value = type;
      currentColumn.value = {...record};
      isModalVisible.value = true;
    };

    // 取消模态框
    const handleCancel = () => {
      isModalVisible.value = false;
      currentColumn.value = {};
    };

    // 处理模态框成功事件
    const handleSuccess = (columnData) => {
      isModalVisible.value = false;
      if (modalType.value === 'add') {
        columnsData.value.push(columnData);
      } else {
        const index = columnsData.value.findIndex(item => item.columnInfoId === columnData.columnInfoId);
        if (index !== -1) {
          columnsData.value[index] = columnData;
        }
      }
      loadColumns();
    };

    // 删除字段信息
    const deleteColumnInfo = async (columnInfoId) => {
      const res = await post(`/columns/delete`, {columnInfoId: columnInfoId});
      if (res && res.success) {
        message.success('删除成功');
        columnsData.value = columnsData.value.filter(item => item.columnInfoId !== columnInfoId);
        loadColumns();
      } else {
        message.error('删除失败');
      }
    };

    // 组件挂载时加载数据库数据
    onMounted(loadDatabase);

    return {
      columns,
      columnsData,
      isModalVisible,
      showModal,
      handleCancel,
      handleSuccess,
      currentColumn,
      modalType,
      treeData,
      tableData,
      selectedDatabaseId,
      selectedTableId,
      onSelectDatabase,
      onSelectTable,
      deleteColumnInfo
    };
  }
};
</script>

<style scoped>
</style>