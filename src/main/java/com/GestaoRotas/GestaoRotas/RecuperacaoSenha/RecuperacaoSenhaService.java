package com.GestaoRotas.GestaoRotas.RecuperacaoSenha;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;


import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.GestaoRotas.GestaoRotas.DTO.recuperacaoSenhaDTO;
import com.GestaoRotas.GestaoRotas.Email.EmailService;
import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.Usuario;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


 


public sealed interface RecuperacaoSenhaService permits  RecuperacaoSenha_Impl {
	

Map<String, String> solicitarRecuperacaoSenha(String username, String email);

 boolean verificarRespostaSeguranca(String username, String respostaSeguranca);



 boolean redefinirSenhaComToken(String token, String novaSenha);


 boolean redefinirSenhaComVerificacao(@Valid recuperacaoSenhaDTO dto) ;


}
