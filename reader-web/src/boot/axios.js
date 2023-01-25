import { boot } from 'quasar/wrappers'
import axios from 'axios'

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create({
  baseURL: '/book-api',
  timeout: 1000 * 30
})

const notifyError = function (error, quasar) {
  const msgList = []
  if (error.response) {
    // The request was made and the server responded with a status code
    // that falls out of the range of 2xx
    const data = error.response.data
    let msg = 'Unkown'
    if (typeof (data) === 'string') {
      msg = data
    } else {
      msg = data.errorMsg
    }
    msgList.push(msg)
  } else {
    msgList.push(error.message)
  }
  quasar.notify({
    color: 'negative',
    position: 'top',
    message: msgList.join(', '),
    icon: 'report_problem'
  })
}

export default boot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file
  api.defaults.headers.common['device-id'] = app.config.globalProperties.$devUUID

  app.config.globalProperties.$api = api
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API
})

export { api, notifyError }
