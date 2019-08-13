package yz.acceleratormod.network.keymgr;

import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

public class KeyManager {
    public Map<EntityPlayer, Set<Key>> playerKeys = new HashMap<>();

    public boolean isPowerKeyDown(EntityPlayer player) {
        return this.get(player, Key.power);
    }

    public boolean isStepKeyDown(EntityPlayer player) {
        return this.get(player, Key.step);
    }

    public boolean isFunctionKeyDown(EntityPlayer player) {
        return this.get(player, Key.function);
    }

    public boolean isChangeGravityKeyDown(EntityPlayer player) {
        return this.get(player, Key.change_gravity);
    }

    public boolean isJumpKeyDown(EntityPlayer player) {
        return this.get(player, Key.jump);
    }

    public boolean andKeyDown(EntityPlayer player, Key... keys) {
        Set<Key> playerKey = this.playerKeys.get(player);
        if (playerKey == null)
            return false;
        return playerKey.containsAll(Arrays.asList(keys));
    }

    public boolean orKeyDown(EntityPlayer player, Key... keys) {
        for (Key k : keys)
            if (this.get(player, k))
                return true;
        return false;
    }

    private boolean get(EntityPlayer player, Key key) {
        Set<Key> keys = this.playerKeys.get(player);
        if (keys == null) return false;
        return this.playerKeys.get(player).contains(key);
    }

    public void processKeyUpdate(EntityPlayer player, int currentState, int lastState) {
        this.playerKeys.put(player, Key.fromInt(currentState));
    }

    public enum Key {
        power,
        step,
        function,
        change_gravity,
        jump;

        public static int toInt(Iterable<Key> keySet) {
            int ret = 0;
            for (Key key : keySet)
                ret |= 1 << key.ordinal();
            return ret;
        }

        public static Set<Key> fromInt(int keyState) {
            Set<Key> ret = EnumSet.noneOf(Key.class);
            for (int i = 0; keyState != 0; i++, keyState >>= 1) {
                if ((keyState & 1) != 0)
                    ret.add(Key.values()[i]);
            }
            return ret;
        }
    }
}
