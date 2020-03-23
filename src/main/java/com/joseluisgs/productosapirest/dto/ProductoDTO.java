package com.joseluisgs.productosapirest.dto;

import lombok.*;


// Los DTO nos sirven entre otras cosas para filtrar información de una o varias clases, podría ser similar a las vistas
// Aplicamos lombok para hacer el DT0
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Solo tiene getter&setter
public class ProductoDTO {

    private long id;
    private String nombre;
    private float precio;
    private String imagen;
    private String categoria;

}
