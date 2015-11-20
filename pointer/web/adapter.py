import requests
from flask import current_app
import urlparse
import json

import base64

class Adapter:
    @staticmethod
    def send_gesture(x, y, action):
        base_url = 'http://127.0.0.1:5000/test/' if current_app.config['TEST_DRAWER'] else current_app.config['DRAWER_URL']

        url = urlparse.urljoin(base_url, 'gesture')
        print "(%s %s): %s" % (x, y, action)
        data = {
            'x': x,
            'y': y,
            'action': action
        }
        res = requests.post(url, data={ 'data': json.dumps(data) })

    @staticmethod
    def send_background(image):
        base_url = 'http://127.0.0.1:5000/test/' if current_app.config['TEST_DRAWER'] else current_app.config['DRAWER_URL']

        url = urlparse.urljoin(base_url, 'gesture')
        data = {
            'x': 0,
            'y': 0,
            'action': {
                'type': 'background',
                'img': base64.b64encode(image)
            }
        }
        res = requests.post(url, data={ 'data': json.dumps(data) })

