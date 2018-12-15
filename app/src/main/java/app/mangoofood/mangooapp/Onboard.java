package app.mangoofood.mangooapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import app.mangoofood.mangooapp.ViewHolder.SliderAdapter;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Onboard extends AppCompatActivity {

    private ViewPager slideViewPager;
    private LinearLayout dotsLayout;

    private SliderAdapter sliderAdapter;

    private TextView[] mDots;

    private FButton btnPrev,btnNext;

    private int curPage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Product-Sans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_onboard);

        slideViewPager = (ViewPager)findViewById(R.id.slideViewPager);
        dotsLayout = (LinearLayout)findViewById(R.id.dotsLayout);

        btnNext = (FButton)findViewById(R.id.btnNext);
        btnPrev = (FButton)findViewById(R.id.btnPrev);

        sliderAdapter = new SliderAdapter(this);

        slideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        slideViewPager.addOnPageChangeListener(viewListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (curPage == mDots.length - 1)
                {
                    Intent intent = new Intent(Onboard.this,MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    slideViewPager.setCurrentItem(curPage + 1);
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideViewPager.setCurrentItem(curPage - 1);
            }
        });

    }

    public void addDotsIndicator(int pos)
    {
        mDots = new TextView[3];
        dotsLayout.removeAllViews();

        for (int i=0;i<mDots.length;i++)
        {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.lightred));
            dotsLayout.addView(mDots[i]);
        }

        if (mDots.length > 0)
        {
            mDots[pos].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);

            curPage = i;

            if (i == 0)
            {
                btnNext.setEnabled(true);
                btnPrev.setEnabled(false);
                btnPrev.setVisibility(View.INVISIBLE);

            }

            else if (i == mDots.length-1)
            {
                btnNext.setEnabled(true);
                btnPrev.setEnabled(true);
                btnNext.setVisibility(View.VISIBLE);
                btnNext.setText("Finish");
                btnPrev.setText("Prev");
            }
            else
            {
                btnNext.setEnabled(true);
                btnPrev.setEnabled(true);
                btnPrev.setVisibility(View.VISIBLE);
                btnNext.setText("Next");
            }

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

}
