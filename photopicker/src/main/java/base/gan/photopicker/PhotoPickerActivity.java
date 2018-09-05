package base.gan.photopicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.gan.photopicker.model.AlbumModel;
import base.gan.photopicker.model.PhotoModel;
import base.gan.photopicker.photo.PhotoSelectorDomain;
import base.gan.photopicker.photo.PhotoView;
import base.gan.photopicker.photo.impl.OnLocalAlbumListener;
import base.gan.photopicker.photo.impl.OnLocalReccentListener;
import base.gan.photopicker.utils.AnimationUtil;
import base.gan.photopicker.utils.CommonAdapter;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/5/22
 */
public class PhotoPickerActivity extends BaseActivity {
    public static int COUNT_MAX = 5;
    private TextView tv_title;
    private GridView gv;
    private ListPopupWindow listPopupWindow;
    //目录弹出框的一次最多显示的目录数目
    private View vg_photo_list;
    private MenuItem addMenu;
    private View view_hidden;
    private TextView tv_count;
    private ViewPager viewPager;

    private CommonAdapter<AlbumModel> listAdapter;

    private ArrayList<PhotoModel> selectPhones;
    Map<String, List<PhotoModel>> ablumLists;
    private PhotoSelectorDomain photoSelectorDomain;
    private MenuItem submitMenu;

    @Override
    protected int getContentView() {
        return R.layout.activity_photo_picker;
    }

    @Override
    protected void afterView() {
        setTitle("图片");
        initView();
        initData();
        initGrid();
        initAblum("最近照片");
        initListPopWindow();
        initListenter();
        initMode();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_count = (TextView) findViewById(R.id.tv_count);
        view_hidden = findViewById(R.id.view_hidden);
        View vg_title = findViewById(R.id.vg_title);
        if (PhotoPickerUitl.getBgColorResId() > 0) {
            toolBar.setBackgroundColor(getResources().getColor(PhotoPickerUitl.getBgColorResId()));
            vg_title.setBackgroundColor(getResources().getColor(PhotoPickerUitl.getBgColorResId()));
        }
        if (PhotoPickerUitl.getTxtColorResId() > 0) {
            toolBar.setTitleTextColor(getResources().getColor(PhotoPickerUitl.getTxtColorResId()));
            tv_title.setTextColor(getResources().getColor(PhotoPickerUitl.getTxtColorResId()));
        }
    }

    private void initData() {
        ablumLists = new HashMap<String, List<PhotoModel>>();
        selectPhones = new ArrayList<PhotoModel>();
        photoSelectorDomain = new PhotoSelectorDomain(this);
    }


