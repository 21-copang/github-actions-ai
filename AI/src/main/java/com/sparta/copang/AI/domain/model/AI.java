package com.sparta.copang.AI.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_ai_logs")
public class AI {
    @Id
    UUID ai_id;

    @Column
    private String request;

    @Column
    private String response;
}
