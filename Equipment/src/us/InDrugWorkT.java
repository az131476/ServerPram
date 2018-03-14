package us;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import bsh.EvalError;
import bsh.Interpreter;


public class InDrugWorkT implements Runnable {
	//$Q201 2 F1 L(方向) 0(盘点符号) 090(药长) 015(药宽) 12(限量) 
	//10(行) 001(列) 12(加药量) 4-513(X坐标) 4-513(Y坐标) 020(槽宽)
	//10 002 12 4-487 4-487 020
	//xxxx*
	public static boolean exitInDrug;
	public static boolean e1Deal;
	public static boolean putDrugOver = false;
	private String iCode;
	private int side;
	private int check;
	private int packageLen;
	private int packageWid;
	private int stockLimit;
	protected Interpreter interpreter = new Interpreter();
	
	public InDrugWorkT(String code) {
		exitInDrug = false;
		e1Deal = false;
		putDrugOver = false;
		
		side = Integer.parseInt(code.substring(8, 9));
		check = Integer.parseInt(code.substring(9, 10));
		packageLen = Integer.parseInt(code.substring(10, 13));
		packageWid = Integer.parseInt(code.substring(13, 16));
		stockLimit = Integer.parseInt(code.substring(16, 18));
		code = code.substring(18, code.length() - 5);
		
		this.iCode = code;
		
		try {
			this.interpreter.source(InitParamters.configPath + "back.script");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
		//	LogWrite.println(">>1");
			String code = "0000000000060106"+ String.format("%04X", 48) + "0001";
			Modbus.modbus.executeCode(code);
			while (true && !exitInDrug) {
				Thread.sleep(200);
				code = "0000000000060103"+ String.format("%04X", 48) + "0001";
				String rCode = Modbus.modbus.executeCode(code);
				if (rCode.substring(rCode.length()-1).equals("0")) {
					// 到位
					break;
				} else if (rCode.substring(rCode.length()-1).equals("2")) {
					LogWrite.println(">>1--exit");
					// 出现异常了，加药终止/急停呗按下
					new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E001" + CRCKey.getKey() + "*");
					return;
				}
				String code52 = "0000000000060103"+ String.format("%04X", 52) + "0001";
				String code_52 = Modbus.modbus.executeCode(code52);
				if(code_52.substring(code_52.length()-1).equals("3"))
				{
				    LogWrite.println("启动补药过程齐药异常，PLC返回MW52错误代码:"+code_52);
				}
			}
			if (exitInDrug) {
				LogWrite.println(">>1--exit");
				return;
			}
			
			// 写入药品宽度
			code = "0000000000060106"+ String.format("%04X", 44) + String.format("%04X", packageWid);
			Modbus.modbus.executeCode(code);
			Thread.sleep(100);
			
		//	LogWrite.println(">>2");
			// 移动门，齐药，拨杆
			if (side == 2) {  //右侧
				code = "0000000000060106"+ String.format("%04X", 58) + "0002"; //四单元，判断左右
				Modbus.modbus.executeCode(code);
				while (true && !exitInDrug) {
					Thread.sleep(300);
					code = "0000000000060103"+ String.format("%04X", 58) + "0001";
					String rCode = Modbus.modbus.executeCode(code);
					if (rCode.substring(rCode.length()-1).equals("4")) { //右边初始化完成
						// 到位
						break;
					}
					
					code = "0000000000060103"+ String.format("%04X", 50) + "0001";
					rCode = Modbus.modbus.executeCode(code);
					if (rCode.substring(rCode.length()-1).equals("1")) { //1表示机械手有药
						LogWrite.println(">>2--exit-机械手有药");
						new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E003" + CRCKey.getKey() + "*");
						return;
					}
				}
			} else {
				code = "0000000000060106"+ String.format("%04X", 58) + "0001";
				Modbus.modbus.executeCode(code);
				while (true && !exitInDrug) {
					Thread.sleep(300);
					code = "0000000000060103"+ String.format("%04X", 58) + "0001";
					String rCode = Modbus.modbus.executeCode(code);
					if (rCode.substring(rCode.length()-1).equals("3")) {
						// 到位
						break;
					}
					
					code = "0000000000060103"+ String.format("%04X", 50) + "0001";
					rCode = Modbus.modbus.executeCode(code);
					if (rCode.substring(rCode.length()-1).equals("1")) {
						LogWrite.println(">>2--exit");
						// 出现异常了，加药终止
						new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E003" + CRCKey.getKey() + "*");
						return;
					}
				}
			}
			if (exitInDrug) {
				LogWrite.println(">>2--exit");
				return;
			}
		//	LogWrite.println(">>3");
			
			// 可以放药了
			new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2022F100" + CRCKey.getKey() + "*");
		//	LogWrite.println(">>4");
			
			// 到位等待放药完成
			while (!exitInDrug && !putDrugOver) {
				Thread.sleep(300);
			}
			if (exitInDrug) {
				LogWrite.println(">>4--exit");
				return;
			}
		//	LogWrite.println(">>5");
			
			// 开始齐药
			code = "0000000000060106"+ String.format("%04X", 58) + "0005";
			Modbus.modbus.executeCode(code);
			
			while (true && !exitInDrug) {
				Thread.sleep(300);
				code = "0000000000060103"+ String.format("%04X", 58) + "0001";
				String rCode = Modbus.modbus.executeCode(code);
				if (rCode.substring(rCode.length()-1).equals("6")) {
					// 齐药完成
					LogWrite.println("齐药完成");
					break;
				}
			}
			if (exitInDrug) {
				LogWrite.println(">>5--exit");
				return;
			}
			LogWrite.println(">>6");
			
			String code52 = "0000000000060103"+ String.format("%04X", 52) + "0001";
			String c_52 = Modbus.modbus.executeCode(code52);
			if(c_52.substring(c_52.length()-1).equals("3"))
			{
			    LogWrite.println("齐药过程出现异常，PLC返回MW52错误代码:"+c_52);
			}
			
			while (iCode.length() > 0 && !exitInDrug) {
				int iRow = Integer.parseInt(iCode.substring(0, 2));
				int iCol = Integer.parseInt(iCode.substring(2, 5));
				int iInQty = Integer.parseInt(iCode.substring(5, 7));
				iCode = iCode.substring(7);
				int iHor = Integer.parseInt(iCode.substring(1, 1 + Integer.parseInt(iCode.substring(0, 1))));
				iCode = iCode.substring(1 + Integer.parseInt(iCode.substring(0, 1)));
				int iVer = Integer.parseInt(iCode.substring(1, 1 + Integer.parseInt(iCode.substring(0, 1))));
				iCode = iCode.substring(1 + Integer.parseInt(iCode.substring(0, 1)));
				int iChnW = Integer.parseInt(iCode.substring(0, 3));
				iCode = iCode.substring(3);
				LogWrite.println(">>"+iRow+"行"+iCol+"列，准备补"+iInQty);
				// 准备盘点
				if (1 == check) {
					
					int hor = 0;
					int vor = 0;
					
						/*
						 * 判断该药槽是否有单独设置的水平或垂直偏移量
						 */
						Interpreter interpreter2 = new Interpreter();
						
						try {
							interpreter2.source(InitParamters.configPath + "server.script");
							
							DBManager.serverIP = interpreter2.get("databaseip").toString();//p.getProperty("serverip");
							DBManager.userName = interpreter2.get("databaseuser").toString();//p.getProperty("username");
							DBManager.userPassword = interpreter2.get("databasepassword").toString();//p.getProperty("password");
							DBManager.dbName = interpreter2.get("databasename").toString();
						} catch (FileNotFoundException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						} catch (EvalError e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						LogWrite.println("iHor:"+iHor); //70
						LogWrite.println("iVer:"+iVer); //18
						Connection conn = null;
						try {
							conn = DBManager.getConnection();
							Statement statement = conn.createStatement();
							ResultSet rSet = statement.executeQuery("select Stock_Hor_correct,Stock_Ver_correct from stock_list where Stock_Hor='"+iHor+"' and Stock_Ver='"+iVer+"'");
							if (rSet.next()) {
								
								hor = rSet.getInt(1);
								vor = rSet.getInt(2);
							}else{
								LogWrite.println("查询药槽偏移量失败"+hor+""+vor);
							}
							rSet.close();
							statement.close();
						} catch (Exception e) {
							// TODO: handle exception
							throw e;
						} finally {
							DBManager.close(conn);
							conn.close();
						}
						int iCkHor = 0;
						int iCkVer = 0;
						LogWrite.println("hor1:"+hor);
						LogWrite.println("vor1:"+vor);

						if(vor>0 && hor>0){
							if(side==1){
								iCkHor = iHor+hor+iChnW/2;
							}else{
								iCkHor = iHor-hor-iChnW/2;
							}
							//iCkHor = iHor - (side == 1 ? -hor : hor) + (side == 1 ? -(iChnW/2) : -iChnW/2);
							iCkVer = iVer - vor;
							LogWrite.println("iCkHor1:"+iCkHor);
							LogWrite.println("iCkVer1:"+iCkVer);
						}
						if(vor==0 && hor==0){
							//取配置文件中统一偏移量
							iCkHor = iHor - Integer.parseInt(interpreter.get("checkOffsetHor_" + side).toString()) + (side == 2 ? -(iChnW/2) : iChnW/2);
							iCkVer = iVer - Integer.parseInt(interpreter.get("checkOffsetVer_" + side).toString());
							LogWrite.println("iCkHor2:"+iCkHor);
							LogWrite.println("iCkVer2:"+iCkVer);
						}
					
					
					//int iCkHor = iHor - Integer.parseInt(interpreter.get("checkOffsetHor_" + side).toString()) + (side == 2 ? -(iChnW/2) : iChnW/2);
					//int iCkVer = iVer - Integer.parseInt(interpreter.get("checkOffsetVer_" + side).toString());
					
					while (true && !exitInDrug) {
						XYMotion xMotion = new XYMotion(AMotion.XY, iCkHor, iCkVer);
						xMotion.run();
						LogWrite.println("xMotion.getErrCode():"+xMotion.getErrCode());
						if (!"00".equals(xMotion.getErrCode())) {
							e1Deal = false;
							// 出现异常了，加药终止
							new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E1" + xMotion.getErrCode() + CRCKey.getKey() + "*");
							// 等待异常解决
							while (!e1Deal && !exitInDrug) {
								Thread.sleep(1000);
								LogWrite.println("E1,等待异常解决："+xMotion.getErrCode());
							}
						} else {
							// 到位了
							break;
						}
					}
					
					if (exitInDrug) {
						return;
					}
					
					Thread.sleep(1000);
					
					try {
						int checkQty = new StockTaking().getQuantity(side, packageLen);
						if (checkQty != -99) {
							LogWrite.println("盘点数量："+checkQty);
							if (iInQty != stockLimit - checkQty) {
								// 返回盘点量
								new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2042F1" + String.format("%02d", iRow) + String.format("%03d", iCol) + String.format("%02d", checkQty) + CRCKey.getKey() + "*");
							} 
							iInQty = stockLimit - checkQty;
							
							if (iInQty < 1) {
								// 药槽满的
								continue;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						LogWrite.println("盘点错误");
						LogWrite.println(e);
					}
				} 
			//	LogWrite.println(">>8");
				// 前往加药位置
				while (true && !exitInDrug) {
					XYMotion xMotion = new XYMotion(AMotion.XY, iHor, iVer);
					xMotion.run();
					if (!"00".equals(xMotion.getErrCode())) {
						e1Deal = false;
						// 出现异常了，加药终止
						new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E1" + xMotion.getErrCode() + CRCKey.getKey() + "*");
						// 等待异常解决
						while (!e1Deal && !exitInDrug) {
							Thread.sleep(1000);
						}
					} else {
						// 到位了
						break;
					}
				}
				
				if (exitInDrug) {
					return;
				}
			//	LogWrite.println(">>9");
				// 加药
				code = "0000000000060106"+ String.format("%04X", 54) + String.format("%04X", iInQty);
				Modbus.modbus.executeCode(code);
				Thread.sleep(100);
			//	LogWrite.println(">>10");
				// 反馈结果
				int time = 1;
				String inDrugOutTime = "";
				while (true && !exitInDrug) {
					String code34 = "0000000000060103"+ String.format("%04X", 34) + "0001";
					String code_34 = Modbus.modbus.executeCode(code34);
					LogWrite.println("code_34:"+code_34);
					
					String code14 = "0000000000060103"+ String.format("%04X", 14) + "0001";
					String code_14 = Modbus.modbus.executeCode(code14);
					LogWrite.println("code_14:"+code_14);
					
					String code12 = "0000000000060103"+ String.format("%04X", 12) + "0001";
					String code_12 = Modbus.modbus.executeCode(code12);
					LogWrite.println("code_12:"+code_12);
					
					code = "0000000000060103"+ String.format("%04X", 52) + "0001";
					String rCode = Modbus.modbus.executeCode(code);
					LogWrite.println("code52:"+rCode);
					
					if (rCode.substring(rCode.length()-1).equals("5")) {
						// 加药结束
						break;
					}else if(rCode.substring(rCode.length()-1).equals("0")){
						//正常
						Thread.sleep(1000);
						//加药超时处理，从开始拨药时开始算，若在设定时间内未
//						time ++;
//						try {
//							this.interpreter.source(InitParamters.configPath + "front.script");
//							inDrugOutTime = interpreter.get("inDrugOutTime").toString();
//							
//						} catch (Exception e1) {
//							e1.printStackTrace();
//						}
//						int outTime = Integer.parseInt(inDrugOutTime);
//						LogWrite.println("最大超时时间:"+outTime);
//						LogWrite.println("超时时间:"+time);
//						if(time==outTime)
//						{
//							LogWrite.println("加药超时!"+time);
//							new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E2" + "05" + CRCKey.getKey() + "*");
//						}
					} 
					else {
						// 加药异常，读取正常加药数量，返回更新库存，同时返回异常，终止线程
						LogWrite.println("52："+rCode);
						LogWrite.println("52加药异常："+rCode.substring(rCode.length()-1));
						code = "0000000000060103"+ String.format("%04X", 56) + "0001";
						String rCode56 = Modbus.modbus.executeCode(code);
						LogWrite.println("56："+rCode56);
						int rQty = Integer.parseInt(rCode56.substring(rCode56.length()-4), 16);
						LogWrite.println("加药过程出错，已完成加药数量"+rQty);
						new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2062F1" + String.format("%02d", iRow) + String.format("%03d", iCol) + String.format("%02d", rQty) + CRCKey.getKey() + "*");
						
						Thread.sleep(300);
						
						// 出现异常了，加药终止
						LogWrite.println("加药过程出错，停止");
						LogWrite.println("56-2："+rCode56.substring(rCode56.length()-4));  //返回数量
						new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E2" + rCode.substring(rCode.length()-2) + CRCKey.getKey() + "*");
						return;
					}
				}
				LogWrite.println(">>11");
				// 读取结果
				code = "0000000000060103"+ String.format("%04X", 56) + "0001";
				String rCode = Modbus.modbus.executeCode(code);
				int rQty = Integer.parseInt(rCode.substring(rCode.length()-4), 16);
				LogWrite.println(">>12");
				LogWrite.println("加药完成，返回加药数量"+rQty);
				/*
				 * 返回加药数量为0时，建议用户盘点
				 */
				if(rQty==0){
					LogWrite.println("返回值为0："+rQty);
					new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q9922F1" + String.format("%02d", iRow) + String.format("%03d", iCol) + String.format("%02d", rQty) + CRCKey.getKey() + "*");
				}
				new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2062F1" + String.format("%02d", iRow) + String.format("%03d", iCol) + String.format("%02d", rQty) + CRCKey.getKey() + "*");
				
				// 没药了，跳出     
				if (rQty < iInQty) {
					break;
				}
			}
			LogWrite.println(">>13");
			//34
			//58
			code = "0000000000060103"+ String.format("%04X", 58) + "0001";
			String code_58 = Modbus.modbus.executeCode(code);
			LogWrite.println("code_58:"+code_58);
			//8
			code = "0000000000060103"+ String.format("%04X", 8) + "0001";
			String code_8 = Modbus.modbus.executeCode(code);
			LogWrite.println("code_8:"+code_8);
			//32
			code = "0000000000060103"+ String.format("%04X", 32) + "0001";
			String code_32 = Modbus.modbus.executeCode(code);
			LogWrite.println("code_32:"+code_32);
			//52
			code = "0000000000060103"+ String.format("%04X", 52) + "0001";
			String code_52 = Modbus.modbus.executeCode(code);
			LogWrite.println("code_52:"+code_52);
			
			// 返回加药口
			code = "0000000000060106"+ String.format("%04X", 48) + "0001";
			Modbus.modbus.executeCode(code);
			
			String rCode_back = "";
			while (true && !exitInDrug) {
				Thread.sleep(300);
				code = "0000000000060103"+ String.format("%04X", 48) + "0001";
				rCode_back = Modbus.modbus.executeCode(code);
				LogWrite.println("rCode_back48返回窗口:"+rCode_back);
				if (rCode_back.substring(rCode_back.length()-1).equals("0")) {
					// 到位
					LogWrite.println("已回窗口");
					break;
				}
			}
			 if (rCode_back.substring(rCode_back.length()-1).equals("2")) {
				// 出现异常了，加药终止
				LogWrite.println("急停被拍下");
				new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E001" + CRCKey.getKey() + "*");
				return;
			}else if(rCode_back.substring(rCode_back.length()-1).equals("0")){
				//正常
			}else if(rCode_back.substring(rCode_back.length()-1).equals("3")){
				//急停松开
			}
			else{
				LogWrite.println("48:"+rCode_back);
				LogWrite.println("返回窗口出现异常:"+rCode_back.substring(rCode_back.length()-1));
				new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E004" + CRCKey.getKey() + "*");
			}
			/*
			 * 机械手上是否有药
			 */
			String codem = "0000000000060103"+ String.format("%04X", 50) + "0001";
			String rCodem = Modbus.modbus.executeCode(codem);
			if (rCodem.substring(rCodem.length()-1).equals("1")) { //1表示机械手有药
				LogWrite.println("机械手上有药，请取出");
				// 出现异常了，加药终止
				new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2102F1E003" + CRCKey.getKey() + "*");
				return;
			}
			
			code = "0000000000060106"+ String.format("%04X", 92) + "0092";
			String str92 = Modbus.modbus.executeCode(code);
			LogWrite.println("拨药杠复位-address92："+str92);
			
			code = "0000000000060103"+ String.format("%04X", 98);
			String str98 = Modbus.modbus.executeCode(code);
			LogWrite.println("拨杠回零状态-address98："+str98);
			
			code = "0000000000060103"+ String.format("%04X", 84)+"0001";
			String str84 = Modbus.modbus.executeCode(code);
			LogWrite.println("齐药回零状态-address98："+str84);
			
		//	LogWrite.println(">>14");
			// 本次加药结束
			new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q2082F101" + CRCKey.getKey() + "*");
		//	LogWrite.println(">>15");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogWrite.println(e);
		}
	}
}
