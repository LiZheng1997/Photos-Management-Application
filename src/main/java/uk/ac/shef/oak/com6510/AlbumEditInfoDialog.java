package uk.ac.shef.oak.com6510;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.util.ArrayList;

/**
 * <h1>Dialog of album photos when users want to change title or description</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class AlbumEditInfoDialog {
    boolean isEnter = false;
    ImageElement element;
    ArrayList<String> list = new ArrayList<>();
    String updateTitle;
    String updateDesc;
    MyViewModel myViewModel;
    public AlbumEditInfoDialog(ImageElement element, MyViewModel myViewModel){
        this.element = element;
        this.myViewModel = myViewModel;
    }

    //show dialog
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
                String curTitle = element.getTitle();
                list.add(title);
                list.add(desc);
                ShowImageActivity.editInfo = list;
                setEditInfo(list);



                // if title is not entered, not change
                if (title.isEmpty() || (title.equals(element.getTitle()))){
                    updateTitle = element.getTitle();
                }else{
                    // update title as what users enter
                    updateTitle = title;
                }

                //if description is not entered, not change
                if (desc.isEmpty()){
                    updateDesc = "add a description!";
                }else if (desc.equals(element.getDescription())){
                    updateDesc = element.getDescription();
                }else{
                    updateDesc = desc;
                }


                element.setTitle(updateTitle);
                element.setDescription(updateDesc);
                element.setDate(element.getDate());
                element.setFilePath(element.getFilePath());
                element.setId(element.getId());
                element.setLatitude(element.getLatitude());
                element.setLongitude(element.getLongitude());
                element.setNailPath(element.getNailPath());

                myViewModel.updateInfo(element);

                ShowImageActivity.titleText.setText("Title: \n" + updateTitle);
                ShowImageActivity.descText.setText("Description: \n" + updateDesc);
            }
        });
        // click cancel, nothing change
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

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
