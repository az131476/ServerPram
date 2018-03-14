package us;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


public class PrescriptionPro {
	public ProcessV getPatient(String winArr) throws Exception {
		ProcessV processV = null;
		Connection conn = null;
		Thread.sleep(1000);
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			ResultSet rSet = statement.executeQuery("select patientID,fetchWindow from prescriptionlist where procFlg=0 and fetchwindow in('"+winArr+"') order by procDate asc");
			if (rSet.next()) {
				processV = new ProcessV();
				processV.setPatientID(rSet.getString(1));
				processV.setWindow(rSet.getInt(2));
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
		return processV;
	}
	
	public boolean setProcess_1(String patientID, int window) throws Exception{
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("update prescriptionlist set procflg=1,procDate=now() where procflg=0 and patientID='"+patientID+"' and fetchwindow="+window);
			int count = statement.getUpdateCount();
			statement.close();
			return count > 0;
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		} finally {
			DBManager.close(conn);
			conn.close();
		}
	}

	public void setProcess_3(String procCode) throws Exception{
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			statement.execute("update prescriptionlist set procflg=3,procDate=now() where procCode='"+procCode+"'");
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		} finally {
			DBManager.close(conn);
			conn.close();
		}
	}
	
	public void setProcess_4(String procCode) throws Exception{
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			statement.execute("update prescriptionlist set procflg=4,procDate=now() where procCode='"+procCode+"'");
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		} finally {
			DBManager.close(conn);
			conn.close();
		}
	}
	
	public void setProcess_2_(String procCode, int procFlg) throws Exception{
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			statement.execute("update prescriptionlist set procflg="+procFlg+",procDate=now() where procCode='"+procCode+"'");
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		} finally {
			DBManager.close(conn);
			conn.close();
		}
	}
	
	public void setProcess_2(String procCode, String patientID) throws Exception{
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			statement.execute("update prescriptionlist set procflg=2,procDate=now(),procCode='"+procCode+"' where patientID='"+patientID+"' and procFlg=1");
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		} finally {
			DBManager.close(conn);
			conn.close();
		}
	}
	
	public void createOutDetail(String procCode) {
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("delete from process_detail");
			
			ResultSet rSet = statement.executeQuery("select DrugCode,sum(quantity) as quantity from prescriptiondetail a left join prescriptionlist b on a.PrescriptionNo=b.PrescriptionNo where procCode='"+procCode+"' group by drugCode");
			while (rSet.next()) {
				String drugCode = rSet.getString(1);
				int quantity = rSet.getInt(2);
				createOutDetail(procCode, drugCode, quantity);
			}
			rSet.close();
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
	
	private void createOutDetail(String procCode, String drugCode, int quantity) {
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			Statement st = conn.createStatement();
			Statement st2 = conn.createStatement();
			ResultSet rSet = statement.executeQuery("select a.Stock_ID,sum(Quantity) as quantity from stock_detail a left join stock_list b on a.stock_id=b.stock_id where a.drug_code='"+drugCode+"' and state=1 group by a.stock_id order by quantity desc");
			while (rSet.next()) {
				int stockID = rSet.getInt(1);
				int stockQuantity = rSet.getInt(2);
				
				int outQuantity = quantity < stockQuantity ? quantity : stockQuantity;
				
				quantity -= outQuantity;
				
				ResultSet rSet2 = st.executeQuery("select stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,quantity,bnotp,exp_date from stock_detail where stock_id="+stockID+" order by pos_no");
				while (rSet2.next()) {
					String sguid = rSet2.getString(1);
					int qty = rSet2.getInt(8);
					if (qty <= outQuantity) {
						// insert Process_detail
						st2.execute("insert into Process_detail(procCode,stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,quantity,bnotp,exp_date) select '"+procCode+"',stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,quantity,bnotp,exp_date from stock_detail where stock_guid='"+sguid+"'");
						
						//delete
						st2.execute("delete from stock_detail where stock_guid='"+sguid+"'");
						outQuantity -= qty;
					} else {
						// insert Process_detail
						st2.execute("insert into Process_detail(procCode,stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,quantity,bnotp,exp_date) select '"+procCode+"',stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,"+outQuantity+",bnotp,exp_date from stock_detail where stock_guid='"+sguid+"'");
						
						//update
						st2.execute("update stock_detail set quantity=quantity-"+outQuantity+" where stock_guid='"+sguid+"'");
						outQuantity = 0;
					}
					
					if (outQuantity == 0) {
						break;
					}
				}
				rSet2.close();
				// 已经满足出药数量
				if (quantity < 1) {
					break;
				}
			}
			rSet.close();
			statement.close();
			st.close();
			st2.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
	
	public String createOutCode(String procCode) {
		String outCode = "";
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			ResultSet rSet = statement.executeQuery("select row_no,col_no,sum(quantity) from process_detail where procCode='"+procCode+"' group by stock_id");
			while (rSet.next()) {
				outCode += String.format("%02d", rSet.getInt(1));
				outCode += String.format("%03d", rSet.getInt(2));
				outCode += String.format("%02d", rSet.getInt(3));
				outCode += String.format("%02d", 32);
				outCode += String.format("%02d", 64);
				outCode += "0";
			}
			rSet.close();
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		} finally {
			DBManager.close(conn);
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return outCode;
	}
	
	public void finishDetail(String procCode, String code) {
		//0100100 0202000 1100100
		HashMap<String, Integer> outHashMap = new HashMap<String, Integer>();
		while (code.length() > 0) {
			outHashMap.put(code.substring(0, 5), Integer.parseInt(code.substring(5, 7)));
			code = code.substring(7);
		}
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			Statement st = conn.createStatement();
			ResultSet rSet = statement.executeQuery("select stock_id,row_no,col_no,sum(quantity) from process_detail group by stock_id");
			while (rSet.next()) {
				String rc = String.format("%02d%03d", rSet.getInt(2), rSet.getInt(3));
				if (outHashMap.get(rc) == null) {
					finishDetail(procCode, rSet.getInt(1), 0, rSet.getInt(4));
				} else if (outHashMap.get(rc) == rSet.getInt(4)) {
				//	String sqlStr = "insert into equipment_out";
				//	st.executeUpdate(sqlStr);
				} else if (outHashMap.get(rc) > rSet.getInt(4)) {
					finishDetail(procCode, rSet.getInt(1), outHashMap.get(rc), rSet.getInt(4));
				} else if (outHashMap.get(rc) < rSet.getInt(4)) {
					finishDetail(procCode, rSet.getInt(1), outHashMap.get(rc), rSet.getInt(4));
				}
			}
			rSet.close();
			statement.close();
			st.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
	
	private void finishDetail(String procCode, int stockID, int qty1, int qty2) {
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			Statement st = conn.createStatement();
			if (qty1 > qty2) {
				// 多发了，减库存
				int outQuantity = qty1 - qty2;
				Statement st2 = conn.createStatement();
				ResultSet rSet2 = st.executeQuery("select stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,quantity,bnotp,exp_date from stock_detail where stock_id="+stockID+" order by pos_no");
				while (rSet2.next()) {
					String sguid = rSet2.getString(1);
					int qty = rSet2.getInt(8);
					if (qty <= outQuantity) {
						//delete
						st2.execute("delete from stock_detail where stock_guid='"+sguid+"'");
						outQuantity -= qty;
					} else {
						//update
						st2.execute("update stock_detail set quantity=quantity-"+outQuantity+" where stock_guid='"+sguid+"'");
						outQuantity = 0;
					}
					
					if (outQuantity == 0) {
						break;
					}
				}
				rSet2.close();
				
				// 插入明细
				
				
				st2.close();
			} else if (qty1 == 0) {
				// 没发，库存恢复，直接插入库存，POS_NO可能会重复
				String sqlStr = "insert into stock_detail(Stock_GUID,Stock_ID,Eqp_ID,Row_NO,Col_NO,Pos_NO,Drug_Code,Quantity,BNOTP,Exp_Date) " +
						"select uuid(),Stock_ID,Eqp_ID,Row_NO,Col_NO,Pos_NO,Drug_Code,Quantity,BNOTP,Exp_Date from process_detail where procCode='"+procCode+"'";
				st.executeUpdate(sqlStr);
			
				// 插入明细
				
			} else {
				// 少发
				Statement st2 = conn.createStatement();
				ResultSet rSet = statement.executeQuery("select Stock_GUID,quantity from process_detail where stock_id="+stockID+" order by pos_no");
				while (rSet.next()) {
					String sGuid = rSet.getString(1);
					if (qty1 == 0) {
						// 恢复库存
						String sqlStr = "insert into stock_detail(Stock_GUID,Stock_ID,Eqp_ID,Row_NO,Col_NO,Pos_NO,Drug_Code,Quantity,BNOTP,Exp_Date) " +
								"select uuid(),Stock_ID,Eqp_ID,Row_NO,Col_NO,Pos_NO,Drug_Code,Quantity,BNOTP,Exp_Date from process_detail where Stock_GUID='"+sGuid+"'";
						st2.executeUpdate(sqlStr);
					} else {
						if (qty1 < rSet.getInt(2)) {
							// 插入明细qty1
							
							// 恢复库存，rSet.getInt(2)-qty1
							String sqlStr = "insert into stock_detail(Stock_GUID,Stock_ID,Eqp_ID,Row_NO,Col_NO,Pos_NO,Drug_Code,Quantity,BNOTP,Exp_Date) " +
									"select uuid(),Stock_ID,Eqp_ID,Row_NO,Col_NO,Pos_NO,Drug_Code,"+(rSet.getInt(2)-qty1)+",BNOTP,Exp_Date from process_detail where Stock_GUID='"+sGuid+"'";
							st2.executeUpdate(sqlStr);
							
							qty1 = 0;
						} else if (qty1 >= rSet.getInt(2)) {
							// 插入明细rSet.getInt(2)
							
							qty1 -= rSet.getInt(2);
						}
					}
				}
				rSet.close();
				st2.close();
			}
			
			// 锁定
			if (OutParamters.isLockErrStock()) {
				statement.executeQuery("update stock_list set state=0 where stock_id="+stockID);
			}
			statement.close();
			st.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
	
	public void finfishClear(String procCode) {
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("delete from process_detail where procCode='"+procCode+"'");
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
