package cn.edu.neu.citybrain.function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.edu.neu.citybrain.dto.InterLaneInfo;
import cn.edu.neu.citybrain.dto.InterSignalOperPlan;
import cn.edu.neu.citybrain.dto.PhaseInfo;
import cn.edu.neu.citybrain.util.SignalOptConstant;
import cn.edu.neu.citybrain.util.StaticConstantClass;

public class CommonFunction {
	public static String StrBinaOr(String str1, String str2) {
		StringBuffer resultBuffer = new StringBuffer();

		assert str1.length() == str1.length();

		for (int i = 0; i < str1.length(); ++i) {
			int value1 = Integer.parseInt(String.valueOf(str1.charAt(i)));
			int value2 = Integer.parseInt(String.valueOf(str2.charAt(i)));
			int orResult = value1 | value2;
			resultBuffer.append(String.valueOf(orResult));
		}

		String resultStr = resultBuffer.toString();
		return resultStr;
	}

	public static String getNextDay(String dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		try {
			Date today = sdf.parse(dt);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(today);
			calendar.add(5, 1);
			Date nextDay = calendar.getTime();
			return sdf.format(nextDay);
		} catch (ParseException var5) {
			throw new RuntimeException(var5);
		}
	}

	public static long getSplitTime(long startTime, long endTime,
			long phaseStartTimeStamp, long cycleTime, long cycleSplitTime,
			double greenLostTime) {
		long totalSplitTime = 0L;
		long splitStartPos = (phaseStartTimeStamp - startTime) / 1000L
				% cycleTime;
		long totalSecs = (endTime - startTime) / 1000L;
		long splitEndPos = splitStartPos + cycleSplitTime;
		if (greenLostTime > (double) cycleSplitTime) {
			greenLostTime = (double) cycleSplitTime;
		}

		while (splitEndPos <= totalSecs) {
			totalSplitTime += cycleSplitTime - (long) greenLostTime;
			splitStartPos += cycleTime;
			splitEndPos = splitStartPos + cycleSplitTime;
		}

		if (splitEndPos - totalSecs < cycleSplitTime) {
			totalSplitTime += splitEndPos - totalSecs
					- (long) (greenLostTime / 2.0D);
		}

		return totalSplitTime;
	}

	public static boolean isIntersection(String turnNoList1, String turnNoList2) {
		if (!"".equals(turnNoList1) && !"".equals(turnNoList2)) {
			String[] turnNo = turnNoList1.split(",");

			for (int i = 0; i < turnNo.length; ++i) {
				if (turnNoList2.contains(turnNo[i])) {
					return true;
				}
			}
		}

		return false;
	}

	public static long convertStepIndex(long oriStepIndex, long oriWinsize,
			long newWinsize) {
		long newStepIndex = 0L;
		newStepIndex = oriStepIndex * oriWinsize / newWinsize;
		return newStepIndex;
	}

	public static long getWinsize(String tp) {
		long winSize = 0L;
		if (tp.contains("mi")) {
			winSize = Long.parseLong(tp.replace("mi", ""));
			winSize *= 60L;
		}

		if (tp.contains("h")) {
			winSize = Long.parseLong(tp.replace("h", ""));
			winSize *= 3600L;
		}

		return winSize;
	}

	public static Double trimTo(Double d, int n) {
		if (d == null || Double.isNaN(d))
			return null;
		double p = Math.pow(10.0D, (double) n);
		return (double) Math.round(d * p) / p;
	}

	public static boolean isDoubleEquals(double d1, double d2) {
		double diff = Math.abs(d1 - d2);
		return diff <= 1.0E-15D;
	}

	public static double getTravelTime(double len, double speed) {
		return len / speed * 3.6D;
	}

	public static double getSpeed(double len, double traveTime) {
		return len / traveTime * 3.6D;
	}

	public static double calStopTime(double speed, double noStopSpeed,
			double len) {
		double travelTime = 0.0D;
		double nostopTravelTime = 0.0D;
		if (speed > 0.0D) {
			travelTime = getTravelTime(len, speed);
		}

		if (noStopSpeed > 0.0D) {
			nostopTravelTime = getTravelTime(len, nostopTravelTime);
		}

		double stopTime = travelTime - nostopTravelTime;
		if (stopTime < 0.0D) {
			stopTime = 0.0D;
		}

		return stopTime;
	}

