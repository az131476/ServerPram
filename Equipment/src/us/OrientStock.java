package us;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.sun.media.Log;

import bsh.EvalError;
import bsh.Interpreter;


public class OrientStock implements Runnable {
	private String code;
	protected Interpreter interpreter = new Interpreter();
	public static boolean exitCheck;
	public static boolean e1Deal;
	
	public OrientStock(String code) {
		this.code = code;
		try {
			this.interpreter.source(InitParamters.configPath + "back.script");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void run() {
		//$Q3012F1  方向  水平坐标长度  水平坐标   垂直坐标长度   垂直坐标   3位药槽宽度  2位行  3位列  3位包装长度
		int side = Integer.parseInt(code.substring(8, 9));
		code = code.substring(9);
		LogWrite.println("code盘点:"+code);
		//270218110001001006ZnxL*
		int iHor = Integer.parseInt(code.substring(1, 1 + Integer.parseInt(code.substring(0, 1))));//70
		code = code.substring(1 + Integer.parseInt(code.substring(0, 1)));
		int iVer = Integer.parseInt(code.substring(1, 1 + Integer.parseInt(code.substring(0, 1))));//18
		code = code.substring(1 + Integer.parseInt(code.substring(0, 1)));
		int iChnW = Integer.parseInt(code.substring(0, 3));
		int iRow = Integer.parseInt(code.substring(3, 5));
		int iCol = Integer.parseInt(code.substring(5, 8));
		int iPackL = Integer.parseInt(code.substring(8, 11));
		
		int hor = 0;
		int vor = 0;
		try {
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
			
			//左侧加，右侧减
			//int iCkHor = iHor - Integer.parseInt(interpreter.get("checkOffsetHor_" + side).toString()) + (side == 2 ? -(iChnW/2) : iChnW/2);
			//int iCkVer = iVer - Integer.parseInt(interpreter.get("checkOffsetVer_" + side).toString());
			
			while (true && !exitCheck) {
				XYMotion xMotion = new XYMotion(AMotion.XY, iCkHor, iCkVer);
				xMotion.run();
				LogWrite.println("errorcode:"+xMotion.getErrCode());
				if (!"00".equals(xMotion.getErrCode())) {
					e1Deal = false;
					// 出现异常了，盘点停止  $Q3022F1             //$Q2102F1E1
					new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q9902F1E1" + xMotion.getErrCode() + CRCKey.getKey() + "*");
					// 等待异常解决
					LogWrite.println("盘点异常停止errorcode:"+xMotion.getErrCode());
					
					while (!e1Deal && !exitCheck) {
						Thread.sleep(1000);
					}
				} else {
					// 到位了
					break;
				}
			}
			if (exitCheck) {
				return;
			}
			
			Thread.sleep(1000);
			
			try {
				int ckLen = new StockTaking().getLen(side);
				int checkQty = (int)(ckLen*1.0/iPackL + 0.5);
				if (ckLen != 9999) {
					LogWrite.println("盘点长度："+ckLen+"  数量："+checkQty);
					// 返回盘点量
					new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q3022F1" + String.format("%02d", iRow) + String.format("%03d", iCol) + String.format("%03d", checkQty) + String.format("%04d", ckLen) + CRCKey.getKey() + "*");
				} else {
					new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q3062F100" + CRCKey.getKey() + "*");
				}
			} catch (Exception e) {
				// TODO: handle exception
				LogWrite.println("盘点错误");
				LogWrite.println(e);
				new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q3062F100" + CRCKey.getKey() + "*");
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
			new CodeSend().send(ASokcetType.TCP_IN_DRUG, "$Q3062F100" + CRCKey.getKey() + "*");
		}
	}
	
	public static void main(String[] args) {
		new OrientStock("$Q3012F1131234123409901010150XSXS*").run();
	}
}
