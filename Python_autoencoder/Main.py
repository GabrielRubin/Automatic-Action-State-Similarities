import socket
import os
import csv


def MsgIntoMarioState(msg):

    values = msg.decode().split(",")
    return values

def WriteStateIntoCSVFile(marioState, filePath):

    mode = ""
    if os.path.isfile(filePath):
        mode = "a"
    else:
        mode = "w"

    with open(filePath, mode) as csvFile:
        writer = csv.writer(csvFile)
        writer.writerow(marioState)

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)

s.bind(("localhost", 9000))

s.listen(10)

connection, address = s.accept()

while 1:

    #print("waiting...")
    message = connection.recv(1024)

    if message == "close":
        break

    marioState = MsgIntoMarioState(message)

    #WriteStateIntoCSVFile(marioState, "marioStates_test.csv")

    #print("Got: {0}".format(message))
    connection.send("Message received\n".encode())