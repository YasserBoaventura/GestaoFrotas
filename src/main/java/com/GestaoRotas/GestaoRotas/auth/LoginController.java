package com.GestaoRotas.GestaoRotas.auth;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.GestaoRotas.GestaoRotas.DTO.AutoCadastroDTO;
import com.GestaoRotas.GestaoRotas.DTO.usuarioUpdateDTO;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LoginController {
 
	private final PasswordEncoder passwordEncoder;
	private final LoginService loginService; 
	private final LoginRepository loginRepository;
	
	public LoginController(LoginService loginService, LoginRepository loginRepository, PasswordEncoder passwordEncoder) {
		this.loginRepository=loginRepository;
		this.loginService=loginService;
		this.passwordEncoder=passwordEncoder;
	
	}
	
// CORRETO - POST para login
@PostMapping("/login")
private ResponseEntity<?> logar(@RequestBody Login login) {
	try {
        String token = loginService.logar(login);
        if(token.isEmpty()) {
        	throw new IllegalArgumentException("Credenciais inválidas");
        }
        return ResponseEntity.ok(token);
        } catch(Exception e){
       // Retorna um objeto JSON com a mensagem de erro
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        System.out.println("Na console  "+e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
            
        }
    }
 
//  POST para registro corrigindo typo "resgister"

   @PostMapping("/auto-cadastro")
   public ResponseEntity<?> autoCadastro(@RequestBody AutoCadastroDTO dto) {
       // Verificar se username, email ou nuit já existem
   if (loginRepository.existsByUsername(dto.getUsername())) {
       return ResponseEntity.badRequest().body("Username já está em uso");
   }
   if (loginRepository.existsByEmail(dto.getEmail())) {
       return ResponseEntity.badRequest().body("Email já está em uso");
   }
   if (loginRepository.existsByNuit(dto.getNuit())) {
       return ResponseEntity.badRequest().body("NUIT já está em uso");
   }

   // Criar novo usuário com os dados do DTO   
   Usuario usuario = new Usuario();
   usuario.setUsername(dto.getUsername());
   usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
   usuario.setEmail(dto.getEmail());
   usuario.setPerguntaSeguranca(dto.getPerguntaSeguranca());
   usuario.setRespostaSeguranca(dto.getRespostaSeguranca());
   usuario.setTelefone(dto.getTelefone());
   usuario.setNuit(dto.getNuit());
   usuario.setDataNascimento(dto.getDataNascimento());
   
   // Definir valores padrão 
   usuario.setRole("USER"); // Cargo padrão
   usuario.setAtivo(false); // Conta desativada até ativação pelo admin
   usuario.setDataCriacao(LocalDateTime.now());
   usuario.setTentativasLogin(0);
   usuario.setContaBloqueada(false);

   loginRepository.save(usuario);

   return ResponseEntity.ok("Cadastro realizado com sucesso. Aguarde ativação da conta por um administrador.");
   } 
		   
   //Devo fazer aqui ate porque o Repositorio e do tipo usuario
    @GetMapping("/findAll")
    private ResponseEntity<List<Usuario>> findAll(){
	  try { 
	 	List<Usuario> lista=this.loginService.findAll();
		return new ResponseEntity<>(lista, HttpStatus.OK);
	    }catch(Exception e) {
		return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
	   }
  } 
    
    //update pra ativar as contas ou desbloquear 
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody @Valid Usuario usuario) {
        
        Usuario usuarioAtualizadoo = this.loginService.atualizarUsuario(id, usuario);
        return ResponseEntity.ok(usuarioAtualizadoo);
    }
 
   
}
