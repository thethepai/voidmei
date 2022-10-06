package prog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import parser.blkx.engineLoad;
import parser.flightLog;
import parser.indicators;
import parser.state;
import sun.net.www.http.HttpClient;

public class service implements Runnable {
	public static calcHelper cH = new calcHelper();
	public calcHelper.simpleMovingAverage diffSpeedSMA;
	public calcHelper.simpleMovingAverage sepSMA;
	public calcHelper.simpleMovingAverage turnrdsSMA;
	public calcHelper.simpleMovingAverage sumSpeedSMA;
	public calcHelper.simpleMovingAverage calcSpeedSMA;
	public calcHelper.simpleMovingAverage fuelTimeSMA;

	public calcHelper.simpleMovingAverage energyDiffSMA;
	// public static URL urlstate;
	// public static URL urlindicators;

	public double energyJKg;
	public double pEnergyJKg;
	public long calcPeriod;
	public static String buf;
	public static final double g = 9.80;
	public long timeStamp;
	public long freq;
	public state sState;
	public indicators sIndic;
	public String sStatus;
	public String sTime;
	public int iTotalHp;
	public String sTotalHp;
	public int iTotalHpEff;
	public String sTotalHpEff;
	public boolean bUnitMHp;
	public int iTotalThr;
	public String sTotalThr;
	public double fTotalFuel;
	public double fTotalFuelP;
	public boolean bLowAccFuel;
	public String sTotalFuel;
	public int iCheckAlt;
	public double dfuel;
	public long fueltime;
	public String sfueltime;
	public boolean notCheckInch;
	// public boolean isFuelpressure;
	public boolean altperCirclflag;
	public long intv;
	public double althour;
	public double altperCircle;
	public double alt;
	public double altp;
	public double altreg;
	public double iastotascoff;
	public long SystemTime;
	public long TimeIncrMili;
	long MainCheckMili;
	long FuelCheckMili;
	public double fuelChange;
	long FuelLastchangeMili;
	long FuelchangeTime;
	long GCCheckMili;
	long SlowCheckMili;
	long intvCheckMili;
	long startTime;
	public long elapsedTime;

	public double noilTemp;
	public double nwaterTemp;
	// public int enginenum;
	// public int enginetype;

	public double speedv;
	public double speedvp;
	public double IASv;
	public double IASvp;
	public double diffspeed;
	public double acceleration;
	public double SEP;

	public long wepTime;

	public String salt;
	public String sSEP;

	public String sNitro;
	public String sWepTime;

	public controller c;

	// 对飞机结构有重大影响的警告
	public Boolean fatalWarn = false;

	// sState转换后
	public boolean hasWingSweepVario;
	public boolean isStateJet;

	public String svalid;
	public String engineNum;
	public String engineType;
	public String aileron;
	public String elevator;
	public String rudder;
	public String flaps;
	public String gear;
	public String TAS;
	public String IAS;
	public String M;
	public String AoA;
	public String AoS;
	public String Ao;
	public String Ny;
	public String Vy;
	public String Wx;
	public String sN;
	public String throttle;
	public String RPMthrottle;
	public String radiator;
	public String mixture;
	public String compass;
	public String sAcc;
	public String sTurnRds;
	public String sWingSweep;
	public String sTurnRate;
	public String compressorstage;
	public String magenato;
	public String power[];
	public String manifoldpressure;
	public String pressurePounds;
	public String pressureInchHg;
	public String pressureMmHg;
	public String watertemp;
	public String oiltemp;
	public String pitch[];
	public String thrust[];
	public String aclrt;
	public int curLoad;
	public double curLoadMinWorkTime;
	public String efficiency[];
	long testCheckMili;

	public long loadWorkTimeMili[];

	public double ratio;
	public double ratio_1;
	// iIndic
	public String rpm;
	private int curWLoad;
	private int curOLoad;
	private double nVy;
	public String sHorizontalLoad;
	public String sEngWorkTime;
	public String sPitchUp;
	public String sThurstPercent;
	public String sfuelPercent;
	public String sAvgEff;
	public String SdThrustPercent;
	public String sRadioAlt;

	public double radioAlt;
	public double pRadioAlt;
	public double dRadioAlt;
	public double An;
	public int iEngType;

	Boolean portOcupied = false;
	private int checkEngineType;
	private int checkPitch;
	public boolean checkEngineFlag;
	public static final int ENGINE_TYPE_PROP = 0;
	public static final int ENGINE_TYPE_JET = 1;
	public static final int ENGINE_TYPE_TURBOPROP = 2;
	public static final int ENGINE_TYPE_UNKNOWN = -1;

	public String NumtoString(int Num, int arg) {
		return String.format("%0" + arg + "d", Num);
	}

	public String NumtoString(double Num, int arg1, int arg2) {
		return String.format("%-" + arg1 + "." + arg2 + "f", Num);
	}

	public static final String nastring = "-";
	public static final String nullstring = "";
	public boolean playerLive;

	public boolean isPlayerLive() {
		return playerLive;
	}

	public static final String pressureUnit = "Ata";

