import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

testsData = pd.read_csv("C:\\Users\\Slint\\PycharmProjects\\MarioAutoEncoder\\sheet1.csv")

sns.set(style="darkgrid")

# Load an example dataset with long-form data
#fmri = sns.load_dataset("fmri")

# Plot the responses for different events and regions
sns.pairplot(data=testsData)

plt.show()