package labrat.com.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.R.attr.bitmap;

public class Account_Settings extends AppCompatActivity {

    private DatabaseReference insDatabaseReference;
    private FirebaseUser insFirebaseUser;
    private TextView insStaus;
    private TextView insName;
    private CircleImageView insImage;
    private Button insStatusUpdate;
    private Button insChangeImage;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog progressDialog;

    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__settings);

        insName = (TextView) findViewById(R.id.displayNameSettings);
        insStaus = (TextView) findViewById(R.id.statusMessage);
        insImage = (CircleImageView) findViewById(R.id.displayPictureSettings);
        insChangeImage = (Button) findViewById(R.id.changeImageButton);
        insStatusUpdate = (Button) findViewById(R.id.statusChangeButton);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        insFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = insFirebaseUser.getUid();
        insDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        insDatabaseReference.keepSynced(true);
        insDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                if (!image.equals("default")){
                    Picasso.with(Account_Settings.this).load(image).placeholder(R.drawable.ca).into(insImage);
                }

                insName.setText(name);
                insStaus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        insStatusUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account_Settings.this,StatusActivity.class);
                startActivity(intent);
            }
        });

        insChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),GALLERY_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            insFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String uid = insFirebaseUser.getUid();

            progressDialog = new ProgressDialog(Account_Settings.this);
            progressDialog.setTitle("Uploading Image");
            progressDialog.setMessage("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Uri file = data.getData();
            File thumb_file = new File(file.getPath());

            Bitmap thumb_bitmap = null;
            try {
                thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(50)
                        .compressToBitmap(thumb_file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] thumbByte = baos.toByteArray();

            final StorageReference thumb_filepath = mStorageRef.child("display_pictures").child("thumbnails").child(uid+".jpg");
            final StorageReference filepath = mStorageRef.child("display_pictures").child(uid + ".jpg");

            filepath.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            final String download_url = taskSnapshot.getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filepath.putBytes(thumbByte);

                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbnails_task) {

                                    String thumb_downloadUrl = thumbnails_task.getResult().getDownloadUrl().toString();
                                    Map hashmapThmbs = new HashMap();
                                    hashmapThmbs.put("image",download_url);
                                    hashmapThmbs.put("thumb_image",thumb_downloadUrl);


                                    if (thumbnails_task.isSuccessful()){
                                        insDatabaseReference.updateChildren(hashmapThmbs).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.hide();
                                                    Toast.makeText(Account_Settings.this, "Upload Successfull", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(Account_Settings.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.hide();
                            Toast.makeText(Account_Settings.this, exception.getMessage().toString(), Toast.LENGTH_LONG).show();

                        }
                    });
        }

    }
}