	public void transtoString() {

		// 数据转换格式
		// sState

		//
		// if (iIndic.fuelpressure == true)
		// isFuelpressure = true;

		// if(sState.throttle <= 100)
		throttle = String.format("%d", sState.throttle);
		// else
		// throttle = "WEP";
		aileron = String.format("%d", sState.aileron);
		elevator = String.format("%d", sState.elevator);
		rudder = String.format("%d", sState.rudder);

		sTime = String.format("%.2f", elapsedTime / 1000f);
		if (fueltime <= 0 || fueltime > 24 * 3600 * 1000)
			sfueltime = nastring;
		else {
			// if (fueltime < 60 * 1000)
			// sfueltime = String.format(".%d", fueltime / 1000);
			// else

			// sfueltime = String.format("%d:%02d", fueltime / 60000, (int)
			// ((fueltime / 1000) % 60 ));
			if (fueltime / 60000 < 100 && !bLowAccFuel) {
				sfueltime = String.format("%d:%02d", fueltime / 60000, (long) ((fueltime / 1000) % 60 / 10) * 10);
				// sfueltime = String.format("%d.%d", fueltime / 60000,
				// (fueltime % 60000) / 6000);
			} else
				sfueltime = String.format("%d", fueltime / 60000);

		}
		sTotalThr = String.format("%d", iTotalThr);
		if (iTotalHp == 0)
			sTotalHp = nastring;
		else
			sTotalHp = String.format("%d", iTotalHp);

		rpm = String.format("%d", (int) sState.RPM);
		if (iTotalHpEff >= 100000) {
			bUnitMHp = true;
			sTotalHpEff = String.format("%.2f", iTotalHpEff / 1000000.0f);
		} else {
			bUnitMHp = false;
			sTotalHpEff = String.format("%d", iTotalHpEff);
		}
		if (sState.efficiency[0] == 0)
			efficiency[0] = nastring;
		else
			efficiency[0] = String.format("%.0f", sState.efficiency[0]);
		// if (sState.watertemp == -65535) {
		// // app.debugPrint(iIndic.engine_temperature);
		// watertemp = String.format("%.0f", 0.0);
		// if (iIndic.water_temperature != -65535)
		// watertemp = String.format("%.0f", iIndic.water_temperature);
		// if (iIndic.engine_temperature != -65535)
		// watertemp = String.format("%.0f", iIndic.engine_temperature);
		//
		// } else {
		// watertemp = String.format("%.0f", sState.watertemp);
		// }
		if (nwaterTemp != -65535)
			watertemp = String.format("%.0f", nwaterTemp);
		else
			watertemp = nastring;
		oiltemp = String.format("%.0f", noilTemp);
		if (sState.manifoldpressure != 1) {
			manifoldpressure = String.format("%.2f", sState.manifoldpressure);
			pressurePounds = String.format("%+d", Math.round((sState.manifoldpressure - 1f) * 14.696f));
			// pressurePounds = String.format("%+d",
			// Math.round((sState.manifoldpressure - 1f) * 14.696f),
			// Math.round(sState.manifoldpressure * 760f / 25.4f));
			// Math.round(sState.manifoldpressure * 760f / 25.4f)
			// pressureMmHg = String.format("%d",
			// Math.round(sState.manifoldpressure));
			pressureInchHg = String.format("P/%d''", Math.round(sState.manifoldpressure * 760f / 25.4f));
		} else {
			manifoldpressure = nastring;
			pressurePounds = nastring;
			pressureMmHg = nastring;
			pressureInchHg = nastring;
		}
		sTotalFuel = String.format("%.0f", fTotalFuel);
		if (sState.pitch[0] != -65535)
			pitch[0] = String.format("%.1f", sState.pitch[0]);
		else
			pitch[0] = nastring;
		if (sState.RPMthrottle >= 0)
			RPMthrottle = String.format("%d", sState.RPMthrottle);
		else {
			RPMthrottle = nastring;
		}
		sThurstPercent = String.format("%.0f", thurstPercent);
		SdThrustPercent = String.format("%.0f", tEngResponse);

		if (sState.radiator >= 0)
			radiator = String.format("%d", sState.radiator);
		else
			radiator = nastring;

		if (sState.mixture >= 0)
			mixture = String.format("%d", sState.mixture);
		else {
			mixture = nastring;
		}
		flaps = String.format("%d", sState.flaps);
		sfuelPercent = String.format("%d", fuelPercent);
		if (hasWingSweepVario) {
			sWingSweep = String.format("%.0f", sIndic.wsweep_indicator * 100.f);
			// app.debugPrint(sWingSweep);
		} else
			sWingSweep = nastring;
		if (sIndic.radio_altitude >= 0) {
			sRadioAlt = String.format("%.0f", radioAlt);
		} else {
			sRadioAlt = nastring;
		}
		//
		if (avgeff == 0)
			sAvgEff = nastring;
		else
			sAvgEff = String.format("%d", Math.round(avgeff));
		// app.debugPrint(sWingSweep);
		Vy = String.format("%.1f", nVy);
		sN = String.format("%.1f", An / g);
		IAS = String.format("%d", sState.IAS);
		TAS = String.format("%d", sState.TAS);
		salt = String.format("%.0f", alt);
		Wx = String.format("%.0f", Math.abs(sState.Wx));
		M = String.format("%.2f", sState.M);
		Ny = String.format("%.1f", sState.Ny);

		// SEP取整改善SEP过高时的可读性
		double SEPAccuracy = (double) ((long) SEP / 50);
		SEPAccuracy = SEPAccuracy * 2.5;
		if (SEPAccuracy == 0)
			SEPAccuracy = 1;

		sSEP = String.format("%.0f", Math.round(SEP / SEPAccuracy) * SEPAccuracy);

		aclrt = String.format("%.3f", acceleration);
		// Ao=String.format("%.1f",
		// Math.sqrt(sState.AoA*sState.AoA+sState.AoS*sState.AoS));
		AoA = String.format("%.1f", sState.AoA);
		AoS = String.format("%.1f", sState.AoS);
		// iIndic
		compass = String.format("%.0f", sIndic.compass);
		sPitchUp = String.format("%.0f", sIndic.aviahorizon_pitch);

		if (c.blkx != null && c.blkx.valid && c.blkx.nitro != 0) {
			double nitrokg = c.blkx.nitro - wepTime * c.blkx.nitroDecr / 1000;
			if (nitrokg < 0)
				nitrokg = 0;

			sNitro = String.format("%.0f", nitrokg);
			long twepTime = (int) ((c.blkx.nitro / c.blkx.nitroDecr - wepTime / 1000));

			if (twepTime < 0) {
				twepTime = 0;
			}
			if (twepTime / 60 >= 100) {
				sWepTime = String.format("%d", twepTime / 60);
			} else {
				// sWepTime = String.format("%d:%02d", twepTime / 60, twepTime %
				// 60);
				sWepTime = String.format("%.1f", (double) twepTime / 60.0f);
			}

		} else {
			sNitro = nastring;
			sWepTime = nastring;
		}

		sAcc = String.format("%.1f", acceleration);
		compressorstage = String.format("%d", sState.compressorstage);
		if (curLoadMinWorkTime == 99999 * 1000)
			sEngWorkTime = nastring;
		else
			sEngWorkTime = String.format("%.0f", curLoadMinWorkTime / 1000);

		if (Math.abs(turnRds) < 9999)
			sTurnRds = String.format("%.0f", Math.abs(turnRds));
		else
			sTurnRds = nastring;

		if (turnRate < 999)
			sTurnRate = String.format("%.1f", turnRate);
		else
			sTurnRate = nastring;
		sHorizontalLoad = String.format("%.1f", horizontalLoad);
		// app.debugPrint("已加力时间(秒)"+wepTime/1000);
		// app.debugPrint("剩余加力时间(分钟)"+rwepTime);

	}

