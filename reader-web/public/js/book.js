let end = 0
let lastGrp = ''
let lastGrpItems = null

const truncate = (str, len) => {
  if (str.length > len) {
    if (len <= 3) {
      return str.slice(0, len - 3) + '...'
    } else {
      return str.slice(0, len) + '...'
    }
  } else {
    return str
  }
}

$(function () {
  let currentTarget = null
  window.addEventListener('message', function (e) {
    const data = e.data
    if (data.event === 'page') {
      end = data.end
      $('body').append(data.pages)
    }
    // TODO: test query dict result
    if (data.event === 'word') {
      console.log(data.dict)
      showTip(currentTarget, data.dict)
      $(currentTarget).on('mouseout', function () {
        if ($(this).attr('tip_init')) {
          setTimeout(() => $(this).tooltip('disable'), 1000)
        }
      })
    }

    if (data.event === 'wordHint') {
      if (data.active) {
        $('.e1').attr('class', 'c1')
        $('.e2').attr('class', 'c2')
        $('.e3').attr('class', 'c3')
      } else {
        $('.c1').attr('class', 'e1')
        $('.c2').attr('class', 'e2')
        $('.c3').attr('class', 'e3')
      }
    }
  })

  window.onscroll = function () {
    if (window.innerHeight + window.pageYOffset >= document.body.offsetHeight) {
      window.parent.postMessage({ event: 'page', page: (end + 1) }, '*')
    }
  }

  const tooltipOptions = {
    items: 'w',
    position: {
      my: 'center bottom-20',
      at: 'center top',
      using: function (position, feedback) {
        $(this).css(position)
        $('<div>')
          .addClass('arrow')
          .addClass(feedback.vertical)
          .addClass(feedback.horizontal)
          .appendTo(this)
      }
    },

    hide: {
      duration: 0,
      delay: 1000
    },

    show: {
      duration: 0
    }
  }

  function showTip (target, data) {
    tooltipOptions.content = `<div>${data.pinYin}</div>
    <div>${data.word}</div>
    <div>${truncate(data.content, 120)}</div>`
    $(target).tooltip(tooltipOptions)
    $(target).tooltip('open')
    $(target).attr('tip_init', true)
  }

  $(document).on('mouseover', 'w', function (e) {
    const item = $(e.target)
    const grp = item.attr('grp')
    const page = item.parent().parent()
    const words = page.find(`w[grp=${grp}]`)
    if (lastGrp === '') {
      lastGrp = grp
      lastGrpItems = words
    }
    if (lastGrp !== '' && lastGrp !== grp) {
      lastGrpItems.css('background-color', '')
      lastGrp = grp
      lastGrpItems = words
    }
    words.css('background-color', 'rgb(238, 238, 168)')
  })

  $(document).on('click', 'w', function (e) {
    currentTarget = e.target
    const item = $(e.target)
    const grp = item.attr('grp')
    const page = item.parent().parent()
    const words = page.find(`w[grp=${grp}]`)
    let wordVal = ''
    words.each((i, w) => {
      wordVal += $(w).text()
    })
    window.parent.postMessage({ event: 'word', word: wordVal }, '*')
  })
})
