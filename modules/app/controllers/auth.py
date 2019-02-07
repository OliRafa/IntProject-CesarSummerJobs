import os
from flask import request, jsonify 
from flask_jwt_extended import (create_access_token, create_refresh_token,
                    jwt_required, jwt_refresh_token_required, get_jwt_identity)
from app import app, mongo, flask_bcrypt, jwt
from app.schemas import validate_auth
import logger

ROOT_PATH = os.environ.get('ROOT_PATH')
LOG = logger.get_root_logger(
    __name__, filename=os.path.join(ROOT_PATH, 'output.log'))

@app.route('/auth', methods=['POST'])
def auth_user():
    data = validate_auth(request.get_json())
    if data['ok']:
        data = data['data']
        usuario = mongo.db.visitantes.find_one(
            { 'rg_passaporte': data['rg_passaporte'] })
        if usuario:
            return jsonify({ 'ok': True,'tipo_usuario': 'visitante',
                'data': usuario }), 200
        else:
            usuario = mongo.db.autorizantes.find_one(
                { 'rg_passaporte': data['rg_passaporte'] })
            if usuario:
                return jsonify({ 'ok': True,'tipo_usuario': 'autorizante',
                    'data': usuario }), 200
            else:
                return jsonify({ 'ok': False, 'data': 'Usuário não encontrado'}), 401
        LOG.debug(usuario)
    return jsonify({'ok': False, 'message': 'Parametros invalidos: {}'
                .format(data['message'])}), 400