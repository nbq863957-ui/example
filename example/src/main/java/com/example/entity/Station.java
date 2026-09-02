package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "station") // 映射到数据库中的 station 表
@Getter
@Setter
@NoArgsConstructor
public class Station {

    @Id
    @Column(name = "name", nullable = false, length = 50)
    private String name; // 驿站名称（主键）

    // 构造函数
    public Station(String name) {
        this.name = name;
    }
}
