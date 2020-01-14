package uk.ac.shef.oak.com6510;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * <h1>The ViewModel</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class MyViewModel extends AndroidViewModel {
    private final MyRepository myRepository;
    LiveData<List<ImageElement>> allImages;
    LiveData<List<ImageElement>> resultImages;

    public MyViewModel(@NonNull Application application) {
        super(application);
        myRepository = new MyRepository(application);
        allImages = myRepository.getAll();
    }



    public LiveData<List<ImageElement>> getAll() {
        return allImages;
    }


    public LiveData<List<ImageElement>> searchByTitle(String title){
        return myRepository.searchByTitle(title);
    }

    public LiveData<List<ImageElement>> searchByDecs(String decs) {
        return myRepository.searchByDecs(decs);
    }

    public LiveData<List<ImageElement>> searchByDate(String date){
        return myRepository.searchByDate(date);
    }

    public LiveData<List<ImageElement>> searchByDecsTitle(String decs, String title){
        System.out.println("Search by decs and title");
        return myRepository.searchByDecsTitle(decs, title);
    }

    public LiveData<List<ImageElement>> searchByDecsDate(String decs, String date){
        return myRepository.searchByDecsDate(decs, date);
    }

    public LiveData<List<ImageElement>> searchByTitleDate(String title, String date){
        return myRepository.searchByTitleDate(title, date);
    }

    public LiveData<List<ImageElement>> searchByAll(String decs, String title, String date){
        return myRepository.searchByAll(decs, title, date);
    }

    public void deleteImage(ImageElement imageElement) {
        myRepository.deleteImage(imageElement);
    }

    public void updateInfo(ImageElement imageElement) {
        myRepository.updateInfo(imageElement);
    }

    public void insertPhoto(List<File> returnedPhotos, String updateTitle, String updateDesc, Location lastLocation) {
        File thumbNailFile = null;
        for (File file : returnedPhotos) {
            //get current date
            Calendar calendar = Calendar.getInstance();
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String date = String.format("%s/%s/%s", day, month, year);

            // get compress bitmap
            Bitmap bitmap = Util.decodedBitmapFromResource(file.getAbsolutePath(), 100, 100);
            try {
                // get file of compress bitmap
                thumbNailFile = Util.saveThumbNailFile(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // path of original bitmap file and path of compress bitmap file
            String nailPath = thumbNailFile.getAbsolutePath();
            String filePath = file.getAbsolutePath();
            if (updateTitle == null) updateTitle = file.getName();

            Double latitude = 100.00;
            Double longitude = 200.00;
            if (lastLocation == null) {
                ImageElement element = new ImageElement(0, updateDesc, updateTitle, date, filePath, nailPath, longitude, latitude);
                myRepository.insertImage(element);
                System.out.println("Location of photos" + element.getLatitude() + ":" + element.getLongitude());

                try {
                    MediaStore.Images.Media.insertImage(ShowPhotosActivity.getActivity().getContentResolver(), element.getNailPath(), element.getTitle(), null);
                    Uri uri = Uri.parse(element.getNailPath());
                    ShowPhotosActivity.getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }

            } else {
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
                ImageElement element = new ImageElement(0, updateDesc, updateTitle, date, filePath, nailPath, longitude, latitude);
                myRepository.insertImage(element);

                try {
                    MediaStore.Images.Media.insertImage(ShowPhotosActivity.getActivity().getContentResolver(), element.getNailPath(), element.getTitle(), null);
                    Uri uri = Uri.parse(element.getNailPath());
                    ShowPhotosActivity.getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }

            }
        }
    }
}


