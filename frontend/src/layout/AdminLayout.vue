<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '210px'" class="admin-aside">
      <div class="logo">
        <img v-if="isCollapse" class="logo-mini" src="@/assets/logo-ico.png" alt="logo" />
        <template v-else>
          <img src="@/assets/logo-sidebar.png" alt="logo" />
          <span>菜鸟驿站后台</span>
        </template>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#001529"
        text-color="#b7c5d4"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>数据看板</template>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/packages">
          <el-icon><Box /></el-icon>
          <template #title>快递管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/stations">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>驿站管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-icon><Avatar /></el-icon>
          <span class="admin-name">{{ username }}</span>
          <el-tag size="small" type="warning">管理员</el-tag>
          <el-button type="danger" size="small" plain @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  DataLine, User, Box, OfficeBuilding, Fold, Expand, Avatar
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const username = computed(() => userStore.username || 'admin')
const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta?.title || '后台管理')

const handleLogout = () => {
  userStore.logout()
  router.replace('/adminlogin')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}
.admin-aside {
  background-color: #001529;
  transition: width 0.28s;
  overflow-x: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  background: #002140;
  white-space: nowrap;
}
.logo img {
  height: 30px;
}
.logo-mini {
  height: 28px;
}
.admin-aside :deep(.el-menu) {
  border-right: none;
}
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #5a5e66;
}
.admin-name {
  font-weight: 600;
  color: #303133;
}
.admin-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
