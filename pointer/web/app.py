from flask import Flask, jsonify, request

import base64

from adapter import Adapter
import json

app = Flask(__name__, instance_relative_config=True)
app.config.from_object('default_settings')
app.config.from_pyfile('application.cfg', silent=True)


if not app.config['DISABLE_CV']:
    from transform import get_matrix, convert_coords
else:
    from test_transform import get_matrix, convert_coords

@app.route('/')
def index():
    return 'Pointer v1.0'


matrix = None


@app.route('/image', methods=['POST'])
def image():
    if 'img' in request.files:
        print 'get image file'
        #request.files['img'].save('file.jpg')
        img = request.files['img'].read()
    elif request.data:
        print 'get base64 image'
        data = json.loads(request.data)
        img = grab_image(data['img'])
    else:
        print 'no image'
        return jsonify(result='empty request'), 500


    m = get_matrix(img)
    if m != None:
        global matrix
        matrix = m
        return jsonify(result='ok')
    else:
        return jsonify(result='cannot find markers'), 404



@app.route('/background', methods=['POST'])
def background():
    if 'img' in request.files:
        print 'get bg-image file'
        #request.files['img'].save('file.jpg')
        img = request.files['img'].read()
    elif request.data:
        print 'get bg-base64 image'
        data = json.loads(request.data)
        img = grab_image(data['img'])
    else:
        print 'no image'
        return jsonify(result='empty request'), 500

    Adapter.send_background(img)
    return jsonify(result='bg updated'), 200



@app.route('/gesture', methods=['POST'])
def gesture():
    data = json.loads(request.data)
    x = data['x']
    y = data['y']
    action = json.loads(data['action'])

    if matrix != None:
        coords = convert_coords(matrix, x, y)
        Adapter.send_gesture(coords[0] * 222 / 275, coords[1], action)
        return jsonify(result='ok')
    else:
        return jsonify(result='cannot draw points'), 404


def grab_image(stream):
    b64 = stream
    data = base64.b64decode(b64)
    return data


#test

@app.route('/test/resolution')
def test_resolution():
    return jsonify(resolution=(295, 210))


@app.route('/test/gesture', methods=['POST'])
def test_gesture():
    print 'TEST NEW GESTURE'
    data = json.loads(request.form['data'])
    x = data['x']
    y = data['y']
    action = data['action']
    print 'x: %s  y: %s  action: %s' % (x, y, action)

    return jsonify(result='ok')


if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0')
