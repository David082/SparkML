package dl4j.action.examples;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.api.transform.analysis.columns.DoubleAnalysis;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.ui.HtmlAnalysis;
import org.datavec.api.util.ClassPathResource;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.AnalyzeSpark;
import org.datavec.spark.transform.misc.StringToWritablesFunction;

import java.io.File;
import java.util.List;

/**
 * Created by yu_wei on 2018/2/1.
 *
 */
public class IrisAnalysis {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema.Builder()
                .addColumnsDouble("Sepal length", "Sepal width", "Petal length", "Petal width")
                .addColumnInteger("Species")
                .build();
        System.out.println(schema);

        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");
        conf.setAppName("DataVec Example");

        JavaSparkContext sc = new JavaSparkContext(conf);

        //Normally just define your directory like "file:/..." or "hdfs:/..."
        String directory = new ClassPathResource("IrisData/iris.txt").getFile().getParent();
        JavaRDD<String> stringData = sc.textFile(directory);

        //We first need to parse this comma-delimited (CSV) format; we can do this using CSVRecordReader:
        RecordReader rr = new CSVRecordReader();
        JavaRDD<List<Writable>> parsedInputData = stringData.map(new StringToWritablesFunction(rr));

        int maxHistogramBuckets = 10;
        DataAnalysis dataAnalysis = AnalyzeSpark.analyze(schema, parsedInputData, maxHistogramBuckets);
        System.out.println(dataAnalysis);

        //We can get statistics on a per-column basis:
        DoubleAnalysis da = (DoubleAnalysis) dataAnalysis.getColumnAnalysis("Sepal length");
        System.out.println(da);

        HtmlAnalysis.createHtmlAnalysisFile(dataAnalysis, new File("DataVecIrisAnalysis.html"));

        //To write to HDFS instead:
        //String htmlAnalysisFileContents = HtmlAnalysis.createHtmlAnalysisString(dataAnalysis);
        //SparkUtils.writeStringToFile("hdfs://your/hdfs/path/here",htmlAnalysisFileContents,sc);

    }

}
