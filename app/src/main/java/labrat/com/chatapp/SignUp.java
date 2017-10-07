package labrat.com.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private EditText insUsername;
    private EditText insEmail;
    private EditText insPassword;
    private Button insSignUp;
    private FirebaseAuth mAuth;
    private Toolbar insToolbar;
    private ProgressDialog insProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        insToolbar = (Toolbar) findViewById(R.id.signup_navbar);
        setSupportActionBar(insToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        insProgressDialog = new ProgressDialog(SignUp.this);

        insUsername = (EditText) findViewById(R.id.signup_username);
        insEmail = (EditText) findViewById(R.id.signup_email);
        insPassword = (EditText) findViewById(R.id.signup_password);
        insSignUp = (Button) findViewById(R.id.signup_button);

        insSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = insUsername.getText().toString();
                String email = insEmail.getText().toString();
                String password = insPassword.getText().toString();

                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    insProgressDialog.setTitle("Creating Account");
                    insProgressDialog.setMessage("Please wait for a while!!");
                    insProgressDialog.setCanceledOnTouchOutside(false);
                    insProgressDialog.show();

                    signup(username,email,password);
                }
            }
        });
    }

    private void signup(String username, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            insProgressDialog.hide();
                            Toast.makeText(SignUp.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUp.this, "Verification mail sent.", Toast.LENGTH_LONG).show();
                                                insProgressDialog.dismiss();
                                                Intent intent = new Intent(SignUp.this,login.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                insProgressDialog.hide();
                                                Toast.makeText(SignUp.this, task.getException().toString() , Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }

                    }
                });
    }
}
