package ru.minat0.minetail.core.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import org.jetbrains.annotations.Nullable;

public class SpellsStringFlag extends Flag<String> {

    protected SpellsStringFlag(String name, @Nullable RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public SpellsStringFlag(String name) {
        super(name);
    }

    @Override
    public String parseInput(FlagContext flagContext) {
        return flagContext.getUserInput().trim();
    }

    @Override
    public String unmarshal(@Nullable Object o) {
        return o instanceof String ? (String) o : null;
    }

    @Override
    public Object marshal(String s) {
        return s;
    }
}
