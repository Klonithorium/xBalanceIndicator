package dev.klonithorium.balanceindicator;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BalanceIndicatorExpansion extends PlaceholderExpansion {
    private final BalanceIndicatorPlugin plugin;
    private final Economy economy;
    private final Map<UUID, Double> lastBalances = new ConcurrentHashMap<>();
    private BalanceFormatter formatter;

    public BalanceIndicatorExpansion(BalanceIndicatorPlugin plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
        reloadFormatter();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "balance";
    }

    @Override
    public @NotNull String getAuthor() {
        return "klonithorium";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !params.equalsIgnoreCase("indicator")) {
            return null;
        }

        UUID playerId = player.getUniqueId();
        double currentBalance = economy.getBalance(player);
        Double previousBalance = lastBalances.put(playerId, currentBalance);

        if (previousBalance == null && formatter.ignoreFirstChange()) {
            return formatter.firstCheckValue();
        }

        double change = previousBalance == null ? currentBalance : currentBalance - previousBalance;
        return formatter.format(change);
    }

    public void reloadFormatter() {
        formatter = BalanceFormatter.fromConfig(plugin.getConfig());
    }
}
