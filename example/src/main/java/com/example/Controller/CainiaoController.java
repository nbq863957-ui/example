package com.example.Controller;

import com.example.entity.PackageInfo;
import com.example.entity.User;
import com.example.service.PackageService;
import com.example.service.StationService;
import com.example.service.UserService;
import com.example.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CainiaoController {

    @Autowired
    private UserService userService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private StationService stationService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        Map<String, String> result = new HashMap<>();
        if (token != null) {
            result.put("type", "success");
            result.put("message", "登录成功");
            result.put("token", token);
        } else {
            result.put("type", "fail");
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        boolean success = userService.register(user.getUsername(), user.getPassword(), user.getIdentityCode());
        Map<String, String> result = new HashMap<>();
        if (success) {
            result.put("type", "success");
            result.put("message", "注册成功");
        } else {
            result.put("type", "fail");
            result.put("message", "用户名已存在");
        }
        return result;
    }

    @GetMapping("/packages")
    public List<PackageInfo> getPackages(@RequestParam String username) {
        return packageService.getByReceiver(username);
    }

    @PostMapping("/send")
    public Map<String, String> send(@RequestBody PackageInfo packageInfo, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, String> result = new HashMap<>();
        System.out.println("Received package info: " + packageInfo);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            result.put("type", "fail");
            result.put("message", "登录已过期，请重新登录");
            return result;
        }
        String token = authHeader.substring(7);
        String sender;
        try {
            sender = TokenUtil.getUsername(token);
        } catch (Exception e) {
            result.put("type", "fail");
            result.put("message", "登录已过期，请重新登录");
            return result;
        }
        if (sender == null) {
            result.put("type", "fail");
            result.put("message", "登录已过期，请重新登录");
            return result;
        }
        if (!stationService.exists(packageInfo.getStation())) {
            result.put("type", "fail");
            result.put("message", "无效的驿站名称");
            return result;
        }
        packageInfo.setSender(sender);
        packageService.addPackage(packageInfo);
        result.put("type", "success");
        result.put("message", "寄件成功");
        return result;
    }
    @PostMapping("/sendadmin")
    public Map<String, String> sendadmin(@RequestBody PackageInfo packageInfo) {
        Map<String, String> result = new HashMap<>();

        if (!stationService.exists(packageInfo.getStation())) {
            result.put("type", "fail");
            result.put("message", "无效的驿站名称");
            return result;
        }

        packageInfo.setSender("admin");
        packageService.addPackage(packageInfo);

        result.put("type", "success");
        result.put("message", "寄件成功");
        return result;
    }

    @PostMapping("/pickup")
    public Map<String, String> pickup(@RequestBody PickupRequest request) {
        Map<String, String> result = new HashMap<>();
        boolean success = packageService.pickup(request.getUsername(), request.getId(), request.getPickupCode());
        if (success) {
            result.put("type", "success");
            result.put("message", "取件成功");
        } else {
            result.put("type", "fail");
            result.put("message", "已经取走了");
        }
        return result;
    }

    public static class PickupRequest {
        private String username;
        private String id;
        private String pickupCode;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getPickupCode() { return pickupCode; }
        public void setPickupCode(String pickupCode) { this.pickupCode = pickupCode; }
    }

    @GetMapping("/admin/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/admin/user/{username}")
    public Map<String, String> deleteUser(@PathVariable String username) {
        boolean success = userService.deleteUser(username);
        Map<String, String> result = new HashMap<>();
        if (success) {
            result.put("type", "success");
            result.put("message", "用户删除成功");
        } else {
            result.put("type", "fail");
            result.put("message", "用户删除失败");
        }
        return result;
    }

    @GetMapping("/admin/stations")
    public List<Map<String, Object>> getStations() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<com.example.entity.Station> stations = stationService.getAll();
        for (com.example.entity.Station station : stations) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", station.getName());
            item.put("packageCount", stationService.countPackages(station.getName()));
            result.add(item);
        }
        return result;
    }

    @PostMapping("/admin/station")
    public Map<String, String> addStation(@RequestBody Map<String, String> body) {
        Map<String, String> result = new HashMap<>();
        boolean success = stationService.add(body.get("name"));
        if (success) {
            result.put("type", "success");
            result.put("message", "驿站新增成功");
        } else {
            result.put("type", "fail");
            result.put("message", "新增失败：名称不能为空或已存在");
        }
        return result;
    }

    @PutMapping("/admin/station/{oldName}")
    public Map<String, String> renameStation(@PathVariable String oldName, @RequestBody Map<String, String> body) {
        Map<String, String> result = new HashMap<>();
        boolean success = stationService.rename(oldName, body.get("name"));
        if (success) {
            result.put("type", "success");
            result.put("message", "驿站修改成功");
        } else {
            result.put("type", "fail");
            result.put("message", "修改失败：名称不能为空、不存在或已存在");
        }
        return result;
    }

    @DeleteMapping("/admin/station/{name}")
    public Map<String, String> deleteStation(@PathVariable String name) {
        Map<String, String> result = new HashMap<>();
        boolean success = stationService.delete(name);
        if (success) {
            result.put("type", "success");
            result.put("message", "驿站删除成功");
        } else {
            result.put("type", "fail");
            result.put("message", "删除失败：驿站不存在或仍有快递未处理");
        }
        return result;
    }

    @GetMapping("/admin/packages")
    public List<PackageInfo> getPackages() {
        return packageService.getAllPackages();
    }

    @PatchMapping("/admin/package/{id}/status")
    public Map<String, String> updatePackageStatus(@PathVariable String id, @RequestBody Map<String, Boolean> status) {
        boolean updated = packageService.updatePackageStatus(id, status.get("isPickedUp"));
        Map<String, String> result = new HashMap<>();
        if (updated) {
            result.put("type", "success");
            result.put("message", "更新成功");
        } else {
            result.put("type", "fail");
            result.put("message", "更新失败，找不到该快递");
        }
        return result;
    }

    @DeleteMapping("/admin/package/{id}")
    public Map<String, String> deletePackage(@PathVariable String id) {
        boolean deleted = packageService.deletePackage(id);
        Map<String, String> result = new HashMap<>();
        if (deleted) {
            result.put("type", "success");
            result.put("message", "删除成功");
        } else {
            result.put("type", "fail");
            result.put("message", "删除失败，找不到该快递");
        }
        return result;
    }

    @PostMapping("/scan-pickup")
    public Map<String, String> scanPickup(@RequestBody Map<String, String> body) {
        String identityCode = body.get("identityCode");
        String packageId = body.get("packageId");
        Map<String, String> result = new HashMap<>();
        if (identityCode == null || packageId == null) {
            result.put("type", "fail");
            result.put("message", "参数不完整");
            return result;
        }
        User user = userService.getUserByIdentityCode(identityCode);
        if (user == null) {
            result.put("type", "fail");
            result.put("message", "无效身份码");
            return result;
        }
        PackageInfo packageInfo = packageService.getById(packageId);
        if (packageInfo == null) {
            result.put("type", "fail");
            result.put("message", "快递不存在");
        } else if (!packageInfo.getReceiver().equals(user.getUsername())) {
            result.put("type", "fail");
            result.put("message", "请放置专用身份码");
        } else if (packageInfo.isPickedUp()) {
            result.put("type", "fail");
            result.put("message", "快递已被取件");
        } else {
            packageInfo.setPickedUp(true);
            packageService.saveAndBroadcast(packageInfo);
            result.put("type", "success");
            result.put("message", "出库成功");
        }
        return result;
    }

    @GetMapping("/getSfmByToken")
    public Map<String, Object> getSfmByToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            result.put("type", "fail");
            result.put("message", "登录已过期，请重新登录");
            return result;
        }
        String token = authHeader.substring(7);
        String username;
        try {
            username = TokenUtil.getUsername(token);
        } catch (Exception e) {
            result.put("type", "fail");
            result.put("message", "登录已过期，请重新登录");
            return result;
        }
        if (username == null) {
            result.put("type", "fail");
            result.put("message", "登录已过期，请重新登录");
            return result;
        }
        String sfm = userService.getIdentityCodeByUsername(username);
        if (sfm == null) {
            result.put("type", "fail");
            result.put("message", "找不到身份码");
        } else {
            result.put("type", "success");
            result.put("identityCode", sfm);
        }
        return result;
    }

    /**
     * 仪表板统计接口：真实数据，收入按每单 1 元计算
     */
    @GetMapping("/admin/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();

        List<User> users = userService.getAllUsers();
        List<PackageInfo> packages = packageService.getAllPackages();

        long totalUsers = users.size();
        long totalPackages = packages.size();
        long pickedUp = packages.stream().filter(PackageInfo::isPickedUp).count();
        long notPickedUp = totalPackages - pickedUp;
        long income = pickedUp; // 收入：每单 1 元（已取件计费）

        result.put("totalUsers", totalUsers);
        result.put("totalPackages", totalPackages);
        result.put("pickedUp", pickedUp);
        result.put("notPickedUp", notPickedUp);
        result.put("income", income);

        // 各快递公司数量（饼图）
        Map<String, Long> courierMap = packages.stream()
                .collect(Collectors.groupingBy(PackageInfo::getCourier, Collectors.counting()));
        List<Map<String, Object>> courierStats = new ArrayList<>();
        courierMap.forEach((name, value) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", name);
            item.put("value", value);
            courierStats.add(item);
        });
        result.put("courierStats", courierStats);

        // 各驿站数量（柱状图）
        Map<String, Long> stationMap = packages.stream()
                .collect(Collectors.groupingBy(PackageInfo::getStation, Collectors.counting()));
        List<Map<String, Object>> stationStats = new ArrayList<>();
        stationMap.forEach((name, value) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", name);
            item.put("value", value);
            stationStats.add(item);
        });
        result.put("stationStats", stationStats);

        // 近 7 天每日新增快递（折线图）
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            long count = packages.stream()
                    .filter(pkg -> pkg.getCreatedAt() != null && pkg.getCreatedAt().toLocalDate().equals(day))
                    .count();
            Map<String, Object> item = new HashMap<>();
            item.put("date", day.format(fmt));
            item.put("count", count);
            dailyStats.add(item);
        }
        result.put("dailyStats", dailyStats);

        return result;
    }
}
