package com.oweu.authorizationserver.oridinary.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

import com.oweu.authorizationserver.oridinary.constant.SystemConstant;
import com.oweu.authorizationserver.oridinary.Entity.resultRender;
/**
 * jwt加密、解密的工具类
 */
public class JwtUtils {
	/**
	 * 签发JWT
	 *
	 */
	public static String createJWT(String id, String subject, long ttlMillis) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		SecretKey secretKey = generalKey();
		JwtBuilder builder = Jwts.builder()
				.setId(id)
				.setSubject(subject)   // 主题
				.setIssuer("Authserver")     // 签发者
				.setIssuedAt(now)      // 签发时间
				.signWith(signatureAlgorithm, secretKey); // 签名算法以及密匙
		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date expDate = new Date(expMillis);
			builder.setExpiration(expDate); // 过期时间
		}
		return builder.compact();
	}
	/* 验证JWT
	 */
	public static resultRender validateJWT(String jwtStr) {
		resultRender resultRender = new resultRender();
		Claims claims = null;
		try {
			claims = parseJWT(jwtStr);
			resultRender.setSuccess(true);
			resultRender.setClaims(claims);
		} catch (ExpiredJwtException e) {
			resultRender.setErrCode(SystemConstant.JWT_ERRCODE_EXPIRE);
			resultRender.setSuccess(false);
		} catch (SignatureException e) {
			resultRender.setErrCode(SystemConstant.JWT_ERRCODE_FAIL);
			resultRender.setSuccess(false);
		} catch (Exception e) {
			resultRender.setErrCode(SystemConstant.JWT_ERRCODE_FAIL);
			resultRender.setSuccess(false);
		}
		return resultRender;
	}
	public static SecretKey generalKey() {
		byte[] encodedKey = Base64.decode(SystemConstant.JWT_SECERT);
	    SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	    return key;
	}

	/*
	 * 解析JWT字符串
	 */
	public static Claims parseJWT(String jwt) throws Exception {
		SecretKey secretKey = generalKey();
		return Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(jwt)
			.getBody();
	}
	public static void main(String[] args) throws InterruptedException {
		//小明失效 10s
		String sc = createJWT("1","小明", 3000);
		System.out.println(sc);
		System.out.println(validateJWT(sc).getErrCode());
		System.out.println(validateJWT(sc).getClaims().getId());
		Thread.sleep(3000);
		System.out.println(validateJWT(sc).getClaims());
	}
}
