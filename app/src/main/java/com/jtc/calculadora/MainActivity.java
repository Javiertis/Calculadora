package com.jtc.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.Expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

import static android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {

    Button numpad_1, numpad_2, numpad_3, numpad_4, numpad_5, numpad_6, numpad_7, numpad_8, numpad_9, numpad_0,
            numpad_punto, numpad_suma, numpad_resta, numpad_div, numpad_multi, numpad_pow, numpad_borrarTodo, numpad_igual,
            numpad_parentesis, numpad_borrar, numpad_log, numpad_raiz, numpad_ans;
    TextView resultado;
    EditText cuenta;
    String cuentaActual, resultadoAnterior;
    HashMap<Integer, String> historial;
    Expression exp;
    Spinner registro;
    int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        historial = new HashMap<>();


        //eventos onClick
        OnClickListener escribir = new OnClickListener() {
            @Override
            public void onClick(View v) {
                escribir(((Button) v).getText().toString());
            }
        };
        OnClickListener escribirLogaritmo = new OnClickListener() {
            @Override
            public void onClick(View v) {
                escribirEspecial("ln(");
            }
        };

        OnClickListener escribirRaiz = new OnClickListener() {
            @Override
            public void onClick(View v) {
                escribirEspecial(getResources().getString(R.string.num_pad_raiz) + "(");
            }
        };
        OnClickListener escribirAns = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resultadoAnterior!=null)
                escribirEspecial(resultadoAnterior);
            }
        };
        OnClickListener borrar = new OnClickListener() {
            @Override
            public void onClick(View v) {
                borrar();
            }
        };
        OnClickListener borrarTodo = new OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarTodo();
            }
        };
        OnClickListener parentesis = new OnClickListener() {
            @Override
            public void onClick(View v) {
                parentesis();
            }
        };
        OnClickListener igual = new OnClickListener() {
            @Override
            public void onClick(View v) {
                igual();
            }
        };
        AdapterView.OnItemSelectedListener seleccionarCuenta = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentItem!=position) {
                    cuenta.setText(String.format("%s%s", cuenta.getText(), registro.getItemAtPosition(position).toString().split("=")[1]));
                    cuenta.setSelection(cuenta.getText().length());
                    currentItem=position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        //numeros
        numpad_1 = findViewById(R.id.numpad_1);
        numpad_1.setOnClickListener(escribir);

        numpad_2 = findViewById(R.id.numpad_2);
        numpad_2.setOnClickListener(escribir);

        numpad_3 = findViewById(R.id.numpad_3);
        numpad_3.setOnClickListener(escribir);

        numpad_4 = findViewById(R.id.numpad_4);
        numpad_4.setOnClickListener(escribir);

        numpad_5 = findViewById(R.id.numpad_5);
        numpad_5.setOnClickListener(escribir);

        numpad_6 = findViewById(R.id.numpad_6);
        numpad_6.setOnClickListener(escribir);

        numpad_7 = findViewById(R.id.numpad_7);
        numpad_7.setOnClickListener(escribir);

        numpad_8 = findViewById(R.id.numpad_8);
        numpad_8.setOnClickListener(escribir);

        numpad_9 = findViewById(R.id.numpad_9);
        numpad_9.setOnClickListener(escribir);

        numpad_0 = findViewById(R.id.numpad_0);
        numpad_0.setOnClickListener(escribir);


        //simbolos
        numpad_punto = findViewById(R.id.numpad_punto);
        numpad_punto.setOnClickListener(escribir);

        numpad_suma = findViewById(R.id.numpad_suma);
        numpad_suma.setOnClickListener(escribir);

        numpad_resta = findViewById(R.id.numpad_resta);
        numpad_resta.setOnClickListener(escribir);

        numpad_div = findViewById(R.id.numpad_div);
        numpad_div.setOnClickListener(escribir);

        numpad_multi = findViewById(R.id.numpad_multiplicar);
        numpad_multi.setOnClickListener(escribir);

        numpad_pow = findViewById(R.id.numpad_pow);
        numpad_pow.setOnClickListener(escribir);

        numpad_raiz = findViewById(R.id.numpad_raiz);
        numpad_raiz.setOnClickListener(escribirRaiz);

        numpad_log = findViewById(R.id.numpad_log);
        numpad_log.setOnClickListener(escribirLogaritmo);

        numpad_parentesis = findViewById(R.id.numpad_parentesis);
        numpad_parentesis.setOnClickListener(parentesis);

        numpad_borrar = findViewById(R.id.numpad_borrar);
        numpad_borrar.setOnClickListener(borrar);

        numpad_borrarTodo = findViewById(R.id.numpad_borrar_todo);
        numpad_borrarTodo.setOnClickListener(borrarTodo);

        numpad_igual = findViewById(R.id.numpad_igual);
        numpad_igual.setOnClickListener(igual);

        numpad_ans = findViewById(R.id.numpad_ans);
        numpad_ans.setOnClickListener(escribirAns);

        cuenta = findViewById(R.id.operacion);
        cuenta.setText("");
        cuenta.setShowSoftInputOnFocus(false);

        resultado = findViewById(R.id.resultado);
        resultado.setText("");

        registro = findViewById(R.id.spinner);
        registro.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, historial.values().toArray(new String[historial.size()])));
        registro.setOnItemSelectedListener(seleccionarCuenta);
    }


    void escribir(String str) {
        String old = cuenta.getText().toString();
        int cursorPos = cuenta.getSelectionStart();
        String izqString = old.substring(0, cursorPos);
        String dchaString = old.substring(cursorPos);
        cuenta.setText(String.format("%s%s%s", izqString, str, dchaString));
        cuenta.setSelection(cursorPos + 1);
        calcular();
    }

    void escribirEspecial(String str) {
        String old = cuenta.getText().toString();
        int cursorPos = cuenta.getSelectionStart();
        String izqString = old.substring(0, cursorPos);
        String dchaString = old.substring(cursorPos);
        cuenta.setText(String.format("%s%s%s", izqString, str, dchaString));
        cuenta.setSelection(cursorPos + str.length());
    }

    void borrar() {
        String old = cuenta.getText().toString();
        int cursorPos = cuenta.getSelectionStart();
        if (old.length() != 0 && cursorPos != 0) {
            String izqString = old.substring(0, cursorPos - 1);
            String dchaString = old.substring(cursorPos);
            cuenta.setText(String.format("%s%s", izqString, dchaString));
            cuenta.setSelection(cursorPos - 1);
            calcular();
        }
    }

    void parentesis() {
        int cursorPos = cuenta.getSelectionStart();
        int parentesisAbiertos = 0, parentesisCerrados = 0;
        int txtLength = cuenta.getText().toString().length();
        for (int i = 0; i < cursorPos; i++) {
            if (cuenta.getText().toString().charAt(i) == '(') {
                parentesisAbiertos++;
            }
            if (cuenta.getText().toString().charAt(i) == ')') {
                parentesisCerrados++;
            }
        }
        if (parentesisAbiertos == parentesisCerrados || cuenta.getText().toString().substring(txtLength - 1).equals("(")) {
            escribir("(");
        } else {
            escribir(")");
        }
        calcular();
    }

    void borrarTodo() {
        cuenta.setText("");
        resultado.setText("");
    }


    private void igual() {
        normalize();
        exp = new Expression(cuentaActual);
        double res = exp.calculate();
        if (exp.checkSyntax() && !(Double.isInfinite(res) || Double.isNaN(res))) {
            resultadoAnterior = String.valueOf(new BigDecimal(res, MathContext.DECIMAL32));
            if (!historial.containsValue(String.format("%s=%s", cuentaActual, res))) {
                historial.put(historial.size(), String.format("%s=%s", cuentaActual, res));
                registro.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, historial.values().toArray(new String[historial.size()])));
                currentItem=historial.size() - 1;
                registro.setSelection(historial.size() - 1);
                resultado.setText("");
                cuenta.setText("");
                cuentaActual = "";
            }
        } else if (Double.isInfinite(res)) {
            resultado.setText("Infinito");
        } else {
            resultado.setText("NaN");
        }
    }

    void calcular() {
        normalize();
        exp = new Expression(cuentaActual);
        double res = exp.calculate();
        if (exp.checkSyntax() && !(Double.isInfinite(res) || Double.isNaN(res))) {
            resultado.setText(String.valueOf(new BigDecimal(res, MathContext.DECIMAL32)));
        } else if (Double.isInfinite(res)) {
            resultado.setText("Infinito");
        } else {
            resultado.setText("NaN");
        }
    }

    void normalize() {
        cuentaActual = cuenta.getText().toString();
        cuentaActual = cuentaActual.replaceAll(getResources().getString(R.string.num_pad_raiz), "sqrt");
        cuentaActual = cuentaActual.replaceAll("[+]+", "+").replaceAll("[*]+", "*").replaceAll("[/]+", "/").replaceAll("[-]+", "-");
    }

}