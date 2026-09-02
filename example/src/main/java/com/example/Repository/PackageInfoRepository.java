package com.example.Repository;

import com.example.entity.PackageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PackageInfoRepository extends JpaRepository<PackageInfo, String> {
    List<PackageInfo> findByReceiver(String receiver);
    List<PackageInfo> findByStation(String station);
    long countByStation(String station);
}