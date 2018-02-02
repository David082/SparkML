# DeepLearning4J in action
## chap01
### Word2Vector example
http://blog.csdn.net/a398942089/article/details/51970691<br>
<br>
error: deeplearning4j 0.4.0 breaks on Windows 10 Insider Preview<br>      
<div class="highlight highlight-text-xml"><pre>&lt;<span class="pl-ent">dependency</span>&gt;
    &lt;<span class="pl-ent">groupId</span>&gt;org.nd4j&lt;/<span class="pl-ent">groupId</span>&gt;
    &lt;<span class="pl-ent">artifactId</span>&gt;nd4j-native&lt;/<span class="pl-ent">artifactId</span>&gt;
    &lt;<span class="pl-ent">version</span>&gt;${nd4j.version}&lt;/<span class="pl-ent">version</span>&gt;
    &lt;<span class="pl-ent">classifier</span>&gt;windows-x86_64-openblas&lt;/<span class="pl-ent">classifier</span>&gt;
&lt;/<span class="pl-ent">dependency</span>&gt;</pre>
</div>
<a href="https://github.com/deeplearning4j/deeplearning4j/issues/1819">https://github.com/deeplearning4j/deeplearning4j/issues/1819</a><br>

### xgboost调参
http://blog.csdn.net/sb19931201/article/details/52577592<br>
<pre></code>params={
        'booster':'gbtree',
        'objective': 'multi:softmax', #多分类的问题
        'num_class':10, # 类别数，与 multisoftmax 并用
        'gamma':0.1,  # 用于控制是否后剪枝的参数,越大越保守，一般0.1、0.2这样子。
        'max_depth':12, # 构建树的深度，越大越容易过拟合
        'lambda':2,  # 控制模型复杂度的权重值的L2正则化项参数，参数越大，模型越不容易过拟合。
        'subsample':0.7, # 随机采样训练样本
        'colsample_bytree':0.7, # 生成树时进行的列采样
        'min_child_weight':3, 
        # 这个参数默认是 1，是每个叶子里面 h 的和至少是多少，对正负样本不均衡时的 0-1 分类而言
        #，假设 h 在 0.01 附近，min_child_weight 为 1 意味着叶子节点中最少需要包含 100 个样本。
        #这个参数非常影响结果，控制叶子节点中二阶导的和的最小值，该参数值越小，越容易 overfitting。 
        'silent':0 ,#设置成1则没有运行信息输出，最好是设置为0.
        'eta': 0.007, # 如同学习率
        'seed':1000,
        'nthread':7,# cpu 线程数
        #'eval_metric': 'auc'
}
</code></pre>
### Python API Reference
http://xgboost.readthedocs.io/en/latest/python/python_api.html<br>
### Spark常见问题汇总
https://my.oschina.net/tearsky/blog/629201<br>
### HiveContext
1. save Spark dataframe to Hive: table not readable because “parquet not a SequenceFile”<br>
https://stackoverflow.com/questions/31482798/save-spark-dataframe-to-hive-table-not-readable-because-parquet-not-a-sequence<br>
mode:append<br>
http://blog.csdn.net/haohaixingyun/article/details/54588461<br>
<pre><code>
someDF.write.mode(SaveMode.Overwrite).format("parquet").saveAsTable("TBL_HIVE_IS_HAPPY")
</code></pre>
2. sparksql udf自定义函数中参数过多问题的解决<br>
http://blog.csdn.net/sparkexpert/article/details/52832530<br>
<pre><code>
val colMatchDF = dataJoinCityDF.withColumn("colMatchDF", matcher(col("city_pricefreqarr"), col("user_avgprice_total")))
</code></pre>
3. user defined function<br>
https://stackoverflow.com/questions/44570303/failed-to-execute-user-defined-function-in-apache-spark-using-scala<br>
<pre><code>
val toVec9 = udf ((a: Int, b: Int, c: String, d: String, e: Int, f: Int, g: Int, h: Int, i: Int) =>
{
  val e3 = c match {
    case "10.0.0.1" => 1
    case "10.0.0.2" => 2
    case "10.0.0.3" => 3
  }
  val e4 = d match {
    case "10.0.0.1" => 1
    case "10.0.0.2" => 2
    case "10.0.0.3" => 3
  }
  Vectors.dense(a, b, e3, e4, e, f, g, h, i)
})
</code></pre>
### Spark与Hadoop关系
1. Spark是一个计算框架<br>
Hadoop是包含计算框架MapReducehe分布式文件系统HDFS。<br>
https://www.cnblogs.com/junjiany/p/6396407.html<br>
http://blog.csdn.net/javastart/article/details/70161694<br>
