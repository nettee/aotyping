#!/usr/bin/env python3

import csv

from mpl_toolkits.axes_grid1 import host_subplot
import mpl_toolkits.axisartist as AA
import matplotlib.pyplot as plt

import numpy as np

left = {}
right = {}

left['accelerate'] = {
  "avgTouchSizeList": [0.0150, 0.0118, 0.0142, 0.0131, 0.0191, 0.0137, 0.0113, 0.0124, 0.0150, 0.0144],
  "maxAccList": [1.75, 0.18, 0.00, 0.14, 0.31, 0.81, 0.60, 0.21, 0.39, 0.44],
  "word": "accelerate"
}

left['paranoid'] = {
  "avgTouchSizeList": [0.0179, 0.0098, 0.0157, 0.0103, 0.0165, 0.0201, 0.0183, 0.0150],
  "maxAccList": [2.10, 2.27, 0.29, 0.56, 0.33, 0.74, 0.82, 1.18],
  "word": "paranoid"
}

left['cotton'] = {
  "avgTouchSizeList": [0.0167, 0.0180, 0.0196, 0.0183, 0.0183, 0.0131],
  "maxAccList": [2.05, 0.89, 1.49, 0.40, 0.27, 2.45],
  "word": "cotton"
}

right['accelerate'] = {
  "avgTouchSizeList": [0.0153, 0.0196, 0.0172, 0.0157, 0.0165, 0.0163, 0.0153, 0.0190, 0.0137, 0.0170],
  "maxAccList": [6.76, 2.31, 0.67, 0.33, 2.49, 1.78, 0.43, 0.00, 1.03, 0.28],
  "word": "accelerate"
}

right['paranoid'] = {
  "avgTouchSizeList": [0.0161, 0.0186, 0.0150, 0.0221, 0.0152, 0.0170, 0.0157, 0.0196],
  "maxAccList": [0.70, 1.50, 1.17, 0.20, 3.11, 1.11, 0.32, 0.51],
  "word": "paranoid"
}

right['cotton'] = {
  "avgTouchSizeList": [0.0167, 0.0196, 0.0150, 0.0191, 0.0163, 0.0190],
  "maxAccList": [2.72, 1.37, 0.61, 0.00, 0.93, 0.19],
  "word": "cotton"
}

words = ['accelerate', 'paranoid', 'cotton']

def plot0(episodes, rewards, losses, test_episodes, test_rewards):

    reward_label = "Training reward"
    loss_label = "Mean training loss"
    test_reward_label = "Mean testing reward"

    reward_color = '#33A1C9'
    loss_color = '#FF6347'
    test_reward_color = '#B03060'

    host = host_subplot(111, axes_class=AA.Axes)
    plt.subplots_adjust(right=0.75)
    par1 = host.twinx()
    par2 = host.twinx()

    offset = 60
    new_fixed_axis = par2.get_grid_helper().new_fixed_axis
    par2.axis["right"] = new_fixed_axis(loc="right", axes=par2, offset=(offset, 0))
    par2.axis["right"].toggle(all=True)

    host.set_xlabel("Episode")
    host.set_ylabel(loss_label)
    par1.set_ylabel(reward_label)
    par2.set_ylabel(test_reward_label)

    host.plot(episodes, losses, color=loss_color, label=loss_label)
    par1.plot(episodes, rewards, color=reward_color, label=reward_label)
    par2.plot(test_episodes, test_reward_mean, color=test_reward_color, label=test_reward_label)
    host.legend(loc='upper left')

    host.set_xlim(np.min(episodes), np.max(episodes))
    host.set_ylim(0, np.max(losses) * 1.5)
    # par1.set_ylim(np.min(rewards), np.max(rewards))
    par1.set_ylim(-2000, 0)
    # par1.set_ylim(0, np.max(rewards) + 20)
    par2.set_ylim(-2000, 0)
    # par2.set_ylim(np.min(test_reward_mean), np.max(test_reward_mean))
    # par2.set_ylim(0, 20100)

    host.axis["left"].label.set_color(loss_color)
    par1.axis["right"].label.set_color(reward_color)
    par2.axis["right"].label.set_color(test_reward_color)

    plt.draw()
    plt.show()

def plot(filename, word, accList, touchSizeList):

    plt.cla()
    plt.clf()

    length = len(word)

    acc_label = 'Max acceleration'
    touch_label = 'Average touch size'

    touch_color = '#33A1C9'
    acc_color = '#FF6347'

    host = host_subplot(111, axes_class=AA.Axes)
    par1 = host.twinx()

    host.set_xlabel("Letter")
    host.set_ylabel(acc_label)
    par1.set_ylabel(touch_label)

    par1.bar(np.arange(0, length, 1), touchSizeList, width=0.5, color=touch_color, label=touch_label)
    host.plot(np.arange(0.5, length-1, 1), accList, color=acc_color, label=acc_label)
    plt.xticks(np.arange(0, length, 1), word)
    host.legend(loc='upper right')

    plt.draw()
    plt.savefig(filename)

def compare(word):

    for (name, data) in (('left', left[word]), ('right', right[word])):
        word = data['word']
        length = len(word)
        accList = data['maxAccList']
        accList.pop(0)
        touchSizeList = data['avgTouchSizeList']
        filename = '{}-{}.png'.format(name, word)
        plot(filename, word, np.array(accList), np.array(touchSizeList))

if __name__ == '__main__':

    for word in words:
        compare(word)

#    csvfile = open(args.file)
#    reader = csv.reader(csvfile, delimiter=',')
#    episodes, rewards, losses = zip(*reader)
#    episodes = np.array([int(v) for v in episodes])
#    rewards = np.array([float(v) for v in rewards])
#    losses = np.array([float(v) for v in losses])
#
#    csvfile2 = open(args.file2)
#    reader2 = csv.reader(csvfile2, delimiter=',')
#    test_episodes, test_reward_mean, test_reward_std = zip(*reader2)
#    test_episodes = np.array([int(v) for v in test_episodes])
#    test_reward_mean = np.array([float(v) for v in test_reward_mean])
#
#    plot(episodes, rewards, losses, test_episodes, test_reward_mean)
#
#    csvfile.close()
#    csvfile2.close()

