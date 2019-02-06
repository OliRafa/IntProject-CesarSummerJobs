import os
from flask import request, jsonify
from flask_jwt_extended import (create_access_token, create_refresh_token,
                                jwt_required, jwt_refresh_token_required, get_jwt_identity)
from app import app, mongo, flask_bcrypt, jwt
from app.schemas import validate_visitante, validate_visitante_auth
from app.controllers import autorizante
from app.utilities import qr_generator
import logger

ROOT_PATH = os.environ.get('ROOT_PATH')
LOG = logger.get_root_logger(
    __name__, filename=os.path.join(ROOT_PATH, 'output.log'))


#@jwt.unauthorized_loader
#def unauthorized_response(callback):
#    return jsonify({
#        'ok': False,
#        'message': 'Missing Authorization Header'
#    }), 401
#
#
#@app.route('/auth', methods=['POST'])
#def auth_user():
#    ''' auth endpoint '''
#    data = validate_visitante_auth(request.get_json())
#    if data['ok']:
#        data = data['data']
#        user = mongo.db.visitantes.find_one({'rg_passaporte': data['rg_passaporte']}, {"_id": 0})
#        LOG.debug(user)
#        if user: #and flask_bcrypt.check_password_hash(user['password'], data['password']):
#            #del user['password']
#            access_token = create_access_token(identity=data)
#            refresh_token = create_refresh_token(identity=data)
#            user['token'] = access_token
#            user['refresh'] = refresh_token
#            return jsonify({'ok': True, 'data': user}), 200
#        else:
#            return jsonify({'ok': False, 'message': 'Usuario ou RG/Passaporte invalidos'}), 401
#    else:
#        return jsonify({'ok': False, 'message': 'Parametros invalidos: {}'.format(data['message'])}), 400


@app.route('/registro', methods=['POST'])
def registro():
    ''' register user endpoint '''
    data = validate_visitante(request.get_json())
    if data['ok']:
        data = data['data']
        data.update({ 'validado': False })
        #data['password'] = flask_bcrypt.generate_password_hash(
        #    data['password'])
        mongo.db.visitantes.insert_one(data)
        if autorizante.update_autorizante(data['autorizante'], data['_id']):
            return jsonify({'ok': True, 'message': 'Visitante registrado'}), 200
    else:
        return jsonify({'ok': False, 'message': 'Parametros invalidos: {}'.format(data['message'])}), 400


#@app.route('/refresh', methods=['POST'])
#@jwt_refresh_token_required
#def refresh():
#    ''' refresh token endpoint '''
#    current_user = get_jwt_identity()
#    ret = {
#        'token': create_access_token(identity=current_user)
#    }
#    return jsonify({'ok': True, 'data': ret}), 200


@app.route('/visitante', methods=['DELETE'])
#@jwt_required
def user():    
    data = request.get_json()
    if request.method == 'DELETE':
        if data.get('rg_passaporte', None) is not None:
            db_response = mongo.db.visitantes.delete_one({'rg_passaporte': data['rg_passaporte']})
            if db_response.deleted_count == 1:
                response = {'ok': True, 'message': 'Visitante deletado'}
            else:
                response = {'ok': True, 'message': 'Visitsnte não encontrado'}
            return jsonify(response), 200
        else:
            return jsonify({'ok': False, 'message': 'Parametros invalidos'}), 400

@app.route('/visitante', methods=['GET'])
def get_visitante():
    query = request.args
    data = mongo.db.visitantes.find_one(query, {"_id": 0})
    return jsonify({'ok': True, 'data': data}), 200 

@app.route('/visitante', methods=['PUT'])
def update_visitante():
    data = request.get_json()
    visitante = mongo.db.visitantes.find_one({ '_id': data['_id'] })
    if visitante:
        mongo.db.visitantes.update_one({ '_id': visitante['_id'] }, { '$set': { 'validado': data['validado'] } })
        #qr_code = qr_generator.generate_qr_code(mongo.db.visitantes.find_one({ '_id': data['_id'] }))
        return jsonify({'ok': True, 'message': 'Visitante atualizado'}), 200
        #return send_file(qr_code, mimetype='image/jpg'), 200
    else:
        return jsonify({'ok': False, 'message': 'Parametros invalidos'}), 400