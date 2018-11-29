# libraries and data
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

#AgentType
#Episodes
#MeanReward

# Make a data frame
testsData = pd.read_csv("C:\\Users\\Slint\\PycharmProjects\\MarioAutoEncoder\\sheet2.csv")

columnForSheet1 = {
 "Euclidian buffer = 100" : r'Euclidian Distance, $C_{b}$ = 100',
 "Euclidian buffer = 50"  : r'Euclidian Distance, $C_{b}$ = 50',
 "Euclidian buffer = 25"  : r'Euclidian Distance, $C_{b}$ = 25',
 "Q-Value"                : "Q-Learning"
}

columnForSheet2 = {
 "Manhattan Distance" : "Manhattan Distance",
 "Euclidian Distance" : "Euclidian Distance",
 "Cosine Similarity"  : "Cosine Similarity",
 "Q-Value"            : "Q-Learning"
}

# Initialize the figure
plt.style.use('seaborn-darkgrid')

# create a color palette
palette = plt.get_cmap('Set1')

# multiple line plot
num = 0
for column in testsData.drop('Episodes', axis=1):
    num += 1

    # Find the right spot on the plot
    plt.subplot(2, 2, num)

    # plot every groups, but discreet
    for v in testsData.drop('Episodes', axis=1):
        plt.plot(testsData['Episodes'], testsData[v], marker='', color='grey', linewidth=0.6, alpha=0.45)

    # Plot the lineplot
    plt.plot(testsData['Episodes'], testsData[column], marker='', color=palette(num), linewidth=1.2, alpha=0.9, label=column)

    # Same limits for everybody!
    #plt.xlim(0, 10)
    #plt.ylim(-2, 22)

    # Not ticks everywhere
    #if num in range(7):
    #    plt.tick_params(labelbottom='off')
    #if num not in [1, 4, 7]:
    #    plt.tick_params(labelleft='off')

    # Add title
    plt.title(columnForSheet2[column], loc='left', fontsize=12, fontweight=0, color=palette(num))

# general title
plt.suptitle(r'Different distance measures x Q-Learning', fontsize=14, fontweight=0, color='black',
             style='italic')

# Axis title
plt.text(-3000, -1150, 'Episodes', ha='center', va='center', fontsize=14)
plt.text(-32000, 2000, 'Mean Reward', ha='center', va='center', rotation='vertical', fontsize=14)

plt.show()
