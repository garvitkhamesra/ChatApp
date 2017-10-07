package labrat.com.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class login extends AppCompatActivity {
    private EditText insEmail;
    private EditText insPassword;
    private Button insLogin;
    private FirebaseAuth mAuth;
    private Toolbar insToolbar;
    private ProgressDialog insProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        insToolbar = (Toolbar) findViewById(R.id.signup_navbar);
        setSupportActionBar(insToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        insProgressDialog = new ProgressDialog(login.this);

        insEmail = (EditText) findViewById(R.id.login_email);
        insPassword = (EditText) findViewById(R.id.login_password);
        insLogin = (Button) findViewById(R.id.login_button);


        insLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = insEmail.getText().toString();
                String password = insPassword.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    insProgressDialog.setTitle("Logging in..");
                    insProgressDialog.setMessage("Please wait for a while!!");
                    insProgressDialog.setCanceledOnTouchOutside(false);
                    insProgressDialog.show();
                    login(email,password);
                }
            }
        });
    }


    private void login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            insProgressDialog.hide();
                            Toast.makeText(login.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }else{

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (!firebaseUser.isEmailVerified()){
                                insProgressDialog.hide();
                                Toast.makeText(login.this, "Please Verify and Login", Toast.LENGTH_LONG).show();
                            }
                            else {
                                insProgressDialog.dismiss();
                                Intent intent = new Intent(login.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                    }
                });
    }
}
