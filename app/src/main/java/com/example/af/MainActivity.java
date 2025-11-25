package com.example.af;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recycler;
    ArrayList<Medicine> lista = new ArrayList<>();
    MedicineAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        criarCanalNotificacao();

        db = FirebaseFirestore.getInstance();

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MedicineAdapter(lista);

        adapter.setOnItemClickListener(m -> {
            Intent i = new Intent(MainActivity.this, AddEditMedicineActivity.class);
            i.putExtra("id", m.getId());
            i.putExtra("nome", m.getNome());
            i.putExtra("descricao", m.getDescricao());
            i.putExtra("horario", m.getHorario());
            startActivity(i);
        });

        recycler.setAdapter(adapter);

        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditMedicineActivity.class))
        );

        Button btnApi = findViewById(R.id.btnApiDragonBall);
        btnApi.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, DragonBallActivity.class);
            startActivity(i);
        });
    }

    private void criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    "meds",
                    getString(R.string.canal_meds_nome),
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarLista();
    }

    private void carregarLista() {
        db.collection("medicamentos").get()
                .addOnSuccessListener(query -> {
                    lista.clear();
                    for (var doc : query.getDocuments()) {
                        lista.add(doc.toObject(Medicine.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

}