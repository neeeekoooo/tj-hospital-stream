package com.hxqh.batch.lowpressure.useefficiency.total;


import com.hxqh.enums.ChangeEnum;
import com.hxqh.utils.RemindDateUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;
import java.sql.Types;
import static com.hxqh.constant.Constant.*;

/**
 * 整体低压设备总运行时长--年度比对
 * <p>
 * Created by Ocean on 2020/4/22.
 *
 * @author Ocean
 */
@SuppressWarnings("DuplicatedCode")
public class TotalLowPressureUseEfficiencyCompareYear {

    public static void main(String[] args) throws Exception {
        final int[] type = getType();


        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        final TypeInformation<?>[] fieldTypes = getFieldTypes();
        RowTypeInfo typeInfo = new RowTypeInfo(fieldTypes);
        String quarterQuery = "select ASSETYPE,LOCATION,PRODUCTMODELC,UTILIZATIONRATIO,RUNNINGTIME,CREATETIME" +
                " from RE_ALL_LP_UE_YEAR where ASSETYPE like '低压开关设备%' and CREATETIME in "+RemindDateUtils.getLastTwoYearString();
        JDBCInputFormat.JDBCInputFormatBuilder lowInputBuilder =
                JDBCInputFormat.buildJDBCInputFormat().setDrivername(DB2_DRIVER_NAME).setDBUrl(DB2_DB_URL)
                        .setQuery(quarterQuery).setRowTypeInfo(typeInfo).setUsername(DB2_USERNAME)
                        .setPassword(DB2_PASSWORD);

        DataSet<Row> quarterRow = env.createInput(lowInputBuilder.finish());

        DataSet<Tuple6<String, Double,Double,String,String,Double>> quarterData = quarterRow.map(new MapFunction<Row, Tuple6<String, Double,Double,String,String,Double>>() {
            @Override
            public Tuple6<String, Double,Double,String,String,Double> map(Row row) throws Exception {
                String key = row.getField(0).toString() + "|" + row.getField(1).toString()+ "|" + row.getField(2).toString();
                return Tuple6.of(key,Double.parseDouble(row.getField(3).toString()),Double.parseDouble(row.getField(4).toString()),row.getField(5).toString(),"",0.0);
            }
        });

        DataSet<Tuple6<String, Double,Double,String,String,Double>> reduce = quarterData.groupBy(0).reduce(new ReduceFunction<Tuple6<String, Double,Double,String,String,Double>>() {
            @Override
            public Tuple6<String, Double,Double,String,String,Double> reduce(Tuple6<String, Double,Double,String,String,Double> v1, Tuple6<String, Double,Double,String,String,Double> v2) throws Exception {
                if (v1.f3.compareTo(v2.f3) == 1) {
                    Double div = (v1.f2 - v2.f2) / v2.f2;
                    v1.f5 = div;
                    if (div > -Proportion && div < Proportion) {
                        v1.f4 = ChangeEnum.Roughly_flat.getCode();
                    } else if (div <= -Proportion) {
                        v1.f4 = ChangeEnum.Decreased.getCode();
                    } else if (div >= Proportion) {
                        v1.f4 = ChangeEnum.Increased.getCode();
                    }
                    return v1;
                } else {
                    Double div = (v2.f2 - v1.f2) / v1.f2;
                    v2.f5 = div;
                    if (div > -Proportion && div < Proportion) {
                        v2.f4 = ChangeEnum.Roughly_flat.getCode();
                    } else if (div <= -Proportion) {
                        v2.f4 = ChangeEnum.Decreased.getCode();
                    } else if (div >= Proportion) {
                        v2.f4 = ChangeEnum.Increased.getCode();
                    }
                    return v2;
                }
            }
        });

        DataSet<Row> result = reduce.map(new MapFunction<Tuple6<String, Double,Double,String,String,Double>, Row>() {
            @Override
            public Row map(Tuple6<String, Double,Double,String,String,Double> value) throws Exception {
                Row row = new Row(8);

                String[] spilt = value.f0.split("\\|");
                row.setField(0, spilt[0]);
                row.setField(1, spilt[1]);
                row.setField(2, spilt[2]);

                row.setField(3, value.f1);
                row.setField(4, value.f2);
                row.setField(5, value.f4);
                row.setField(6, Double.isNaN(value.f5)?0.0:value.f5);//比对率
                row.setField(7, value.f3);//时间-年度

                return row;
            }
        });
        String insertQuery = "INSERT INTO RE_ALL_LP_UE_COM_YEAR(ASSETYPE,LOCATION,PRODUCTMODELC,UTILIZATIONRATIO," +
                "RUNNINGTIME,COMPARISON,RATIO,CREATETIME) VALUES(?,?,?,?,?,?,?,?)";
        JDBCOutputFormat.JDBCOutputFormatBuilder outputBuilder =
                JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(DB2_DRIVER_NAME).setDBUrl(DB2_DB_URL)
                        .setQuery(insertQuery).setSqlTypes(type).setUsername(DB2_USERNAME).setPassword(DB2_PASSWORD);
        result.output(outputBuilder.finish());

        env.execute("TotalLowPressureUseEfficiencyCompareYear");
    }


    private static TypeInformation<?>[] getFieldTypes() {
        return new TypeInformation<?>[]{BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO,BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.DOUBLE_TYPE_INFO,BasicTypeInfo.DOUBLE_TYPE_INFO,BasicTypeInfo.STRING_TYPE_INFO
        };
    }


    private static int[] getType() {
        return new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.DOUBLE, Types.DOUBLE, Types.VARCHAR,Types.DOUBLE,Types.VARCHAR};
    }


}
