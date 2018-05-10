package com.stp.ssm.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.stp.ssm.Adapters.ListItemAdapter;
import com.stp.ssm.Interfaces.OnAddItemListener;
import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.R;
import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout.view_lista_dinamica;

public class ListaDinamicaView extends LinearLayout {

    private ListView lstListaDinamica;
    private Button btnAddItem;
    private OnAddItemListener onAddItemListener;
    private OnDeleteListener onDeleteListener;
    private ListItemAdapter adapter;
    private int layoutsize = 200;

    public ListaDinamicaView(Context context) {
        super(context);
        inicializar();
    }


    private void inicializar() {
        String infService = LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(view_lista_dinamica, this, true);

        lstListaDinamica = (ListView) findViewById(id.lstListaDinamica);
        btnAddItem = (Button) findViewById(id.btnAddItem);
        asignarEventos();
    }

    private void asignarEventos() {
        btnAddItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddItemListener.OnAddItem();
            }
        });
    }

    public void setOnAddItemListener(OnAddItemListener onAddItemListener) {
        this.onAddItemListener = onAddItemListener;
    }

    public void setListItems(final ArrayList<String> items) {
        adapter = new ListItemAdapter(getContext(), 0, items);
        lstListaDinamica.setAdapter(adapter);
        adapter.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(int position) {
                items.remove(position);
                adapter.notifyDataSetChanged();
                onDeleteListener.OnDelete(0);
            }
        });
    }

    public int getLayoutsize() {
        return layoutsize;
    }

    public void setLayoutsize(int layoutsize) {
        this.layoutsize = layoutsize;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