	public static double corrReliabity(double oriReliability,
			double upperLimmit, double lowerLimit) {
		if (oriReliability < 0.0D) {
			return oriReliability;
		} else {
			double correctValue = lowerLimit + (upperLimmit - lowerLimit)
					/ 100.0D * oriReliability;
			return correctValue;
		}
	}

	public static String getSrcType(long devcTypeNo) {
		StringBuffer srcType = new StringBuffer("0000000000");
		if (devcTypeNo == StaticConstantClass.DEVCTYPENO_AMAP) {
			srcType.replace(0, 1, "1");
		} else if (devcTypeNo == StaticConstantClass.DEVCTYPENO_BAYONET) {
			srcType.replace(1, 2, "1");
		}

		if (devcTypeNo == StaticConstantClass.DEVCTYPENO_COIL) {
			srcType.replace(3, 4, "1");
		} else if (devcTypeNo == StaticConstantClass.DEVCTYPENO_RADAR) {
			srcType.replace(4, 5, "1");
		} else if (devcTypeNo == StaticConstantClass.DEVCTYPENO_MULTIFUNCTIONCAMERA) {
			srcType.replace(5, 6, "1");
		} else if (devcTypeNo == StaticConstantClass.DEVCTYPENO_EPOLICE) {
			srcType.replace(6, 7, "1");
		} else if (devcTypeNo == StaticConstantClass.DEVCTYPENO_SIGNAL) {
			srcType.replace(7, 8, "1");
		} else if (devcTypeNo == StaticConstantClass.DEVCTYPENO_CAMARA) {
			srcType.replace(8, 9, "1");
		}

		return srcType.toString();
	}

	public static double getPcuByVhcType(String vhcType) {
		if ("1".equals(vhcType)) {
			return 1.0D;
		} else if ("2".equals(vhcType)) {
			return 2.0D;
		} else if ("3".equals(vhcType)) {
			return 2.5D;
		} else {
			return "4".equals(vhcType) ? 3.0D : 1.0D;
		}
	}
	
	public static double getDefaultLaneSplit(String turnDirNoList,
											 Map<String, List<PhaseInfo>> interPhaseInfo, Map<String, Set<PhaseInfo>> lanePhaseInfo) {
		if(interPhaseInfo == null || interPhaseInfo.isEmpty() || lanePhaseInfo == null || lanePhaseInfo.isEmpty()) {
			String turnRight = "" + SignalOptConstant.TURN_DIR_NO_RIGHT;
			// 如果是纯右转车道且不受灯控 绿信比为1
			return turnRight.equals(turnDirNoList) ? 1 : -1;
		}
		
		double split = -1;
        for(Entry<String, List<PhaseInfo>> onePhasePlan : interPhaseInfo.entrySet()) {
            String phasePlanId = onePhasePlan.getKey();
            Set<PhaseInfo> phases = lanePhaseInfo.get(phasePlanId);
            if (phases == null || phases.isEmpty())
            	continue;
            
            double ratio = 1.0 * phases.size() / onePhasePlan.getValue().size();
            split = Math.max(split, ratio);
        }
        
        return split;
	}
	
    public static double getLaneSplitRatio(InterLaneInfo oneLaneInfo,
										   Map<String, List<PhaseInfo>> interPhaseInfo, Map<String, Set<PhaseInfo>> lanePhaseInfo,
										   List<InterSignalOperPlan> allInterSignalOper) {
        double split = -1.0;
        if (allInterSignalOper == null || allInterSignalOper.isEmpty()) {
        	split = getDefaultLaneSplit(oneLaneInfo.getTurnDirNoList(), interPhaseInfo, lanePhaseInfo);
        } else {
        	double splitTime = 0;
            double cycleTime = allInterSignalOper.get(0).getCycleTime();
            //使用信号配时数据计算绿信比
            String phasePlanId = allInterSignalOper.get(0).getPhaseInfo().getPhasePlanId();
            if (null != lanePhaseInfo && lanePhaseInfo.containsKey(phasePlanId)) {
                Set<PhaseInfo> currLanePhaseInfo = lanePhaseInfo.get(phasePlanId);
                for (PhaseInfo onePhase : currLanePhaseInfo) {
                    for (InterSignalOperPlan oneOper : allInterSignalOper) {
                        if (onePhase.getPhaseName().equals(oneOper.getPhaseName())) {
                            splitTime += oneOper.getSplitTime();
                            break;
                        }
                    }
                }
                if (splitTime > 0 && splitTime <= cycleTime) {
                    split = 1.0 * splitTime / cycleTime;
                }
            }
        }
        return split;
    }
}