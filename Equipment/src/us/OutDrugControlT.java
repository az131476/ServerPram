package us;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;


public class OutDrugControlT implements Runnable {
	private OutDrugByRowVO outDrugByRowVO;
	private boolean stopCheckState;
	public static boolean outDrugBusy = false;
	
	
	public OutDrugControlT(OutDrugByRowVO outDrugByRowVO) {
		this.outDrugByRowVO = outDrugByRowVO;
	}

	public void setStopCheckState(boolean stopCheckState) {
		this.stopCheckState = stopCheckState;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Interpreter interpreter = new Interpreter();
		String sockState = "";
		try {
			interpreter.source(InitParamters.configPath + "server.script");
			
			DBManager.serverIP = interpreter.get("databaseip").toString();//p.getProperty("serverip");
			DBManager.userName = interpreter.get("databaseuser").toString();//p.getProperty("username");
			DBManager.userPassword = interpreter.get("databasepassword").toString();//p.getProperty("password");
			DBManager.dbName = interpreter.get("databasename").toString();
			sockState = interpreter.get("sockState").toString();
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
		
		stopCheckState = false;
		
		// ��ҩָ��
		String controlCode = "AA01" + String.format("%02X", outDrugByRowVO.getRowNO());
		//AA01 05 2E02  37FFEE
		//unserver��ҩָ���ʽ��$Q4012F1 2S 01+12+201710096924+01   002 03 24 ff  0+
		
		//AA01 08 | 2104 37FF 1F01 37FF 2001 37FFEE
		Iterator iter = outDrugByRowVO.getColHashMap().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String colCode = entry.getValue().toString();
			
			int row_revol = outDrugByRowVO.getRowNO();
			int col_revol = Integer.parseInt(colCode.substring(0, 3));
			int num_revol = Integer.parseInt(colCode.substring(3, 5));//��ҩ����
			controlCode += String.format("%02X", col_revol); //��
			/*
			 * �������кŶ�Ӧ�ķ�ҩ���������жϣ������Ƿ񳬹�10������10�������õ����������ҩ
			 *
			
			LogWrite.println("�У�"+row_revol);
			LogWrite.println("�У�"+col_revol);
			LogWrite.println("��ҩ������"+num_revol);
			int out_limit = 10;
			
			Connection con_outlimit = null;
			String sql_outlimit = "select Out_Limit from drug_list a LEFT JOIN stock_list b on a.drug_code=b.drug_code where b.row_no='"+row_revol+"' and b.col_no='"+col_revol+"'";
			try {
				con_outlimit = DBManager.getConnection();
				Statement st_outlimit = con_outlimit.createStatement();
				ResultSet rs_out_limit = st_outlimit.executeQuery(sql_outlimit);
				if (rs_out_limit.next()) {
					out_limit = rs_out_limit.getInt(1);
					
					LogWrite.println("out_limit"+out_limit);
				}else{
					LogWrite.println("out_limit-��ѯ���Ϊ��"+sql_outlimit);
					break;
				}
				rs_out_limit.close();
				rs_out_limit.close();
			} catch (Exception e) {
				LogWrite.println("��ѯ�쳣outLimit:"+sql_outlimit);
			} finally {
				DBManager.close(con_outlimit);
				try {
					con_outlimit.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} //11  13
			LogWrite.println("���ؿ������"+col_revol);*/
			
			
			controlCode += String.format("%02X", num_revol); //����
			//����ʱ����ȴ�ʱ��
			if (colCode.substring(5, 7).toUpperCase().equals("00") || colCode.substring(7, 9).toUpperCase().equals("00")) {
				controlCode += "3264";
			} else {
				if (colCode.substring(5, 7).toUpperCase().equals("EE")){
					controlCode += "ED";
				} else {
					controlCode += colCode.substring(5, 7);
				}
				if (colCode.substring(7, 9).toUpperCase().equals("EE")){
					controlCode += "ED";
				} else {
					controlCode += colCode.substring(7, 9);
				}
			}
		}
		controlCode += "EE";
		
		int waittime1 = 0;
		
		// ����
		synchronized (SerialPortEx.LOCK_SERIALPORT) {
			waittime1 = 0;
			LogWrite.println("[��ҩ]��ҩָ�"+controlCode);   //AA01052E0237FFEE
			SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
			while (true) {
				String code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
				if (code.equals("")) {
					if (waittime1 == 20) {
						LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·���");
						SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
					} else if (waittime1 > 40) {
						LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
						break;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					LogWrite.println("[��ҩ]����ָ�"+ code);
					break;
				}
				waittime1++;
			}//end while
		}//end synchronized
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//end try
		
		LogWrite.println("[��ҩ]�ж��Ƿ��ҩ���");
		String outDrugReturnCode1 = "";
		while (!stopCheckState) {
			String code1 = "";
			synchronized (SerialPortEx.LOCK_SERIALPORT) {
				waittime1 = 0;
				SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm("AA00EE", 16);
				while (true) {
					code1 = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
					if (code1.equals("")) {
						if (waittime1 == 20) {
							LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·���");
							SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
						} else if (waittime1 > 40) {
							LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
							break;
						} 
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						LogWrite.println("[��ҩ]�Ƿ��ҩ��ɷ���ָ�"+ code1);
						
						//��������***
						//������ָ��ΪAA02+�к�+00EE����������ҩ��δ�ܷ���ҩƷ����Ҫȥ��Ĳ�ִ�з�ҩ
						//���ݸ�ҩƷ�ķ�ҩָ��ȥ�����Ƿ��б�Ĳۿ��Է�ҩ��������������·��ͱ��ҩ�۵ķ�ҩָ��
						//���������һ��AA010203
						//�������кŲ�ѯҩƷ�Ƿ��б��ҩ�ۿɷ�ҩ��
						
						if(code1.length()==10) //�����۷�ҩ
						{
							String number = controlCode.substring(8,10); //��ҩ����
							String checkValue = code1.substring(code1.length()-4,code1.length()-2); //��������
							//����ֵ����Ϊ00
						if((code1.substring(code1.length()-4,code1.length()-2).equals("00")) || Integer.parseInt(checkValue, 16)<Integer.parseInt(number, 16))
						{
							
							//��ѯ��Ҫ��ҩ��ҩ�� AA 01 01 61 07 37 FF EE
							int row_no = Integer.parseInt(controlCode.substring(4, 6), 16);//ת10����
							int col_no = Integer.parseInt(controlCode.substring(6, 8), 16);
							String rowNo = row_no +"";
							String colNo = col_no +"";
							
							String time1 = controlCode.substring(10, 12);
							String time2 = controlCode.substring(12, 14);
							
							//����ִ�з�ҩ����
							String outCode_2 = null;
							String row_1 = null;
							String col_1 = null;
							
							//����ֵΪ00������ҩ��
							if(sockState.equals("1")){
							if(code1.substring(code1.length()-4,code1.length()-2).equals("00")|| Integer.parseInt(checkValue, 16)<Integer.parseInt(number, 16)){
								Connection conn = null;
								try {
									conn = DBManager.getConnection();
									Statement statement = conn.createStatement();
									statement.execute("update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo+"'");
									statement.close();
								} catch (Exception e) {
									// TODO: handle exception
								} finally {
									DBManager.close(conn);
									try {
										conn.close();
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							}
							//�ٳ����٣������跢ҩ����
							int quantity = Integer.parseInt(number, 16)-Integer.parseInt(checkValue, 16);
							Connection conn_s = null;
							try {
								conn_s = DBManager.getConnection();
								Statement statement = conn_s.createStatement();
								String remark = "Ӧ�� "+number+" �ٳ� "+quantity;
								statement.execute("update stock_list set Remark='"+remark+"' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo+"'");
								statement.close();
							} catch (Exception e) {
								// TODO: handle exception
							} finally {
								DBManager.close(conn_s);
								try {
									conn_s.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							//�����µĳ�ҩָ��
								String drugCode = null;
								String stockCount = null;
								Connection con = null;
								String sql = "select m.Drug_Code,m.StockCount from v_f_stock2 m,stock_list n where m.Drug_Code=n.Drug_Code and n.Row_NO='"+rowNo+"' and n.Col_NO='"+colNo+"'";
								
								try {
									con = DBManager.getConnection();
									Statement st = con.createStatement();
									ResultSet rs = st.executeQuery(sql);
									if (rs.next()) {
										drugCode = rs.getString(1);
										stockCount = rs.getString(2);
										
										LogWrite.println("drugcode:"+drugCode+"stockcount:"+stockCount+sql);
										
									}else{
										break;
									}
									rs.close();
									st.close();
								} catch (Exception e) {
									LogWrite.println("��ѯ�쳣1:"+sql);
								} finally {
									DBManager.close(con);
									try {
										con.close();
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}	
								if(Integer.parseInt(stockCount)>0)
								{
									Connection con3 = null;
									String sql_1 = "select a.Stock_ID,sum(Quantity)-ifnull(Stock_HoldQuantity,0) as quantity,b.Row_NO,b.Col_NO from stock_detail a left join stock_list b on a.stock_id=b.stock_id left join drug_list c on a.Drug_Code=c.Drug_Code where a.drug_code='"+drugCode+"' and b.Row_NO ='"+rowNo+"' and b.Col_NO !='"+colNo+"' and state=1 group by a.stock_id order by quantity desc LIMIT 1";
									try {
										con3 = DBManager.getConnection();
										Statement st3 = con3.createStatement();
										ResultSet rs3 = st3.executeQuery(sql_1);
										if (rs3.next()) {
											row_1 = rs3.getString(3);
											col_1 = rs3.getString(4);
											
											LogWrite.println("row_1"+row_1+"col_1"+col_1+sql_1);
										}else{
											LogWrite.println("��ѯ���Ϊ��");
											break;
										}
										rs3.close();
										st3.close();
									} catch (Exception e) {
										LogWrite.println("��ѯ�쳣2"+sql_1);
									} finally {
										DBManager.close(con3);
										try {
											con3.close();
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									if(Integer.parseInt(row_1) >= Integer.parseInt(rowNo))
									{
									
									row_1 = String.format("%02X",Integer.parseInt(row_1));
									col_1 = String.format("%02X",Integer.parseInt(col_1));
									

									
									String quantity_1 = String.format("%02X",quantity);
									
									outCode_2 = "AA01"+row_1+col_1+quantity_1+time1+time2+"EE";
									//LogWrite.println("�³�ҩָ��:"+outCode_2);
									}else{
										break;
									}
								}else
								{
									break;
								}
								int waittime = 0;
								
								// ����
								synchronized (SerialPortEx.LOCK_SERIALPORT) {
									waittime = 0;
									LogWrite.println("[�³�ҩ]��ҩָ�"+outCode_2);
									SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(outCode_2, 16);
									while (true) {
										String code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
										if (code.equals("")) {
											if (waittime == 20) {
												LogWrite.println("[�³�ҩ]ָ�ʱ������Ӧ�����·���");
												SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(outCode_2, 16);
											} else if (waittime > 40) {
												LogWrite.println("[�³�ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
												break;
											}
											try {
												Thread.sleep(100);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										} else {
											LogWrite.println("[�³�ҩ]����ָ�"+ code);
											break;
										}
										waittime++;
									}//end while
								}//end synchronized
								
								try {
									Thread.sleep(200);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}//end try
								
								LogWrite.println("[�³�ҩ]�ж��Ƿ��ҩ���");
								String outDrugReturnCode = "";
								while (!stopCheckState) {
									String code = "";
									synchronized (SerialPortEx.LOCK_SERIALPORT) {
										waittime = 0;
										SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm("AA00EE", 16);
										while (true) {
											code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
											if (code.equals("")) {
												if (waittime == 20) {
													LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·���");
													SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
												} else if (waittime > 40) {
													LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
													break;
												} 
												try {
													Thread.sleep(100);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
											} else {
												LogWrite.println("[�³�ҩ]�Ƿ��ҩ��ɷ���ָ�"+ code);
												
												//  if(!(code.substring(code.length()-4,code.length()-2)).equals(number))
//													{
//														break;
//													}
													//������һ�Σ����۷��������Ƿ�Ϊ�գ�������һ���ж�
													//����� AA 02 14 02 EE
												if(code.length()==10){
													int num_m = Integer.parseInt(code.substring(code.length()-4,code.length()-2),16); //��������
													int row_2 = Integer.parseInt(row_1,16);
													int col_2 = Integer.parseInt(col_1,16);
													String sql_num = "update stock_detail set Quantity=Quantity-'"+num_m+"' where Row_NO='"+row_2+"' and Col_NO='"+col_2+"' ORDER BY Quantity desc LIMIT 1";
													LogWrite.println("sql_num"+sql_num);
													Connection conn = null;
													try {
														conn = DBManager.getConnection();
														Statement statement = conn.createStatement();
														statement.execute(sql_num);
														statement.close();
													} catch (Exception e) {
														// TODO: handle exception
													} finally {
														DBManager.close(conn);
														try {
															conn.close();
														} catch (SQLException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
													}//end
													}
												break;
											}
											waittime++;
										}//end while
									}//end synchronized
									
									if (code.toUpperCase().indexOf("AA00") == 0) {
										// ��ʱ���
										try {
											Thread.sleep(200);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
						 			} else if (code.toUpperCase().indexOf("AA02") == 0) {
										// ��ҩ���
										outDrugReturnCode = code;
										break;
									}
//									
								}			
						}else if(Integer.parseInt(checkValue,16) == Integer.parseInt(number,16))
							{
								break;
							}else if(Integer.parseInt(checkValue,16) > Integer.parseInt(number,16)){
								LogWrite.println("��ҩ�෢1������е���");
							break;
						}
						//aa00272f2f2fee
						//aa020c050d01ee
						}else if(code1.length()==14 && code1.substring(2,4).equals("02"))//ͬʱ����ҩ�۷�ҩ,ͬ�в�ͬ����ͬʱ ��ҩ
						{ // AA 01 07 48 03 37FF 04 01 37FFEE  24
							//aa 02 12 02 13 01 ee   14
							//��ҩָ��Ķ�Ӧ�к�
							String col2_1 = Integer.parseInt(controlCode.substring(6, 8), 16)+"";
							String col2_2 = Integer.parseInt(controlCode.substring(controlCode.length()-10,controlCode.length()-8), 16)+"";
							
							int row_no = Integer.parseInt(controlCode.substring(4, 6), 16);//ת10����		
							String rowNo = row_no +"";
							//����ָ���Ӧ��
							String col_no1 = Integer.parseInt(code1.substring(code1.length()-10, code1.length()-8), 16)+"";
							String col_no2 = Integer.parseInt(code1.substring(code1.length()-6, code1.length()-4), 16)+"";
							String colNo1 = "";
							String colNo2 = "";
							
							String number2_1 = controlCode.substring(8,10); //��ҩ����
							String number2_2 = controlCode.substring(controlCode.length()-8,controlCode.length()-6);
							
							String checkValue2_1 = code1.substring(code1.length()-8,code1.length()-6); //�ж�Ӧ�ķ�������
							String checkValue2_2 = code1.substring(code1.length()-4,code1.length()-2);
							
							String number1 = "";
							String number2 = "";
							String checkValue1 = "";
							String checkValue2 = "";
							
							if(col2_1.equals(col_no1)&& col2_2.equals(col_no2)){
								colNo1 = col_no1;
								colNo2 = col_no2;
								number1 = number2_1;
								number2 = number2_2;
								checkValue1 = checkValue2_1;
								checkValue2 = checkValue2_2;								
							}else{
								colNo1 = col_no2;
								colNo2 = col_no1;
								number1 = number2_1;
								number2 = number2_2;
								checkValue1 = checkValue2_2;
								checkValue2 = checkValue2_1;
							}
							//����ֵ����Ϊ00
						if((code1.substring(code1.length()-4,code1.length()-2).equals("00"))||(code1.substring(code1.length()-8,code1.length()-6).equals("00")) || Integer.parseInt(checkValue1, 16)<Integer.parseInt(number1, 16)||Integer.parseInt(checkValue2, 16)<Integer.parseInt(number2, 16))
						{

							//��ѯ��Ҫ��ҩ��ҩ�� AA 01 01 61 07 37 FF EE
							//int row_no = Integer.parseInt(controlCode.substring(4, 6), 16);//ת10����
							//String rowNo = row_no +"";
							//int col_no = Integer.parseInt(controlCode.substring(6, 8), 16);
							//String colNo = col_no +"";
							
							//����ִ�з�ҩ����
							String outCode_2_12 = null;
							String row_1_12 = null;
							String col_1_12 = null;//�����µ��к��к�
							
							String drugCode_12 = null;
							String stockCount_12 = null;
							Connection con_12 = null;
							String sql_12 = null;
							String time1_12 = controlCode.substring(10, 12);
							String time2_12 = controlCode.substring(12, 14);
							//����ֵΪ00������ҩ��
							//aa 02 12 02 13 01 ee 
							if(sockState.equals("1")){
							if(checkValue1.equals("00")|| Integer.parseInt(checkValue1, 16)<Integer.parseInt(number1, 16)){
								String sql_1 = "update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo1+"'";
								excuteShutrowcol(sql_1);
								LogWrite.println("*********�������2-1**********"+sql_1);
							}
							if(checkValue2.equals("00")||Integer.parseInt(checkValue2, 16)<Integer.parseInt(number2, 16)){
								String sql_1 = "update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo2+"'";
								excuteShutrowcol(sql_1);
								LogWrite.println("*******�������2-2**********"+sql_1);
							}
							}
							//��¼�ٳ�
							int quantity = 0;
							if(checkValue1.equals("00")||Integer.parseInt(checkValue1, 16)<Integer.parseInt(number1, 16)){
								quantity = Integer.parseInt(number1, 16)-Integer.parseInt(checkValue1, 16);
							}else if(checkValue2.equals("00")||Integer.parseInt(checkValue2, 16)<Integer.parseInt(number2, 16)){
								quantity = Integer.parseInt(number2, 16)-Integer.parseInt(checkValue2, 16);
							}
							if(checkValue1.equals("00")||Integer.parseInt(checkValue1, 16)<Integer.parseInt(number1, 16)){
								
								Connection conn = null;
								try {
									conn = DBManager.getConnection();
									Statement statement = conn.createStatement();
									String remark = "Ӧ�� "+number1+" �ٳ� "+quantity;
									statement.execute("update stock_list set Remark='"+remark+"' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo1+"'");
									statement.close();
								} catch (Exception e) {
									// TODO: handle exception
								} finally {
									DBManager.close(conn);
									try {
										conn.close();
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}else if(checkValue2.equals("00")||Integer.parseInt(checkValue2, 16)<Integer.parseInt(number2, 16)){
								Connection conn = null;
								try {
									conn = DBManager.getConnection();
									Statement statement = conn.createStatement();
									String remark = "Ӧ�� "+number2+" �ٳ� "+quantity;
									statement.execute("update stock_list set Remark='"+remark+"' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo2+"'");
									statement.close();
								} catch (Exception e) {
									// TODO: handle exception
								} finally {
									DBManager.close(conn);
									try {
										conn.close();
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							//�����µĳ�ҩָ��
							for(int i=1;i<3;i++){
								if(i==1){
									if(checkValue1.equals("00")||Integer.parseInt(checkValue1, 16)<Integer.parseInt(number1, 16)){
										sql_12 = "select m.Drug_Code,m.StockCount from v_f_stock2 m,stock_list n where m.Drug_Code=n.Drug_Code and n.Row_NO='"+rowNo+"' and n.Col_NO='"+colNo1+"'";
									LogWrite.println("��һ����");
									try {
										con_12 = DBManager.getConnection();
										Statement st = con_12.createStatement();
										ResultSet rs = st.executeQuery(sql_12);
										if (rs.next()) {
											drugCode_12 = rs.getString(1);
											stockCount_12 = rs.getString(2);
											
											LogWrite.println("drugcode:"+drugCode_12+"stockcount:"+stockCount_12+sql_12);
											
										}else{
											break;
										}
										rs.close();
										st.close();
									} catch (Exception e) {
										LogWrite.println("��ѯ�쳣1:"+sql_12);
									} finally {
										DBManager.close(con_12);
										try {
											con_12.close();
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}	
									if(Integer.parseInt(stockCount_12)>0)
									{
										Connection con3 = null;
										String sql_1 = null;
										if(checkValue1.equals("00")||Integer.parseInt(checkValue1, 16)<Integer.parseInt(number1, 16)){
											sql_1 = "select a.Stock_ID,sum(Quantity)-ifnull(Stock_HoldQuantity,0) as quantity,b.Row_NO,b.Col_NO from stock_detail a left join stock_list b on a.stock_id=b.stock_id left join drug_list c on a.Drug_Code=c.Drug_Code where a.drug_code='"+drugCode_12+"' and b.Row_NO ='"+rowNo+"' and b.Col_NO !='"+colNo1+"' and state=1 group by a.stock_id order by quantity desc LIMIT 1";
										}else if(checkValue2.equals("00")||Integer.parseInt(checkValue2, 16)<Integer.parseInt(number2, 16)){
											sql_1 = "select a.Stock_ID,sum(Quantity)-ifnull(Stock_HoldQuantity,0) as quantity,b.Row_NO,b.Col_NO from stock_detail a left join stock_list b on a.stock_id=b.stock_id left join drug_list c on a.Drug_Code=c.Drug_Code where a.drug_code='"+drugCode_12+"' and b.Row_NO ='"+rowNo+"' and b.Col_NO !='"+colNo2+"' and state=1 group by a.stock_id order by quantity desc LIMIT 1";
										}
										try {
											con3 = DBManager.getConnection();
											Statement st3 = con3.createStatement();
											ResultSet rs3 = st3.executeQuery(sql_1);
											if (rs3.next()) {
												row_1_12 = rs3.getString(3);
												col_1_12 = rs3.getString(4);
												
												LogWrite.println("row_1"+row_1_12+"col_1"+col_1_12+sql_1);
											}else{
												LogWrite.println("��ѯ���Ϊ��");
												break;
											}
											rs3.close();
											st3.close();
										} catch (Exception e) {
											LogWrite.println("��ѯ�쳣2"+sql_1);
										} finally {
											DBManager.close(con3);
											try {
												con3.close();
											} catch (SQLException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										if(Integer.parseInt(row_1_12) >= Integer.parseInt(rowNo)) //��ҩͬ�м�����
										{
										
										row_1_12 = String.format("%02X",Integer.parseInt(row_1_12));//����
										col_1_12 = String.format("%02X",Integer.parseInt(col_1_12));//����
										
										String quantity_1 = String.format("%02X",quantity);
										
										outCode_2_12 = "AA01"+row_1_12+col_1_12+quantity_1+time1_12+time2_12+"EE"; //��ָ��
										//LogWrite.println("�³�ҩָ��:"+outCode_2);
										}else{
											break;
										}
									}else
									{
										break;
									}
									int waittime = 0;
									
									// ����
									synchronized (SerialPortEx.LOCK_SERIALPORT) {
										waittime = 0;
										LogWrite.println("[�³�ҩ]��ҩָ�"+outCode_2_12);
										SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(outCode_2_12, 16);
										while (true) {
											String code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
											if (code.equals("")) {
												if (waittime == 20) {
													LogWrite.println("[�³�ҩ]ָ�ʱ������Ӧ�����·���");
													SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(outCode_2_12, 16);
												} else if (waittime > 40) {
													LogWrite.println("[�³�ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
													break;
												}
												try {
													Thread.sleep(100);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
											} else {
												LogWrite.println("[�³�ҩ]����ָ�"+ code);
												break;
											}
											waittime++;
										}//end while
									}//end synchronized
									
									try {
										Thread.sleep(200);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}//end try
									
									LogWrite.println("[�³�ҩ]�ж��Ƿ��ҩ���");
									String outDrugReturnCode = "";
									while (!stopCheckState) {
										String code = "";
										synchronized (SerialPortEx.LOCK_SERIALPORT) {
											waittime = 0;
											SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm("AA00EE", 16);
											while (true) {
												code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
												if (code.equals("")) {
													if (waittime == 20) {
														LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·���");
														SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
													} else if (waittime > 40) {
														LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
														break;
													} 
													try {
														Thread.sleep(100);
													} catch (InterruptedException e) {
														e.printStackTrace();
													}
												} else {
													LogWrite.println("[�³�ҩ]�Ƿ��ҩ��ɷ���ָ�"+ code);
													
													//  if(!(code.substring(code.length()-4,code.length()-2)).equals(number))
//														{
//															break;
//														}
														//������һ�Σ����۷��������Ƿ�Ϊ�գ�������һ���ж�
														//����� AA 02 14 02 EE
													if(code.length()==10){
														int num_m = Integer.parseInt(code.substring(code.length()-4,code.length()-2),16); //��������
														int row_2_n = Integer.parseInt(row_1_12,16);
														int col_2_n = Integer.parseInt(col_1_12,16);
														String sql_num = "update stock_detail set Quantity=Quantity-'"+num_m+"' where Row_NO='"+row_2_n+"' and Col_NO='"+col_2_n+"' ORDER BY Quantity desc LIMIT 1";
														LogWrite.println("sql_num"+sql_num);
														Connection conn = null;
														try {
															conn = DBManager.getConnection();
															Statement statement = conn.createStatement();
															statement.execute(sql_num);
															statement.close();
														} catch (Exception e) {
															// TODO: handle exception
														} finally {
															DBManager.close(conn);
															try {
																conn.close();
															} catch (SQLException e) {
																// TODO Auto-generated catch block
																e.printStackTrace();
															}
														}//end
														}
													break;
												}
												waittime++;
											}//end while
										}//end synchronized
										
										if (code.toUpperCase().indexOf("AA00") == 0) {
											// ��ʱ���
											try {
												Thread.sleep(200);
											} catch (InterruptedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
							 			} else if (code.toUpperCase().indexOf("AA02") == 0) {
											// ��ҩ���
											outDrugReturnCode = code;
											break;
										}
//										
									}
									}
								}else if(i==2){
									if(checkValue2.equals("00")||Integer.parseInt(checkValue2, 16)<Integer.parseInt(number2, 16)){
										sql_12 = "select m.Drug_Code,m.StockCount from v_f_stock2 m,stock_list n where m.Drug_Code=n.Drug_Code and n.Row_NO='"+rowNo+"' and n.Col_NO='"+colNo2+"'";
										LogWrite.println("�ڶ�����");
										try {
											con_12 = DBManager.getConnection();
											Statement st = con_12.createStatement();
											ResultSet rs = st.executeQuery(sql_12);
											if (rs.next()) {
												drugCode_12 = rs.getString(1);
												stockCount_12 = rs.getString(2);
												
												LogWrite.println("drugcode:"+drugCode_12+"stockcount:"+stockCount_12+sql_12);
												
											}else{
												break;
											}
											rs.close();
											st.close();
										} catch (Exception e) {
											LogWrite.println("��ѯ�쳣1:"+sql_12);
										} finally {
											DBManager.close(con_12);
											try {
												con_12.close();
											} catch (SQLException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}	
										if(Integer.parseInt(stockCount_12)>0)
										{
											Connection con3 = null;
											String sql_1 = null;
											if(checkValue1.equals("00")||Integer.parseInt(checkValue1, 16)<Integer.parseInt(number1, 16)){
												sql_1 = "select a.Stock_ID,sum(Quantity)-ifnull(Stock_HoldQuantity,0) as quantity,b.Row_NO,b.Col_NO from stock_detail a left join stock_list b on a.stock_id=b.stock_id left join drug_list c on a.Drug_Code=c.Drug_Code where a.drug_code='"+drugCode_12+"' and b.Row_NO ='"+rowNo+"' and b.Col_NO !='"+colNo1+"' and state=1 group by a.stock_id order by quantity desc LIMIT 1";
											}else if(checkValue2.equals("00")||Integer.parseInt(checkValue2, 16)<Integer.parseInt(number2, 16)){
												sql_1 = "select a.Stock_ID,sum(Quantity)-ifnull(Stock_HoldQuantity,0) as quantity,b.Row_NO,b.Col_NO from stock_detail a left join stock_list b on a.stock_id=b.stock_id left join drug_list c on a.Drug_Code=c.Drug_Code where a.drug_code='"+drugCode_12+"' and b.Row_NO ='"+rowNo+"' and b.Col_NO !='"+colNo2+"' and state=1 group by a.stock_id order by quantity desc LIMIT 1";
											}
											try {
												con3 = DBManager.getConnection();
												Statement st3 = con3.createStatement();
												ResultSet rs3 = st3.executeQuery(sql_1);
												if (rs3.next()) {
													row_1_12 = rs3.getString(3);
													col_1_12 = rs3.getString(4);
													
													LogWrite.println("row_1"+row_1_12+"col_1"+col_1_12+sql_1);
												}else{
													LogWrite.println("��ѯ���Ϊ��");
													break;
												}
												rs3.close();
												st3.close();
											} catch (Exception e) {
												LogWrite.println("��ѯ�쳣2"+sql_1);
											} finally {
												DBManager.close(con3);
												try {
													con3.close();
												} catch (SQLException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
											if(Integer.parseInt(row_1_12) >= Integer.parseInt(rowNo)) //��ҩͬ�м�����
											{
											
											row_1_12 = String.format("%02X",Integer.parseInt(row_1_12));//����
											col_1_12 = String.format("%02X",Integer.parseInt(col_1_12));//����
											
											
											String quantity_1 = String.format("%02X",quantity);
											
											outCode_2_12 = "AA01"+row_1_12+col_1_12+quantity_1+time1_12+time2_12+"EE"; //��ָ��
											//LogWrite.println("�³�ҩָ��:"+outCode_2);
											}else{
												break;
											}
										}else
										{
											break;
										}
										int waittime = 0;
										
										// ����
										synchronized (SerialPortEx.LOCK_SERIALPORT) {
											waittime = 0;
											LogWrite.println("[�³�ҩ]��ҩָ�"+outCode_2_12);
											SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(outCode_2_12, 16);
											while (true) {
												String code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
												if (code.equals("")) {
													if (waittime == 20) {
														LogWrite.println("[�³�ҩ]ָ�ʱ������Ӧ�����·���");
														SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(outCode_2_12, 16);
													} else if (waittime > 40) {
														LogWrite.println("[�³�ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
														break;
													}
													try {
														Thread.sleep(100);
													} catch (InterruptedException e) {
														e.printStackTrace();
													}
												} else {
													LogWrite.println("[�³�ҩ]����ָ�"+ code);
													break;
												}
												waittime++;
											}//end while
										}//end synchronized
										
										try {
											Thread.sleep(200);
										} catch (InterruptedException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}//end try
										
										LogWrite.println("[�³�ҩ]�ж��Ƿ��ҩ���");
										String outDrugReturnCode = "";
										while (!stopCheckState) {
											String code = "";
											synchronized (SerialPortEx.LOCK_SERIALPORT) {
												waittime = 0;
												SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm("AA00EE", 16);
												while (true) {
													code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
													if (code.equals("")) {
														if (waittime == 20) {
															LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·���");
															SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
														} else if (waittime > 40) {
															LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
															break;
														} 
														try {
															Thread.sleep(100);
														} catch (InterruptedException e) {
															e.printStackTrace();
														}
													} else {
														LogWrite.println("[�³�ҩ]�Ƿ��ҩ��ɷ���ָ�"+ code);
														
														//  if(!(code.substring(code.length()-4,code.length()-2)).equals(number))
//															{
//																break;
//															}
															//������һ�Σ����۷��������Ƿ�Ϊ�գ�������һ���ж�
															//����� AA 02 14 02 EE
														if(code.length()==10){
															int num_m = Integer.parseInt(code.substring(code.length()-4,code.length()-2),16); //��������
															int row_2_n = Integer.parseInt(row_1_12,16);
															int col_2_n = Integer.parseInt(col_1_12,16);
															String sql_num = "update stock_detail set Quantity=Quantity-'"+num_m+"' where Row_NO='"+row_2_n+"' and Col_NO='"+col_2_n+"' ORDER BY Quantity desc LIMIT 1";
															LogWrite.println("sql_num"+sql_num);
															Connection conn = null;
															try {
																conn = DBManager.getConnection();
																Statement statement = conn.createStatement();
																statement.execute(sql_num);
																statement.close();
															} catch (Exception e) {
																// TODO: handle exception
															} finally {
																DBManager.close(conn);
																try {
																	conn.close();
																} catch (SQLException e) {
																	// TODO Auto-generated catch block
																	e.printStackTrace();
																}
															}//end
															}
														break;
													}
													waittime++;
												}//end while
											}//end synchronized
											
											if (code.toUpperCase().indexOf("AA00") == 0) {
												// ��ʱ���
												try {
													Thread.sleep(200);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
								 			} else if (code.toUpperCase().indexOf("AA02") == 0) {
												// ��ҩ���
												outDrugReturnCode = code;
												break;
											}
//											
										}
									}
								}
							}	
					}else if(Integer.parseInt(checkValue1,16) == Integer.parseInt(number1,16)||Integer.parseInt(checkValue2,16) == Integer.parseInt(number2,16))
						{
							break;
						}else if(Integer.parseInt(checkValue1,16) > Integer.parseInt(number1,16)||Integer.parseInt(checkValue2,16) > Integer.parseInt(number2,16)){
							LogWrite.println("��ҩ�෢������е���");
						break;
					}
							
						}else if (code1.length() == 18 && code1.substring(2,4).equals("02")){ //������ͬʱ��ҩ
							//����ֵΪ00������ҩ��
							//AA0102030124FF040224FF050124FFEE
							//AA02030104020501EE
							//��ҩָ��Ķ�Ӧ�к�
							String col3_1 = Integer.parseInt(controlCode.substring(6, 8), 16)+"";
							String col3_2 = Integer.parseInt(controlCode.substring(controlCode.length()-18,controlCode.length()-16), 16)+"";
							String col3_3 = Integer.parseInt(controlCode.substring(controlCode.length()-10,controlCode.length()-8), 16)+"";
							
							int row_no = Integer.parseInt(controlCode.substring(4, 6), 16);//ת10����		
							String rowNo = row_no +"";
							String colNo1 = Integer.parseInt(code1.substring(code1.length()-14, code1.length()-12), 16)+"";
							String colNo2 = Integer.parseInt(code1.substring(code1.length()-10, code1.length()-8), 16)+"";
							String colNo3 = Integer.parseInt(code1.substring(code1.length()-6, code1.length()-4), 16)+"";
							
							if(col3_1.equals(colNo1) && col3_2.equals(colNo2) && col3_3.equals(colNo3))
							{
								if(sockState.equals("1")){
								for(int i=1;i<4;i++){
									if(i==1){
										if(code1.substring(code1.length()-12,code1.length()-10).equals("00")){
											String sql_1 = "update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo1+"'";
											excuteShutrowcol(sql_1);
											LogWrite.println("*********�������3-1**********"+sql_1);
										}
									}else if(i==2){
										if(code1.substring(code1.length()-8,code1.length()-6).equals("00")){
											String sql_1 = "update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo2+"'";
											excuteShutrowcol(sql_1);
											LogWrite.println("*********�������3-2**********"+sql_1);
										}
									}else if(i==3){
										if(code1.substring(code1.length()-4,code1.length()-2).equals("00")){
											String sql_1 = "update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo3+"'";
											excuteShutrowcol(sql_1);
											LogWrite.println("*********�������3-3**********"+sql_1);
										}
									}
								}
								}
							}
							break;
						}else if(code1.length() == 22 && code1.substring(2,4).equals("02")){//�ĸ���ͬʱ��ҩ
							//����ֵΪ00������ҩ��
							//AA0102030124FF 040224FF 050124FF 060124ffee
							//AA02030104020501EE
							//��ҩָ��Ķ�Ӧ�к�
							String col4_1 = Integer.parseInt(controlCode.substring(6, 8), 16)+"";
							String col4_2 = Integer.parseInt(controlCode.substring(controlCode.length()-26,controlCode.length()-24), 16)+"";
							String col4_3 = Integer.parseInt(controlCode.substring(controlCode.length()-18,controlCode.length()-16), 16)+"";
							String col4_4 = Integer.parseInt(controlCode.substring(controlCode.length()-10,controlCode.length()-8), 16)+"";
							
							int row_no = Integer.parseInt(controlCode.substring(4, 6), 16);//ת10����		
							String rowNo = row_no +"";
							int col_no1 = Integer.parseInt(code1.substring(code1.length()-18, code1.length()-16), 16);
							int col_no2 = Integer.parseInt(code1.substring(code1.length()-14, code1.length()-12), 16);
							int col_no3 = Integer.parseInt(code1.substring(code1.length()-10, code1.length()-8), 16);
							int col_no4 = Integer.parseInt(code1.substring(code1.length()-6, code1.length()-4), 16);
							String colNo1 = col_no1 +"";
							String colNo2 = col_no2 +"";
							String colNo3 = col_no3 +"";
							String colNo4 = col_no4 +"";
							
							if(col4_1.equals(colNo1)&& col4_2.equals(colNo2)&&col4_3.equals(colNo3)&&col4_4.equals(colNo4)){
								if(sockState.equals("1")){
								for(int i=1;i<5;i++){
									if(i==1){
										if(code1.substring(code1.length()-16,code1.length()-14).equals("00")){
											String sql_1 = "update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo1+"'";
											excuteShutrowcol(sql_1);
											LogWrite.println("*********�������4-1**********"+sql_1);
										}
									}else if(i==2){
										if(code1.substring(code1.length()-12,code1.length()-10).equals("00")){
											String sql_1 = "update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo2+"'";
											excuteShutrowcol(sql_1);
											LogWrite.println("*********�������4-2**********"+sql_1);
										}
									}else if(i==3){
										if(code1.substring(code1.length()-8,code1.length()-6).equals("00")){
											String sql_1 = "update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo3+"'";
											excuteShutrowcol(sql_1);
											LogWrite.println("*********�������4-3**********"+sql_1);
										}
									}else if(i==4){
										if(code1.substring(code1.length()-4,code1.length()-2).equals("00")){
											String sql_1 = "update stock_list set state='0' ,_mask_sync_v2=NOW() where row_no='"+rowNo+"' and col_no='"+colNo4+"'";
											excuteShutrowcol(sql_1);
											LogWrite.println("*********�������4-4**********"+sql_1);
										}
									}
								}
								}
							}
							break;
						}else{
							break;
						}
						break;
					}
					waittime1++;
				}//end while
			}//end synchronized
			
			if (code1.toUpperCase().indexOf("AA00") == 0) {
				// ��ʱ���
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 			} else if (code1.toUpperCase().indexOf("AA02") == 0) {
				// ��ҩ���
				outDrugReturnCode1 = code1;
				break;
			}
		}
		
		LogWrite.println("[��ҩ]���γ�ҩ���");
		outDrugReturnCode1 = outDrugReturnCode1.substring(4, outDrugReturnCode1.length() - 2);
		while (outDrugReturnCode1.length() > 0) {
			String col = String.format("%03d", Integer.parseInt(outDrugReturnCode1.substring(0, 2), 16));
			String qty = String.format("%02d", Integer.parseInt(outDrugReturnCode1.substring(2, 4), 16));
			
			outDrugByRowVO.getResultHashMap().put(col, qty);
			
			outDrugReturnCode1 = outDrugReturnCode1.substring(4);
		}//end while
		
		// �˴����ӳ�ҩ����
		if (OutDrugWorkT.secondOutDrug) {
			String secondOutCode = "";
			Iterator iter2 = outDrugByRowVO.getColHashMap().entrySet().iterator();
			while (iter2.hasNext()) {
				Map.Entry entry = (Map.Entry) iter2.next();
				String colCode = entry.getValue().toString();
				if (Integer.parseInt(outDrugByRowVO.getResultHashMap().get(colCode)) - Integer.parseInt(colCode.substring(3, 5)) > 0) {
					secondOutCode += String.format("%02d", outDrugByRowVO.getRowNO());
					secondOutCode += colCode.substring(0, 3);
					secondOutCode += String.format("%02d", Integer.parseInt(outDrugByRowVO.getResultHashMap().get(colCode)) - Integer.parseInt(colCode.substring(3, 5)));
				}
			}
			// ��Ҫ���в���
			if (!secondOutCode.equals("")) {
				// ������Ҫ����������
				String sOutCode = "$Q402";
				sOutCode += OutDrugWorkT.outParamters.getKey().length() + OutDrugWorkT.outParamters.getKey();
				sOutCode += secondOutCode;
				sOutCode += CRCKey.getKey() + "*";
				new CodeSend().send(ASokcetType.TCP_OUT_DRUG, sOutCode);
				OutDrugControlT.outDrugBusy = true;
				while (OutDrugControlT.outDrugBusy) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}//
	}
	public void excuteShutrowcol(String sql){
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			statement.execute(sql);
			statement.close();
		} catch (Exception e) {
			LogWrite.println("�쳣����:"+e+" //"+sql);
		} finally {
			DBManager.close(conn);
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}