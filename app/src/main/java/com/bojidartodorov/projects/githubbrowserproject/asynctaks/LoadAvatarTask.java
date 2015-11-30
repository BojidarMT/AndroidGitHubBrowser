package com.bojidartodorov.projects.githubbrowserproject.asynctaks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import com.bojidartodorov.projects.githubbrowserproject.asynctaks.taskresponses.AvatarBitmapResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Bojidar on 22.11.2015 г..
 */
public class LoadAvatarTask extends AsyncTask<String, Void, Bitmap> {

    private Context context;
    private String processMessage;
    private ProgressDialog pDlg;
    private AvatarBitmapResponse avatarBitmapResponse;

    public LoadAvatarTask(Context context, String processMessage) {
        this.context = context;
        this.processMessage = processMessage;
    }

    private void showProgressDialog() {

        this.pDlg = new ProgressDialog(context);
        this.pDlg.setMessage(processMessage);
        this.pDlg.show();
        this.pDlg.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {

        if (this.pDlg == null) {
            this.showProgressDialog();
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        String urlImage = params[0];

        Bitmap bitmap = null;
        URL url = null;

        try {
            url = new URL(urlImage);

            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        avatarBitmapResponse.avatarBitmapResponse(bitmap);

        if (this.pDlg != null && this.pDlg.isShowing()) {
            this.pDlg.dismiss();
            this.pDlg = null;

        }
    }

    public void setAvatarBitmapResponse(AvatarBitmapResponse avatarBitmapResponse) {
        this.avatarBitmapResponse = avatarBitmapResponse;
    }

}
