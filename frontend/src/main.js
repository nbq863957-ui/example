import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import ElementPlus from 'element-plus';
import { createPinia } from 'pinia';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import 'element-plus/dist/index.css';
import { setupAuthExpiredHandler } from './utils/authExpired';

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(ElementPlus);

setupAuthExpiredHandler(router);
app.use(router);

// 注册 Element Plus 图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.mount('#app');
