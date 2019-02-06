import os
from flask import request, jsonify
from flask_jwt_extended import (create_access_token, create_refresh_token,
                                jwt_required, jwt_refresh_token_required, get_jwt_identity)
from app import app, mongo, flask_bcrypt, jwt
from app.schemas import validate_autorizante, validate_autorizante_auth
import logger

ROOT_PATH = os.environ.get('ROOT_PATH')
LOG = logger.get_root_logger(
    __name__, filename=os.path.join(ROOT_PATH, 'output.log'))

@app.route('/register_autorizante', methods=['POST'])
def register_autorizante():
    ''' register user endpoint '''
    data = validate_autorizante(request.get_json())
    if data['ok']:
        data = data['data']
        #data['password'] = flask_bcrypt.generate_password_hash(
        #    data['password'])
        mongo.db.autorizantes.insert_one(data)
        return jsonify({'ok': True, 'message': 'Autorizante registrado'}), 200
    else:
        return jsonify({'ok': False, 'message': 'Parametros invalidos: {}'.format(data['message'])}), 400

@app.route('/autorizante', methods=['DELETE', 'PATCH'])
#@jwt_required
def autorizante():

    data = request.get_json()
    if request.method == 'DELETE':
        if data.get('rg_passport', None) is not None:
            db_response = mongo.db.autorizantes.delete_one({'rg_passport': data['rg_passport']})
            if db_response.deleted_count == 1:
                response = {'ok': True, 'message': 'Autorizante deletado'}
            else:
                response = {'ok': True, 'message': 'Autorizante não encontrado'}
            return jsonify(response), 200
        else:
            return jsonify({'ok': False, 'message': 'Parametros invalidos'}), 400

    if request.method == 'PATCH':
        if data.get('query', {}) != {}:
            mongo.db.autorizantes.update_one(
                data['query'], {'$set': data.get('payload', {})})
            return jsonify({'ok': True, 'message': 'Autorizante atualizado'}), 200
        else:
            return jsonify({'ok': False, 'message': 'Parametros invalidos'}), 400

@app.route('/autorizante', methods=['GET'])
def get_autorizantes():
    data = mongo.db.autorizantes.find()
    autorizantes = list()
    for autorizante in data[:]:
        autorizantes.append(autorizante['nome'])
    return jsonify({'ok': True, 'data': autorizantes}), 200

def update_autorizante(autorizante, _id):
    _autorizante = mongo.db.autorizantes.find_one({ 'nome': autorizante })
    if _autorizante:
        mongo.db.autorizantes.update_one({ '_id': _autorizante['_id'] }, { '$push': { 'validacoes': _id } })
        return True
    return False

@app.route('/push', methods=['POST'])
def push_autorizante():
    data = request.get_json()
    _autorizante = mongo.db.autorizantes.find_one({ 'nome': data['autorizante'] })
    #print(type(_autorizante['validacoes']))
    #print(type(_autorizante))
    if 'validacoes' in _autorizante:
        val = list()
        for validacao in _autorizante['validacoes']:
            val.append(mongo.db.visitantes.find_one({ '_id': validacao }))
        val.append({ 'quantidade_validacoes': len(_autorizante['validacoes']) })
        return jsonify(val), 200
    else:
        return jsonify({ 'quantidade_validacoes': 0 }), 200

