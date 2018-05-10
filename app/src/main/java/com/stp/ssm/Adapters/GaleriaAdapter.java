package com.stp.ssm.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import android.view.ViewGroup.LayoutParams;

import com.stp.ssm.View.ViewFactory;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.VERTICAL;
import static com.stp.ssm.View.ViewFactory.crearImagenView;

public class GaleriaAdapter extends PagerAdapter {

    private Context context;
    private LinearLayout mContainer;
    private ArrayList<String> imagens;

    public GaleriaAdapter(ArrayList<String> imagens, Context context) {
        this.imagens = imagens;
        this.context = context;
    }


    @Override
    public int getCount() {
        return imagens.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((LinearLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mContainer = new LinearLayout(context);///Nueva Instancia de contenedor
        mContainer.setOrientation(VERTICAL);
        mContainer.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mContainer.addView(crearImagenView(imagens.get(position), context));

        ((ViewPager) container).addView(mContainer, 0);
        return mContainer;
    }
}
