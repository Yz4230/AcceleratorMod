package yz.acceleratormod.keymgr;

import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class KeyManager {
    public Map<EntityPlayer, Set<Key>> playerKeys = new WeakHashMap<>();

    public boolean isPowerKeyDown(EntityPlayer player) {
        return this.get(player, Key.power);
    }

    public boolean isReflectKeyDown(EntityPlayer player) {
        return this.get(player, Key.reflect);
    }

    public boolean isFunctionKeyDown(EntityPlayer player) {
        return this.get(player, Key.reflect);
    }

    private boolean get(EntityPlayer player, Key key) {
        Set<Key> keys = this.playerKeys.get(player);
        if (keys == null)
            return false;
        return keys.contains(key);
    }

    public void processKeyUpdate(EntityPlayer player, int keyState) {
        this.playerKeys.put(player, Key.fromInt(keyState));
    }

    public enum Key {
        power,
        reflect,
        function;

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
