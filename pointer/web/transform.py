from flask import current_app
from ar_markers.hamming.detect import detect_markers

import numpy as np
import cv2



def get_matrix(img_data):
    img = convert_image(img_data)
    dm = detect_markers(img)

    points = []
    point_ids = []
    for marker in dm:
        if marker.id in current_app.config['MARKERS'] and not marker.id in point_ids:
            #marker.highlite_marker(img)
            point_ids.append(marker.id)
            points.append({ 'id': marker.id, 'x': marker.center[0], 'y': marker.center[1] })

    #print 'Markers found: %s' % len(points)
    if len(points) > 2:
        #for p in points:
        #    print p['id']
        #print '------'
        return calc_transform(points)

    return None


def convert_coords(m, x, y):
    p = np.float32([x, y, 1])
    pt = p.dot(m.T)
    return (pt[0], pt[1])


def convert_image(data):
    image = np.asarray(bytearray(data), dtype="uint8")
    image = cv2.imdecode(image, cv2.IMREAD_COLOR)
    return image


def calc_transform(points):
    a = []
    b = []

    for point in points:
        marker = current_app.config['MARKERS'][point['id']]
        a.append([marker['x'], marker['y']])
        b.append([point['x'], point['y']])

    pts2 = np.float32(a)
    pts1 = np.float32(b)

    return cv2.getAffineTransform(pts1, pts2)
