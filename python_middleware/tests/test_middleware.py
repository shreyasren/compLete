import os
import pytest
from app.middleware import app

@pytest.fixture
def client():
    os.environ['FLASK_API_KEY'] = 'testtoken'
    app.config['TESTING'] = True
    with app.test_client() as c:
        yield c

def test_health(client):
    assert client.get('/health').status_code == 200

def test_missing_body(client):
    resp = client.post(
        '/complete_model', json={}, headers={'Authorization': 'Bearer testtoken'}
    )
    assert resp.status_code == 400
    assert "error" in resp.get_json()
