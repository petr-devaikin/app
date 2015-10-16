import numpy as np
import cv2
import cv
from locator.MarkerTracker import *

cap = cv2.VideoCapture(0)

template = cv2.imread('template.png',0)
w, h = template.shape[::-1]

while(True):
    # Capture frame-by-frame
    ret, frame = cap.read()

    # Our operations on the frame come here
    height, width = frame.shape[:2]
    print height, width
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    small = cv2.resize(gray, (width / 2, height / 2))
    #ret, thresh = cv2.threshold(small, 127, 255, cv2.THRESH_BINARY)
    thresh = cv2.adaptiveThreshold(small, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, 9, 5)
    result = cv2.cvtColor(thresh, cv2.COLOR_GRAY2RGB)

    res = cv2.matchTemplate(thresh, template, cv2.TM_CCOEFF)

    min_val, max_val, min_loc, max_loc = cv2.minMaxLoc(res)
    threshold = max_val * 0.8
    loc = np.where(res >= threshold)
    for pt in zip(*loc[::-1]):
        cv2.rectangle(result, pt, (pt[0] + w, pt[1] + h), (0, 0, 255), 2)
    print min_val, max_val


    #template = cv2.imread('mario_coin.png',0)
    #w, h = template.shape[::-1]

    #res = cv2.matchTemplate(img_gray,template, cv2.TM_CCOEFF_NORMED)
    #threshold = 0.8
    #oc = np.where( res >= threshold)
    #for pt in zip(*loc[::-1]):
    #    cv2.rectangle(frame, pt, (pt[0] + w, pt[1] + h), (0,0,255), 2)

    # Display the resulting frame
    cv2.imshow('frame', result)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
