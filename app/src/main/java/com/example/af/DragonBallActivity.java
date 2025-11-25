package com.example.af;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

public class DragonBallActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> lista = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Button btnCarregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dragon_ball);

        listView = findViewById(R.id.listDbz);
        btnCarregar = findViewById(R.id.btnCarregar);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adapter);

        btnCarregar.setOnClickListener(v -> carregarPersonagens());
    }

    private void carregarPersonagens() {
        String url = "https://dragonball-api.com/api/characters";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    lista.clear();

                    try {
                        var array = response.getJSONArray("items");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String nome = obj.getString("name");
                            String raca = obj.getString("race");
                            lista.add(nome + " - " + raca);
                        }

                    } catch (Exception e) {
                        lista.add("Erro ao ler JSON!");
                    }

                    adapter.notifyDataSetChanged();
                },

                error -> lista.add("Erro ao carregar personagens (API offline?)")
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
