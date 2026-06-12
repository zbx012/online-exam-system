import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

//项目级Axios配置中心
// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // 后端API地址
  timeout: 15000 // 15秒超时
})

// 请求拦截器 — 自动添加 JWT Token
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器中添加成功提示
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 如果响应包含成功信息，显示提示
    const data = response.data
    if (typeof data === 'string') {
      if (data.includes('成功')) {
        // 避免重复提示，只在控制台显示
        console.log('✅ 操作成功:', data)
      }
    }
    return response
  },
  (error: any) => {
    console.error('❌ 请求配置错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    console.log('🟢 响应接收:', {
      url: response.config.url,
      status: response.status,
      data: response.data
    })
    return response
  },
  (error: any) => {
    console.error('❌ 响应错误:', error)
    
    if (error.response) {
      // 服务器返回了错误状态码
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          ElMessage.error(`请求错误: ${data || '参数有误'}`)
          break
        case 401:
          ElMessage.error('未授权，请重新登录')
          // 可以跳转到登录页
          break
        case 403:
          ElMessage.error('禁止访问')
          break
        case 404:
          ElMessage.error('请求的接口不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        case 502:
          ElMessage.error('网关错误')
          break
        case 503:
          ElMessage.error('服务不可用')
          break
        case 504:
          ElMessage.error('网关超时')
          break
        default:
          ElMessage.error(`请求失败: ${status}`)
      }
    } else if (error.request) {
      // 请求发送了但没有收到响应
      if (error.code === 'ECONNABORTED') {
        ElMessage.error('请求超时，请检查网络')
      } else {
        ElMessage.error('网络错误，请检查网络连接')
      }
    } else {
      // 请求配置出错
      ElMessage.error(`请求错误: ${error.message}`)
    }
    
    return Promise.reject(error)
  }
)
export default service