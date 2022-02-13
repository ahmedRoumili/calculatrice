package com.example.myapplicationhelloworld;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String NUMBERS_REGEXP = "[0-9]+([.]?[0-9]+)?";
    private static final String OPERATORS_REGEXP = "[+\\-*/]";
    private String numbers = "";
    private double result = 0.0;

    private TextView screen;
    private EditText board;
    private Button history, delete;
    LinearLayout listHistory;
    private Button cls;
    private GridLayout listColor;

    private boolean isOp = false;
    private String nowOp = "";
    private boolean firstNb = false;
    private int visibilityHistory = 0;
    private int visibilityColor = 1;
    private Map<Integer, List<String>> historyMap = new HashMap<>();
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screen = findViewById(R.id.screen);
        board = findViewById(R.id.board_text);
        history = findViewById(R.id.history);
        delete = findViewById(R.id.delete);
        cls = findViewById(R.id.btnClear);
        listHistory = findViewById(R.id.listHistory);
        listColor = findViewById(R.id.listColor);
        cls.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clear(true);
            }
        });
    }

    public void addNumber(View v) {
        isOp = false;
        firstNb = true;
        nowOp = "";
        compute(v);
    }

    public void setOperator(View v) {
        if (!nowOperation(v)) {
            compute(v);
        }
    }

    boolean nowOperation(View v) {
        System.out.println(String.format("%s    %s", nowOp.equals(((Button) v).getText()), firstNb));
        if (!nowOp.equals(((Button) v).getText()) & firstNb) {
            nowOp = (String) ((Button) v).getText();
            numbers = isOp ? numbers.substring(0, numbers.length() - 1) : numbers;
            isOp = true;
            return false;
        }
        return true;
    }

    public void equal(View v) {
        if (statusOperations()) {
            board.setText(String.format("%.2f", result));
            history.setEnabled(true);
            addHistory();
            visibilityHistory = 1;
            System.out.println(numbers + "    " + String.format("%.2f", result));
            clear(false);
        }
    }

    public void clear(boolean b) {
        numbers = "";
        nowOp = "";
        isOp = false;
        firstNb = false;
        screen.setText(numbers);
        if (b) {
            result = 0.0;
            board.setText(numbers);
        } else {
            board.setText(String.format("%.2f", result));
        }
    }

    public void compute(View v) {
        numbers = String.format("%s%s", numbers, ((Button) v).getText());
        board.setText(numbers);
        parser();
    }

    void parser() {
        if (statusOperations()) {
            Pattern p = Pattern.compile(NUMBERS_REGEXP);
            String[] listOperators = p.split(numbers);
            p = Pattern.compile(OPERATORS_REGEXP);
            String[] listNumbers = p.split(numbers);
            result = Double.parseDouble(listNumbers[0]);

            switchAllOperations(listNumbers, listOperators);
            update();
        }
    }

    boolean statusOperations() {
        // "^([0-9]+([+\\-*/][0-9]+)+)$"
        return numbers.matches(
                String.format("^(%s(%s%s)+)$", NUMBERS_REGEXP, OPERATORS_REGEXP, NUMBERS_REGEXP)
        );
    }

    void switchAllOperations(String[] listNumbers, String[] listOperators) {
        for (int i = 1; i < listNumbers.length; ++i) {
            switch (listOperators[i]) {
                case "+":
                    result += Double.parseDouble(listNumbers[i]);
                    break;
                case "-":
                    result -= Double.parseDouble(listNumbers[i]);
                    break;
                case "*":
                    result *= Double.parseDouble(listNumbers[i]);
                    break;
                case "/":
                    result /= Double.parseDouble(listNumbers[i]);
                    break;
            }
        }
    }

    public void update() {
        screen.setText(String.format("%.2f", result));
    }

    public void delete(View v) {
        if(firstNb){
            numbers = (numbers.length() != 0)? numbers.substring(0, numbers.length() - 1) : numbers;
            board.setText(numbers);
            screen.setText("");
            parser();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void history(View v) {
        if (visibilityHistory == 1) {
            listHistory.setVisibility(View.VISIBLE);
            visibilityHistory = 0;
        } else {
            listHistory.setVisibility(View.INVISIBLE);
            visibilityHistory = 1;
        }
    }

    public void addHistory() {
        historyMap.put(id, Arrays.asList(numbers, String.format("%.2f", result)));
        TextView txt = new TextView(this);
        txt.setText(historyMap.get(id).get(0) + " = " + historyMap.get(id).get(0));
        listHistory.addView(txt);
    }

    public void color(View v){
        if (visibilityColor == 1) {
            listColor.setVisibility(View.VISIBLE);
            visibilityColor = 0;
        } else {
            listColor.setVisibility(View.INVISIBLE);
            visibilityColor = 1;
        }
    }

    public void setColor(View v) {
        final Drawable draw = v.getBackground();
        findViewById(R.id.color).setBackground(draw);
        findViewById(R.id.delete).setBackground(draw);
        findViewById(R.id.listColor).setBackground(draw);
        history.setBackground(draw);
        listHistory.setBackground(draw);
        ArrayList<View> numberGrid = findViewById(R.id.numbers_grid).getTouchables();
        for (View vv : numberGrid) {
            vv.setBackground(draw);
        }
        ArrayList<View> listOper = findViewById(R.id.operators_grid_1).getTouchables();
        for (View vv : listOper) {
            vv.setBackground(draw);

        }
    }
}