    private void initGrid() {
        vg_photo_list = findViewById(R.id.vg_photo_list);
        gv = (GridView) findViewById(R.id.gv_photo);
        gv.setAdapter(new CommonAdapter<PhotoModel>(this) {
            @Override
            public void doItem(final ViewHolder holder, final PhotoModel photoModel, final int postion) {
                ImageView iv = holder.getView(R.id.iv_photo);
                if (photoModel.getOriginalPath().equals(iv.getTag())) {
                } else {
                    ImageLoader.getInstance().displayImage("file://" + photoModel.getOriginalPath(), iv);
                    iv.setTag(photoModel.getOriginalPath());
                }
                holder.setOnClickListener(R.id.iv_photo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initViewPager(postion);
                    }
                });

                if (PhotoPickerUitl.isMultiMode()) {
                    holder.setVisible(R.id.ck_photo, true);
                    if (photoModel.isChecked()) {
                        holder.setVisible(R.id.vg_mask, true);
                        holder.setChecked(R.id.ck_photo, true);
                    } else {
                        holder.setVisible(R.id.vg_mask, false);
                        holder.setChecked(R.id.ck_photo, false);
                    }
                    holder.setOnClickListener(R.id.ck_photo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v instanceof CheckBox) {

                                boolean isCheck = ((CheckBox) v).isChecked();
                                if (isCheck) {
                                    if (selectPhones.size() >= PhotoPickerUitl.getMaxSize()) {
                                        ((CheckBox) v).setChecked(false);
                                        return;
                                    }
                                    photoModel.setChecked(true);
                                    selectPhones.add(photoModel);
                                } else {
                                    selectPhones.remove(photoModel);
                                    photoModel.setChecked(false);
                                }
                                notifyDataSetChanged();
                                initMode();
                            }
                        }
                    });
                } else {
                    holder.setVisible(R.id.vg_mask, false);
                    holder.setVisible(R.id.ck_photo, false);
                }

            }

            @Override
            public int getItemLayoutResId() {
                return R.layout.item_phtoto_picker;
            }
        });
    }

    private void initViewPager(int position) {
        if (viewPager == null) {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
        }

        Animation translateAnim1 = AnimationUtil.getTranslateAnimationLeft2Right1(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vg_photo_list.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation translateAnim2 = AnimationUtil.getTranslateAnimationLeft2Right2();
        vg_photo_list.startAnimation(translateAnim1);
        viewPager.startAnimation(translateAnim2);
        viewPager.setVisibility(View.VISIBLE);


        setTitle(PhotoPickerUitl.isMultiMode() ? selectPhones.size() + "/" + PhotoPickerUitl.getMaxSize() : "重新选择");
        addMenu.setVisible(true);
        submitMenu.setVisible(false);
        final List<PhotoModel> list = ((CommonAdapter<PhotoModel>) gv.getAdapter()).getData();
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ViewGroup viewGroup = (ViewGroup) View.inflate(PhotoPickerActivity.this, R.layout.pager_img_picker, null);
                PhotoView mPhotoView = (PhotoView) viewGroup.findViewById(R.id.photo_preview_head_pv);
                mPhotoView.enable();
                String pic = list.get(position).getOriginalPath();
                if (pic.startsWith("http") || pic.startsWith("drawable")) {
                    ImageLoader.getInstance().displayImage(pic, mPhotoView, initNoScaleDisplayOptions(true));
                } else {
                    ImageLoader.getInstance().getDiskCache().remove("file://" + (TextUtils.isEmpty(pic) ? "" : pic.trim()));
                    ImageLoader.getInstance().displayImage("file://" + (TextUtils.isEmpty(pic) ? "" : pic.trim()), mPhotoView, initNoScaleDisplayOptions(true));
                }
                container.addView(viewGroup);
                return viewGroup;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        viewPager.setCurrentItem(position);
    }

    private void initAblum(final String ablum) {
        tv_title.setText(ablum);
        if (ablumLists.containsKey(ablum)) {
            ((CommonAdapter) gv.getAdapter()).setData(ablumLists.get(ablum));
            return;
        }
        if ("最近照片".equals(ablum)) {

            photoSelectorDomain.getReccent(new OnLocalReccentListener() {
                @Override
                public void onPhotoLoaded(List<PhotoModel> list) {
                    ((CommonAdapter) gv.getAdapter()).setData(list);
                    ablumLists.put(ablum, list);
                }
            });
        } else {

            photoSelectorDomain.getAlbum(ablum, new OnLocalReccentListener() {
                @Override
                public void onPhotoLoaded(List<PhotoModel> list) {
                    ((CommonAdapter) gv.getAdapter()).setData(list);
                    ablumLists.put(ablum, list);
                }
            });
        }

    }

    private void initListPopWindow() {
        photoSelectorDomain.updateAlbum(new OnLocalAlbumListener() {
            @Override
            public void onAlbumLoaded(List<AlbumModel> list) {
                listPopupWindow = new ListPopupWindow(PhotoPickerActivity.this);
                listPopupWindow.setWidth(android.support.v7.widget.ListPopupWindow.MATCH_PARENT);
                listPopupWindow.setAnchorView(tv_title);
                listAdapter = new CommonAdapter<AlbumModel>(PhotoPickerActivity.this, list) {
                    @Override
                    public void doItem(ViewHolder holder, AlbumModel photoModel, int postion) {
                        ImageView imageView = holder.getView(R.id.iv_album_la);
                        ImageLoader.getInstance().displayImage("file://" + photoModel.getRecent(), imageView);
                        holder.setText(R.id.tv_name_la, photoModel.getName());
                        holder.setText(R.id.tv_count_la, photoModel.getCount() + "");
                    }

                    @Override
                    public int getItemLayoutResId() {
                        return R.layout.item_album_list;
                    }
                };
                //点击空白处时，隐藏掉pop窗口
                listPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                listPopupWindow.setAdapter(listAdapter);
                listPopupWindow.setModal(true);
                listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
                listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listPopupWindow.dismiss();
                        initAblum(listAdapter.getData().get(position).getName());
                    }
                });
                adjustHeight();
                listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundShow(false);
                    }
                });
            }
        });
    }


    private void initListenter() {
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPopupWindow.isShowing()) {
                    listPopupWindow.dismiss();
                } else if (!PhotoPickerActivity.this.isFinishing()) {
                    adjustHeight();
                    backgroundShow(true);
                    listPopupWindow.show();
                }
            }
        });


        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_add) {
                    if (PhotoPickerUitl.isMultiMode()) {
                        if (selectPhones.size() >= PhotoPickerUitl.getMaxSize()) {
                            toListPage();
                            return true;
                        }
                        if (!((CommonAdapter<PhotoModel>) gv.getAdapter()).getData().get(viewPager.getCurrentItem()).isChecked()) {
                            ((CommonAdapter<PhotoModel>) gv.getAdapter()).getData().get(viewPager.getCurrentItem()).setChecked(true);
                            selectPhones.add(((CommonAdapter<PhotoModel>) gv.getAdapter()).getData().get(viewPager.getCurrentItem()));
                            ((CommonAdapter<PhotoModel>) gv.getAdapter()).notifyDataSetChanged();
                            initMode();
                        }
                        toListPage();
                    } else {
                        ArrayList<PhotoModel> list = new ArrayList<PhotoModel>();
                        list.add(((CommonAdapter<PhotoModel>) gv.getAdapter()).getData().get(viewPager.getCurrentItem()));
                        setResult(RESULT_OK, new Intent().putParcelableArrayListExtra("photos", list));
                        finish();
                    }

                } else if (item.getItemId() == R.id.action_submit) {
                    setResult(RESULT_OK, new Intent().putParcelableArrayListExtra("photos", selectPhones));
                    finish();
                }
                return true;
            }
        });
    }

    private void initMode() {
        //数目是否显示
        tv_count.setVisibility(PhotoPickerUitl.isMultiMode() ? View.VISIBLE : View.GONE);
        tv_count.setText(selectPhones.size() + "/" + PhotoPickerUitl.getMaxSize());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);//加载menu文件到布局
        addMenu = menu.getItem(0);
        submitMenu = menu.getItem(1);
        addMenu.setVisible(false);
        submitMenu.setVisible(PhotoPickerUitl.isMultiMode());
        return true;
    }

    @Override
    public void onBackPressed() {
        if (viewPager != null) {
            if (viewPager.getVisibility() == View.GONE) {
                super.onBackPressed();
            } else {
                toListPage();
            }
        } else {
            super.onBackPressed();
        }
    }

    public static DisplayImageOptions initNoScaleDisplayOptions(boolean isShowDefault) {
        DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
        // 设置图片缩放方式
        // EXACTLY: 图像将完全按比例缩小的目标大小
        // EXACTLY_STRETCHED: 图片会缩放到目标大小
        // IN_SAMPLE_INT: 图像将被二次采样的整数倍
        // IN_SAMPLE_POWER_OF_2: 图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
        // NONE: 图片不会调整
        displayImageOptionsBuilder.imageScaleType(ImageScaleType.NONE);
        if (isShowDefault) {
            // 默认显示的图片
            displayImageOptionsBuilder.showStubImage(R.mipmap.icon_default);
            // 地址为空的默认显示图片
            displayImageOptionsBuilder
                    .showImageForEmptyUri(R.mipmap.icon_default);
            // 加载失败的显示图片
            displayImageOptionsBuilder.showImageOnFail(R.mipmap.icon_default);
        }
        // 开启内存缓存
        displayImageOptionsBuilder.cacheInMemory(false);
        // 开启SDCard缓存
        displayImageOptionsBuilder.cacheOnDisk(false);
        // 设置图片的编码格式为RGB_565，此格式比ARGB_8888快
        displayImageOptionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);
        displayImageOptionsBuilder.displayer(new FadeInBitmapDisplayer(100));

        return displayImageOptionsBuilder.build();
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param show
     */
    public void backgroundShow(boolean show) {
        if (show) {
            view_hidden.setVisibility(View.VISIBLE);
        } else {
            view_hidden.setVisibility(View.GONE);
        }
    }

    public void adjustHeight() {
        if (listAdapter == null) {
            return;
        }
        int count = listAdapter.getCount();
        count = count < COUNT_MAX ? count : COUNT_MAX;
        if (listPopupWindow != null) {
            listPopupWindow.setHeight(count * getResources().getDimensionPixelOffset(R.dimen.albumitem_height_picker));
        }
    }

    private void toListPage() {
        vg_photo_list.setVisibility(View.VISIBLE);
        Animation translateAnim1 = AnimationUtil.getTranslateAnimationRight2left1(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                viewPager.setVisibility(View.GONE);
                vg_photo_list.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation translateAnim2 = AnimationUtil.getTranslateAnimationRight2left2();
        vg_photo_list.startAnimation(translateAnim1);
        viewPager.startAnimation(translateAnim2);


       /* viewPager.setVisibility(View.GONE);
        vg_photo_list.setVisibility(View.VISIBLE);*/
        addMenu.setVisible(false);
        setTitle("图片");
        submitMenu.setVisible(PhotoPickerUitl.isMultiMode());
    }
}
