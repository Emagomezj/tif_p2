package utn.tif.trabajo_integrador_final.DAOS;

import java.util.List;

public interface GenericDAO <T> {
    T save(T entity) throws Exception;
    List<T> bulkCreate(List<T> entities) throws Exception;
    T findById(String id) throws Exception;
    List<T> findAll() throws Exception;
    List<T> findMany(List<String> ids) throws Exception;
    T updateOne(T entity) throws Exception;
    List<T> updateMany(List<T> entities) throws Exception;
    void deleteOne(String id) throws Exception;
    void deleteMany(List<String> ids) throws Exception;
}
