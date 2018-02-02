//package xgb;
//
//
//import org.dmlc.xgboost4j.Booster;
//import org.dmlc.xgboost4j.DMatrix;
//import org.dmlc.xgboost4j.util.Trainer;
//import org.dmlc.xgboost4j.util.XGBoostError;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//
///**
// * Created by yu_wei on 2018/1/31.
// *
// */
//public class XGBModel {
//
//    private static final String LABEL = "ordergp";
//    private static final String CSV_PATH = "data/purexgb.csv";
//    private static final String MODEL_PATH = "data/javaxgb.model";
//    private static final String[] FEATURE_LIST = {
//        "cityid",
//        "starlicence",
//        "star",
//        "goldstar",
//        "ratingoverall",
//        "novoters",
//        "customereval",
//        "country",
//        "province",
//        "zone",
//        "upper_1cycle_gp",
//        "upper_2cycle_gp",
//        "upper_3cycle_gp",
//        "upper_4cycle_gp",
//        "his_28days_gp",
//        "his_3months_gp",
//        "his_6months_gp",
//        "his_9months_gp",
//        "his_12months_gp",
//        "year",
//        "month",
//        "weekofyear",
//        "quarter",
//        "weekdays",
//        "holidays",
//        "gp_holiday_hotel_heat",
//        "gp_holiday_zone_heat",
//        "gp_holiday_city_heat",
//        "hotel_div_zone_heat_gp",
//        "zone_div_city_heat_gp",
//        "upper_1cycle_uv",
//        "upper_2cycle_uv",
//        "upper_3cycle_uv",
//        "upper_4cycle_uv",
//        "his_28days_uv",
//        "his_3months_uv",
//        "his_6months_uv",
//        "hotel_pic_score",
//        "hq_hotel_pic_score",
//        "room_pic_score",
//        "static_info_score"
//    };
//
//    public static void main(String[] args) throws XGBoostError, IOException {
//        byte round = 100;
//        Set<Map.Entry<String, Object>> param = generateParameters();
//
//        DataBean bean = dMatrixFromCsv(CSV_PATH);
//        List<String> keys = bean.keys;
//        System.out.println(keys);
//
//        DMatrix dtrain = bean.dataMat;
//        System.out.println(dtrain.rowNum());
//
//        List<Map.Entry<String, DMatrix>> watchs = new ArrayList<>();
//        watchs.add(new AbstractMap.SimpleEntry<>("train", dtrain));
//
//        Booster booster = Trainer.train(param, dtrain, round, watchs, null, null);
//        booster.saveModel(MODEL_PATH);
//
//    }
//
//    private static Set<Map.Entry<String, Object>> generateParameters() {
//        return new HashMap<String, Object>() {{
//            put("eta", 0.05f);
//            put("max_depth", 5);
//            put("silent", 0);
//            put("eval_metric", "rmse");
//            put("objective", "reg:linear");
//        }}.entrySet();
//    }
//
//    private static DataBean dMatrixFromCsv(String filePath) throws IOException, XGBoostError {
//        BufferedReader br = new BufferedReader(new FileReader(filePath));
//        String[] nameArr = br.readLine().split(",");
//
//        // Read csv data as list
//        List<Map<String, Float>> list = new ArrayList<Map<String, Float>>();
//        String paramLine = null;
//        while ((paramLine = br.readLine()) != null) {
//            Map<String, Float> map = new HashMap<String, Float>();
//            String[] paramLineArr = paramLine.split(",");
//            for (int i = 0; i < paramLineArr.length; i++) {
//                map.put(nameArr[i], Float.parseFloat(paramLineArr[i]));
//            }
//            list.add(map);
//        }
//
//        // List to map: generate keys
//        Map<String, HashMap<String, Float>> dataMap = new HashMap<String, HashMap<String, Float>>();
//        for (int i = 0; i < list.size(); i++) {
//            Map<String, Float> nrow = list.get(i);
//            dataMap.put(Integer.toString(i), (HashMap<String, Float>) nrow);
//        }
//
//        /* sort */
//        TreeMap<String, HashMap<String, Float>> sortMap = new TreeMap<String, HashMap<String, Float>>(dataMap);
//        List<String> keys = new ArrayList<String>();  // keys
//        float[] labels = new float[list.size()];  // labels
//        List<Float> rowValues = new ArrayList<Float>();  // features: row values
//        int r = 0;
//        for (Map.Entry<String, HashMap<String, Float>> it : sortMap.entrySet()) {
//            keys.add(it.getKey());
//            labels[r] = it.getValue().get(LABEL);
//            for (int i = 0; i < FEATURE_LIST.length; i++) {
//                rowValues.add(it.getValue().get(FEATURE_LIST[i]));
//            }
//            r += 1;
//        }
//
//        // Map to databean
//        DataBean bean = new DataBean();
//        float[] onerow = new float[rowValues.size()];
//        for (int j = 0; j < rowValues.size(); j++) {
//            onerow[j] = rowValues.get(j);  // get values
//        }
//        int nrow = onerow.length / FEATURE_LIST.length;
//        int ncol = FEATURE_LIST.length;
//        DMatrix dataMat = new DMatrix(onerow, nrow, ncol);
//        dataMat.setLabel(labels);
//
//        bean.keys = keys;
//        bean.dataMat = dataMat;
//
//        return bean;
//    }
//
//}
//
//class DataBean {
//    List<String> keys;
//    DMatrix dataMat;
//}
