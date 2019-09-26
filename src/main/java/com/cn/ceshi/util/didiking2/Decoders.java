package com.cn.ceshi.util.didiking2;

import com.lamfire.code.AES;
import com.lamfire.code.Base64;
import com.lamfire.json.JSON;
import com.lamfire.logger.Logger;
import com.lamfire.utils.StringUtils;
import com.lamfire.utils.URLUtils;
import com.lamfire.utils.ZipUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Decoders {
	private static final Logger LOGGER = Logger.getLogger(Decoders.class);
	/**
	 * 解码数据
	 * @param base64
	 * @return
	 */
	public static String decodeData(String base64){
		if(StringUtils.isEmpty(base64))
			return null;
		byte[] bytes = Base64.decode(base64);
		AES aes = new AES("SeVQtpynIq6ZddLN".getBytes());
		bytes = aes.decode(bytes);
		
		String result= new String(bytes,Charset.forName("utf-8"));
		result = result.trim();
		if(result.lastIndexOf("}") != -1 && (result.lastIndexOf("}") + 1) != result.length()) {
			result = result.substring(0, result.lastIndexOf("}") + 1);
		}
		LOGGER.debug("[DECODE DATA]:" +result);
		return result;
	}

	/**
	 * 解码内容
	 * @param deviceid
	 * @param base64
	 * @return
	 */
	public static String decodeContent(String deviceid,String base64){
		byte[] bytes = Base64.decode(base64);
		String key = deviceid.substring(0, 16);
		AES aes = new AES(key.getBytes());
		byte[] decode = aes.decode(bytes);
		String result= new String(decode,Charset.forName("utf-8"));
		result = result.trim();
		if(result.lastIndexOf("}") != -1 && (result.lastIndexOf("}") + 1) != result.length()) {
			result = result.substring(0, result.lastIndexOf("}") + 1);
		}
		LOGGER.debug("[DECODE CONTENT]:" +result);
		return result;
	}
	
	/**
	 * 解码用户信息，针对log3
	 * @param deviceid
	 * @param base64
	 * @return
	 */
	public static String decodeUserData(String deviceid,String base64){
		byte[] bytes = Base64.decode(base64);
		String key = deviceid.substring(0, 16);
		AES aes = new AES(key.getBytes());
		byte[] decode = aes.decode(bytes);
		String result= new String(decode,Charset.forName("utf-8"));
		result = result.trim();
		LOGGER.debug("[DECODE USERDATA]:" +result);
		return result;
	}
	
	/**
	 * 解码设备信息，针对log3
	 * @param deviceid
	 * @param base64
	 * @return
	 */
	public static String decodeDeviceData(String deviceid,String base64){
		byte[] bytes = Base64.decode(base64);
		String key = deviceid.substring(0, 16);
		AES aes = new AES(key.getBytes());
		byte[] decode = aes.decode(bytes);
		String result= new String(decode,Charset.forName("utf-8"));
		result = result.trim();
		LOGGER.debug("[DECODE DEVICEDATA]:" +result);
		return result;
	}
	
	/**
	 * 解码设备信息，兼容安卓的60055，60056的bug
	 * @param deviceid
	 * @param base64
	 * @return
	 */
	public static String decodeDeviceDataCompatAndroidBug(String deviceid,String base64){
		byte[] bytes = Base64.decode(base64);
		String key = deviceid.substring(0, 16);
		byte[] bs = key.getBytes();
		bs[15] = 0;
		AES aes = new AES(bs);
		byte[] decode = aes.decode(bytes);
		String result= new String(decode,Charset.forName("utf-8"));
		result = result.trim();
		if(result.split("\\|").length != 5) {
			result = decodeDeviceData(deviceid, base64);
		}
		LOGGER.debug("[DECODE DEVICEDATA]:" +result);
		return result;
	}
	
	public static String encodeContent(byte[] key, String content) throws UnsupportedEncodingException {
		AES aes = new AES(key);
		byte[] encode = aes.encode(content.getBytes("UTF-8"));
		return Base64.encodeBytes(encode);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
//		System.out.println(encodeContent("afb676e4517481b591f04eb396dfeb180ee31f9f".substring(0, 16).getBytes(), decodeDeviceData("516cfb084057eff9a7a7e687", "5UaQi+iUmdlSx5Awd+XNBKmpsyEsv3kfwXAaBBonh1TYXAH8yznIxWYza0LKIwaJrus3YrwUt+ekq7M6G6UMHzO3slUBn8i4zjEmaomV0nHClD8fSG7/RCbCtzuhwZq4tI5NoXL4DryVhyAq8rDrPGtQyWo+SflteRb0TL+/lkKzZsH2bCA24N9pPl846r7URLSBBqVpSUrdrY9rhIZ2hdKRbsGaxIgLk0sgvU3za7b0ks+fj0W27Gdf7u+rhxlzIhnbf9rqJPL8CdiWqioE+3l296g1BqmKM/A/xA/vo2pDQOJpDz60CaivgaPmDVzqAhpG23dVFclHmb+SPM44vILuGlWKW+sFfhCunnO0wPx7Hvp4X/RZ28u1FB5KKxh3jewWnSklcBq6WkX/mGRqjX9EbrqhQN0glVUBgktuocZ9jDw3l9Mrh+0ODXvFZl2BWqShOkm+uWNJt7DxzgJVQfvOnHPIw6AaC9r+vRyfaFcHbVzmCtZ8sEwi5YMy+oryw5+9ksVAM7YfZUB6ypL5E2Po5TsRNph+fadIikD2n5lmY2GpE6+nhYED+iJ4hx2yh7uRYMdyQf2WonAjBE7Pps/T1mgDjjUWA3/YWhBpdFJbjxHOoVr+DvV8ME0HqFUu7q9vV761Tgv7qkcvLN8dm5ZBIBuJfhs2+V64XPkO7ZcbrWyVbQhfOQ8r87a7hhIXutHRZNFYwKcr00YC8YASeIfqUr88HU7UHmNdPKuToY4=")).replaceAll("\n", ""));
//		System.out.println(decodeDeviceData("516cfb084057eff9a7a7e687", "IOjg9QL8IFfn+npLfOKwafgYGzvgLt2XuD6CkRpqIyQYvoRRr/t1gsJRrzdK0ykX"));
//		System.out.println(decodeUserData("305043b47bcc32c573c4516a4af8379c53be4687", "Ph0B7guF2v4NkhkbOvVIBZdmLOxqgOJMlQYVX8xoNZ6S07y1geWgQ6YDj6lij/r+fsyo3k1B5j1bKkckkg/sfxrovbXmHQW/+ovr9UefqDeX/zMom3JhwBL0/k9kJdWuVE+1dNCsff8JaoIVunVJQ8hW7cbeHFbk2ceg0BcK7fOJ7jqrPWLhPUthBqhUTcpHfCT4ZZRsTl/hLWbIeglItQPfE8IG9MSMoSc0i0x7kdi148M1JV8XOCVcQiO4U9hWCRtF2T74ikd+BDCKz6Dz8nvQJmxYnvj7rKyPRAkfXI4NZ4nIAuR9aWwkwVigF9eKkFCCkz81Ks68ETkK2SI6LWT05EyInTW6GPZNcJX92KlpXPPlVjHrlijBov/uM1s7rPQK2wiihbdpIsIJjB1sc597KG1KHJWofEZxjecfqOZKVrkkE3RLAZUjqUG2Whw4PU4VBW4ZJW+S6W0NlKc17zCfK1meVxeT+e/oBuVudvTsZ/k2vppvTGO3Tk2hNBNb6nnmALbK95H+o76hES5fcJmuK/NG9n0thF2O1hj94zJwoEiO5s4zFCgwldfhXUpoEqmhXt8XyMzIfgBP0Ukkfc+rTFNISjR1yqJOoM9WN61WQNmo3dcv65rf5enKQhQSLOC4m/mDNkBIZKoZfu2s1cacyKngsYsjwnDS0TG05S18MVoWwHOW81O4fky/7hL6iBBE/20r7Ggzz3hK7rgViqYxQ633SrCZDP/0+buYQTOPRgcZ8vLWrOA8hf2+P4Dd9GMrD2GcQW7X0EZ+Tp1lzg=="));
//		System.out.println(URLUtils.decode("%5BSHR%5D%3A1497423627129%7C305043b47bcc32c573c4516a4af8379c53be4687%7C1c96491d41282%7Ccom.biwang.psgod%7C1.1.4%7C60122%7C2%7Cwifi%7C22%7C%7C%7C%7CPh0B7guF2v4NkhkbOvVIBZdmLOxqgOJMlQYVX8xoNZ6S07y1geWgQ6YDj6lij%2Fr%2Bfsyo3k1B5j1bKkckkg%2FsfxrovbXmHQW%2F%2Bovr9UefqDeX%2FzMom3JhwBL0%2Fk9kJdWuVE%2B1dNCsff8JaoIVunVJQ8hW7cbeHFbk2ceg0BcK7fOJ7jqrPWLhPUthBqhUTcpHfCT4ZZRsTl%2FhLWbIeglItQPfE8IG9MSMoSc0i0x7kdi148M1JV8XOCVcQiO4U9hWCRtF2T74ikd%2BBDCKz6Dz8nvQJmxYnvj7rKyPRAkfXI4NZ4nIAuR9aWwkwVigF9eKkFCCkz81Ks68ETkK2SI6LWT05EyInTW6GPZNcJX92KlpXPPlVjHrlijBov%2FuM1s7rPQK2wiihbdpIsIJjB1sc597KG1KHJWofEZxjecfqOZKVrkkE3RLAZUjqUG2Whw4PU4VBW4ZJW%2BS6W0NlKc17zCfK1meVxeT%2Be%2FoBuVudvTsZ%2Fk2vppvTGO3Tk2hNBNb6nnmALbK95H%2Bo76hES5fcJmuK%2FNG9n0thF2O1hj94zJwoEiO5s4zFCgwldfhXUpoEqmhXt8XyMzIfgBP0Ukkfc%2BrTFNISjR1yqJOoM9WN61WQNmo3dcv65rf5enKQhQSLOC4m%2FmDNkBIZKoZfu2s1cacyKngsYsjwnDS0TG05S18MVoWwHOW81O4fky%2F7hL6iBBE%2F20r7Ggzz3hK7rgViqYxQ633SrCZDP%2F0%2BbuYQTOPRgcZ8vLWrOA8hf2%2BP4Dd9GMrD2GcQW7X0EZ%2BTp1lzg%3D%3D%7CjIc41hEEs8%2BUFpCfrsVYw0cvNJVmxDSrkt9q0%2Bku08g1gLL4ofd%2Bq%2BFUOWP7BFP6%7CmCUPzQnaD%2BrSYlsIY2enWA%3D%3D", "UTF-8"));
		try {
			System.out.println(System.currentTimeMillis());
			String m = "H4sIAAAAAAAAA83OS27CMBAA0H2vgpTOjD%2BDuywKrUJLRRUkPmJhx3EJCQKUIojlw7e7XqEXeHrbfFXunlAa1oA4pjFyIrJCSOFqLyuJaJwPxnqhKIAjgR5ZsEXixLUCK50ywXOqTsfscI1N5mzX9UlnOpNJAShIlG5NaFLU5auHO7f5rKD940v7dYld26uhW3p%2FKs5RxPfp1c1n53zxtrkMYf28Wg8f5QjC950nvw49bD%2BX87%2BvEaD1v%2F3%2BAAm%2FmF1cAQAA";
			m = URLUtils.decode(m, "utf-8");
			byte[] gzip = Base64.decode(m);
			System.out.println(new String(gzip, "UTF-8"));
			byte[] source = ZipUtils.ungzip(gzip);
			String text = new String(source,"UTF-8"); 
			System.out.println(JSON.toJSONString(StringUtils.toLines(text)));
		} catch (Exception e) {
//			e.printStackTrace();
		}
//		
//		String userDataSource = "NgHyhV1Q8gSVO+6uyal5tkWZlFkiVzI5aRuf18dvaV0q1E7x38Jo0GipFSzLONXfFcdLcwapzdL1tfr3M6pxElM+rKI45UvmQkIGgmGPiVrcfOXcgbZ+8y9FXPQHbTWgRqYTDm+Hspibt2tSsuggvZeW9GAUaz2tb2vHmxm6Qh0w8x5Hoac+qUOZTSCqfatwNVs7do+ZYBVNUN5M/HmwtkfxjN3f3CTdHC4zNHMIa/CdvQe6qStYozbu58gsu6O1wcWTdBGhCX2wQlNC/D9KjdDgcrD9sMPmCyAISVnXHqKlFu+mS4pvXFvJjrIukQcwFWjsWPLbrH6epkcZ7SikaRIRAogmUQypVvxCOR/kvas01LZE/HPTapJn5Bhn9NSw9ewFxbUdeRjBLfyNB0OnnsJMeF8XICp4BMhCihDRWbktiHGmX3f67/sWh7TmTqOuGaE1CIXD07U/UzSClkTUEqYhXgoSEs3uWF/y/2U+MNj3SBYqNbgTgAbpZqaTiMZXi/c27Abj58muygd0c0mnlOSMcUe/cUKEV+wPyA4hkfIM3jkxMO4yejLx8BL5j7VkvGO8dSLPh017STKb/3cvXYhfF7s5nxBooyontZPW8oU=";
//		System.out.println(decodeUserData("171f89b8c020dec02db9265a45b90a440b4330ca", userDataSource));
////		userDataSource = "xXGCxlUBa/LVRRmQ6C5tzR4nTUcdNm2zrYQbxXRlq/Y3A3Sa8Kuyd80FxxHo/WPal822z3QgNRwH6onorOFYU3MSNyDAWksr0vxz2DWlvBma30D2X+bWL0HzxLYANsuKE9MQESs0u1JbZRrvy4DfJCybMBhDF7FOV2MgnlFncIVYWV+GWvCkCSokad/ZT3RDF8ulOP895U30QnQb3AGIwC2j7CANF0zkQNETtZ6qylYt6PDHwSzkXiN6RifCKId/I86I/bNdw9bGUVSpG+c3qDAWmhRVkZFY7yfi0eX1JL07PGI8wz4W8vLrAmCM4xsx//bB5dbNezQr8Cu6otyKGKM3HzVAeg6rV7gGPkXW0J8mo/sV2HWJBTQLQV+AW6seF+CU3D9zWQnR4RCA6nsvVHrGW11fdKPOYctURWjlLJsE7JiFbpVNPhOeMP2BwZYRpStRUYX+plrm6NPgeIQXNjyxfNRtCtkXGcES/S6QdT+gTkBCAwEimkETMQQD3Bjx689aiWU61VBiVoVDP87SlfcxyfXRYKjwbCam39Y5Cy2ZQswqkaRK8C+6/9aALGXedzClgCb7S1irPo2Y8vx2IpKjpzdK/sykVCY7fOQujJ6hTbj41QXVnewO/DsHK0ori3BolTWTawKqX08XmifbnghABVPGZQv7Lnd4tEsWwiigN9Htc9qm23ZnKUfW4yYwOtnynpTt7sjX5xGbGYv4bzftbn1uo9J3RDh5Kz3OiYoy53G2Gqq3xTNhY1EYE+2oE1I0e2VKfqUOG31JUGJFV5KjpzdK/sykVCY7fOQujJ6hTbj41QXVnewO/DsHK0ori3BolTWTawKqX08XmifbnghABVPGZQv7Lnd4tEsWwiigN9Htc9qm23ZnKUfW4yYw0y/xbHLjqtQfzYX2DTdIkg==";
////		System.out.println(decodeUserData("e74f0126bcdb0af99946fbe0a24f44e59148e298", userDataSource));
//		Map<String, Object> kk = new HashMap<String, Object>();
//		System.out.println(String.valueOf(kk.get("sss")));
//		System.out.println(String.valueOf((String)null));//没问题
//		System.out.println(decodeDeviceData("22a90f0bdc64402b06d7702efc909a4fd638b9cc", "v6CNB+cY9fj9r5zBec4/kBE+BKo4GviFxspxC4kWVVeflPtY63zc7D+PmndnQdgE"));
	}
}
