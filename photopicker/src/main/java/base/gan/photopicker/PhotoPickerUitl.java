package base.gan.photopicker;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/5/22
 */
public class PhotoPickerUitl {
    private static int bgColorResId = -1;
    private static int textColorResId = -1;
    private static boolean mode = false;
    private static int maxSize = 1;

    public PhotoPickerUitl() {
    }

    public static void initMode(boolean multiMode, int max) {
        mode = multiMode;
        maxSize = max;
    }

    public static void initColor(int bgColorId, int textColorId) {
        bgColorResId = bgColorId;
        textColorResId = textColorId;
    }

    public static int getBgColorResId() {
        return bgColorResId;
    }

    public static int getTxtColorResId() {
        return textColorResId;
    }

    public static int getMaxSize () {
        return maxSize;
    }
    public static boolean isMultiMode () {
        return mode;
    }
}
