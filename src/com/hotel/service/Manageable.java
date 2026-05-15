package com.hotel.service;

import java.util.List;
import com.hotel.exception.HotelException;

public interface Manageable<T> {
    void add(T entity) throws HotelException;
    T getById(String id) throws HotelException;
    List<T> getAll();
    void update(T entity) throws HotelException;
    void delete(String id) throws HotelException;
}
