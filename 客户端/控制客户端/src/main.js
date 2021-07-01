import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import Vant from 'vant';
import { Notify, Toast } from 'vant'
import 'vant/lib/index.css';
import Aplayer from 'vue-aplayer'

Vue.use(Aplayer);
Vue.use(Vant);
Vue.prototype.$notify = Notify;
Vue.prototype.$toast = Toast;

import axios from 'axios';
Vue.prototype.$axios = axios;

import api from './assets/config/api'
Vue.prototype.$api = api;

import AmapVue from "@amap/amap-vue";
AmapVue.config.key = '4dcbef0386968be0a666efdc0f179513';
Vue.use(AmapVue);

// 导入全局样式表
import "./assets/css/global.css"

Vue.config.productionTip = false;

Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };

    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(
                RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }

    return fmt;
}

//请求拦截器 发送请求前,会发送一个token
axios.interceptors.request.use((config) => {
    let temp;
    print(config);
    if ((temp = localStorage.getItem("token")) != null) {
        config.headers.token = localStorage.getItem('token');
    }
    return config;
}, (error) => {
    Notify({ type: "danger", message: error.message });
    return Promise.reject(error);
});
//响应拦截器
axios.interceptors.response.use((response) => {
    print(response);
    if (response.data.stateCode === undefined || response.data.stateCode === null) {
        return response;
    } else {
        switch (response.data.stateCode) {
            case api.ERROR:
                Notify({ type: "danger", message: response.data.msg });
                return Promise.reject(response);
            case api.WARNING:
                Notify({ type: "warning", message: response.data.msg });
                return Promise.reject(response);
            case api.TOKEN_ISSUE:
                Notify({ type: "warning", message: response.data.msg });
                localStorage.removeItem("token");
                return Promise.reject(response);
            case api.SUCCESS:
            case api.WAITING:
            default:
                return response;

        }

    }

}, (error) => {
    //当响应异常时
    let isTimeout = error.toString().includes('timeout')
    if (isTimeout) {
        Notify({
            message: '请求超时...',
            type: 'warning',
            duration: 2000,
        })
    }
    return Promise.reject(error);
});

//全局调试用打印功能
print = async function (object) {
    console.log(object);
}

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')