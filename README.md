# How To Install

requirements:
- Python 2.7
- PIP
- Virtualenv

1) Install PIP and Virtualenv
2) Go to the project directory (pointer)
3) Create new virtualenv dir:
> virtualenv venv
4) activate virtual environment:
> . /venv/bin/activate
(to deactivate virtualenv run command 'deactivate')
5) install dependencies:
python setup.py develop

# How to run test app

By default app is in a test mode (see TEST_DRAWER and DISABLE_CV in default_settings.py)
Command to run application:
> python web/app.py

Server will be ran at 127.0.0.1:3000

Server can receive POST request with JSON params:
{ data: {
        x: 123,
        y: 321,
        img: 'base64/...'   // - base64 encoded image
        action: {
            ...             // - description of the action: tap, swipe, text, whatever
        }
    }
}

Request will be forwarder to the drawing application with transformed coordinates. Action will be the same.
