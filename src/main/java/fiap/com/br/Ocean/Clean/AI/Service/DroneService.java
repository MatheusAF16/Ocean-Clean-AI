package fiap.com.br.Ocean.Clean.AI.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fiap.com.br.Ocean.Clean.AI.Models.Drone;
import fiap.com.br.Ocean.Clean.AI.Repository.DroneRepository;

@Service
public class DroneService {
    
    @Autowired
    private DroneRepository droneRepository;

    public Page<Drone> getAllDrones(Pageable pageable) {
        return droneRepository.findAll(pageable);
    }

    public Optional<Drone> getDroneById(Long id) {
        return droneRepository.findById(id);
    }

    public Drone addDrone(Drone drone) {
        return droneRepository.save(drone);
    }

}
