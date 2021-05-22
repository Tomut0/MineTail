package ru.minat0.minetail.core.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Flags {
    private static final List<String> INBUILT_FLAGS_LIST = new ArrayList<>();

    public static LocationFlag CUSTOM_RESPAWN;
    public static SetFlag<String> SPELL_BLACKLIST;
    public static SetFlag<String> SPELL_WHITELIST;
    public static StateFlag SPELL_USAGE;

    public Flags() {
    }

    private static <T extends Flag<?>> T register(final T flag) throws FlagConflictException {
        WorldGuard.getInstance().getFlagRegistry().register(flag);
        INBUILT_FLAGS_LIST.add(flag.getName());
        return flag;
    }

    private static <T extends Flag<?>> T register(final T flag, Consumer<T> cfg) throws FlagConflictException {
        T f = register(flag);
        cfg.accept(f);
        return f;
    }

    public static void registerAll() {
        CUSTOM_RESPAWN = register(new LocationFlag("custom-respawn", RegionGroup.ALL));
        SPELL_BLACKLIST = register(new SetFlag<>("spell-blacklist", new SpellsStringFlag(null)));
        SPELL_WHITELIST = register(new SetFlag<>("spell-whitelist", new SpellsStringFlag(null)));
        SPELL_USAGE = register(new StateFlag("spell-usage", true));
    }
}
