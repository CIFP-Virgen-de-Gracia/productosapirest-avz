package com.joseluisgs.productosapirest.modelos;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // Para la auditoría
public class Pedido {


    @Id
    @GeneratedValue
    private Long id;

    private String cliente;

    @CreatedDate
    private LocalDateTime fecha;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LineaPedido> lineas = new HashSet<>();

    public float getTotal() {
        return (float) lineas.stream()
                .mapToDouble(LineaPedido::getSubtotal)
                .sum();
    }

    /**
     * Métodos helper, por la bidireccionalidad, es decir por ambas vías
     * Si asociados una linea de pedido a un pedido o quitar
     */

    public void addLineaPedido(LineaPedido lp) {
        lineas.add(lp);
        lp.setPedido(this);
    }

    public void removeLineaPedido(LineaPedido lp) {
        lineas.remove(lp);
        lp.setPedido(null);
    }


}
