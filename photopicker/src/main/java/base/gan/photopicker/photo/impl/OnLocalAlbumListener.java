package base.gan.photopicker.photo.impl;

import java.util.List;

import base.gan.photopicker.model.AlbumModel;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/5/7
 */
public interface OnLocalAlbumListener {
    void onAlbumLoaded(List<AlbumModel> list);
}
