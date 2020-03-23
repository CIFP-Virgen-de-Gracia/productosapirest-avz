package com.joseluisgs.productosapirest.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.joseluisgs.productosapirest.views.ProductoViews;
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

    // Indicamos los atributos que queremos que salga y como saldrán con JSON Vies
    @JsonView(ProductoViews.Dto.class)
    private long id;

    @JsonView(ProductoViews.Dto.class)
    private String nombre;

    @JsonView(ProductoViews.DtoConPrecio.class)
    private float precio;

    @JsonView(ProductoViews.Dto.class)
    private String imagen;

    @JsonView(ProductoViews.Dto.class)
    private String categoria;

}
