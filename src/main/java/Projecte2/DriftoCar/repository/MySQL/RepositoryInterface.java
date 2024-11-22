/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import java.sql.SQLException;

/**
 *
 * @author Anna
 * @param <T>
 */
public interface RepositoryInterface<T> {

    public void insert(T t) throws SQLException;

    public void delete(T t) throws SQLException;

    public void modify(T t) throws SQLException;

    public void get(T t) throws SQLException;

}
