package com.gyk.firebaseauthenticationwithnavigationdrawer;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditPhotoActivity extends AppCompatActivity {
    private static final int IMAGE_ACTION_CODE = 102;
    private static final int CAMERA_PERMISSON_REQUEST_CODE = 103;
    private static final int IMAGE_REQUEST = 104;

    private ProgressDialog progressDialog;

    private Uri file;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private StorageReference storageReference;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        imageView = (ImageView) findViewById(R.id.imageViewProfilePhoto);

        downloadProfilePhoto();
        /* if (!checkPermission()) { //izinler kontrol edilir
            requestPermission(); //İzin verilmemiş ise izin istenir
        }*/
        Button takePhoto = (Button) findViewById(R.id.buttonTakePhoto);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermission()) {//izinler kontrol edilir

                    requestPermission();//İzin verilmemiş ise izin istenir
                } else {
                    takeNewPhoto(); //İzin verilmiş ise fotoğraf çek
                }
            }
        });
        Button savePhoto = (Button) findViewById(R.id.buttonSavePhoto);

        savePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePhotoStorage();
            }
        });

        Button buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });
    }

    public void choosePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Resim seçiniz"), IMAGE_REQUEST);

    }

    public void downloadProfilePhoto() {

        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageReference.child("ProfilePhotos").child(user.getUid()).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Picasso.get().load(localFile).centerCrop().fit().into(imageView);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Picasso.get().load(R.drawable.nav_profile_picture).centerCrop()
                                    .fit().into(imageView);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePhotoStorage() {
        showProgressBar();
        if (file != null) {
            storageReference.child("ProfilePhotos")
                    .child(user.getUid()).putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditPhotoActivity.this,
                                    "Fotoğraf yüklendi!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismissProgressBar();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) /
                                    taskSnapshot.getTotalByteCount();
                            Log.d("FireStorage", "onSuccess: " + (int) progress);
                            progressDialog.setProgress((int) progress);
                            if (progressDialog.getProgress() == 100) {
                                try {
                                    new Thread().sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                dismissProgressBar();
                                Toast.makeText(EditPhotoActivity.this,
                                        "Fotoğraf yüklendi!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void showProgressBar() {
        progressDialog = new ProgressDialog(EditPhotoActivity.this);
        progressDialog.setTitle("Gezgin Uygulaması");
        progressDialog.setMessage("Fotoğraf yükleniyor...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setMax(100);
    }

    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void takeNewPhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Fotoğraf çekme intenti
        startActivityForResult(takePhotoIntent, IMAGE_ACTION_CODE); //intenti belirlenen kod ile başlat
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return; //Fotoğraf veya Video onaylanır ise RESULT_OK döner

        switch (requestCode) {
            case IMAGE_ACTION_CODE:
                Bundle extras = data.getExtras();
                imageView.setImageBitmap((Bitmap) extras.get("data"));
                if (createFolder()) {
                    saveImage((Bitmap) extras.get("data"));
                    Picasso.get().load(file).centerCrop().fit().into(imageView);
                } else {
                    Toast.makeText(this, "Kaydedilemedi!", Toast.LENGTH_SHORT).show();
                }
                break;
            case IMAGE_REQUEST:

                Log.d("Url", "onActivityResult: " + data.getData().getPath());
                file = data.getData();
                try {
                    Picasso.get().load(data.getData()).centerCrop().fit().into(imageView);
                } catch (Exception e) {
                    Log.w("Picasso", "onActivityResult: ", e);
                }
                break;
        }
    }

    public boolean createFolder() {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "gyk301/Fotograflar");

        if (!folder.exists()) {
            return folder.mkdirs();
        } else {
            return true;
        }

    }

    private void saveImage(Bitmap bitmap) {
        OutputStream fOut = null;

        File f1 = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/gyk301/Fotograflar");

        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
                .format(new Date());


        File file = new File(f1.getAbsolutePath() + "/" + timeStamp + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
            MediaStore.Images.Media.insertImage(getContentResolver(),
                    file.getAbsolutePath(), file.getName(), file.getName());
            this.file = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w("SavePhoto", "saveImage: ", e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("SavePhoto", "saveImage: ", e);
        }
    }

    public boolean checkPermission() {
        // result WRITE_EXTERNAL_STORAGE izni var mı? varsa 0 yoksa -1
        int result = ContextCompat.
                checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // result1 RECORD_AUDIO izni var mı? varsa 0 yoksa -1
        int result1 = ContextCompat.
                checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        //İkisinede izin verilmiş ise true diğer durumlarda false döner
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        //Verilen String[] dizisi içerisindeki izinlere istek atılır
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CAMERA_PERMISSON_REQUEST_CODE);
    }


    //İstek atılır istek onay/red işlemi bittiğinde bu metod çalışır
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode istek atılırken kullanılan kod ile aynıysa
        if (requestCode == CAMERA_PERMISSON_REQUEST_CODE) {
            if (grantResults.length > 0) { // İzin verilenlerin listesi en az 1 elemanlı ise
                //Record izni verildi mi?
                boolean permissionToCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                //External Store izni verildi mi
                boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                //izinler kontrol edilir
                if (permissionToCamera && permissionToStore) {
                    Toast.makeText(this, "İzinler alındı!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "İzin vermen gerekli!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
