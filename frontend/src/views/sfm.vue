<template>
  <div class="sfm-page">
    <!-- 返回按钮 -->
    <div class="back">
      <el-button class="back-button" :icon="ArrowLeft" @click="$router.back()"></el-button>
    </div>

    <!-- 条形码或错误信息 -->
    <div v-if="identityCode" class="barcode-container">
      <svg id="barcode"></svg>
    </div>
    <div v-else-if="errorMessage" class="error">
      {{ errorMessage }}
    </div>
  </div>

  <!-- 底部图片 -->
  <div class="bottom-image-container">
    <img class="bottom-image" :src="sfmbackground" alt="Bottom Image" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue';
import { useUserStore } from '@/stores/user';
import axios from 'axios';
import { ArrowLeft } from '@element-plus/icons-vue';
import JsBarcode from 'jsbarcode';
import sfmbackground from '../assets/icons/sfmbackground.png';

const userStore = useUserStore();
const token = ref(userStore.token);
const identityCode = ref('');
const errorMessage = ref('');

// 查询身份码（通过 token）
const fetchIdentityCode = async () => {
  try {
    const response = await axios.get('/api/getSfmByToken', {
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
    });

    if (response.data.type === 'success') {
      identityCode.value = response.data.identityCode;
      errorMessage.value = '';
    } else {
      identityCode.value = '';
      errorMessage.value = response.data.message || '查询失败';
    }
  } catch (error) {
    identityCode.value = '';
    errorMessage.value = '请求失败：' + error.message;
  }
};

// 监听 identityCode 的变化并生成条形码
watch(identityCode, async (newCode) => {
  if (newCode) {
    await nextTick(); // 等待 DOM 更新完成
    JsBarcode("#barcode", newCode, {
      format: "CODE128",
      lineColor: "#000",
      width: 2,
      height: 100,
      displayValue: true,
    });
  }
});

// 页面加载时自动查询身份码
onMounted(() => {
  if (token.value) {
    fetchIdentityCode();
  } else {
    errorMessage.value = '未登录，请先登录后再查看身份码';
  }
});
</script>

<style scoped>
/* 确保页面根元素占满整个视口 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  background-color: #007bff; /* 同页面背景色一致 */
}

/* 页面主容器，内容居中显示并响应式适配 */
.sfm-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  height: 100vh;
  background-color: #007bff;
  position: relative;
  padding-top: 64px;
  padding-bottom: 140px; /* 为底部图片留出空间 */
  max-width: 480px;
  margin: 0 auto;
  box-sizing: border-box;
}

/* 返回按钮定位 */
.back {
  position: absolute;
  top: 16px;
  left: 16px;
  z-index: 10;
}

.back-button {
  font-size: 16px;
}

/* 条形码区域 */
.barcode-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
  width: 100%;
}

.barcode-container svg {
  max-width: 90%;
  height: auto;
}

/* 错误信息 */
.error {
  margin-top: 16px;
  font-size: 18px;
  color: #f44336;
  text-align: center;
  padding: 0 16px;
}

/* 底部图片容器：脱离 sfm-page，横跨全屏 */
.bottom-image-container {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100vw;       /* 关键：强制横跨整个屏幕宽度 */
  max-width: 100%; /* 防止超出屏幕 */
  z-index: 1;
}

.bottom-image {
  width: 100%;
  height: auto;
  display: block;
  object-fit: contain;
}

/* 大屏适配优化 */
@media (min-width: 787px) {
  .buttom-image-container {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100vw; /* 关键：强制横跨整个屏幕宽度 */
    max-width: 440px; /* 防止超出屏幕 */
    z-index: 1;
  }
  .bottom-image {
  width: 100vh;
  max-width: 600px; /* 防止超出屏幕 */
  height: auto;
  display: flex;

  


  }

  .back-button {
    font-size: 20px;
  }

  .error {
    font-size: 20px;
  }

  .barcode-container {
    margin-top: 32px;
  }
}
</style>
