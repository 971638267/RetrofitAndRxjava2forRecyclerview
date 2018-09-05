package base.gan.photopicker.photo;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import base.gan.photopicker.model.AlbumModel;
import base.gan.photopicker.model.PhotoModel;
import base.gan.photopicker.photo.impl.OnLocalAlbumListener;
import base.gan.photopicker.photo.impl.OnLocalReccentListener;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("HandlerLeak")
public class PhotoSelectorDomain {

    private AlbumController albumController;

    public PhotoSelectorDomain(Context context) {
        albumController = new AlbumController(context);
    }

    public void getReccent(final OnLocalReccentListener listener) {
        Flowable.create(new FlowableOnSubscribe<List<PhotoModel>>() {
            @Override
            public void subscribe(FlowableEmitter<List<PhotoModel>> emitter) throws Exception {
                List<PhotoModel> photos = albumController.getCurrent();
                emitter.onNext(photos);
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<PhotoModel>>() {
            @Override
            public void accept(List<PhotoModel> photoModels) throws Exception {
                listener.onPhotoLoaded(photoModels);
            }
        });

    }

    /**
     * 获取相册列表
     */
    public void updateAlbum(final OnLocalAlbumListener listener) {
        Flowable.create(new FlowableOnSubscribe<List<AlbumModel>>() {
            @Override
            public void subscribe(FlowableEmitter<List<AlbumModel>> emitter) throws Exception {
                List<AlbumModel> albums = albumController.getAlbums();
                emitter.onNext(albums);
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<AlbumModel>>() {
            @Override
            public void accept(List<AlbumModel> photoModels) throws Exception {
                listener.onAlbumLoaded(photoModels);
            }
        });
    }

    /**
     * 获取单个相册下的所有照片信息
     */
    public void getAlbum(final String name, final OnLocalReccentListener listener) {


        Flowable.create(new FlowableOnSubscribe<List<PhotoModel>>() {
            @Override
            public void subscribe(FlowableEmitter<List<PhotoModel>> emitter) throws Exception {
                List<PhotoModel> photos = albumController.getAlbum(name);
                emitter.onNext(photos);
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<PhotoModel>>() {
            @Override
            public void accept(List<PhotoModel> photoModels) throws Exception {
                listener.onPhotoLoaded(photoModels);
            }
        });
    }

}
