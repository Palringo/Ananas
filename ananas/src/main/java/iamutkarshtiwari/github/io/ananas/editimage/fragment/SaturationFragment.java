package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Utils;
import iamutkarshtiwari.github.io.ananas.editimage.view.SaturationView;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;

public class SaturationFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_CONTRAST;
    private static final int INITIAL_SATURATION = 100;
    public static final String TAG = SaturationFragment.class.getName();
    private SeekBar mSeekBar;

    public static SaturationFragment newInstance() {
        return new SaturationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_saturation, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        View mBackToMenu = view.findViewById(R.id.back_to_main);
        mBackToMenu.setOnClickListener(new SaturationFragment.BackToMenuClick());

        mSeekBar = view.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - (seekBar.getMax() / 2);
                EditImageActivity activity;
                if ((activity = getActivityInstance()) != null)
                    activity.saturationView.setSaturation(value / 10f);
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
            activity.mode = EditImageActivity.MODE_SATURATION;
            activity.mainImage.setImageBitmap(activity.getMainBit());
            activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            activity.mainImage.setVisibility(View.GONE);

            activity.saturationView.setImageBitmap(activity.getMainBit());
            activity.saturationView.setVisibility(View.VISIBLE);
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
            activity.saturationView.setVisibility(View.GONE);
            activity.bannerFlipper.showPrevious();
            activity.saturationView.setSaturation(INITIAL_SATURATION);
        }
    }

    public void applySaturation() {
        if (mSeekBar.getProgress() == mSeekBar.getMax()) {
            backToMain();
            return;
        }
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            Bitmap bitmap = ((BitmapDrawable) activity.saturationView.getDrawable()).getBitmap();
            activity.changeMainBitmap(Utils.saturationBitmap(bitmap,
                    activity.saturationView.getSaturation()), true);
        }
        backToMain();
    }

    private void initView() {
        mSeekBar.setProgress(mSeekBar.getMax());
    }

    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }
}
