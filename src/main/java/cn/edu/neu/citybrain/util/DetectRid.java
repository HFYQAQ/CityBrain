package cn.edu.neu.citybrain.util;

import cn.edu.neu.citybrain.db.DBConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class DetectRid {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://mysql-svc:3306/city_brain_hz?characterEncoding=UTF-8&useSSL=false&serverTimezone=Hongkong&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true";
        Connection connection = DriverManager.getConnection(url, DBConstants.JDBC_USER, DBConstants.JDBC_PWD);
        PreparedStatement ps1 = connection.prepareStatement("select rid from dwd_tfc_rltn_wide_inter_lane");
        PreparedStatement ps2 = connection.prepareStatement("select rid from dws_tfc_state_rid_tpwkd_index_m where day_of_week=0 and step_index=0");
        PreparedStatement ps3 = connection.prepareStatement("select f_rid from dws_tfc_state_signinterfridseq_tpwkd_delaydur_m where day_of_week=0 and step_index=0");

        Set<String> set1 = new HashSet<>();
        ResultSet resultSet = ps1.executeQuery();
        while (resultSet.next()) {
            set1.add(resultSet.getString("rid"));
        }

        Set<String> set2 = new HashSet<>();
        resultSet = ps2.executeQuery();
        while (resultSet.next()) {
            set2.add(resultSet.getString("rid"));
        }

        Set<String> set3 = new HashSet<>();
        resultSet = ps3.executeQuery();
        while (resultSet.next()) {
            set3.add(resultSet.getString("f_rid"));
        }
        // size1=133033
        // size2=51727
        // size3=18602
        System.out.printf("size1=%d\nsize2=%d\nsize3=%d\n", set1.size(), set2.size(), set3.size());

        int commonCnt12 = 0;
        int commonCnt13 = 0;
        for (String rid : set1) {
            if (set2.contains(rid)) {
                commonCnt12++;
            }
            if (set3.contains(rid)) {
                commonCnt13++;
            }
        }
        int commonCnt23 = 0;
        for (String rid : set2) {
            if (set3.contains(rid)) {
                commonCnt23++;
            }
        }
        // commonCnt12=51189
        // commonCnt13=18602
        // commonCnt23=13066
        System.out.printf("commonCnt12=%d\ncommonCnt13=%d\ncommonCnt23=%d\n", commonCnt12, commonCnt13, commonCnt23);

        ps1.close();
        ps2.close();
        ps3.close();
        connection.close();
    }
}
