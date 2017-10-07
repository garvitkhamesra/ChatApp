package labrat.com.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
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


import de.hdodenhof.circleimageview.CircleImageView;

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
        insDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.with(Account_Settings.this).load(image).into(insImage);

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
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            insFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String uid = insFirebaseUser.getUid();

            progressDialog = new ProgressDialog(Account_Settings.this);
            progressDialog.setTitle("Uploading Image");
            progressDialog.setMessage("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            final Uri file = data.getData();

            final StorageReference filepath = mStorageRef.child("display_pictures").child(uid+ ".jpg");

            filepath.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String download_url = taskSnapshot.getDownloadUrl().toString();
                            insDatabaseReference.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.hide();
                                        Toast.makeText(Account_Settings.this, "Upload Successfull", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(Account_Settings.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
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
