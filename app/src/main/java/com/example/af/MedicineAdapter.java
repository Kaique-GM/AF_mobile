package com.example.af;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder>{

    private List<Medicine> lista;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Medicine medicine);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MedicineAdapter(List<Medicine> lista) {
        this.lista = lista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);

        CheckBox chk = new CheckBox(parent.getContext());
        chk.setText(parent.getContext().getString(R.string.chk_tomado));
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            LinearLayout wrapper = new LinearLayout(parent.getContext());
            wrapper.setOrientation(LinearLayout.HORIZONTAL);
            wrapper.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            wrapper.addView(v, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            wrapper.addView(chk, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            return new ViewHolder(wrapper, wrapper.findViewById(android.R.id.text1),
                    wrapper.findViewById(android.R.id.text2), chk);
        } else {
            return new ViewHolder(v, v.findViewById(android.R.id.text1),
                    v.findViewById(android.R.id.text2), chk);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        Medicine m = lista.get(pos);

        holder.txt1.setText(m.getNome());
        holder.txt2.setText(
                holder.itemView.getContext().getString(
                        R.string.label_horario,
                        (m.getHorario() == null ? "" : m.getHorario())
                )
        );


        holder.chkTomado.setOnCheckedChangeListener(null);
        holder.chkTomado.setChecked(m.isTomado());

        holder.itemView.setBackgroundColor(m.isTomado() ? 0xFFE8F5E9 : 0xFFFFFFFF);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(m);
        });

        holder.itemView.setOnLongClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("medicamentos")
                    .document(m.getId())
                    .delete()
                    .addOnSuccessListener(a -> {
                        int position = holder.getAdapterPosition();
                        if (position >= 0 && position < lista.size()) {
                            lista.remove(position);
                            notifyItemRemoved(position);
                        }
                        Toast.makeText(v.getContext(), "Medicamento excluído", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Erro ao excluir", Toast.LENGTH_SHORT).show();
                    });

            return true;
        });

        holder.chkTomado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseFirestore.getInstance()
                    .collection("medicamentos")
                    .document(m.getId())
                    .update("tomado", isChecked)
                    .addOnSuccessListener(a -> {
                        m.setTomado(isChecked);
                        holder.itemView.setBackgroundColor(isChecked ? 0xFFE8F5E9 : 0xFFFFFFFF);
                    })
                    .addOnFailureListener(e -> {
                        holder.chkTomado.setChecked(!isChecked);
                        Toast.makeText(buttonView.getContext(), "Erro ao atualizar", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return lista == null ? 0 : lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt1, txt2;
        CheckBox chkTomado;

        public ViewHolder(View itemView, TextView txt1, TextView txt2, CheckBox chkTomado) {
            super(itemView);
            this.txt1 = txt1;
            this.txt2 = txt2;
            this.chkTomado = chkTomado;
        }
    }


}
