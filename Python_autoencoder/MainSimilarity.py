import socket
import os
import csv
from keras.layers import Input, Dense
from keras.models import Model
from keras import backend as K
from keras.models import load_model
import numpy as np
import matplotlib.pyplot as plt
import pickle
import pandas as pd
from scipy.spatial import distance
import io

def MsgIntoMarioState(msg):

    #values = msg.split(",")[
    states = pd.read_csv(io.BytesIO(msg))
    return states

def GetEmbedding(state, m):

    return m.predict(state)

def GetSimilarities(states, m):

    embeddings = GetEmbedding(states, m)
    return [distance.euclidean(embeddings[0], embeddings[n]) for n in range(1, len(embeddings))]

#s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#s.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
#
#s.bind(("localhost", 9000))
#
#s.listen(1)
#
#connection, address = s.accept()

server_address = ('localhost', 9000)
other_address = ('localhost', 9001)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(server_address)

model = load_model('marioStateEncoder.h5')

highestValue = 0
h1 = 0
h2 = 0

requests = 0
incomplete = False

while 1:

    message = sock.recv(4096)

    if message == "close":
        break

    #states = message.decode().split(";")

    marioStates = MsgIntoMarioState(message)
    #marioState2 = MsgIntoMarioState(states[1].encode())

    #WriteStateIntoCSVFile(marioState, "marioStates_test.csv")

    #similarities = GetSimilarities(marioStates, model)
    #similaritiesStr = ';'.join(map(str, similarities))

    #embedding = GetEmbedding(marioStates, model)
    #embeddingStr = "{0};{1};{2};{3}".format(embedding[:, 0][0], embedding[:, 1][0], embedding[:, 0][1], embedding[:, 1][1])

    #embeddings = GetEmbedding(marioStates, model)
    #
    #if(embeddings[0][0] > h1):
    #    h1 = embeddings[0][0]
    #    print(h1)

    #if(similarity > highestValue):
    #    highestValue = similarity
        #print("highest value = {0}".format(highestValue))

    #print("Got: {0}".format(message))
    sock.sendto("{0}\n".format(embeddingStr).encode(), other_address)

    #requests += 1
    #print(requests)