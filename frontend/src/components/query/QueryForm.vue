<template>
  <a-form
    ref="formRef"
    :model="formState"
    :rules="rules"
    :label-col="{ span: 6 }"
    :wrapper-col="{ span: 14 }"
  >
    <a-form-item label="数据库信息ID" name="databaseInfoId">
      <a-input v-model:value="formState.databaseInfoId" placeholder="请输入数据库信息ID"/>
    </a-form-item>
    <a-form-item label="会话ID" name="sessionId">
      <a-input v-model:value="formState.sessionId" placeholder="请输入会话ID"/>
    </a-form-item>
    <a-form-item label="用户ID" name="userId">
      <a-input v-model:value="formState.userId" placeholder="请输入用户ID"/>
    </a-form-item>
    <a-form-item label="查询文本" name="queryText">
      <a-input v-model:value="formState.queryText" placeholder="请输入查询文本"/>
    </a-form-item>
    <a-form-item label="SQL文本" name="sqlText">
      <a-textarea v-model:value="formState.sqlText" placeholder="请输入SQL文本" :rows="4"/>
    </a-form-item>
    <a-form-item label="响应文本" name="responseText">
      <a-textarea v-model:value="formState.responseText" placeholder="请输入响应文本" :rows="4"/>
    </a-form-item>
    <a-form-item label="执行时间（秒）" name="executionTime">
      <a-input-number v-model:value="formState.executionTime" placeholder="请输入执行时间（秒）"/>
    </a-form-item>
    <a-form-item label="执行时间" name="executedAt">
      <a-input v-model:value="formState.executedAt" show-time placeholder="请选择执行时间"/>
    </a-form-item>
    <a-form-item label="成功" name="success">
      <a-switch v-model:checked="formState.success"/>
    </a-form-item>
    <a-form-item :wrapper-col="{ span: 20, offset: 6 }">
      <a-button type="primary" @click="onSubmit">提交</a-button>
      <a-button style="margin-left: 10px" @click="onCancel">取消</a-button>
    </a-form-item>
  </a-form>
</template>

<script>
import { ref, reactive, watch, onMounted } from 'vue';
import { post } from '@/api';

export default {
  props: {
    initialValues: {
      type: Object,
      default: () => ({})
    },
    mode: {
      type: String,
      default: 'add'
    }
  },
  setup(props, { emit }) {
    const formRef = ref(null);
    const formState = reactive({
      databaseInfoId: '',
      sessionId: '',
      userId: '',
      queryText: '',
      sqlText: '',
      responseText: '',
      executionTime: null,
      executedAt: null,
      success: false
    });

    const rules = {
      databaseInfoId: [{ required: true, message: '请输入数据库信息ID', trigger: 'blur' }],
      sessionId: [{ required: true, message: '请输入会话ID', trigger: 'blur' }],
      userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
      queryText: [{ required: true, message: '请输入查询文本', trigger: 'blur' }],
      sqlText: [{ required: true, message: '请输入SQL文本', trigger: 'blur' }],
      responseText: [{ required: true, message: '请输入响应文本', trigger: 'blur' }],
      executionTime: [{ required: true, message: '请输入执行时间（秒）', trigger: 'blur' }],
      executedAt: [{ required: true, message: '请选择执行时间', trigger: 'change' }],
      success: [{ required: true, message: '请选择是否成功', trigger: 'change' }]
    };

    watch(() => props.initialValues, (newVal) => {
      Object.assign(formState, newVal);
    }, { immediate: true });

    const onSubmit = async () => {
      try {
        await formRef.value.validate();
        const data = { ...formState };

        if (props.mode === 'add') {
          await post('/queries/add', data);
        } else {
          data.queryId = props.initialValues.queryId;
          await post('/queries/update', data);
        }

        emit('onCancel');
      } catch (error) {
        console.error('Validation failed or request error:', error);
      }
    };

    const onCancel = () => {
      emit('onCancel');
    };

    onMounted(() => {
      Object.assign(formState, props.initialValues);
    });

    return {
      formRef,
      formState,
      rules,
      onSubmit,
      onCancel
    };
  }
};
</script>

<style scoped>
</style>