# Doc Reader

Doc Reader is a reader app which provides functions like Chinese word segmentation and a built-in dictionary to show the meaning a Chinese vocabulary.
Online demo can be found here [35utilsvr.com](http://35utilsvr.com)
## Components

1. doc-reader-service
This is a micro-service component which has the APIs to get/upload PDF file, get book pages and lookup dictionary.
2. doc-file-server
DocFileServer is a batch job processing tool, used to convert uploaded file into html files then do word segmentation and update html files with segment meta info.
3. reader-web
ReaderWeb is the front-end of this app, it's built using [quasar framework](https://quasar.dev/), a UI frameword based on Vue3.
4. pkuseg
pkuseg is Chinese segmentation tool provided by Peking University
https://github.com/lancopku/pkuseg-python

