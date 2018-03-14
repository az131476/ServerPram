package us;

import java.io.File;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *  Copyright (c) 2014 Aisino Corporation Inc.
 * 	All rights reserved.
 *  
 *  文  件  名:	Algorithm.java
 *  功能说明:	RSA加解密/SHA1WithRSA签名验签/DES加解密算法
 *  创  建  人:	zhuxing (zhuxing_ncut@126.com)
 *  日         期:	2014.11.19
 */

public class Algorithm {
   
	private static String module;
	private static String exponentString;
	private static String delement;
	private static String decryptString;
	private static String encryptString = "123abc航天信息人1234";
	private static String encryptString2 = "1231中国人民11111111abc航天信息人123456789";
	
	private static String desKey = "123abc45";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Base64DecodingException {
		
		//------------------------ test RSA encrypt/decrypt ----------------------------
		System.out.println(encryptString);
		
        byte[] en = RSAEncrypt(encryptString, "PubKey.dat"); 
        decryptString = Base64.encode(en);
        System.out.println(decryptString);
        
        byte[] enTest = null;
        enTest = Base64.decode(decryptString);
//        enTest = Base64.decode("NlTlSjMWvJ55KgmZWcn44Rwe0ZrAkofkkQuLFxnKsXfMjwrs60rzcHgTewJN8hM1JJrnTQwpNYU3JbdYnMfry0OEL+n7+dhZTqB7EyzV0v4JEHNoGG+qqthqMNgqAoLKBEL7ILTP/PtCC97+3dJ3ovxY1znfYoLg8QXCA5jIJZY=");
        System.out.println(enTest.length);
        System.out.println(enTest);
        
        System.out.println(new String(RSADecrypt(enTest, "PriKey.dat")));
        System.out.println("test end1");
        
        System.out.println(encryptString2);
        String strEncrypt = RSAEncryptStr(encryptString2, "PubKey.dat");
        System.out.println(strEncrypt);
        
//        strEncrypt = "NlTlSjMWvJ55KgmZWcn44Rwe0ZrAkofkkQuLFxnKsXfMjwrs60rzcHgTewJN8hM1JJrnTQwpNYU3JbdYnMfry0OEL+n7+dhZTqB7EyzV0v4JEHNoGG+qqthqMNgqAoLKBEL7ILTP/PtCC97+3dJ3ovxY1znfYoLg8QXCA5jIJZY=";
        String strDecrypt = RSADecryptStr(strEncrypt, "PriKey.dat");
        System.out.println(strDecrypt);        
        System.out.println("test end2");
		
		//------------------------ test sha1withrsa sign/verifysign ----------------------------
		String sign = Sign(encryptString2, "PriKey.dat");
		System.out.println(sign);
		
		System.out.println(encryptString2);
		
//		encryptString2 = "1111266273733abadf567中国7373hdh航天信息8889abc";
//		sign = "uVKI8gIWzBD2RErxQKcMnX+6IU3F0EsLkQ5P5NoCiDlfcim43HDIX9RehXohxIOHEoaMJAmVCAwlfyomGZXgaBkrl/VURe7yxQrtDvzRg6Tt0mXJEn4jwKHOqlNbPxfWjRI5zd8MEc5ilSd55NKrXXnfLdZ7gZQr8/u2//pafpg=";
		boolean bVerify = VerifySign(encryptString2, sign, "PubKey.dat");
//		boolean bVerify = VerifySign("111111111", sign, "PubKey.dat");
		if (bVerify){
			System.out.println("VerifySign result is true.");
		}else{
			System.out.println("VerifySign result is false.");
		}
		
		//------------------------ test des encrypt/decrypt ----------------------------
		desKey = Rands();
		System.out.println(desKey);
		
		
		String strDesEncrypt = DESEncrypt(encryptString2, desKey);
		System.out.println(strDesEncrypt);
		
//		desKey = "12345678";
//		strDesEncrypt = "pawrnLU+6DalRW9Iw/eOkr3fv4gvf+jwdkC5uBI6AmMfs5P+3Tqtl3dB9GjWaNmn";
		String strDesDecrypt = DESDecrypt(strDesEncrypt, desKey);
		
		System.out.println(strDesDecrypt);
		System.out.println("test end3");	
		
	}	
	
