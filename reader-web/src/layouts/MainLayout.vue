<template>
  <q-layout view="HHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          <q-btn
            flat
            class="text-h5"
            :to="{path: '/index'}"
            label="35阅读"
          />
        </q-toolbar-title>

        <div>Quasar v{{ $q.version }}</div>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
    >
      <q-list>
        <q-item-label
          header
        >
          Books
        </q-item-label>

        <BookItem
          v-for="book in books"
          :key="book.id"
          v-bind="book"
        />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view :key="$route.fullPath" />
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent, ref } from 'vue'
import { useQuasar } from 'quasar'
import BookItem from 'components/BookItem.vue'
import { useBookStore } from 'stores/book-store'

export default defineComponent({
  name: 'MainLayout',

  components: {
    BookItem
  },

  setup () {
    const $q = useQuasar()
    const books = ref(null)
    const leftDrawerOpen = ref(false)
    // eslint-disable-next-line func-call-spacing
    const bookStore = useBookStore()
    // eslint-disable-next-line no-unexpected-multiline

    bookStore.status
      .then(recommended => {
        books.value = recommended
      })
      .catch((msg) => {
        $q.notify({
          color: 'negative',
          position: 'top',
          message: msg.join(', '),
          icon: 'report_problem'
        })
      })

    return {
      books,
      leftDrawerOpen,
      toggleLeftDrawer () {
        leftDrawerOpen.value = !leftDrawerOpen.value
      }
    }
  }
})
</script>
