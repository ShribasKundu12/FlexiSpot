package com.flexispot.usage_analytics.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workspaces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // e.g., "Desk A101"
    private String type;            // e.g., "desk", "meeting_room"
    // manually add if Lombok fails
    @Getter
    private String floor;           // optional, for analytics

    private boolean isActive;       // whether this seat is available or not

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    // manually add if Lombok fails
    public String getFloor() {
        return floor;
    }



}
