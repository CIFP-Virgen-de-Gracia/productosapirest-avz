package com.joseluisgs.productosapirest.dto;

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