	public void checkEngineJet() {

		// if(c.blkx != null && c.blkx.valid){
		// if (c.blkx.isJet){
		// iEngType = ENGINE_TYPE_JET;
		// }
		// else{
		// iEngType = ENGINE_TYPE_PROP;
		// }
		// return ;
		// }
		// else{
		// // TODO:自适应方式获得,由磁电机判断. 只有活塞才有磁电机
		// if (sState.magenato >= 0){
		// iEngType = ENGINE_TYPE_PROP;
		// }
		// else{
		// iEngType = ENGINE_TYPE_JET;
		// }
		// }
		// TODO:自适应方式获得,由磁电机判断. 只有活塞才有磁电机
		if (!checkEngineFlag) {
			if (sState.magenato < 0) {
				checkEngineType--;
			} else {

				checkEngineType++;
			}
			if (sState.pitch[0] != -65535) {
				checkPitch++;
			} else {
				checkPitch--;
			}
		}
		if (Math.abs(checkEngineType) >= 100) {
			checkEngineFlag = true;
			if (checkEngineType >= 0) {
				iEngType = ENGINE_TYPE_PROP;
			} else {

				// 涡桨
				if (checkPitch > 0) {
					iEngType = ENGINE_TYPE_TURBOPROP;
					// app.debugPrint("涡桨\n");
				} else
					iEngType = ENGINE_TYPE_JET;
			}
		}
		// app.debugPrint(iEngType);
	}

	public void slowcalculate(long dtime) {
		// 计算耗油率及持续时间
		// app.debugPrint(totalfuelp - totalfuel);
		// if (MainCheckMili - FuelCheckMili > 1000) {

		if ((sState.gear == 100 || sState.gear < 0) && fTotalFuel > fTotalFuelP) {
			// 加油,重置

			app.debugPrint("reset " + fTotalFuel + "," + fTotalFuelP);
			resetvaria();

		}

		// dfuel = (fTotalFuelP - fTotalFuel) / (MainCheckMili - FuelCheckMili);
		dfuel = (fTotalFuelP - fTotalFuel) / dtime;

		if (dfuel > 0) {

			if (!bLowAccFuel) {
				// fueltime = (long) (fueltime + (fTotalFuel / dfuel)) / 2;
				// 改用滑动平均
				fueltime = (long) fuelTimeSMA.addNewData(fTotalFuel / dfuel);

			}
			// fueltime = (long)(ratio_1 * fueltime + ratio *(fTotalFuel /
			// dfuel));
			FuelchangeTime = MainCheckMili - FuelLastchangeMili;
			FuelLastchangeMili = MainCheckMili;
			fuelChange = fTotalFuelP - fTotalFuel; // 改变1公斤花了多长时间
			// app.debugPrint(fuelChange);

		} else {
			// 没有变化，使用上次
			// fueltime = (MainCheckMili - FuelLastchangeMili)
			// fueltime = 0;
			// fueltime = (totalfuelp -
			// totalfuel)/MainCheckMili-FuelLastchangeMili);

			if (fuelChange != 0) {
				// fueltime = (long) ((ratio * (fTotalFuel * FuelchangeTime /
				// fuelChange)) + ratio_1 * fueltime);
				fueltime = (long) (fTotalFuel * FuelchangeTime / fuelChange);
			} else
				fueltime = 0;
			// app.debugPrint(fueltime);
		}

		if (fueltime < 0)
			fueltime = 0;

		FuelCheckMili = MainCheckMili;
		fTotalFuelP = fTotalFuel;

		// 计算变化率
		// long TPchangeTime;
		// int dthrust = thurstPercent - pThurstPercent;
		// if (thurstPercent - pThurstPercent > 0){
		// TPchangeTime = MainCheckMili - TPLastchangeMili;
		// TPLastchangeMili = MainCheckMili;
		// }

		// }
	}

