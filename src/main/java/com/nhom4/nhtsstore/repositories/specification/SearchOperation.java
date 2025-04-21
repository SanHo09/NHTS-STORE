package com.nhom4.nhtsstore.repositories.specification;
/**
 *The SearchOperation enum defines the supported search operations.
 */
public enum SearchOperation {
    /**
     * EQUALITY: field = value
     * NEGATION: field != value
     * GREATER_THAN: field > value
     * LESS_THAN: field < value
     * LIKE: field LIKE value
     * STARTS_WITH: field LIKE value%
     * ENDS_WITH: field LIKE %value
     * CONTAINS: field LIKE %value%
     */
    EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS;
    /**
     * SIMPLE_OPERATION_SET: An array of characters that represent simple operations.
     * OR_PREDICATE_FLAG: A flag used to indicate an OR predicate.
     * ZERO_OR_MORE_REGEX: A regex for zero or more occurrences (used for LIKE operations).
     * OR_OPERATOR: String representation for the OR operator.
     * AND_OPERATOR: String representation for the AND operator.
     * LEFT_PARENTHESIS: String representation for the left parenthesis.
     * RIGHT_PARENTHESIS: String representation for the right parenthesis.
     */

    public static final String[] SIMPLE_OPERATION_SET = { ":", "!", ">", "<", "~" };

    public static final String OR_PREDICATE_FLAG = "'";

    public static final String ZERO_OR_MORE_REGEX = "*";

    public static final String OR_OPERATOR = "OR";

    public static final String AND_OPERATOR = "AND";

    public static final String LEFT_PARENTHESIS = "(";

    public static final String RIGHT_PARENTHESIS = ")";
    /**
     * getSimpleOperation: Returns the SearchOperation corresponding to the input character.
     * @param input The input character representing a search operation.
     * @return The corresponding SearchOperation, or null if not found.
     */
    public static SearchOperation getSimpleOperation(final char input) {
        return switch (input) {
            case ':' -> EQUALITY;
            case '!' -> NEGATION;
            case '>' -> GREATER_THAN;
            case '<' -> LESS_THAN;
            case '~' -> LIKE;
            default -> null;
        };
    }
}
