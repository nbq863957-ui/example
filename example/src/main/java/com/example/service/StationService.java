package com.example.service;

import com.example.entity.Station;
import com.example.Repository.StationRepository;
import com.example.Repository.PackageInfoRepository;
import com.example.entity.PackageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private PackageInfoRepository packageInfoRepository;

    @Autowired
    private WebSocketService webSocketService;

    public List<Station> getAll() {
        return stationRepository.findAll();
    }

    public boolean exists(String name) {
        return stationRepository.existsById(name);
    }

    public boolean add(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String trimmed = name.trim();
        if (stationRepository.existsById(trimmed)) {
            return false;
        }
        stationRepository.save(new Station(trimmed));
        System.out.println("[驿站新增] " + trimmed);
        webSocketService.broadcast("{\"type\":\"refresh\"}");
        return true;
    }

    public boolean rename(String oldName, String newName) {
        if (oldName == null || newName == null || newName.trim().isEmpty()) {
            return false;
        }
        String trimmed = newName.trim();
        if (!stationRepository.existsById(oldName)) {
            return false;
        }
        if (stationRepository.existsById(trimmed)) {
            return false;
        }
        // 同步更新该驿站下所有快递的 station 字段
        List<PackageInfo> packages = packageInfoRepository.findByStation(oldName);
        for (PackageInfo pkg : packages) {
            pkg.setStation(trimmed);
        }
        packageInfoRepository.saveAll(packages);

        stationRepository.deleteById(oldName);
        stationRepository.save(new Station(trimmed));
        System.out.println("[驿站修改] " + oldName + " -> " + trimmed + "（关联快递 " + packages.size() + " 件已同步）");
        webSocketService.broadcast("{\"type\":\"refresh\"}");
        return true;
    }

    public boolean delete(String name) {
        if (name == null || !stationRepository.existsById(name)) {
            return false;
        }
        long count = packageInfoRepository.countByStation(name);
        if (count > 0) {
            System.out.println("[驿站删除失败] " + name + " 还有 " + count + " 件快递");
            return false;
        }
        stationRepository.deleteById(name);
        System.out.println("[驿站删除] " + name);
        webSocketService.broadcast("{\"type\":\"refresh\"}");
        return true;
    }

    public long countPackages(String name) {
        return packageInfoRepository.countByStation(name);
    }
}