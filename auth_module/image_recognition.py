#!/usr/bin/python
# -*- coding: utf-8 -*-
from imutils.video import VideoStream
from pyzbar import pyzbar
import face_recognition
import argparse
import imutils
import pickle
import time
import cv2
import datetime

# CSV FILE
#import 

# OPERATIONAL SYSTEM

import os

# ARDUINO SERIAL COMMUNICATION

import serial
import serial.tools.list_ports
import time

import requests

ROOT_PATH = os.path.dirname(os.path.realpath(__file__))
ROOT_PATH = os.path.split(ROOT_PATH)[0]
ENCODINGS_PATH = ROOT_PATH + "/encodings.pickle"

API_ENDPOINT = 'http://179.106.208.155:8080'


def get_data_by_hash(_hash):
    print "Trying to get data"
    r = requests.post(url=API_ENDPOINT+'/qr_code', json={'hash': _hash})
    if r.status_code is 200:
        return r.json()['_id']
    return None


def get_info_by_id(_id):
    print "Trying to get info by id"
    r = requests.get(API_ENDPOINT+'/visitante', params={'_id': _id})
    if r.status_code is 200:
        print r.json()['nome']
        return r.json()['nome']
    return None


def run_qr_scanner():

    # initialize the video stream and allow the camera sensor to warm up

    print '[INFO] starting video stream...'
    vs = VideoStream(src=0).start()
    time.sleep(2.0)

    # open the output CSV file for writing and initialize the set of
    # barcodes found thus far

    csv = open('log/barcode.csv', 'w')
    found = set()

    # loop over the frames from the video stream

    while True:
        print "Waiting ofr QR Code"
    

        # grab the frame from the threaded video stream and resize it to
        # have a maximum width of 400 pixels

        frame = vs.read()
        frame = imutils.resize(frame, width=750)

        # find the barcodes in the frame and decode each of the barcodes

        barcodes = pyzbar.decode(frame)

        # loop over the detected barcodes

        get_out_qr = False
        for barcode in barcodes:

            # extract the bounding box location of the barcode and draw
            # the bounding box surrounding the barcode on the image

            (x, y, w, h) = barcode.rect
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 0xFF, 0),
                          2)

            # the barcode data is a bytes object so if we want to draw it
            # on our output image we need to convert it to a string first

            barcodeData = barcode.data.decode('utf-8')
            print "Barcode Data: " + barcodeData

            # barcodeType = barcode.type

            # draw the barcode data and barcode type on the image

            hash_code = get_data_by_hash(barcodeData)
            if hash_code is None:
                print 'Nao existe'
            else:
                print 'ID: ' + hash_code
                get_out_qr = True

            # if the barcode text is currently not in our CSV file, write
            # the timestamp + barcode to disk and update the set

            if barcodeData not in found:
                csv.write('{},{}\n'.format(datetime.datetime.now(),
                          barcodeData))
                csv.flush()
                found.add(barcodeData)

        # show the output frame

        cv2.imshow('QRCode Scanner', frame)
        key = cv2.waitKey(1) & 0xFF

        # if the `q` key was pressed, break from the loop

        if key == ord('q') or get_out_qr == True:
            break

    # close the output CSV file do a bit of cleanup

    print '[INFO] cleaning up...'
    csv.close()
    cv2.destroyAllWindows()
    vs.stop()

    return hash_code


# Function to send data to arduino

def send_to_gate(data):
    if pySerial.isOpen():
        pySerial.write(data)


