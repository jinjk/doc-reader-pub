
import { useBookStore } from 'src/stores/book-store'
const routes = [
  {
    path: '/:index?',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') }
    ]
  },

  {
    name: 'book',
    path: '/books/:id',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/BookPage.vue') }
    ],
    beforeEnter: (to, from, next) => {
      const bookStore = useBookStore()
      bookStore.status.then(() => {
        bookStore.setCurrentBookById(to.params.id)
        next()
      }).catch(() => {
        next({ component: () => import('pages/ErrorNotFound.vue') })
      })
    }
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
