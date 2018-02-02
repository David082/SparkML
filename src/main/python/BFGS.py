# -*- coding: utf-8 -*-
"""
describe : 
author : yu_wei
created on : 2018/1/18
version :
refer :
-- 1.  BFGS算法
http://blog.csdn.net/acdreamers/article/details/44664941
"""
from scipy.optimize import fmin, fmin_bfgs


def func(x):
    return x ** 2 - 4 * x + 8


def FMIN():
    x = [1.0]
    ans = fmin(func, x)
    print(ans)


def BFGS():
    x = [1.0]
    ans = fmin_bfgs(func, x)
    print(ans)


if __name__ == "__main__":
    FMIN()
    BFGS()
