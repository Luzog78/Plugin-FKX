package fr.luzog.pl.fkx.fk;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FKLang {

    public static class FKLangItem {
        private String os, s;
        FKLangItem(String s) {
            this.os = this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }

        public FKLangItem getOriginal() {
            return new FKLangItem(os);
        }

        public FKLangItem reset() {
            s = os;
            return this;
        }

        public FKLangItem format(Object... o) {
            for (Object obj : o) {
                String changer = null;
                if (obj instanceof Integer)
                    changer = "int";
                else if (obj instanceof Double)
                    changer = "double";
                else if (obj instanceof Float)
                    changer = "float";
                else if (obj instanceof Boolean)
                    changer = "bool";
                else if (obj instanceof Long)
                    changer = "long";
                else if (obj instanceof Short)
                    changer = "short";
                else if (obj instanceof Byte)
                    changer = "byte";
                else if (obj instanceof String)
                    changer = "str";
                else if (obj instanceof Character)
                    changer = "char";
                else if (obj instanceof Player)
                    changer = "player";
                else if (obj instanceof Location)
                    changer = "loc";
                else if (obj instanceof Vector)
                    changer = "vector";
                else if (obj instanceof ChatColor)
                    changer = "color";

                if (changer == null)
                    continue;
                s = s.replace("%" + changer.toUpperCase() + "%", obj.toString());
            }
            return this;
        }
    }

}
