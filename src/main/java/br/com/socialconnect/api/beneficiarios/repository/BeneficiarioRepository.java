package br.com.socialconnect.api.beneficiarios.repository;

import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

    Optional<Beneficiario> findByCpf(String cpf);

    Page<Beneficiario> findByCpf(String cpf, Pageable pageable);

    List<Beneficiario> findByNomeContainingIgnoreCase(String nome);

    Page<Beneficiario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdBeneficiarioNot(String cpf, Long idBeneficiario);
}