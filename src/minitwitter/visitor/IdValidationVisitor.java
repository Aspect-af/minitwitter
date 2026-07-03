package minitwitter.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minitwitter.model.User;
import minitwitter.model.UserGroup;

/**
 * VISITOR: verifies that every ID in the Composite tree is unique and free of
 * spaces. Collects both kinds of violations so the caller can report them.
 */
public class IdValidationVisitor implements Visitor {

    private final Set<String> seen = new HashSet<>();
    private final List<String> duplicates = new ArrayList<>();
    private final List<String> idsWithSpaces = new ArrayList<>();

    @Override
    public void visit(User user) {
        check(user.getId());
    }

    @Override
    public void visit(UserGroup group) {
        check(group.getId());
    }

    private void check(String id) {
        if (id.contains(" ")) {
            idsWithSpaces.add(id);
        }
        if (!seen.add(id)) {
            duplicates.add(id);
        }
    }

    public boolean isValid() {
        return duplicates.isEmpty() && idsWithSpaces.isEmpty();
    }

    public List<String> getDuplicates() {
        return duplicates;
    }

    public List<String> getIdsWithSpaces() {
        return idsWithSpaces;
    }
}
