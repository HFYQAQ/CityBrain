/*
 Navicat Premium Data Transfer

 Source Server         : city
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Host           : rm-2ze5czh6fqwgnkk743o.mysql.rds.aliyuncs.com:3306
 Source Schema         : city_brain

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 11/07/2021 16:27:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dwd_tfc_bas_rdnet_rid_info
-- ----------------------------
DROP TABLE IF EXISTS `dwd_tfc_bas_rdnet_rid_info`;
CREATE TABLE `dwd_tfc_bas_rdnet_rid_info`  (
  `rid` varchar(23) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '路段ID',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'rid名称，构成格式为x:y-z，其中x为rid所在道路名称，x为与rid起点相交的道路的名称，y为与rid终点相交的道路的名称',
  `len` double NULL DEFAULT NULL COMMENT '路段净长度：米；不含路口长度，路段起点经纬度到路段终点经纬度之间的路段长度',
  `betw_inter_len` double NULL DEFAULT NULL COMMENT '路段(含路口)长度.单位：米，两个路口中心点之间的路段长度',
  `width` double NULL DEFAULT NULL COMMENT '路段宽度，单位：米；取路长度占比较大的link的路宽',
  `max_speed` int NULL DEFAULT NULL COMMENT '最大限速：取link中最大限速km/h ',
  `min_speed` int NULL DEFAULT NULL COMMENT '最小限速：取link中最小限速km/h',
  `angle` double NULL DEFAULT NULL COMMENT '起点路口→终点路口连直线，相对于正北，顺时针方向的角度',
  `dir_4_no` tinyint NULL DEFAULT NULL COMMENT '起点路口id到终点路口id连直线，顺时针方向的角度。1、南向北（<=45或>=315）2、西向东（>45且<=135）3、北向南（>135且<=225）4、东向西（>225且<=315）',
  `dir_8_no` tinyint NULL DEFAULT NULL COMMENT '1、南向北（<=22.5或>=337.5） 2、西南向东北（>22.5且<=67.5） 3、西向东（>67.5且<=112.5） 4、西北向东南（>112.5且<=157.5） 5、北向南（>157.5且<=202.5） 6、东北向西南（<=202.5或>=247.5） 7、东向西（>247.5且<=292.5） 8、东南向西北（>292.5且<=337.5）',
  `road_level` int NULL DEFAULT NULL COMMENT '路段等级；按组成路段的主要link的等级算。取值参照高德道路数据roadclass字段：41000(高速公路)；42000(国道)；43000(城市快速路)；44000(城市主干道)；45000(城市次干道)；47000(城市普通道路)；51000(省道)；52000(县道)；53000(乡道)；54000(县乡村内部道路)；49：小路',
  `rid_type_no` int NULL DEFAULT NULL COMMENT '路段类型包括： 1、主路：（高德link，formway=1,15） 2、辅路：路段中只要有辅路link就算辅路。（高德link，formway=7,11,12,13,14,6,56,58,3, 8,9,10,53,16）',
  `pass_type_no` int NULL DEFAULT NULL COMMENT '-1：暂无数据,1：机动车,2：非机动车,3：机非混合,4：行人',
  `overlap` int NULL DEFAULT NULL COMMENT '指沿着同向行车方向上方有高架，或下方有隧道。 -1：未调查 1：无 2：上方有高架  3：下方有隧道 4：上方既有高架又有隧道',
  `median` int NULL DEFAULT NULL COMMENT '隔离带属性,-1:未调查,1:有物理隔离带但双向道路经纬度串不重叠,2:无隔离带但双向道路经纬度串不重叠,3:有物理隔离带但双向道路经纬度串重叠,4:无隔离带且双向道路经纬度串重叠',
  `walkway` int NULL DEFAULT NULL COMMENT '是否有人行道，-1：未调查 1：有人行道 2：无人行道',
  `fork` int NULL DEFAULT NULL COMMENT '是否有岔口，-1：未调查 1：有岔口 2：无岔口',
  `lane_cnt` int NULL DEFAULT NULL COMMENT 'rid上渠化面长度占比最大的渠化面对应的车道数,若想要rid末端处的车道数,请取lane_cnt_end',
  `p_rid` varchar(23) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '独立右转、独立左转和辅路存附属的主路段RID',
  `droad_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属分方向道路',
  `start_cross_id` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '起点对象ID,正常情况，路段起点坐标在空间位置和起点路口相连；但独立右转等特殊路段，路段起点坐标和起点路口空间上不相连。',
  `start_cross_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'inter、fork',
  `start_lng` double NULL DEFAULT NULL COMMENT '起点经度',
  `start_lat` double NULL DEFAULT NULL COMMENT '起点纬度',
  `start_geohash` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '起点经纬度geohash',
  `end_cross_id` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '终点路口ID',
  `end_cross_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'inter、fork',
  `end_lng` double NULL DEFAULT NULL COMMENT '终点经度',
  `end_lat` double NULL DEFAULT NULL COMMENT '终点纬度',
  `end_geohash` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '终点经纬度geohash',
  `lnglat_seq` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '路段经纬度序列，路段详细坐标字符串，格式：x1,y1;x2,y2;x3,y3;',
  `p_start_inter` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '独立右转、独立左转和辅路存附属的主路段的出口道路口',
  `p_end_inter` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '独立右转、独立左转和辅路存附属的主路段的进口道路口',
  `openlr_info` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内部字段。把路段编码成openlr点。用于季度版本更新时，查找过滤差分link',
  `rid_func_no` int NULL DEFAULT NULL COMMENT 'rid功能类型编码,取值如下,0:其他,1:独立右转,2:独立左转,3:高架/快速路进口匝道,4:高架/快速路出口匝道,5:高速进口匝道,6:高速出口匝道,7:隧道入口,8:隧道出口,9:桥梁入口,10:桥梁出口,11:隧道主线,12:桥梁主线,13:高架/快速路主线,14:高速主线,15:服务区道路,16:高速进口互通,17:高速出口互通,18:高速直连互通(既是进口也是出口),19:高速中段互通(非进口非出口并且具有互通属性)',
  `lane_cnt_start` int NULL DEFAULT NULL COMMENT 'rid起点处车道数,不包含非机动车道',
  `lane_cnt_end` int NULL DEFAULT NULL COMMENT 'rid终点处车道数,不包含非机动车道',
  `data_version` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据版本，如20180331',
  `adcode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '城市编码',
  PRIMARY KEY (`rid`, `data_version`) USING BTREE,
  INDEX `endcross_index`(`end_cross_id`, `data_version`) USING BTREE,
  INDEX `startcross_index`(`start_cross_id`, `data_version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'rid基本信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dwd_tfc_ctl_intersignal_oper_rt
-- ----------------------------
DROP TABLE IF EXISTS `dwd_tfc_ctl_intersignal_oper_rt`;
CREATE TABLE `dwd_tfc_ctl_intersignal_oper_rt`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `inter_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '路口id',
  `inter_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '路口名称',
  `cycle_start_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '周期开始时间，格式为yyyy-mm-dd hh:mi:ss',
  `start_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '相位/阶段实际开始时间-与phase_name对齐，格式为yyyy-mm-dd hh:mi:ss',
  `end_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '相位/阶段实际结束时间-与phase_name对齐，格式为yyyy-mm-dd hh:mi:ss',
  `phase_plan_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '相位方案号，与phasedir表对齐',
  `time_plan_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配时方案号',
  `phase_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '相位名称/阶段名称，具体以信号机可接收的为准',
  `split_time` int NULL DEFAULT -1 COMMENT '实际绿信时长，绿信时长=绿灯时长+黄灯时长+全红时长',
  `cycle_time` int NULL DEFAULT -1 COMMENT '相位方案实际周期时长，秒',
  `green_time` int NULL DEFAULT -1 COMMENT '相位/阶段实际绿灯时长,秒-与phase_name对齐',
  `source` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '数据来源，系统接入或者人工录入等',
  `dt` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日期,yyyymmdd',
  `data_version` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '路网数据版本',
  `adcode` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '城市编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_default`(`inter_id`, `cycle_start_time`, `start_time`, `phase_plan_id`, `time_plan_id`, `phase_name`, `data_version`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17260 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '信号灯配时实际执行数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dwd_tfc_rltn_inter_ctlregion_multiperiod
-- ----------------------------
DROP TABLE IF EXISTS `dwd_tfc_rltn_inter_ctlregion_multiperiod`;
CREATE TABLE `dwd_tfc_rltn_inter_ctlregion_multiperiod`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `task_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '优化任务ID',
  `udgrid_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '优化范围ID',
  `doe_date_type` int NOT NULL COMMENT '全维度日期类型  0：周一；1：周二；2：周三；3：周四；4：周五；5：周六；6：周日；99：工作日，100：非工作日',
  `multiperiod_start_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '多时段子区开始时间，格式：HH:mi:ss',
  `multiperiod_end_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '多时段子区结束时间，格式：HH:mi:ss',
  `inter_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '路口ID',
  `is_offsetkey_inter` tinyint NOT NULL COMMENT '是否相位差优化参考路口：0：不是，1：是',
  `unit_ctlregion_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属最小子区单元ID，固定不变',
  `ctlregion_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属将子区单元合并后的控制子区ID，可随时段变化',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开始时间,格式yyyy-MM-dd hh:mi:ss',
  `modified_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改时间,格式yyyy-MM-dd hh:mi:ss',
  `delete_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除时间,格式yyyy-MM-dd hh:mi:ss',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 1：删除 0：未删除，有效',
  `data_version` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '路网数据版本',
  `adcode` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '城市编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk`(`task_id`, `udgrid_id`, `doe_date_type`, `inter_id`, `multiperiod_start_time`, `multiperiod_end_time`, `unit_ctlregion_id`, `ctlregion_id`, `is_deleted`, `data_version`, `adcode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14414 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '分时段路口子区关系信息表-应用维护' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dwd_tfc_rltn_wide_cross_ftrid
-- ----------------------------
DROP TABLE IF EXISTS `dwd_tfc_rltn_wide_cross_ftrid`;
CREATE TABLE `dwd_tfc_rltn_wide_cross_ftrid`  (
  `cross_id` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '  交叉口id ',
  `cross_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  交叉口名称 ',
  `cross_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  交叉口类型：inter/fork  ',
  `is_signlight` tinyint NULL DEFAULT NULL COMMENT '  是否为信号灯路口 1表示路口 0表示非路口 ',
  `is_corner` tinyint NULL DEFAULT NULL COMMENT '  是否为综合路口 1是 0非 ',
  `ftrid_len` int NULL DEFAULT NULL COMMENT '  进口路段rid终点与出口路段rid起点直线距离 ',
  `f_rid` varchar(23) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '  进口rid ',
  `f_rid_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  进口rid名称 ',
  `f_rid_angle` double NULL DEFAULT NULL COMMENT '  rid起点路口→终点路口连直线，相对于正北，顺时针方向的角度  ',
  `f_rid_dir_8_no` int NULL DEFAULT NULL COMMENT '  进口道rid角度的8方向编码  ',
  `f_rid_dir_4_no` int NULL DEFAULT NULL COMMENT '  进口道rid角度的4方向编码  ',
  `f_angle` double NULL DEFAULT NULL COMMENT '  取进口道rid倒数第一个和倒数第二个经纬度点，计算倒数第二个点到倒数第一个点相对于正北方向的角度，作为进口道进入路口的角度 ',
  `f_dir_8_no` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  进口道进入路口相对于正北方向的角度的8方向编码 ',
  `f_dir_4_no` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  进口道进入路口相对于正北方向的角度的4方向编码 ',
  `f_road_level` int NULL DEFAULT NULL COMMENT '  进口rid道路等级 ',
  `f_rid_type_no` int NULL DEFAULT NULL COMMENT '  进口路段类型包括,1、主路：（高德link，formway=1/15）;2、辅路：路段中只要有辅路link就算辅路。（高德link，formway=7）;3、独立右转路段：路段中只要有独立右转车道就算是独立右转路段。（高德link，formray=1112）;4、独立左转路段;5、高速/高架/快速路上匝道路段。（高德link，formray=6）;5、高速/高架/快速路下匝道路段。（高德link，formray=6）;6、高速与高速连接路段（互通，高德link，formway=3）;7、隧道;8、虚拟路段：在以上路段内部的拓扑无法表达时，可以构建虚拟路段;9、出口（高德link formway=9）;10、入口（高德link formway= 10）;11、引路+jct（高德link formway=8） ',
  `f_pass_type_no` tinyint NULL DEFAULT NULL COMMENT '  -1：暂无数据,1：机动车,2：非机动车,3：机非混合,4：行人  ',
  `f_overlap` tinyint NULL DEFAULT NULL COMMENT '  进口是否立体重叠道路，。-1：未调查 1：为立体重叠道路，2：不是立体重叠道路 ',
  `f_median` tinyint NULL DEFAULT NULL COMMENT '  进口是否有隔离带  ',
  `f_walkway` tinyint NULL DEFAULT NULL COMMENT '  进口是否有人行道个数，-1：未调查 1：有人行道 2：无人行道 ',
  `f_fork` tinyint NULL DEFAULT NULL COMMENT '  进口是否有岔口，-1：未调查 1：有岔口 2：无岔口  ',
  `f_rid_length` double NULL DEFAULT NULL COMMENT '  进口rid长度 ',
  `f_rid_lnglat_seq` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '  进口rid 经纬度点序列  ',
  `f_lane_cnt` int NULL DEFAULT NULL COMMENT '  进口rid车道数量 ',
  `f_road_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  进口道路名称  ',
  `t_rid` varchar(23) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '  出口rid ',
  `t_rid_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  出口rid名称 ',
  `t_rid_angle` double NULL DEFAULT NULL COMMENT '  rid起点路口→终点路口连直线，相对于正北，顺时针方向的角度  ',
  `t_rid_dir_8_no` int NULL DEFAULT NULL COMMENT '  进口道rid角度的8方向编码  ',
  `t_rid_dir_4_no` int NULL DEFAULT NULL COMMENT '  进口道rid角度的4方向编码  ',
  `t_angle` double NULL DEFAULT NULL COMMENT '  取出口道rid第一个和第二个经纬度点，计算第一个点到第二个点相对于正北方向的角度，作为出口道离开路口的角度 ',
  `t_dir_8_no` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  进口道进入路口相对于正北方向的角度的8方向编码 ',
  `t_dir_4_no` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  进口道进入路口相对于正北方向的角度的4方向编码 ',
  `t_road_level` int NULL DEFAULT NULL COMMENT '  出口rid的道路等级  ',
  `t_rid_type_no` int NULL DEFAULT NULL COMMENT '  进口路段类型包括,1、主路：（高德link，formway=1/15）;2、辅路：路段中只要有辅路link就算辅路。（高德link，formway=7）;3、独立右转路段：路段中只要有独立右转车道就算是独立右转路段。（高德link，formray=1112）;4、独立左转路段;5、高速/高架/快速路上匝道路段。（高德link，formray=6）;5、高速/高架/快速路下匝道路段。（高德link，formray=6）;6、高速与高速连接路段（互通，高德link，formway=3）;7、隧道;8、虚拟路段：在以上路段内部的拓扑无法表达时，可以构建虚拟路段;9、出口（高德link formway=9）;10、入口（高德link formway= 10）;11、引路+jct（高德link formway=8） ',
  `t_pass_type_no` tinyint NULL DEFAULT NULL COMMENT '  -1：暂无数据,1：机动车,2：非机动车,3：机非混合,4：行人  ',
  `t_overlap` tinyint NULL DEFAULT NULL COMMENT '  出口道是否立体重叠道路，。-1：未调查 1：为立体重叠道路，2：不是立体重叠道路  ',
  `t_median` tinyint NULL DEFAULT NULL COMMENT '  出口道是否有隔离带,只要有一段link有隔离带，整个路段就认为有;-1：未调查,1：有物理隔离带,2：有法律隔离带,3：无隔离带  ',
  `t_walkway` tinyint NULL DEFAULT NULL COMMENT '  出口道是否有人行道个数，-1：未调查 1：有人行道 2：无人行道  ',
  `t_fork` tinyint NULL DEFAULT NULL COMMENT '  出口道是否有岔口，-1：未调查 1：有岔口 2：无岔口f_f_ ',
  `t_rid_length` double NULL DEFAULT NULL COMMENT '  出口道rid长度  ',
  `t_rid_lnglat_seq` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '  出口道rid 经纬度点序列 ',
  `t_lane_cnt` int NULL DEFAULT NULL COMMENT '  出口到车道数量 ',
  `t_road_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '  出口道路名称  ',
  `turn_dir_no` tinyint NULL DEFAULT NULL COMMENT '  转向编码  ',
  `data_version` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '  版本信息，季度的最后一天如20180331 ',
  `adcode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '  城市编码  ',
  PRIMARY KEY (`cross_id`, `f_rid`, `t_rid`, `data_version`, `adcode`) USING BTREE,
  INDEX `index_id`(`cross_id`) USING BTREE,
  INDEX `index_frid`(`f_rid`, `t_rid`, `cross_id`) USING BTREE,
  INDEX `index_trid`(`t_rid`, `f_rid`, `cross_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'rid和交叉口关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dws_tfc_devc_devc_devctypetp_status_d
-- ----------------------------
DROP TABLE IF EXISTS `dws_tfc_devc_devc_devctypetp_status_d`;
CREATE TABLE `dws_tfc_devc_devc_devctypetp_status_d`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `stat_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '数据产出时间,格式yyyymmddhhmiss',
  `devc_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '设备id',
  `step_index` int NULL DEFAULT -1 COMMENT '时间片序号',
  `sub_devc_type_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '子设备类型编码',
  `devc_status_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '设备当前状态类型,1:完好,2:故障',
  `acu_alarm_cnt` int NULL DEFAULT -1 COMMENT '设备当天0点到当前时间片,累积发生过的故障次数,不包含正在发生的故障',
  `devc_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '设备名称',
  `acu_alarm_time` int NULL DEFAULT -1 COMMENT '设备当天0点到当前时间片的故障总时长',
  `dt` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '日期,yyyymmdd',
  `tp` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '时间片,目前为10mi',
  `devc_type_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '设备类型编码',
  `adcode` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '项目编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_default`(`dt`, `devc_type_no`, `devc_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3206121 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '交管设备可用状态-天更新' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dws_tfc_state_rid_nd_index_m
-- ----------------------------
DROP TABLE IF EXISTS `dws_tfc_state_rid_nd_index_m`;
CREATE TABLE `dws_tfc_state_rid_nd_index_m`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_month` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '000000' COMMENT '业务月',
  `rid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT 'rid',
  `rid_len` double NOT NULL DEFAULT -1 COMMENT 'rid长度',
  `road_level` int NOT NULL DEFAULT -1 COMMENT '道路等级',
  `avg_speed_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月包含等待红绿灯平均速度,km/h',
  `avg_nostop_speed_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月不包含等待红绿灯平均速度,km/h',
  `avg_travel_time_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月包含等待红绿灯平均旅行时间,秒',
  `avg_nostop_travel_time_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月不包含等待红绿灯平均旅行时间,秒',
  `benchmark_speed_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月包含等待红绿灯基准速度,km/h',
  `benchmark_nostop_speed_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月不包含等待红绿灯基准速度,km/h',
  `benchmark_travel_time_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月包含等待红绿灯基准旅行时间,秒',
  `benchmark_nostop_travel_time_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月不包含等待红绿灯基准旅行时间,秒',
  `free_speed_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月包含等待红绿灯自由流速度,km/h',
  `free_nostop_speed_3m` double NOT NULL DEFAULT 0 COMMENT '最近3个自然月不包含等待红绿灯自由流速度,km/h',
  `month` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '000000' COMMENT '月分区',
  `data_version` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '数据编码',
  `adcode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '城市编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rid`(`rid`, `data_version`, `adcode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 133746 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'rid粒度指标汇总表(速度)-月更新' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for dws_tfc_state_rid_tp_lastspeed_rt
-- ----------------------------
DROP TABLE IF EXISTS `dws_tfc_state_rid_tp_lastspeed_rt`;
CREATE TABLE `dws_tfc_state_rid_tp_lastspeed_rt`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `stat_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '00000000' COMMENT '业务时间',
  `rid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '00' COMMENT 'rid',
  `step_index` int NOT NULL DEFAULT -1 COMMENT '业务时间片序号',
  `data_step_index` int NOT NULL DEFAULT -1 COMMENT '数据时间片序号',
  `data_tp` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '2mi' COMMENT '数据指标时间片步长描述如 2mi',
  `data_step_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0000000' COMMENT '数据时间格式 yyyymmddhhmmss，和 data_stap_index 对应，如stat_time = 20180705100000, data_tp = 5mi, data_step_index = 1 ，则data_step_time = 20180704100500',
  `speed` double NOT NULL DEFAULT 0 COMMENT '包含等待红绿灯的行驶速度',
  `nostop_speed` double NOT NULL DEFAULT 0 COMMENT '不包含等待红绿灯的行驶速度',
  `travel_time` double NOT NULL DEFAULT 0 COMMENT '包含等待红绿灯的行驶的通行时间',
  `nostop_travel_time` double NOT NULL DEFAULT 0 COMMENT '不 包含等待红绿灯的行驶通行时间',
  `reliability_code` double NOT NULL DEFAULT 0 COMMENT '置信度',
  `dt` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '00000000' COMMENT '数据日期',
  `tp` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '2mi' COMMENT '计算时间片分区 tp =  2mi',
  `data_version` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '000000' COMMENT '版本号',
  `adcode` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '000000' COMMENT '城市编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rid_tptime`(`rid`, `data_step_index`, `data_tp`, `tp`, `data_version`) USING BTREE COMMENT '唯一主键'
) ENGINE = InnoDB AUTO_INCREMENT = 89736 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'rid最新速度实时表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dws_tfc_state_rid_tpwkd_index_m
-- ----------------------------
DROP TABLE IF EXISTS `dws_tfc_state_rid_tpwkd_index_m`;
CREATE TABLE `dws_tfc_state_rid_tpwkd_index_m`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `stat_month` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '业务月份',
  `rid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'rid',
  `day_of_week` tinyint NULL DEFAULT 0 COMMENT '周几',
  `step_index` int NULL DEFAULT 0 COMMENT '时间片序号',
  `avg_speed_1m` double NULL DEFAULT -1 COMMENT '本月平均等待红绿灯通行速度,km/h',
  `avg_nostop_speed_1m` double NULL DEFAULT -1 COMMENT '本月不等待红绿灯平均通行速度,km/h',
  `avg_travel_time_1m` double NULL DEFAULT -1 COMMENT '本月等待红绿灯平均旅行时间,秒',
  `avg_nostop_travel_time_1m` double NULL DEFAULT -1 COMMENT '本月等待红绿灯平均通行旅行时间,秒',
  `med_speed_1m` double NULL DEFAULT -1 COMMENT '本月等待红绿灯中位数速度,km/h',
  `med_nostop_speed_1m` double NULL DEFAULT -1 COMMENT '本月不等待红绿灯中位数速度,km/h',
  `med_travel_time_1m` double NULL DEFAULT -1 COMMENT '本月等待红绿灯中位数旅行时间,秒',
  `med_nostop_travel_time_1m` double NULL DEFAULT -1 COMMENT '本月不等待红绿灯中位数旅行时间,秒',
  `avg_speed_3m` double NULL DEFAULT -1 COMMENT '近3个月月平均等待红绿灯通行速度,km/h',
  `avg_nostop_speed_3m` double NULL DEFAULT -1 COMMENT '近3个月月不等待红绿灯平均通行速度,km/h',
  `avg_travel_time_3m` double NULL DEFAULT -1 COMMENT '近3个月月等待红绿灯平均旅行时间,秒',
  `avg_nostop_travel_time_3m` double NULL DEFAULT -1 COMMENT '近3个月月等待红绿灯平均通行旅行时间,秒',
  `med_speed_3m` double NULL DEFAULT 0 COMMENT '近3个月等待红绿灯中位数速度,km/h',
  `med_nostop_speed_3m` double NULL DEFAULT -1 COMMENT '近3个月不等待红绿灯中位数速度,km/h',
  `med_travel_time_3m` double NULL DEFAULT -1 COMMENT '近3个月等待红绿灯中位数旅行时间,秒',
  `med_nostop_travel_time_3m` double NULL DEFAULT -1 COMMENT '近3个月不等待红绿灯中位数旅行时间,秒',
  `month` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '月份',
  `tp` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '时间分片',
  `data_version` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '数据版本',
  `adcode` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '城市编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rid_tp_step`(`data_version`, `rid`, `day_of_week`, `tp`, `step_index`) USING BTREE,
  INDEX `idx_rid`(`rid`, `day_of_week`, `step_index`, `tp`) USING BTREE,
  INDEX `idx_stepIndex`(`day_of_week`, `step_index`, `tp`, `data_version`, `adcode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 89066249 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'rid粒度指标汇总表(速度)-时间分片区分周几-月更新' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dws_tfc_state_signinterfridseq_nd_index_m
-- ----------------------------
DROP TABLE IF EXISTS `dws_tfc_state_signinterfridseq_nd_index_m`;
CREATE TABLE `dws_tfc_state_signinterfridseq_nd_index_m`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID主键',
  `stat_month` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '统计月份,如201701',
  `inter_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'inter_id',
  `inter_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路口名字',
  `f_ridseq` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上游序列，第一个rid距离路口最近,最后一个rid距离路口最远',
  `f_ridseq_len` int NULL DEFAULT NULL COMMENT '上游序列长度',
  `f_rid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '进口道id',
  `rid_angle` double NULL DEFAULT NULL COMMENT 'rid相对于正北方向的夹角，0-360',
  `rid_dir_4_no` tinyint NULL DEFAULT NULL COMMENT 'rid自身角度方向的4方位编码,详见编码表',
  `rid_dir_8_no` tinyint NULL DEFAULT NULL COMMENT 'rid自身角度方向的8方位编码,详见编码表',
  `f_angle` double NULL DEFAULT NULL COMMENT 'rid进入路口相对于正北方向的夹角，0-360',
  `f_dir_4_no` tinyint NULL DEFAULT NULL COMMENT 'rid进入路口角度方向的4方位编码,详见编码表',
  `f_dir_8_no` tinyint NULL DEFAULT NULL COMMENT 'rid进入路口角度方向的8方位编码,详见编码表',
  `turn_dir_no` tinyint NULL DEFAULT NULL COMMENT '转向编号',
  `benchmark_nostop_travel_time_3m` double NULL DEFAULT NULL COMMENT '最近3个月上游rid序列不包含等到红绿灯基准旅行时间',
  `benchmark_travel_time_3m` double NULL DEFAULT NULL COMMENT '最近3个月上游rid序列包含等到红绿灯基准旅行时间',
  `benchmark_nostop_speed_3m` double NULL DEFAULT NULL COMMENT '最近3个月上游rid序列不包含等到红绿灯基准行驶速度',
  `benchmark_speed_3m` double NULL DEFAULT NULL COMMENT '最近3个月上游rid序列包含等到红绿灯基准行驶速度',
  `free_nostop_speed_3m` double NULL DEFAULT NULL COMMENT '最近3个月上游rid序列不包含等到红绿灯自由流速度',
  `free_speed_3m` double NULL DEFAULT NULL COMMENT '最近3个月上游rid序列包含等到红绿灯自由流速度',
  `month` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '月份，格式如：201701',
  `data_version` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路网数据版本',
  `adcode` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '城市编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_inter_frid_td`(`inter_id`, `f_rid`, `turn_dir_no`, `data_version`, `adcode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 184025 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '信号灯路口进口道转向粒度指标汇总(基准旅行时间、基准速度)-月更新' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dws_tfc_state_signinterfridseq_tpwkd_delaydur_m
-- ----------------------------
DROP TABLE IF EXISTS `dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`;
CREATE TABLE `dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `stat_month` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '000000' COMMENT '业务月份',
  `inter_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '路口id',
  `inter_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路口名称',
  `f_ridseq` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '进口道rid上游序列,第1个rid距离路口最近,分隔符#',
  `f_rid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '进口道rid',
  `f_dir_4_no` int NULL DEFAULT NULL COMMENT 'rid进入路口方向的4方位编码,详见编码表',
  `f_dir_8_no` int NULL DEFAULT NULL COMMENT 'rid进入路口方向的8方位编码,详见编码表',
  `turn_dir_no` int NOT NULL COMMENT '转向编号',
  `day_of_week` int NOT NULL COMMENT '周几',
  `step_index` int NOT NULL COMMENT '时间片序号',
  `avg_speed_3m` double NULL DEFAULT NULL COMMENT '近3个月等待红绿灯平均速度',
  `avg_nostop_speed_3m` double NULL DEFAULT NULL COMMENT '近3个月不等待红绿灯平均速度',
  `avg_speed_travel_time_3m` double NULL DEFAULT NULL COMMENT '近三个月平均不等待红绿灯旅行时间-取自速度表',
  `avg_speed_nostop_travel_time_3m` double NULL DEFAULT NULL COMMENT '近三个月平均不等待红绿灯旅行时间-取自速度表',
  `avg_trace_travel_time_3m` double NULL DEFAULT NULL COMMENT '近三个月平均旅行时间-使用轨迹计算',
  `avg_delay_dur_3m` double NULL DEFAULT NULL COMMENT '近三个月平均延误时间',
  `month` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '月份',
  `tp` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '时间片分区',
  `data_version` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据版本',
  `adcode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '城市编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_time_inter`(`inter_id`, `data_version`, `f_rid`, `turn_dir_no`, `day_of_week`, `step_index`) USING BTREE,
  INDEX `idx_stepIndex`(`day_of_week`, `step_index`, `tp`, `data_version`, `adcode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 151000184 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '信号灯路口进口道转向粒度指标汇总(旅行时间、延误时长)-时间分片区分周几-月更新' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dws_tfc_state_tfcdline_tp_index_d
-- ----------------------------
DROP TABLE IF EXISTS `dws_tfc_state_tfcdline_tp_index_d`;
CREATE TABLE `dws_tfc_state_tfcdline_tp_index_d`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `stat_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '业务指标时间 yyyyMMddHHmm00',
  `tfcdline_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '大脑统一线实体tfcdline_id',
  `step_index` int NULL DEFAULT -1 COMMENT '时间片序号',
  `avg_speed` double NULL DEFAULT -1 COMMENT '大脑统一线实体平均等待红绿灯速度',
  `avg_nostop_speed` double NULL DEFAULT -1 COMMENT '大脑统一线实体平均不等待红绿灯速度',
  `fw_speed` double NULL DEFAULT -1 COMMENT '当前时间片上等待红绿灯流量加权计算的等待红绿灯速度',
  `fw_nostop_speed` double NULL DEFAULT -1 COMMENT '当前时间片上等待红绿灯流量加权计算的不等待红绿灯通行速度',
  `lw_speed` double NULL DEFAULT -1 COMMENT '当前时间片上等待红绿灯rid长度加权计算的等待红绿灯速度',
  `lw_nostop_speed` double NULL DEFAULT -1 COMMENT '当前时间片上等待红绿灯rid长度加权计算的不等待红绿灯通行速度',
  `flw_speed` double NULL DEFAULT -1 COMMENT '当前时间片上等待红绿灯rid长度和流量加权计算的等待红绿灯速度',
  `flw_nostop_speed` double NULL DEFAULT -1 COMMENT '当前时间片上等待红绿灯rid长度和流量加权计算的不等待红绿灯通行速度',
  `travel_time` double NULL DEFAULT -1 COMMENT '等待红绿灯通行时间',
  `nostop_travel_time` double NULL DEFAULT -1 COMMENT '不等待红绿灯通行时间',
  `delay_dur` double NULL DEFAULT -1 COMMENT '当前时间片的延误时间,秒',
  `avg_delay_dur_km` double NULL DEFAULT -1 COMMENT '当前时间片平均每公里延误时间,秒',
  `speed_reliability_code` int NULL DEFAULT -1 COMMENT '速度置信度，取值范围为0-100',
  `speed_reliability_detail` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '速度置信度说明',
  `avg_eff_index` double NULL DEFAULT -1 COMMENT '当前时间片的平均通行效率指数',
  `effindex_reliability_code` int NULL DEFAULT -1 COMMENT '通行效率指数置信度，取值范围为0-100',
  `effindex_reliability_detail` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '通行效率置信度说明',
  `jam_state_no` int NULL DEFAULT -1 COMMENT '拥堵状态 0:不拥堵 1:一般拥堵 2:严重拥堵',
  `jam_dur` int NULL DEFAULT -1 COMMENT '拥堵时长，单位为分钟',
  `free_len` double NULL DEFAULT -1 COMMENT '畅通状态里程,米',
  `general_jam_len` double NULL DEFAULT -1 COMMENT '一般拥堵里程,米',
  `general_jam_detail` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '大脑统一线实体一般拥堵的RID明细JSON',
  `serious_jam_len` double NULL DEFAULT -1 COMMENT '严重拥堵里程,米',
  `serious_jam_detail` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '大脑统一线实体严重拥堵的RID明细JSON',
  `jam_reliability_code` int NULL DEFAULT -1 COMMENT '拥堵置信度，取值范围为0-100',
  `jam_reliability_detail` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '拥堵置信度说明',
  `free_detail` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '大脑统一线实体畅通状态的RID明细: rid1,rid2,rid3',
  `dt` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '日期yyyymmdd',
  `tp` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '时间片长度',
  `line_type_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '线类型编码 01:rdseg,02:droad,03:drdchl',
  `data_version` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '路网数据版本',
  `adcode` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '城市大脑项目交付code',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_default`(`tfcdline_id`, `dt`, `tp`, `step_index`, `data_version`, `adcode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29764801 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '大脑统一线实体tfcdline指标-区分时间片-天更新' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dws_tfc_trl_interfridlane_tp_smtmultiflow_rt
-- ----------------------------
DROP TABLE IF EXISTS `dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`;
CREATE TABLE `dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `stat_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '统计时间，yyyymmddhhmiss',
  `reliable_devc_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '置信度最高检测设备id',
  `reliable_devc_type_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '置信度最高检测设备类型编码',
  `lane_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '车道id',
  `rid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'rid',
  `inter_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '路口id，如果是路口设备，则为该路口id，如果是路段设备，则取下游1200米以内的最近路口',
  `step_index` int NULL DEFAULT -1 COMMENT '时间片序号',
  `flow` double NULL DEFAULT -1 COMMENT '流量（-1是特殊值，表示流量数据缺失且无法填充，需要特殊处理）',
  `reliability_code` double NULL DEFAULT -1 COMMENT '置信度',
  `multi_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '融合方式，使用10位开关形式，依赖数据源类型：第一位：高德、第二位：卡口、第三位：微波、第四位：线圈',
  `dt` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '日期',
  `tp` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '时间片分区 目前需要计算 tp = 5mi',
  `data_version` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '路网数据版本',
  `adcode` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '城市编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_default`(`inter_id`, `rid`, `lane_id`, `dt`, `tp`, `step_index`, `data_version`, `adcode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 884620 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '路口·进口道车道粒度融合后流量平滑结果-时间片-实时更新' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dws_ts_trl_tfcunit2tfcunit_trltypetpdoe_tripindex_d
-- ----------------------------
DROP TABLE IF EXISTS `dws_ts_trl_tfcunit2tfcunit_trltypetpdoe_tripindex_d`;
CREATE TABLE `dws_ts_trl_tfcunit2tfcunit_trltypetpdoe_tripindex_d`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `stat_date` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '业务日期,yyyymmdd',
  `tfcunit_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '交通区域ID',
  `tfcunit_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '交通区域名称',
  `od_tfcunit_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'od交通区域ID',
  `od_tfcunit_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'od交通区域名称',
  `step_index` int NULL DEFAULT -1 COMMENT '时间片序号',
  `doe_date_type` int NULL DEFAULT -1 COMMENT '全维度日期类型 -1:不区分；0：周一；1：周二；2：周三；3：周四；4：周五；5：周六；6：周日；99：工作日，100：非工作日',
  `trl_type_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '出行方式',
  `trl_reason_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '出行原因',
  `od_cnt` int NULL DEFAULT -1 COMMENT 'tfcunit到od_tfcunit的出行量',
  `reverse_od_cnt` int NULL DEFAULT -1 COMMENT 'od_tfcunit到tfcunit的出行量',
  `od_sequence_type` int NULL DEFAULT -1 COMMENT 'tfcunit_id跟od_tfcunit_id的字符排序顺序，1:tfcunit_id>od_tfcunit_id，2:tfcunit_id<od_tfcunit_id，3:tfcunit_id=od_tfcunit_id',
  `dt` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '日期类型',
  `tp` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '时间片粒度',
  `unit_type_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '区域类型',
  `od_unit_type_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'OD区域类型',
  `data_version` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '路网版本',
  `adcode` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '项目编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_default`(`tfcunit_id`, `od_tfcunit_id`, `doe_date_type`, `trl_type_no`, `trl_reason_no`, `dt`, `tp`, `unit_type_no`, `od_unit_type_no`, `data_version`, `adcode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6379901 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '交通区域OD指标分析-区分出行方式、日期类型、时间片-天更新' ROW_FORMAT = Dynamic;

-- ----------------------------
-- View structure for table1
-- ----------------------------
DROP VIEW IF EXISTS `table1`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `table1` AS select `dwd_tfc_ctl_intersignal_oper_rt`.`id` AS `id`,`dwd_tfc_ctl_intersignal_oper_rt`.`inter_id` AS `inter_id`,`dwd_tfc_ctl_intersignal_oper_rt`.`inter_name` AS `inter_name`,`dwd_tfc_ctl_intersignal_oper_rt`.`cycle_start_time` AS `cycle_start_time`,`dwd_tfc_ctl_intersignal_oper_rt`.`start_time` AS `start_time`,`dwd_tfc_ctl_intersignal_oper_rt`.`end_time` AS `end_time`,`dwd_tfc_ctl_intersignal_oper_rt`.`phase_plan_id` AS `phase_plan_id`,`dwd_tfc_ctl_intersignal_oper_rt`.`time_plan_id` AS `time_plan_id`,`dwd_tfc_ctl_intersignal_oper_rt`.`phase_name` AS `phase_name`,`dwd_tfc_ctl_intersignal_oper_rt`.`split_time` AS `split_time`,`dwd_tfc_ctl_intersignal_oper_rt`.`cycle_time` AS `cycle_time`,`dwd_tfc_ctl_intersignal_oper_rt`.`green_time` AS `green_time`,`dwd_tfc_ctl_intersignal_oper_rt`.`source` AS `source`,`dwd_tfc_ctl_intersignal_oper_rt`.`dt` AS `dt`,`dwd_tfc_ctl_intersignal_oper_rt`.`data_version` AS `data_version`,`dwd_tfc_ctl_intersignal_oper_rt`.`adcode` AS `adcode` from `dwd_tfc_ctl_intersignal_oper_rt` limit 10000;

-- ----------------------------
-- View structure for table2
-- ----------------------------
DROP VIEW IF EXISTS `table2`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `table2` AS select `dws_tfc_state_rid_nd_index_m`.`id` AS `id`,`dws_tfc_state_rid_nd_index_m`.`stat_month` AS `stat_month`,`dws_tfc_state_rid_nd_index_m`.`rid` AS `rid`,`dws_tfc_state_rid_nd_index_m`.`rid_len` AS `rid_len`,`dws_tfc_state_rid_nd_index_m`.`road_level` AS `road_level`,`dws_tfc_state_rid_nd_index_m`.`avg_speed_3m` AS `avg_speed_3m`,`dws_tfc_state_rid_nd_index_m`.`avg_nostop_speed_3m` AS `avg_nostop_speed_3m`,`dws_tfc_state_rid_nd_index_m`.`avg_travel_time_3m` AS `avg_travel_time_3m`,`dws_tfc_state_rid_nd_index_m`.`avg_nostop_travel_time_3m` AS `avg_nostop_travel_time_3m`,`dws_tfc_state_rid_nd_index_m`.`benchmark_speed_3m` AS `benchmark_speed_3m`,`dws_tfc_state_rid_nd_index_m`.`benchmark_nostop_speed_3m` AS `benchmark_nostop_speed_3m`,`dws_tfc_state_rid_nd_index_m`.`benchmark_travel_time_3m` AS `benchmark_travel_time_3m`,`dws_tfc_state_rid_nd_index_m`.`benchmark_nostop_travel_time_3m` AS `benchmark_nostop_travel_time_3m`,`dws_tfc_state_rid_nd_index_m`.`free_speed_3m` AS `free_speed_3m`,`dws_tfc_state_rid_nd_index_m`.`free_nostop_speed_3m` AS `free_nostop_speed_3m`,`dws_tfc_state_rid_nd_index_m`.`month` AS `month`,`dws_tfc_state_rid_nd_index_m`.`data_version` AS `data_version`,`dws_tfc_state_rid_nd_index_m`.`adcode` AS `adcode` from `dws_tfc_state_rid_nd_index_m` limit 10000;

-- ----------------------------
-- View structure for table3
-- ----------------------------
DROP VIEW IF EXISTS `table3`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `table3` AS select `dws_tfc_state_rid_tp_lastspeed_rt`.`id` AS `id`,`dws_tfc_state_rid_tp_lastspeed_rt`.`stat_time` AS `stat_time`,`dws_tfc_state_rid_tp_lastspeed_rt`.`rid` AS `rid`,`dws_tfc_state_rid_tp_lastspeed_rt`.`step_index` AS `step_index`,`dws_tfc_state_rid_tp_lastspeed_rt`.`data_step_index` AS `data_step_index`,`dws_tfc_state_rid_tp_lastspeed_rt`.`data_tp` AS `data_tp`,`dws_tfc_state_rid_tp_lastspeed_rt`.`data_step_time` AS `data_step_time`,`dws_tfc_state_rid_tp_lastspeed_rt`.`speed` AS `speed`,`dws_tfc_state_rid_tp_lastspeed_rt`.`nostop_speed` AS `nostop_speed`,`dws_tfc_state_rid_tp_lastspeed_rt`.`travel_time` AS `travel_time`,`dws_tfc_state_rid_tp_lastspeed_rt`.`nostop_travel_time` AS `nostop_travel_time`,`dws_tfc_state_rid_tp_lastspeed_rt`.`reliability_code` AS `reliability_code`,`dws_tfc_state_rid_tp_lastspeed_rt`.`dt` AS `dt`,`dws_tfc_state_rid_tp_lastspeed_rt`.`tp` AS `tp`,`dws_tfc_state_rid_tp_lastspeed_rt`.`data_version` AS `data_version`,`dws_tfc_state_rid_tp_lastspeed_rt`.`adcode` AS `adcode` from `dws_tfc_state_rid_tp_lastspeed_rt` limit 10000;

-- ----------------------------
-- View structure for table4
-- ----------------------------
DROP VIEW IF EXISTS `table4`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `table4` AS select `dws_tfc_state_rid_tpwkd_index_m`.`id` AS `id`,`dws_tfc_state_rid_tpwkd_index_m`.`stat_month` AS `stat_month`,`dws_tfc_state_rid_tpwkd_index_m`.`rid` AS `rid`,`dws_tfc_state_rid_tpwkd_index_m`.`day_of_week` AS `day_of_week`,`dws_tfc_state_rid_tpwkd_index_m`.`step_index` AS `step_index`,`dws_tfc_state_rid_tpwkd_index_m`.`avg_speed_1m` AS `avg_speed_1m`,`dws_tfc_state_rid_tpwkd_index_m`.`avg_nostop_speed_1m` AS `avg_nostop_speed_1m`,`dws_tfc_state_rid_tpwkd_index_m`.`avg_travel_time_1m` AS `avg_travel_time_1m`,`dws_tfc_state_rid_tpwkd_index_m`.`avg_nostop_travel_time_1m` AS `avg_nostop_travel_time_1m`,`dws_tfc_state_rid_tpwkd_index_m`.`med_speed_1m` AS `med_speed_1m`,`dws_tfc_state_rid_tpwkd_index_m`.`med_nostop_speed_1m` AS `med_nostop_speed_1m`,`dws_tfc_state_rid_tpwkd_index_m`.`med_travel_time_1m` AS `med_travel_time_1m`,`dws_tfc_state_rid_tpwkd_index_m`.`med_nostop_travel_time_1m` AS `med_nostop_travel_time_1m`,`dws_tfc_state_rid_tpwkd_index_m`.`avg_speed_3m` AS `avg_speed_3m`,`dws_tfc_state_rid_tpwkd_index_m`.`avg_nostop_speed_3m` AS `avg_nostop_speed_3m`,`dws_tfc_state_rid_tpwkd_index_m`.`avg_travel_time_3m` AS `avg_travel_time_3m`,`dws_tfc_state_rid_tpwkd_index_m`.`avg_nostop_travel_time_3m` AS `avg_nostop_travel_time_3m`,`dws_tfc_state_rid_tpwkd_index_m`.`med_speed_3m` AS `med_speed_3m`,`dws_tfc_state_rid_tpwkd_index_m`.`med_nostop_speed_3m` AS `med_nostop_speed_3m`,`dws_tfc_state_rid_tpwkd_index_m`.`med_travel_time_3m` AS `med_travel_time_3m`,`dws_tfc_state_rid_tpwkd_index_m`.`med_nostop_travel_time_3m` AS `med_nostop_travel_time_3m`,`dws_tfc_state_rid_tpwkd_index_m`.`month` AS `month`,`dws_tfc_state_rid_tpwkd_index_m`.`tp` AS `tp`,`dws_tfc_state_rid_tpwkd_index_m`.`data_version` AS `data_version`,`dws_tfc_state_rid_tpwkd_index_m`.`adcode` AS `adcode` from `dws_tfc_state_rid_tpwkd_index_m` limit 10000;

-- ----------------------------
-- View structure for table5
-- ----------------------------
DROP VIEW IF EXISTS `table5`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `table5` AS select `dws_tfc_state_rid_tpwkd_index_m_copy1`.`id` AS `id`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`stat_month` AS `stat_month`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`rid` AS `rid`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`day_of_week` AS `day_of_week`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`step_index` AS `step_index`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`avg_speed_1m` AS `avg_speed_1m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`avg_nostop_speed_1m` AS `avg_nostop_speed_1m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`avg_travel_time_1m` AS `avg_travel_time_1m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`avg_nostop_travel_time_1m` AS `avg_nostop_travel_time_1m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`med_speed_1m` AS `med_speed_1m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`med_nostop_speed_1m` AS `med_nostop_speed_1m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`med_travel_time_1m` AS `med_travel_time_1m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`med_nostop_travel_time_1m` AS `med_nostop_travel_time_1m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`avg_speed_3m` AS `avg_speed_3m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`avg_nostop_speed_3m` AS `avg_nostop_speed_3m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`avg_travel_time_3m` AS `avg_travel_time_3m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`avg_nostop_travel_time_3m` AS `avg_nostop_travel_time_3m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`med_speed_3m` AS `med_speed_3m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`med_nostop_speed_3m` AS `med_nostop_speed_3m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`med_travel_time_3m` AS `med_travel_time_3m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`med_nostop_travel_time_3m` AS `med_nostop_travel_time_3m`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`month` AS `month`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`tp` AS `tp`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`data_version` AS `data_version`,`dws_tfc_state_rid_tpwkd_index_m_copy1`.`adcode` AS `adcode` from `dws_tfc_state_rid_tpwkd_index_m_copy1` limit 10000;

-- ----------------------------
-- View structure for table6
-- ----------------------------
DROP VIEW IF EXISTS `table6`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `table6` AS select `dws_tfc_state_signinterfridseq_nd_index_m`.`id` AS `id`,`dws_tfc_state_signinterfridseq_nd_index_m`.`stat_month` AS `stat_month`,`dws_tfc_state_signinterfridseq_nd_index_m`.`inter_id` AS `inter_id`,`dws_tfc_state_signinterfridseq_nd_index_m`.`inter_name` AS `inter_name`,`dws_tfc_state_signinterfridseq_nd_index_m`.`f_ridseq` AS `f_ridseq`,`dws_tfc_state_signinterfridseq_nd_index_m`.`f_ridseq_len` AS `f_ridseq_len`,`dws_tfc_state_signinterfridseq_nd_index_m`.`f_rid` AS `f_rid`,`dws_tfc_state_signinterfridseq_nd_index_m`.`rid_angle` AS `rid_angle`,`dws_tfc_state_signinterfridseq_nd_index_m`.`rid_dir_4_no` AS `rid_dir_4_no`,`dws_tfc_state_signinterfridseq_nd_index_m`.`rid_dir_8_no` AS `rid_dir_8_no`,`dws_tfc_state_signinterfridseq_nd_index_m`.`f_angle` AS `f_angle`,`dws_tfc_state_signinterfridseq_nd_index_m`.`f_dir_4_no` AS `f_dir_4_no`,`dws_tfc_state_signinterfridseq_nd_index_m`.`f_dir_8_no` AS `f_dir_8_no`,`dws_tfc_state_signinterfridseq_nd_index_m`.`turn_dir_no` AS `turn_dir_no`,`dws_tfc_state_signinterfridseq_nd_index_m`.`benchmark_nostop_travel_time_3m` AS `benchmark_nostop_travel_time_3m`,`dws_tfc_state_signinterfridseq_nd_index_m`.`benchmark_travel_time_3m` AS `benchmark_travel_time_3m`,`dws_tfc_state_signinterfridseq_nd_index_m`.`benchmark_nostop_speed_3m` AS `benchmark_nostop_speed_3m`,`dws_tfc_state_signinterfridseq_nd_index_m`.`benchmark_speed_3m` AS `benchmark_speed_3m`,`dws_tfc_state_signinterfridseq_nd_index_m`.`free_nostop_speed_3m` AS `free_nostop_speed_3m`,`dws_tfc_state_signinterfridseq_nd_index_m`.`free_speed_3m` AS `free_speed_3m`,`dws_tfc_state_signinterfridseq_nd_index_m`.`month` AS `month`,`dws_tfc_state_signinterfridseq_nd_index_m`.`data_version` AS `data_version`,`dws_tfc_state_signinterfridseq_nd_index_m`.`adcode` AS `adcode` from `dws_tfc_state_signinterfridseq_nd_index_m` limit 10000;

-- ----------------------------
-- View structure for table7
-- ----------------------------
DROP VIEW IF EXISTS `table7`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `table7` AS select `dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`id` AS `id`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`stat_month` AS `stat_month`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`inter_id` AS `inter_id`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`inter_name` AS `inter_name`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`f_ridseq` AS `f_ridseq`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`f_rid` AS `f_rid`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`f_dir_4_no` AS `f_dir_4_no`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`f_dir_8_no` AS `f_dir_8_no`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`turn_dir_no` AS `turn_dir_no`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`day_of_week` AS `day_of_week`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`step_index` AS `step_index`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`avg_speed_3m` AS `avg_speed_3m`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`avg_nostop_speed_3m` AS `avg_nostop_speed_3m`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`avg_speed_travel_time_3m` AS `avg_speed_travel_time_3m`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`avg_speed_nostop_travel_time_3m` AS `avg_speed_nostop_travel_time_3m`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`avg_trace_travel_time_3m` AS `avg_trace_travel_time_3m`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`avg_delay_dur_3m` AS `avg_delay_dur_3m`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`month` AS `month`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`tp` AS `tp`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`data_version` AS `data_version`,`dws_tfc_state_signinterfridseq_tpwkd_delaydur_m`.`adcode` AS `adcode` from `dws_tfc_state_signinterfridseq_tpwkd_delaydur_m` limit 10000;

-- ----------------------------
-- View structure for table8
-- ----------------------------
DROP VIEW IF EXISTS `table8`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `table8` AS select `dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`id` AS `id`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`stat_time` AS `stat_time`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`reliable_devc_id` AS `reliable_devc_id`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`reliable_devc_type_no` AS `reliable_devc_type_no`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`lane_id` AS `lane_id`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`rid` AS `rid`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`inter_id` AS `inter_id`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`step_index` AS `step_index`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`flow` AS `flow`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`reliability_code` AS `reliability_code`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`multi_type` AS `multi_type`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`dt` AS `dt`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`tp` AS `tp`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`data_version` AS `data_version`,`dws_tfc_trl_interfridlane_tp_smtmultiflow_rt`.`adcode` AS `adcode` from `dws_tfc_trl_interfridlane_tp_smtmultiflow_rt` limit 10000;

SET FOREIGN_KEY_CHECKS = 1;


=================附加两张表=====================

CREATE TABLE `dwd_tfc_ctl_signal_phasedir` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `inter_id` varchar(32) NOT NULL COMMENT '路口id',
  `inter_name` varchar(64) NOT NULL COMMENT '路口名称',
  `phase_plan_id` varchar(32) NOT NULL COMMENT '相位方案号',
  `phase_name` varchar(32) NOT NULL COMMENT '相位名称/阶段名称，具体以信号机可接收的为准',
  `dir_name` varchar(32) DEFAULT NULL COMMENT '通行方向，如东-直行',
  `f_rid` varchar(64) NOT NULL COMMENT '进口道id',
  `t_rid` varchar(64) NOT NULL COMMENT '出口道rid',
  `f_dir_4_no` tinyint(4) NOT NULL COMMENT '进口道进入路口方向的4方位编码，详见编码表',
  `f_dir_8_no` tinyint(4) NOT NULL COMMENT '进口道进入路口方向的8方位编码，详见编码表',
  `turn_dir_no` tinyint(4) NOT NULL COMMENT '转向，详见编码表',
  `modified_date` varchar(32) NOT NULL COMMENT '修改时间,yyyymmdd',
  `source` varchar(32) NOT NULL COMMENT '数据来源，系统化接入或者人工录入等',
  `start_date` varchar(32) NOT NULL COMMENT '开始时间,yyyymmdd',
  `data_version` varchar(32) NOT NULL COMMENT '路网数据版本',
  `adcode` varchar(32) NOT NULL COMMENT '城市编码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk` (`inter_id`,`f_rid`,`t_rid`,`turn_dir_no`,`phase_plan_id`,`phase_name`,`data_version`,`adcode`)
) ENGINE=InnoDB AUTO_INCREMENT=495 DEFAULT CHARSET=utf8 COMMENT='路口相位通行方向-最新可用数据表';

CREATE TABLE `dwd_tfc_rltn_wide_inter_lane` (
  `inter_id` varchar(11) NOT NULL COMMENT '路口id',
  `inter_name` varchar(200) DEFAULT NULL COMMENT '路口名称',
  `lane_id` varchar(30) NOT NULL COMMENT '车道id',
  `lane_no` varchar(10) DEFAULT NULL COMMENT '车道号',
  `turn_dir_no_list` varchar(32) DEFAULT NULL COMMENT '车道转向list，车道转向集合，逗号分隔，例如：1,3,4',
  `lane_angle` double DEFAULT NULL COMMENT '车道相对于正北方向的夹角，0-360',
  `ft_type_no` tinyint(4) DEFAULT NULL COMMENT '进口车道标识 1进口道，2出口道',
  `rid` varchar(23) DEFAULT NULL COMMENT '所属r_id',
  `rid_angle` double DEFAULT NULL COMMENT 'RID相对于正北方向的夹角，0-360',
  `rid_dir_4_no` tinyint(4) DEFAULT NULL COMMENT '路口进出口道4方向编码',
  `rid_dir_8_no` tinyint(4) DEFAULT NULL COMMENT '路口进出口道8方向编码',
  `ft_dir_4_no` tinyint(4) DEFAULT NULL COMMENT 'rid进入(离开)路口方向的4方位编码,详见编码表',
  `ft_dir_8_no` tinyint(4) DEFAULT NULL COMMENT 'rid进入(离开)路口方向的8方位编码,详见编码表',
  `lane_sdtype_no` tinyint(4) DEFAULT NULL COMMENT '标清车道类别:1.路段中车道;2.进口道（进入路口的车道）;3.出口道（驶出路口的车道）',
  `lane_hdtype_no` tinyint(4) DEFAULT NULL COMMENT '高清车道类型:1.机动车道2.非机动车道3.潮汐车道4.可变车道5.公交车道6.应急车道7.左转弯待转区8.对向可变车道',
  `data_version` varchar(8) NOT NULL COMMENT '路网数据版本',
  `adcode` varchar(20) NOT NULL COMMENT '城市编码',
  PRIMARY KEY (`inter_id`,`lane_id`,`data_version`,`adcode`),
  KEY `idx_interid_rid` (`inter_id`,`adcode`,`data_version`,`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='路口车道关系表';