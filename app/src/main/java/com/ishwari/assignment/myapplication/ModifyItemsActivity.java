package com.ishwari.assignment.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ModifyItemsActivity extends Activity implements View.OnClickListener{
String id,item_name,item_qty,place_Id;
    private EditText titleText;
    private Button updateBtn, deleteBtn;
    private EditText qtyText;
    private DBManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_items);
        dbManager = new DBManager(this);
        dbManager.open();

        Intent intent = getIntent();
        id=intent.getStringExtra("id");
        item_name=intent.getStringExtra("name");
        item_qty=intent.getStringExtra("qty");
        place_Id=intent.getStringExtra("placeId_fk");
setTitle("Modify Item");
        titleText = (EditText) findViewById(R.id.name_edittext);
        qtyText = (EditText) findViewById(R.id.qty_edittext);

        updateBtn = (Button) findViewById(R.id.btn_update);
        deleteBtn = (Button) findViewById(R.id.btn_delete);

        titleText.setText(item_name);
        qtyText.setText(item_qty);

        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                String title = titleText.getText().toString();
                String qty = qtyText.getText().toString();

                dbManager.updateItem(id, title, qty);
                this.returnHome();
                break;

            case R.id.btn_delete:
               // dbManager.delete(_id);
                this.returnHome();
                break;
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), ShowListDetails.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);

    }
}
