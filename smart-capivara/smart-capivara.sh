
#!/bin/sh
echo "smart capivara script..."
python encode_faces.py --dataset dataset --encodings encodings.pickle
python image-recognition.py --encodings encodings.pickle