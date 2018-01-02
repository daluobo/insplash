package daluobo.insplash.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import daluobo.insplash.db.model.DownloadItem;
import io.reactivex.Flowable;

/**
 * Created by daluobo on 2018/1/2.
 */

@Dao
public interface DownloadDao {

    @Query("SELECT * FROM download")
    Flowable<List<DownloadItem>> getAll();

    @Query("SELECT * FROM download WHERE id = :id")
    Flowable<DownloadItem> getById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(DownloadItem downloadItem);

    @Update
    void update(DownloadItem downloadItem);

    @Delete
    void deleteAll(List<DownloadItem> downloadItems);

    @Delete
    void delete(DownloadItem... downloadItems);
}