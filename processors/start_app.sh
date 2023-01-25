#!/bin/bash
gunicorn -w 1 -b :5001 'word_seg.seg_controller:get_app()'