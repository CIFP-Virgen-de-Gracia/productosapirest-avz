package com.joseluisgs.productosapirest.dto.coverter;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditProductoDTO {

    private String nombre;
    private float precio;

}