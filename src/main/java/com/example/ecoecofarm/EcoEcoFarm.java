package com.example.ecoecofarm;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EcoEcoFarm extends JavaPlugin implements Listener {

    private Economy economy; // Vault 경제 객체

    @Override
    public void onEnable() {
        // Vault 경제 시스템을 초기화
        if (!setupEconomy()) {
            getLogger().severe("Vault가 설치되어 있지 않습니다. 플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("EcoEcoFarm 플러그인이 활성화되었습니다!");

        // 명령어 등록
        getCommand("돈").setExecutor(this);
        getCommand("송금").setExecutor(this);
        getCommand("상점").setExecutor(this);
        getCommand("주식").setExecutor(this);
        getCommand("이자").setExecutor(this);

        // 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(this, this);

        // 자동 이자 지급을 위한 스케줄러 설정 (예: 20분마다 자동 지급)
        new BukkitRunnable() {
            @Override
            public void run() {
                payInterestToAllPlayers();
            }
        }.runTaskTimer(this, 0L, 20L * 60 * 20); // 20분마다 실행
    }

    @Override
    public void onDisable() {
        getLogger().info("EcoEcoFarm 플러그인이 비활성화되었습니다.");
    }

    // Vault 경제 시스템을 초기화
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        return economy != null;
    }

    // 돈 확인
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("돈")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                double balance = economy.getBalance(player);
                player.sendMessage("현재 잔액: " + balance + "원");
            } else {
                sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            }
            return true;
        }

        // 송금
        if (cmd.getName().equalsIgnoreCase("송금")) {
            if (args.length != 2) {
                return false;
            }
            Player senderPlayer = (Player) sender;
            Player recipientPlayer = getServer().getPlayer(args[0]);
            double amount;

            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("금액은 숫자여야 합니다.");
                return false;
            }

            if (recipientPlayer != null && economy.has(senderPlayer, amount)) {
                economy.withdrawPlayer(senderPlayer, amount);
                economy.depositPlayer(recipientPlayer, amount);
                sender.sendMessage("송금이 완료되었습니다: " + amount + "원");
                recipientPlayer.sendMessage(senderPlayer.getName() + "님이 " + amount + "원 송금했습니다.");
            } else {
                sender.sendMessage("송금에 실패했습니다. 확인해주세요.");
            }
            return true;
        }

        // 상점
        if (cmd.getName().equalsIgnoreCase("상점")) {
            // 상점 관련 로직 구현 (예: 아이템 판매)
            sender.sendMessage("상점 기능은 아직 구현되지 않았습니다.");
            return true;
        }

        // 주식
        if (cmd.getName().equalsIgnoreCase("주식")) {
            // 주식 관련 로직 구현
            sender.sendMessage("주식 기능은 아직 구현되지 않았습니다.");
            return true;
        }

        // 이자
        if (cmd.getName().equalsIgnoreCase("이자")) {
            Player player = (Player) sender;
            double balance = economy.getBalance(player);
            double interestRate = 0.05; // 5% 이자율
            double interest = balance * interestRate;
            economy.depositPlayer(player, interest);
            player.sendMessage("이자 지급 완료: " + interest + "원");
            return true;
        }

        return false;
    }

    // 자동으로 모든 플레이어에게 이자를 지급
    private void payInterestToAllPlayers() {
        for (Player player : getServer().getOnlinePlayers()) {
            double balance = economy.getBalance(player);
            double interestRate = 0.05; // 5% 이자율
            double interest = balance * interestRate;
            economy.depositPlayer(player, interest);
            player.sendMessage("이자가 지급되었습니다: " + interest + "원");
        }
    }

    // 플레이어가 서버에 입장할 때 기본 주식 및 이자 설정
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // 초기 주식 및 이자 설정 (필요 시)
    }
}
