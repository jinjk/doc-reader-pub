import { boot } from 'quasar/wrappers'
import { useBookStore } from 'src/stores/book-store'

// "async" is optional;
// more info on params: https://v2.quasar.dev/quasar-cli/boot-files
export default boot(({ app, router }) => {
  const bookStore = useBookStore()
  const api = app.config.globalProperties.$api
  // eslint-disable-next-line no-unexpected-multiline
  bookStore.setStatus(new Promise((resolve, reject) => {
    api.get('/books')
      .then(response => {
        const recommended = response.data.recommended
        bookStore.addBooks(recommended)
        resolve(recommended)
      })
      .catch((error) => {
        const msg = []
        if (error.response) {
          // The request was made and the server responded with a status code
          // that falls out of the range of 2xx
          msg.push(error.response.data)
          msg.push(error.response.status)
          msg.push(error.response.headers)
        } else if (error.request) {
          msg.push(error.message)
        }
        reject(msg)
      })
  }))
})
