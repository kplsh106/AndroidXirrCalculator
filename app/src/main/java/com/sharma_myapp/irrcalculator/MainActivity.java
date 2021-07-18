package com.sharma_myapp.irrcalculator;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private ArrayList<invData> investmentList=new ArrayList<invData>();

    private int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyEditTextDatePicker a=new MyEditTextDatePicker(this,R.id.date_org);
        invDataAdapter adapter = new invDataAdapter(getApplicationContext(),
                R.layout.list, investmentList);
        ListView listview = findViewById(R.id.amount_list);
        listview.setAdapter(adapter);


        EditText editText = (EditText) findViewById(R.id.amount_org);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                }
                EditText dateEditText=findViewById(R.id.date_org);
                String dateInfo=dateEditText.getText().toString();

                EditText amountEditText=findViewById(R.id.amount_org);

                Log.v("amount","the string i got is "+dateEditText.getText().toString());
                double amount=Double.parseDouble(amountEditText.getText().toString());
                ++counter;
                investmentList.add(new invData(dateInfo,amount,counter));
                dateEditText.requestFocus();
                dateEditText.getText().clear(); //or you can use editText.setText("");
                amountEditText.getText().clear(); //or you can use editText.setText("");
                adapter.notifyDataSetChanged();
                return handled;
            }
        });


        FloatingActionButton fabDone = findViewById(R.id.fab_done);
        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Date> dates=new ArrayList<Date>();
                ArrayList<Double> amountList=new ArrayList<Double>();
                for(int i=0;i<investmentList.size();i++) {
                    dates.add(investmentList.get(i).getAccDate());
                    amountList.add(investmentList.get(i).getAmount());
                }
                XirrDate xirrCalc=new XirrDate();
                double xirrans=xirrCalc.Newtons_method(0.1,amountList,dates);
                xirrans=xirrans*100;
                String per="%";
                String xirrStr=String.format("the rate of return is %.2f%s",xirrans,per);
                EditText ansEditText=findViewById(R.id.answer);
                FloatingActionButton doneEditText=findViewById(R.id.fab_done);
                LinearLayout ansLinearLayout=findViewById(R.id.enter_ans);

                ansEditText.setVisibility(View.VISIBLE);
                ansEditText.setText(xirrStr);
                doneEditText.setVisibility(View.INVISIBLE);
                ansLinearLayout.setVisibility(View.INVISIBLE);



            }
        });

    }
    //this entire class is for processing calendar dates
    public class MyEditTextDatePicker  implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
        EditText _editText;
        private int _day;
        private int _month;
        private int _birthYear;
        private Context _context;

        public MyEditTextDatePicker(Context context, int editTextViewID)
        {
            Activity act = (Activity)context;
            this._editText = act.findViewById(editTextViewID);
            this._editText.setOnClickListener((View.OnClickListener) this);
            this._context = context;
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            _birthYear = year;
            _month = monthOfYear;
            _day = dayOfMonth;
            updateDisplay();
        }
        public void onClick(View v) {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            DatePickerDialog dialog = new DatePickerDialog(_context, (DatePickerDialog.OnDateSetListener) this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();

        }

        // updates the date in the birth date EditText
        private void updateDisplay() {

            _editText.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(_day).append("/").append(_month + 1).append("/").append(_birthYear).append(" "));
        }
    }

    public class XirrDate {

        public static final double tol = 0.001;

        public  double dateDiff(Date d1, Date d2){
            long day = 24*60*60*1000;

            return (d1.getTime() - d2.getTime())/day;
        }

        public  double f_xirr(double p, Date dt, Date dt0, double x) {
            return p * Math.pow((1.0 + x), (dateDiff(dt0,dt) / 365.0));
        }

        public  double df_xirr(double p, Date dt, Date dt0, double x) {
            return (1.0 / 365.0) * dateDiff(dt0,dt) * p * Math.pow((x + 1.0), ((dateDiff(dt0,dt) / 365.0) - 1.0));
        }

        public  double total_f_xirr(ArrayList<Double> payments, ArrayList<Date> days, double x) {
            double resf = 0.0;

            for (int i = 0; i < payments.size(); i++) {
                resf = resf + f_xirr(payments.get(i), days.get(i), days.get(0), x);
            }

            return resf;
        }

        public  double total_df_xirr(ArrayList<Double> payments, ArrayList<Date> days, double x) {
            double resf = 0.0;

            for (int i = 0; i < payments.size(); i++) {
                resf = resf + df_xirr(payments.get(i), days.get(i), days.get(0), x);
            }

            return resf;
        }

        public  double Newtons_method(double guess, ArrayList<Double> payments, ArrayList<Date> days) {
            double x0 = guess;
            double x1 = 0.0;
            double err = 1e+100;

            while (err > tol) {
                x1 = x0 - total_f_xirr(payments, days, x0) / total_df_xirr(payments, days, x0);
                err = Math.abs(x1 - x0);
                x0 = x1;
            }

            return x0;
        }

        private SimpleDateFormat sdf;

        {
            sdf = new SimpleDateFormat("dd/MM/yyyy");
        }

        public Date strToDate(String str){
            try {
                return sdf.parse(str);
            } catch (ParseException ex) {
                return null;
            }
        }
    }

}
