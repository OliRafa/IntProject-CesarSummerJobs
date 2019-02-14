import requests
import json

API_ENDPOINT = "http://localhost:8080/qr_code"

def get_data(_hash):
    r = requests.post(url = API_ENDPOINT, json={ 'hash': _hash })
    if r.status_code is 200:
        return r.json()['_id']
    return None