package com.GestaoRotas.GestaoRotas.authConfig;

//JwtService.java

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.auth.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


public sealed  interface JwtServiceGenerator permits  JwtServiceGenerator_Impl {




	 Map<String, Object> gerarPayload(Usuario usuario);

	
	public String generateToken(Usuario usuario) ;

	 Claims extractAllClaims(String token);

	 boolean isTokenValid(String token, UserDetails userDetails);

	 boolean isTokenExpired(String token);

	 Date extractExpiration(String token);

	 Key getSigningKey();


	 String extractUsername(String token);
	 <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

  
}
