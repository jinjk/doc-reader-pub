import { defineStore } from 'pinia'

export const useBookStore = defineStore('counter', {
  state: () => ({
    books: {},
    currentBook: null,
    _status: null
  }),

  getters: {
    getBookById: (state) => {
      return (id) => state.books[id]
    },

    status: (state) => state._status
  },

  actions: {
    addBooks (bookList) {
      for (const book of bookList) {
        this.books[book.id] = book
      }
    },

    setCurrentBookById (id) {
      this.currentBook = this.books[id]
    },

    setStatus (_status) {
      this._status = _status
    }
  }
})
