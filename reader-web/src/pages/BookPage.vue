<template>
  <q-page class="row">
    <q-card class="col-xs-12 col-md-9">
      <iframe
        id="book-content"
        style="position: relative; height: 90%; width: 100%; border: none"
      />
      <div class="q-pa-md q-gutter-sm">
        <q-banner
          inline-actions
          rounded
          class="bg-white"
        >
          <q-spinner
            v-if="loading"
            color="primary"
            size="3em"
          />
          <template #action>
            <q-toggle
              v-model="showWordHint"
              color="primary"
              label="词语提示"
              left-label
              @update:model-value="showWordHintChanged"
            />
          </template>
        </q-banner>
      </div>
    </q-card>
  </q-page>
</template>

<script>
import { useQuasar } from 'quasar'
import { useRoute } from 'vue-router'
import { ref } from 'vue'
import { api, notifyError } from 'boot/axios'
import { useBookStore } from 'src/stores/book-store'
import { saveBookMark } from 'src/boot/dev-uuid'

const pageLen = 5
const bookScript = `<_script src='js/jquery-3.6.1.min.js'></_script>
                    <_script src='js/jquery-ui.min.js'></_script>
                    <_script src='js/book.js'></_script>`.replaceAll('_script', 'script')
const cssLink = '<link rel="stylesheet" href="css/jquery-ui.min.css">' +
'<link rel="stylesheet" href="css/jquery-ui.theme.min.css">' +
'<link rel="stylesheet" href="css/page.css">'

const thisBook = {
  iframeLoaded: false,
  requestPage: 0,
  $q: null,
  downloadedPages: new Set(),

  init: function (quasar) {
    this.$q = quasar
    this.iframeLoaded = false
    this.requestPage = 0
    this.downloadedPages = new Set()
  },

  updateIndex: function (begin, end) {
    for (let i = begin; i <= end; i++) {
      this.downloadedPages.add(i)
    }
  },

  hasIndexed: function (begin, end) {
    const notIndexed = []
    for (let i = begin; i <= end; i++) {
      if (!this.downloadedPages.has(i)) {
        notIndexed.push(i)
      }
    }
    return notIndexed.length === 0
  },

  updateIframe: function (callback) {
    if (this.iframeLoaded === false) {
      setTimeout(() => this.updateIframe(callback), 100)
    } else {
      callback()
    }
  },

  getPage: function (pageNum) {
    if (this.requestPage === pageNum || this.hasIndexed(pageNum, pageNum + pageLen)) {
      return
    }
    this.requestPage = pageNum
    const endPage = pageNum + pageLen - 1
    // Get book pages
    this.$vue.enableLoading()
    api.get(`books/${this.bookId}/${pageNum},${pageLen}`)
      .then((resp) => {
        const bookFrame = document.querySelector('#book-content')
        let htmlLines = resp.data
        const metaIndex = htmlLines.lastIndexOf('35util-api-book-meta-info')
        if (this.$vue.showWordHint === false) {
          htmlLines = htmlLines.replace(/class=.c(\d)./g, 'class="e$1"')
        }
        let metaInfo = { begin: pageNum, end: endPage }
        if (metaIndex > 0) {
          const str = htmlLines.substring(metaIndex)
          metaInfo = JSON.parse(str.match(/\{.+\}/))
        }
        const pages = { event: 'page', pages: htmlLines, begin: metaInfo.begin, end: metaInfo.end }
        this.updateIndex(metaInfo.begin, metaInfo.end)
        this.updateIframe(() => { bookFrame.contentWindow.postMessage(pages, '*') })
        saveBookMark(this.bookId, pageNum)
      })
      .catch((error) => {
        notifyError(error, this.$q)
      })
      .finally(() => {
        this.$vue.disableLoading()
      })
  },

  getDictInfo: function (word) {
    this.$vue.enableLoading()
    api.get(`dict?word=${word}`)
      .then((resp) => {
        const bookFrame = document.querySelector('#book-content')
        const dictInfo = resp.data
        const eventData = { event: 'word', dict: dictInfo }
        this.updateIframe(() => { bookFrame.contentWindow.postMessage(eventData, '*') })
      })
      // eslint-disable-next-line n/handle-callback-err
      .catch((error) => { // notifyError(error, this.$q)
      })
      .finally(() => {
        this.$vue.disableLoading()
      })
  },

  showHideWordHint: function (value) {
    const bookFrame = document.querySelector('#book-content')
    const eventData = { event: 'wordHint', active: value }
    this.updateIframe(() => { bookFrame.contentWindow.postMessage(eventData, '*') })
  },

  initBookContainer: function () {
    this.$vue.enableLoading()
    return api.get(`books/${this.bookId}`)
      .then((resp) => {
        const bookFrame = document.querySelector('#book-content')
        const htmlLines = resp.data.split('\n')
        const len = htmlLines.length
        for (let i = len - 1; i > 0; i--) {
          if (htmlLines[i].includes('</head>')) {
            htmlLines.splice(i, 0, cssLink) // Add css link here
            break
          }
          if (htmlLines[i].includes('</body>')) {
            htmlLines.splice(i, 0, bookScript) // javascript inserted here
          }
          if (!this.$vue.showHideWordHint) {
            htmlLines[i] = htmlLines[i].replace(/class="c(\d)"/g, 'class="e$1"')
          }
        }
        bookFrame.srcdoc = htmlLines.join('\n')
        bookFrame.onload = () => {
          this.iframeLoaded = true
        }
        return Promise.resolve(resp)
      })
      .catch((error) => {
        notifyError(error, this.$q)
        return Promise.reject(error)
      })
      .finally(() => {
        this.$vue.disableLoading()
      })
  },

  addWindowEventListener: function () {
    window.addEventListener('message', (e) => {
      const data = e.data
      const event = data.event
      if (event === 'page') {
        const page = data.page
        if (page <= this.pageCount) {
          this.getPage(page)
        }
      }

      if (event === 'word') {
        const word = data.word
        if (word != null && word.trim().length > 0) {
          this.getDictInfo(word)
        }
      }
    })
  }
}

export default {
  name: 'BookPage',

  setup () {
    const loading = ref(false)
    const showWordHint = ref(true)
    const route = useRoute()
    const bookStore = useBookStore()

    thisBook.init(useQuasar())
    thisBook.bookId = parseInt(route.params.id)
    thisBook.page = parseInt(route.query.page)
    const book = bookStore.getBookById(thisBook.bookId)
    thisBook.pageCount = book != null ? book.pageCount : 0

    return { loading, showWordHint }
  },

  mounted () {
    thisBook.$vue = this
    thisBook.initBookContainer().then(() =>
      thisBook.getPage(thisBook.page)
    )
    thisBook.addWindowEventListener()
  },

  methods: {
    enableLoading () {
      this.loading = true
    },

    disableLoading () {
      setTimeout(() => { this.loading = false }, 2000)
    },

    showWordHintChanged (value, evt) {
      thisBook.showHideWordHint(value)
    }
  }
}
</script>
