package utn.tif.trabajo_integrador_final.DAOS;

import java.util.List;

public interface GenericDAO <T> {
    T save(T entity) ;
    List<T> bulkCreate(List<T> entities) ;
    T findById(String id) ;
    List<T> findAll() ;
    List<T> findMany(List<String> ids) ;
    T updateOne(T entity) ;
    List<T> updateMany(List<T> entities) ;
    void deleteOne(String id) ;
    void deleteMany(List<String> ids) ;
}
