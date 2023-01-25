from flask import Flask, jsonify, request
import pkuseg
  
# creating a Flask app
app = Flask(__name__)
# 以默认配置加载模型
seg = pkuseg.pkuseg()

# on the terminal type: curl http://127.0.0.1:5000/
# returns hello world when we use GET.
# returns the data that we send when we use POST.
@app.route('/', methods = ['POST'])
def home():
    req_data = request.json
    lines = req_data['lines']
    res = []
    if lines:
        for line in lines:
            words = seg.cut(line)
            res.append(words)

    return jsonify({'res': res})
  
  
# A simple function to calculate the square of a number
# the number to be squared is sent in the URL when we use GET
# on the terminal type: curl http://127.0.0.1:5000 / home / 10
# this returns 100 (square of 10)
@app.route('/', methods = ['GET'])
def  get():
    return jsonify({'msg': 'Hello World'})
  
  
def get_app():
    app.config['DEBUG'] = True
    return app
