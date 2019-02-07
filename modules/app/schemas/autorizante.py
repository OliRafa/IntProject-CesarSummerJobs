from jsonschema import validate
from jsonschema.exceptions import ValidationError
from jsonschema.exceptions import SchemaError

autorizante_schema = {
    "type": "object",
    "properties": {
        "nome": {
            "type": "string",
        },
        "email": {
            "type": "string",
            "format": "email"
        },
        "rg_passaporte": {
            "type": "string",
            "minLength": 9
        },
        "matricula": {
            "type": "string",
        },
        "fone": {
            "type": "string",
        },
        "areas_acesso": {
            "type": "string",
        },
        "data_inicial": {
            "type": "string",
        },
        "data_final": {
            "type": "string",
        }
    },
    "required": ["nome", "rg_passaporte", "areas_acesso", "matricula", "fone"],
    "additionalProperties": False
}

autorizante_auth_schema = {
    "type": "object",
    "properties": {
        "rg_passaporte": {
            "type": "string",
            "minLength": 9
        }
    }
}

push_schema = {
    "type": "object",
    "properties": {
        "autorizante": {
            "type": "string"
        }
    },
    "required": ["autorizante"],
    "additionalProperties": False
}


def validate_autorizante(data):
    try:
        validate(data, autorizante_schema)
    except ValidationError as e:
        return {'ok': False, 'message': e}
    except SchemaError as e:
        return {'ok': False, 'message': e}
    return {'ok': True, 'data': data}

def validate_autorizante_auth(data):
    try:
        validate(data, autorizante_auth_schema)
    except ValidationError as e:
        return {'ok': False, 'message': e}
    except SchemaError as e:
        return {'ok': False, 'message': e}
    return {'ok': True, 'data': data}

def validate_push(data):
    try:
        validate(data, push_schema)
    except ValidationError as e:
        return {'ok': False, 'message': e}
    except SchemaError as e:
        return {'ok': False, 'message': e}
    return {'ok': True, 'data': data}