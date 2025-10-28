package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.Data;;
@Data
@Entity
@Table(name="service_item", indexes=@Index(columnList="name", unique=true))
public class ServiceEntity
{
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
}