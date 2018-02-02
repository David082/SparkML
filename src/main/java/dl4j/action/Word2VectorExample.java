package dl4j.action;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by yu_wei on 2018/1/31.
 *
 */
public class Word2VectorExample {
    private static Logger log = LoggerFactory.getLogger(Word2VectorExample.class);

    public static void main(String[] args) throws IOException {


        // String filePath = new ClassPathResource("data/tlbb_t.txt").getFile().getAbsolutePath();

        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator("data/tlbb_t.txt");
        // Split on white spaces in the line to get words

        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");

        // Write word vectors
        WordVectorSerializer.writeWordVectors(vec, "tlbb_vectors.txt");
        WordVectorSerializer.writeFullModel(vec, "tlbb_model.txt");

        String[] names = {"萧峰", "乔峰", "段誉", "虚竹", "王语嫣", "阿紫", "阿朱", "木婉清"};
        log.info("Closest Words:");

        for (String name : names) {
            System.out.println(name + ">>>>>>");
            Collection<String> lst = vec.wordsNearest(name, 10);
            System.out.println(lst);
        }


    }
}
