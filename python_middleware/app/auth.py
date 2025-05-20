from flask_httpauth import HTTPTokenAuth
from config import Config

auth = HTTPTokenAuth(scheme='Bearer')

valid_tokens = { Config.FLASK_API_KEY: 'compLeteClient' }

@auth.verify_token
def verify_token(token):
    return token in valid_tokens
