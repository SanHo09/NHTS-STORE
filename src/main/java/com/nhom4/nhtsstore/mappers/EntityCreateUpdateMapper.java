package com.nhom4.nhtsstore.mappers;



/**
 * A generic base interface for mapping between model, view model, and view model response objects.
 * In case we are implementing create/update operation, we need 3 objects "request", "entity", "response".
 * Then this base mapper will help us to manage mapping them.
 *
 * @param <M> The entity type that represents the model.
 * @param <V> The type that represents the view model.
 * @param <R> The type that represents the view model response.
 */
public interface EntityCreateUpdateMapper<M, V, R> {

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


    /**
     * Converts the provided model entity to its corresponding view model response.
     *
     * @param m The model entity to convert.
     * @return The view model response corresponding to the model entity.
     */
    R toVmResponse(M m);

}
