<template>
  <a-form
      ref="formRef"
      :model="formState"
      :rules="rules"
      :label-col="{span: 6}"
      :wrapper-col="{span: 14}"
  >
    <a-form-item label="表名称" name="tableName">
      <a-input v-model:value="formState.tableName" placeholder="请输入表名"/>
    </a-form-item>
    <a-form-item label="表注释" name="tableComment">
      <a-input v-model:value="formState.tableComment" placeholder="请输入表注释"/>
    </a-form-item>

    <a-form-item label="表说明" name="tableDescription">
      <a-textarea v-model:value="formState.tableDescription" placeholder="请输入表说明"/>
    </a-form-item>

    <a-form-item :wrapper-col="{ span: 18, offset: 6 }">
      <a-button type="primary" @click="onSubmit">确认</a-button>
      <a-button style="margin-left: 10px" @click="onCancel">取消</a-button>
    </a-form-item>
  </a-form>
</template>

<script>
import { ref, reactive, watch } from 'vue';
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
      tableName: '',
      tableComment: '',
      tableDescription: ''
    });

    const rules = {
      tableName: [
        {required: true, message: '请输入表名', trigger: 'blur'}
      ],
      tableComment: [
        {required: false}
      ],
      tableDescription: [
        {required: false}
      ]
    };

    watch(() => props.initialValues, (newVal) => {
      formState.tableName = newVal.tableName || '';
      formState.tableComment = newVal.tableComment || '';
      formState.tableDescription = newVal.tableDescription || '';
    }, {immediate: true});

    const onSubmit = async () => {
      try {
        await formRef.value.validate();
        const data = {...formState};
        let response;
        if (props.mode === 'add') {
          response = await post('/tables/add', data);
        } else {
          data.tableInfoId = props.initialValues.tableInfoId;
          response = await post('/tables/update', data);
        }
        if (response && response.success) {
          emit('onSuccess', response.data);  // 发送成功事件，并传递表信息数据
        }
      } catch (error) {
        console.error('Validation failed or request error:', error);
      }
    };

    const onCancel = () => {
      emit('onCancel');
    };

    return {formRef, formState, rules, onSubmit, onCancel};
  }
};
</script>

<style scoped>
/* 可根据需求添加CSS样式 */
</style>
