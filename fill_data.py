import requests

base_url = 'http://localhost:8080'
n = 5

auth=('user', 'password')
token = requests.post(base_url + '/auth/token', auth=auth).json()
headers = {'Authorization': f'Bearer {token}'}

for index in range(n):
    i = index + 1
    new_vehicle_req = {
        'brand': f'Brand-{i}',
        'model': f'Model-{i}',
        'manufacturer': f'Manufacturer-{i}',
        'manufactureDate': '2024-04-13T12:00:00',
        'maxSpeed': 200.0 + i * 10,
        'price': str(30000.00 + i * 2000),
        'isABS': i % 2 == 0,
        'battery': {
            'model': f'Bat-Model-{i}',
            'manufacturer': f'Bat-Manuf-{i}',
            'type': f'Bat-Type-{i}',
            'capacity': 1000 + 100 * i,
            'manufactureDate': '2024-03-02T01:00:59',
            'chargeTime': 15.0 + 1.5 * i,
            'isFastCharge': i % 2 == 0
        }
    }
    res = requests.post(base_url + '/vehicles', json=new_vehicle_req, headers=headers)
    assert res.json()