	public void checkOverheat() {
		engineLoad[] pL = c.blkx.engLoad;
		curLoad = c.blkx.findmaxLoad(pL, nwaterTemp, noilTemp);
		// 减去时间
		double minWorkTime = 99999 * 1000;

		// 水冷
		curWLoad = c.blkx.findmaxWaterLoad(pL, nwaterTemp);
		for (int i = 0; i < c.blkx.maxEngLoad; i++) {
			if (i < curWLoad) {
				if (pL[i].WorkTime != 0) {
					pL[i].curWaterWorkTimeMili -= TimeIncrMili;
					if (pL[i].curWaterWorkTimeMili < minWorkTime) {
						minWorkTime = pL[i].curWaterWorkTimeMili;
					}
				}

			} else {
				// 大于load且工作时长不满则进行恢复
				// if (i >= curLoad) {
				if (sState.throttle <= 100) {
					if (pL[i].RecoverTime != 0 && (1000 * pL[i].WorkTime > pL[i].curWaterWorkTimeMili)) {
						pL[i].curWaterWorkTimeMili += (double) TimeIncrMili * pL[i].WorkTime / pL[i].RecoverTime;
					}
				}
				// }
			}
		}

		// app.debugPrint("当前水工作负载: " + curLoad + "," + minWorkTime);
		// app.debugPrint("水工作负载数组: [");
		// for (int i = 0; i < c.blkx.maxEngLoad; i++) {
		// System.out.print(pL[i].curWaterWorkTimeMili / 1000 + " ");
		// }
		// app.debugPrint("]");

		// 油冷
		curOLoad = c.blkx.findmaxOilLoad(pL, noilTemp);
		for (int i = 0; i < c.blkx.maxEngLoad; i++) {
			if (i < curOLoad) {
				if (pL[i].WorkTime != 0) {
					pL[i].curOilWorkTimeMili -= TimeIncrMili;
					if (pL[i].curOilWorkTimeMili < minWorkTime) {
						minWorkTime = pL[i].curOilWorkTimeMili;
					}
				}

			} else {
				// 大于load且工作时长不满则进行恢复
				// if (i >= curLoad) {CCCCCCC
				if (sState.throttle <= 100) {
					if (pL[i].RecoverTime != 0 && (1000 * pL[i].WorkTime > pL[i].curOilWorkTimeMili)) {
						pL[i].curOilWorkTimeMili += (double) TimeIncrMili * pL[i].WorkTime / pL[i].RecoverTime;
					}
				}
				// }
			}
		}

		//// app.debugPrint("当前油工作负载: " + curLoad + "," + minWorkTime);
		// app.debugPrint("油工作负载数组: [");
		// for (int i = 0; i < c.blkx.maxEngLoad; i++) {
		// System.out.print(pL[i].curOilWorkTimeMili / 1000 + " ");
		// }
		// app.debugPrint("]");

		curLoadMinWorkTime = minWorkTime;
	}

	// 转弯半径和转弯时间计算
	public double turnRds;
	public double turnRate;

	public double horizontalLoad;
	public double bangleR;

	public double altmeterp;
	public double altmeter;
	public double thurstPercent;
	private int maxTotalThr;
	public int fuelPercent;
	public double avgeff;
	private int maxTotalHp;
	private double vTAS;
	private double pThurstPercent;
	public double tEngResponse;
	public double flapAllowSpeed;
	private int flapp;
	private int flap;
	private boolean downflap;
	private long flapCheck;
	double maximumThrRPM;
	// double maximumAllowedRPM;
	private long checkMaxiumRPM;
	public boolean getMaximumRPM;
	public httpHelper httpClient;

	public void calculateB() {
		// 计算斜抛角度,基本是正确的
		// double theta_rads = Math.toRadians(iIndic.aviahorizon_pitch);
		// double tantheta = Math.tan(theta_rads);
		// double costheta = Math.cos(theta_rads);
		// double bpow_vcostheta = (speedv * costheta) *(speedv * costheta);
		// double bxoffset = (- tantheta + Math.sqrt(tantheta * tantheta + (alt
		// * 2 * 9.78 / bpow_vcostheta) )) * bpow_vcostheta / 9.78;
		// bangleR = Math.toDegrees(Math.atan(alt/bxoffset));
		//
		// double pow_vcostheta = (speedv * costheta) *(speedv * costheta);
		// double xoffset = (- tantheta + Math.sqrt(tantheta * tantheta + (alt *
		// 2 * 9.78 / pow_vcostheta) )) * pow_vcostheta / 9.78;
		// double angleR = Math.toDegrees(Math.atan(alt/xoffset));

		// app.debugPrint(bangleR +"," + bxoffset);

		// 问题是怎么把斜抛角度映射到屏幕空间上
		// degree to Pixel
		// 求出屏幕空间角度比例FOV,然后像素点映射
	}

