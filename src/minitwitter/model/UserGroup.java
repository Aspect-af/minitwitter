package minitwitter.model;

import java.util.ArrayList;
import java.util.List;

import minitwitter.visitor.Visitor;

/**
 * A group of users (and, recursively, of sub-groups).
 *
 */
public class UserGroup implements Entry {

    private final String id;
    private final List<Entry> entries = new ArrayList<>();

    public UserGroup(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /** Add a child entry (a user or a sub-group) to this group. */
    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        for (Entry entry : entries) {
            entry.accept(visitor);   // forward to children (COMPOSITE + VISITOR)
        }
    }

    @Override
    public String toString() {
        return id;
    }
}
