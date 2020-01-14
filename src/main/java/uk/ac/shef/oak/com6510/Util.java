package uk.ac.shef.oak.com6510;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

/**
 * <h1>Utility Class</h1>
 * Some useful and common methods are included.
 * <p>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class Util {
    public static ArrayList<String> uniqueDateList;
    public static ArrayList<String> dates;

    /**
     * Static method to decode bitmap from resource.
     * @param filePath path of file
     * @param reqWidth required width
     * @param reqHeight required height
     * @return Bitmap bitmap decoded from source.
     */
    // get ThumbNail Bitmap
    public static Bitmap decodedBitmapFromResource(String filePath, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();

        //set properties of options
        // only get the width and height of bitmap
        options.inJustDecodeBounds = true;

        // get bitmap
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        //return compress bitmap
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * Static method to calculate size of compressed bitmap.
     * @param reqHeight required height
     * @param reqWidth required width
     * @return int size of compressed bitmap.
     */
    public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Static method to save file.
     * @param bm Bitmap
     * @return int size of compressed bitmap.
     */
    // save ThumbNail File
    public static File saveThumbNailFile(Bitmap bm) throws IOException {
        File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/thumbNail");
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        String fileName = String.valueOf(UUID.randomUUID().toString()) + ".jpeg";
        File myCaptureFile = new File(dirFile, fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;
    }

    /**
     * Static method to get location of each photo.
     * @param list list stores ImageElement
     * @return ArrayList<Integer> list stores location of each photo
     */
    //get the number of photos and unique location
    public static ArrayList<Integer> getNumberAndLocation(List<ImageElement> list) {
        ArrayList<Integer> tempList = new ArrayList<>();

        int photosNumber = list.size();
        TreeSet<ImageElement> set = new TreeSet<ImageElement>(new locationComparator());
        int locationNumber = set.size();
        tempList.add(photosNumber);
        tempList.add(locationNumber);
        return tempList;
    }

    /**
     * Comparator of location of each photo
     */
    // location comparator
    public static class locationComparator implements Comparator<ImageElement> {

        @Override
        public int compare(ImageElement o1, ImageElement o2) {
            int result = (int) (o1.getLatitude() - o2.getLatitude());
            if (result == 0) {
                result = (int) (o1.getLongitude() - o2.getLongitude());
                return result;
            }
            return result;
        }
    }
    /**
     * Get the number of different date of all photos.
     * @param list list stores ImageElement
     * @return int number of different date of all photos
     */
    // find unique date
    public static int getNumberOfUniqueDate(List<ImageElement> list) {
        ArrayList<String> dates = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String date = list.get(i).getDate();
            dates.add(date);
        }

        setDates(dates);

        HashSet<String> set = new HashSet<>(dates);

        setUniqueDateList(new ArrayList<String>(set));

        return set.size();
    }

    /**
     * Get date of all photos.
     * @param list list stores ImageElement
     * @return  ArrayList<String> stores date of all photos
     */
    //get date list
    public static ArrayList<String> getDateList(List<ImageElement> list) {

        ArrayList<String> dateList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String date = list.get(i).getDate();
            dateList.add(date);
        }

        return dateList;
    }

    /**
     * Get different date of all photos.
     * @param list list stores ImageElement
     * @return  ArrayList<String> stores different date of all photos
     */
    public static ArrayList<String> getUniqueDateList(List<ImageElement> list){
        ArrayList<String> dates = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String date = list.get(i).getDate();
            dates.add(date);
        }

        HashSet<String> set = new HashSet<>(dates);
        ArrayList<String> uniqueDatesList = new ArrayList<>(set);

        return uniqueDatesList;
    }

    /**
     * Set list of different date.
     * @param set list of different date
     */
    public static void setUniqueDateList(ArrayList<String> set) {
        Util.uniqueDateList = set;
    }

    /**
     * Get list of date of all photos.
     * @return ArrayList<String> list of date of all photos
     */
    public static ArrayList<String> getDates() {
        return dates;
    }

    /**
     * Set list of date of all photos.
     * @param dates list of different date
     */
    public static void setDates(ArrayList<String> dates) {
        Util.dates = dates;
    }

    /**
     * Get list of photos on same date
     * @param date list of different date
     * @param list list of ImageElement
     * @return List<ImageElement> list of photos on same date
     */
    //find image on specific date
    public static List<ImageElement> getListOnDate(List<ImageElement> list, String date) {
        List<ImageElement> imageOnDateList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++){
            if (date.equals(list.get(i).getDate())){
                imageOnDateList.add(list.get(i));
            }else continue;
        }
        return imageOnDateList;
    }










}


