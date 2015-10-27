import requests
from flask import current_app
import urlparse
import json

class Adapter:
    @staticmethod
    def get_resolution():
        url = urlparse.urljoin(current_app.config['DRAWER_URL'], 'resolution')
        res = requests.get(url)
        return res

    @staticmethod
    def send_gesture(x, y, action):
        url = urlparse.urljoin(current_app.config['DRAWER_URL'], 'gesture')
        print url
        data = {
            'x': x,
            'y': y,
            'action': action
        }
        res = requests.post(url, data={ 'data': json.dumps(data) })

