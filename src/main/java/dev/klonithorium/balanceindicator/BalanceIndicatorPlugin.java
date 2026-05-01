package dev.klonithorium.balanceindicator;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class BalanceIndicatorPlugin extends JavaPlugin {
    private Economy economy;
    private BalanceIndicatorExpansion expansion;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Vault economy provider was not found. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        expansion = new BalanceIndicatorExpansion(this, economy);
        expansion.register();
        getLogger().info("Registered %balance_indicator% placeholder.");
    }

    @Override
    public void onDisable() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("balanceindicator.reload")) {
                sender.sendMessage(color(getConfig().getString("messages.no-permission", "&cYou do not have permission to use this command.")));
                return true;
            }

            reloadConfig();
            if (expansion != null) {
                expansion.reloadFormatter();
            }
            sender.sendMessage(color(getConfig().getString("messages.reload", "&aBalanceIndicator config reloaded.")));
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " reload");
        return true;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            return false;
        }

        economy = provider.getProvider();
        return economy != null;
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
