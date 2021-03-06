package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Utils;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;


public class BrightnessFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_BRIGHTNESS;
    public static final String TAG = BrightnessFragment.class.getName();

    private static final int INITIAL_BRIGHTNESS = 0;

    private SeekBar mSeekBar;
    private AppCompatTextView textPercent;

    public static BrightnessFragment newInstance() {
        return new BrightnessFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_brightness, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSeekBar = view.findViewById(R.id.seekBar);
        mSeekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.LTGRAY,
                PorterDuff.Mode.MULTIPLY));
        textPercent = view.findViewById(R.id.text_median);

        View mBackToMenu = view.findViewById(R.id.back_to_main);
        mBackToMenu.setOnClickListener(new BrightnessFragment.BackToMenuClick());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - (seekBar.getMax() / 2);
                EditImageActivity activity;
                if ((activity = getActivityInstance()) != null)
                    activity.brightnessView.setBright(value / 10f);

                int val = (int) (value / 20);
                textPercent.setText(String.valueOf(val));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        initView();
    }

    @Override
    public void onShow() {
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            activity.mode = EditImageActivity.MODE_BRIGHTNESS;
            activity.mainImage.setImageBitmap(activity.getMainBit());
            activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            activity.mainImage.setVisibility(View.GONE);

            activity.brightnessView.setImageBitmap(activity.getMainBit());
            activity.brightnessView.setVisibility(View.VISIBLE);
            activity.bannerFlipper.showNext();
        }
        initView();
    }

    @Override
    public void backToMain() {
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            activity.mode = EditImageActivity.MODE_NONE;
            activity.bottomGallery.setCurrentItem(0);
            activity.mainImage.setVisibility(View.VISIBLE);
            activity.brightnessView.setVisibility(View.GONE);
            activity.bannerFlipper.showPrevious();
            activity.brightnessView.setBright(INITIAL_BRIGHTNESS);
        }
    }

    public void applyBrightness() {
        if (mSeekBar.getProgress() == mSeekBar.getMax() / 2) {
            backToMain();
            return;
        }
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            Bitmap bitmap = ((BitmapDrawable) activity.brightnessView.getDrawable()).getBitmap();
            activity.changeMainBitmap(Utils.brightBitmap(bitmap,
                    activity.brightnessView.getBright()), true);
        }
        backToMain();
    }

    private void initView() {
        mSeekBar.setProgress(mSeekBar.getMax() / 2);
        textPercent.setText(String.valueOf(0));
    }


    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }
}
