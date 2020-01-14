package uk.ac.shef.oak.com6510;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
/**
 * <h1>Dialog for users to enter title and description after taking photos</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class TakePhotoDialog {
    boolean isEnter = false;
    ImageElement element;
    ArrayList<String> list = new ArrayList<>();
    String updateTitle;
    String updateDesc;
    MyViewModel myViewModel;
    List<File> returnedPhotos;
    Location lastLocation;
    public TakePhotoDialog(List<File> returnedPhotos,MyViewModel myViewModel, Location lastLocation){
        this.returnedPhotos = returnedPhotos;
        this.myViewModel = myViewModel;
        this.lastLocation = lastLocation;
    }
    /**
     * Show dialog
     * @param activity activity which shows this dialog
     */
    public  void showDialog(final Activity activity){

        LayoutInflater factory = LayoutInflater.from(activity);

        final View textEntryView = factory.inflate(R.layout.edit_photo, null);
        final EditText editTextTitle = (EditText) textEntryView.findViewById(R.id.editTitle);
        final EditText editTextDescription = (EditText)textEntryView.findViewById(R.id.editDescription);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Edit Photo Information:");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
        alertDialogBuilder.setView(textEntryView);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
            String title = editTextTitle.getText().toString();
            String desc = editTextDescription.getText().toString();
            list.add(title);
            list.add(desc);
            ShowImageActivity.editInfo = list;
            setEditInfo(list);

            // if no title is entered, using default title
            if (title.isEmpty()){
                updateTitle = null;
            }else{
                // else using entered title
                updateTitle = title;
            }

            // if no description is entered, using default description
            if (desc.isEmpty()){
                updateDesc = "add a description!";
            }else{
                // else using entered description
                updateDesc = desc;
            }

            // check if WRITE_EXTERNAL_STORAGE is granted

                // insert photo
                if (ShowPhotosActivity.getStorageIsGranted()) {
                    myViewModel.insertPhoto(returnedPhotos, updateTitle, updateDesc, lastLocation);
                }else {
                    Toast.makeText(ShowPhotosActivity.getActivity(), "Permission Denied, cannot save photo", Toast.LENGTH_LONG).show();
                }
            }
        });

        //click cancel, user default title and description
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                long timeInMillis = Calendar.getInstance().getTimeInMillis();
                String updateTitle = String.valueOf(timeInMillis);
                String updateDesc = "add a description!";
                list.add(updateTitle);
                list.add(updateDesc);
                ShowImageActivity.editInfo = list;
                setEditInfo(list);

                // insert photo
                if (ShowPhotosActivity.getStorageIsGranted()){
                myViewModel.insertPhoto(returnedPhotos,updateTitle,updateDesc,lastLocation);
                }else{
                    Toast.makeText(ShowPhotosActivity.getActivity(), "Permission Denied, cannot save photo", Toast.LENGTH_LONG).show();
                }
            }
        });
        alertDialogBuilder.show();
    }

    /**
     * Set list of title and description
     * @param list list stores title and description
     */
    public void setEditInfo(ArrayList<String> list){
        this.list = list;
    }

    /**
     * Get list of title and description
     * @return list stores title and description
     */
    public ArrayList<String> getEditInfo(){
        return list;
    }

    /**
     * check if information are entered
     * @return if information are entered
     */
    public boolean getIndicator(){
        return isEnter;
    }
}
