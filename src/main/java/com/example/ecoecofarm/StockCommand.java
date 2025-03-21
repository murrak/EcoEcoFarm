package com.example.ecoecofarm.commands;

import com.example.ecoecofarm.stock.StockManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class StockCommand implements CommandExecutor {
    private final StockManager stockManager;

    public StockCommand(StockManager stockManager) {
        this.stockManager = stockManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("ecf")) {
            if (args.length < 3) {
                player.sendMessage("사용법: /ecf stock establish [기업명] [기본주가] 또는 /ecf stock delete [기업명]");
                return true;
            }

            if (args[0].equalsIgnoreCase("stock")) {
                if (args[1].equalsIgnoreCase("establish")) {
                    String companyName = args[2];
                    double initialPrice;

                    try {
                        initialPrice = Double.parseDouble(args[3]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("올바른 가격을 입력하세요.");
                        return true;
                    }

                    if (stockManager.establishStock(companyName, initialPrice)) {
                        player.sendMessage(companyName + " 기업이 주식 시장에 등록되었습니다!");
                    } else {
                        player.sendMessage("이미 존재하는 기업입니다.");
                    }
                    return true;
                }

                if (args[1].equalsIgnoreCase("delete")) {
                    String companyName = args[2];

                    if (stockManager.deleteStock(companyName)) {
                        player.sendMessage(companyName + " 기업이 삭제되었습니다.");
                    } else {
                        player.sendMessage("존재하지 않는 기업입니다.");
                    }
                    return true;
                }
            }
        }

        if (label.equalsIgnoreCase("주식")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("차트")) {
                Map<String, String> stockPrices = stockManager.getStockPrices();
                player.sendMessage("=== 주식 차트 ===");
                for (Map.Entry<String, String> entry : stockPrices.entrySet()) {
                    player.sendMessage(entry.getKey() + ": " + entry.getValue());
                }
                return true;
            }
        }

        return false;
    }
}
