package com.joseluisgs.productosapirest.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLoteDTO {

    private String nombre;
    @Builder.Default
    private List<Long> productos = new ArrayList<>();

}
