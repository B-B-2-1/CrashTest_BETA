package com.example.josep.testingthedrawerlayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Contact extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList,numbersOnly;
    private Button getContactsButton;
    private Button doneButton;
    private ListView addedContacts;
    private String number,name,tenDigitNumber;
    private TextView textView;
    private TextView textView2;
    private boolean edit=false;
    private int index;
    private SharedPreferences mPrefs;
    public boolean itisfirsttime;

    private TinyDB tinyDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        insideOncreateFor_GettingContacts();
        tinyDB=new TinyDB(getApplicationContext());

        mPrefs=getSharedPreferences("FirstTimeCheck",MODE_PRIVATE);

        firstTimeorNot();
//...............................................................getting values from Sharedpreferences if available...........................
        if (itisfirsttime){

        }else {
            for (int i = 0; i < tinyDB.getListString("Names&Numbers").size(); i++) {
                arrayList.add(tinyDB.getListString("Names&Numbers").get(i));
                arrayAdapter.notifyDataSetChanged();
            }

            for (int i = 0; i < tinyDB.getListString("NumbersOnly").size(); i++) {
                numbersOnly.add(tinyDB.getListString("NumbersOnly").get(i));

            }

//.............................................................../getting values from Sharedpreferences if available...........................
        }

    }

    private void firstTimeorNot() {
        if (mPrefs.getBoolean("firstrun", true)) {
//            itisfirsttime=true;
//            mPrefs.edit().putBoolean("firstrun", false).commit();
        }else {
            textView.setText("Click in + to add contacts.\n Select contacts to replace them \n Long press to delete them");
            doneButton.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            itisfirsttime=false;
        }
    }

    private void insideOncreateFor_GettingContacts() {

        getContactsButton=findViewById(R.id.button2);
        doneButton=findViewById(R.id.button3);
        addedContacts=findViewById(R.id.listview);
        textView=findViewById(R.id.textView);
        textView2=findViewById(R.id.textView2);
        arrayList=new ArrayList<String>(); //Creating the actual array containing the numbers and names
        numbersOnly=new ArrayList<String>();//ArrayList of only numbers
        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,arrayList);//Getting the array ready for Display on the ListView
        addedContacts.setAdapter(arrayAdapter);  //Setting the ListView



        addedContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edit=true;
                index=position;
                getContacts();
            }
        });
        addedContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                arrayList.remove(position);
                arrayAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==5 && resultCode==RESULT_OK){

            doneButton.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.VISIBLE);

            itisfirsttime=true;
            mPrefs.edit().putBoolean("firstrun", false).commit();

            Uri contactUri=data.getData();
            Cursor cursor = getContentResolver().query(contactUri,null,null,null,null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int namecolumn=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            number=cursor.getString(column);
            name=cursor.getString(namecolumn);


            alignNumbers();

            EditOrAdd();


        }
    }

    private void EditOrAdd() {
        if (edit==true){
            arrayList.remove(index);
            arrayList.add(index,tenDigitNumber + "   " + name);
            numbersOnly.remove(index);
            numbersOnly.add(index,tenDigitNumber);
            arrayAdapter.notifyDataSetChanged();
            edit=false;

        }else {

            if (numbersOnly.contains(tenDigitNumber)){

            }else {
                numbersOnly.add(tenDigitNumber);
                arrayList.add(tenDigitNumber + "   " + name);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }


    public void getcontactsButton(View view) {

        getContacts();
    }

    private void getContacts() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, 5);
    }

    private void alignNumbers() {
        if (number.charAt(0)!='0'){
            number= "0" + number;

        }
        String newnumber= number.replace(" ","");
        String newnumber1= newnumber.replace("(","");
        String newnumber3= newnumber1.replace(")","");
        String newnumber2=newnumber3.replace("-","");
        tenDigitNumber=newnumber2.replace("+91","");
    }


    public void saveInSharedpreferences(View view) {

        tinyDB.putListString("Names&Numbers",arrayList);
        tinyDB.putListString("NumbersOnly",numbersOnly);

        Intent tomainactivity=new Intent(Contact.this,MainActivity.class);
        startActivity(tomainactivity);
        finish();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


