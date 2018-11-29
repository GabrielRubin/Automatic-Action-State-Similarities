from keras.layers import Input, Dense
from keras.models import Model
from keras import backend as K
import numpy as np
import matplotlib.pyplot as plt
import pickle
import pandas as pd

# Deep Autoencoder

features_path = 'deep_autoe_features.pickle'
labels_path = 'deep_autoe_labels.pickle'

# this is the size of our encoded representations
encoding_dim = 2   # 32 floats -> compression factor 24.5, assuming the input is 784 floats
input_dim = 10

# this is our input placeholder; 784 = 28 x 28
input_img = Input(shape=(input_dim, ))

my_epochs = 400

# "encoded" is the encoded representation of the inputs
encoded = Dense(encoding_dim * 4, activation='relu')(input_img)
encoded = Dense(encoding_dim * 2, activation='relu')(encoded)
encoded = Dense(encoding_dim, activation='relu')(encoded)

# "decoded" is the lossy reconstruction of the input
decoded = Dense(encoding_dim * 2, activation='relu')(encoded)
decoded = Dense(encoding_dim * 4, activation='relu')(decoded)
decoded = Dense(input_dim, activation='sigmoid')(decoded)

# this model maps an input to its reconstruction
autoencoder = Model(input_img, decoded)

# Separate Encoder model

# this model maps an input to its encoded representation
encoder = Model(input_img, encoded)

# Separate Decoder model

# create a placeholder for an encoded (32-dimensional) input
encoded_input = Input(shape=(encoding_dim, ))
# retrieve the layers of the autoencoder model
decoder_layer1 = autoencoder.layers[-3]
decoder_layer2 = autoencoder.layers[-2]
decoder_layer3 = autoencoder.layers[-1]
# create the decoder model
decoder = Model(encoded_input, decoder_layer3(decoder_layer2(decoder_layer1(encoded_input))))

# Train to reconstruct MNIST digits

# configure model to use a per-pixel binary crossentropy loss, and the Adadelta optimizer
autoencoder.compile(optimizer='adadelta', loss='binary_crossentropy')

x_train = pd.read_csv("marioStates.csv").values
x_test  = pd.read_csv("marioStates_test.csv").values

# prepare input data
#(x_train, _), (x_test, y_test) = mnist.load_data()

# normalize all values between 0 and 1 and flatten the 28x28 images into vectors of size 784
#x_train = x_train.astype('float32') / 255.
#x_test = x_test.astype('float32') / 255.
#x_train = x_train.reshape((len(x_train), np.prod(x_train.shape[1:])))
#x_test = x_test.reshape((len(x_test), np.prod(x_test.shape[1:])))
print(x_train.shape)
#print(x_test.shape)

# Train autoencoder for 50 epochs

autoencoder.fit(x_train, x_train, epochs=my_epochs, batch_size=256, shuffle=True, validation_data=(x_test, x_test),
                verbose=2)

# after 100 epochs the autoencoder seems to reach a stable train/test lost value

# Visualize the reconstructed encoded representations

# encode and decode some digits
# note that we take them from the *test* set
encoded_imgs = encoder.predict(x_test)
decoded_imgs = decoder.predict(encoded_imgs)

encoder.save('marioStateEncoder.h5')

# save latent space features 32-d vector
#pickle.dump(encoded_imgs, open(features_path, 'wb'))
#pickle.dump(y_test, open(labels_path, 'wb'))

n = 10  # how many digits we will display
for i in range(n):
    print("original: {0} - decoded: {1}".format(x_test[i], decoded_imgs[i]))

K.clear_session()