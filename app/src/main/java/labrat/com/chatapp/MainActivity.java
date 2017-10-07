package labrat.com.chatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar insToolbar;
    private ViewPager insViewPager;
    private SectionAdapter sectionAdapter;
    private TabLayout insTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        insToolbar = (Toolbar) findViewById(R.id.mainActivity_navbar);
        setSupportActionBar(insToolbar);
        getSupportActionBar().setTitle("Chatter");

        insViewPager = (ViewPager) findViewById(R.id.tab_pager);
        sectionAdapter = new SectionAdapter(getSupportFragmentManager());
        insViewPager.setAdapter(sectionAdapter);

        insTabLayout = (TabLayout) findViewById(R.id.main_tab);
        insTabLayout.setupWithViewPager(insViewPager);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null){
            ChoiceIntent();
        }
        else if (!firebaseUser.isEmailVerified()){
            ChoiceIntent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()== R.id.main_logout){
            FirebaseAuth.getInstance().signOut();
            ChoiceIntent();
        }
        else if (item.getItemId() == R.id.main_accountSettings){
            Intent intent = new Intent(MainActivity.this, Account_Settings.class);
            startActivity(intent);
        }
        return true;
    }

    private void ChoiceIntent(){
        Intent SignUpChoice = new Intent(MainActivity.this, SignUpChoice.class);
        startActivity(SignUpChoice);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
