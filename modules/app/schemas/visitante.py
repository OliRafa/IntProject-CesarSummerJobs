from jsonschema import validate
from jsonschema.exceptions import ValidationError
from jsonschema.exceptions import SchemaError

visitante_schema = {
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
        "fone": {
            "type": "string",
        },
        "autorizante": {
            "type": "string",
        },
        "data_inicial": {
            "type": "string",
        },
        "data_final": {
            "type": "string",
        },
        "motivo": {
            "type": "string",
        }
    },
    "required": ["nome", "rg_passaporte", "autorizante", "data_inicial", "data_final", "motivo"],
    "additionalProperties": False
}

visitante_auth_schema = {
    "type": "object",
    "properties": {
        "rg_passaporte": {
            "type": "string",
            "minLength": 9
        }
    }
}

visitante_push_schema = {
    "type": "object",
    "properties": {
        "id_visitante": {
            "type": "string"
        },
        "id_autorizante": {
            "type": "string"
        },
        "validado": {
            "type": "bool"
        }
    },
    "required": ["id_visitante", "id_autorizante", "validado"],
    "additionalProperties": False
}


def validate_visitante(data):
    try:
        validate(data, visitante_schema)
    except ValidationError as e:
        return {'ok': False, 'message': e}
    except SchemaError as e:
        return {'ok': False, 'message': e}
    return {'ok': True, 'data': data}

def validate_visitante_auth(data):
    try:
        validate(data, visitante_auth_schema)
    except ValidationError as e:
        return {'ok': False, 'message': e}
    except SchemaError as e:
        return {'ok': False, 'message': e}
    return {'ok': True, 'data': data}

def validate_visitante_push(data):
    try:
        validate(data, visitante_push_schema)
    except ValidationError as e:
        return {'ok': False, 'message': e}
    except SchemaError as e:
        return {'ok': False, 'message': e}
    return {'ok': True, 'data': data}