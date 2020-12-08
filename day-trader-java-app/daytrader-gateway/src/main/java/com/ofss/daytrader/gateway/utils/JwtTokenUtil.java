package com.ofss.daytrader.gateway.utils;

import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenUtil {
	
	@Value("${DAYTRADER_AUTH_PUBLIC_KEY_BASE64}")
	private String publicKeyBase64;
	
public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
	//retrieve username from jwt token
	public String getUsernameFromToken(String token) throws Exception {
	return getClaimFromToken(token, Claims::getSubject);
	}
	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) throws Exception {
	return getClaimFromToken(token, Claims::getExpiration);
	}
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws Exception {
	final Claims claims = getAllClaimsFromToken(token);
	return claimsResolver.apply(claims);
	}
	    //for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) throws Exception {
		System.out.println("inside getAllClaimsFromToken: ");
		//Load keys - start
	    byte[] publicByteArray = null;
	    PublicKey publicKey = null;
		try {
			//publicAsc = FileUtil.readFromFile("src/main/resources/rsaPublic.asc");
			publicByteArray = Utils.decodeBase64(publicKeyBase64);
		    publicKey = RSAUtil.convertByteArrayToPublicKey(publicByteArray);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
	}
	//check if the token has expired
	public Boolean isTokenExpired(String token) throws Exception {
	final Date expiration = getExpirationDateFromToken(token);
	return expiration.before(new Date());
	}
	//generate token for user
	/*public String generateToken(UserDetails userDetails) {
	Map<String, Object> claims = new HashMap<>();
	return doGenerateToken(claims, userDetails.getUsername());
	}*/
	//while creating the token -
	//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	//2. Sign the JWT using the HS512 algorithm and secret key.
	//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	//   compaction of the JWT to a URL-safe string 
	/*private String doGenerateToken(Map<String, Object> claims, String subject) {
		    PublicKey publicKey = null;
	return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
	.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
	.signWith(SignatureAlgorithm.HS512, publicKey.toString()).compact();
	}*/
	//validate token
	
	public boolean validateJwtToken(String authToken) {
		try {
			
			byte[] publicByteArray = Utils.decodeBase64(publicKeyBase64);
			PublicKey publicKey = RSAUtil.convertByteArrayToPublicKey(publicByteArray);
			Jws<Claims> parseClaimsJws = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(authToken);
			//System.out.println("signature: "+parseClaimsJws.getSignature());
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	/*public Boolean validateToken(String token, UserDetails userDetails) throws Exception {
	final String username = getUsernameFromToken(token);
	
	
	return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}*/
}
