package com.gan.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gan.base.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import base.gan.photopicker.PhotoPickerUitl;
import base.gan.photopicker.PhotoPickerActivity;
import base.gan.photopicker.model.PhotoModel;
import base.gan.photopicker.utils.CommonAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/6/7
 */
public class PhotoPickerDemoActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.btn)
    TextView btn;
    @BindView(R.id.btn2)
    TextView btn2;
    @BindView(R.id.gv)
    GridView gv;

    @Override
    protected int getContentView() {
        return R.layout.activity_photo_demo_picker;
    }

    @Override
    protected void afterView() {
        ButterKnife.bind(this);
        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                PhotoPickerUitl.initMode(false, 5);
                startActivityForResult(new Intent(this, PhotoPickerActivity.class), 200);
                break;
            case R.id.btn2:
                PhotoPickerUitl.initMode(true, 5);
                startActivityForResult(new Intent(this, PhotoPickerActivity.class), 200);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 200) {
                if (data != null) {
                    ArrayList<PhotoModel> list = data.getParcelableArrayListExtra("photos");
                    gv.setAdapter(new CommonAdapter<PhotoModel>(this, list) {
                        @Override
                        public void doItem(ViewHolder holder, PhotoModel t, int postiont) {
                            ImageView iv = holder.getView(R.id.iv_photo);
                            ImageLoader.getInstance().displayImage("file://" + t.getOriginalPath(), iv);
                            holder.setOnClickListener(R.id.iv_photo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                        }

                        @Override
                        public int getItemLayoutResId() {
                            return R.layout.item_phtoto;
                        }
                    });
                }
            }

        }
    }
}
