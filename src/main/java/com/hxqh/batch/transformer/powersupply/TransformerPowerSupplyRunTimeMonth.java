package com.hxqh.batch.transformer.powersupply;

import com.hxqh.task.sink.MySQLYxFanSink;
import com.hxqh.utils.DateUtils;
import com.hxqh.utils.RemindDateUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;

import java.sql.Timestamp;
import java.sql.Types;

import static com.hxqh.constant.Constant.*;

/**
 * 每月第2天生成下月运行时间数据
 * <p>
 * 实时处理风机数据 {@link MySQLYxFanSink}
 *
 * Created by Ocean lin on 2020/4/16.
 *
 * @author Ocean lin
 */
@SuppressWarnings("Duplicates")
public class TransformerPowerSupplyRunTimeMonth {

    public static void main(String[] args) throws Exception {
        final TypeInformation<?>[] fieldTypes = getFieldTypes();
        final int[] type = getType();

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // asset
        RowTypeInfo assetTypeInfo = new RowTypeInfo(fieldTypes);
        String selectQuery = "select ASSETNUM,ASSETYPE,PRODUCTMODEL,LOCATION,PRODUCTMODELB,PRODUCTMODELC,PARENT from ASSET where ASSETYPE='变压器'";
        JDBCInputFormat.JDBCInputFormatBuilder inputBuilder =
                JDBCInputFormat.buildJDBCInputFormat().setDrivername(DB2_DRIVER_NAME).setDBUrl(DB2_DB_URL)
                        .setQuery(selectQuery).setRowTypeInfo(assetTypeInfo).setUsername(DB2_USERNAME)
                        .setPassword(DB2_PASSWORD);

        DataSet<Row> assetRow = env.createInput(inputBuilder.finish());

        DataSet<Row> result = assetRow.map(new MapFunction<Row, Row>() {
            @Override
            public Row map(Row value) throws Exception {
                Row row = new Row(13);
                row.setField(0, value.getField(0).toString());
                row.setField(1, value.getField(1).toString());
                row.setField(2, value.getField(2).toString());
                row.setField(3, value.getField(3).toString());
                row.setField(4, value.getField(4).toString());
                row.setField(5, value.getField(5).toString());
                row.setField(6, value.getField(6).toString());
                // RUNNINGTIME,DOWNTIME,RUNSTATUS,COLTIME,CREATETIME,PARTICULARTIME

                // 当月总分钟数
                row.setField(7, RemindDateUtils.countDaysInNextMonth() * 24 * 60 * 1.0);
                row.setField(8, 0.0d);
                row.setField(9, 0);
                row.setField(10, new Timestamp(RemindDateUtils.getNextMonthStartTime().getTime()));
                row.setField(11, new Timestamp(DateUtils.getFormatDate().getTime()));
                row.setField(12, RemindDateUtils.getNextMonth());

                return row;
            }
        });


        String insertQuery = "INSERT INTO RE_TRANS_PS_RUN_MONTH (IEDNAME,ASSETYPE,PRODUCTMODEL,LOCATION,PRODUCTMODELB,PRODUCTMODELC,PARENT,RUNNINGTIME,DOWNTIME,RUNSTATUS,COLTIME,CREATETIME,PARTICULARTIME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JDBCOutputFormat.JDBCOutputFormatBuilder outputBuilder =
                JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(DB2_DRIVER_NAME).setDBUrl(DB2_DB_URL)
                        .setQuery(insertQuery).setSqlTypes(type).setUsername(DB2_USERNAME).setPassword(DB2_PASSWORD);
        result.output(outputBuilder.finish());

        env.execute("TransformerPowerSupplyQuarter");
    }


    private static TypeInformation<?>[] getFieldTypes() {
        return new TypeInformation<?>[]{BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO};
    }

    private static int[] getType() {
        return new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.DOUBLE, Types.DOUBLE, Types.INTEGER, Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR};
    }

}

