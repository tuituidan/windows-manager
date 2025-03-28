import axios from 'axios'
import { Message, Loading } from 'element-ui'
import QS from 'qs';

// 计数器及Loading实例
let loadingCount = 0;
let loadingInstance = null;

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  baseURL: process.env.VUE_APP_BASE_API,
  // 超时
  timeout: 100000
})

// request拦截器
service.interceptors.request.use(config => {
  config.headers['X-Requested-With'] = 'XMLHttpRequest';
  config.paramsSerializer = param => QS.stringify(param, {indices: false});
  if (config.params) {
    for (const key in config.params) {
      const obj = config.params[key];
      if ([null, undefined, '', NaN].includes(obj) || (Array.isArray(obj) && obj.length <= 0)) {
        delete config.params[key];
      }
    }
  }
  if(!isGetRequest(config)){
    loadingCount++;
    if (loadingCount === 1) {
      loadingInstance = Loading.service({
        fullscreen: true,
        text: '处理中...',
        background: 'rgba(0, 0, 0, 0.7)'
      });
    }
  }
  return config;
}, error => {
  console.log(error)
  if(!isGetRequest(error.config)){
    loadingCount--;
    if (loadingCount <= 0) {
      resetLoading();
    }
  }
  return Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(res => {
    if(!isGetRequest(res.config)){
      decrementLoading();
    }
    return res.data;
  },
  err => {
  console.log(err)
    if(!isGetRequest(err.config)){
      decrementLoading();
    }
    let {message} = err;
    if (message === "Network Error") {
      Message.error('后端接口连接异常');
      return Promise.reject(err)
    }
    if (message.includes("timeout")) {
      Message.error('系统接口请求超时');
      return Promise.reject(err)
    }
    Message.error(err.response.data);
    return Promise.reject(err)
  }
)
function isGetRequest(config) {
  return config.method === 'get' || config.method === 'GET'
}
// 减少loading计数
function decrementLoading() {
  loadingCount--;
  if (loadingCount <= 0) {
    resetLoading();
  }
}

// 重置loading
function resetLoading() {
  if (loadingInstance) {
    loadingInstance.close();
    loadingInstance = null;
  }
  loadingCount = 0;
}
service.save = (url, data) => data.id ? service.patch(`${url}/${data.id}`, data) : service.post(url, data);

export default service
