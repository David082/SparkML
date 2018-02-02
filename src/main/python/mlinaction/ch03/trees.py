# -*- coding: utf-8 -*-
"""
describe : 
author : yu_wei
created on : 2018/1/17
version : 
"""
from math import log
import operator


def createDataSet():
    dataSet = [[1, 1, 'yes'],
               [1, 1, 'yes'],
               [1, 0, 'no'],
               [0, 1, 'no'],
               [0, 1, 'no']]
    labels = ['no surfacing', 'flippers']
    # change to discrete values
    return dataSet, labels


def calaShannonEnt(dataSet):
    """
    :return shannonEnt
    entropy：熵，信息的期望值
    熵越高，则混合的数据越多，信息量越大
    得到熵之后，按照获取最大信息增益的方法划分数据集/or Gini impurity
    """
    numEntries = len(dataSet)
    labelCounts = {}
    for featVec in dataSet:  # the the number of unique elements and their occurance
        currentLabel = featVec[-1]
        if currentLabel not in labelCounts.keys():
            labelCounts[currentLabel] = 0
        labelCounts[currentLabel] += 1
    shannonEnt = 0.0
    for key in labelCounts:
        prob = float(labelCounts[key]) / numEntries
        shannonEnt -= prob * log(prob, 2)  # log base 2
    return shannonEnt


def splitDataSet(dataSet, axis, value):
    retDataSet = []
    for featVec in dataSet:
        if featVec[axis] == value:
            reducedFeatVec = featVec[:axis]  # chop out axis used for splitting
            reducedFeatVec.extend(featVec[axis + 1:])
            retDataSet.append(reducedFeatVec)
    return retDataSet


if __name__ == "__main__":
    dataSet, labels = createDataSet()
    dataSet[0][-1] = 'maybe'
    print(dataSet)
    shannonEnt = calaShannonEnt(dataSet)
    print(shannonEnt)

