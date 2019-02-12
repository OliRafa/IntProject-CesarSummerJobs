from imutils.video import VideoStream
from pyzbar import pyzbar
import face_recognition
import argparse
import imutils
import pickle
import time
import cv2

# READ CSV
import datetime
import pandas as pd

# ARDUINO SERIAL COMMUNICATION
import serial
import serial.tools.list_ports
import time


def get_index(info):
	pd.set_option('display.max_colwidth', -1)
	df = pd.read_csv('database/teste.csv')

	for i in range(len(df["Documento"])):
		try:
			a = int(info)
			b = int( df["Documento"].get(i) )
			if a == b:
				return i
		except:
			print("Invalid input")
	return -1

def get_data(i):
	pd.set_option('display.max_colwidth', -1)
	df = pd.read_csv('database/teste.csv')

	print("-----DADOS-----")
	for data in df:
		print(df[data].get(i))
 

def run_qr_scanner():

	# initialize the video stream and allow the camera sensor to warm up
	print("[INFO] starting video stream...")
	# vs = VideoStream(src=0).start()
	vs = VideoStream(src=0).start()
	time.sleep(2.0)
	 
	# open the output CSV file for writing and initialize the set of
	# barcodes found thus far
	csv = open("log/barcode.csv", "w")
	found = set()

	# loop over the frames from the video stream
	while True:
		# grab the frame from the threaded video stream and resize it to
		# have a maximum width of 400 pixels
		frame = vs.read()
		frame = imutils.resize(frame, width=400)
	 
		# find the barcodes in the frame and decode each of the barcodes
		barcodes = pyzbar.decode(frame)

		# loop over the detected barcodes
		for barcode in barcodes:
			# extract the bounding box location of the barcode and draw
			# the bounding box surrounding the barcode on the image
			(x, y, w, h) = barcode.rect
			cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
	 
			# the barcode data is a bytes object so if we want to draw it
			# on our output image we need to convert it to a string first
			barcodeData = barcode.data.decode("utf-8")
			barcodeType = barcode.type
	 
			# draw the barcode data and barcode type on the image
			#text = "{} ({})".format(barcodeData, barcodeType)
			#print(barcodeData)
		
			index = get_index(barcodeData)
			if index >= 0:
				print(get_data(index))
			else:
				print("Deu xablau!")

			#cv2.putText(frame, text, (x, y - 10),
				#cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
	 
			# if the barcode text is currently not in our CSV file, write
			# the timestamp + barcode to disk and update the set
			if barcodeData not in found:
				csv.write("{},{}\n".format(datetime.datetime.now(),
					barcodeData))
				csv.flush()
				found.add(barcodeData)

		# show the output frame
		cv2.imshow("Barcode Scanner", frame)
		key = cv2.waitKey(1) & 0xFF
	 
		# if the `q` key was pressed, break from the loop
		if key == ord("q"):
			break
	 
	# close the output CSV file do a bit of cleanup
	print("[INFO] cleaning up...")
	csv.close()
	cv2.destroyAllWindows()
	vs.stop()

# Function to send data to arduino
def send_to_gate(data):
	if(pySerial.isOpen()):
		#print("....serial open...\n")
		pySerial.write( data )

def run_face_recognition():
	# /SMART CAPIVARA SECTION

	# load the known faces and embeddings
	print("[INFO] loading encodings...")
	data = pickle.loads(open(args["encodings"], "rb").read())

	# initialize the video stream and pointer to output video file, then
	# allow the camera sensor to warm up
	print("[INFO] starting video stream...")
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
		boxes = face_recognition.face_locations(rgb,
			model=args["detection_method"])
		encodings = face_recognition.face_encodings(rgb, boxes)
		names = []


		# Check if there's someone in front of the camera
		if not boxes:
			#print("No boxes")
			send_to_gate("%")

		# loop over the facial embeddings
		for encoding in encodings:
			# attempt to match each face in the input image to our known
			# encodings
			matches = face_recognition.compare_faces(data["encodings"],
				encoding)
			name = "Unknown"

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
					name = data["names"][i]
					counts[name] = counts.get(name, 0) + 1

				# determine the recognized face with the largest number
				# of votes (note: in the event of an unlikely tie Python
				# will select first entry in the dictionary)
				name = max(counts, key=counts.get)
		
			# update the list of names
			names.append(name)

			# SMART CAPIVARA
			# Here starts the gambiarra
			if(len(names) > 1):
				if("Unknown" in names):
					send_to_gate("#")
				else:
					send_to_gate("@")
			else:
				if( name == "Unknown"):
					send_to_gate("#")
				else:
					send_to_gate("@")
			# /SMART CAPIVARA

		# loop over the recognized faces
		for ((top, right, bottom, left), name) in zip(boxes, names):
			# rescale the face coordinates
			top = int(top * r)
			right = int(right * r)
			bottom = int(bottom * r)
			left = int(left * r)

			# draw the predicted face name on the image
			if(name == "Unknown"):
				cv2.rectangle(frame, (left, top), (right, bottom),
					(0, 0, 255), 2)
				y = top - 15 if top - 15 > 15 else top + 15
				cv2.putText(frame, name, (left, y), cv2.FONT_HERSHEY_SIMPLEX,
					0.75, (0, 0, 255), 2)
			else:
				cv2.rectangle(frame, (left, top), (right, bottom),
					(0, 255, 0), 2)
				y = top - 15 if top - 15 > 15 else top + 15
				cv2.putText(frame, name, (left, y), cv2.FONT_HERSHEY_SIMPLEX,
					0.75, (0, 255, 0), 2)



		# if the video writer is None *AND* we are supposed to write
		# the output video to disk initialize the writer
		if writer is None and args["output"] is not None:
			fourcc = cv2.VideoWriter_fourcc(*"MJPG")
			writer = cv2.VideoWriter(args["output"], fourcc, 20,
				(frame.shape[1], frame.shape[0]), True)

		# if the writer is not None, write the frame with recognized
		# faces t odisk
		if writer is not None:
			writer.write(frame)

		# check to see if we are supposed to display the output frame to
		# the screen
		if args["display"] > 0:
			cv2.imshow("Frame", frame)
			key = cv2.waitKey(1) & 0xFF

			# if the `q` key was pressed, break from the loop
			if key == ord("q"):
				break

	# do a bit of cleanup
	cv2.destroyAllWindows()
	vs.stop()

	# check to see if the video writer point needs to be released
	if writer is not None:
		writer.release()


#------------MAIN SCRIPT------------------

# construct the argument parser and parse the arguments
ap = argparse.ArgumentParser()
#ap.add_argument("-o", "--output", type=str, default="barcodes.csv",
#	help="path to output CSV file containing barcodes")
#args = vars(ap.parse_args())

# construct the argument parser and parse the arguments
#ap = argparse.ArgumentParser()
ap.add_argument("-e", "--encodings", required=True,
	help="path to serialized db of facial encodings")
ap.add_argument("-o", "--output", type=str,
	help="path to output video")
ap.add_argument("-y", "--display", type=int, default=1,
	help="whether or not to display output frame to screen")
ap.add_argument("-d", "--detection-method", type=str, default="cnn",
	help="face detection model to use: either `hog` or `cnn`")
args = vars(ap.parse_args())


run_qr_scanner()
# Facial Recognition Parts
# Starts serial communication with arduino
pySerial = serial.Serial('/dev/ttyACM0',9600)
time.sleep(1) #time for arduino to reboot
run_face_recognition()