	/// <summary>
	/// RSA字符串加密（加密最大长度为117字节）
	/// </summary>
	/// <param name="data">待加密字符串</param>
	/// <param name="keyFile">公钥文件路径</param>	
	/// <returns>加密后的字节数组base64后的字符串</returns> 
    public static String RSAEncryptStr(String data, String keyFile) {
        try {
        	KeyFactory fact = KeyFactory.getInstance("RSA");
        	
        	// 读取公钥
        	readKeyFromFile(keyFile);        	
            byte[] modulusBytes = Base64.decode(module);
            byte[] exponentBytes = Base64.decode(exponentString);
            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger exponent = new BigInteger(1, exponentBytes);
            RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponent);            
            PublicKey pubKey = fact.generatePublic(rsaPubKey);

            Cipher cipher = Cipher.getInstance("RSA");            
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);            
            byte[] byteCipherData = cipher.doFinal(data.getBytes());
            
            String strEncrypt = Base64.encode(byteCipherData);
            return strEncrypt;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

	/// <summary>
	/// RSA字符串解密（解密最大长度是128字节）
	/// </summary>
    /// <param name="data">待解密字符串</param>
	/// <param name="keyFile">私钥文件路径</param>	
	/// <returns>解密字符串</returns> 
    public static String RSADecryptStr(String data, String keyFile) {
        try {
        	KeyFactory factory = KeyFactory.getInstance("RSA");
        	
        	// 读取私钥
        	readKeyFromFile(keyFile);        	
            byte[] expBytes = Base64.decode(delement);
            byte[] modBytes = Base64.decode(module);
            BigInteger modules = new BigInteger(1, modBytes);
            BigInteger exponent = new BigInteger(1, expBytes);
            RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modules, exponent);
            PrivateKey privKey = factory.generatePrivate(privSpec);
            
            Cipher cipher = Cipher.getInstance("RSA");            
            cipher.init(Cipher.DECRYPT_MODE, privKey);            
            byte[] byteEncrypt = Base64.decode(data);
            String strDecrypt = new String(cipher.doFinal(byteEncrypt));
            return strDecrypt;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/// <summary>
	/// RSA字符串加密（加密最大长度为117字节）
	/// </summary>
    /// <param name="data">待加密字符串</param>
	/// <param name="keyFile">公钥文件路径</param>	
	/// <returns>加密后的字节数组</returns> 
    public static byte[] RSAEncrypt(String data, String keyFile) {
        try {
        	KeyFactory fact = KeyFactory.getInstance("RSA");
        	
        	// 读取公钥
        	readKeyFromFile(keyFile);        	
            byte[] modulusBytes = Base64.decode(module);
            byte[] exponentBytes = Base64.decode(exponentString);
            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger exponent = new BigInteger(1, exponentBytes);
            RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponent);            
            PublicKey pubKey = fact.generatePublic(rsaPubKey);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] byteCipherData = cipher.doFinal(data.getBytes());
            return byteCipherData;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	/// <summary>
	/// RSA解密字节数组解密（解密最大长度是128字节）
	/// </summary>
    /// <param name="data">待解密字节数组</param>
	/// <param name="keyFile">私钥文件路径</param>	
	/// <returns>解密后的字节数组</returns> 
    public static byte[] RSADecrypt(byte[] data, String keyFile) {
        try {
        	KeyFactory factory = KeyFactory.getInstance("RSA");
        	
        	// 读取私钥
        	readKeyFromFile(keyFile);        	
            byte[] expBytes = Base64.decode(delement);
            byte[] modBytes = Base64.decode(module);
            BigInteger modules = new BigInteger(1, modBytes);
            BigInteger exponent = new BigInteger(1, expBytes);
            RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modules, exponent);
            PrivateKey privKey = factory.generatePrivate(privSpec);
            
            Cipher cipher = Cipher.getInstance("RSA");            
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            byte[] byteDecrypted = cipher.doFinal(data);
            return byteDecrypted;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
	/// <summary>
	/// 从C#生成的RSA算法密钥文件中读取公私钥对应的字符串
	/// </summary>
	/// <param name="keyFile">公私钥文件路径</param>
	/// <returns></returns> 
    public static void readKeyFromFile(String keyFile){
    	try{ 		   
 		   File  xmlFile = new File(keyFile); 		   
 		   DocumentBuilderFactory  builderFactory =  DocumentBuilderFactory.newInstance(); 		   
 		   DocumentBuilder   builder = builderFactory.newDocumentBuilder(); 		   
 		   org.w3c.dom.Document  doc = builder.parse(xmlFile); 		   
 		   doc.getDocumentElement().normalize(); 		   
// 		   System.out.println("Root  element: " + doc.getDocumentElement().getNodeName());
 		   
 		   if(doc.hasChildNodes()){
 			  NodeList nodeList = doc.getChildNodes();
 			  for (int i = 0; i < nodeList.getLength(); i++){
				Node  node = (Node)nodeList.item(i);
					if((node.getNodeType() == Node.ELEMENT_NODE) && ("RSAKeyValue".equals(node.getNodeName()))){
						if (node.hasChildNodes()){
							getKey(node.getChildNodes());
						}
					}
 			  	}
 		   	}
	    }catch(Exception  e){	 	   
	 	   e.printStackTrace();	 	   
	    }    	
    }
    
    /// <summary>
  	/// 读取公私钥字符串
  	/// </summary>
  	/// <param name="nodeList">RSAKeyValue子节点列表</param>
  	/// <returns></returns> 
	private static void getKey(NodeList nodeList){
		 for(int i = 0;  i < nodeList.getLength(); i++){   		 
			 Node  node = (Node)nodeList.item(i);
			 if(node.getNodeType() == Node.ELEMENT_NODE){   			 
				 if("Modulus".equals(node.getNodeName())){
					 module = node.getTextContent();
				 }
				 else if ("Exponent".equals(node.getNodeName())){
					 exponentString = node.getTextContent(); 
				 }
				 else if ("D".equals(node.getNodeName())){
					 delement = node.getTextContent();
				 }
			 }   	
		 }
	}
	
	/// <summary>
	/// SHA1WITHRSA签名
	/// </summary>
	/// <param name="data">待签名字符串</param>
	/// <param name="keyFile">私钥文件路径</param>	
	/// <returns>签名后的字节数组base64后的字符串</returns> 
    public static String Sign(String data, String keyFile) {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            
            // 读取公私钥
        	readKeyFromFile(keyFile);
        	
        	byte[] expBytes = Base64.decode(delement);
            byte[] modBytes = Base64.decode(module);
            BigInteger modules = new BigInteger(1, modBytes);
            BigInteger exponent = new BigInteger(1, expBytes);            
            RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modules, exponent);            
            PrivateKey privKey = factory.generatePrivate(privSpec);
            
            Signature sign = Signature.getInstance("SHA1withRSA"); 
            sign.initSign(privKey);
            sign.update(data.getBytes());
            byte[] byteSign = sign.sign();
            
            String strSign = Base64.encode(byteSign);
            return strSign;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    
	/// <summary>
	/// SHA1WITHRSA验证签名
	/// </summary>
	/// <param name="data">明文字符串</param>
	/// <param name="sign">签名字符串</param>
    /// <param name="keyFile">公钥文件路径</param>	
	/// <returns>验签结果，true验签通过，false验签未通过</returns> 
    public static boolean VerifySign(String data, String sign, String keyFile) {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            
            // 读取公私钥
        	readKeyFromFile(keyFile);
        	
        	byte[] modulusBytes = Base64.decode(module);
            byte[] exponentBytes = Base64.decode(exponentString);
            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger exponent = new BigInteger(1, exponentBytes);        
            RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponent);
            PublicKey pubKey = factory.generatePublic(rsaPubKey);
            
            Signature signa = Signature.getInstance("SHA1withRSA"); 
            signa.initVerify(pubKey);
            signa.update(data.getBytes());
            
            return signa.verify(Base64.decode(sign));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	
    /// <summary>
  	/// DES加密
  	/// </summary>
  	/// <param name="encryptString">待加密字符串</param>
	/// <param name="encryptKey">des密钥（8字节）</param>
  	/// <returns>加密后的字符串</returns> 
	public static String DESEncrypt(String encryptString, String encryptKey) {
		try {			
			IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
			SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
			
			return Base64.encode(encryptedData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}

    /// <summary>
  	/// DES解密
  	/// </summary>
	/// <param name="decryptString">解密字符串</param>
	/// <param name="encryptKey">des密钥（8字节）</param>
  	/// <returns>解密后的字符串</returns> 
	public static String DESDecrypt(String decryptString, String decryptKey) {
		try {
			byte[] byteMi = Base64.decode(decryptString);
			
			IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
			SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			byte decryptedData[] = cipher.doFinal(byteMi);
			
			String strData = new String(decryptedData);
			return strData;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    /// <summary>
  	/// 产生8位随机数（“0-9”+“a-z”）
  	/// </summary>
  	/// <returns>8位随机数</returns> 
	public static String Rands() {
        Random rd = new Random(); // 创建随机对象
        String n = "";            //保存随机数
        int rdGet; // 取得随机数
        do {
            if (rd.nextInt() % 2 == 1) {
            rdGet = Math.abs(rd.nextInt()) % 10 + 48; // 产生48到57的随机数(0-9的键位值)
           } else {
             rdGet = Math.abs(rd.nextInt()) % 26 + 97; // 产生97到122的随机数(a-z的键位值)
           }
        char num1 = (char) rdGet;            //int转换char
        String dd = Character.toString(num1);
        n += dd;
        
        } while (n.length() < 8);// 设定长度，此处假定长度小于8
        
        return n;
	}
}
