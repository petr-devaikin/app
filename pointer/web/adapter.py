import requests
from flask import current_app
import urlparse
import json

class Adapter:
    @staticmethod
    def send_gesture(x, y, action):
        base_url = 'http://127.0.0.1:5000/test/' if current_app.config['TEST_DRAWER'] else current_app.config['DRAWER_URL']

        url = urlparse.urljoin(base_url, 'gesture')
        data = {
            'x': x,
            'y': y,
            'action': action
        }
        res = requests.post(url, data={ 'data': json.dumps(data) })

