package uk.ac.shef.oak.com6510;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.a14290.photolocation.R;

/**
 * <h1>Define the fragment of Discover tab</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class DiscoverFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_show_discover_content, container, false);
        ImageButton button1 = view.findViewById(R.id.button1);
        ImageButton button2 = view.findViewById(R.id.button2);
        ImageButton button3 = view.findViewById(R.id.button3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // no Twitter app, open browser
                Intent  intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"));

                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // no Instagram app, open browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/"));
                ShowPhotosActivity.getActivity().startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // no Facebook app, open browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/"));
                ShowPhotosActivity.getActivity().startActivity(intent);
            }
        });

        return view;
    }


}
