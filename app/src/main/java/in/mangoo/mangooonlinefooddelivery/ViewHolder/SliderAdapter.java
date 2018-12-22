package in.mangoo.mangooonlinefooddelivery.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.mangoo.mangooonlinefooddelivery.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.search,
            R.drawable.scooter,
            R.drawable.dining
    };

    public String[] slide_heads = {
            "Find food you love",
            "Fast delivery",
            "Enjoy the experience"
    };

    public String[] slide_desc = {
            "Discover the best menus from a wide variety of restaurants.",
            "Fast delivery to your home or office, we will deliver it. Wherever you are!",
            "Don't feel like going out? No problem, we'll deliver your order in your room."
    };

    @Override
    public int getCount() {
        return slide_heads.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout)o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view =layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImage = (ImageView)view.findViewById(R.id.slide_image);
        TextView slideHead = (TextView) view.findViewById(R.id.slide_head);
        TextView slideDesc = (TextView) view.findViewById(R.id.slide_desc);

        slideImage.setImageResource(slide_images[position]);
        slideHead.setText(slide_heads[position]);
        slideDesc.setText(slide_desc[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
