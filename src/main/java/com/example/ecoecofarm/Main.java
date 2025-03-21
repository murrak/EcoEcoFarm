package com.example.ecoecofarm;

import com.example.ecoecofarm.commands.StockCommand;
import com.example.ecoecofarm.stock.StockManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private StockManager stockManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        stockManager = new StockManager(this);
        getCommand("ecf").setExecutor(new StockCommand(stockManager));
        getCommand("주식").setExecutor(new StockCommand(stockManager));

        // 30분마다 주가 업데이트
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            stockManager.updateStockPrices();
            getLogger().info("주식 가격이 업데이트되었습니다.");
        }, 36000L, 36000L); // 36000L = 30분
    }
}