	public void updateWepTime() {
		if (sState.throttle > 100) {
			// 进入Wep状态
			// app.debugPrint(TimeIncrMili);
			wepTime += TimeIncrMili;
		}
	}

	public void updateTemp() {
		noilTemp = sIndic.oilTemp;
		nwaterTemp = sIndic.waterTemp;
		if (noilTemp == -65535) {
			noilTemp = sState.oiltemp;
		}
		if (nwaterTemp == -65535) {
			nwaterTemp = sIndic.engine_temperature;
			if (nwaterTemp == -65535)
				nwaterTemp = sState.watertemp;
		}
	}

	public void updateAlt() {
		altp = alt;
		alt = sState.heightm;

		altmeterp = altmeter;
		altmeter = sIndic.altitude_10k;

		// 人类毒瘤英制飞机
		if (!notCheckInch && Math.abs(sState.Vy) > 0) {
			if ((Math.abs(altmeter - altmeterp) * 1000 > Math.abs(2 * sState.Vy * intv))) {
				iCheckAlt += intv;
			} else {
				iCheckAlt -= intv;
			}
			if (Math.abs(iCheckAlt) > 10000)
				notCheckInch = true;
		}

		// if (checkAlt > 2)
		// alt = alt * 0.3048f;
		// // app.debugPrint(Math.abs(alt - altp)*1000+"?"+Math.abs(2 *
		// // sState.Vy * intv));
		//
		// // 解决熊猫的高度问题
		// alt = alt + altperCircle * altreg;
		// app.debugPrint("checkalt"+checkAlt);

		// 无线电高度
		pRadioAlt = radioAlt;
		// radioAlt = iIndic.radio_altitude;
		if (iCheckAlt > 0) {
			radioAlt = sIndic.radio_altitude * 0.3048f;
		} else {
			radioAlt = sIndic.radio_altitude;
		}
		dRadioAlt = (ratio_1 * dRadioAlt) + ratio * 1000.0f * (radioAlt - pRadioAlt) / intv;
		// app.debugPrint(dRadioAlt);

	}

	public void updateClimbRate() {
		if (sIndic.vario != -65535) {
			// 如果有爬升率表，使用爬升率表订正Vy
			nVy = sIndic.vario;

		} else {
			nVy = sState.Vy;
		}

	}

	public void updateTurn() {
		// 转弯半径等于speedv*speedv/9.78*G

		// 转弯加速度约等于法向过载与重力过载之和
		double alpha = 0;
		double beta = 0;
//		if (sIndic.aviahorizon_roll != -65535) {
//			alpha = sIndic.aviahorizon_roll;
//		}
//		if (sIndic.aviahorizon_pitch != -65535) {
//			beta = sIndic.aviahorizon_pitch + sState.AoA;
//		}

		if (sIndic.aviahorizon_roll != -65535 && sIndic.aviahorizon_pitch != -65535) {
			// 获得横滚角
			
			An = (double) (g * Math
					.sqrt(sState.Ny * sState.Ny + 1 - 2 * sState.Ny * Math.cos(Math.toRadians(sIndic.aviahorizon_roll))
							* Math.cos(Math.toRadians(sIndic.aviahorizon_pitch + sState.AoA))));
			// An = (double)(g * Math.sqrt(a))
		} else
			An = (g * sState.Ny);
		// app.debugPrint(Math.cos(sIndic.aviahorizon_roll));
		// 计算时取前后两次采样的速度平均值

		// double sumspeed1 = sumSpeedSMA.addNewData(speedv + speedvp);

		if (sIndic.turn != -65535) {

			// double cosa = Math.cos(Math.toRadians(sIndic.aviahorizon_roll));
			// double cosb = Math.cos(Math.toRadians(sIndic.aviahorizon_pitch +
			// sState.AoA)) ;
			// double tAn = (double)(g*
			// Math.sqrt(sState.Ny*sState.Ny * cosa * cosa * cosb * cosb
			// + 1
			// - 2 * sState.Ny * cosa * cosb
			// + Math.abs(sIndic.turn) * Math.abs(sIndic.turn) * (speedvp +
			// speedv) * (speedvp + speedv) / (4*g*g))
			// );
			//
			// System.out.println(tAn - An);
			// An = tAn;
			horizontalLoad = Math.abs(sIndic.turn) * (speedvp + speedv) / (2 * g);
			// horizontalLoad = Math.abs(sIndic.turn) * sumspeed1 / (2 * g);s

		} else {
			horizontalLoad = 0;
		}

		// turnRds = (sumspeed1 * sumspeed1) / (4 * An);
		// turnRds = (speedvp + speedv) * (speedvp + speedv) / (4 * An) ;
		if (An != 0) {
			turnRds = turnrdsSMA.addNewData((speedvp + speedv) * (speedvp + speedv) / (4 * An));
			// 转弯率等于向心加速度除以半径开根号
			turnRate = (double) (Math.toDegrees(Math.sqrt(An / turnRds)));
			// turnRds = turnRds/10 * 10
		}
		else{
			
		}
	}

