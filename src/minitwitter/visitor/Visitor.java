package minitwitter.visitor;

import minitwitter.model.User;
import minitwitter.model.UserGroup;

/**
 * Visitor interface (VISITOR design pattern).
 *
 */
public interface Visitor {

    /** Visit a single user (a leaf in the Composite tree). */
    void visit(User user);

    /** Visit a user group (a composite node in the Composite tree). */
    void visit(UserGroup group);
}
