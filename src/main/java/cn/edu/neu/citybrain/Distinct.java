package cn.edu.neu.citybrain;

import cn.edu.neu.citybrain.db.DBConstants;
import org.apache.flink.types.Row;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class Distinct {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/city_brain?characterEncoding=UTF-8&useSSL=false&serverTimezone=Hongkong&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true";
        Connection connection = DriverManager.getConnection(url, DBConstants.JDBC_USER, DBConstants.JDBC_PWD);
        PreparedStatement ps = connection.prepareStatement("select * from dwd_tfc_rltn_wide_inter_lane_backup");
        PreparedStatement ps2 = connection.prepareStatement("insert into dwd_tfc_rltn_wide_inter_lane(inter_id,inter_name,lane_id,lane_no,turn_dir_no_list,lane_angle,ft_type_no,rid,rid_angle,rid_dir_4_no,rid_dir_8_no,ft_dir_4_no,ft_dir_8_no,lane_sdtype_no,lane_hdtype_no,data_version,adcode) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        HashMap<String, Row> map = new HashMap<>();

        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            Row row = new Row(17);

            row.setField(0,resultSet.getObject(1));
            row.setField(1,resultSet.getObject(2));
            row.setField(2,resultSet.getObject(3));
            row.setField(3,resultSet.getObject(4));
            row.setField(4,resultSet.getObject(5));
            row.setField(5,resultSet.getObject(6));
            row.setField(6,resultSet.getObject(7));
            row.setField(7,resultSet.getObject(8));
            row.setField(8,resultSet.getObject(9));
            row.setField(9,resultSet.getObject(10));
            row.setField(10,resultSet.getObject(11));
            row.setField(11,resultSet.getObject(12));
            row.setField(12,resultSet.getObject(13));
            row.setField(13,resultSet.getObject(14));
            row.setField(14,resultSet.getObject(15));
            row.setField(15,resultSet.getObject(16));
            row.setField(16,resultSet.getObject(17));

            map.put(resultSet.getString(8), row);
        }

        for (Map.Entry<String, Row> entry : map.entrySet()) {
            ps2.clearParameters();
            Row row = entry.getValue();

            ps2.setObject(1,row.getField(0));
            ps2.setObject(2,row.getField(1));
            ps2.setObject(3,row.getField(2));
            ps2.setObject(4,row.getField(3));
            ps2.setObject(5,row.getField(4));
            ps2.setObject(6,row.getField(5));
            ps2.setObject(7,row.getField(6));
            ps2.setObject(8,row.getField(7));
            ps2.setObject(9,row.getField(8));
            ps2.setObject(10,row.getField(9));
            ps2.setObject(11,row.getField(10));
            ps2.setObject(12,row.getField(11));
            ps2.setObject(13,row.getField(12));
            ps2.setObject(14,row.getField(13));
            ps2.setObject(15,row.getField(14));
            ps2.setObject(16,row.getField(15));
            ps2.setObject(17,row.getField(16));
            ps2.executeUpdate();
        }

        ps2.close();
        ps.close();
        connection.close();
    }
}
