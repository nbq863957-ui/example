package com.example.service;

import com.example.entity.PackageInfo;
import com.example.Repository.PackageInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PackageService {
    @Autowired
    private PackageInfoRepository packageInfoRepository;

    @Autowired
    private WebSocketService webSocketService;

    public void addPackage(PackageInfo pkg) {
        packageInfoRepository.save(pkg);
        System.out.println("[寄件成功] " + pkg.getSender() + " -> " + pkg.getReceiver() + " 快递：" + pkg.getId());
        webSocketService.broadcast("{\"type\":\"refresh\"}");
    }

    public void saveAndBroadcast(PackageInfo pkg) {
        packageInfoRepository.save(pkg);
        System.out.println("[出库成功] 快递：" + pkg.getId() + " 已取件");
        webSocketService.broadcast("{\"type\":\"refresh\"}");
    }

    public List<PackageInfo> getByReceiver(String username) {
        return packageInfoRepository.findByReceiver(username);
    }

    public boolean pickup(String username, String id, String code) {
        Optional<PackageInfo> optionalPackage = packageInfoRepository.findById(id);
        if (optionalPackage.isPresent()) {
            PackageInfo pkg = optionalPackage.get();
            if (pkg.getReceiver().equals(username) && !pkg.isPickedUp() && pkg.getPickupCode().equals(code)) {
                pkg.setPickedUp(true);
                packageInfoRepository.save(pkg);
                System.out.println("[取件成功] " + username + " 取走了包裹：" + id);
                webSocketService.broadcast("{\"type\":\"refresh\"}");
                return true;
            }
        }
        return false;
    }

    public List<PackageInfo> getAllPackages() {
        return packageInfoRepository.findAll();
    }

    public boolean updatePackageStatus(String id, boolean isPickedUp) {
        Optional<PackageInfo> optionalPackage = packageInfoRepository.findById(id);
        if (optionalPackage.isPresent()) {
            PackageInfo pkg = optionalPackage.get();
            pkg.setPickedUp(isPickedUp);
            packageInfoRepository.save(pkg);
            System.out.println("[更新状态成功] 快递：" + id + " 取件状态：" + (isPickedUp ? "已取件" : "未取件"));
            webSocketService.broadcast("{\"type\":\"refresh\"}");
            return true;
        }
        return false;
    }

    public boolean deletePackage(String id) {
        if (packageInfoRepository.existsById(id)) {
            packageInfoRepository.deleteById(id);
            System.out.println("[删除快递成功] 快递：" + id);
            webSocketService.broadcast("{\"type\":\"refresh\"}");
            return true;
        }
        System.out.println("[删除快递失败] 快递：" + id + " 不存在");
        return false;
    }

    public PackageInfo getById(String id) {
        return packageInfoRepository.findById(id).orElse(null);
    }
}
