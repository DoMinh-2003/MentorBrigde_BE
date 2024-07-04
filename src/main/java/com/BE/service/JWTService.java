package com.BE.service;


import com.BE.exception.exceptions.NotAllowException;
import com.BE.model.entity.User;
import com.BE.model.request.IntrospectRequest;
import com.BE.model.response.IntrospectResponse;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.text.ParseException;

import java.util.Base64;
import java.util.Date;

@Service
public class JWTService {

    private final String SECRET_KEY = "HT4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d7490630407c";
    private final long EXPIRATION = 1 * 24 * 60 * 60 * 1000;

    //check valid token
//    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
//        var signedJWT = verifyToken(request.getToken(), true);
//
//        var jit = signedJWT.getJWTClaimsSet().getJWTID();
//        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//
//        InvalidatedToken invalidatedToken =
//                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
//
//        invalidatedTokenRepository.save(invalidatedToken);
//
//        var username = signedJWT.getJWTClaimsSet().getSubject();
//
//        var user =
//                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
//
//        var token = generateToken(user);
//
//        return AuthenticationResponse.builder().token(token).authenticated(true).build();
//    }

    String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date(System.currentTimeMillis()))
                .expirationTime(new Date(System.currentTimeMillis()+60*60*1000))
                .claim("scope", "ROLE_" + user.getRole())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }


    public JWTClaimsSet verifyToken(String token) throws ParseException, JOSEException {

            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(Base64.getDecoder().decode(SECRET_KEY));

            if (signedJWT.verify(verifier)) {
                return signedJWT.getJWTClaimsSet();
            } else {
                throw new NotAllowException("Token invalid");
            }

    }





}
