package minitwitter.model;

import minitwitter.visitor.Visitor;

/**
 * Component of the COMPOSITE design pattern.
 *
 */
public interface Entry {

    /** @return the unique ID of this user or group. */
    String getId();

    /**
     * Accept a Visitor (VISITOR pattern). A leaf dispatches to
     * {@code visit(User)}; a composite dispatches to {@code visit(UserGroup)}
     * and then forwards the visitor to its children.
     */
    void accept(Visitor visitor);
}
