package com.bhumca2017.eventuate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by toaka on 29-04-2017.
 */

public class AvailabilityImagesAdapter extends ArrayAdapter<AvailabilityImages> {


    Context context;
    ProgressDialog dialog;
    private static String DELETE_IMAGE_URL;
    Integer itemPosition = -1;
    private String mType;

    AvailabilityImagesAdapter(Context context, ArrayList<AvailabilityImages> availabilityImagesList,String type) {


        super(context,0,availabilityImagesList);
        this.context = context;
        DELETE_IMAGE_URL = context.getString(R.string.ip_address)+"/Eventuate/Services/DeleteImgAvail.php";
        mType = type;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        ViewHolder viewHolder = null;
        if(listItemView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            listItemView = layoutInflater.inflate(R.layout.availabilities_images_item,parent,false);
            viewHolder.imageView = (ImageView) listItemView.findViewById(R.id.avail_image_view);
            listItemView.setTag(viewHolder);
        }
        else {
            viewHolder= (ViewHolder) listItemView.getTag();
        }
        Button deleteButton = (Button) listItemView.findViewById(R.id.delete_image);
        if(mType.equals("services")) {
            final int pos = position;
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemPosition = pos;
                    showAlert(getItem(pos).getImageId());
                }
            });
        } else {
            deleteButton.setVisibility(View.GONE);
        }
        Picasso.with(getContext()).load(getItem(position).getImageLocation()).into(viewHolder.imageView);


      //  new LoadImageAsyncTask().execute(getItem(position).getImageLocation());

        return listItemView;
    }
    static class ViewHolder{
        ImageView imageView;
    }
    void showAlert(final Integer imageId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Image");
        builder.setMessage("Are sure? Do you want to delete it?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new deleteAvailabilityImagesAsyncTask().execute(imageId);

            }
        });

        builder.setNegativeButton("Cancel",null);

        builder.show();
    }
    private HashMap<String,String> getPostParamsForDeleteImage(Integer imageId) {

        HashMap<String,String> params = new HashMap<>();
        params.put("imageId",imageId.toString());
        return params;

    }

    class deleteAvailabilityImagesAsyncTask extends AsyncTask<Integer,Void,String> {

        @Override
        protected void onPreExecute() {
            showProgressDialog("Deleting image please wait...");
        }

        @Override
        protected String doInBackground(Integer... params) {

            ServerRequestHandler requestHandler = new ServerRequestHandler();
            String result="";
            try {
                result = requestHandler.sendPostRequest(DELETE_IMAGE_URL,getPostParamsForDeleteImage(params[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.hide();
           // Log.e("res",result);
            if(result.equals("1") && itemPosition!=-1){

                remove(getItem(itemPosition));
                Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(context,"Unable to delete. Try again",Toast.LENGTH_SHORT).show();
            }
        }
    }
    void showProgressDialog(String message){
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    /*class LoadImageAsyncTask extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected void onPreExecute() {

            //showProgressDialog("Please wait loading images");
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL imageUrl = null;
            try {
                imageUrl= new URL(urls[0]);
                // Log.e("imageUrl",imageUrl.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bitmap imageBitmap = null;
            try {
                imageBitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            //Log.e("listView",listView.toString());

            //mViewHolder.imageView.setImageBitmap(bitmap);

        }
    }*/

}
