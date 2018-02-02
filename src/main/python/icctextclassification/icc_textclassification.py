# -*- coding: utf-8 -*-
"""
describe : 基于多项式贝叶斯的增量学习的文本分类
author : yu_wei
created on : 2018/1/12
version :
refer :
https://mp.weixin.qq.com/s?__biz=MzI0OTQyNzEzMQ==&mid=2247485899&idx=1&sn=b8ce9adaf0685375b0aeb93af3dce416&chksm=e990ec1fdee7650966693666088edfd6196b2fe5ef8adf9a8cffd4878aaabc9198334f5e4236&mpshare=1&scene=1&srcid=0105NezfIEEBQot7vAjEqhqY&pass_ticket=0NNEqiUqQr9qwCacogiqB2HP893ghR5ZfE%2FPByHmFe0Qz2Jxgp2xLWc4W0JgO%2F4L#rd

-- 1.unichr
https://stackoverflow.com/questions/2352018/cant-use-unichr-in-python-3-1
"""
import re
import tarfile  # tar压缩包出库
import os
import numpy as np
from bs4 import BeautifulSoup
from sklearn.feature_extraction.text import HashingVectorizer  # 文本转稀疏矩阵
from sklearn.naive_bayes import MultinomialNB  # 贝叶斯分类器
from sklearn.metrics import accuracy_score  # 分类评估指标


def str_convert(content):
    new_str = ''  # 新字符串
    for each_char in content:  # 循环读取每个字符
        code_num = ord(each_char)  # 读取字符的ASCII值或Unicode值
        if code_num == 12288:  # 全角空格直接转换
            code_num = 32
        elif (code_num >= 65281 and code_num <= 65374):  # 全角字符（除空格）根据关系转化
            code_num -= 65248
        new_str += chr(code_num)
    return new_str


def data_parse(data):
    raw_code = BeautifulSoup(data, 'lxml')  # 建立BeautifulSoup对象
    doc_code = raw_code.find_all('doc')  # 从包含文本的代码块中找到doc标签
    content_list = []  # 建立空列表，用来存储每个content标签的内容
    label_list = []  # 建立空列表，用来存储每个content对应的label的内容
    for each_doc in doc_code:  # 循环读出每个doc标签
        if len(each_doc) > 0:  # 如果dco标签的内容不为空
            content_code = each_doc.find('content')  # 从包含文本的代码块中找到doc标签
            raw_content = content_code.text  # 获取原始内容字符串
            convert_content = str_convert(raw_content)  # 将全角转换为半角
            content_list.append(convert_content)  # 将content文本内容加入列表
            label_code = each_doc.find('url')  # 从包含文本的代码块中找到url标签
            label_content = label_code.text  # 获取url信息
            label = re.split('[/|.]', label_content)[2]  # 将URL做分割并提取子域名
            label_list.append(label)  # 将子域名加入列表
    return content_list, label_list


def cross_val(model_object, data, label):
    predict_label = model_object.predict(data)  # 预测测试集标签
    score_tmp = round(accuracy_score(label, predict_label), 4)  # 计算预测准确率
    return score_tmp


def word_to_vector(data):
    model_vector = HashingVectorizer(non_negative=True)  # 建立HashingVectorizer对象
    vector_data = model_vector.fit_transform(data)  # 将输入文本转化为稀疏矩阵
    return vector_data


def label_to_vector(label, unique_list):
    for each_index, each_data in enumerate(label):  # 循环读取每个标签的索引及对应值
        label[each_index] = unique_list.index(each_data)  # 将值替换为其索引
    return label


if __name__ == '__main__':
    if not os.path.exists('./news_data'):  # 如果不存在数据目录，则先解压数据文件
        print(os.path.curdir)
        print('extract data fromnews_data.tar.gz...')
        # tar = tarfile.open('news_data.tar.gz')  # 打开tar.gz压缩包对象
        # names = tar.getnames()  # 获得压缩包内的每个文件对象的名称
        # for name in names:  # 循环读出每个文件
        #     tar.extract(name, path='./')  # 将文件解压到指定目录
        # tar.close()  # 关闭压缩包对象
    else:
        print('test')
