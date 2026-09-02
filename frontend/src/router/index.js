import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import list from '../views/list.vue';
import homepage from '../views/homepage.vue';
import component from 'element-plus/es/components/tree-select/src/tree-select-option.mjs';
import send from '../views/send.vue';
import { pa, ro } from 'element-plus/es/locales.mjs';
import pac  from '../views/packa1ge.vue';
import global from '../views/global.vue';
import sfm from '../views/sfm.vue';
import scan from '../views/scan.vue';
import adminlogin from '../views/adminlogin.vue';
import { validateToken } from '../utils/authExpired';

const publicPaths = ['/', '/register', '/adminlogin'];

const routes = [
  {
    path: '/',
    meta:{
      title:'菜鸟驿站'
    },
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    meta:{
      title:'注册'
    },
    name: 'Register',
    component: Register
  },
  {
    path: '/list',
    name: 'list',
    meta:{
      title:'用户管理'
    },
    component: list
  },
  {
    path:'/home',
    name:'homepage',
    meta:{
      title:'首页'
    },
    component: homepage
  },
  {
    path:'/send',
    name:'send',
    component:send
  },{
    path:'/pac',
    
    meta:{
      title:'包裹管理'
    },
    component:pac
  },{
    path:'/global',
    name:'global',  
    component:global
  },{
    path:'/scan', 
    name:'scan',
    component:scan
  },{
    path:'/sfm',
    name:'sfm',
    component:sfm
  },{
    path:'/adminlogin',
    name:'adminlogin',
    meta:{
      title:'管理员登录'
    },
    component:adminlogin
  }

];

const router = createRouter({
 
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});
router.beforeEach(async(to, from, next) => {
  if(to.meta.title){
    document.title = to.meta.title
  }
  const token = localStorage.getItem('token')
  if (!publicPaths.includes(to.path) && !token) {
    next({ path: '/', query: { redirect: to.fullPath } })
    return
  }
  if (!publicPaths.includes(to.path) && token) {
    const isValid = await validateToken()
    if (!isValid) {
      localStorage.removeItem('username')
      localStorage.removeItem('token')
      next({ path: '/', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router;
