package br.com.meusistema.api.repository;

import br.com.meusistema.api.model.Fornecedor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FornecedorRepository extends
        JpaRepository<Fornecedor, Long> {
}