	public void updateSpeed() {

		speedvp = speedv;
		IASvp = IASv;
		IASv = sState.IAS;

		// vTASp = vTAS;;
		vTAS = (double) sState.TAS;

		if (sIndic.speed != -65535) {
			// 指示空速，需要进行TAS校正
			double tspeedv = sIndic.speed;
			if (tspeedv != 0) {
				// iastotascoff = (1000 * ratio_1 * iastotascoff + 1000 * ratio
				// * vTAS / (speedv * 3.6f)) / 1000.0f;
				// 改用滑动平均
				iastotascoff = calcSpeedSMA.addNewData(vTAS / (tspeedv * 3.6));
			}
			// iastotascoff = 1+(double) (0.02 * sState.heightm * 3.2808 /
			// 1000);
			speedv = tspeedv * iastotascoff;
			// app.debugPrint("校正TAS:"+ speedv*3.6);
			// 订正后加速度还是会有跳变
			// app.debugPrint("校正TAS:"+ speedv*3.6 + "," + iastotascoff);

		} else {
			// 使用IASv作为辅助订正TAS
			speedv = vTAS * IASv / (3.6 * IASvp);
		}
	}

	public boolean isEngJet() {
		return iEngType == ENGINE_TYPE_JET;
	}

	public void updateEngineState() {
		int i;

		checkEngineJet();

		if (!isEngJet()) {
			// 活塞机

			double ttotalhp = 0;
			double ttotalhpeff = 0;
			double ttotalthr = 0;
			for (i = 0; i < sState.engineNum; i++) {
				ttotalthr = ttotalthr + sState.thrust[i];
				// app.debugPrint(sState.engineNum);
				ttotalhp = ttotalhp + sState.power[i];
				ttotalhpeff = ttotalhpeff + sState.thrust[i] * g * speedv / 735;
			}
			// app.debugPrint(totalhp);
			// app.debugPrint(totalhpeff);

			iTotalHp = (int) (ttotalhp);
			iTotalHpEff = (int) (ttotalhpeff);
			iTotalThr = (int) (ttotalthr);

			if (iTotalHp != 0)
				avgeff = (double) 100 * iTotalHpEff / iTotalHp;
			else
				avgeff = 0;
		} else {
			// 喷气机
			double ttotalthr = 0;
			for (i = 0; i < sState.engineNum; i++) {
				// app.debugPrint(sState.thrust[0]);
				ttotalthr = ttotalthr + sState.thrust[i];
			}
			// app.debugPrint(totalthr+" "+totalhpeff);
			double ttotalhpeff = ((ttotalthr * g * speedv) / 735);

			iTotalThr = (int) ttotalthr;
			iTotalHpEff = (int) ttotalhpeff;

			avgeff = 0;
		}

		if (maxTotalThr < iTotalThr) {
			maxTotalThr = (int) (ratio_1 * maxTotalThr + ratio * iTotalThr);
		}
		if (maxTotalHp < iTotalHpEff) {
			maxTotalHp = (int) (ratio_1 * maxTotalHp + ratio * iTotalHpEff);
		}

		pThurstPercent = thurstPercent;
		if (isEngJet() && maxTotalThr != 0) {
			// 喷气机
			if (maxTotalThr != 0) {
				thurstPercent = 100.0f * iTotalThr / maxTotalThr;
			}
		} else {
			if (maxTotalHp != 0) {
				thurstPercent = 100.0f * iTotalHpEff / maxTotalHp;
			}
		}

		tEngResponse = (ratio_1 * tEngResponse) + ratio * (thurstPercent - pThurstPercent) * 1000.0f / intv;

	}

	public void updateFuel() {
		int i;
		if (sIndic.fuelnum != 0) {
			double ttotalfuel = 0;
			bLowAccFuel = Boolean.FALSE;
			for (i = 0; i < sIndic.fuelnum; i++) {
				ttotalfuel = ttotalfuel + sIndic.fuel[i];
			}
			fTotalFuel = ttotalfuel;

		}
		// app.debugPrint("I"+totalfuel);
		if (fTotalFuel == 0) {
			bLowAccFuel = Boolean.TRUE;
			fTotalFuel = sState.mfuel;
		}
		fuelPercent = (int) (100 * fTotalFuel / sState.mfuel0);

	}

	public void updateSEP() {
		// if (sState.IAS != 0) {
		double diffspeed1 = diffSpeedSMA.addNewData(speedv - speedvp);
		// 这是不是示空速的差?
		// 使用修正
		// diffspeed = (ratio_1 * diffspeed + ratio * (speedv - speedvp));
		diffspeed = diffspeed1;
		// app.debugPrint(diffspeed);
		acceleration = diffspeed * 1000.0 / intv;

		// 三种计算方式

		// 这两种等价
		// SEP = acceleration * (speedvp + speedv) / (2 * g) + nVy;
		// SEP /= 2;

		// SEP = (diffspeed * (speedv + speedvp)) /((2 * intv * g)/ 1000.0f) +
		// nVy;
		// -38.8 4.7 = 28
		// SEP = SEP1;

		// 跳变太大, 没法读数

		// SEP = ((speedv * speedv) - (speedvp * speedvp))*1000.0f/(2 * intv *
		// g) + nVy;

		SEP = sepSMA.addNewData(((speedv + speedvp) * (speedv - speedvp) * 1000) / (2 * intv * g) + nVy);
		// SEP /= 2;

		// } else {
		// acceleration = 0;
		// SEP = 0;
		// }

		// 总能量
		pEnergyJKg = energyJKg;
		// energyJKg = ((speedv + speedvp) * (speedv + speedvp) / (8 * g) +
		// sState.heightm);
		energyJKg = ((speedv + speedvp) * (speedv + speedvp) / (8 * g) + sState.heightm);
		// System.out.println(String.format("%.0f",
		// energyDiffSMA.addNewData((energyJKg - pEnergyJKg)*1000/intv)));
	}

