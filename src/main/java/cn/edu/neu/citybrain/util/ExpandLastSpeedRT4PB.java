package cn.edu.neu.citybrain.util;

import cn.edu.neu.citybrain.db.DBConstants;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.types.Row;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 为城市大脑贡献部分PB级数据
 */
public class ExpandLastSpeedRT4PB {
    private static final Random random = new Random();

    private static final int DAY = 1; // 默认生成1天的数据
    private static final String DT = "20180327"; // 默认生成1天的数据，即1440个时间片

    public static void main(String[] args) {
		// parameters
		ParameterTool parameterTool = ParameterTool.fromArgs(args);
		// help
		if (parameterTool.has("h")) {
			System.out.printf("Usage:\n" +
					"\t%-20s%s\n" +
					"\t%-20s%s\n" +
					"\t%-20s%s\n" +
					"\t%-20s%s\n" +
					"\t%-20s%s\n" +
					"\t%-20s%s\n" +
					"\t%-20s%s\n" +
					"\t%-20s%s\n",
				"--db-address", "default value is " + DBConstants.REMOTE_IP_PORT + ".",
				"--db-name", "default value is " + DBConstants.DB_NAME + ".",
				"--jdbc-user", "default value is " + DBConstants.JDBC_USER + ".",
				"--jdbc-password", "default value is " + DBConstants.JDBC_PWD + ".",
				"--dt-from", "default value is '20180327'.",
				"--day", "default value is 1.",
				"--from", "default 0",
				"--to", "default 0");
			return;
		}
		// dbAddress
		String dbAddress = parameterTool.get("db-address") == null ? DBConstants.REMOTE_IP_PORT : parameterTool.get("db-address");
		// dbName
		String dbName = parameterTool.get("db-name") == null ? DBConstants.DB_NAME : parameterTool.get("db-name");
		// jdbcUrl
		String jdbcUrl = String.format("jdbc:mysql://%s/%s?characterEncoding=UTF-8&useSSL=false&serverTimezone=Hongkong&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true", dbAddress, dbName);
		// jdbcUser
		String jdbcUser = parameterTool.get("jdbc-user") == null ? DBConstants.JDBC_USER : parameterTool.get("jdbc-user");
		// jdbcPassword
		String jdbcPassword = parameterTool.get("jdbc-password") == null ? DBConstants.JDBC_PWD : parameterTool.get("jdbc-password");
		// dt_from
		String dtFromStr = parameterTool.get("dt-from") == null ? DT : parameterTool.get("dt-from");
		// day
		int day = parameterTool.get("day") == null ? DAY : Integer.parseInt(parameterTool.get("day"));
		// from
		int from = parameterTool.get("from") == null ? 0 : Integer.parseInt(parameterTool.get("from"));
		// to
		int to = parameterTool.get("to") == null ? 0 : Integer.parseInt(parameterTool.get("to"));
		// display
		System.out.printf("bootstrap parameters:\n" +
				"\t%-20s%s\n" +
				"\t%-20s%s\n" +
				"\t%-20s%s\n" +
				"\t%-20s%s\n" +
				"\t%-20s%s\n" +
				"\t%-20s%s\n" +
				"\t%-20s%s\n" +
				"\t%-20s%s\n",
			"--db-address", dbAddress,
			"--db-name", dbName,
			"--jdbc-user", jdbcUser,
			"--jdbc-password", jdbcPassword,
			"--dt-from", dtFromStr,
			"--day", day,
			"--from", from,
			"--to", to);

        int speedL = Integer.MAX_VALUE;
        int speedU = Integer.MIN_VALUE;
        int travelTimeL = Integer.MAX_VALUE;
        int travelTimeU = Integer.MIN_VALUE;
        int reliabilityCodeL = Integer.MAX_VALUE;
        int reliabilityCodeU = Integer.MIN_VALUE;

        // read
        List<Row> records = new ArrayList<>();
        try (Connection connection = getConnection(jdbcUrl, jdbcUser, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement("select * from " + DBConstants.dws_tfc_state_rid_tp_lastspeed_rt)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
				String statTime = resultSet.getString("stat_time");
                String rid = resultSet.getString("rid");
                Long stepIndex = resultSet.getLong("step_index");
				Long dataStepIndex = resultSet.getLong("data_step_index");
				String dataTp = resultSet.getString("data_tp");
				String dataStepTime = resultSet.getString("data_step_time");
                Double speed = resultSet.getDouble("speed");
                Double nostopSpeed = resultSet.getDouble("nostop_speed");
                Double travelTime = resultSet.getDouble("travel_time");
                Double nostopTravelTime = resultSet.getDouble("nostop_travel_time");
                Double reliabilityCode = resultSet.getDouble("reliability_code");
                String dt = resultSet.getString("dt");
                String tp = resultSet.getString("tp");
                String dataVersion = resultSet.getString("data_version");
                String adcode = resultSet.getString("adcode");

                speedL = Math.min(speedL, (int) Math.floor(speed));
                speedU = Math.max(speedL, (int) Math.ceil(speed));
                travelTimeL = Math.min(travelTimeL, (int) Math.floor(travelTime));
                travelTimeU = Math.max(travelTimeU, (int) Math.ceil(travelTime));
                reliabilityCodeL = Math.min(reliabilityCodeL, (int) Math.floor(reliabilityCode));
                reliabilityCodeU = Math.max(reliabilityCodeU, (int) Math.ceil(reliabilityCode));

                records.add(new Row(15) {
                    {
                        setField(0, statTime);
                        setField(1, rid);
                        setField(2, stepIndex);
                        setField(3, dataStepIndex);
                        setField(4, dataTp);
                        setField(5, dataStepTime);
                        setField(6, speed);
                        setField(7, nostopSpeed);
                        setField(8, travelTime);
                        setField(9, nostopTravelTime);
                        setField(10, reliabilityCode);
                        setField(11, dt);
                        setField(12, tp);
                        setField(13, dataVersion);
                        setField(14, adcode);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write
        try (Connection connection = getConnection(jdbcUrl, jdbcUser, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement("insert into " + DBConstants.dws_tfc_state_rid_tp_lastspeed_rt + "_pb" + "(stat_time,rid,step_index,data_step_index,data_tp,data_step_time,speed,nostop_speed,travel_time,nostop_travel_time,reliability_code,dt,tp,data_version,adcode) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
            connection.setAutoCommit(false);

			Date dtFrom = new SimpleDateFormat("yyyyMMdd").parse(dtFromStr);
			Date dtTo = new Date(dtFrom.getTime() + (day - 1) * 24 * 60 * 60 * 1000);
			for (Date date = dtFrom; date.compareTo(dtTo) <= 0; date = new Date(date.getTime() + 24 * 60 * 60 * 1000)) {
				String curDt = new SimpleDateFormat("yyyyMMdd").format(date);
				for (long curStepIndex = from; curStepIndex < to; curStepIndex++) {
					System.out.printf("inserting records with %s-%d...\n", curDt, curStepIndex);

					for (Row row : records) {
						double speedMock = speedL + random.nextInt(speedU - speedL) + random.nextDouble();
						double travelTimeMock = travelTimeL + random.nextInt(travelTimeU - travelTimeL) + random.nextDouble();
						double reliabilityCodeMock = reliabilityCodeL + random.nextInt(reliabilityCodeU - reliabilityCodeL) + random.nextDouble();

						preparedStatement.setString(1, (String) row.getField(0)); // stat_time
						preparedStatement.setString(2, (String) row.getField(1)); // rid
						preparedStatement.setLong(3, curStepIndex); // step_index
						preparedStatement.setLong(4, (Long) row.getField(3)); // data_step_index
						preparedStatement.setString(5, (String) row.getField(4)); // data_tp
						preparedStatement.setString(6, (String) row.getField(5)); // data_step_time
						preparedStatement.setDouble(7, speedMock); // speed
						preparedStatement.setDouble(8, (Double) row.getField(7)); // nostop_speed
						preparedStatement.setDouble(9, travelTimeMock); // travel_time
						preparedStatement.setDouble(10, (Double) row.getField(9)); // nostop_travel_time
						preparedStatement.setDouble(11, reliabilityCodeMock); // reliability_code
						preparedStatement.setString(12, curDt); // dt
//						preparedStatement.setString(13, (String) row.getField(12)); // tp
						preparedStatement.setString(13, "1mi"); // tp
						preparedStatement.setString(14, (String) row.getField(13)); // data_version
						preparedStatement.setString(15, (String) row.getField(14)); // adcode
						preparedStatement.addBatch();
					}

					preparedStatement.executeBatch();
					connection.commit();
					preparedStatement.clearBatch();
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private static Connection getConnection(String jdbcUrl, String jdbcUser, String jdbcPassword) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
