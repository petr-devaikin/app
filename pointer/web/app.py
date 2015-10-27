from flask import Flask
import requests

app = Flask(__name__, instance_relative_config=True)
app.config.from_object('default_settings')
app.config.from_pyfile('application.cfg', silent=True)


@app.route('/')
def index():
    return 'Hello world'


if __name__ == '__main__':
    app.run()