	public void checkWing() {
		if (sIndic.wsweep_indicator != -65535)
			hasWingSweepVario = true;
		else
			hasWingSweepVario = false;
	}

	public void checkFlap() {
		flapp = flap;
		flap = sState.flaps;
		if (flap - flapp > 0) {
			downflap = true;
		} else if (flap == flapp) {
			// 加计数
			flapCheck += intv;

			// 维持1秒稳定
			if (flapCheck >= 1000) {
				flapCheck = 0;
				downflap = false;
			}
		} else {
			// 小于则一定是收
			downflap = false;
		}

		flapAllowSpeed = getFlapAllowSpeed(sState.flaps, downflap);
	}

	public void getMaximumRPM() {
		if (!getMaximumRPM) {
			if (c.blkx != null && c.blkx.valid) {
				// FM合法直接取FM
				maximumThrRPM = c.blkx.maxRPM;
				// 使用最大允许RPM
				// maximumThrRPM = c.blkx.maxAllowedRPM;
				// app.debugPrint(maximumThrRPM);
				getMaximumRPM = true;
			} else {
				// 自适应获得(无FM)

				// 获得最大转速，条件是以最大转速持续约20秒或者桨距
				if (checkMaxiumRPM < 20000 / freq) {
					if (sState.IAS > 50) {
						if (sState.RPM >= maximumThrRPM) {
							// app.debugPrint(sState.RPM
							// +","+maximumThrRPM);
							maximumThrRPM = (ratio_1 * maximumThrRPM) + ratio * (sState.RPM);
						}
						checkMaxiumRPM++;
					}
				} else {
					getMaximumRPM = true;
				}
			}
		}
	}

	public void calculate() {
		int i;

		// 获得开始时间
		elapsedTime = SystemTime - startTime;

		// 增加wep时间
		updateWepTime();

		// 更新温度，优先使用更精确的
		updateTemp();
		// 检查是否过热，如果过热，计算引擎健康度
		checkOverheat();

		// 更新爬升率
		updateClimbRate();

		// 获得准确高度,需要依赖Vy因此要放到爬升率后面
		updateAlt();

		// 更新速度
		updateSpeed();

		// 更新转弯半径
		updateTurn();

		// app.debugPrint(horizontalLoad);
		// 计算总推力、总功率和总轴功率
		updateEngineState();
		// 计算总油量
		updateFuel();

		// 计算SEP
		updateSEP();

		// 可变翼判断
		checkWing();

		// 襟翼判断
		checkFlap();
		// app.debugPrint(flapAllowSpeed);

		// 获得最大转速
		getMaximumRPM();

		// TODO:升力阻力实时计算
		// TODO:临界速度和马赫数动态计算(考虑可变翼)

		// TODO:可用过载动态计算(油、重量)

	}

	public double getFlapAllowSpeed(int flapPercent, Boolean isDowningFlap) {
		// fm文件无法解析
		if (flapPercent == 0 || c.blkx == null || !c.blkx.valid)
			return Double.MAX_VALUE;

		// 找到襟翼档位
		int i = 0;
		for (; i < c.blkx.FlapsDestructionNum; i++) {
			// 大于
			if (flapPercent < c.blkx.FlapsDestructionIndSpeed[i][0] * 100.0f) {
				break;
			}
		}

		// 找到档位了
		// 线性求值
		// 找前面的flap值
		double x0, x1, y0, y1;
		double k;
		// 没有找到，都小于

		if (i == 0) {
			// 下襟翼时直接越级使用下一级
			if (isDowningFlap) {
				return c.blkx.FlapsDestructionIndSpeed[i][1];
			}
			// 襟翼只有0级
			// if(c.blkx.FlapsDestructionNum == 0){
			// return c.blkx.FlapsDestructionIndSpeed[0][1];
			// }
			return Double.MAX_VALUE;
		} else {
			// 下襟翼时直接越级使用
			// if (isDowningFlap) {
			// return c.blkx.FlapsDestructionIndSpeed[i][1];
			// }

			// 相等
			if (flapPercent == c.blkx.FlapsDestructionIndSpeed[i - 1][0] * 100.0f) {
				// 直接返回速度
				return c.blkx.FlapsDestructionIndSpeed[i - 1][1];
			}

			// 否则进行线性插值运算
			// 算斜率
			x0 = c.blkx.FlapsDestructionIndSpeed[i - 1][0] * 100.0f;
			y0 = c.blkx.FlapsDestructionIndSpeed[i - 1][1];
			x1 = c.blkx.FlapsDestructionIndSpeed[i][0] * 100.0f;
			y1 = c.blkx.FlapsDestructionIndSpeed[i][1];
			if (x1 - x0 != 0) {
				k = (y1 - y0) / (x1 - x0);
			} else {
				k = 0;
			}
			// 速度等于
			// app.debugPrint(x0 + "-" + x1 + ", " + y0 + "-" + y1);
			return y0 + (flapPercent - x0) * k;
		}

	}

