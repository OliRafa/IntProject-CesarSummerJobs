import os
from flask import request, jsonify, send_file
from flask_jwt_extended import (create_access_token, create_refresh_token,
                    jwt_required, jwt_refresh_token_required, get_jwt_identity)
from bson.objectid import ObjectId
import hashlib
from app import app, mongo, flask_bcrypt, jwt
from app.schemas import validate_visitante, validate_visitante_auth
from app.controllers import autorizante
from app.utilities import generate_qr_code
import logger
from gridfs import GridFS

ROOT_PATH = os.environ.get('ROOT_PATH')
LOG = logger.get_root_logger(
    __name__, filename=os.path.join(ROOT_PATH, 'output.log'))

@app.route('/registro', methods=['POST'])
def registro():
    data = validate_visitante(request.get_json())
    if data['ok']:
        data = data['data']
        data.update({ 'validado': False })
        mongo.db.visitantes.insert_one(data)
        if autorizante.update_autorizante(data['autorizante'], data['_id']):
            return jsonify({'ok': True, 'message': 'Visitante registrado'}), 200
    else:
        return jsonify({'ok': False, 'message': 'Parametros invalidos: {}'
                                        .format(data['message'])}), 400


@app.route('/visitante', methods=['DELETE'])
def user():    
    data = request.get_json()
    if request.method == 'DELETE':
        if data.get('rg_passaporte', None) is not None:
            db_response = mongo.db.visitantes.delete_one(
                {'rg_passaporte': data['rg_passaporte']})
            if db_response.deleted_count == 1:
                response = {'ok': True, 'message': 'Visitante deletado'}
            else:
                response = {'ok': True, 'message': 'Visitante não encontrado'}
            return jsonify(response), 200
        else:
            return jsonify({'ok': False, 'message': 'Parametros invalidos'}), 400

@app.route('/visitante/<string:_id>', methods=['GET'])
def get_status_visitante(_id):
    data = mongo.db.visitantes.find_one({ '_id': ObjectId(_id )})
    fs = GridFS(mongo.db)
    qr_code = fs.get(ObjectId(data['qr_code_id']))
    return send_file(qr_code, mimetype='image/png'), 200

@app.route('/visitante', methods=['PUT'])
def update_visitante():
    data = request.get_json()
    visitante = mongo.db.visitantes.find_one({ '_id': ObjectId(data['id_visitante']) })
    if visitante:
        query = dict()
        if data['validado']:
            hash = hashlib.sha256(str(visitante['rg_passaporte'] +
                visitante['data_inicial'] + visitante['data_final'])
                .encode('utf-8')).hexdigest()
            query.update({ 'validado': data['validado'] })
            query.update({ 'hash': hash }) 
        else:
            query.update({ 'validado': data['validado'] })
            query.update({ 'hash': 0 })
        qr_code = generate_qr_code(hash)

        fs = GridFS(mongo.db)
        qr_id = fs.put(qr_code)
        query.update({ 'qr_code_id': qr_id })

        mongo.db.visitantes.update_one({ '_id': visitante['_id'] },
                { '$set': query })
        mongo.db.autorizantes.find_one_and_update({ '_id': ObjectId(data['id_autorizante']) }, { '$pull': { 'validacoes': ObjectId(visitante['_id']) } })
        
        return jsonify({'ok': True, 'message': 'Visitante autorizado'}), 200
    else:
        return jsonify({'ok': False, 'message': 'Parametros invalidos'}), 400