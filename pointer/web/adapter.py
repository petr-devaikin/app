import requests
from flask import current_app
import urlparse

class Adapter:
    @staticmethod
    def get_resolution():
        url = urlparse.urljoin(current_app.config['DRAWER_URL'], 'resolution')
        res = requests.get(url)
        return res

    @staticmethod
    def send_gesture(x, y, gesture):
        url = urlparse.urljoin(current_app.config['DRAWER_URL'], 'gesture')
        print url
        res = requests.post(url, data={ "x": x, "y": y, "gesture": gesture })