def run_face_recognition(id_usuario):

    # /SMART CAPIVARA SECTION

    # load the known faces and embeddings

    data = pickle.loads(open(ENCODINGS_PATH, 'rb').read())

    # initialize the video stream and pointer to output video file, then
    # allow the camera sensor to warm up

    print '[INFO] starting video stream...'
    vs = VideoStream(src=0).start()
    writer = None
    time.sleep(2.0)

    # loop over frames from the video file stream

    while True:

        # grab the frame from the threaded video stream

        frame = vs.read()

        # convert the input frame from BGR to RGB then resize it to have
        # a width of 750px (to speedup processing)

        rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        rgb = imutils.resize(frame, width=750)
        r = frame.shape[1] / float(rgb.shape[1])

        # detect the (x, y)-coordinates of the bounding boxes
        # corresponding to each face in the input frame, then compute
        # the facial embeddings for each face

        boxes = face_recognition.face_locations(rgb, model='cnn')
        encodings = face_recognition.face_encodings(rgb, boxes)
        names = []
        names_window = []

        # Check if there's someone in front of the camera

        if not boxes:

            # print("No boxes")

            send_to_gate('%')

        # loop over the facial embeddings

        for encoding in encodings:

            # attempt to match each face in the input image to our known
            # encodings

            matches = face_recognition.compare_faces(data['encodings'],
                    encoding)
            name = 'Unknown'
            name_window = 'Unknown'

            # check to see if we have found a match

            if True in matches:

                # find the indexes of all matched faces then initialize a
                # dictionary to count the total number of times each face
                # was matched

                matchedIdxs = [i for (i, b) in enumerate(matches) if b]
                counts = {}

                # loop over the matched indexes and maintain a count for
                # each recognized face face

                for i in matchedIdxs:
                    name = data['names'][i]
                    counts[name] = counts.get(name, 0) + 1

                # determine the recognized face with the largest number
                # of votes (note: in the event of an unlikely tie Python
                # will select first entry in the dictionary)

                name = max(counts, key=counts.get)

                if name == id_usuario:
                    print 'Dupla verificacao'
                    
                name_window = get_info_by_id(id_usuario)
                print "NOME: " + name_window + " ID: " + id_usuario

            # update the list of names

            names.append(name)
            names_window.append(name_window)

            # SMART CAPIVARA
            # Here starts the gambiarra

            if len(names) > 1:
                if 'Unknown' in names:
                    send_to_gate('#')
                else:
                    send_to_gate('@')
            else:
                if name == 'Unknown':
                    send_to_gate('#')
                else:
                    send_to_gate('@')

            # /SMART CAPIVARA

        # loop over the recognized faces

        for ((top, right, bottom, left), name_window) in zip(boxes, names_window):

            # rescale the face coordinates

            top = int(top * r)
            right = int(right * r)
            bottom = int(bottom * r)
            left = int(left * r)

            # draw the predicted face name on the image

            if name_window == 'Unknown':
                cv2.rectangle(frame, (left, top), (right, bottom), (0,
                              0, 0xFF), 2)
                y = (top - 15 if top - 15 > 15 else top + 15)
                cv2.putText(
                    frame,
                    name_window,
                    (left, y),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.75,
                    (0, 0, 0xFF),
                    2,
                    )
            else:
                cv2.rectangle(frame, (left, top), (right, bottom), (0,
                              0xFF, 0), 2)
                y = (top - 15 if top - 15 > 15 else top + 15)
                cv2.putText(
                    frame,
                    name_window,
                    (left, y),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.75,
                    (0, 0xFF, 0),
                    2,
                    )

        # if the video writer is None *AND* we are supposed to write
        # the output video to disk initialize the writer

        if writer is None and args['output'] is not None:
            fourcc = cv2.VideoWriter_fourcc(*'MJPG')
            writer = cv2.VideoWriter(args['output'], fourcc, 20,
                    (frame.shape[1], frame.shape[0]), True)

        # if the writer is not None, write the frame with recognized
        # faces t odisk

        if writer is not None:
            writer.write(frame)

        # check to see if we are supposed to display the output frame to
        # the screen

        if args['display'] > 0:
            cv2.imshow('Face Detection', frame)
            key = cv2.waitKey(1) & 0xFF

            # if the `q` key was pressed, break from the loop

            if key == ord('q'):
                break

    # do a bit of cleanup

    cv2.destroyAllWindows()
    vs.stop()

    # check to see if the video writer point needs to be released

    if writer is not None:
        writer.release()


# ------------MAIN SCRIPT------------------

# construct the argument parser and parse the arguments

ap = argparse.ArgumentParser()

# ap.add_argument("-o", "--output", type=str, default="barcodes.csv",
# ....help="path to output CSV file containing barcodes")
# args = vars(ap.parse_args())

# construct the argument parser and parse the arguments
# ap = argparse.ArgumentParser()

ap.add_argument('-e', '--encodings', required=False,
                help='path to serialized db of facial encodings')
ap.add_argument('-o', '--output', type=str, help='path to output video')
ap.add_argument('-y', '--display', type=int, default=1,
                help='whether or not to display output frame to screen')
args = vars(ap.parse_args())

id_usuario = run_qr_scanner()

# Facial Recognition Parts
# Starts serial communication with arduino

pySerial = serial.Serial('/dev/ttyACM0', 9600)
time.sleep(1)  # time for arduino to reboot
run_face_recognition(id_usuario)

