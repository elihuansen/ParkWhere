package io.parkwhere.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.parkwhere.model.serializers.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class Carpark extends BaseResource {

    private int id;
    private RatesCollection ratesCollection;
    private String name;
    private String address;
    private String remarks;

    @JsonSerialize(using=LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using=LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    public Carpark() {}

    public Carpark(int id, RatesCollection ratesCollection, String name, String address, String remarks) {
        this.id = id;
        this.ratesCollection = ratesCollection;
        this.name = name;
        this.address = address;
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public Carpark setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Carpark setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Carpark setAddress(String address) {
        this.address = address;
        return this;
    }

    public RatesCollection getRatesCollection() {
        return ratesCollection;
    }

    public Carpark setRatesCollection(RatesCollection ratesCollection) {
        this.ratesCollection = ratesCollection;
        return this;
    }

    public String getRemarks() {
        return remarks;
    }

    public Carpark setRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Carpark setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Carpark setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public String toString() {
        return "Carpark{" +
                "id=" + id +
                ", ratesCollection=" + ratesCollection +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
