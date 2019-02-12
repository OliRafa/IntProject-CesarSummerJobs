
#!/bin/sh
echo "smart capivara script..."
python encode_faces.py --dataset dataset --encodings encodings.pickle
python image-recognition-2.py --encodings encodings.pickle
