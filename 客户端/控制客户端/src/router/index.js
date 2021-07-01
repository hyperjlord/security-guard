import Vue from 'vue'
import VueRouter from 'vue-router'

//group-main-view
const MainView = () => import(/* webpackChunkName: "group-main-view" */ '../views/MainView.vue');
const Indoor = () => import(/* webpackChunkName: "group-main-view" */ '../views/MainView/Indoor.vue');
const Outdoor = () => import(/* webpackChunkName: "group-main-view" */ '../views/MainView/Outdoor.vue');
const SelfHome = () => import(/* webpackChunkName: "group-main-view" */ '../views/MainView/SelfHome.vue');
//group-guest
const Guest = () => import(/* webpackChunkName: "group-guest" */ '../views/Guest.vue');
const Login = () => import(/* webpackChunkName: "group-guest" */ '../components/Guest/Login.vue');
const Register = () => import(/* webpackChunkName: "group-guest" */ '../components/Guest/Register.vue');
//group-indoor
const IndoorSelect = () => import(/* webpackChunkName: "group-indoor" */ '../components/Indoor/Select.vue');
const IndoorStream = () => import(/* webpackChunkName: "group-indoor" */ '../components/Indoor/Stream.vue');
const IndoorSetting = () => import(/* webpackChunkName: "group-indoor" */ '../components/Indoor/Setting.vue');
//group-outdoor
const OutdoorSelect = () => import(/* webpackChunkName: "group-outdoor" */ '../components/Outdoor/OutdoorSelect.vue');
const OutdoorTrack = () => import(/* webpackChunkName: "group-outdoor" */ '../components/Outdoor/OutdoorTrack.vue');
const OutdoorSetting = () => import(/* webpackChunkName: "group-outdoor" */ '../components/Outdoor/OutdoorSetting.vue');
const OutdoorHistory = () => import(/* webpackChunkName: "group-outdoor" */ '../components/Outdoor/OutdoorHistory.vue');
//group-self-home
const MessageList = () => import(/* webpackChunkName: "group-self-home" */ '../components/SelfHome/MessageList.vue');
const RecordList = () => import(/* webpackChunkName: "group-self-home" */ '../components/SelfHome/RecordList.vue');
const SelfSetting = () => import(/* webpackChunkName: "group-self-home" */ '../components/SelfHome/SelfSetting.vue');

const redirect = () => {
  return "/login";
}

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Root',
    redirect: redirect()
  },
  {
    path: '/main-view',
    name: 'MainView',
    component: MainView,
    redirect: '/indoor',
    children: [
      {
        path: '/self-home',
        name: 'SelfHome',
        component: SelfHome,
        children: [
          {
            path: 'message-list',
            name: 'MessageList',
            component: MessageList,
          },
          {
            path: 'record-list',
            name: 'RecordList',
            component: RecordList,
          },
          {
            path: 'self-setting',
            name: 'SelfSetting',
            component: SelfSetting,
          }
        ]
      },
      {
        path: '/indoor',
        name: 'Indoor',
        component: Indoor,
        children: [
          {
            path: 'select',
            name: 'IndoorSelect',
            component: IndoorSelect,
          },
          {
            path: 'stream',
            name: 'IndoorStream',
            component: IndoorStream,
          },
          {
            path: 'setting',
            name: 'IndoorSetting',
            component: IndoorSetting,
          }
        ]
      },
      {
        path: '/outdoor',
        name: 'Outdoor',
        component: Outdoor,
        children: [
          {
            path: 'select',
            name: 'OutdoorSelect',
            component: OutdoorSelect,
          },
          {
            path: 'track',
            name: 'OutdoorTrack',
            component: OutdoorTrack,
          },
          {
            path: 'setting',
            name: 'OutdoorSetting',
            component: OutdoorSetting,
          },
          {
            path: 'history',
            name: 'OutdoorHistory',
            component: OutdoorHistory,
          }
        ]
      }
    ]
  },
  {
    path: '/guest',
    name: 'Guest',
    component: Guest,
    redirect: "/login",
    children: [
      {
        path: '/login',
        name: 'Login',
        component: Login,
        children: []
      },
      {
        path: '/register',
        name: 'Register',
        component: Register,
        children: []
      }
    ]
  },
]

const router = new VueRouter({
  routes
})

//路由全局前置守卫
router.beforeEach((to, from, next) => {
  if (to.fullPath === from.fullPath ) {
    next(false);
  }
  else {
    next();
  }
})

export default router