import time
import paho.mqtt.client as mqtt
import json


command_key = "command"
topic = "PTI"


def on_connect(client, userdata, flags, rc):
    print("Connected to the {0} topic".
          format(topic))
    subscribe_result = client.subscribe(topic)
    publish_result_1 = client.publish(
        topic=topic,
        payload="Listening to messages in the Paho Python Client")
    publish_result_2 = publish_command(
        client,
        topic,
        "print_temperature_fahrenheit",
        "temperature_fahrenheit",
        45)
    publish_result_3 = publish_command(
        client,
        topic,
        "print_information_message",
        "text",
        "Python IoT")


def on_message(client, userdata, msg):
    if msg.topic == topic:
        print("I've received the following message: {0}".format(str(msg.payload)))


def publish_command(client, topic, command_name, key, value):
    command_message = json.dumps({
        command_key: command_name,
        key: value})
    result = client.publish(topic=topic,
                            payload=command_message)
    return result


if __name__ == "__main__":
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(host="0.0.0.0",
                   port=1883,
                   keepalive=60)
    client.loop_forever()

"""
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    client.subscribe("PTI")
# Callback responável por receber uma mensagem publicada no tópico acima
def on_message(client, userdata, msg):
    print(msg.topic+" -  "+str(msg.payload))
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
#client.username_pw_set("USUARIO", password="SENHA")
client.connect("0.0.0.0", 1883, 60)
client.loop_forever()

client.subscribe("PTI")
#client.publish("PTI", "huehuehue")

# Publica
publish.single("PTI", "Oi, aqui é um teste", hostname="0.0.0.0:1883")
"""