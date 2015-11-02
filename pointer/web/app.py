from flask import Flask, jsonify, request

import base64

from adapter import Adapter
import json

app = Flask(__name__, instance_relative_config=True)
app.config.from_object('default_settings')
app.config.from_pyfile('application.cfg', silent=True)


if not app.config['DISABLE_CV']:
    from transform import transform
else:
    from test_transform import transform

@app.route('/')
def index():
    return 'Pointer v1.0'


@app.route('/gesture', methods=['POST'])
def gesture():
    img = grab_image(request.data)
    x = data['x']
    y = data['y']
    action = data['action']

    #print 'New gesture! Resolution: %s %s' % img.shape[:2]
    coords = transform(img, x, y)

    if coords != None:
        Adapter.send_gesture(coords[0], coords[1], action)
        return jsonify(result='ok')
    else:
        return jsonify(result='cannot find markers'), 404


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
