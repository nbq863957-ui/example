<template>
    <div class="login-wrapper" :style="{ backgroundImage: `url(${cnbg})`, backgroundSize: 'cover' }">
      <div class="login-center glass-effect">
        <div class="login-header text-center">
          <a>菜鸟驿站管理系统</a>
        </div>
        <el-form :model="form" ref="loginForm" class="login-form" @submit.prevent="handleLogin">
          <el-form-item prop="username" :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]">
            <el-input v-model="form.username" placeholder="请输入您的用户名" prefix-icon="user"></el-input>
          </el-form-item>
          <el-form-item prop="password" :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }]">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="lock"></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-checkbox v-model="form.autoLogin">5天内自动登录</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" block @click="handleLogin">立即登录</el-button>
          </el-form-item>
        </el-form>
        <hr />
        <footer class="text-center">
          <p class="m-b-0">
            Copyright © {{ currentYear }}
            <a >运城职业技术学校</a>. All rights reserved.
          </p>
        </footer>
      </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import axios from 'axios';
import bg from '../assets/login-bg-2.jpg';
import cnbg from '../assets/cnbg.jpg';
const form = ref({
  username: '',
  password: '',
  captcha: '',
  autoLogin: false,
});

const loginForm = ref(null); 
const currentYear = new Date().getFullYear();
const router = useRouter();
const userStore = useUserStore();

const handleLogin = async () => {
  if (!loginForm.value) {
    console.error('loginForm 未绑定');
    return;
  }
  loginForm.value.validate(async (valid) => {
    if (valid) {
      if (form.value.username !== 'admin') {
        ElMessage.error('你不是管理员，无法登录');
        return;
      }
      try {
        const response = await axios.post('/api/login', form.value);
        const { type, message, token: receivedToken } = response.data;

        if (type === 'success') {
          userStore.login(form.value.username, receivedToken);
          ElMessage.success(message || '登录成功');
          router.push('/pac');
        } else {
          ElMessage.error(message || '登录失败');
        }
      } catch (error) {
        console.error('登录失败:', error);
        ElMessage.error('网络或服务器错误，请稍后重试');
      }
    } else {
      console.log('表单验证失败');
      ElMessage.error('表单验证失败');
    }
  });
};
</script>

<style scoped>
.login-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
}

.login-center {
  background: rgba(255, 255, 255, 0.2);
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.37);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  width: 400px;
}

.login-header img {
  max-width: 100%;
  height: auto;
}

.captcha-container {
  display: flex;
  align-items: center;
}

.captcha-img {
  margin-left: 10px;
  cursor: pointer;
  height: 38px;
}
</style>