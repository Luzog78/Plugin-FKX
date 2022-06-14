package fr.luzog.pl.fkx.fk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FKAuth {

    public static enum Type {PVP, FRIENDLY_FIRE, MOBS, BREAK, BREAKSPE, PLACE, PLACESPE;}

    public static enum Definition {ON, OFF, DEFAULT;}

    public static class Item {
        private Type type;
        private Definition definition;

        public Item(Type type, Definition definition) {
            this.type = type;
            this.definition = definition;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "type=" + type +
                    ", definition=" + definition +
                    '}';
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Definition getDefinition() {
            return definition;
        }

        public void setDefinition(Definition definition) {
            this.definition = definition;
        }
    }

    private Map<Type, Definition> authorizations;

    public FKAuth(Definition defaultDefinition, Item... authorizations) {
        this.authorizations = new HashMap<Type, Definition>() {{
            for (Item a : authorizations)
                if (containsKey(a.getType()))
                    replace(a.getType(), a.getDefinition());
                else
                    put(a.getType(), a.getDefinition());
            for (Type value : Type.values())
                putIfAbsent(value, defaultDefinition);
        }};
    }

    @Override
    public String toString() {
        return "FKAuth{" +
                "authorizations=" + authorizations +
                '}';
    }

    public Map<Type, Definition> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(Map<Type, Definition> authorizations) {
        this.authorizations = authorizations;
    }

    public Definition getAuthorization(Type type) {
        return authorizations.getOrDefault(type, null);
    }

    public void setAuthorization(Type type, Definition definition) {
        if (authorizations.containsKey(type))
            authorizations.replace(type, definition);
        else
            authorizations.put(type, definition);
    }

    public Item getItem(Type type) {
        return authorizations.containsKey(type) ? new Item(type, authorizations.get(type)) : null;
    }

    public void setItem(Item item) {
        setAuthorization(item.getType(), item.getDefinition());
    }

    public List<Item> getItems() {
        return new ArrayList<Item>() {{
            authorizations.forEach((t, d) -> add(new Item(t, d)));
        }};
    }
}
