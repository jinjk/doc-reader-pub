<template>
  <q-page class="row">
    <q-card class="col-xs-12 col-md-9">
      <q-toolbar />
      <q-card-section>
        <div class="text-h6">
          小小阅读器是为小朋友提供一个方便阅读时查询词汇含义的工具，它可以高亮显示词汇，并提供词义查询功能。
        </div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue'
import { useRouter, useRoute } from 'vue-router'

import { loadBookMark } from 'src/boot/dev-uuid'

export default defineComponent({
  name: 'IndexPage',

  setup () {
    const router = useRouter()
    const route = useRoute()
    const index = route.params.index
    console.log(`index ${index}`)
    if (index !== 'index') {
      const bookInfo = loadBookMark()
      if (bookInfo != null) {
        const pageNum = bookInfo.page == null ? 1 : bookInfo.page
        router.push(`/books/${bookInfo.book}?page=${pageNum}`)
      }
    }
  }
})
</script>
