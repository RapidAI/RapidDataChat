<template>
  <a-layout style="min-height: 100vh; max-height: 100vh">
    <a-layout-sider v-model:collapsed="collapsed" theme="light">
      <div class="logo-container">
        <div class="logo"></div>
        <div class="logo-text">RapidDataChat</div>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys">
        <a-menu-item key="User" @click="navigateTo('/User')">
          <UserOutlined/>
          <span>用户管理</span>
        </a-menu-item>
        <a-menu-item key="ModelTable" @click="navigateTo('/ModelTable')">
          <ToolOutlined/>
          <span>AI模型管理</span>
        </a-menu-item>
        <a-menu-item key="Database" @click="navigateTo('/Database')">
          <DatabaseOutlined/>
          <span>数据库信息</span>
        </a-menu-item>
        <a-menu-item key="Table" @click="navigateTo('/Table')">
          <AppstoreOutlined/>
          <span>表信息管理</span>
        </a-menu-item>
        <a-menu-item key="Column" @click="navigateTo('/Column')">
          <TableOutlined/>
          <span>字段信息管理</span>
        </a-menu-item>
        <a-menu-item key="SessionWithBigData" @click="navigateTo('/SessionWithBigData')">
          <LineChartOutlined/>
          <span>多数据库AI分析</span>
        </a-menu-item>
        <a-menu-item key="QueryVector" @click="navigateTo('/QueryVector')">
          <FileSearchOutlined/>
          <span>sql知识库</span>
        </a-menu-item>
        <a-menu-item key="Query" @click="navigateTo('/Query')">
          <SearchOutlined/>
          <span>查询执行记录</span>
        </a-menu-item>
        <a-menu-item key="MySettings" @click="navigateTo('/MySettings')">
          <SettingOutlined/>
          <span>个人信息</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <router-view></router-view>
    </a-layout>
  </a-layout>
</template>

<script>
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Layout, Menu } from 'ant-design-vue';
import {
  UserOutlined,
  DatabaseOutlined,
  TableOutlined,
  BarChartOutlined,
  MessageOutlined,
  FileSearchOutlined,
  SettingOutlined,
  AppstoreOutlined,
  SearchOutlined,
  DownOutlined,
  ToolOutlined,
  LineChartOutlined // 新增图标用于多数据库AI分析
} from '@ant-design/icons-vue';
import { useUserStore } from '@/store/UserStore.js';

export default {
  components: {
    Layout,
    Menu,
    UserOutlined,
    DatabaseOutlined,
    TableOutlined,
    BarChartOutlined,
    MessageOutlined,
    FileSearchOutlined,
    SettingOutlined,
    AppstoreOutlined,
    SearchOutlined,
    DownOutlined,
    ToolOutlined,
    LineChartOutlined // 注册新图标
  },
  setup() {
    const collapsed = ref(false);
    const router = useRouter();
    const route = useRoute();
    const userStore = useUserStore();
    const selectedKeys = ref([route.path.split('/').pop()]);

    const navigateTo = (route) => {
      router.push(route);
      selectedKeys.value = [route.split('/').pop()];
    };

    const logout = () => {
      userStore.logout();
    };

    // 监听路由变化，更新选中的菜单项
    watch(
        () => route.path,
        (newPath) => {
          selectedKeys.value = [newPath.split('/').pop()];
        }
    );

    return {
      collapsed,
      selectedKeys,
      navigateTo,
      userStore,
      logout,
    };
  }
};
</script>

<style scoped>
.logo-container {
  display: flex;
  align-items: center;
  height: 64px;
  padding: 16px;
  background-color: #fff;
}

.logo {
  width: 32px;
  height: 32px;
  background: url('/logo.png') no-repeat center center;
  background-size: contain;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  margin-left: 10px;
  color: #333;
}
</style>