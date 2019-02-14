import os
from imutils import paths
import face_recognition
import pickle

from flask import Flask
from flask import jsonify

app = Flask(__name__)
ROOT_PATH = os.path.dirname(os.path.realpath(__file__))
ROOT_PATH = os.path.split(ROOT_PATH)[0]

def encode_faces():
    image_paths = list(paths.list_images(ROOT_PATH + '/dataset'))

    # initialize the list of known encodings and known names
    known_encodings = []
    known_names = []

    for (i, image_path) in enumerate(image_paths):
        # extract the person name from the image path
        print("[INFO] processing image {}/{}".format(i + 1, len(image_paths)))
        name = image_path.split(os.path.sep)[-2]

        # load the input image and convert it from RGB (OpenCV ordering)
        # to dlib ordering (RGB)
        image = cv2.imread(image_path)
        rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        # detect the (x, y)-coordinates of the bounding boxes
        # corresponding to each face in the input image
        boxes = face_recognition.face_locations(rgb, model='cnn')

        # compute the facial embedding for the face
        encodings = face_recognition.face_encodings(rgb, boxes)

        # loop over the encodings
        for encoding in encodings:
            # add each encoding + name to our set of known names and
            # encodings
            known_encodings.append(encoding)
            known_names.append(name)

    # dump the facial encodings + names to disk
    print("[INFO] serializing encodings...")
    data = {"encodings": known_encodings, "names": known_names}
    f = open(ROOT_PATH + '/encodings.pickle', "wb")
    f.write(pickle.dumps(data))
    f.close()

@app.route('/encode_faces', methods=['GET'])
def default():
    encode_faces()
    return jsonify({ 'ok': True })


if __name__ == '__main__':
    app.config['DEBUG'] = os.environ.get('ENV') == 'development'
    app.run(host='0.0.0.0', port=8081)