package com.cn.ceshi.v4conf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cn.ceshi.util.data_collector.Response;
import com.lamfire.code.AES;
import com.lamfire.code.Base64;
import com.lamfire.json.JSON;
import com.lamfire.utils.StringUtils;
import com.mob.util.crypt.MobCSStandardCrypter;
import com.mob.util.crypt.rsa.MobRSACrypter;

public class AWT {
	
	private static PoolingHttpClientConnectionManager poolConnManager;
	private static final int maxTotalPool = 200;
	private static final int maxConPerRoute = 20;
	private static final int socketTimeout = 30000;
	private static final int connectionRequestTimeout = 30000;
	private static final int connectTimeout = 10000;

	private static final Logger LOGGER = LogManager.getLogger(AWT.class.getName());
	
	 private static final int KEY_SIZE = 1024;
     //唤醒解密
     private static final String PUBLIC_KEY = "e566828c25d8ac4b588217c0a437ace230917f1135abf7d98ef9346e5d8e4f99fc4d6c21bc818c0fffd6db3e3a1aca947feb987003c104d2ab803f7f06726177"; // 指数
     private static final String PRIVATE_KEY = "9cb54a4700937a68414e52d6d2e57ac6ea1fafb23bcbbbe18e133d7d11b7b5f9c670371ffa226e6442dfdb616192e75e2d553c26042867a5021856bdc236053233a5a257d59719b05afde80ff6544c759cfc59165b1bb68c0d2e442d27a59b1150bd83a7ae8e13e798bf294e20841d011c997ac4deba050833fc2bf45486e7";
     private static final String MODULES = "21af4ddbf8167dd7a7af6de3e61ec55783db252d983aeab13cacc8159437e05c59645b2319241b0f0b4a030f6023fe00c2b633d9a1e17f4e2296d30338b0d3ca0b4285bc342004f9b8a4e2696f21507717f9c396ec7d9775a4fe429d542f4bf66fc445672a7a7f3b3d96fc556b86621086c3f74967a4d5da1e2a318d87805e9b";
     private static final BigInteger RSA_PUBLIC = new BigInteger(PUBLIC_KEY, 16);
     private static final BigInteger RSA_PRIVATE = new BigInteger(PRIVATE_KEY, 16);
     private static final BigInteger RSA_MODULES = new BigInteger(MODULES, 16);
	
     static int count =0;
     
     private static final  byte[] aesKey = "19779ABVCDECSa4a".getBytes();
     //测试请求url
//     private static String url = "http://10.21.141.13:8699/v2/awt";
//     private static String url = "http://10.21.141.13:8699/awt";
     
     //线上请求url
//     private static String url = "http://api.aw.smarttel.cn/awt";
     private static String url = "http://api.aw.smarttel.cn/v2/awt";
     //请求体-未加密
     private static JSON originBody = new JSON();
     
     /**
      * 请求参数初始化
      */
     public static void init() {
    	 
    	 //公共库
    	 
//    	originBody.put("carrier", "-1");
// 		originBody.put("front", "true");
// 		originBody.put("plat", "1");
// 		originBody.put("duid", "132a9267ea753c8d9a4214888f27360018938377");
// 		originBody.put("imei", "868013025056460");
// 		originBody.put("appver", "190321");
// 		originBody.put("apppkg", "com.mob.test.COMMON");
// 		originBody.put("sysver", "5.1");
// 		originBody.put("networkType", "wifi");
// 		originBody.put("sysverint", "22");
// 		originBody.put("appkey", "243a1f570315e");
// 		originBody.put("fq", "0");
// 		originBody.put("mac", "68:3e:34:09:f0:8d");
// 		originBody.put("serialNo", "810EBM22MQSG");
// 		originBody.put("factory", "Meizu");
// 		originBody.put("lock", "1");
// 		originBody.put("home", "0");
// 		originBody.put("longitude", "121.412237");
// 		originBody.put("latitude", "31.179245");
// 		originBody.put("accuracy", "30");
// 		originBody.put("uiver", "Flyme 6.3.0.3A");
// 		originBody.put("clientTime", new Timestamp(new Date().getTime()));
// 		originBody.put("llt", "0");
// 		originBody.put("sdkver", "20190122");
// 		originBody.put("lbt", "0");
// 		originBody.put("appmd5", "7d024858396b55d7019babe85914937b");
// 		originBody.put("model", "m2 note");
    	 
    	 
    	 //独立包
    	originBody.put("carrier", "-1");
  		originBody.put("front", "true");
  		originBody.put("plat", "1");
  		originBody.put("duid", "132a9267ea753c8d9a4214888f27360018938377");
  		originBody.put("imei", "868013025056460");
  		originBody.put("appver", "190319");
  		originBody.put("apppkg", "com.mob.test.common.awk");
  		originBody.put("sysver", "5.1");
  		originBody.put("networkType", "wifi");
  		originBody.put("sysverint", "22");
  		originBody.put("appkey", "243a1f570315e");
  		originBody.put("fq", "1");
  		originBody.put("mac", "68:3e:34:09:f0:8d");
  		originBody.put("serialNo", "810EBM22MQSG");
  		originBody.put("factory", "Meizu");
  		originBody.put("lock", "1");
  		originBody.put("home", "0");
  		originBody.put("longitude", "121.412217");
  		originBody.put("latitude", "31.179301");
  		originBody.put("accuracy", "30");
  		originBody.put("uiver", "Flyme 6.3.0.3A");
  		originBody.put("clientTime", new Timestamp(new Date().getTime()));
  		originBody.put("llt", "0");
  		originBody.put("lbt", "0");
  		originBody.put("appmd5", "7d024858396b55d7019babe85914937b");
  		originBody.put("dc", "DC/2");
  		originBody.put("sdkver", "2019031900");
  		originBody.put("awe", "1");
  		originBody.put("model", "m2 note");
  		
     }
     
