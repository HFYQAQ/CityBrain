import json
import pymysql
import pymysql.cursors
import random





try:
	#查询数据
	for i in range(-883,557):

		connect1 = pymysql.connect(
		            host="master61",
		            port= 3306,
		            db="city_brain",
		            user="root",
		            passwd="root",
		            charset='utf8',
		            use_unicode=True)


		cursor1 = connect1.cursor(cursor=pymysql.cursors.SSDictCursor)

		connect2 = pymysql.connect(
		            host="master61",
		            port= 3306,
		            db="city_brain",
		            user="root",
		            passwd="root",
		            charset='utf8',
		            use_unicode=True)


		cursor2 = connect2.cursor(cursor=pymysql.cursors.SSDictCursor)
		cursor1.execute(
		    """ select * from dws_tfc_state_rid_tp_lastspeed_rt; """)
		# row = cursor1.fetchone()

		while True:
			row = cursor1.fetchone()
			if not row:
				break
			row["stat_time"]=str(int(row["stat_time"])+i)
			row["step_index"]=row["step_index"]+i
			row["data_step_index"]=row["data_step_index"]+i
			row["data_step_time"]=str(int(row["data_step_time"])+i)
			row["speed"]=random.uniform(10, 70)
			row["nostop_speed"]=random.uniform(10, 70)
			row["travel_time"]=random.uniform(2, 100)
			row["nostop_travel_time"]=random.uniform(2, 100)
			row["reliability_code"]=random.uniform(0, 100)
			#这两个参数不用动
			# row["dt"]=str(int(row["dt"])+i)
			# row["data_version"]=str(int(row["data_version"])+i)
			
			#删除id列，让其自增
			row.pop("id")
			val = '%s, ' * (len(row)-1) + '%s'
			sql = f'insert into mock_speed_rt(stat_time,rid,step_index,data_step_index,data_tp,data_step_time,speed,nostop_speed,travel_time,nostop_travel_time,reliability_code,dt,tp,data_version,adcode) values ({val})'
			# print(row)
			cursor2.execute(sql, tuple(row.values()))
		cursor2.connection.commit()

		cursor1.close()
		cursor1.close()
		cursor2.close()
		cursor2.close()
except Exception as error:
    # 出现错误时打印错误日志
    print(error)
