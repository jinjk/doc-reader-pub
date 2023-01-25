import { boot } from 'quasar/wrappers'
import { LocalStorage } from 'quasar'
import { v4 as uuidv4 } from 'uuid'

const DEV_UUID_KEY = 'DEV_UUID_KEY'

const saveBookMark = function (book, page) {
  const bookInfo = { book, page }
  LocalStorage.set('BOOK_INFO', bookInfo)
}

const loadBookMark = function () {
  return LocalStorage.getItem('BOOK_INFO')
}
// "async" is optional;
// more info on params: https://v2.quasar.dev/quasar-cli/boot-files
export default boot(({ app }) => {
  let devUUID = LocalStorage.getItem(DEV_UUID_KEY)
  if (devUUID == null) {
    devUUID = uuidv4()
    LocalStorage.set(DEV_UUID_KEY, devUUID)
  }
  app.config.globalProperties.$devUUID = devUUID
})

export { saveBookMark, loadBookMark }
