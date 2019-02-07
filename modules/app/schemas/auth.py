from jsonschema import validate
from jsonschema.exceptions import ValidationError
from jsonschema.exceptions import SchemaError

auth_schema = {
    "type": "object",
    "properties": {
        "rg_passaporte": {
            "type": "string",
            "minLength": 9
        }
    },
    "required": ["rg_passaporte"],
    "additionalProperties": False
}

def validate_auth(data):
    try:
        validate(data, auth_schema)
    except ValidationError as e:
        return {'ok': False, 'message': e}
    except SchemaError as e:
        return {'ok': False, 'message': e}
    return {'ok': True, 'data': data}