package com.stp.ssm.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.stp.ssm.BuscarPersonaActivity;
import com.stp.ssm.Evt.FinalizoFormEvt;
import com.stp.ssm.Interfaces.OnAddPersonaListener;
import com.stp.ssm.Interfaces.OnSetFamiliaSimpleListener;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.SubFormularioActivity;
import com.stp.ssm.View.LstAddPersonaView;
import com.stp.ssm.View.LstSubFormView;
import com.stp.ssm.View.SeccionView;
import com.stp.ssm.View.ViewFactory;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import com.stp.ssm.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.OnClickListener;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.LayoutParams;
import static android.widget.LinearLayout.VERTICAL;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.lbl_finalizar;
import static com.stp.ssm.View.ViewFactory.crearListAddPersona;
import static com.stp.ssm.View.ViewFactory.crearListSubFormularios;
import static com.stp.ssm.View.ViewFactory.createButton;
import static de.greenrobot.event.EventBus.getDefault;

public class FormularioPageAdapter extends PagerAdapter {

    private Context mContext;
    private FragmentManager fragmentManager;
    private EventBus eventBus;
    private ArrayList<Secciones> secciones;
    private String beneficiario;
    private int codproyecto;

    public FormularioPageAdapter(Context mContext, ArrayList<Secciones> secciones, FragmentManager fragmentManager, String beneficiario, int codproyecto) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        this.secciones = secciones;
        this.beneficiario = beneficiario;
        this.codproyecto = codproyecto;
        eventBus = getDefault();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return secciones.size() + 1;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LinearLayout parent = new LinearLayout(mContext);
        parent.setLayoutParams(new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT));
        parent.setOrientation(VERTICAL);
        if (position == secciones.size()) {
            Button button = createButton(mContext.getString(lbl_finalizar), mContext);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventBus.post(new FinalizoFormEvt());
                }
            });
            parent.addView(button);
        } else {
            final SeccionView seccionView = new SeccionView(mContext, fragmentManager);
            LayoutParams params = new LayoutParams(MATCH_PARENT,
                    MATCH_PARENT);
            seccionView.setLayoutParams(params);
            switch (secciones.get(position).getTipo()) {
                case 1:
                    seccionView.cargarPreguntas(secciones.get(position).getPreguntas(), secciones.get(position).getCondicionsSiguiente(), secciones.get(position).getCodSeccion());
                    parent.addView(seccionView);
                    break;
                case 2:
                    final LstSubFormView lstSubFormView = crearListSubFormularios(mContext);
                    lstSubFormView.setCodigoSeccion(secciones.get(position).getCodSeccion());
                    lstSubFormView.setSubFormularios(secciones.get(position).getSubFormularios());
                    lstSubFormView.cargarDatos();
                    lstSubFormView.setPosition(position);

                    if (secciones.get(position).getTotalizable() == 1) {
                        lstSubFormView.setTotalizable(secciones.get(position));
                    }

                    if (secciones.get(position).getCondicionable() == 1) {
                        lstSubFormView.condicionalVisible();
                    }

                    lstSubFormView.setOnAddPersonaListener(new OnAddPersonaListener() {
                        @Override
                        public void OnAddPersona() {
                            lstSubFormView.swagregar();
                            Intent intent = new Intent(mContext, SubFormularioActivity.class);
                            intent.putExtra("seccion", secciones.get(position));
                            intent.putExtra("posicion", position);
                            intent.putExtra("totales", lstSubFormView.getTotales());
                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    });
                    parent.addView(lstSubFormView);
                    break;
                case 3:
                    LstAddPersonaView lstAddPersonaView = crearListAddPersona(mContext, fragmentManager);
                    lstAddPersonaView.setLisBeneficiario(secciones.get(position).getBeneficiarios(), position);
                    lstAddPersonaView.setOnAddPersonaListener(new OnAddPersonaListener() {
                        @Override
                        public void OnAddPersona() {
                            ArrayList<String> limitced = new ArrayList<String>();
                            limitced.add(beneficiario);
                            if (secciones.get(position).getBeneficiarios() != null) {
                                for (Beneficiario beneficiario : secciones.get(position).getBeneficiarios()) {
                                    limitced.add(beneficiario.getDocumento());
                                }
                            }

                            Intent intent = new Intent(mContext, BuscarPersonaActivity.class);
                            intent.putExtra("posicion", position);
                            intent.putExtra("sw", 1);
                            intent.putExtra("ArrCedulas", limitced);
                            intent.putExtra("codproyecto", codproyecto);

                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    });

                    lstAddPersonaView.setOnSetFamiliaSimpleListener(new OnSetFamiliaSimpleListener() {
                        @Override
                        public void OnSetFamiliaSimple(int valor) {
                            secciones.get(position).setHasfamily(valor);
                        }
                    });
                    parent.addView(lstAddPersonaView);
                    break;
                case 4:
                    seccionView.cargarPreguntas(secciones.get(position).getPreguntas(), secciones.get(position).getCondicionsSiguiente(), secciones.get(position).getCodSeccion());
                    parent.addView(seccionView);
                    break;
            }
        }
        ((ViewPager) container).addView(parent, 0);
        return parent;
    }
}