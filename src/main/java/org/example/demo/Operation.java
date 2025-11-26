package org.example.demo;

import java.util.List;

public  class Operation<E> implements GenericDAO<E>{
    @Override
    public boolean add(E obj) {
        return false;
    }

    @Override
    public boolean update(E obj) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public E getById(int id) {
        return null;
    }

    @Override
    public List<E> getAll() {
        return null;

}

}
