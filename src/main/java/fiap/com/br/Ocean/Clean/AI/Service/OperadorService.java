package fiap.com.br.Ocean.Clean.AI.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import fiap.com.br.Ocean.Clean.AI.Models.Operador;
import fiap.com.br.Ocean.Clean.AI.Repository.OperadorRepository;

@Service
public class OperadorService {
    
    @Autowired
    private OperadorRepository operadorRepository;

    public Page<Operador> getAllOperadores(Pageable pageable) {
        return operadorRepository.findAll(pageable);
    }

    public Optional<Operador> getOperadorById(Long id) {
        return operadorRepository.findById(id);
    }

    public Operador addOperador(Operador operador) {
        return operadorRepository.save(operador);
    }

}
