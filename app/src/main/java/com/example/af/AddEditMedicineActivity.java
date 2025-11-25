package com.example.af;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AddEditMedicineActivity extends AppCompatActivity {

    EditText edtNome, edtDesc;
    TimePicker timePicker;
    Button btnSalvar;

    FirebaseFirestore db;

    String docId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_medicine);

        edtNome = findViewById(R.id.edtNome);
        edtDesc = findViewById(R.id.edtDesc);
        timePicker = findViewById(R.id.timePicker);
        btnSalvar = findViewById(R.id.btnSalvar);

        db = FirebaseFirestore.getInstance();

        if (getIntent().hasExtra("id")) {
            docId = getIntent().getStringExtra("id");
            edtNome.setText(getIntent().getStringExtra("nome"));
            edtDesc.setText(getIntent().getStringExtra("descricao"));

            String horario = getIntent().getStringExtra("horario");

            if (horario != null && horario.contains(":")) {
                String[] partes = horario.split(":");
                int hora = Integer.parseInt(partes[0]);
                int minuto = Integer.parseInt(partes[1]);

                timePicker.setHour(hora);
                timePicker.setMinute(minuto);
            }
        }

        btnSalvar.setOnClickListener(v -> salvarMedicamento());
    }

    private void salvarMedicamento() {

        String nome = edtNome.getText().toString();
        String descricao = edtDesc.getText().toString();

        int hora = timePicker.getHour();
        int minuto = timePicker.getMinute();
        String horarioStr = hora + ":" + String.format("%02d", minuto);

        if (docId.isEmpty()) {
            docId = db.collection("medicamentos").document().getId();
        }

        Medicine m = new Medicine(docId, nome, descricao, horarioStr, false);

        db.collection("medicamentos")
                .document(docId)
                .set(m)
                .addOnSuccessListener(a -> {
                    Log.d("ALARM", "Salvar medicamento: " + nome + " horário=" + horarioStr + " id=" + docId);

                    agendarAlarme(m);

                    Log.d("ALARM", "agendarAlarme() chamado para: " + m.getNome() + " horario=" + m.getHorario());

                    Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    public void agendarAlarme(Medicine m) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return; // não agenda até o usuário permitir
            }
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("nome", m.getNome());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                m.getId().hashCode(), // id único
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String[] partes = m.getHorario().split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, minuto);
        c.set(Calendar.SECOND, 0);

        if (c.getTimeInMillis() < System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis(),
                pendingIntent
        );
    }

}
