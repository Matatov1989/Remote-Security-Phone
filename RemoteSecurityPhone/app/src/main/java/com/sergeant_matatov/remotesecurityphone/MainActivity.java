package com.sergeant_matatov.remotesecurityphone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //   final String LOG_TAG = "myLogs";

    DrawerLayout drawer;
    Dialog dialog;

    RadioGroup groupRadQuestion;
    RadioGroup groupRadCommands;

    RadioButton radSoundOn;
    RadioButton radGPSOn;
    RadioButton radNotifOn;
    RadioButton radBattery;
    RadioButton radNotifOff;
    RadioButton radMyQuestion;

    Button btnRandPass;
    Button btnSavePass;
    Button btnCancelPass;
    Button btnSettingsGPS;
    Button btnOK;
    Button btnCancelQuestion;
    Button btnSaveQuestion;

    EditText editPass;
    EditText editPassContact;
    EditText editPhoneContact;
    EditText editAnswer;
    TextView textPass;
    TextView textFriend;
    TextView textQuestion;
    TextView textPrivacyPolicy;

    static String checkPass = "";
    static String checkContact = "";
    static String command = "";
    static int checkComm;

    MenuItem localNew;

    String question = "";

    SendSMS sms;

    SharedPreferences sinSIMPref;    //для sim
    final String SAVED_SIM = "saved_sim";

    SharedPreferences passPref;    //для пароля
    final String SAVED_PASS = "saved_pass";

    SharedPreferences contactPref;    //для контакта
    final String SAVED_CONTACT = "saved_contact";

    SharedPreferences questionPref;    //для вопроса
    final String SAVED_QUESTION = "saved_question";

    SharedPreferences answerPref;    //для ответа
    final String SAVED_ANSWER = "saved_answer";

    SharedPreferences flagLocalPref;    //сохраняет flag local (new/old)
    final String SAVED_FLAG_LOCAL = "saved_local_new";

    public static final int MULTIPLE_PERMISSIONS = 2; // code you want.

    String[] permissions = new String[]{
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //       Log.d(LOG_TAG, "DrawerLayout " + textPass.getText().toString());
        checkPermissions();
        //запуск стартовой инструкции
        if (loadPass().toString().isEmpty() && loadContact().toString().isEmpty() && loadQuestion().toString().isEmpty()) {
            //открываем dialog Pass
            dialogStartInstructions();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        textPass = (TextView) header.findViewById(R.id.textPass);
        textFriend = (TextView) header.findViewById(R.id.textFriend);
        textQuestion = (TextView) header.findViewById(R.id.textQuestion);

        editPassContact = (EditText) findViewById(R.id.editPassContact);
        editPhoneContact = (EditText) findViewById(R.id.editPhoneContact);

        groupRadCommands = (RadioGroup) findViewById(R.id.groupRadCommands);
        radSoundOn = (RadioButton) findViewById(R.id.radSoundOn);
        radGPSOn = (RadioButton) findViewById(R.id.radGPSOn);
        radNotifOn = (RadioButton) findViewById(R.id.radNotifOn);
        radBattery = (RadioButton) findViewById(R.id.radBattery);
        radNotifOff = (RadioButton) findViewById(R.id.radNotifOff);
        radMyQuestion = (RadioButton) findViewById(R.id.radMyQuestion);

        //выыод инфы в navigation
        if (!loadPass().toString().isEmpty())
            textPass.append(" " + loadPass().toString());

        if (!loadContact().toString().isEmpty())
            textFriend.append(" " + loadContact().toString());

        if (!loadQuestion().toString().isEmpty())
            textQuestion.setText(getString(R.string.question) + " " + getString(R.string.strSingNum) + "" + Integer.parseInt(loadQuestion().replaceAll("[\\D]", "")));

        if (!checkPass.toString().isEmpty())
            editPassContact.setText(checkPass.toString());

        if (!checkContact.toString().isEmpty())
            editPhoneContact.setText(checkContact.toString());

        Intent intent = getIntent();
        if (intent.hasExtra("contact")) {
            String contact = intent.getStringExtra("contact");

            String[] arrTemp = contact.split(" ");
            int len = arrTemp.length;
            editPhoneContact.setText(arrTemp[len - 1]);
        }

        if (checkComm != -1) {
            switch (checkComm) {
                case R.id.radSoundOn:
                    radSoundOn.setChecked(true);
                    break;
                case R.id.radGPSOn:
                    radGPSOn.setChecked(true);
                    break;
                case R.id.radNotifOn:
                    radNotifOn.setChecked(true);
                    break;
                case R.id.radBattery:
                    radBattery.setChecked(true);
                    break;
                case R.id.radNotifOff:
                    radNotifOff.setChecked(true);
                    break;
                case R.id.radMyQuestion:
                    radMyQuestion.setChecked(true);
                    break;
                case -1:
                    command = "";
                    break;
            }
        }
        //выбор команды через radio
        groupRadCommands.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                editPassContact.setEnabled(true);
                editPassContact.setCursorVisible(true);
                switch (checkedId) {
                    case R.id.radSoundOn:
                        command = "onSound-";
                        break;
                    case R.id.radGPSOn:
                        command = "onGPS-";
                        break;
                    case R.id.radNotifOn:
                        command = "onNotif-";
                        break;
                    case R.id.radBattery:
                        command = "onBattery-";
                        break;
                    case R.id.radNotifOff:
                        command = "offNotif-";
                        break;
                    case R.id.radMyQuestion:
                        command = "My Question-";
                        editPassContact.setEnabled(false);
                        editPassContact.setCursorVisible(false);
                        break;
                    case -1:
                        command = "";
                        break;
                }
                checkComm = checkedId;
            }
        });
    }
    //кнопка контактов
    public void onClickBook(View v) {
        if (!editPassContact.getText().toString().isEmpty())
            checkPass = editPassContact.getText().toString();   //сохранили вписаный пароль

        if (!editPhoneContact.getText().toString().isEmpty())
            checkContact = editPhoneContact.getText().toString();   //сохранили выбраный контакт (номер телефона)

        startActivity(new Intent(this, Book.class).putExtra("flagTab", "send"));
    }
    //кнопка отправки контактов
    public void onClickSend(View v) {
        checkCommand();
    }

    public void checkCommand() {
        String pass = editPassContact.getText().toString();

        String phone = editPhoneContact.getText().toString();

        //подтверждение даных
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getResources().getString(R.string.error_intro));

        if (groupRadCommands.getCheckedRadioButtonId() == R.id.radMyQuestion) {
            //проверка на ввод номера
            if (1 > phone.length()) {
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_phoneNo));
                Toast.makeText(MainActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
            } else {
                sms = new SendSMS();
                sms.sendSMS(getBaseContext(), phone, command);
                checkSendSMS();
            }
        } else {
            //проверка на ввод номера
            if (1 > phone.length()) {
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_phoneNo));
            }
            //проверка на ввод пароля
            if (1 > pass.length()) {
                if (validationError) {
                    validationErrorMessage.append(getResources().getString(R.string.error_join));
                }
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
            }
            //проверка на быбор команды
            if (command == null || command.toString().equals("")) {
                if (validationError) {
                    validationErrorMessage.append(getResources().getString(R.string.error_join));
                }
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_command));
            }
            validationErrorMessage.append(getResources().getString(R.string.error_end));
            // Если есть ошибка проверки, отображения ошибку
            if (validationError && (1 > pass.length() || 1 > phone.length() || 2 > command.length()))
                Toast.makeText(this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
            else {
                sms = new SendSMS();
                sms.sendSMS(getBaseContext(), phone, pass + "-" + command);
                checkSendSMS();
            }
        }
    }
    //проверка на доставку sms
    public void checkSendSMS() {
        //сообщения когда SMS отправлено
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(MainActivity.this, R.string.checkSend,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(MainActivity.this, R.string.checkGeneric,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(MainActivity.this, R.string.checkService,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(MainActivity.this, R.string.checkPDU,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(MainActivity.this, R.string.checkRadio, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT"));
        //сообщения когда SMS доставлено
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(MainActivity.this, R.string.checkDeliveredOn,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(MainActivity.this, R.string.checkDeliveredOff,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED"));

        //очистка полей
        editPhoneContact.setText("");
        editPassContact.setText("");

        checkPass = "";
        checkContact = "";
        command = "";

        //очистка чека
        groupRadCommands.clearCheck();
    }

    //диплог для пароля
    public void dialogPassword() {
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View view = adbInflater.inflate(R.layout.dialog_security_pass, null);

        btnRandPass = (Button) view.findViewById(R.id.btnRandPass);
        btnSavePass = (Button) view.findViewById(R.id.btnSave);
        btnCancelPass = (Button) view.findViewById(R.id.btnCancel);
        editPass = (EditText) view.findViewById(R.id.editPass);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(true);
        adb.setTitle(getString(R.string.titleNewPass));
        adb.setIcon(android.R.drawable.ic_secure);
        adb.setView(view);
        dialog = adb.show();

        //кнопка случайного пароля
        btnRandPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                editPass.setText(String.valueOf(random.nextInt(999999) + 1000));
            }
        });

        //кнопка перенести клиента
        btnSavePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPass.length() >= 4 && editPass.length() <= 8) {
                    if ("".equals(editPass.getText().toString()))
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.hintPass), Toast.LENGTH_SHORT).show();
                    else {
                        //             savePass(editPass.getText().toString());
                        savePass(editPass.getText().toString());
                        textPass.setText(getString(R.string.passText) + " " + editPass.getText().toString());
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, getText(R.string.toastSavePass), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toastIfPass), Toast.LENGTH_SHORT).show();
            }
        });

        //кнопка отмены
        btnCancelPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadPass().toString().isEmpty())
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.btnCancelePass), Toast.LENGTH_SHORT).show();
                else
                    dialog.dismiss();
            }
        });
    }

    //диалог страртовой инструкции
    public void dialogStartInstructions() {
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View view = adbInflater.inflate(R.layout.dialog_start_instructions, null);

        btnOK = (Button) view.findViewById(R.id.btnOK);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(true);
        adb.setTitle(getString(R.string.dialogTitleInst));
        adb.setIcon(R.drawable.ic_instr);
        adb.setView(view);
        dialog = adb.show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //узнаем сирийник симки
                TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String numSIM = telephonyMgr.getSimSerialNumber();

                //       saveNumSIM(numSIM);
                saveNumSIM(numSIM);
                dialog.dismiss();
            }
        });
    }

    //диалог главной инструкции
    public void dialogGeneralInstructions() {
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View view = adbInflater.inflate(R.layout.dialog_general_instruction, null);

        btnSettingsGPS = (Button) view.findViewById(R.id.btnSettingsGPS);
        btnOK = (Button) view.findViewById(R.id.btnOK);
        textPrivacyPolicy = (TextView) view.findViewById(R.id.textPrivacyPolicy);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(true);
        adb.setTitle(getString(R.string.titleGeneralInstr));
        adb.setIcon(R.drawable.ic_instr);
        adb.setView(view);
        dialog = adb.show();

        btnSettingsGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        textPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPrivacyPolicy.setTextColor(Color.RED);
                startActivity(new Intent(MainActivity.this, PrivacyPolicy.class));
                dialog.dismiss();
            }
        });
    }

    //диалог разрабов (лист)
    public void dialogDevelopers() {
        final String[] developers = {getString(R.string.develop1), getString(R.string.develop2), getString(R.string.painter)};

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.developers));
        adb.setIcon(android.R.drawable.ic_dialog_info);
        adb.setItems(developers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                switch (item) {
                    case 0:
                        intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=HTIG"));
                        startActivity(intent);
                        dialog.dismiss();
                        break;
                    case 1:
                        intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Yurka+Sergeant+Matatov"));
                        startActivity(intent);
                        dialog.dismiss();
                        break;
                    case 2:
                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("plain/text");
                        // Кому
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Docmat63@gmail.com"});
                        // тема
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Remote Secyrity Phone");
                        // отправка!
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.toastSendMail)));
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog = adb.show();
    }

    //диалог контрольного вопроса
    public void dialogQuestion() {
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View view = adbInflater.inflate(R.layout.dialog_question, null);

        btnSaveQuestion = (Button) view.findViewById(R.id.btnSave);
        btnCancelQuestion = (Button) view.findViewById(R.id.btnCancel);
        editAnswer = (EditText) view.findViewById(R.id.editAnswer);
        groupRadQuestion = (RadioGroup) view.findViewById(R.id.groupRadQuestion);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(true);
        adb.setTitle(getString(R.string.questionText));
        adb.setIcon(android.R.drawable.ic_menu_help);
        adb.setView(view);
        dialog = adb.show();

        //выбор команды через radio
        groupRadQuestion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioQuestion1:
                        question = getString(R.string.question1);
                        break;
                    case R.id.radioQuestion2:
                        question = getString(R.string.question2);
                        break;
                    case R.id.radioQuestion3:
                        question = getString(R.string.question3);
                        break;
                    case R.id.radioQuestion4:
                        question = getString(R.string.question4);
                        break;
                }
            }
        });

        btnSaveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!question.toString().isEmpty() && !editAnswer.getText().toString().isEmpty()) {
                    saveQuestion(question);
                    saveAnswer(editAnswer.getText().toString());
                    groupRadQuestion.clearCheck();
                    textQuestion.setText(getString(R.string.question) + " " + getString(R.string.strSingNum) + "" + Integer.parseInt(question.replaceAll("[\\D]", "")));
                    editAnswer.setText("");
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, getText(R.string.toastQuestionAnswer), Toast.LENGTH_SHORT).show();
                } else if (question.toString().isEmpty() && editAnswer.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, getText(R.string.toastEnterQuestionAnswer), Toast.LENGTH_SHORT).show();
                } else if (!question.toString().isEmpty() && editAnswer.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, getText(R.string.toastEnterAnswer), Toast.LENGTH_SHORT).show();
                } else if (question.toString().isEmpty() && !editAnswer.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, getText(R.string.toastEnterQuestion), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //сохраняет пароль
    public void savePass(String pass) {
        passPref = getSharedPreferences("My Password", MODE_PRIVATE);
        SharedPreferences.Editor ed = passPref.edit();
        ed.putString(SAVED_PASS, pass);
        ed.commit();
    }

    //загрузка пароля
    public String loadPass() {
        passPref = getSharedPreferences("My Password", MODE_PRIVATE);
        return passPref.getString(SAVED_PASS, "");
    }

    //сохраняет вопрос
    public void saveQuestion(String question) {
        questionPref = getSharedPreferences("My Question", MODE_PRIVATE);
        SharedPreferences.Editor ed = questionPref.edit();
        ed.putString(SAVED_QUESTION, question + ".");
        ed.commit();
    }

    //загружает вопрос
    public String loadQuestion() {
        questionPref = getSharedPreferences("My Question", MODE_PRIVATE);
        String question = questionPref.getString(SAVED_QUESTION, "");
        return question;
    }

    //сохраняет ответ
    public void saveAnswer(String answer) {
        answerPref = getSharedPreferences("My Answer", MODE_PRIVATE);
        SharedPreferences.Editor ed = answerPref.edit();
        ed.putString(SAVED_ANSWER, "(" + answer + ")");
        ed.commit();
    }

    //сохраняем flag новой локации
    public void saveCheckLocal() {
        flagLocalPref = getSharedPreferences(SAVED_FLAG_LOCAL, 0);
        SharedPreferences.Editor editor = flagLocalPref.edit();
        editor.putBoolean("localFlag", false);
        editor.commit();
    }

    //загружает локацию (новая/старая)
    public Boolean loadCheckLocal() {
        flagLocalPref = getSharedPreferences(SAVED_FLAG_LOCAL, 0);
        return flagLocalPref.getBoolean("localFlag", false);
    }

    //сохраняет сирийный номер сим карты
    public void saveNumSIM(String numSIM) {
        sinSIMPref = getSharedPreferences("My sim", MODE_PRIVATE);
        SharedPreferences.Editor ed = sinSIMPref.edit();
        ed.putString(SAVED_SIM, numSIM);
        ed.commit();
    }

    //загружает контакт
    public String loadContact() {
        contactPref = getSharedPreferences("My Contact", MODE_PRIVATE);
        String contact = contactPref.getString(SAVED_CONTACT, "");
        return contact;
    }

    //посоветовать другу
    public void sendAdviseFriend() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.sergeant_matatov.remotesecurityphone&hl");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            moveTaskToBack(true);
            exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (loadCheckLocal()) {
            localNew = menu.findItem(R.id.action_local);
            localNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            saveCheckLocal();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_local) {
            startActivity(new Intent(MainActivity.this, Maps.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_password) {
            dialogPassword();
        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(this, Book.class).putExtra("flagTab", "security"));
        } else if (id == R.id.nav_question) {
            dialogQuestion();
        } else if (id == R.id.nav_location) {
            startActivity(new Intent(MainActivity.this, Maps.class));
        } else if (id == R.id.nav_instr) {
            dialogGeneralInstructions();
        } else if (id == R.id.nav_advise_friend) {
            sendAdviseFriend();
        } else if (id == R.id.nav_developers) {
            dialogDevelopers();
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                    showDialogOK(getString(R.string.dialogPermissionContact), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkPermissions();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    // proceed with logic by disabling the related features or quit the app.
                                    break;
                            }
                        }
                    });
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {
                    showDialogOK(getString(R.string.dialogPermissionSMS), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkPermissions();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    // proceed with logic by disabling the related features or quit the app.
                                    break;
                            }
                        }
                    });
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    showDialogOK(getString(R.string.dialogPermissionCall), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkPermissions();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    // proceed with logic by disabling the related features or quit the app.
                                    break;
                            }
                        }
                    });
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showDialogOK(getString(R.string.dialogPermissionLocation), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkPermissions();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    // proceed with logic by disabling the related features or quit the app.
                                    break;
                            }
                        }
                    });
                }
                return;
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.btnOK), okListener)
                .setNegativeButton(getString(R.string.btnCancel), okListener)
                .create()
                .show();
    }
}