import numpy as np
import cv2
import requests
import base64


if __name__ == '__main__':
    capture = cv2.VideoCapture(0)

    if capture.isOpened(): # try to get the first frame
        frame_captured, frame = capture.read()
    else:
        frame_captured = False
    while frame_captured:
        cv2.imshow('Test Frame', frame)
        r, img = cv2.imencode('.jpg', frame)
        b64 = base64.encodestring(img)
        requests.post('http://127.0.0.1:5000/gesture', data={ 'img': b64 })

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        frame_captured, frame = capture.read()

    # When everything done, release the capture
    capture.release()
    cv2.destroyAllWindows()
