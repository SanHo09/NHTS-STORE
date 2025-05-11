package com.nhom4.nhtsstore.repositories.specification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.nhom4.nhtsstore.repositories.specification.SearchOperation.*;

/**
 The SpecSearchCriteria class is a container for individual search criteria.
 It holds the key (field to search), operation (type of search), and value (search term).
 */
@Getter
@Setter
@NoArgsConstructor
public class SpecSearchCriteria {
    /**
     * key: The field name to be searched (e.g., "username", "email").
     * operation: The type of search operation to perform (e.g., EQUALITY, LIKE). See SearchOperation enum for details.
     * value: The value to search for.
     * orPredicate: A boolean flag to indicate if this criteria should be combined with others using an OR condition.
     */
    private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate;

    public SpecSearchCriteria(String key, SearchOperation operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String orPredicate, String key, SearchOperation operation, Object value) {
        this.orPredicate = orPredicate != null && orPredicate.equals(OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }
    /**
     * The prefix and suffix parameters in the SpecSearchCriteria constructor
     * are used to automatically determine the LIKE operation based on the presence of wildcard characters (*).
     * If both prefix and suffix contain *, the operation is CONTAINS.
     * If only prefix contains *, the operation is ENDS_WITH.
     * If only suffix contains *, the operation is STARTS_WITH.
     * If neither contains *, the operation defaults to EQUALITY.
     *
     * @param key The field name to be searched.
     * @param operation The type of search operation to perform.
     * @param prefix The prefix for the search term (used for wildcard searches).
     * @param value The value to search for.
     * @param suffix The suffix for the search term (used for wildcard searches).
     */
    public SpecSearchCriteria(String key, String operation, String prefix, String value, String suffix) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (searchOperation == EQUALITY) {
            final boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
            final boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);

            if (startWithAsterisk && endWithAsterisk) {
                searchOperation = CONTAINS;
            } else if (startWithAsterisk) {
                searchOperation = ENDS_WITH;
            } else if (endWithAsterisk) {
                searchOperation = STARTS_WITH;
            }
        }
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
    }
}