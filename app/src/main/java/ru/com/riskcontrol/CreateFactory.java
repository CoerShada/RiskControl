package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

public class CreateFactory extends AppCompatActivity {

    int currentModelsRating;
    float editFrom;
    float editTo;
    int lastIndexFactory;
    DBHelper dpHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_factory);

        this.currentModelsRating = getIntent().getIntExtra("currentModelsRating", 0);
        this.editFrom = getIntent().getFloatExtra("editFrom", 0);
        this.editTo = getIntent().getFloatExtra("editTo", 0);
    }

    public void buttonBackOnClick(View view){
        Intent intent = new Intent(CreateFactory.this, CreateRegistry.class);

        intent.putExtra("currentModelsRating", currentModelsRating);
        EditText editFrom = findViewById(R.id.editTextFrom);
        EditText editTo = findViewById(R.id.editTextTo);
        intent.putExtra("editFrom", editFrom.getText());
        intent.putExtra("editTo", editTo.getText());
        startActivity(intent);
    }

    public void buttonCreateOnClick(View view){
        Intent intent = new Intent(CreateFactory.this, CreateRegistry.class);

        EditText name = findViewById(R.id.label_factory_name);
        if (name.getText().toString().trim().length()!=0){
            EditText desc = findViewById(R.id.description);
            SQLiteDatabase db = dpHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("name", name.getText().toString());
            cv.put("description", desc.getText().toString());
            db.insert("factories", null , cv);
        }

        intent.putExtra("currentFactory", this.lastIndexFactory+1);
        intent.putExtra("currentModelsRating", currentModelsRating);
        EditText editFrom = findViewById(R.id.editTextFrom);
        EditText editTo = findViewById(R.id.editTextTo);
        intent.putExtra("editFrom", editFrom.getText());
        intent.putExtra("editTo", editTo.getText());
        startActivity(intent);
    }
}