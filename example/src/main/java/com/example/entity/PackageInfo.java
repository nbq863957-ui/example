package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "package_info")
@Getter
@Setter
@NoArgsConstructor
public class PackageInfo {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "pickupCode", nullable = false, length = 50)
    private String pickupCode;

    @Column(name = "sender", nullable = false, length = 50)
    private String sender;
    @Column(name="courier",nullable = false ,length = 50)
    private String courier;
    @Column(name = "receiver", nullable = false, length = 50)
    private String receiver;

    @Column(name = "station", nullable = false, length = 50)
    private String station;

    @Column(name = "pickedUp", nullable = false)
    private boolean pickedUp;

    @Column(name = "createdAt")
    private LocalDateTime createdAt = LocalDateTime.now();

    public PackageInfo(String id, String pickupCode, String sender, String receiver, String station,String courier,boolean pickedUp) {
        this.id = id;
        this.pickupCode = pickupCode;
        this.sender = sender;
        this.receiver = receiver;
        this.station = station;
        this.courier = courier;
        this.pickedUp = false;
        this.createdAt = LocalDateTime.now();
    }

    public void save(PackageInfo packageInfo) {

    }
}
