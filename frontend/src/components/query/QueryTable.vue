<template>
  <a-layout-content class="custom-content">
    <a-table :columns="columns" :dataSource="queries" :rowKey="record => record.id" class="custom-table"
             :pagination="{ pageSize: 8 }">
      <template v-slot:bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'sqlText' || column.dataIndex === 'responseText'">
          <span v-if="record[column.dataIndex].length > 50">
            <a-tooltip :title="record[column.dataIndex]">
              {{ record[column.dataIndex].slice(0, 50) }}...
            </a-tooltip>
          </span>
          <span v-else>
            {{ record[column.dataIndex] }}
          </span>
        </template>
        <template v-else-if="column.dataIndex === 'action'">
          <a-space>
            <a-button type="primary" @click="showModal('edit', record)">编辑</a-button>
            <a-popconfirm title="确定要删除这个查询记录吗？" okText="确定" cancelText="取消"
                          @confirm="() => deleteQuery(record.queryId)">
              <a-button type="primary">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
        <template v-else>
          {{ record[column.dataIndex] }}
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="isModalVisible" :title="modalType === 'add' ? '新建查询' : '编辑查询'"  width="60%" :footer="null">
      <QueryForm v-if="isModalVisible" ref="queryFormRef" :initialValues="currentQuery" :mode="modalType"
                 @onCancel="handleCancel"/>
    </a-modal>
  </a-layout-content>
</template>

<script>
import {ref, onMounted} from 'vue';
import QueryForm from './QueryForm.vue';
import {post} from '@/api';
import {message} from 'ant-design-vue';
import {useUserStore} from "@/store/UserStore.js";

export default {
  components: {
    QueryForm
  },
  setup() {
    const queries = ref([]);
    const isModalVisible = ref(false);
    const currentQuery = ref({});
    const modalType = ref('add');
    const userStore = useUserStore();

    const columns = [
      {title: 'queryId', dataIndex: 'queryId', align: 'center'},
      {title: '查询文本', dataIndex: 'queryText', align: 'center'},
      {title: 'SQL文本', dataIndex: 'sqlText', align: 'center'},
      {title: '响应文本', dataIndex: 'responseText', align: 'center'},
      {title: '执行时间s', dataIndex: 'executionTime', align: 'center'},
      {title: '成功', dataIndex: 'success', align: 'center'},
      {title: '操作', dataIndex: 'action', align: 'center'}
    ];

    const loadQueries = async () => {
      const res = await post('/queries/getByUserId', {userId: userStore.userData.userId});
      if (res && res.success) {
        queries.value = res.data;
      }
    };

    onMounted(loadQueries);

    function showModal(type, query = {}) {
      modalType.value = type;
      currentQuery.value = {...query};
      isModalVisible.value = true;
    }

    function handleCancel() {
      isModalVisible.value = false;
      currentQuery.value = {};
      loadQueries();
    }

    async function deleteQuery(queryId) {
      const res = await post(`/queries/delete`, {queryId: queryId});
      if (res && res.success) {
        message.success('删除成功');
        loadQueries();
      } else {
        message.error('删除失败');
      }
    }

    return {
      columns,
      queries,
      isModalVisible,
      showModal,
      handleCancel,
      deleteQuery,
      currentQuery,
      modalType
    };
  }
};
</script>

<style scoped>
</style>