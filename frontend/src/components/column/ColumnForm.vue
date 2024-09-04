<template>
  <a-form
      ref="formRef"
      :model="formState"
      :rules="rules"
      :label-col="{span: 6}"
      :wrapper-col="{span: 14}"
  >
    <a-form-item label="字段名" name="columnName">
      <a-input v-model:value="formState.columnName" placeholder="请输入字段名"/>
    </a-form-item>

    <a-form-item label="数据类型" name="dataType">
      <a-input v-model:value="formState.dataType" placeholder="请输入数据类型"/>
    </a-form-item>

    <a-form-item label="字段注释" name="columnComment">
      <a-textarea v-model:value="formState.columnComment" placeholder="请输入字段注释"/>
    </a-form-item>

    <a-form-item label="字段说明" name="columnDescription">
      <a-textarea v-model:value="formState.columnDescription" placeholder="请输入字段的功能说明"/>
    </a-form-item>

    <a-form-item :wrapper-col="{ span: 18, offset: 6 }">
      <a-button type="primary" @click="onSubmit">提交</a-button>
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
    },
    tableInfoId: {
      type: Number,
      required: true
    }
  },
  setup(props, { emit }) {
    const formRef = ref(null);
    const formState = reactive({
      columnName: '',
      dataType: '',
      columnComment: '',
      columnDescription: '',
    });

    const rules = {
      columnName: [
        { required: true, message: '请输入字段名', trigger: 'blur' },
      ],
      dataType: [
        { required: true, message: '请输入数据类型', trigger: 'blur' },
      ],
      columnComment: [
        { required: true, message: '请输入字段注释', trigger: 'blur' },
      ],
      columnDescription: [
        { required: true, message: '请输入字段说明', trigger: 'blur' },
      ],
    };

    watch(() => props.initialValues, (newVal) => {
      formState.columnName = newVal.columnName || '';
      formState.dataType = newVal.dataType || '';
      formState.columnComment = newVal.columnComment || '';
      formState.columnDescription = newVal.columnDescription || '';
    }, { immediate: true });

    const onSubmit = async () => {
      try {
        await formRef.value.validate();
        const data = {
          tableInfoId: props.tableInfoId,
          columnName: formState.columnName,
          dataType: formState.dataType,
          columnComment: formState.columnComment,
          columnDescription: formState.columnDescription
        };

        if (props.mode === 'add') {
          await post('/columns/add', data);
        } else {
          data.columnInfoId = props.initialValues.columnInfoId;
          await post('/columns/update', data);
        }

        emit('onCancel'); // 提交后关闭表单
      } catch (error) {
        console.error('Validation failed or request error:', error);
      }
    };

    const onCancel = () => {
      emit('onCancel'); // 发射取消事件
    };

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