	// 重置变量
	public void resetvaria() {
		playerLive = false;
		iEngType = ENGINE_TYPE_UNKNOWN;
		checkMaxiumRPM = 0;
		getMaximumRPM = false;
		dRadioAlt = 0;
		curLoad = 0;
		wepTime = 0;
		energyJKg = 0;
		pEnergyJKg = 0;
		elapsedTime = 0;
		altperCircle = 0;
		iCheckAlt = 0;
		altreg = 0;
		altp = 0;
		alt = 0;
		calcPeriod = 0;
		maximumThrRPM = 1;
		maxTotalThr = 0;
		iastotascoff = 1;
		thurstPercent = 0;
		checkEngineFlag = false;
		checkEngineType = 0;
		checkPitch = 0;
		fuelPercent = 0;
		maxTotalHp = 0;
		maxTotalThr = 0;
		diffspeed = 0;
		curLoadMinWorkTime = 99999 * 1000;
		FuelCheckMili = System.currentTimeMillis();
		MainCheckMili = FuelCheckMili;
		notCheckInch = false;
		altperCirclflag = false;
		// isFuelpressure = false;
		notCheckInch = false;
		hasWingSweepVario = false;
		flapAllowSpeed = Float.MAX_VALUE;
		fTotalFuelP = 0;
		isStateJet = false;

		calcSpeedSMA = cH.new simpleMovingAverage((int) (1000 / freq));
		diffSpeedSMA = cH.new simpleMovingAverage((int) (1000 / freq));
		sepSMA = cH.new simpleMovingAverage((int) (1000 / freq));
		turnrdsSMA = cH.new simpleMovingAverage((int) (1000 / freq));
		sumSpeedSMA = cH.new simpleMovingAverage((int) (1000 / freq));
		energyDiffSMA = cH.new simpleMovingAverage((int) (1000 / freq));
		fuelTimeSMA = cH.new simpleMovingAverage(4);
		if (c.blkx != null) {
			engineLoad[] pL = c.blkx.engLoad;
			if (pL != null) {
				for (int i = 0; i < c.blkx.maxEngLoad; i++) {
					pL[i].curWaterWorkTimeMili = pL[i].curWaterWorkTimeMili;
					pL[i].curOilWorkTimeMili = pL[i].curOilWorkTimeMili;
				}
			}
		}
	}

	public void clearvaria() {
		// sState = null;
		// iIndic = null;
		resetvaria();
	}

	public void clear() {
		// app.debugPrint("执行清洁");
		clearvaria();

		System.gc();
		sState.init();
		sIndic.init();

	}

	public service(controller xc) {

		c = xc;

		freq = xc.freqService;
		clearvaria();

		ratio = freq / 1000.0f;
		ratio_1 = 1.0f - ratio;
		sState = new state();
		sState.init();
		sIndic = new indicators();
		sIndic.init();
		httpClient = new httpHelper();
		power = new String[state.maxEngNum];
		pitch = new String[state.maxEngNum];
		thrust = new String[state.maxEngNum];
		efficiency = new String[state.maxEngNum];
		FuelCheckMili = System.currentTimeMillis();
		// isFuelpressure = false;

	}

	public void checkState() {
		int conState;
		// 更新时间戳
		timeStamp = SystemTime;
		// app.debugPrint("s:"+s+"s1:"+s1);
		// 更新state

		c.initStatusBar();
		if (httpClient.strState.length() > 2 && httpClient.strIndic.length() > 2) {
			// 改变状态为连接成功
			// app.debugPrint(sState);
			conState = sState.update(httpClient.strState);
			c.changeS2();
			if (sState.flag) {
				sIndic.update(httpClient.strIndic);

				if (sState.totalThr != 0) {
					playerLive = true;
				}

				if (isPlayerLive()) {
					c.changeS3();// 打开面板

					speedvp = sState.IAS;
					// 开始计算数据
					calculate();

					// 0.5秒一次
					if (((calcPeriod++) % (500 / freq)) == 0)
						slowcalculate((500 / freq) * freq);

					// 将数据转换格式
					transtoString();

					// 写入文档
					// c.writeDown();

					// 检查死亡
					if (sState.totalThr == 0 && sState.RPM <= 0 && sState.IAS < 10) {
						playerLive = false;
					}
				}
			} else {
				// 状态置为等待游戏开始（状态1）
				// c.changeS2();//连接成功等待游戏开始

				c.S4toS1();
				// app.debugPrint("等待游戏开始");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			// 状态置为等待连接中
			conState = -1;
			c.S4toS1();
			// app.debugPrint("等待连接中");
		}
		if (conState == -1) {
			// 端口连接可能有问题，切换端口
			// app.debugPrint("切换端口\n");
			portOcupied = !portOcupied;
		}
	}

	@Override
	public void run() {
		long waitMili = app.threadSleepTime;
		// app.debugPrint("" + waitMili);
		while (true) {

			try {
				Thread.sleep(waitMili);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SystemTime = System.currentTimeMillis();
			long diffTime = SystemTime - MainCheckMili;
			if (diffTime >= freq) {

				// 尝试GET数据
				if (!portOcupied)
					httpClient.getReqResult(app.requestDest);
				else
					httpClient.getReqResult(app.requestDestBkp);

				intv = (diffTime / freq) * freq;
				TimeIncrMili = diffTime;
				MainCheckMili += intv;
				// MainCheckMili = SystemTime;

				// 检查是否需要改变状态
				checkState();

				// 记录
				if (c.logon) {
					flightLog tempLog = c.Log;
					if (tempLog != null)
						tempLog.logTick();
				}

				// 检查超时
				// if (MainCheckMili <= (System.currentTimeMillis() - intv)) {
				// app.debugPrint("deadline Miss, try catch\n" + SystemTime +
				// "," + MainCheckMili);
				// }

			}

		}
	}
}