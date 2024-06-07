package fiap.com.br.Ocean.Clean.AI.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fiap.com.br.Ocean.Clean.AI.Models.ResiduoPlastico;
import fiap.com.br.Ocean.Clean.AI.Repository.ResiduoPlasticoRepository;

import java.util.List;

@Service
public class ResiduoPlasticoService {
    
    @Autowired
    private ResiduoPlasticoRepository residuoPlasticoRepository;

    public List<ResiduoPlastico> getAllResiduoPlastico() {
        return residuoPlasticoRepository.findAll();
    }

    public ResiduoPlastico addResiduoPlastico(ResiduoPlastico residuoPlastico) {
        return residuoPlasticoRepository.save(residuoPlastico);
    }
}
