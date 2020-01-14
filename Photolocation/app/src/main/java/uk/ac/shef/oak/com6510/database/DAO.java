package uk.ac.shef.oak.com6510.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DAO {
    @Insert
    void insert(ImageElement imageElement);

    @Query("SELECT COUNT(*) FROM ImageElement")
    int howManyElements();

    @Query("SELECT * FROM ImageElement")
    LiveData<List<ImageElement>> getAll();

    @Query("SELECT * FROM ImageElement WHERE date = :date")
    LiveData<List<ImageElement>> searchByDate(String date);

    @Query("SELECT * FROM ImageElement WHERE title LIKE :title")
    LiveData<List<ImageElement>> searchByTitle(String title);

    @Query("SELECT * FROM ImageElement WHERE description LIKE :decs")
    LiveData<List<ImageElement>> searchByDecs(String decs);

    @Query("SELECT * FROM ImageElement WHERE description LIKE :decs AND title LIKE :title")
    LiveData<List<ImageElement>> searchByDecsTitle(String decs, String title);

    @Query("SELECT * FROM ImageElement WHERE title LIKE :title AND date = :date")
    LiveData<List<ImageElement>> searchByTitleDate(String title, String date);

    @Query("SELECT * FROM ImageElement WHERE description LIKE :decs AND date = :date")
    LiveData<List<ImageElement>> searchByDecsDate(String decs, String date);

    @Query("SELECT * FROM ImageElement WHERE description LIKE :decs AND title LIKE :title AND date =:date")
    LiveData<List<ImageElement>> searchByAll(String decs, String title, String date);

    @Delete
    void deleteImage(ImageElement imageElement);

    @Update
    int updateInfo(ImageElement imageElement);
}
