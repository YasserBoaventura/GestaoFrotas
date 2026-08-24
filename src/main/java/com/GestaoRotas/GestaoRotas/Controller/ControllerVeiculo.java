package com.GestaoRotas.GestaoRotas.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GestaoRotas.GestaoRotas.DTO.VeiculoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Service.ServiceVeiculo;

import lombok.RequiredArgsConstructor;

import java.util.*;


@RestController
@RequestMapping("/api/veiculos")
@CrossOrigin("*")
@RequiredArgsConstructor 
public class ControllerVeiculo {

    private final ServiceVeiculo serviceVeiculo;
    private final RepositoryVeiculo repositoryVeiculo;

    @PostMapping("/salvar")
    public ResponseEntity<Map<String, String>> salvar(@RequestBody Veiculo veiculo) {
          serviceVeiculo.salvar(veiculo);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Veículo cadastrado com sucesso");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

    @GetMapping("/findAll")
    public ResponseEntity<List<Veiculo>> findAll() {
            return  ResponseEntity.status(HttpStatus.OK).body(serviceVeiculo.findAll());
    }

    @PatchMapping("/{veiculoId}/kilometragem")
    public ResponseEntity<Veiculo> atualizarKilometragem(
            @PathVariable Long veiculoId,
            @RequestBody Map<String, Double> body) {

        Double kilometragemAtual = body.get("kilometragemAtual");

        Veiculo veiculo = repositoryVeiculo.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        veiculo.setKilometragemAtual(kilometragemAtual);

        return ResponseEntity.ok(repositoryVeiculo.save(veiculo));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody Veiculo veiculo, @PathVariable long id) {
            return ResponseEntity.ok(serviceVeiculo.update(veiculo, id));
    }
    @GetMapping("/findById/{id}")
    public ResponseEntity<Veiculo> findById(Long id) {
        return ResponseEntity.ok(repositoryVeiculo.findById(id).get());

        }
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<String> delete (@PathVariable Long id){
            return ResponseEntity.ok(serviceVeiculo.deletar(id));

        }



}
