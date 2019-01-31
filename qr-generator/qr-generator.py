import qrcode

address = "/home/summer/Documents/Python/qr-generator/"

qr_info = raw_input("Type the qr information: ")
qr_name = raw_input("Type the qr file name: ")

img = qrcode.make(qr_info)
#img.show()
img.save(address + qr_name + ".png")
