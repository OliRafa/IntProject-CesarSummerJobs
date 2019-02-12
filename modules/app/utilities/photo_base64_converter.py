import base64
from app import mongo
from gridfs import GridFS
from bson.objectid import ObjectId

def convert_photo_to_base64(_id):
    fs = GridFS(mongo.db)
    photo = fs.get(ObjectId(_id))

    return base64.encodebytes(photo.read()).decode('utf8')