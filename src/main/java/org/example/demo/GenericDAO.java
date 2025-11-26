package org.example.demo;

import java.util.List;

public interface GenericDAO<E> {
    public boolean add(E obj);
    public boolean update(E obj);
    public boolean delete(int id);
    public E getById(int id);
    public List<E> getAll();
}