# -*- coding: utf-8 -*-
"""
Created on Fri Jan 26 13:21:08 2018

@author: xqwen
"""

'''
/opt/app/spark-2.0.1/bin/pyspark

hdfs://ns/user/hotel/wxq/Dictionary/stopwords.txt
hdfs://ns/user/hotel/wxq/Dictionary/xqwen_Dic.txt
/home/hotel/wxq/stopwords.txt
/home/hotel/wxq/xqwen_Dic.txt
/home/hotel/wxq/uid_comment.txt

'''
# from pyspark.sql.types import *
from pyspark.sql import SQLContext, SparkSession
# from pyspark import SparkConf, SparkContext
#
# # conf=SparkConf().setAppName("miniProject").setMaster("local[*]")
# conf = SparkConf().setAppName("pyspark keywords exact").setMaster("yarn-client")
# sc = SparkContext.getOrCreate(conf)
# # sc = SparkContext.getOrCreate()
# sqlContext = SQLContext(sc)
# spark = SparkSession.builder.appName("pyspark keywords exact").master("yarn").enableHiveSupport().getOrCreate()
# spark = SparkSession.builder.appName("pyspark keywords exact").master("local").getOrCreate()
# print(spark.conf)

# import sys
# import os
# import math
# import pandas as pd
# import re
# import codecs
# import jieba.analyse as ana
# import jieba
#
# jieba.load_userdict("/home/hotel/wxq/HotelUidComment/xqwen_Dic.txt")
# from optparse import OptionParser
# from pyspark.sql.functions import udf
#
# # os.chdir("E:\\PythonProject\\publicData")
# # publicDataPath="E:\\PythonProject\\publicData\\"
# # data=pd.read_csv('E:\\优选二期优化/comment_uid_hotel.txt',sep='\t' )
# # data=pd.read_csv("/home/hotel/wxq/uid_comment.txt",sep='\t' )
#
# USAGE = "usage:    python JiebaTest.py [save_table name] [start_table_name] -k [top k]"
#
# parser = OptionParser(USAGE)
# parser.add_option("-k", dest="topK")
# opt, args = parser.parse_args()
#
# if len(args) < 1:
#     print(USAGE)
#     sys.exit(1)
#
# save_table_name = args[0]
# start_table_name = args[1]
#
# if opt.topK is None:
#     topK = 10
# else:
#     topK = int(opt.topK)
#
# print ("keywords num is: ", topK)
# print ("result keywords save table is: ", save_table_name)
# print ("origin writingcontent start table is: ", start_table_name)
#
#
# def getcol(x):
#     return x
#
#
# def getKeywords(line):
#     # ana.set_stop_words("/home/hotel/wxq/stopwords.txt")
#     ana.set_stop_words("/home/hotel/wxq/HotelUidComment/stopwords.txt")
#     # ana.set_idf_path("/home/hotel/wxq/idf.txt")
#     return ana.extract_tags(line, topK=topK, withWeight=False, allowPOS=('ns', 'n'))
#
#
# udf_getKeywords = udf(getKeywords)
# udf_getcol = udf(getcol)
#
# sql = " select * from " + start_table_name + " where writingcontent is not null limit 100 "
# print(sql)
# data = sqlContext.sql(sql)
# data.show()
#
# # result.show()
# # result.write.saveAsTable(save_table_name)
# # result.write.saveAsTable("dw_htlbizdb.tmp_xqwen_keywords_uid_3")



"""
# https://www.cnblogs.com/one-lightyear/p/6814833.html

import re

words = open("data/test.txt", "r").read()
print(words)

regex = re.compile("\\bs\w+e")
print(regex.findall(words))
"""

