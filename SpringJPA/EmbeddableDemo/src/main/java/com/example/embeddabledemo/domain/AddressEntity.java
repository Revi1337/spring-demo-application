package com.example.embeddabledemo.domain;

import jakarta.persistence.*;

/**
 * Embeddable 값 타입 컬렉션의 대안
 */
@Entity
@Table(name = "ADDRESS")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Address address;

    protected AddressEntity() {}

    public AddressEntity(String city, String zipcode) {
        this.address = new Address(city, zipcode);
    }
}

