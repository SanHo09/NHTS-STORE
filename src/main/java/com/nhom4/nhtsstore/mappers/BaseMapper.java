package com.nhom4.nhtsstore.mappers;


/**
 * A generic base interface for mapping between model and view model objects.
 *
 * @param <M>    The entity type that represents the model.
 * @param <V> The type that represents the view model.
 */
public interface BaseMapper<M, V> {

    /**
     * Converts the provided view model to its corresponding model entity.
     *
     * @param vm The view model object to convert.
     * @return The model entity corresponding to the view model.
     */
    M toModel(V vm);

    /**
     * Converts the provided model entity to its corresponding view model.
     *
     * @param m The model entity to convert.
     * @return The view model corresponding to the model entity.
     */
    V toVm(M m);



}
