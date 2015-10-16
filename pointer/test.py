import numpy as np
import cv2

from ar_markers.hamming.detect import detect_markers

markers = {
    #2841: { 'x': 47, 'y': 28, 'size': 32 },
    1002: { 'x': 222, 'y': 28, 'size': 32 },
    3172: { 'x': 47, 'y': 148, 'size': 32 },
    2553: { 'x': 221, 'y': 148, 'size': 32 }
}

paper_size = (295, 210)

def cals_transform(points):
    a = []
    b = []

    point1 = points[0]
    marker1 = markers[point1['id']]
    point2 = points[1]
    marker2 = markers[point2['id']]
    point3 = points[2]
    marker3 = markers[point3['id']]

    a.append([marker1['x'], marker1['y']])
    b.append([point1['x'], point1['y']])

    a.append([marker2['x'], marker2['y']])
    b.append([point2['x'], point2['y']])

    a.append([marker3['x'], marker3['y']])
    b.append([point3['x'], point3['y']])

    pts2 = np.float32(a)
    pts1 = np.float32(b)

    return cv2.getAffineTransform(pts1, pts2)


if __name__ == '__main__':
    capture = cv2.VideoCapture(0)

    if capture.isOpened(): # try to get the first frame
        frame_captured, frame = capture.read()
    else:
        frame_captured = False
    while frame_captured:
        h, w = frame.shape[:2]

        dm = detect_markers(frame)

        top_left = { 'x': 0, 'y': 0 }
        bottom_right = { 'x': 0, 'y': 0 }

        points = []
        point_ids = []
        for marker in dm:
            if marker.id in markers and not marker.id in point_ids:
                marker.highlite_marker(frame)
                point_ids.append(marker.id)
                points.append({ 'id': marker.id, 'x': marker.center[0], 'y': marker.center[1] })

        if len(points) > 2:
            for p in points:
                print p['id']
            print '------'
            m = cals_transform(points)
            if m != None :
                #print m.cols
                #print cv2.transform(np.array([[0, 0, 0]]), m)
                frame = cv2.warpAffine(frame, m, paper_size)

        cv2.imshow('Test Frame', frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        frame_captured, frame = capture.read()

    # When everything done, release the capture
    capture.release()
    cv2.destroyAllWindows()
