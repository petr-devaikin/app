from flask import Flask, jsonify, request

import numpy as np
import cv2
from ar_markers.hamming.detect import detect_markers
import base64

app = Flask(__name__, instance_relative_config=True)
app.config.from_object('default_settings')
app.config.from_pyfile('application.cfg', silent=True)


@app.route('/')
def index():
    return 'Pointer v1.0'


@app.route('/gesture', methods=['POST'])
def gesture():
    print 'New Gesture!'
    img = grab_image(request.form['img'])
    print 'Resolution: %s %s' % img.shape[:2]

    dm = detect_markers(img)

    points = []
    point_ids = []
    for marker in dm:
        if marker.id in app.config['MARKERS'] and not marker.id in point_ids:
            #marker.highlite_marker(img)
            point_ids.append(marker.id)
            points.append({ 'id': marker.id, 'x': marker.center[0], 'y': marker.center[1] })

    print 'Markers found: %s' % len(points)
    if len(points) > 2:
        for p in points:
            print p['id']
        print '------'
        m = cals_transform(points)
        if m != None :
            print 'Transformed'
            #print m.cols
            #print cv2.transform(np.array([[0, 0, 0]]), m)
            #frame = cv2.warpAffine(frame, m, paper_size)

    return jsonify(result='ok')


def grab_image(stream):
    b64 = stream
    data = base64.b64decode(b64)
    #data = stream.read()
    image = np.asarray(bytearray(data), dtype="uint8")
    image = cv2.imdecode(image, cv2.IMREAD_COLOR)
    return image


def cals_transform(points):
    a = []
    b = []

    point1 = points[0]
    marker1 = app.config['MARKERS'][point1['id']]
    point2 = points[1]
    marker2 = app.config['MARKERS'][point2['id']]
    point3 = points[2]
    marker3 = app.config['MARKERS'][point3['id']]

    a.append([marker1['x'], marker1['y']])
    b.append([point1['x'], point1['y']])

    a.append([marker2['x'], marker2['y']])
    b.append([point2['x'], point2['y']])

    a.append([marker3['x'], marker3['y']])
    b.append([point3['x'], point3['y']])

    pts2 = np.float32(a)
    pts1 = np.float32(b)

    return cv2.getAffineTransform(pts1, pts2)

#test

@app.route('/test/resolution')
def test_resolution():
    return jsonify(resolution=(295, 210))


@app.route('/test/gesture', methods=['POST'])
def test_gesture():
    return jsonify(result='ok')


if __name__ == '__main__':
    app.run()
