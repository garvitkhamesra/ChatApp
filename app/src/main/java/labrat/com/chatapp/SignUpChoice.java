package labrat.com.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpChoice extends AppCompatActivity {
    Button choice_signup;
    Button login_choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_choice);

        choice_signup = (Button)findViewById(R.id.choice_signup);
        choice_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(SignUpChoice.this, SignUp.class);
                startActivity(signup);
            }
        });

        login_choice = (Button)findViewById(R.id.login_choice);
        login_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(SignUpChoice.this, login.class);
                startActivity(login);
            }
        });
    }
}
