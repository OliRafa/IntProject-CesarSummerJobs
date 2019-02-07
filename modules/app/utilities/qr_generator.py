import qrcode
from io import BytesIO


def generate_qr_code(input):
    #return qrcode.make(input)
    qr = qrcode.QRCode(
    version=4,
    error_correction=qrcode.constants.ERROR_CORRECT_H,
    box_size=10,
    border=1
    )
    qr.add_data(input)
    img = qr.make_image()

    byte_io = BytesIO()
    img.save(byte_io, 'PNG')
    byte_io.seek(0)

    return byte_io

#address = "/home/summer/Documents/Python/qr-generator/"

#qr_info = raw_input("Type the qr information: ")
#qr_name = raw_input("Type the qr file name: ")

#img = qrcode.make(qr_info)
#img.show()
#img.save(address + qr_name + ".png")
