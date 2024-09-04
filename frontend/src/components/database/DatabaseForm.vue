<template>
  <a-form
      ref="formRef"
      :model="formState"
      :rules="rules"
      layout="vertical"
      @submit.prevent="onSubmit"
      style="max-width: 600px; margin: 0 auto;"
  >
    <a-row :gutter="16">
      <a-col :span="12">
        <a-form-item label="主机地址" name="host">
          <a-input v-model:value="formState.host" placeholder="请输入主机地址"/>
        </a-form-item>
      </a-col>
      <a-col :span="12">
        <a-form-item label="端口" name="port">
          <a-input-number v-model:value="formState.port" style="width: 100%;" placeholder="请输入端口号"/>
        </a-form-item>
      </a-col>
    </a-row>

    <a-form-item label="认证方式" name="authMethod">
      <a-select v-model:value="formState.authMethod" @change="onAuthMethodChange" placeholder="请选择认证方式">
        <a-select-option :value="1">User&Password</a-select-option>
        <a-select-option :value="0">NoPassword</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="用户名" name="username" v-if="requiresAuth">
      <a-input v-model:value="formState.username" placeholder="请输入用户名"/>
    </a-form-item>
    <a-form-item label="密码" name="password" v-if="requiresAuth">
      <a-input v-model:value="formState.password" type="password" placeholder="请输入密码"/>
    </a-form-item>
    <a-form-item label="数据库名称" name="databaseName">
      <a-input v-model:value="formState.databaseName" placeholder="请输入数据库名称"/>
    </a-form-item>
    <a-form-item label="数据库类型" name="databaseType">
      <a-select v-model:value="formState.databaseType" placeholder="请选择数据库类型">
        <a-select-option v-for="(type, key) in databaseTypes" :key="key" :value="key">
          <svg class="icon">
            <use :xlink:href="`#icon-${type.icon}`"></use>
          </svg>
          {{ type.label }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="数据库说明" name="databaseDescription">
      <a-textarea v-model:value="formState.databaseDescription" placeholder="请输入数据库说明"/>
    </a-form-item>
    <a-form-item>
      <a-button type="primary" html-type="submit">提交</a-button>
      <a-button style="margin-left: 10px" @click="onCancel">取消</a-button>
    </a-form-item>
  </a-form>
</template>

<script>
import { ref, reactive, watch, onMounted, nextTick } from 'vue';
import { post } from '@/api';
import { message } from 'ant-design-vue';
import { databaseTypes } from './databaseTypes'; // 引入数据库类型配置

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
      host: '',
      port: null,
      authMethod: 1,
      username: '',
      password: '',
      databaseName: '',
      databaseType: '',
      databaseDescription: ''
    });

    const requiresAuth = ref(true);

    const rules = {
      host: [
        { required: true, message: '请输入主机地址', trigger: 'blur' }
      ],
      port: [
        { required: true, message: '请输入端口号', trigger: 'blur' }
      ],
      username: [
        { required: requiresAuth.value, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: requiresAuth.value, message: '请输入密码', trigger: 'blur' }
      ],
      databaseName: [
        { required: true, message: '请输入数据库名称', trigger: 'blur' }
      ],
      databaseType: [
        { required: true, message: '请选择数据库类型', trigger: 'change' }
      ],
      databaseDescription: [
        { required: true, message: '请选择数据库说明', trigger: 'blur' }
      ]
    };

    watch(() => props.initialValues, (newVal) => {
      if (newVal) {
        formState.host = newVal.host || '';
        formState.port = newVal.port || null;
        formState.authMethod = newVal.authMethod || 1;
        formState.username = newVal.username || '';
        formState.password = newVal.password || '';
        formState.databaseName = newVal.databaseName || '';
        formState.databaseType = newVal.databaseType || '';
        formState.databaseDescription = newVal.databaseDescription || '';
        requiresAuth.value = formState.authMethod === 1;
      }
    }, { immediate: true });

    const onAuthMethodChange = (value) => {
      requiresAuth.value = value === 1;
    };

    const onSubmit = async () => {
      try {
        await nextTick();
        await formRef.value.validate();
        const data = { ...formState };

        if (!requiresAuth.value) {
          delete data.username;
          delete data.password;
        }

        if (props.mode === 'add') {
          const res = await post('/databases/add', data);
          if (res.success) {
            message.success("数据库配置成功");
          }
        } else {
          data.databaseInfoId = props.initialValues.databaseInfoId;
          const res = await post('/databases/update', data);
          if (res.success) {
            message.success("数据库配置修改成功");
          }
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
      if (props.initialValues) {
        formState.host = props.initialValues.host || '';
        formState.port = props.initialValues.port || null;
        formState.authMethod = props.initialValues.authMethod || 1;
        formState.username = props.initialValues.username || '';
        formState.password = props.initialValues.password || '';
        formState.databaseName = props.initialValues.databaseName || '';
        formState.databaseType = props.initialValues.databaseType || '';
        formState.databaseDescription = props.initialValues.databaseDescription || '';
        onAuthMethodChange(formState.authMethod);
      }
    });

    return {
      formRef,
      formState,
      databaseTypes,
      rules,
      requiresAuth,
      onSubmit,
      onCancel,
      onAuthMethodChange
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
