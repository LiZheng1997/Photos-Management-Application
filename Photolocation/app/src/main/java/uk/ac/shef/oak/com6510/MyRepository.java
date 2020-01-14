package uk.ac.shef.oak.com6510;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.util.Log;

import uk.ac.shef.oak.com6510.database.DAO;
import uk.ac.shef.oak.com6510.database.ImageElement;
import uk.ac.shef.oak.com6510.database.PhotoDatabase;

import java.util.List;


/**
 * <h1>Operations for accessing database are included here</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */

public class MyRepository extends ViewModel {
    private final DAO dao;
    LiveData<List<ImageElement>> allImages;

    public MyRepository(Application application) {
        PhotoDatabase database = PhotoDatabase.getDatabase(application);
        this.dao = database.dao();
        allImages = dao.getAll();
    }

    /**
     *Insert photos to database
     * @param imageElement
     */
    // insert photo
    public void insertImage(ImageElement imageElement) {
        new insertAsyncTask(dao).execute(imageElement);
    }

    /**
     *Retrieve all photos from database
     * @return LiveData<List<ImageElement>> LiveData of List of ImageElement
     */
    // get all photos
    public LiveData<List<ImageElement>> getAll(){
        return allImages;
    }

    /**
     *Retrieve photos searched by title
     * @param title
     * @return LiveData<List<ImageElement>> LiveData of List of ImageElement
     */
    // search photos by title
    public LiveData<List<ImageElement>> searchByTitle(String title){
            return dao.searchByTitle(title);
    }

    /**
     *Retrieve photos searched by description
     * @param decs search description
     * @return LiveData<List<ImageElement>> LiveData of List of ImageElement
     */
    // search photos by description
    public LiveData<List<ImageElement>> searchByDecs(String decs) {
        return dao.searchByDecs(decs);
    }

    /**
     *Retrieve photos searched by date
     * @param date search date
     * @return LiveData<List<ImageElement>> LiveData of List of ImageElement
     */
    // search photos by date
    public LiveData<List<ImageElement>> searchByDate(String date){
        return dao.searchByDate(date);
    }

    /**
     *Retrieve photos searched by description and title
     * @param title search title
     * @param decs search description
     * @return LiveData<List<ImageElement>> LiveData of List of ImageElement
     */
    // search photos by description and title
    public LiveData<List<ImageElement>> searchByDecsTitle(String decs, String title){
        return dao.searchByDecsTitle(decs, title);
    }

    /**
     *Retrieve photos searched by description and date
     * @param decs search description
     * @param date search date
     * @return LiveData<List<ImageElement>> LiveData of List of ImageElement
     */
    // search photos by description and date
    public LiveData<List<ImageElement>> searchByDecsDate(String decs, String date){
        return dao.searchByDecsDate(decs, date);
    }

    /**
     *Retrieve photos searched by title and date
     * @param title search title
     * @param date search date
     * @return LiveData<List<ImageElement>> LiveData of List of ImageElement
     */
    // search photos by title and date
    public LiveData<List<ImageElement>> searchByTitleDate(String title, String date){
        return dao.searchByTitleDate(title, date);
    }

    /**
     *Retrieve photos searched by description and date and title
     * @param decs search description
     * @param date search date
     * @param title search title
     * @return LiveData<List<ImageElement>> LiveData of List of ImageElement
     */
    // search photos by title, description and date
    public LiveData<List<ImageElement>> searchByAll(String decs, String title, String date){
        return dao.searchByAll(decs, title, date);
    }

    /**
     *Delete photo
     * @param imageElement photo to be deleted
     */
    // delete photo
    public void deleteImage(ImageElement imageElement) {
        new deleteAsyncTask(dao).execute(imageElement);
    }

    /**
     *Update photo information
     * @param imageElement photo to be updated
     */
    // update information of photo
    public void updateInfo(ImageElement imageElement) {
        new updateAsyncTask(dao).execute(imageElement);
    }

    /**
     * AsyncTask to insert photos
     */
    //async task for insert
    private static class insertAsyncTask extends AsyncTask<ImageElement, Void, Integer> {
        private DAO mAsyncTaskDao;
        insertAsyncTask(DAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(final ImageElement... params) {
            mAsyncTaskDao.insert(params[0]);
            Log.i("MyRepository", "insert Image: "+params[0]+"");
            return 1;
        }
    }

    /**
     * AsyncTask to delete photos
     */
    // async task for delete
    private static class deleteAsyncTask extends  AsyncTask<ImageElement, Void, Void>{
        private DAO mAsyncTaskDao;
        deleteAsyncTask(DAO dao) { mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(ImageElement... imageElements) {
            mAsyncTaskDao.deleteImage(imageElements[0]);
            Log.i("MyRepository", "Delete:" + imageElements[0]);
            return null;
        }
    }

    /**
     * AsyncTask to update photos
     */
    // async task for update
    private static class updateAsyncTask extends  AsyncTask<ImageElement, Void, Void>{
        private DAO mAsyncTaskDao;
        updateAsyncTask(DAO dao) { mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(ImageElement... imageElements) {
            mAsyncTaskDao.updateInfo(imageElements[0]);
            Log.i("MyRepository", "Update:" + imageElements[0]);
            return null;
        }
    }

}


