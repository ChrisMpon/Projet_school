package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private EditText pubTitle;
    private LinearLayout addImage;
    private Button sendPub;
    private ImageView media;

    private androidx.appcompat.widget.Toolbar mToolbar;
    private int reqCode;
    private Intent mData;
    private String pub_id, urlImage;

    private DatabaseReference mPubDatabase;
    private FirebaseUser mCurrentUser;

    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;

    private Bitmap bitmap;

    private Uri ImageUri;

    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.pub_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Nouvelle Publication");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pubTitle = (EditText) findViewById(R.id.contentPub);
        addImage = (LinearLayout) findViewById(R.id.add_newPub);
        media = (ImageView) findViewById(R.id.contentmedia);
        sendPub = (Button) findViewById(R.id.pub_sum);
        mProgressDialog = new ProgressDialog(this);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrentUser.getUid();

        mPubDatabase = Utils.getDatabase().getReference().child("publications");
        mPubDatabase.keepSynced(true);
        sendPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidatePubtData();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                OpenGallery();

            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        reqCode = requestCode;
        mData = data;

        if (reqCode == GalleryPick  &&  resultCode == RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(5000, 5000)
                    .start(this);
            media.setImageURI(ImageUri);
        }
    }

    private void StorePubInformation()
    {

        mProgressDialog.setTitle("Ajout de la publication en cours");
        mProgressDialog.setMessage("Svp patientez pendant que nous postons votre publication");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        if(ImageUri == null)
        {
            SaveProductInfoToDatabase();
        }
        else
        {
            if (reqCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(mData);

                ImageUri = result.getUri();

                final StorageReference filePath = mImageStorage.child("publications").child(mCurrentUser.getUid()).child(pub_id + ".jpg");

                final UploadTask uploadTask = filePath.putFile(ImageUri);


                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        String message = e.toString();
                        Toast.makeText(PostActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Toast.makeText(PostActivity.this, "Publication téléchargée avec succès", Toast.LENGTH_SHORT).show();

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                            {
                                if (!task.isSuccessful())
                                {
                                    throw task.getException();
                                }

                                urlImage = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task)
                            {
                                if (task.isSuccessful())
                                {
                                    urlImage = task.getResult().toString();


                                    SaveProductInfoToDatabase();
                                }
                            }
                        });
                    }
                });
            }



        }

    }


    private void ValidatePubtData()
    {

        if (ImageUri == null && TextUtils.isEmpty(pubTitle.getText().toString()))
        {
            Toast.makeText(this, "Veuillez renseigner au moins une information svp!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StorePubInformation();
        }
    }


    private void SaveProductInfoToDatabase()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        Map update_hashMap = new HashMap();

        if(ImageUri == null)
        {
            update_hashMap.put("pub_image", "default");
        }
        else
        {
            update_hashMap.put("pub_image", urlImage);
        }


        update_hashMap.put("user_id", mCurrentUser.getUid());
        update_hashMap.put("post_date", strDate);
        update_hashMap.put("text_content", pubTitle.getText().toString().trim());

        pub_id = mPubDatabase.push().getKey();

        mPubDatabase.child(pub_id).updateChildren(update_hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(PostActivity.this, MainActivity.class);
                            startActivity(intent);

                            mProgressDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Publication ajoutée avec succès", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            mProgressDialog.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(PostActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}