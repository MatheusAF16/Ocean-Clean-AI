package fiap.com.br.Ocean.Clean.AI.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fiap.com.br.Ocean.Clean.AI.Models.Missao;
import fiap.com.br.Ocean.Clean.AI.Repository.MissaoRepository;

import java.util.Optional;

@Service
public class MissaoService {
    
    @Autowired
    private MissaoRepository missaoRepository;

    public Page<Missao> getAllMissoes(Pageable pageable) {
        return missaoRepository.findAll(pageable);
    }

    public Optional<Missao> getMissaoById(Long id) {
        return missaoRepository.findById(id);
    }

    public Missao addMissao(Missao missao) {
        return missaoRepository.save(missao);
    }
}