     static {
         MobCSStandardCrypter.setRSACrypter(new MobRSACrypter(RSA_MODULES, RSA_PRIVATE, RSA_PUBLIC, KEY_SIZE));
     }
     /**
      * 请求参数加密
      * @return
      * @throws Exception
      */
     private static String inEncode()throws Exception {
    	 
	       
	        AES aes = new AES(aesKey);
	        byte[] data = aes.encode(originBody.toJSONString().getBytes());
	        String retStr = "";

	        try {
	        	MobRSACrypter mobRSACrypter = new MobRSACrypter(RSA_MODULES, RSA_PRIVATE, RSA_PUBLIC, KEY_SIZE);
	            byte[] aesKeyRSA = mobRSACrypter.encodeByPublicKey(aesKey);
//	            System.out.println("aesKeyRSA Len: " + aesKeyRSA.length);
	            byte[] mBytes = new byte[8 + data.length + aesKeyRSA.length];
	            ByteBuffer b = ByteBuffer.allocate(4);
	            b.putInt(aesKeyRSA.length);
	            System.arraycopy(b.array(), 0, mBytes, 0, 4);
	            System.arraycopy(aesKeyRSA, 0, mBytes, 4, aesKeyRSA.length);
	            b = ByteBuffer.allocate(4);
	            b.putInt(data.length);
	            System.arraycopy(b.array(), 0, mBytes, 4 + aesKeyRSA.length, 4);
	            System.arraycopy(data, 0, mBytes, 8 + aesKeyRSA.length, data.length);
	            retStr = Base64.encode(mBytes).replaceAll("\n", "");
//	            System.out.println("encrypt success!=====" + retStr);
	        } catch (Throwable var9) {
	            var9.printStackTrace();
	        }

//	        System.out.println("decrypt success!=====" + MobCSStandardCrypter.decodeData(retStr, false));
	        return retStr;
	    }
     	
     /**
      * 响应数据解密
      * @param originResponse
      * @return
      * @throws Exception
      */
         private static JSON outDecode(String originResponse)throws Exception {
        	 JSON jsonData = new JSON();
             AES aes = new AES(aesKey);
             byte[] data = aes.decode(Base64.decode(originResponse));
               String dataStr = new String(data);
               if(!StringUtils.isBlank(dataStr)){
                   jsonData = Response.successJSON(dataStr);
               }
             return jsonData;
         }
     
     /**
      * 调用接口
      * @return
     * @throws Exception 
      */
     public static JSON post(String requestBody) throws Exception {
    	 
    	 JSON result = new JSON();
    	 HttpPost httpPost = new HttpPost(url);
    	 //IP地址湖北武汉
         httpPost.setHeader("x-forwarded-for", "202.103.44.150");
         httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
         
         
         StringEntity se = new StringEntity(requestBody,"UTF-8");
         httpPost.setEntity(se);
        
         int status = -111;
         try {
             CloseableHttpResponse response = getConnection().execute(httpPost);
             status = response.getStatusLine().getStatusCode();
             HttpEntity backEntity = response.getEntity();
             if (status == 200) {
                 InputStream resStream = backEntity.getContent();
                 BufferedReader br = new BufferedReader(new InputStreamReader(resStream));
                 StringBuffer resBuffer = new StringBuffer();
                 String resTemp = "";
                 while ((resTemp = br.readLine()) != null) {
                     resBuffer.append(resTemp);
                 }
                 result.put("result", resBuffer.toString());
             } else {
                 //记日志2
                 LOGGER.error("[url]=" + url + "   [status=]=" + status);
             }

         } catch (Exception e) {
             e.printStackTrace();
             //记日志3
             LOGGER.error("[url]=" + url);
             httpPost.abort();
         }
         result.put("status", status);
         return result;
 	}
     
     private static CloseableHttpClient getConnection() {
 		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
 				.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
 		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolConnManager)
 				.setDefaultRequestConfig(requestConfig).build();
 		return httpClient;
 	}

     
	public static void main(String args[])throws Exception {
		for(int i=0;i<300;i++) {
			init();
			String requestBody = inEncode();
			System.out.println("请求明文:  "+originBody.toJSONString());
			System.out.println("请求密文:  "+inEncode());
			System.out.println("响应密文:  "+post(requestBody));
			if(post(requestBody).containsKey("result")) {
				String originResponse = (String) post(requestBody).get("result");
				JSON result = JSON.fromJSONString(outDecode(originResponse).toJSONString());
				System.out.println("响应明文:  "+result.getString("resultData"));
			}
			count++;
			System.out.println("------------------------------------------------------"+count);
			Thread.sleep(3000);
		}
		
	}
		        

}
