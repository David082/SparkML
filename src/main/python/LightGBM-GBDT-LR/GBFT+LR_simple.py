# -*- coding: utf-8 -*-
"""
describe : 
author : yu_wei
created on : 2018/1/12
version :
refer :
-- LightGBM-GBDT-LR
https://github.com/neal668/LightGBM-GBDT-LR/blob/master/GBFT%2BLR_simple.py
question: LgithGBM install
"""
from __future__ import division
import json
import lightgbm as lgb
import pandas as pd
import numpy as np
from sklearn.metrics import mean_squared_error
from sklearn.linear_model import LogisticRegression

# load or create your dataset
print('Load data...')

