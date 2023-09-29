package com.juanfrajberg.oportunidadzapata;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class SendProposalActivity extends AppCompatActivity {

    //TextView que cuenta la cantidad de caracteres de la Descripción
    private TextView maxCharactersDescripcionTextView;

    //Último EditText del layout
    static private EditText nombreUsuarioEditText;

    //Botón para enviar propuesta
    static private Button doneButton;

    //Botones para pasar a las otras ventanas
    static private ImageView homeButton;
    static private ImageView contactButton;

    //Flecha para abrir el spinner de Rubro laboral
    static private ImageView arrowUpRubroLaboralImageView;

    //ScrollView general del formulario
    static private NestedScrollView layoutScrollView;

    //Spinners de la actividad
    static Spinner rubroLaboralSpinner; //Rubro laboral
    static Spinner cursosSpinner; // Curso
    static Spinner divisionSpinner; //División
    static Spinner contactoSpinner; //Tipo de contacto


    //EditText de la actividad
    static private EditText nombreCompletoEditText;
    static private EditText mailEditText;
    static private EditText numeroTelefonoEditText;
    static private EditText profesionEditText;
    static private EditText nombreAlumnoEditText;
    static private EditText descripcionEditText;

    //Botón para adjuntar archivos
    static RelativeLayout adjuntarArchivosButton;

    //CheckBox para elegir si se muestra o no al alumno
    static CheckBox mostrarNombreAlumno;

    //Almacena el último ID de la base de datos
    private String lastID;

    //Variable para saber si mostrar el Dialog al perderse la conexión
    boolean showWiFiStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Código básico para que se muestre la interfaz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendproposal_activity);

        //Establecer el modo claro como predeterminado incluso con el modo oscuro (ya no es necesario, se configuró el modo oscuro en cada layout)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Buscar elementos en layout
        maxCharactersDescripcionTextView = findViewById(R.id.proposal_counterdescription_textview);

        arrowUpRubroLaboralImageView = findViewById(R.id.proposal_arrow_imageview);

        layoutScrollView = findViewById(R.id.proposal_scrolllayout_scrollview);

        rubroLaboralSpinner = (com.juanfrajberg.oportunidadzapata.CustomSpinner) findViewById(R.id.proposal_seleccionrubro_spinner);
        cursosSpinner = findViewById(R.id.proposal_cursos_spinner);
        divisionSpinner = findViewById(R.id.proposal_division_spinner);
        contactoSpinner = findViewById(R.id.proposal_contacto_spinner);

        doneButton = findViewById(R.id.proposal_done_button);
        contactButton = findViewById(R.id.proposal_contact_imageview);
        homeButton = findViewById(R.id.proposal_home_imageview);

        nombreCompletoEditText = findViewById(R.id.proposal_nombrecompleto_edittext);
        mailEditText = findViewById(R.id.proposal_mail_edittext);
        numeroTelefonoEditText = findViewById(R.id.proposal_numero_edittext);
        profesionEditText = findViewById(R.id.proposal_profesion_edittext);
        nombreAlumnoEditText = findViewById(R.id.proposal_alumno_editext);
        descripcionEditText = findViewById(R.id.proposal_descripcion_edittext);
        nombreUsuarioEditText = findViewById(R.id.proposal_contacto_edittext);

        mostrarNombreAlumno = findViewById(R.id.proposal_checkboxalumno_checkbox);

        adjuntarArchivosButton = findViewById(R.id.proposal_archivosview_view);

        //Detectar cuado el texto de la descripción se modifica
        descripcionEditText.addTextChangedListener(descripcionTextEditorWatcher);

        //Detectar cuado el texto de cualquier EditText o Spinner se modifica
        nombreCompletoEditText.addTextChangedListener(nombreCompletoTextEditorWatcher);
        mailEditText.addTextChangedListener(mailTextEditorWatcher);
        numeroTelefonoEditText.addTextChangedListener(numeroTelefonoTextEditorWatcher);
        profesionEditText.addTextChangedListener(profesionTextEditorWatcher);
        nombreAlumnoEditText.addTextChangedListener(nombreAlumnoTextEditorWatcher);
        descripcionEditText.addTextChangedListener(descripcionTextoTextEditorWatcher);
        nombreUsuarioEditText.addTextChangedListener(nombreUsuarioTextEditorWatcher);

        //Para el CheckBox de elegir mostrar el nombre del alumno se usa este código
        mostrarNombreAlumno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor dataPersonRegistered = preferences.edit();
                dataPersonRegistered.putBoolean("showStudentName", isChecked);
                dataPersonRegistered.apply();
            }
        });

        //Dejamos en el formulario los valores que se guardaron por última vez
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        nombreCompletoEditText.setText(preferences.getString("fullname", null));
        mailEditText.setText(preferences.getString("email", null));
        numeroTelefonoEditText.setText(preferences.getString("phone", null));
        profesionEditText.setText(preferences.getString("job", null));
        nombreAlumnoEditText.setText(preferences.getString("student", null));
        descripcionEditText.setText(preferences.getString("description", null));
        nombreUsuarioEditText.setText(preferences.getString("username", null));

        //Para los Spinner se usa un código distinto
        rubroLaboralSpinner.post(new Runnable() {
            @Override
            public void run() {
                rubroLaboralSpinner.setSelection(preferences.getInt("rubro", 0));
            }
        });
        contactoSpinner.post(new Runnable() {
            @Override
            public void run() {
                contactoSpinner.setSelection(preferences.getInt("contactPosition", 0));
            }
        });
        cursosSpinner.post(new Runnable() {
            @Override
            public void run() {
                cursosSpinner.setSelection(preferences.getInt("coursePosition", 0));
            }
        });
        divisionSpinner.post(new Runnable() {
            @Override
            public void run() {
                divisionSpinner.setSelection(preferences.getInt("divisionPosition", 0));
            }
        });

        //Para el CheckBox de elegir mostrar el nombre del alumno
        mostrarNombreAlumno.setChecked(preferences.getBoolean("showStudentName", true));

        //Limpiar el focus cuando se hace clic en el OK del teclado del último EditText, que es
        //el del Nombre de Usuario para Contacto
        nombreUsuarioEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    nombreUsuarioEditText.clearFocus();
                }
                return false;
            }
        });

        //Abrir la actividad Done cuando se hace clic en el botón de Enviar
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Detectar que ningún campo está sin completar o se muestra un Toast
                //El EditText del contacto es opcional, si no se eligió un contacto
                if (TextUtils.isEmpty(nombreCompletoEditText.getText())
                        || TextUtils.isEmpty(mailEditText.getText())
                        || TextUtils.isEmpty(numeroTelefonoEditText.getText())
                        || TextUtils.isEmpty(profesionEditText.getText())
                        || TextUtils.isEmpty(nombreAlumnoEditText.getText())

                        || cursosSpinner.getSelectedItem().toString().equals("Curso")
                        || divisionSpinner.getSelectedItem().toString().equals("División")

                        || (!contactoSpinner.getSelectedItem().toString().equals("Red social") && TextUtils.isEmpty(nombreUsuarioEditText.getText()))
                        || (contactoSpinner.getSelectedItem().toString().equals("Red social") && !TextUtils.isEmpty(nombreUsuarioEditText.getText()))

                        || (!mailEditText.getText().toString().contains(("@")) && !TextUtils.isEmpty(mailEditText.getText()))
                    //|| (numeroTelefonoEditText.getText().length() < 10) Ya no es necesario, limité la cantidad a 10 desde el XML, porque sino
                    // da un error desde Firebase y no se puede subir
                ) {
                    Toast.makeText(getApplicationContext(), "¡Faltan campos por rellenar!",
                            Toast.LENGTH_SHORT).show();

                    //Animación de sacudir para dar más feedback
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(doneButton);
                } else {
                    //Se ejecuta la verificación reCAPTCHA
                    SafetyNet.getClient(SendProposalActivity.this).verifyWithRecaptcha("6Lfji2onAAAAAACfatQPqo8Y6quWKHivTN24JTHq")
                            .addOnSuccessListener(SendProposalActivity.this,
                                    new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                                        @Override
                                        public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                            //La comunicación con reCAPTCHA fue exitosa y se envía la propuesta
                                            sendProposal();
                                        }
                                    })
                            .addOnFailureListener(SendProposalActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    //Se imprime el error y no se puede enviar la propuesta
                                    Toast.makeText(getApplicationContext(), "No se pudo realizar la verificación reCAPTCHA.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        //Deshabilitar el spinner de Rubro Laboral para que solamente se pueda abrir desde la flecha
        rubroLaboralSpinner.setEnabled(false);

        //Hacer que el spinner de Rubro Laboral se abra al hacer clic en la flecha
        arrowUpRubroLaboralImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrowUpRubroLaboralImageView.setRotation(180); //Hacer que la flecha mire hacia abajo
                rubroLaboralSpinner.performClick();
            }
        });

        //Dirigirse a Home al hacer clic en el ícono
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendProposalActivity.this, HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Animación

                //Animación del botón
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(homeButton);
            }
        });

        //Dirigirse a Contact al hacer clic en el ícono
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendProposalActivity.this, ContactActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Animación

                //Animación del botón
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(contactButton);
            }
        });

        //Crear una lista de ítems para el spinner de Rubro Laboral
        String[] itemsRubroLaboral = new String[]{"Salud", "Tecnología", "Finanzas", "Educación", "Ventas", "Ingeniería", "Recursos humanos", "Servicios",
                "Asesoría legal", "Artes y entretenimiento", "Otros"};

        //Crear un adaptador para describir cómo se muestran estos ítems
        ArrayAdapter<String> adapterRubroLaboral = new ArrayAdapter<>(this, R.layout.proposal_spinners_item, itemsRubroLaboral);
        adapterRubroLaboral.setDropDownViewResource(R.layout.proposal_spinners_dropdown);

        //set the spinners adapter to the previously created one.
        //"Setear" el adaptador que creamos al spinner de Rubro Laboral
        rubroLaboralSpinner.setAdapter(adapterRubroLaboral);

        //Repetimos el proceso para los cursos y las divisiones

        //Cursos
        ArrayAdapter<String> cursosArrayAdapter = new ArrayAdapter<String>(this, R.layout.proposal_hintspinners_item, getResources().getStringArray(R.array.cursos_array)) {
            @Override
            public boolean isEnabled(int position) {
                //Deshabilitamos el primer elemento y lo dejamos como un hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) { //@NonNull ViewGroup parent
                //Conseguir el número de ítem
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    //Ponemos el hint de color gris
                    textView.setTextColor(Color.GRAY);
                }
                return view;
            }
        };

        //Modificamos el estilo de cómo se verá la lista
        cursosArrayAdapter.setDropDownViewResource(R.layout.proposal_hintspinners_dropdown);

        cursosSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(
                    AdapterView<?> parent, View view,
                    int position, long id) {
                //Línea para obtener el texto del ítem seleccionado por si lo queremos usar
                String selectedItemText = (String) parent.getItemAtPosition(position);

                //Si el usuario cambia la selección por defecto, el primer ítem se
                //deshabilita y se usa como hint
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.GRAY);
                }

                //Guardamos el valor de manera local
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor dataPersonRegistered = preferences.edit();
                dataPersonRegistered.putInt("coursePosition", position);
                dataPersonRegistered.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Aplicamos todos los cambios en el spinner
        cursosSpinner.setAdapter(cursosArrayAdapter);


        //Repetimos el proceso para las divisiones
        ArrayAdapter<String> divisionesArrayAdapter = new ArrayAdapter<String>(this, R.layout.proposal_hintspinners_item,
                getResources().getStringArray(R.array.divisiones_array)) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) { //@NonNull ViewGroup parent
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                }
                return view;
            }
        };

        divisionesArrayAdapter.setDropDownViewResource(R.layout.proposal_hintspinners_dropdown);

        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.GRAY);
                }

                //Guardamos el valor de manera local
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor dataPersonRegistered = preferences.edit();
                dataPersonRegistered.putInt("divisionPosition", position);
                dataPersonRegistered.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        divisionSpinner.setAdapter(divisionesArrayAdapter);


        ArrayAdapter<String> contactoArrayAdapter = new ArrayAdapter<String>(this, R.layout.proposal_hintspinners_item, getResources().getStringArray(R.array.redes_array)) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) { //@NonNull ViewGroup parent
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                }
                return view;
            }
        };

        // Y repetimos el proceso para los tipos de contacto
        contactoArrayAdapter.setDropDownViewResource(R.layout.proposal_hintspinners_dropdown);

        contactoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.GRAY);
                }

                //Guardamos el valor de manera local
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor dataPersonRegistered = preferences.edit();
                dataPersonRegistered.putInt("contactPosition", position);
                dataPersonRegistered.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        contactoSpinner.setAdapter(contactoArrayAdapter);


        rubroLaboralSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Guardamos el valor de manera local
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor dataPersonRegistered = preferences.edit();
                dataPersonRegistered.putInt("rubro", position);
                dataPersonRegistered.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        //Hacemos que se pierda el focus al hacer clic en OK del teclado
        //Este es el último EditText del layout
        nombreUsuarioEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    nombreUsuarioEditText.clearFocus();
                }
                return false;
            }
        });

        //Lo mismo se hace con el número de teléfono, si no el teclado salta al Spinner de Rubro
        numeroTelefonoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    numeroTelefonoEditText.clearFocus();
                }
                return false;
            }
        });

        //Abrir la galería (luego será elegir archivos obviamente, es una prueba)
        adjuntarArchivosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "content://media/internal/images/media"));
                startActivity(intent);
            }
        });


        //Código para activar el scroll horizontal y vertical dentro del ScrollView
        //Hay casos en los que no hace falta configurarlo, por eso solamente se mencionan algunos EditText

        //Se llama a las Scroll View donde están ubicados los EditText
        HorizontalScrollView alumnoHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.proposal_alumnolayout_horizontalscrollview);
        HorizontalScrollView contactoHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.proposal_contacto_scrollview);
        //*LayoutScrollView ya está definida al comienzo del código

        //Se configura que al hacer clic en estos EditText no se tome en cuenta los otros ScrollView
        nombreAlumnoEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                alumnoHorizontalScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        nombreUsuarioEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                contactoHorizontalScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        descripcionEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                layoutScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        numeroTelefonoEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                layoutScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //Esconder el teclado cuando cambia el focus
        //Hacerlo de esta forma es lo más óptimo porque permite que me desplace por el ScrollView
        nombreUsuarioEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        nombreCompletoEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        mailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        numeroTelefonoEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        profesionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        nombreAlumnoEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        descripcionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    //Función para detectar cuando el texto de la descripción cambia
    private final TextWatcher descripcionTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            maxCharactersDescripcionTextView.setText(String.valueOf(s.length()) + "/450");
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher nombreCompletoTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor dataPersonRegistered = preferences.edit();
            dataPersonRegistered.putString("fullname", nombreCompletoEditText.getText().toString());
            dataPersonRegistered.apply();
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher mailTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor dataPersonRegistered = preferences.edit();
            dataPersonRegistered.putString("email", mailEditText.getText().toString());
            dataPersonRegistered.apply();
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher numeroTelefonoTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor dataPersonRegistered = preferences.edit();
            dataPersonRegistered.putString("phone", numeroTelefonoEditText.getText().toString());
            dataPersonRegistered.apply();
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher profesionTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor dataPersonRegistered = preferences.edit();
            dataPersonRegistered.putString("job", profesionEditText.getText().toString());
            dataPersonRegistered.apply();
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher nombreAlumnoTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor dataPersonRegistered = preferences.edit();
            dataPersonRegistered.putString("student", nombreAlumnoEditText.getText().toString());
            dataPersonRegistered.apply();
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher descripcionTextoTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor dataPersonRegistered = preferences.edit();
            dataPersonRegistered.putString("description", descripcionEditText.getText().toString());
            dataPersonRegistered.apply();
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher nombreUsuarioTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor dataPersonRegistered = preferences.edit();
            dataPersonRegistered.putString("username", nombreUsuarioEditText.getText().toString());
            dataPersonRegistered.apply();
        }

        public void afterTextChanged(Editable s) {
        }
    };

    //Para que haya una animación cuando se presiona el botón atrás, y no sea la que aparece
    //por defecto
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //Todo este código es para implementar el cartel de la conectividad
    @Override
    protected void onResume() {
        //Prueba para detectar si hay conexión, no si está activo o no el WiFi, como está programado
        //Toast.makeText(getApplicationContext(), "Conectividad: " + isOnline() + ".", Toast.LENGTH_SHORT).show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //Parte del código para detectar si el Wi-Fi se ha conectado o desconectado, acá se ejecuta el Receiver cuando el estado del Wi-Fi cambia
        //Este valor se actualiza en WiFiActivity cuando se marca el CheckBox de no "Volver a mostrar"
        showWiFiStatus = preferences.getBoolean("showWiFiStatus", true);

        Handler waitUntilActivityIsLoaded = new Handler();
        waitUntilActivityIsLoaded.postDelayed(new Runnable() {
            public void run() {
                //IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
                IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                registerReceiver(wifiStateReceiver, intentFilter);
            }
        }, 750); //El tiempo que se demora la transición de la actividad

        //Toast.makeText(getApplicationContext(), "Estado final: " + showWiFiStatus + ".", Toast.LENGTH_SHORT).show(); //A modo de prueba

        super.onResume();
    }

    //Parte del código para detectar si el Wi-Fi se ha conectado o desconectado, acá se cancela el Receiver del Wi-Fi una vez que la actividad pasa al fondo
    @Override
    protected void onStop() {
        try {
            unregisterReceiver(wifiStateReceiver);
        } catch (Exception e) {
        } //Por algún motivo, a veces falla, entonces es mejor usar un try
        super.onStop();
    }

    //Parte del código para detectar si el Wi-Fi se ha conectado o desconectado, este es el Receiver que detecta si el Wifi se conectó o desconectó
    public BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Este método verifica si hay conexión a Internet, no se si activó o desactivó el Wi-Fi
            Intent i; //Se usa en ambos casos, por lo que lo defino acá
            //Lo mismo ocurre con estas dos líneas
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            //Si hay conectividad se abre la ventana de que tenemos Internet, y viceversa
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                //Toast.makeText(getApplicationContext(), "Hay conexión", Toast.LENGTH_SHORT).show();
                if (!WiFiInformation.isActivityVisible() && !WiFiInformation.lastState.equals("ON")) { //&& WiFiInformation.isShowAgain()
                    WiFiInformation.lastState = "ON";
                    i = new Intent(SendProposalActivity.this, WiFiActivity.class);
                    i.putExtra("State", "ON");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            } else {
                //Toast.makeText(getApplicationContext(), "No hay conexión", Toast.LENGTH_SHORT).show();
                if (!WiFiInformation.isActivityVisible() && !WiFiInformation.lastState.equals("OFF") && WiFiInformation.isShowAgain() && showWiFiStatus) {
                    WiFiInformation.lastState = "OFF";
                    i = new Intent(SendProposalActivity.this, WiFiActivity.class);
                    i.putExtra("State", "OFF");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            }
        }
    };

    //Función de prueba para detectar si hay conexión, no si está activo o no el WiFi, como está programado
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    //Esta función se llama desde WiFiActivity, para que cuando aparezca el Dialog no se pueda hacer clic en los elementos del layout
    public static void setElementsLayoutClickeable(boolean state) {
        nombreUsuarioEditText.setClickable(state);
        doneButton.setClickable(state);
        homeButton.setClickable(state);
        contactButton.setClickable(state);
        arrowUpRubroLaboralImageView.setClickable(state);
        layoutScrollView.setClickable(state);
        rubroLaboralSpinner.setClickable(state);
        cursosSpinner.setClickable(state);
        divisionSpinner.setClickable(state);
        contactoSpinner.setClickable(state);
        nombreCompletoEditText.setClickable(state);
        mailEditText.setClickable(state);
        numeroTelefonoEditText.setClickable(state);
        profesionEditText.setClickable(state);
        nombreAlumnoEditText.setClickable(state);
        descripcionEditText.setClickable(state);
        adjuntarArchivosButton.setClickable(state);
        mostrarNombreAlumno.setClickable(state);
    }

    public static void makeArrowUpRubroLaboralLookUp() {
        arrowUpRubroLaboralImageView.setRotation(0); //Dejamos la flecha como estaba, en su rotación original
    }

    //Función para enviar la propuesta una vez completada
    public void sendProposal() {
        finish(); //Para que al cerrarse DoneActivity, no regrese a esta actividad

        //Se guarda toda la información de la persona que se registró en Shared Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor dataPersonRegistered = preferences.edit();

        dataPersonRegistered.putString("fullname", nombreCompletoEditText.getText().toString());
        dataPersonRegistered.putLong("phoneDB", Long.parseLong(numeroTelefonoEditText.getText().toString()));

        //Data Format es para elegir el formato en el que se guardará la hora
        //Tipos de Data Format
                    /*
                    "yyyy.MM.dd G 'at' HH:mm:ss z" ---- 2001.07.04 AD at 12:08:56 PDT
                    "hh 'o''clock' a, zzzz" ----------- 12 o'clock PM, Pacific Daylight Time
                    "EEE, d MMM yyyy HH:mm:ss Z"------- Wed, 4 Jul 2001 12:08:56 -0700
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ"------- 2001-07-04T12:08:56.235-0700
                    "yyMMddHHmmssZ"-------------------- 010704120856-0700
                    "K:mm a, z" ----------------------- 0:08 PM, PDT
                    "h:mm a" -------------------------- 12:08 PM
                    "EEE, MMM d, ''yy" ---------------- Wed, Jul 4, '01
                     */

        //Así, guardamos la fecha actual en el mismo formato que las del Excel
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3")); //La zona horaria que usamos en Argentina
        String date = dateFormat.format(Calendar.getInstance().getTime());

        dataPersonRegistered.putString("time", date);
        dataPersonRegistered.putString("email", mailEditText.getText().toString());
        dataPersonRegistered.putString("job", profesionEditText.getText().toString());
        dataPersonRegistered.putString("student", nombreAlumnoEditText.getText().toString());
        dataPersonRegistered.putString("course", cursosSpinner.getSelectedItem().toString());
        dataPersonRegistered.putString("division", divisionSpinner.getSelectedItem().toString());
        dataPersonRegistered.putString("description", descripcionEditText.getText().toString());

        //Verificar si hay algo escrito
        if (!contactoSpinner.getSelectedItem().toString().equals("Tipo") && !TextUtils.isEmpty(nombreUsuarioEditText.getText())) {
            dataPersonRegistered.putString("contact", contactoSpinner.getSelectedItem().toString());
            dataPersonRegistered.putString("username", nombreUsuarioEditText.getText().toString());
        }

        //Guardamos todos los cambios hechos
        dataPersonRegistered.apply();

        //Enviamos la información a Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("1JcKn4lV9YC5cF8o_QyekJ7-72u-bRn748CLrLc9jTD0/workers");

        //Función para saber cuál es el último child (hijo) de la base de datos, para que se cree en el número que sigue
        Query lastQuery = databaseReference.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //Nos devuelve el número que se usa como ID
                    lastID = data.getKey();

                    //Se le suma una posición a lastID
                    int lastIDPlusOne = Integer.parseInt(lastID) + 1;
                    lastID = String.valueOf(lastIDPlusOne);
                    //Toast.makeText(getApplicationContext(), lastID, Toast.LENGTH_SHORT).show(); //Para comprobar que funcione

                    //Usamos la clase Worker para guardar todos los datos
                    Worker worker = new Worker(preferences.getString("fullname", "null"),
                            preferences.getLong("phoneDB", 0L),
                            preferences.getString("time", "null"),
                            preferences.getString("email", "null"),
                            preferences.getString("job", "null"),
                            preferences.getString("student", "null"),
                            preferences.getString("course", "null"),
                            preferences.getString("division", "null"),
                            Integer.parseInt(lastID));

                    //Se almacena en nuestra Realtime Database
                    databaseReference.child(String.valueOf(lastID)).setValue(worker);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Iniciamos la Actividad Done
        startActivity(new Intent(SendProposalActivity.this, DoneActivity.class));
    }
}