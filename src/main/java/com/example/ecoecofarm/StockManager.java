package com.example.ecoecofarm.stock;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StockManager {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Random random = new Random();
    private final Map<String, Double> stockPrices = new HashMap<>();
    private final Map<String, Double> previousPrices = new HashMap<>();

    public StockManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadStockData();
    }

    // 주식 데이터 로드
    public void loadStockData() {
        if (config.contains("stocks")) {
            for (String stock : config.getConfigurationSection("stocks").getKeys(false)) {
                double price = config.getDouble("stocks." + stock + ".price");
                stockPrices.put(stock, price);
                previousPrices.put(stock, price);
            }
        }
    }

    // 주식 데이터 저장
    public void saveStockData() {
        for (String stock : stockPrices.keySet()) {
            config.set("stocks." + stock + ".price", stockPrices.get(stock));
        }
        plugin.saveConfig();
    }

    // 주식 등록
    public boolean establishStock(String name, double price) {
        if (stockPrices.containsKey(name)) return false;
        stockPrices.put(name, price);
        previousPrices.put(name, price);
        config.set("stocks." + name + ".price", price);
        saveStockData();
        return true;
    }

    // 주식 삭제
    public boolean deleteStock(String name) {
        if (!stockPrices.containsKey(name)) return false;
        stockPrices.remove(name);
        previousPrices.remove(name);
        config.set("stocks." + name, null);
        saveStockData();
        return true;
    }

    // 주가 업데이트 (-10% ~ +10% 변동)
    public void updateStockPrices() {
        for (String stock : stockPrices.keySet()) {
            double prevPrice = stockPrices.get(stock);
            if (prevPrice == 0) continue; // 상장폐지된 주식은 변동 없음

            double changeRate = (random.nextDouble() * 20 - 10) / 100; // -10% ~ +10%
            double newPrice = Math.max(0, prevPrice * (1 + changeRate));

            previousPrices.put(stock, prevPrice);
            stockPrices.put(stock, newPrice);
        }
        saveStockData();
    }

    // 현재 주가 가져오기
    public Map<String, String> getStockPrices() {
        Map<String, String> displayPrices = new HashMap<>();
        for (String stock : stockPrices.keySet()) {
            double price = stockPrices.get(stock);
            double prevPrice = previousPrices.getOrDefault(stock, price);
            double change = price - prevPrice;
            String changeText = (price == 0) ? "(상장폐지)" : String.format("(%+.2f원)", change);
            displayPrices.put(stock, String.format("%.2f원 %s", price, changeText));
        }
        return displayPrices;
    }
}