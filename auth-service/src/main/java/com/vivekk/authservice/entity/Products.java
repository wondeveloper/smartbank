package com.vivekk.authservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Data
@NoArgsConstructor
public class Products {

    @Id
    private Long id;
    private String productName;
    private Double price;
    private String brandName;
}
