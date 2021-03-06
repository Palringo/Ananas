package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedHashMap;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.StickerAdapter;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.StickerTypeAdapter;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Matrix3;
import iamutkarshtiwari.github.io.ananas.editimage.view.StickerItem;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity.MODE_NONE;
import static iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity.MODE_STICKERS;

public class StickerFragment extends BaseEditFragment implements StickerAdapter.OnStickerSelection {
    public static final int INDEX = ModuleConfig.INDEX_STICKER;
    public static final String TAG = StickerFragment.class.getName();

    private ViewFlipper flipper;
    private StickerAdapter stickerAdapter;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static StickerFragment newInstance() {
        return new StickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_edit_image_sticker_type, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        flipper = view.findViewById(R.id.flipper);

        RecyclerView typeList = view.findViewById(R.id.stickers_type_list);
        typeList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typeList.setLayoutManager(mLayoutManager);
        typeList.setAdapter(new StickerTypeAdapter(this));

        RecyclerView stickerList = view.findViewById(R.id.stickers_list);
        stickerList.setHasFixedSize(true);
        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(getContext());
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        stickerList.setLayoutManager(stickerListLayoutManager);
        stickerAdapter = new StickerAdapter(this);
        stickerList.setAdapter(stickerAdapter);

        View backToMenu = view.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());

        View backToType = view.findViewById(R.id.back_to_type);
        backToType.setOnClickListener(v -> showPreviousWithAnimation());
    }

    private void showNextWithAnimation() {
        flipper.setInAnimation(this.getContext(), R.anim.in_bottom_to_top);
        flipper.setOutAnimation(this.getContext(), R.anim.out_bottom_to_top);
        flipper.showNext();
    }

    private void showPreviousWithAnimation() {
        flipper.setInAnimation(this.getContext(), R.anim.in_top_to_bottom);
        flipper.setOutAnimation(this.getContext(), R.anim.out_top_to_bottom);
        flipper.showPrevious();
    }

    @Override
    public void onShow() {
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            activity.mode = MODE_STICKERS;
            activity.bannerFlipper.showNext();
            activity.stickerView.setVisibility(View.VISIBLE);
        }
    }

    public void swipToStickerDetails(String path, int stickerCount) {
        stickerAdapter.addStickerImages(path, stickerCount);
        showNextWithAnimation();
    }

    @Override
    public void onStickerSelected(String path) {
        int imageKey = getResources().getIdentifier(path, "drawable",
                getContext().getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageKey);
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null)
            activity.stickerView.addBitImage(bitmap);
    }

    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        flipper = null;
    }

    @Override
    public void backToMain() {
        if (flipper.getDisplayedChild() == 1) {
            showPreviousWithAnimation();
            return;
        }
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            activity.mode = MODE_NONE;
            activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
            activity.stickerView.clear();
            activity.stickerView.setVisibility(View.GONE);
            activity.bannerFlipper.showPrevious();
        }
    }

    public void applyStickers() {
        compositeDisposable.clear();
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            Disposable saveStickerDisposable = applyStickerToImage(activity.getMainBit())
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(subscriber -> {
                        EditImageActivity activityInstance;
                        if ((activityInstance = getActivityInstance()) != null)
                            activityInstance.showLoadingDialog();
                    })
                    .doFinally(() -> {
                        EditImageActivity activityInstance;
                        if ((activityInstance = getActivityInstance()) != null)
                            activityInstance.dismissLoadingDialog();
                    })
                    .subscribe(bitmap -> {
                        if (bitmap == null) {
                            return;
                        }
                        EditImageActivity activityInstance;
                        if ((activityInstance = getActivityInstance()) != null) {
                            activityInstance.stickerView.clear();
                            activityInstance.changeMainBitmap(bitmap, true);
                            activityInstance.stickerFragment.backToMain();
                        }
                    }, e -> {
                        EditImageActivity activityInstance;
                        if ((activityInstance = getActivityInstance()) != null)
                            Toast.makeText(activityInstance,
                                    R.string.iamutkarshtiwari_github_io_ananas_save_error,
                                    Toast.LENGTH_SHORT).show();
                    });


            compositeDisposable.add(saveStickerDisposable);
        }
    }

    private Single<Bitmap> applyStickerToImage(Bitmap mainBitmap) {
        return Single.fromCallable(() -> {
            Bitmap resultBitmap = null;
            EditImageActivity activity;
            if ((activity = getActivityInstance()) != null) {
                Matrix touchMatrix = activity.mainImage.getImageViewMatrix();

                resultBitmap = Bitmap.createBitmap(mainBitmap).copy(
                        Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(resultBitmap);

                float[] data = new float[9];
                touchMatrix.getValues(data);
                Matrix3 cal = new Matrix3(data);
                Matrix3 inverseMatrix = cal.inverseMatrix();
                Matrix m = new Matrix();
                m.setValues(inverseMatrix.getValues());
                handleStickerImage(canvas, m);
            }
            return resultBitmap;
        });
    }

    private void handleStickerImage(Canvas canvas, Matrix m) {
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            LinkedHashMap<Integer, StickerItem> addItems = activity.stickerView.getBank();
            for (Integer id : addItems.keySet()) {
                StickerItem item = addItems.get(id);
                item.matrix.postConcat(m);
                canvas.drawBitmap(item.bitmap, item.matrix, null);
            }
        }
    }

    @Override
    public void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
