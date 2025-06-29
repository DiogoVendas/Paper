package io.papermc.paper.loot;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Server-side implementation of LootContextBuilder.
 */
public class PaperLootContextBuilderImpl implements LootContextBuilder {
    
    private Location location;
    private float luck = 0.0f;
    private Entity killedEntity;
    private HumanEntity killer;
    private ItemStack tool;
    private NamespacedKey damageSource;
    private float explosionRadius = 0.0f;
    private final Map<String, Object> parameters = new HashMap<>();

    public PaperLootContextBuilderImpl(@NotNull Location location) {
        this.location = Preconditions.checkNotNull(location, "Location cannot be null").clone();
        Preconditions.checkNotNull(location.getWorld(), "Location world cannot be null");
    }

    @Override
    public @NotNull LootContextBuilder location(@NotNull Location location) {
        Preconditions.checkNotNull(location, "Location cannot be null");
        Preconditions.checkNotNull(location.getWorld(), "Location world cannot be null");
        this.location = location.clone();
        return this;
    }

    @Override
    public @NotNull LootContextBuilder luck(float luck) {
        this.luck = luck;
        return this;
    }

    @Override
    public @NotNull LootContextBuilder killedEntity(@Nullable Entity entity) {
        this.killedEntity = entity;
        return this;
    }

    @Override
    public @NotNull LootContextBuilder killer(@Nullable HumanEntity killer) {
        this.killer = killer;
        return this;
    }

    @Override
    public @NotNull LootContextBuilder tool(@Nullable ItemStack tool) {
        this.tool = tool != null ? tool.clone() : null;
        return this;
    }

    @Override
    public @NotNull LootContextBuilder damageSource(@Nullable NamespacedKey damageSource) {
        this.damageSource = damageSource;
        return this;
    }

    @Override
    public @NotNull LootContextBuilder explosionRadius(float radius) {
        this.explosionRadius = radius;
        return this;
    }

    @Override
    public @NotNull LootContextBuilder parameter(@NotNull String key, @Nullable Object value) {
        Preconditions.checkNotNull(key, "Parameter key cannot be null");
        if (value == null) {
            this.parameters.remove(key);
        } else {
            this.parameters.put(key, value);
        }
        return this;
    }

    @Override
    public @NotNull LootContext build() {
        Preconditions.checkNotNull(location, "Location must be set");
        return new PaperLootContextImpl(
            location,
            luck,
            killedEntity,
            killer,
            tool,
            damageSource,
            explosionRadius,
            new HashMap<>(parameters)
        );
    }

    @Override
    public @NotNull LootContextBuilder validateFor(@NotNull LootTable lootTable) {
        Preconditions.checkNotNull(lootTable, "LootTable cannot be null");
        Preconditions.checkNotNull(location, "Location must be set for loot table validation");
        
        // Create a generator to check requirements
        LootGenerator generator = new PaperLootGeneratorImpl(lootTable);
        LootContext testContext = build();
        
        if (!generator.canGenerateWith(testContext)) {
            throw new IllegalStateException("LootContext is missing required parameters for the specified loot table. " +
                "Required: " + generator.getRequiredContextTypes() + 
                ", Optional: " + generator.getOptionalContextTypes());
        }
        
        return this;
    }

    @Override
    public @Nullable Location getLocation() {
        return location != null ? location.clone() : null;
    }

    @Override
    public float getLuck() {
        return luck;
    }

    @Override
    public @Nullable Entity getKilledEntity() {
        return killedEntity;
    }

    @Override
    public @Nullable HumanEntity getKiller() {
        return killer;
    }

    @Override
    public @Nullable ItemStack getTool() {
        return tool != null ? tool.clone() : null;
    }
}
