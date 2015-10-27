import numpy as np
import cv2
import requests
import base64
import json


if __name__ == '__main__':
    capture = cv2.VideoCapture(0)

    if capture.isOpened(): # try to get the first frame
        frame_captured, frame = capture.read()
    else:
        frame_captured = False
    while frame_captured:
        x = frame.shape[:2][1] / 2
        y = frame.shape[:2][0] / 2

        cv2.circle(frame, (x, y), 5, color=(0, 255, 0), thickness=5)

        cv2.imshow('Test Frame', frame)
        r, img = cv2.imencode('.jpg', frame)
        b64 = base64.encodestring(img)

        data = {
            'x': x,
            'y': y,
            'img': b64,
            'action': {
                'type': 'tap'
            }
        }

        requests.post('http://127.0.0.1:5000/gesture', data={ 'data': json.dumps(data) })

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        frame_captured, frame = capture.read()

    # When everything done, release the capture
    capture.release()
    cv2.destroyAllWindows()
