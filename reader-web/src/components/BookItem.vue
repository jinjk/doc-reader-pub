<template>
  <q-item
    clickable
    :to="link(id)"
  >
    <q-item-section
      avatar
    >
      <q-icon name="import_contacts" />
    </q-item-section>

    <q-item-section>
      <q-item-label>{{ bookName }}</q-item-label>
    </q-item-section>
  </q-item>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'BookItem',
  props: {
    bookName: {
      type: String,
      required: true
    },

    id: {
      type: Number,
      default: -1
    },

    pageCount: {
      type: Number,
      default: -1
    }
  },
  setup () {
    function link (id) {
      return `/books/${id}?page=${1}`
    }
    return { link }
  }
})
</script>
